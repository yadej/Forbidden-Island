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

	public ControleurCartes(Modele modele, VueCartes vueCartes)
	{
		this.modele = modele;
		this.vueCartes = vueCartes;
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
		int numCarte = getNumCarte(e);
		int numJoueur = getNumJoueur(e);

		if(0 <= numJoueur && numJoueur < 4
		&& 0 <= numCarte && numCarte < 5)
		{
			Joueur joueur = modele.getJoueurs().get(numJoueur);

			if(numCarte >= joueur.getCartes().size()
			|| modele.getIdJoueur() != joueur) return;

			Carte carte = joueur.getCartes().get(numCarte);

			switch(carte)
			{
				case FEU: case EAU: case TERRE: case AIR:

				if(e.getButton() == MouseEvent.BUTTON1
				&& joueur.getPosition().getType().toArtefact() == carte.toArtefact()
				&& joueur.getCartes().stream().filter(t -> t == carte).count() >= 4)
					joueur.recupereArtefact(carte);

				/* TODO donner carte
				if(e.getButton() == MouseEvent.BUTTON3
				&& joueur.getPosition() == cible.getPosition())
					joueur.donneCarte(numCarte, cible);*/

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
/* TODO flemme de lire
	@Override
	public void mouseReleased(MouseEvent e)
	{
		int numCarte = (e.getX() - vueCartes.getX())/vueCartes.WIDTH;
		int numJoueur = (e.getY() - vueCartes.getY())/vueCartes.HEIGHT;
		if(0 <= numCarte && numCarte < 10
				&& 0 <= numJoueur && numJoueur < 4 && e.getButton() == MouseEvent.BUTTON1)
		{
			vueCartes.chosenCard = numCarte;
			vueCartes.chosenJouer = numJoueur;
			vueCartes.repaint();
		}
		//A la place de getJoueur car c'est plus de travailler si on prend celui directement via numJoueur
		if(e.getButton() == MouseEvent.BUTTON3){
			Joueur j = modele.getJoueurs().get(numJoueur);
			if(j.getCartes().size() > 5) {
				modele.getJoueurs().get(numJoueur).discardTresor(numCarte);
				vueCartes.repaint();
			}
		}

	}*/

	@Override
	public void mouseMoved(MouseEvent e)
	{
		int numCarte = getNumCarte(e);
		int numJoueur = getNumJoueur(e);

		if(0 <= numCarte && numCarte < 5
		&& 0 <= numJoueur && numJoueur < 4
		&& modele.getIdJoueur() == modele.getJoueurs().get(numJoueur))
		{
			vueCartes.hoveredCard = numCarte;
			vueCartes.hoveredJoueur = numJoueur;
			vueCartes.repaint();
		}
		else mouseExited(e);
	}

	public void mouseExited(MouseEvent e)
	{
		vueCartes.hoveredJoueur = -1;
		vueCartes.hoveredCard = -1;
		vueCartes.repaint();
	}

	private int getNumCarte(MouseEvent e)
	{
		int i = (e.getX() - vueCartes.getX());
		return i >= 0 ? i/vueCartes.WIDTH : -1;
	}

	private int getNumJoueur(MouseEvent e)
	{
		int i = (e.getY() - vueCartes.getY());
		return i >= 0 ? i/vueCartes.HEIGHT : -1;
	}

	public void mouseDragged(MouseEvent e) {}

	public void mouseClicked(MouseEvent e) {}
	public void mousePressed(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}

}
