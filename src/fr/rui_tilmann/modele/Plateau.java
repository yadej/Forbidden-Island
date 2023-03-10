package fr.rui_tilmann.modele;

import fr.rui_tilmann.modele.enums.Etat;
import fr.rui_tilmann.modele.enums.Zone;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class Plateau
{

	private final Modele modele;
	public static final int LENGTH = 8;
	private final Case[][] cases = new Case[LENGTH][LENGTH];

	public Plateau(Modele modele)
	{
		this.modele = modele;

		initCases();
		ileCirculaire();
		placerZones();
	}

	private void initCases()
	{
		for(int x = 0; x < LENGTH; x++)
			for(int y = 0; y < LENGTH; y++)
				cases[x][y] = new Case(modele, x, y);
	}

	private void ileCirculaire()
	{
		forEachCase(c ->
		{
			if(!c.dansIle())
				c.setEtat(Etat.SUBMERGEE);
		});
	}

	private void placerZones()
	{
		for(int i = 0; i < 2; i++)
		{
			placerZoneAleatoire(Zone.AIR);
			placerZoneAleatoire(Zone.EAU);
			placerZoneAleatoire(Zone.FEU);
			placerZoneAleatoire(Zone.TERRE);
		}
		placerZoneAleatoire(Zone.HELIPORT);
	}

	public Case getCase(int x, int y)
	{
		return cases[x][y];
	}

	public void forEachCase(Consumer<Case> action)
	{
		Objects.requireNonNull(action);
		for(Case[] ligne : cases)
			for(Case c : ligne)
				action.accept(c);
	}

	private void placerZoneAleatoire(Zone type)
	{
		Case c;
		do c = caseAleatoire(Etat.SECHE, Etat.INONDEE);
		while(c.getZone() != Zone.NORMALE);
		c.setZone(type);
	}

	protected Case caseAleatoire(Etat... etatsPossibles)
	{
		Case c;
		do c = caseAleatoire();
		while(!List.of(etatsPossibles).contains(c.getEtat()));
		return c;
	}

	public Case caseAleatoire()
	{
		return getCase(new Random().nextInt(LENGTH), new Random().nextInt(LENGTH));
	}

	public int compte(Zone zone) {
		AtomicInteger i = new AtomicInteger();
		forEachCase(c ->
		{
			if(c.getZone() == zone)
				i.getAndIncrement();
		});
		return i.get();
	}
}
