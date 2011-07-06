package org.openstreetmap.gui.jmapviewer.interfaces;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.JPanel;

import org.gis.data.election.PartyResults;

/**
 * Draws a visualization of the party results for one specific constituency. Draws a bar and
 * below the percentage for the corresponding party.
 * 
 * @author Stephanie Marx
 * @author Dirk Kirsten
 *
 */
public class PartyChart extends JPanel {
	private static final long serialVersionUID = 6007795415944736390L;
	private Collection<PartyResults> parties;
	private Integer voter;
	
	private int width;
	private int height;
	
	public PartyChart(int w, int h) {
		width = w;
		height = h;
	}
	
	@Override 
	public void paintComponent(Graphics g) {
		if (parties == null || voter == null)
			return;
		
		/* Clear the old drawing */
		g.clearRect(0, 0, width + 1, height);
		
		if (parties == null)
			return;
		
		/* Set a correct Font for drawing percentages */
		Font font = new Font("Verdana", Font.PLAIN, 11);
		g.setFont(font);
    	FontMetrics fm   = g.getFontMetrics(g.getFont());
		
		Iterator<PartyResults> it = parties.iterator();
		int i = 0;
		while (it.hasNext()) {
			PartyResults p = it.next();
			
			if (p.getZweitstimmen() != 0) {
				/* Calculate the result of one party based on the Zweitstimmen */
				float result = p.getZweitstimmen() / (float) voter;
				
				/* Draw the percentage as bar with a black border around */
				g.setColor(p.getColor());
				g.fillRect(i * (width / 5), (int) (height - 50 - result*width), (width / 5), (int) (result*height));
				g.setColor(Color.BLACK);
				g.drawRect(i * (width / 5), (int) (height - 50 - result*width), (width / 5), (int) (result*height));
				
				/* Print the percentage below the bar */
				/* Gets the bounding rectangle the text will need */
				String percentageString = String.format("%.1f", result*100)+"%";
	        	java.awt.geom.Rectangle2D rect = fm.getStringBounds(percentageString, g);
	
	        	int textHeight = (int)(rect.getHeight()); 
	        	int textWidth  = (int)(rect.getWidth());
	        	
				int x = i * (width / 5) + (width / 10) - textWidth / 2;
	        	int y = width - 20 - textHeight / 2  + fm.getAscent();
	
	        	g.drawString(percentageString, x, y);
				
				++i;
			}
		}
	}
	
	public void setParties(Collection<PartyResults> p) {
		parties = p;
	}
	
	public void setVoter(int v) {
		voter = v;
	}
}
