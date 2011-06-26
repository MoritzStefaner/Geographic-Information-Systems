package org.openstreetmap.gui.jmapviewer;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.gis.db.Constituency;
import org.gis.db.ElectionWorld;
import org.gis.db.MaltePoint;
import org.gis.db.World;
import org.gis.tools.ExportObjectsTool;
import org.openstreetmap.gui.jmapviewer.interfaces.TileLoader;
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

    public GisApplication() {
        super("Geographic Information Systems");
        setSize(400, 400);
        map = new JMapViewer();
        // final JMapViewer map = new JMapViewer(new MemoryTileCache(),4);
        // map.setTileLoader(new OsmFileCacheTileLoader(map));
        // new DefaultMapController(map);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        JPanel panel = new JPanel();
        JPanel helpPanel = new JPanel();
        add(panel, BorderLayout.NORTH);
        add(helpPanel, BorderLayout.SOUTH);
        JLabel helpLabel = new JLabel("Use right mouse button to move,\n "
                + "left double click or mouse wheel to zoom.");
        helpPanel.add(helpLabel);
        JButton button = new JButton("setDisplayToFitMapMarkers");
        button.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                map.setDisplayToFitMapMarkers();
            }
        });
        JComboBox tileSourceSelector = new JComboBox(new TileSource[] { new OsmTileSource.Mapnik(),
                new OsmTileSource.TilesAtHome(), new OsmTileSource.CycleMap(), new BingAerialTileSource() });
        tileSourceSelector.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                map.setTileSource((TileSource) e.getItem());
            }
        });
        JComboBox tileLoaderSelector;
        try {
            tileLoaderSelector = new JComboBox(new TileLoader[] { new OsmFileCacheTileLoader(map),
                    new OsmTileLoader(map) });
        } catch (IOException e) {
            tileLoaderSelector = new JComboBox(new TileLoader[] { new OsmTileLoader(map) });
        }
        tileLoaderSelector.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                map.setTileLoader((TileLoader) e.getItem());
            }
        });
        map.setTileLoader((TileLoader) tileLoaderSelector.getSelectedItem());
        panel.add(tileSourceSelector);
        panel.add(tileLoaderSelector);
        
        map.setMapMarkerVisible(true);
        map.setTileGridVisible(false);

        final JCheckBox showZoomControls = new JCheckBox("Show zoom controls");
        showZoomControls.setSelected(map.getZoomContolsVisible());
        showZoomControls.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                map.setZoomContolsVisible(showZoomControls.isSelected());
            }
        });
        panel.add(showZoomControls);
        panel.add(button);
        
        final JCheckBox malteControl = new JCheckBox("show Malte", true);
        malteControl.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                displayMalte(malteControl.isSelected());
            }
        });
        panel.add(malteControl);
        displayMalte(true);
        
        add(map, BorderLayout.CENTER);
        
		//World w = new World();
		//map.addMapMarkerPolygonList(w.getWorldPolygons());

        // map.setDisplayPositionByLatLon(49.807, 8.6, 11);
        // map.setTileGridVisible(true);
    }
    
    private void displayMalte(boolean show) {
    	if (show) {
            ElectionWorld ew = new ElectionWorld();
            map.addMapMarkerPolygonList(ew.getDrawList());
            
            Collection<MaltePoint> c = ExportObjectsTool.exportMalte().values();
            Iterator<MaltePoint> it = c.iterator();
            while (it.hasNext()) {
            	MaltePoint mp = it.next();
                map.mapMarkerList.add(new MapMarkerDot(mp.getY(), mp.getX()));
            }
    	} else {
    		map.mapMarkerPolygonList.clear();
    		map.mapMarkerList.clear();
    		map.setZoom(map.getZoom()*2);
    	}
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        new GisApplication().setVisible(true);
    }
}
