package org.gis.data.election;

import java.awt.Color;

/**
 * Represents a political Party for the Election 2009 data set per constituency.
 * This was the <i>Bundestagswahl</i> for the German parliament. Only the major 
 * political parties are considered, however this Class can also represent other 
 * parties as well.
 * 
 * @author Stephanie Marx
 * @author Dirk Kirsten
 *
 */
public class PartyResults {

	private Integer firstVote;
	private Integer secondVote;
	private String name;
	
	public PartyResults(String name, Integer firstVote, Integer secondVote){
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
	
	/**
	 * Returns the typical color of the main partys.
	 * 
	 * @return Color of this party.
	 */
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
