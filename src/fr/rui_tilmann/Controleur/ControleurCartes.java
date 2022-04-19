package fr.rui_tilmann.Controleur;

import fr.rui_tilmann.Modele.Enums.Carte;
import fr.rui_tilmann.Modele.Joueur;
import fr.rui_tilmann.Modele.Modele;
import fr.rui_tilmann.Vue.VueCartes;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class ControleurCartes implements MouseMotionListener, MouseListener
{

	private Modele modele;
	private VueCartes vueCartes;

	private int pressedCard = -1;
	private Joueur pressedPlayer = null;

	public ControleurCartes(Modele modele, VueCartes vueCartes)
	{
		this.modele = modele;
		this.vueCartes = vueCartes;
	}

	@Override
	public void mouseClicked(MouseEvent e)
	{
		int numCarte = getNumCarte(e);
		Joueur j = getJoueur(e);
		if(j == null || numCarte == -1) return;

		if(e.getButton() == MouseEvent.BUTTON1) {
			vueCartes.clickedCard = numCarte;
			vueCartes.clickedJoueur = j;
			vueCartes.repaint();
		}
		else if(e.getButton() == MouseEvent.BUTTON3
		&& j.getCartes().size() > 5) {
			j.defausseCarte(numCarte);
			vueCartes.repaint();

			Joueur donneur = vueCartes.chosenJoueur;
			if(donneur != j
			&& j.getPosition() == donneur.getPosition()
			&& vueCartes.chosenCard <= donneur.getCartes().size()) {
				donneur.donneCarte(vueCartes.chosenCard, j);
			}
		}

		if(numCarte < 5)
		{
			if(numCarte >= j.getCartes().size()
			|| modele.getIdJoueur() != j) return;

			Carte carte = j.getCartes().get(numCarte);

			switch(carte)
			{
				case FEU: case EAU: case TERRE: case AIR:

				if(e.getButton() == MouseEvent.BUTTON1
				&& j.getPosition().getType().toArtefact() == carte.toArtefact()
				&& j.getCartes().stream().filter(t -> t == carte).count() >= 4)
					j.recupereArtefact(carte);

				break;

				case HELICOPTERE:
					if(e.getButton() == MouseEvent.BUTTON1)
					{
						// TODO mettre dans une variable qu'on veut voler avec l'helico (dans ControleurJoueur)
						// puis joueur.deplace(c);
					}
					break;

				case SAC_DE_SABLE:
					if(e.getButton() == MouseEvent.BUTTON1)
					{
						// TODO idem
						// puis joueur.asseche(c);
					}
					break;
			}
		}
	}

	public void mousePressed(MouseEvent e)
	{
		int numCarte = getNumCarte(e);
		Joueur j = getJoueur(e);
		if(j == null || numCarte == -1) return;

		pressedCard = numCarte;
		pressedPlayer = j;
	}

	public void mouseReleased(MouseEvent e)
	{
		int numCarte = getNumCarte(e);
		Joueur j = getJoueur(e);
		if(j == null || numCarte == -1) return;

		if(pressedPlayer != j)
			pressedPlayer.donneCarte(numCarte, j);
	}

	@Override
	public void mouseMoved(MouseEvent e)
	{
		int numCarte = getNumCarte(e);
		Joueur j = getJoueur(e);
		if(j == null || numCarte == -1) return;

		if(modele.getIdJoueur() == j)
		{
			vueCartes.hoveredCard = numCarte;
			vueCartes.hoveredJoueur = j;
			vueCartes.repaint();
		}
		else mouseExited(e);
	}

	public void mouseExited(MouseEvent e)
	{
		vueCartes.hoveredJoueur = null;
		vueCartes.hoveredCard = -1;
		vueCartes.repaint();
	}

	private int getNumCarte(MouseEvent e)
	{
		int numCarte = (e.getX() - vueCartes.getX())/VueCartes.WIDTH;
		return 0 <= numCarte && numCarte < Carte.MAX ? numCarte : -1;
	}

	private Joueur getJoueur(MouseEvent e)
	{
		int numJoueur = (e.getY() - vueCartes.getY())/VueCartes.HEIGHT;
		return 0 <= numJoueur && numJoueur < 4 ? modele.getJoueur(numJoueur) : null;
	}

	public void mouseDragged(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}

}
