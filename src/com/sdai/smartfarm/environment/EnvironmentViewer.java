package com.sdai.smartfarm.environment;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.sdai.smartfarm.agents.BaseFarmingAgent;
import com.sdai.smartfarm.environment.tiles.Tile;
import com.sdai.smartfarm.settings.WindowSettings;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Color;

public class EnvironmentViewer extends JPanel {

    private static WindowSettings windowSettings = WindowSettings.defaultWindowSettings();

    private final transient Environment environment;

    private float worldX;
    private float worldY;

    private int tileSize;

    // TODO: remove this it is here just for debug
    Color[] coloredClusters;

    public EnvironmentViewer(Environment environment) {
        this.environment = environment;

        worldX = environment.getWidth() / 2.0f;
        worldY = environment.getHeight() / 2.0f;
        recomputeTileSize();

        coloredClusters = new Color[environment.getWidth() * environment.getHeight()];

    }

    public void createWindow() {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Smart Farming Simulation");
            frame.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
            frame.setSize(getPreferredSize());

            frame.add(this);
            recomputeTileSize();

            frame.setVisible(true);
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());

        // The window is splitted into a grid of tiles
        int gridWidth = getWidth() / tileSize + 1;
        int gridHeight = getHeight() / tileSize + 1;

        int startX = (int) worldX;
        int startY = (int) worldY;

        // offsets = how much we must offset each tile to be consistent with fractional world X and Y
        int offsetX = (int) ((worldX - startX) * tileSize);
        int offsetY = (int) ((worldY - startY) * tileSize);

        // DEBUG ONLY !!!
        /* 
        int[] fieldsMap = null;

        List<List<IntegerVector>> clusters = null;
        
        for(int row = 0; row < gridHeight; row++) {
            for(int col = 0; col < gridWidth; col++) {
                FarmingAgent agent = environment.getAgentAt(startX + col, startY + row);

                if (agent instanceof DroneAgent droneAgent) {

                    int[] fmap = droneAgent.getFieldsMap();

                    if (fmap != null) fieldsMap = fmap;

                    List<List<IntegerVector>> cl = droneAgent.getClusters();

                    if(cl != null) clusters = cl;
                }
            }
        }

        

        if(clusters != null) {
            for(List<IntegerVector> cluster : clusters) {
                
                Random rng = new Random();

                Color color = new Color(rng.nextInt(25, 255), rng.nextInt(25, 255), rng.nextInt(25, 255));

                for(IntegerVector point: cluster) {
                    int index = point.intValue(1) * environment.getWidth() + point.intValue(0);
                    if (coloredClusters[index] == null)
                        coloredClusters[index] = color;
                }


                
            }
        } */
        ////
        

        for(int row = 0; row < gridHeight; row++) {
            for(int col = 0; col < gridWidth; col++) {

                // Get correct tile from world environment
                Tile tile = environment.getTile(startX + col, startY + row);
                if (tile == null)
                    continue;

                int drawX = (col * tileSize) - offsetX;
                int drawY = (row * tileSize) - offsetY;

                // Draw the tile
                g.setColor(tile.getColor());
                g.fillRect(drawX, drawY, tileSize, tileSize);

                // DEBUG ONLY
                /*int y = startY + row;
                int x = startX + col;
                int index = y * environment.getWidth() + x;
                
                if(x >= 0 && x < environment.getWidth() && y >= 0 && y < environment.getHeight() && fieldsMap != null && fieldsMap[index] != -1) {
                    g.setColor(new Color((fieldsMap[index] * 300) % 256, (fieldsMap[index] * 250) % 200, (fieldsMap[index] * 400) % 150));
                    //if (coloredClusters[index] != null) {
                        //g.setColor(coloredClusters[index]);
                        g.fillRect(drawX, drawY, tileSize, tileSize);
                    //}
                    
                }*/
                //////

                // Draw grid lines for better visualization (?) 
                g.setColor(new Color(100, 100, 100, 40));
                g.drawRect(drawX, drawY, tileSize, tileSize);

                BaseFarmingAgent agent = environment.getAgentAt(startX + col, startY + row);

                // TODO: think about either switch or polymorphism
                /*switch(agent) {
                    DRONE:
                        break;
                    ROBOT:
                        break;
                    TRACTOR:
                        break;
                    //default == null == do nothing
                }*/

                if (agent != null) {
                    g.setColor(new Color(44, 44, 44));
                    g.fillRect(drawX, drawY, tileSize, tileSize);
                }

            }
        }


    }

    @Override
    public Dimension getPreferredSize() {
        // Set the preferred size of the panel to fit the tiles
        return new Dimension(windowSettings.width(), windowSettings.height());
    }

    public void recomputeTileSize() {
        int width = getWidth();
        int height = getHeight();

        tileSize = Math.min(width / windowSettings.gridSize(), height / windowSettings.gridSize());

        tileSize = Math.min(Math.max(tileSize, windowSettings.minTileSize()), windowSettings.maxTileSize());
    }

    public int getTileSize() {
        return tileSize;
    }

    public float getWorldX() {
        return worldX;
    }

    public void setWorldX(float worldX) {
        this.worldX = worldX;
    }

    public float getWorldY() {
        return worldY;
    }

    public void setWorldY(float worldY) {
        this.worldY = worldY;
    }
}