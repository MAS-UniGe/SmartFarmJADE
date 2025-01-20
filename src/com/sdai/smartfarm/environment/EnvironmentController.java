package com.sdai.smartfarm.environment;

import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

public class EnvironmentController {

    private final Environment environment;

    private final EnvironmentViewer environmentViewer;

    private static final float MOUSE_SENSITIVITY = 1.0f;

    private Point mouseStart = null;

    public EnvironmentController(Environment environment) {

        this.environment = environment;

        environmentViewer = new EnvironmentViewer(environment);

    }

    public void init() {

        setupEnvironmentViewer();

        environmentViewer.renderEnvironment();
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
                
                System.out.println(
                      String.valueOf(environmentViewer.getWorldX()) 
                    + "; " 
                    + String.valueOf(environmentViewer.getWorldY())
                );
                System.out.println("");

            }
        });
    }
    
}
