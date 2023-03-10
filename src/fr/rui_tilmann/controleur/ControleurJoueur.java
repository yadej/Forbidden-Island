package fr.rui_tilmann.controleur;

import fr.rui_tilmann.modele.Case;
import fr.rui_tilmann.modele.Joueur;
import fr.rui_tilmann.modele.Modele;
import fr.rui_tilmann.modele.enums.*;
import fr.rui_tilmann.vue.jeu.VueBoutons;
import fr.rui_tilmann.vue.jeu.VuePlateau;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Timer;
import java.util.TimerTask;

import static fr.rui_tilmann.vue.jeu.VuePlateau.P;

public class ControleurJoueur extends MouseAdapter implements KeyListener
{

	private final Modele modele;
	private final VuePlateau vuePlateau;

	public Joueur clickedJoueur = null;
	public int clickedCard = -1;

	private Case caseHelico = null;

	private boolean actionSpeNavigateurOuPilote = false;
	private boolean actionSpePlongeur = false;
	private final boolean[] jSelect = new boolean[Modele.NOMBRE_JOUEURS];
	private int joueurDeplace = 0;

	private int caseDeplace = 0;

	public ControleurJoueur(Modele modele, VuePlateau vuePlateau, VueBoutons vueBoutons)
	{
		this.modele = modele;
		this.vuePlateau = vuePlateau;

		for(int i = 0; i < Modele.NOMBRE_JOUEURS; i++)
		{
			jSelect[i] = false;

			int finalI = i;
			vueBoutons.boutonJoueur[i].addActionListener(e -> jSelect[finalI] = !jSelect[finalI]);
		}

		vueBoutons.boutonActionSpe.addActionListener(e -> actionSpeNavigateurOuPilote = !actionSpeNavigateurOuPilote);


		if(modele.getJoueurs().stream().anyMatch(e -> e.getRole() == Role.MESSAGER)) {
			vueBoutons.MessagerJoueurAction.addActionListener( e -> joueurDeplace = (joueurDeplace + 1) % 4);
			vueBoutons.MessagerDeplaceCase.addActionListener( e -> caseDeplace = (caseDeplace + 1) % 2);

			new Timer().schedule(new TimerTask() {
				@Override
				public void run() {
					boolean b = modele.getCurrentJoueur().getRole() == Role.NAVIGATEUR;
					vueBoutons.MessagerJoueurAction.setVisible(b);
					vueBoutons.MessagerDeplaceCase.setVisible(b);

					vueBoutons.MessagerJoueurAction.setText("Déplacer: " +  modele.getJoueur(joueurDeplace).getRole());
					vueBoutons.MessagerDeplaceCase.setText("Nombre de cases: " + (caseDeplace + 1));
				}
			}, 0, 100);
		}

		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				Role role = modele.getCurrentJoueur().getRole();
				boolean b = role == Role.PILOTE || role == Role.NAVIGATEUR;
				vueBoutons.boutonActionSpe.setVisible(b);
				vueBoutons.boutonActionSpe.setVisible(b);

				if(actionSpeNavigateurOuPilote)
					vueBoutons.boutonActionSpe.setBackground(Color.GREEN);
				else
					vueBoutons.boutonActionSpe.setBackground(Color.RED);
			}
		}, 0, 100);
		
		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				for(int i = 0; i < Modele.NOMBRE_JOUEURS; i++) {
					if(jSelect[i])
						vueBoutons.boutonJoueur[i].setBackground(Color.GREEN);
					else
						vueBoutons.boutonJoueur[i].setBackground(Color.RED);
				}
			}
		}, 0, 100);
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
		vuePlateau.setFocusable(true);
		Case c = getCase(e);
		if(c == null) return;

		Joueur joueur = clickedJoueur;

		if(e.getButton() == MouseEvent.BUTTON3)
			caseHelico = c;

		if(0 <= clickedCard && clickedCard < clickedJoueur.getCartes().size()
		&& e.getButton() == MouseEvent.BUTTON1)
		{
			switch(clickedJoueur.getCartes().get(clickedCard))
			{
				case HELICOPTERE:
					if(c.getEtat() == Etat.SUBMERGEE) return;

					if(caseHelico == null)
						caseHelico = clickedJoueur.getPosition();

					if(c != caseHelico)
					{
						boolean aBienTransporte = false;
						for(Joueur jh : caseHelico.getJoueurs()) {
							for(int j = 0; j < modele.getJoueurs().size(); j++) {
								if(jh == modele.getJoueur(j) && jSelect[j]) {
									jh.deplace(c, false);
									aBienTransporte = true;
								}
							}
						}
						if(aBienTransporte)
						{
							joueur.defausseCarte(clickedCard);
							Son.HELICOPTERE.jouerSon();
						}
						clickedJoueur = null;
						clickedCard = -1;
					}
					caseHelico = null;
					break;

				case SAC_DE_SABLE:
					if(c.getEtat() == Etat.INONDEE)
					{
						joueur.asseche(c, true);
						joueur.defausseCarte(clickedCard);
					}
					break;
			}
			return;
		}

		if(modele.getCurrentJoueur().getRole() == Role.PILOTE
		&& !modele.actionUtiliseePilote && actionSpeNavigateurOuPilote
		&& e.getButton() == MouseEvent.BUTTON1)
		{
			modele.getCurrentJoueur().deplace(c);
			modele.actionUtiliseePilote = true;
			return;
		}

		boolean diago = modele.getCurrentJoueur().getRole() == Role.EXPLORATEUR;
		Joueur j = modele.getCurrentJoueur();

		if(c.estAdjacente(j.getPosition(), diago)) {
			switch(e.getButton())
			{
				case 1: // gauche
					if(j.getRole() == Role.INGENIEUR && modele.actionSpeIngenieur) {
						modele.actionSpeIngenieur = false;
						modele.useAction();
						if(!modele.actionsRestantes()) return;
					}
					actionSpePlongeur = j.getRole() == Role.PLONGEUR && c.getEtat() != Etat.SECHE;
					j.deplace(c,!actionSpePlongeur);
					break;

				case 3: // droit
					if(actionSpePlongeur){
						actionSpePlongeur = false;
						modele.useAction();
					}
					if(j.getRole() == Role.INGENIEUR && !modele.actionSpeIngenieur) {
						j.asseche(c, true);
						modele.actionSpeIngenieur = true;
					} else {
						j.asseche(c);
						modele.actionSpeIngenieur = false;
					}
			}
		}

		if(c == j.getPosition() && e.getButton() == MouseEvent.BUTTON1) {
			long occurences = j.getCartes().stream().filter(carte -> carte.toArtefact() == c.getZone().toArtefact()).count();

			if(occurences > 3)
				for(Carte carte : j.getCartes())
					if(carte.toArtefact() == c.getZone().toArtefact()) {
						if(j.getRole() == Role.INGENIEUR && modele.actionSpeIngenieur) {
							modele.actionSpeIngenieur = false;
							modele.useAction();
							if(!modele.actionsRestantes())return;
						}
						modele.getCurrentJoueur().recupereArtefact(carte);
						break;
					}
		}
	}

	@Override
	public void keyReleased(KeyEvent e)
	{
		Direction d = Direction.AUCUNE;

		switch(e.getKeyCode())
		{
			// déplacement
			case KeyEvent.VK_UP: 	d = Direction.NORD;  break;
			case KeyEvent.VK_RIGHT: d = Direction.EST; 	 break;
			case KeyEvent.VK_DOWN: 	d = Direction.SUD; 	 break;
			case KeyEvent.VK_LEFT: 	d = Direction.OUEST; break;
			case KeyEvent.VK_1: jSelect[0] = !jSelect[0];break;
			case KeyEvent.VK_2:	jSelect[1] = !jSelect[1];break;
			case KeyEvent.VK_3:	if(3 <= jSelect.length)jSelect[2] = !jSelect[2];break;
			case KeyEvent.VK_4: if(4 <= jSelect.length)jSelect[3] = !jSelect[3];break;

			// TODO trouver d'autre moyen pour changer
			case KeyEvent.VK_N:
				actionSpeNavigateurOuPilote = !actionSpeNavigateurOuPilote;
				break;

			case KeyEvent.VK_M:
				caseDeplace = (caseDeplace + 1) % 2;
				break;

			case  KeyEvent.VK_SPACE:
				joueurDeplace = (joueurDeplace + 1) % 4;

			case  KeyEvent.VK_R:
				clickedCard = -1;
				clickedJoueur = null;
		}

		// TODO à gerer les cas out of bounds afin qu'il utilise pas d'action

		if(modele.getCurrentJoueur().getRole() == Role.INGENIEUR && modele.actionSpeIngenieur) {
			modele.actionSpeIngenieur = false;
			modele.useAction();
			if(!modele.actionsRestantes()) return;
		}

		if(modele.getCurrentJoueur().getRole() == Role.NAVIGATEUR
		&& d != Direction.AUCUNE
		&& actionSpeNavigateurOuPilote
		&& modele.getJoueur(joueurDeplace).getRole() != Role.NAVIGATEUR) {

			switch (caseDeplace){
				case 0: modele.getJoueur(joueurDeplace).deplace(d);break;
				case 1:
					modele.getJoueur(joueurDeplace).deplace(d, false);
					modele.getJoueur(joueurDeplace).deplace(d);break;
			}
		}
		else modele.getCurrentJoueur().deplace(d);
	}

	@Override
	public void mouseMoved(MouseEvent e)
	{
		Case c = getCase(e);

		if(c != null)
		{
			vuePlateau.hoveredCase = c;
			vuePlateau.repaint();
		}
		else mouseExited(e);
	}

	@Override
	public void mouseDragged(MouseEvent e)
	{
		mouseMoved(e);
	}

	@Override
	public void mouseExited(MouseEvent e)
	{
		vuePlateau.hoveredCase = null;
		vuePlateau.repaint();
	}

	private Case getCase(MouseEvent e)
	{
		int x = (e.getX() - 8);
		int y = (e.getY() - 31);

		if(0 <= x && x < vuePlateau.getWidth()
		&& 0 <= y && y < vuePlateau.getHeight())
			return modele.getPlateau().getCase(x/P, y/P);

		return null;
	}

	public void keyPressed(KeyEvent e) {}
	public void keyTyped(KeyEvent e) {}

}
