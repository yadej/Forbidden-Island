package fr.rui_tilmann.vue.jeu;

import fr.rui_tilmann.controleur.ControleurCartes;
import fr.rui_tilmann.modele.enums.Carte;
import fr.rui_tilmann.modele.Joueur;
import fr.rui_tilmann.modele.Modele;
import fr.rui_tilmann.vue.Observer;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class VueCartes extends JPanel implements Observer
{

	private final Modele modele;

	public static final int WIDTH = 80;
	public static final int HEIGHT = 110;

	public Joueur hoveredJoueur = null;
	public int hoveredCard = -1;

	public Joueur clickedJoueur = null;
	public int clickedCard = -1;

	public int draggedX, draggedY;

	public VueCartes(Modele modele)
	{
		this.modele = modele;
		modele.addObserver(this);

		this.setPreferredSize(new Dimension(Carte.MAX*WIDTH, Modele.NOMBRE_JOUEURS*HEIGHT));
	}

	@Override
	public void update() {repaint();}

	public void paintComponent(Graphics g)
	{
		super.repaint();

		List<Joueur> joueurs = modele.getJoueurs();

		for(int i = 0; i < joueurs.size(); i++)
		{
			g.setColor(joueurs.get(i).getRole().getCouleur());
			g.fillRect(0, i*HEIGHT, getWidth(), HEIGHT);
		}

		// on dessine les cartes qui sont pas du joueur current
		for(int y = 0; y < joueurs.size(); y++)
		{
			if(modele.getCurrentJoueur() != joueurs.get(y))
			{
				List<Carte> tresors = joueurs.get(y).getCartes();

				for(int x = 0; x < joueurs.get(y).getCartes().size(); x++)
				{
					paint(g, tresors.get(x), x, y);
				}

				// on noircit
				g.setColor(new Color(0, 0, 0, 127));
				g.fillRect(0, y*HEIGHT, getWidth(), HEIGHT);
			}
		}

		// on dessine les cartes du joueur current pour qu'elles soient en avant plan
		int y = modele.getCurrentJoueurId();
		for(int x = 0; x < joueurs.get(y).getCartes().size(); x++)
			paint(g, joueurs.get(y).getCartes().get(x), x, y);
	}

	private void paint(Graphics g, Carte carte, int x, int y)
	{
		if(ControleurCartes.joueurEnfonce == modele.getJoueur(y) && ControleurCartes.carteEnfoncee == x)
			g.drawImage(carte.getImage(), draggedX - WIDTH/2, draggedY - HEIGHT/2, null);
		else if(hoveredJoueur == modele.getJoueur(y) && hoveredCard == x
		|| (clickedJoueur != null && clickedCard != -1 && clickedJoueur.getId() == y && clickedCard == x))
			g.drawImage(carte.getImage(), x*WIDTH + WIDTH/16 - 5, y*HEIGHT + HEIGHT/16 - 5, null);
		else g.drawImage(carte.getImage(), x*WIDTH + WIDTH/16, y*HEIGHT + HEIGHT/16, null);
	}


	/*
	private void paint2(Graphics g, Carte carte, int x, int y)
	{
		Image image = carte.getImage();
		BufferedImage img = new BufferedImage(image.getWidth(null), image.getHeight(null) , Image.SCALE_DEFAULT);
		img.getGraphics().drawImage(carte.getImage(), 0, 0 , null);
		int pixelColor;
		int toAlpha;

		if(clickedJoueur == y && clickedCard == x) {
			for (int i = 0; i < image.getWidth(null); i++) {
				for (int j = 0; j < image.getHeight(null); j++) {
					pixelColor = img.getRGB(i, j);
					toAlpha = (200) | (pixelColor & 0xFFFFFF);
					img.setRGB(i, j, toAlpha);
				}
			}
		}

		if(modele.positionJoueur(hoveredJoueur) == y && hoveredCard == x)
			g.drawImage(img, x*WIDTH + WIDTH/16 - 5, y*HEIGHT + HEIGHT/16 - 5, null);
		else g.drawImage(img, x*WIDTH + WIDTH/16, y*HEIGHT + HEIGHT/16, null);
	}
	*/
}
