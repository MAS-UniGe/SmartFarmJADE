package com.sdai.smartfarm.environment;

import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

public class EnvironmentController {

    private static final float MOUSE_SENSITIVITY = 1.0f;

    private static final int TARGET_FPS = 20;

    private static final int TARGET_UPS = 80;
    
    private static final long OPTIMAL_RENDER_TIME = 1_000_000_000 / TARGET_FPS;

    private static final long OPTIMAL_UPDATE_TIME = 1_000_000_000 / TARGET_UPS;

    private final Environment environment;
    private final EnvironmentViewer environmentViewer;

    private Point mouseStart = null;
    private boolean running = false;

    public EnvironmentController(Environment environment) {

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

        while(running) {
            now = System.nanoTime();

            if (now - lastUpdate >= OPTIMAL_UPDATE_TIME) {

                lastUpdate = now;
                environment.update();
            }

            if (now - lastRender >= OPTIMAL_RENDER_TIME) {

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
                    
                float distanceX = -(currentPoint.x - mouseStart.x) * MOUSE_SENSITIVITY / tileSize;
                float distanceY = -(currentPoint.y - mouseStart.y) * MOUSE_SENSITIVITY / tileSize;

                mouseStart = currentPoint;

                environmentViewer.setWorldX(environmentViewer.getWorldX() + distanceX);
                environmentViewer.setWorldY(environmentViewer.getWorldY() + distanceY);

                environmentViewer.repaint();

            }
        });
    }
    
}
