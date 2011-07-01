package org.openstreetmap.gui.jmapviewer.interfaces;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.JPanel;

import org.gis.db.Party;

public class PartyChart extends JPanel {
	private static final long serialVersionUID = 6007795415944736390L;
	private LinkedList<Party> parties;
	private Integer voter;
	
	@Override 
	public void paintComponent(Graphics g) {
		if (parties == null || voter == null)
			return;
		
		g.clearRect(0, 0, 201, 200);
		Font font = new Font("Verdana", Font.PLAIN, 11);
		g.setFont(font);
    	FontMetrics fm   = g.getFontMetrics(g.getFont());
		
		Iterator<Party> it = parties.iterator();
		int i = 0;
		while (it.hasNext()) {
			Party p = it.next();
			
			if (p.getZweitstimmen() != 0) {
				float result = p.getZweitstimmen() / (float) voter;
				Color c;
				if (p.getName().equalsIgnoreCase("CDU") || p.getName().equalsIgnoreCase("CSU"))
					c = Color.BLACK;
				else if (p.getName().equalsIgnoreCase("SPD"))
					c = Color.RED;
				else if (p.getName().equalsIgnoreCase("GRÜNE"))
					c = Color.GREEN;
				else if (p.getName().equalsIgnoreCase("DIE LINKE"))
					c = Color.MAGENTA;
				else if (p.getName().equalsIgnoreCase("FDP"))
					c = Color.YELLOW;
				else
					c = Color.GRAY;
				
				g.setColor(c);
				g.fillRect(i * 40, (int) (150 - result*200), 40, (int) (result*200));
				g.setColor(Color.BLACK);
				g.drawRect(i * 40, (int) (150 - result*200), 40, (int) (result*200));
				
				
	        	java.awt.geom.Rectangle2D rect = fm.getStringBounds(String.format("%.1f", result*100)+"%", g);
	
	        	int textHeight = (int)(rect.getHeight()); 
	        	int textWidth  = (int)(rect.getWidth());
	        	
				int x = i * 40 + 20 - textWidth / 2;
	        	int y = 180 - textHeight / 2  + fm.getAscent();
	
	        	g.drawString(String.format("%.1f", result*100)+"%", x, y);
				
				++i;
			}
		}
	}
	
	public void setParties(LinkedList<Party> p) {
		parties = p;
	}
	
	public void setVoter(int v) {
		voter = v;
	}
}
