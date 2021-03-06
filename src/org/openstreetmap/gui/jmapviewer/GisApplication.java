package org.openstreetmap.gui.jmapviewer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.gis.data.GisPoint.PointRelation;
import org.gis.data.Polygon.PolygonRelation;
import org.gis.data.election.Constituency;
import org.gis.data.election.ElectionWorld;
import org.gis.data.election.MaltePoint;
import org.gis.data.election.PartyResults;
import org.gis.data.world.Country;
import org.gis.data.world.CountryPolygon;
import org.gis.data.world.StorkPoint;
import org.gis.data.world.World;
import org.openstreetmap.gui.jmapviewer.interfaces.PartyChart;
import org.openstreetmap.gui.jmapviewer.interfaces.TileSource;
import org.openstreetmap.gui.jmapviewer.tilesources.BingAerialTileSource;
import org.openstreetmap.gui.jmapviewer.tilesources.OsmTileSource;

/*
* ----------------------------------------------------------------------------
* "THE BEVERAGE-WARE LICENSE" (Revision 42):
* <stephanie.marx@uni-konstanz.de> and <dirk.kirsten@uni-konstanz.de> wrote this 
* file. As long as you retain this notice you can do whatever you want with this 
* stuff. If we meet some day, and you think this stuff is worth it, you can buy 
* us a nice beverage in return.
* ----------------------------------------------------------------------------
*/

/**
 *
 * Display map data using {@link JMapViewer}. This is a programm for the course
 * "Geographic Information Systems" in the year 2011 at the University of Konstanz.
 *
 * @author Dirk Kirsten
 * @author Stephanie Marx
 *
 */
public class GisApplication extends JFrame {
    private static final long serialVersionUID = 1L;
    private final JMapViewer map;
    private World w;
    private ElectionWorld ew;
    private JTextArea informationElection;
    private JTextArea informationWorld;
    private PartyChart partyChart;
    private final JComboBox storkSelection;
    private interactionType interaction;
    private final JComboBox displayStyleElection;
    private final JComboBox displayStyleWorld;
    private final JTabbedPane tabs;
    private final JSlider mappingCoefficient;
    
    private static enum displayStyleTypeElection { GREEN_PARTY_NORMAL, GREEN_PARTY_CORR_MALTE, WINNER, DIFFERENCE,
    	TURNOUT, INFLUENCE, SIZE }; 
    private static enum displayStyleTypeStorks { RANDOM, SIZE, TRAVEL_THROUGH, TRAVEL_THROUGH_PERCENTAGE };
    private static enum interactionType { NORMAL, TOPOLOGICAL };
    
