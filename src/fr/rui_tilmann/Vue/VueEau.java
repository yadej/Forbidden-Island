package fr.rui_tilmann.Vue;

import fr.rui_tilmann.Modele.Modele;
import fr.rui_tilmann.Modele.Plateau;

import javax.swing.*;
import java.awt.*;

public class VueEau extends JPanel implements Observer
{

	private final Modele modele;
	public static final int P = VuePlateau.P;
	private boolean b = true;

	public VueEau(Modele modele)
	{
		this.modele = modele;
		modele.addObserver(this);

		this.setPreferredSize(new Dimension(2*P, P * Plateau.LENGTH));
	}

	@Override
	public void update() {repaint();}

	public void paintComponent(Graphics g)
	{
		super.repaint();
		paint(g);
	}

	public void paint(Graphics g)
	{
		// eau
		ImageIcon water = new ImageIcon("src/fr/rui_tilmann/images/water.png");
		g.drawImage(water.getImage(), 0, (int)((10 - (modele.getniveauEau() + 1)) * P/2 + P - 9), null);

		// overlay blanc pour effet rond
		ImageIcon over = new ImageIcon("src/fr/rui_tilmann/images/water_overlay.png");
		g.drawImage(over.getImage(), 0, 0, null);

		// lignes
		for(int i = 0; i < 10; i++)
		{
			if(i == 0) g.setColor(Color.RED);
			else g.setColor(Color.WHITE);
			g.fillRect(8,i * P/2 + P, 4*P/5, 4);
		}

		g.setColor(Color.WHITE);
		g.setFont(new Font("Calibri", Font.BOLD, 20));
		// dessine 2
		g.drawString("2", P + 20, 8*P/2 + P + 10);

		// 3
		g.drawString("3", P + 20, 5*P/2 + P + 10);

		// 4
		g.drawString("4", P + 20, 3*P/2 + P + 10);

		// 5
		g.drawString("5", P + 20, 1*P/2 + P + 10);

		// dessine tete de mort
		ImageIcon skull = new ImageIcon("src/fr/rui_tilmann/images/skull.png");
		g.drawImage(skull.getImage(), P + 10, P - 13, null);
	}

}
