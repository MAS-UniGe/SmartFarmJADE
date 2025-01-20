package com.sdai.smartfarm.environment;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.sdai.smartfarm.environment.tiles.Tile;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Color;

public class EnvironmentViewer extends JPanel {

    private static final int DEFAULT_WIDTH = 500;
    private static final int DEFAULT_HEIGHT = 500;

    private final transient Environment environment;

    private int tileSize = 50;      // this initial value is here just to avoid a potential NullPtrException: it gets overridden immediately

    private final int minTileSize = 10;
    private final int maxTileSize = 100;

    private float worldX = 0.0f;
    private float worldY = 0.0f;

    public EnvironmentViewer(Environment environment) {
        this.environment = environment;
    }

    public void renderEnvironment() {
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

        int gridWidth = getWidth() / tileSize + 1;
        int gridHeight = getHeight() / tileSize + 1;

        int startX = (int) worldX;
        int startY = (int) worldY;

        int offsetX = (int) ((worldX - startX) * tileSize);
        int offsetY = (int) ((worldY - startY) * tileSize);

        for(int row = 0; row < gridHeight; row++) {
            for(int col = 0; col < gridWidth; col++) {
                
                Tile tile = environment.getTile(startX + col, startY + row);
                if (tile == null)
                    continue;

                int drawX = (col * tileSize) - offsetX;
                int drawY = (row * tileSize) - offsetY;

                g.setColor(tile.getColor());
                g.fillRect(drawX, drawY, tileSize, tileSize);

            }
        }

        /* 
        // Draw the tiles
        for (int row = 0; row < environment.getHeight(); row++) {
            for (int col = 0; col < environment.getWidth(); col++) {

                Tile tile = environment.getTile(row, col);
                g.setColor(tile.getColor());

                // Calculate tile position
                int x = col * tileSize;
                int y = row * tileSize;

                // Draw the tile
                g.fillRect(x, y, tileSize, tileSize);

                // Draw grid lines for better visualization (?)
                g.setColor(new Color(100, 100, 100, 50));
                g.drawRect(x, y, tileSize, tileSize);
            }
        }*/

    }

    @Override
    public Dimension getPreferredSize() {
        // Set the preferred size of the panel to fit the tiles
        return new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    public void recomputeTileSize() {
        int width = getWidth();
        int height = getHeight();

        tileSize = Math.min(width / environment.getWidth(), height / environment.getHeight());
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