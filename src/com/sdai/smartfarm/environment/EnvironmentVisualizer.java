package com.sdai.smartfarm.environment;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.Color;

public class EnvironmentVisualizer extends JPanel {

    private final transient Environment environment;

    private int tileSize = 50;      // this initial value is here just to avoid a potential NullPtrException: it gets overridden immediately

    public EnvironmentVisualizer(Environment environment) {
        this.environment = environment;

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                calculateTileSize();
                repaint();
            }
        });
    }

    public void renderEnvironment() {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Smart Farming Simulation");
            frame.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
            frame.setSize(getPreferredSize());

            frame.add(this);
            calculateTileSize();

            frame.setVisible(true);
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());
        // Draw the tiles
        for (int row = 0; row < environment.getHeight(); row++) {
            for (int col = 0; col < environment.getWidth(); col++) {

                boolean isEven = (row + col) % 2 == 0;
                g.setColor(isEven ? Color.LIGHT_GRAY : Color.DARK_GRAY);

                // Calculate tile position
                int x = col * tileSize;
                int y = row * tileSize;

                // Draw the tile
                g.fillRect(x, y, tileSize, tileSize);

                // Draw grid lines for better visualization (?)
                g.setColor(Color.BLACK);
                g.drawRect(x, y, tileSize, tileSize);
            }
        }
    }

    @Override
    public Dimension getPreferredSize() {
        // Set the preferred size of the panel to fit the tiles
        return new Dimension(500, 500);
    }

    protected void calculateTileSize() {
        int width = getWidth();
        int height = getHeight();

        tileSize = Math.min(width / environment.getWidth(), height / environment.getHeight());
    }
}