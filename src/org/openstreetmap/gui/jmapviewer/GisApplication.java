package org.openstreetmap.gui.jmapviewer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.Iterator;

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
import org.gis.db.World;
import org.gis.tools.ExportObjectsTool;
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
    
    private static enum displayStyleType { GREEN_PARTY_NORMAL, GREEN_PARTY_CORR_MALTE }; 
    
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
        String[] displayStyleTypes = {"Results Green Party", "Green Party <> Malte Spitz"};
        final JComboBox displayStyle = new JComboBox(displayStyleTypes);
        
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
        tabs.setPreferredSize(new Dimension(250, 500));
        tabs.addTab("Election", electionTab);
        tabs.addTab("World", worldTab);
        tabs.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
              if (tabs.getSelectedIndex() == 0) {
            	  if (displayStyle.getSelectedIndex() == 0)
                  	displayMalte(true, displayStyleType.GREEN_PARTY_NORMAL);
                  else if (displayStyle.getSelectedIndex() == 0)
                  	displayMalte(true, displayStyleType.GREEN_PARTY_CORR_MALTE);
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
            }
        });
        electionTab.add(displayStyle);
        
        informationElection = new JTextArea();
        informationElection.setPreferredSize(new Dimension(220, 420));
        informationElection.setText("None");
        informationElection.setBackground(rightPanel.getBackground());
        informationElection.setEditable(false);
        informationElectionPanel.add(informationElection);
        informationElectionPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Information"),
                BorderFactory.createEmptyBorder(5,5,5,5)));
        electionTab.add(informationElectionPanel);
        
        /* Constructs Information panel for world*/
        informationWorld = new JTextArea();
        informationWorld.setPreferredSize(new Dimension(220, 420));
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
        
        displayMalte(true, displayStyleType.GREEN_PARTY_CORR_MALTE);
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
    
    private void getConstituency(double longitude, double latitude) {
    	GisPoint p = new GisPoint(latitude, longitude);
    	Constituency c = ew.getConstituencyMap().get(ew.compareToGermany(p));
    	if (c != null)
    		informationElection.setText(c.getInformation());
    }
    
    private void getCountry(double longitude, double latitude) {
    	GisPoint p = new GisPoint(latitude, longitude);
    	MapMarkerPolygon pol = w.getCountries().get(w.compareToWorld(p));
    	if (pol != null)
    		informationWorld.setText(pol.getPolygon().getText());
    }
    
    private void showWorld(boolean show) {
    	if (show) {
    		w = new World();
    		map.addMapMarkerPolygonList(w.getWorldPolygons());
    	} else {
    		map.mapMarkerPolygonList.clear();
    		w = null;
    	}
}
    
    private void displayMalte(boolean show, displayStyleType style) {
    	if (show) {
            Collection<MaltePoint> c = ExportObjectsTool.exportMalte().values();
            ew = new ElectionWorld();
            
            if (style == displayStyleType.GREEN_PARTY_NORMAL)
            	ew.setColorByGreenPartyLinear();
            else if (style == displayStyleType.GREEN_PARTY_CORR_MALTE)
            	ew.setColorByGreenPartyCorrMalte(c);
            
            map.addMapMarkerPolygonList(ew.getDrawList());
            
            Iterator<MaltePoint> it = c.iterator();
            while (it.hasNext()) {
            	MaltePoint mp = it.next();
                map.mapMarkerList.add(new MapMarkerDot(mp.getX(), mp.getY()));
            }
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