    public GisApplication() {
        super();
        
        final String[] displayStyleTypes = { "Results Green Party", "Green Party <> Malte Spitz", 
        		"Winner", "Difference", "Turnout", "Influence", "Area Size" };
        final String[] displayStyleWorldTypes = { "Normal", "Area Size", "Travel through", 
        		"Travel through percentage" };
        final String[] storkSelectionOptions = { "All", "1", "2", "3", "4", "5", "6", "7", "8", 
        		"9", "10", "11", "12"};
        final String[] interactionTypesStrings = { "Information", "Topological" };
        
        createWindow();
        
        /* Initializes all variables */
        map = new JMapViewer();
        map.setZoom(6);
        w = null;
        ew = null;
        interaction = interactionType.NORMAL;
        JPanel rightPanel = new JPanel();
        JPanel helpPanel = new JPanel();
        JPanel informationElectionPanel = new JPanel(new FlowLayout());
        JPanel informationWorldPanel = new JPanel();
        JPanel electionTab = new JPanel(new FlowLayout());
        JPanel worldTab = new JPanel(new FlowLayout());
        tabs = new JTabbedPane();
        
        /* Set up Slider */
        mappingCoefficient = new JSlider();
        mappingCoefficient.setMinimum(1);
        mappingCoefficient.setMaximum(8);
        mappingCoefficient.setValue(2);
        mappingCoefficient.setMajorTickSpacing(1);
        mappingCoefficient.setSnapToTicks(true);
        mappingCoefficient.setPaintLabels(true);
        mappingCoefficient.setPaintTicks(true);
        mappingCoefficient.addChangeListener(new VisualizationEventListener());
        
        displayStyleElection = new JComboBox(displayStyleTypes);
        storkSelection = new JComboBox(storkSelectionOptions);
        displayStyleWorld = new JComboBox(displayStyleWorldTypes);
        final JComboBox interactionBox = new JComboBox(interactionTypesStrings);
        interaction = interactionType.NORMAL;
        partyChart = new PartyChart(200, 200);
        partyChart.setPreferredSize(new Dimension(201, 200));
        
        /* Add a mouse listener to get a clicks on the map */
        map.addMouseListener(new MouseEventListener());
        
        /* Construct lower help text */
        add(helpPanel, BorderLayout.SOUTH);
        JLabel helpLabel = new JLabel("Use right mouse button to move,\n "
                + "left double click or mouse wheel to zoom.");
        helpPanel.add(helpLabel);
        
        /* Constructs right panel */
        add(rightPanel, BorderLayout.EAST);
        rightPanel.setPreferredSize(new Dimension(250, 1));		/* Set the width of the panel to 250,
        														   the height will be streched */
        rightPanel.setLayout(new FlowLayout());
        
        /* Constructs the tile selector combo box */
        JComboBox tileSourceSelector = new JComboBox(new TileSource[] { new OsmTileSource.Mapnik(),
                new OsmTileSource.TilesAtHome(), new OsmTileSource.CycleMap(), new BingAerialTileSource() });
        tileSourceSelector.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                map.setTileSource((TileSource) e.getItem());
            }
        });
        rightPanel.add(tileSourceSelector);
        
        /* Create the interaction combo box */
        rightPanel.add(interactionBox);
        interactionBox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
            	if (interactionBox.getSelectedIndex() == 0)
            		interaction = interactionType.NORMAL;
            	else if (interactionBox.getSelectedIndex() == 1)
            		interaction = interactionType.TOPOLOGICAL;
            }
        });
        
        /* Add coefficient slider */
        rightPanel.add(mappingCoefficient);
        
        /* Create the tabs */
        tabs.setPreferredSize(new Dimension(250, 600));
        tabs.addTab("Election", electionTab);
        tabs.addTab("World", worldTab);
        tabs.addChangeListener(new VisualizationEventListener());
        rightPanel.add(tabs);
        
        /* Constructs information panel for election 2009 */
        final JCheckBox renderNames = new JCheckBox("Render Names?");
        renderNames.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				map.setRenderNames(renderNames.isSelected());
			}
        });
        map.setRenderNames(false);
        electionTab.add(renderNames);
        
        /* Set listener and place combo box for election */
        displayStyleElection.addItemListener(new VisualizationEventListener());
        electionTab.add(displayStyleElection);
        
        /* Creates the information text box for election */
        informationElection = new JTextArea();
        informationElection.setPreferredSize(new Dimension(220, 250));
        informationElection.setText("None");
        informationElection.setBackground(rightPanel.getBackground());
        informationElection.setEditable(false);
        informationElectionPanel.add(informationElection);
        informationElectionPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Information"),
                BorderFactory.createEmptyBorder(5,5,5,5)));
        electionTab.add(informationElectionPanel);
        electionTab.add(partyChart);
        
        
        /* Set listener and place combo boxes for world */
        storkSelection.addItemListener(new VisualizationEventListener());
        worldTab.add(storkSelection);        
        displayStyleWorld.addItemListener(new VisualizationEventListener());
        worldTab.add(displayStyleWorld);
        
        /* Constructs information text box for world*/
        informationWorld = new JTextArea();
        informationWorld.setPreferredSize(new Dimension(220, 250));
        informationWorld.setText("None");
        informationWorld.setBackground(rightPanel.getBackground());
        informationWorld.setEditable(false);
        informationWorldPanel.add(informationWorld);
        informationWorldPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Information"),
                BorderFactory.createEmptyBorder(5,5,5,5)));
        worldTab.add(informationWorldPanel);
        
        
        /* Constructs main map display */
        try {
			map.setTileLoader(new OsmFileCacheTileLoader(map));
		} catch (Exception e) {
			e.printStackTrace();
		}
        
        map.setMapMarkerVisible(true);
        add(map, BorderLayout.CENTER);
        
        /* Call the change Listener once to start the initial visualization */
        new VisualizationEventListener().change();
    }
    
    private class VisualizationEventListener implements ChangeListener, ItemListener {
        private void displayVisualizationElection() {
      	  	if (displayStyleElection.getSelectedIndex() == 0)
            	drawElection(true, displayStyleTypeElection.GREEN_PARTY_NORMAL);
            else if (displayStyleElection.getSelectedIndex() == 1)
            	drawElection(true, displayStyleTypeElection.GREEN_PARTY_CORR_MALTE);
            else if (displayStyleElection.getSelectedIndex() == 2)
              	drawElection(true, displayStyleTypeElection.WINNER);
            else if (displayStyleElection.getSelectedIndex() == 3)
            		drawElection(true, displayStyleTypeElection.DIFFERENCE);
            else if (displayStyleElection.getSelectedIndex() == 4)
              	drawElection(true, displayStyleTypeElection.TURNOUT);
            else if (displayStyleElection.getSelectedIndex() == 5)
            		drawElection(true, displayStyleTypeElection.INFLUENCE);
            else if (displayStyleElection.getSelectedIndex() == 6)
          		drawElection(true, displayStyleTypeElection.SIZE);
        }
        
        private void displayVisualizationWorld() {
      		if (displayStyleWorld.getSelectedIndex() == 0)
              	drawWorld(true, displayStyleTypeStorks.RANDOM);
      		else if (displayStyleWorld.getSelectedIndex() == 1)
              	drawWorld(true, displayStyleTypeStorks.SIZE);
      		else if (displayStyleWorld.getSelectedIndex() == 2)
              	drawWorld(true, displayStyleTypeStorks.TRAVEL_THROUGH);
      		else if (displayStyleWorld.getSelectedIndex() == 3)
              	drawWorld(true, displayStyleTypeStorks.TRAVEL_THROUGH_PERCENTAGE); 
        }
        
    	private void change() {
            if (tabs.getSelectedIndex() == 0) {
          	  displayVisualizationElection();
            } else if (tabs.getSelectedIndex() == 1) {
          	  displayVisualizationWorld();
            }
    	}
    	
    	@Override
        public void stateChanged(ChangeEvent e) {
        	change();
        }

		@Override
		public void itemStateChanged(ItemEvent e) {
			change();
		}
      }
    
    /** 
     * Internal class to handle mouse events
     * 
     */
    private class MouseEventListener extends MouseAdapter {
    	/* is called when the mouse is clicked (pressed and released again.
    	 * This is used to select a point or polygon by an user.
    	 * 
    	 * (non-Javadoc)
    	 * @see java.awt.event.MouseAdapter#mouseClicked(java.awt.event.MouseEvent)
    	 */
        public void mouseClicked(MouseEvent event) {    
    		Coordinate c = map.getPosition(event.getX(), event.getY());
    		
        	if (ew != null) {
        		if (interaction == interactionType.NORMAL) {
        			//test for point
        			MaltePoint mp = ew.getMaltePoint(c.getLon(), c.getLat());
        			Point p = map.getMapPosition(mp.getX(), mp.getY());
        			if (ew.isMalteVisible() && p != null && 
        					Math.sqrt((event.getX() - p.getX())*(event.getX() - p.getX()) + (event.getY() - p.getY())*(event.getY() - p.getY())) <= 6) {
        				informationElection.setText(mp.getInformation());
        				partyChart.setParties(null);
        				partyChart.paint(getGraphics());
        			} else {
	        			//test for polygon
	        			Constituency constituency = ew.getConstituency(c.getLon(), c.getLat());
	        	    	if (c != null) {
	        	    		informationElection.setText(constituency.getInformation());
	        	    		drawPartyResults(constituency.getResult().values(), constituency.getVoter());
	        	    	}
        			}
        		} else if (interaction == interactionType.TOPOLOGICAL) {
        			//test for point
        			MaltePoint mp = ew.getMaltePoint(c.getLon(), c.getLat());
        			Point p = map.getMapPosition(mp.getX(), mp.getY());
        			if (ew.isMalteVisible() && p != null && Math.sqrt((event.getX() - p.getX())*(event.getX() - p.getX()) + (event.getY() - p.getY())*(event.getY() - p.getY())) <= 6) {	
        				if (ew.getLastPoint() != null) {
        					double dist = ew.getLastPoint().compareToPoint(mp);
        					informationElection.setText(String.valueOf(dist));
        					ew.setLastPoint(null);
        				} else if (ew.getLastPolygon() != null) {
        					PointRelation r = ew.getLastPolygon().compareTo(mp);
        					informationElection.setText(r.toString());
        					ew.setLastPolygon(null);
        				} else {
        					informationElection.setText("Selected point " + mp.getId().toString());
        					ew.setLastPoint(mp);
        				}
        			} else {
	        			//test for polygon
	        			Constituency constituency = ew.getConstituency(c.getLon(), c.getLat());
	        	    	if (constituency != null) {
	        	    		drawPartyResults(constituency.getResult().values(), constituency.getVoter());
	        	    		
	        	    		if (ew.getLastPoint() != null) {
	        	    			PointRelation r = constituency.compareTo(ew.getLastPoint());
	        	    			informationElection.setText(r.toString());
	        					ew.setLastPoint(null);
	        				} else if (ew.getLastPolygon() != null) {
	        					PolygonRelation s = constituency.compareTo(ew.getLastPolygon());
	        					switch(s){
	        						case INSIDE: informationElection.setText("Inside"); break;
	        						case DISJOINT: informationElection.setText("Disjoint"); break;
	        						case MEET: informationElection.setText("Meet"); break;
	        						case OVERLAPS: informationElection.setText("Overlaps"); break;
	        						case COVERS: informationElection.setText("Covers"); break;
	        						case CONTAINS: informationElection.setText("Contains"); break;
	        						case COVEREDBY: informationElection.setText("Coveredby"); break;
	        						case EQUAL: informationElection.setText("Equals"); break;
	        					}
	        					ew.setLastPolygon(null);
	        				} else {
	        					informationElection.setText("Selected polygon " + constituency.getName());
		        	    		ew.setLastPolygon(constituency);
	        				}
	        	    	}
        			}
        		}
        	} else if (w != null) {
        		if (interaction == interactionType.NORMAL) {
        			//test for point
        			StorkPoint sp;
        			if (storkSelection.getSelectedIndex() == 0)
        				sp = w.getStorkPoint(c.getLon(), c.getLat());
            		else 
            			sp = w.getStorkPoint(c.getLon(), c.getLat(), w.getStorkId(storkSelection.getSelectedIndex()));
        			
        			if (sp != null) {
	        			Point p = map.getMapPosition(sp.getX(), sp.getY());
	        			if (p != null && Math.sqrt((event.getX() - p.getX())*(event.getX() - p.getX()) + (event.getY() - p.getY())*(event.getY() - p.getY())) <= 6) {
	        				informationWorld.setText(sp.getInformation());
	        			} else {
		        			//test for polygon
	                		Country country = w.getCountry(c.getLon(), c.getLat());
	                		if (country != null) {
	                			informationWorld.setText(country.getInformation());
	                		}
	        			}    
        			}
        		} else if (interaction == interactionType.TOPOLOGICAL) {
        			//test for point
        			StorkPoint sp = w.getStorkPoint(c.getLon(), c.getLat());
        			if (sp != null) {
	        			Point p = map.getMapPosition(sp.getX(), sp.getY());
	        			if (p != null && Math.sqrt((event.getX() - p.getX())*(event.getX() - p.getX()) + (event.getY() - p.getY())*(event.getY() - p.getY())) <= 6) {
	        				if (w.getLastPoint() != null) {
	        					double dist = w.getLastPoint().compareToPoint(sp);
	        					informationWorld.setText(String.valueOf(dist));
	        					w.setLastPoint(null);
	        				} else if (w.getLastPolygon() != null) {
	        					PointRelation r = w.getLastPolygon().compareToPoint(sp);
	        					informationWorld.setText(r.toString());
	        					w.setLastPolygon(null);
	        				} else {
	        					informationWorld.setText("Selected point " + sp.getId().toString());
	        					w.setLastPoint(sp);
	        				}
	        			} else {
		        			//test for polygon
	                		Country country = w.getCountry(c.getLon(), c.getLat());
		        	    	if (country != null) {
		        	    		if (w.getLastPoint() != null) {
		        	    			PointRelation r = country.compareToPoint(w.getLastPoint());
		        	    			informationWorld.setText(r.toString());
		        					w.setLastPoint(null);
		        				} else if (w.getLastPolygon() != null) {
		        					PolygonRelation s = country.compareToCountry(w.getLastPolygon());
		        					switch (s) {
		        						case INSIDE: informationWorld.setText("Inside"); break;
		        						case DISJOINT: informationWorld.setText("Disjoint"); break;
		        						case MEET: informationWorld.setText("Meet"); break;
		        						case OVERLAPS: informationWorld.setText("Overlaps"); break;
		        						case COVERS: informationWorld.setText("Covers"); break;
		        						case CONTAINS: informationWorld.setText("Contains"); break;
		        						case COVEREDBY: informationWorld.setText("Coveredby"); break;
		        						case EQUAL: informationWorld.setText("Equals"); break;
		        					}
		        					w.setLastPolygon(null);
		        				} else {
		        					informationWorld.setText("Selected polygon " + country.getName());
			        	    		w.setLastPolygon(country);
		        				}
		        	    	}
	        			}
        			}
        		}
        	}
        }
    }
    
    private void drawPartyResults(Collection<PartyResults> parties, int voters) {
    	partyChart.setParties(parties);
    	partyChart.setVoter(voters);
    	partyChart.paint(partyChart.getGraphics());
    }
    
    private void drawStorks() {
		map.mapMarkerList.clear();
		
		if (w.getStorks() != null) {
	    	Iterator<StorkPoint> it = w.getStorks().values().iterator();
	        while (it.hasNext()) {
	        	StorkPoint sp = it.next();
	            map.mapMarkerList.add(new MapMarkerDot(sp.getX(), sp.getY()));
	        }
		}
    }
    
	private void drawWorld(boolean show, displayStyleTypeStorks displayStyle) {
    	if (show) {
    		/* Delete all drawings of election */
    		drawElection(false, null);
    		
    		w = new World();
    		w.loadAllStorks();
    		
    		int coefficient = mappingCoefficient.getValue();
    		/* Draw the selected visualization and take in account the selected storks */
    		if (displayStyle == displayStyleTypeStorks.RANDOM) {
				w.setColorRandom();
    			if (storkSelection.getSelectedIndex() == 0)
        			w.loadAllStorks();
        		else 
        			w.loadSelectedStorks(w.getStorkId(storkSelection.getSelectedIndex()));
    		} else if (displayStyle == displayStyleTypeStorks.SIZE) {
    			w.setColorBySize(coefficient);
    			w.setStorks(null);
    		} else if (displayStyle == displayStyleTypeStorks.TRAVEL_THROUGH) {
    			if (storkSelection.getSelectedIndex() == 0) {
        			w.setColorByTravelThrough();
        			w.loadAllStorks();
    			} else {
    				w.setColorByTravelThrough(w.getStorkId(storkSelection.getSelectedIndex()));
        			w.loadSelectedStorks(w.getStorkId(storkSelection.getSelectedIndex()));
    			}
    		} else if (displayStyle == displayStyleTypeStorks.TRAVEL_THROUGH_PERCENTAGE) {
    			
    			if (storkSelection.getSelectedIndex() == 0) {
        			w.loadAllStorks();
        			w.setColorByTravelThroughPercentage(coefficient);
    			}
        		else {
        			w.loadSelectedStorks(w.getStorkId(storkSelection.getSelectedIndex()));
        			w.setColorByTravelThroughPercentage(w.getStorkId(storkSelection.getSelectedIndex()));
        		}
    		}

    		LinkedList<CountryPolygon> all = new LinkedList<CountryPolygon>();
    		Iterator<Country> it = w.getCountryPolygons().iterator();
    		while (it.hasNext()) {
    			Country c = it.next();
    			all.addAll(c.getPolygons());
    		}
    		map.addMapMarkerPolygonList(all);
    		drawStorks();
    	} else {
    		map.mapMarkerPolygonList.clear();
    		map.mapMarkerList.clear();
    		w = null;
    	}
}
    
    private void drawElection(boolean show, displayStyleTypeElection style) {
    	if (show) {
    		/* Delete all drawings of world */
    		drawWorld(false, null);
    		           
            ew = new ElectionWorld();
            
            /* Draw the selected visualization */
            int coefficient = mappingCoefficient.getValue();
            if (style == displayStyleTypeElection.GREEN_PARTY_NORMAL)
            	ew.setColorByGreenParty(coefficient);
            else if (style == displayStyleTypeElection.GREEN_PARTY_CORR_MALTE)
            	ew.setColorByGreenPartyCorrMalte(coefficient);
            else if (style == displayStyleTypeElection.WINNER)
            	ew.setColorByWinner();
            else if (style == displayStyleTypeElection.DIFFERENCE)
            	ew.setColorByDifference(coefficient);
            else if (style == displayStyleTypeElection.TURNOUT)
            	ew.setColorByTurnout(coefficient);
            else if (style == displayStyleTypeElection.INFLUENCE)
            	ew.setColorByInfluence(coefficient);
            else if (style == displayStyleTypeElection.SIZE)
            	ew.setColorBySize(coefficient);
            
            map.addMapMarkerPolygonList(ew.getDrawList());
            
            /* Draw the positions of Malte Spitz only for two visualizations */
            if (style == displayStyleTypeElection.GREEN_PARTY_CORR_MALTE || style == displayStyleTypeElection.GREEN_PARTY_NORMAL) {
	            Iterator<MaltePoint> it = ew.getMaltePoints().values().iterator();
	            while (it.hasNext()) {
	            	MaltePoint mp = it.next();
	                map.mapMarkerList.add(new MapMarkerDot(mp.getX(), mp.getY()));
	            }
	            ew.setMalteVisible(true);
            } else {
            	map.mapMarkerList.clear();
            	ew.setMalteVisible(false);
            }
    	} else {
    		map.mapMarkerPolygonList.clear();
    		map.mapMarkerList.clear();
    		ew = null;
    	}
    }
    

    /**
     *  Set same window standard operations 
     */
    private void createWindow() {
        setTitle("Geographic Information Systems SS 2011 - Stephanie Marx, Dirk Kirsten");
        setSize(800, 800);									/* in case maximizing the window fails */
        setLayout(new BorderLayout());
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        new GisApplication().setVisible(true);
    }
}
