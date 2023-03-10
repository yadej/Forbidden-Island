package fr.rui_tilmann.modele;

import fr.rui_tilmann.modele.enums.Direction;
import fr.rui_tilmann.modele.enums.Etat;
import fr.rui_tilmann.modele.enums.Zone;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Case
{

	private final Modele modele;
	private Etat etat;
	private Zone zone;
	private final int x, y;

	public Case(Modele modele, int x, int y)
	{
		this.modele = modele;
		this.etat = Etat.SECHE;
		this.zone = Zone.NORMALE;
		this.x = x; this.y = y;
	}

	public Etat getEtat() {return etat;}
	public void setEtat(Etat etat) {this.etat = etat;}

	public Zone getZone() {return zone;}
	public void setZone(Zone zone) {this.zone = zone;}

	public int getX() {return x;}
	public int getY() {return y;}

	public List<Joueur> getJoueurs()
	{
		return modele.getJoueurs().stream().filter(j -> j.getPosition() == this)
		.collect(Collectors.toList());
	}

	public Case adjacente(Direction direction)
	{
		int x = this.x;
		int y = this.y;

		switch(direction)
		{
			case NORD : y--; break;
			case SUD: 	y++; break;
			case OUEST: x--; break;
			case EST: 	x++; break;
		}

		if(0 <= x && x < Plateau.LENGTH
		&& 0 <= y && y < Plateau.LENGTH)
			return modele.getPlateau().getCase(x, y);
		else return null;
	}

	public boolean estAdjacente(Case c, boolean diago)
	{
		int x = c.getX(); int y = c.getY();

		return (this.x == x || this.x == x-1 || this.x == x+1)
			&& (this.y == y || this.y == y-1 || this.y == y+1)
			&& (diago || this.x == x || this.y == y);
	}

	public List<Case> casesAdjacentes(boolean diago)
	{
		List<Case> cases = new ArrayList<>();

		modele.getPlateau().forEachCase(c ->
		{
			if(estAdjacente(c, diago) && c != this)
				cases.add(c);
		});

		return cases;
	}

	public boolean dansIle()
	{
		double x = this.x - 3.5;
		double y = this.y - 3.5;
		return x*x + y*y < 8;
	}

	public String toString()
	{
		return "case " + x + ", " + y + ", " + etat + ", " + zone;
	}

}
