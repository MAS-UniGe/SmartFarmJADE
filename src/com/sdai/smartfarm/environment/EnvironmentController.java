package com.sdai.smartfarm.environment;

import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import com.sdai.smartfarm.settings.SimulationSettings;

public class EnvironmentController {

    private static SimulationSettings settings = SimulationSettings.defaultSimulationSettings();

    public static void setSimulationSettings(SimulationSettings settings) {
        EnvironmentController.settings = settings;
    }

    private final Environment environment;
    private final EnvironmentViewer environmentViewer;

    private Point mouseStart = null;
    private boolean running = false;

    public EnvironmentController(
        Environment environment
    ) {

        this.environment = environment;

        environmentViewer = new EnvironmentViewer(environment);

    }

    public void stop() {
        running = false;
    }

    public void init() {

        setupEnvironmentViewer();

        environmentViewer.createWindow();

    }

    public void start() {

        running = true;

        long now = System.nanoTime();
        long lastRender = now;
        long lastUpdate = now;

        final long optimalUpdateTime = 1_000_000_000 / settings.targetUPS();
        final long optimalRenderTime = 1_000_000_000 / settings.targetFPS();
        
        while(running) {
            now = System.nanoTime();

            if (now - lastUpdate >= optimalUpdateTime) {

                lastUpdate = now;
                environment.update();
            }

            if (now - lastRender >= optimalRenderTime) {

                lastRender = now;
                environmentViewer.repaint();
            }

        }
        
    }

    private void setupEnvironmentViewer() {

        environmentViewer.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                environmentViewer.recomputeTileSize();
                environmentViewer.repaint();
            }
        });

        environmentViewer.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                mouseStart = e.getPoint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                mouseStart = null;
            }
        });

        environmentViewer.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {

                Point currentPoint = e.getPoint();

                if(mouseStart == null) {
                    mouseStart = currentPoint;
                    return;
                }

                int tileSize = environmentViewer.getTileSize();
                    
                float distanceX = -(currentPoint.x - mouseStart.x) * settings.mouseSensitivity() / tileSize;
                float distanceY = -(currentPoint.y - mouseStart.y) * settings.mouseSensitivity() / tileSize;

                mouseStart = currentPoint;

                environmentViewer.setWorldX(environmentViewer.getWorldX() + distanceX);
                environmentViewer.setWorldY(environmentViewer.getWorldY() + distanceY);

                environmentViewer.repaint();

            }
        });
    }
    
}
