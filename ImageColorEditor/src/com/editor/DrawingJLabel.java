package com.editor;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public class DrawingJLabel extends JLabel implements Const {
    private BufferedImage image;
    private Point startPoint;
    private Color color = Color.RED;
    private Rectangle2D.Float rectangle2DShape;
    private Ellipse2D.Float ellipse2DShape;
    private Path2D.Float path2DShape;
    private Shape currentShape;
    private Shape shape;
    private AlphaEditPanel alphaEditPanel;

    public DrawingJLabel(BufferedImage image, AlphaEditPanel alphaEditPanel) {
        Icon icon = new ImageIcon(image);
        this.setIcon(icon);
        this.image = image;
        this.alphaEditPanel = alphaEditPanel;

        rectangle2DShape = new Rectangle2D.Float();
        ellipse2DShape = new Ellipse2D.Float();
        path2DShape = new Path2D.Float();

        currentShape = rectangle2DShape;

        addMouseListener(new MyMouseListener());
        addMouseMotionListener(new MyMouseListener());
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void clearDrawing() {
        if (shape instanceof Path2D.Float) {
            ((Path2D.Float) shape).closePath();
            ((Path2D.Float) shape).reset();
        }
        shape = null;
        repaint();
    }

    public void setShape(String str) {
        if (str.equalsIgnoreCase(RECTANGLE)) {
            currentShape = rectangle2DShape;
        } else if (str.equalsIgnoreCase(ELLIPSE)) {
            currentShape = ellipse2DShape;
        } else if (str.equalsIgnoreCase(LINE)) {
            currentShape = path2DShape;
        } else if (str.equalsIgnoreCase("COLOR")) {
            currentShape = null;
        }
    }

    public Shape getShape() {
        return shape;
    }

    private class MyMouseListener extends MouseInputAdapter {

        public void mousePressed(MouseEvent e) {
            startPoint = e.getPoint();
            shape = currentShape;
            if (shape == null) {
                Color selectedPixelColor = new Color(image.getRGB(e.getX(),
                        e.getY()), true);
                alphaEditPanel.removeAlpha(null, selectedPixelColor);
            } else if (shape instanceof Path2D.Float) {
                ((Path2D.Float) shape).moveTo(startPoint.x, startPoint.y);
            }
        }

        public void mouseDragged(MouseEvent e) {
            int x = Math.min(startPoint.x, e.getX());
            int y = Math.min(startPoint.y, e.getY());
            int width = Math.abs(startPoint.x - e.getX());
            int height = Math.abs(startPoint.y - e.getY());

            if (shape instanceof Rectangle2D.Float) {
                ((Rectangle2D.Float) shape).setRect(x, y, width, height);
            } else if (shape instanceof Ellipse2D.Float) {
                ((Ellipse2D.Float) shape).setFrame(x, y, width, height);
            } else if (shape instanceof Path2D.Float) {
                ((Path2D.Float) shape).lineTo(e.getX(), e.getY());
                ((Path2D.Float) shape).moveTo(e.getX(), e.getY());
            }
            repaint();
        }

    }

    public void addRectangle(Rectangle rectangle, Color color) {
        // Draw the Rectangle onto the BufferedImage

        Graphics2D g2d = (Graphics2D) image.getGraphics();
        g2d.setColor(color);
        g2d.draw(rectangle);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Paint the Shape as the mouse is being dragged
        if (shape != null) {
            Graphics2D g2d = (Graphics2D) g;
            BasicStroke stroke = new BasicStroke(1, BasicStroke.CAP_ROUND,
                    BasicStroke.JOIN_ROUND);

            g2d.setStroke(stroke);
            g2d.setColor(color);
            g2d.draw(shape);
        }
    }

	/*
     * private BufferedImage getResizedImage(int w, int h, BufferedImage img) {
	 * 
	 * BufferedImage tmp = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
	 * Graphics2D g2 = tmp.createGraphics(); g2.drawImage(img, 0, 0, w, h,
	 * null); g2.dispose(); return tmp;
	 * 
	 * }
	 * 
	 * public void saveImage(String file_path) { BufferedImage image = new
	 * BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
	 * Graphics2D g2 = image.createGraphics(); paint(g2); try {
	 * ImageIO.write(image, "png", new File(file_path + ".png")); } catch
	 * (Exception e) { e.printStackTrace(); } }
	 * 
	 * public void clearDrawingArea() { image = null; repaint(); }
	 */

}
