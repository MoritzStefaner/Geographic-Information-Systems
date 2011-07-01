package org.openstreetmap.gui.jmapviewer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
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
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.gis.db.Constituency;
import org.gis.db.ElectionWorld;
import org.gis.db.GisPoint;
import org.gis.db.MaltePoint;
import org.gis.db.Party;
import org.gis.db.StorkPoint;
import org.gis.db.World;
import org.gis.tools.ExportObjectsTool;
import org.openstreetmap.gui.jmapviewer.interfaces.PartyChart;
import org.openstreetmap.gui.jmapviewer.interfaces.TileSource;
import org.openstreetmap.gui.jmapviewer.tilesources.BingAerialTileSource;
import org.openstreetmap.gui.jmapviewer.tilesources.OsmTileSource;

/**
 *
 * Display map data using {@link JMapViewer}.
 *
 * @author Dirk Kirsten
 * @author Stefanie Marx
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
    
    private static enum displayStyleType { GREEN_PARTY_NORMAL, GREEN_PARTY_CORR_MALTE, WINNER, DIFFERENCE,
    	TURNOUT, INFLUENCE }; 
    
    public GisApplication() {
        super("Geographic Information Systems SS 2011 - Stephanie Marx, Dirk Kirsten");
        setSize(800, 800);
        map = new JMapViewer();
        w = null;
        ew = null;
        JPanel rightPanel = new JPanel();
        JPanel helpPanel = new JPanel();
        JPanel informationElectionPanel = new JPanel(new FlowLayout());
        JPanel informationWorldPanel = new JPanel();
        JPanel electionTab = new JPanel(new FlowLayout());
        JPanel worldTab = new JPanel(new FlowLayout());
        final JTabbedPane tabs = new JTabbedPane();
        String[] displayStyleTypes = { "Results Green Party", "Green Party <> Malte Spitz", 
        		"Winner", "Difference", "Turnout", "Influence" };
        final JComboBox displayStyle = new JComboBox(displayStyleTypes);
        String[] storkSelectionOptions = { "All", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"};
        storkSelection = new JComboBox(storkSelectionOptions);
        String[] displayStyleWorldTypes = { "Normal", "Travel through" };
        final JComboBox displayStyleWorld = new JComboBox(displayStyleWorldTypes);
        partyChart = new PartyChart();
        partyChart.setPreferredSize(new Dimension(201, 200));
        
        /* Set same Window standard operations */
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        map.addMouseListener(new MouseEventListener());
        
        /* Construct lower help text */
        add(helpPanel, BorderLayout.SOUTH);
        JLabel helpLabel = new JLabel("Use right mouse button to move,\n "
                + "left double click or mouse wheel to zoom.");
        helpPanel.add(helpLabel);
        
        /* Constructs right panel */
        add(rightPanel, BorderLayout.EAST);
        rightPanel.setPreferredSize(new Dimension(250, 1));
        rightPanel.setLayout(new FlowLayout());
        JComboBox tileSourceSelector = new JComboBox(new TileSource[] { new OsmTileSource.Mapnik(),
                new OsmTileSource.TilesAtHome(), new OsmTileSource.CycleMap(), new BingAerialTileSource() });
        tileSourceSelector.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                map.setTileSource((TileSource) e.getItem());
            }
        });
        rightPanel.add(tileSourceSelector);
        
        /* Creates the tabs */
        tabs.setPreferredSize(new Dimension(250, 600));
        tabs.addTab("Election", electionTab);
        tabs.addTab("World", worldTab);
        tabs.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
              if (tabs.getSelectedIndex() == 0) {
            	  if (displayStyle.getSelectedIndex() == 0)
                  	displayMalte(true, displayStyleType.GREEN_PARTY_NORMAL);
                  else if (displayStyle.getSelectedIndex() == 1)
                  	displayMalte(true, displayStyleType.GREEN_PARTY_CORR_MALTE);
                  else if (displayStyle.getSelectedIndex() == 2)
                    	displayMalte(true, displayStyleType.WINNER);
                  else if (displayStyle.getSelectedIndex() == 3)
                  		displayMalte(true, displayStyleType.DIFFERENCE);
                  else if (displayStyle.getSelectedIndex() == 4)
                    	displayMalte(true, displayStyleType.TURNOUT);
                  else if (displayStyle.getSelectedIndex() == 5)
                  		displayMalte(true, displayStyleType.INFLUENCE);
              } else if (tabs.getSelectedIndex() == 1) {
            	  showWorld(true);
              }
            }
          });
        rightPanel.add(tabs);
        
        /* Constructs Information panel for election 2009 */
        final JCheckBox renderNames = new JCheckBox("Render Names?");
        renderNames.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				map.setRenderNames(renderNames.isSelected());
			}
        });
        map.setRenderNames(false);
        electionTab.add(renderNames);
        
        displayStyle.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (displayStyle.getSelectedIndex() == 0)
                	displayMalte(true, displayStyleType.GREEN_PARTY_NORMAL);
                else if (displayStyle.getSelectedIndex() == 1)
                	displayMalte(true, displayStyleType.GREEN_PARTY_CORR_MALTE);
                else if (displayStyle.getSelectedIndex() == 2)
                	displayMalte(true, displayStyleType.WINNER);
                else if (displayStyle.getSelectedIndex() == 3)
                  	displayMalte(true, displayStyleType.DIFFERENCE);
                else if (displayStyle.getSelectedIndex() == 4)
                	displayMalte(true, displayStyleType.TURNOUT);
                else if (displayStyle.getSelectedIndex() == 5)
                	displayMalte(true, displayStyleType.INFLUENCE);
            }
        });
        electionTab.add(displayStyle);
        
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
        
        /* Constructs Information panel for world*/
        storkSelection.addItemListener(new ItemListener() {
        	public void itemStateChanged(ItemEvent e) {
        		if (storkSelection.getSelectedIndex() == 0)
        			w.loadAllStorks();
        		else 
        			w.loadSelectedStorks(getStorkId());
        		
        		drawStorks();
        	}
        });
        worldTab.add(storkSelection);
        
        displayStyleWorld.addItemListener(new ItemListener() {
        	public void itemStateChanged(ItemEvent e) {
        		if (displayStyleWorld.getSelectedIndex() == 0) {
        			if (storkSelection.getSelectedIndex() == 0)
            			w.loadAllStorks();
            		else 
            			w.loadSelectedStorks(getStorkId());
        		} else if (displayStyleWorld.getSelectedIndex() == 1) {
        			w.setColorByTravelThrough();
        			
        			if (storkSelection.getSelectedIndex() == 0)
            			w.loadAllStorks();
            		else 
            			w.loadSelectedStorks(getStorkId());
        		} else if (displayStyleWorld.getSelectedIndex() == 2) {
        			
        			if (storkSelection.getSelectedIndex() == 0) {
            			w.loadAllStorks();
            			w.setColorByTravelThroughPercentage();
        			}
            		else {
            			w.loadSelectedStorks(getStorkId());
            			w.setColorByTravelThroughPercentage(getStorkId());
            		}
        		}
        			
        	}
        });
        worldTab.add(displayStyleWorld);
        
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
        
        displayMalte(true, displayStyleType.GREEN_PARTY_NORMAL);
    }
    
    private Integer getStorkId() {
    	if (storkSelection.getSelectedIndex() == 1)
			return 91397;
		else if (storkSelection.getSelectedIndex() == 2)
			return 77195;
		else if (storkSelection.getSelectedIndex() == 3)
			return 91398;
		else if (storkSelection.getSelectedIndex() == 4)
			return 91398;
		else if (storkSelection.getSelectedIndex() == 5)
			return 93412;
		else if (storkSelection.getSelectedIndex() == 6)
			return 93411;
		else if (storkSelection.getSelectedIndex() == 7)
			return 54977;
		else if (storkSelection.getSelectedIndex() == 8)
			return 14544;
		else if (storkSelection.getSelectedIndex() == 9)
			return 40534;
		else if (storkSelection.getSelectedIndex() == 10)
			return 54983;
		else if (storkSelection.getSelectedIndex() == 11)
			return 54988;
		else if (storkSelection.getSelectedIndex() == 12)
			return 14543;
		else
			return null;
    }
    
    /* Internal class to handle mouse events
     * 
     */
    public class MouseEventListener extends MouseAdapter {
    	/* is called when the mouse is clicked (pressed and released again.
    	 * This is used to select a point or polygon by an user.
    	 * 
    	 * (non-Javadoc)
    	 * @see java.awt.event.MouseAdapter#mouseClicked(java.awt.event.MouseEvent)
    	 */
        public void mouseClicked(MouseEvent event) {    
    		Coordinate c = map.getPosition(event.getX(), event.getY());
    		
        	if (ew != null) {
        		getConstituency(c.getLon(), c.getLat());
        	} else if (w != null) {
        		getCountry(c.getLon(), c.getLat());
        	}
        }
    }
    
    private void drawPartyResults(LinkedList<Party> parties, int voters) {
    	partyChart.setParties(parties);
    	partyChart.setVoter(voters);
    	partyChart.paint(partyChart.getGraphics());
    }
    
    private void getConstituency(double longitude, double latitude) {
    	GisPoint p = new GisPoint(latitude, longitude);
    	Constituency c = ew.getConstituencyMap().get(ew.compareToGermany(p));
    	if (c != null) {
    		informationElection.setText(c.getInformation());
    		drawPartyResults(c.getElectionResult(), c.getVoter());
    	}
    }
    
    private void getCountry(double longitude, double latitude) {
    	GisPoint p = new GisPoint(latitude, longitude);
    	MapMarkerPolygon pol = w.getCountries().get(w.compareToWorld(p));
    	if (pol != null)
    		informationWorld.setText(pol.getPolygon().getText());
    }
    
    private void drawStorks() {
		map.mapMarkerList.clear();
    	Iterator<StorkPoint> it = w.getStorks().values().iterator();
        while (it.hasNext()) {
        	StorkPoint sp = it.next();
            map.mapMarkerList.add(new MapMarkerDot(sp.getX(), sp.getY()));
        }
    }
    
    private void showWorld(boolean show) {
    	if (show) {
    		map.mapMarkerPolygonList.clear();
    		ew = null;
    		
    		w = new World();
    		w.loadAllStorks();
    		map.addMapMarkerPolygonList(w.getWorldPolygons());
    		
    		drawStorks();
    	} else {
    		map.mapMarkerPolygonList.clear();
    		w = null;
    	}
}
    
    private void displayMalte(boolean show, displayStyleType style) {
    	if (show) {
            Collection<MaltePoint> c = ExportObjectsTool.exportMalte().values();
            w = null;
            
            ew = new ElectionWorld();
            
            if (style == displayStyleType.GREEN_PARTY_NORMAL)
            	ew.setColorByGreenPartyLinear();
            else if (style == displayStyleType.GREEN_PARTY_CORR_MALTE)
            	ew.setColorByGreenPartyCorrMalte(c);
            else if (style == displayStyleType.WINNER)
            	ew.setColorByWinner();
            else if (style == displayStyleType.DIFFERENCE)
            	ew.setColorByDifference();
            else if (style == displayStyleType.TURNOUT)
            	ew.setColorByTurnout();
            else if (style == displayStyleType.INFLUENCE)
            	ew.setColorByInfluence();
            
            map.addMapMarkerPolygonList(ew.getDrawList());
            
            if (style == displayStyleType.GREEN_PARTY_CORR_MALTE || style == displayStyleType.GREEN_PARTY_NORMAL) {
	            Iterator<MaltePoint> it = c.iterator();
	            while (it.hasNext()) {
	            	MaltePoint mp = it.next();
	                map.mapMarkerList.add(new MapMarkerDot(mp.getX(), mp.getY()));
	            }
            } else
            	map.mapMarkerList.clear();
    	} else {
    		map.mapMarkerPolygonList.clear();
    		map.mapMarkerList.clear();
    		map.setZoom(map.getZoom()*2);
    		ew = null;
    	}
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        new GisApplication().setVisible(true);
    }
}
