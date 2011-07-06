package org.gis.db;

import java.awt.Color;

public class Party {

	private Integer firstVote;
	private Integer secondVote;
	private String name;
	
	public Party(String name, Integer firstVote, Integer secondVote){
		this.firstVote = firstVote;
		this.secondVote = secondVote;
		this.name = name;
	}

	public Integer getErststimme() {
		return firstVote;
	}

	public Integer getZweitstimmen() {
		return secondVote;
	}

	public String getName() {
		return name;
	}
	
	public Color getColor() {
		if (getName().equalsIgnoreCase("CDU") || getName().equalsIgnoreCase("CSU"))
			return Color.BLACK;
		else if (getName().equalsIgnoreCase("SPD"))
			return Color.RED;
		else if (getName().equalsIgnoreCase("GRÜNE"))
			return Color.GREEN;
		else if (getName().equalsIgnoreCase("DIE LINKE"))
			return Color.MAGENTA;
		else if (getName().equalsIgnoreCase("FDP"))
			return Color.YELLOW;
		else
			return Color.GRAY;
	}
}
