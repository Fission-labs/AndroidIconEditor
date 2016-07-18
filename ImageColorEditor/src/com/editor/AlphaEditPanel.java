package com.editor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.ArrayList;

public class AlphaEditPanel extends JPanel implements ActionListener, Const {

    JRadioButton drawBox, drawLine, selectColor;
    ButtonGroup buttonGroup;
    JComboBox<String> shapeColorJCB, shapeTypeJCB, lineColorJCB;
    DrawingJLabel drawingJLabel;
    private BufferedImage image;
    JButton applyBtn, clearBtn;

    public AlphaEditPanel(BufferedImage image) {
        setLayout(null);
        this.image = image;
        init();
        addComponents();
    }

    private void init() {
        drawingJLabel = new DrawingJLabel(image, this);
        applyBtn = new JButton("Apply");
        clearBtn = new JButton("Clear");
        drawBox = new JRadioButton("Draw Shape");
        drawLine = new JRadioButton("Draw Line");
        selectColor = new JRadioButton("Select Color");
        buttonGroup = new ButtonGroup();
        drawBox.setSelected(true);
        buttonGroup.add(drawBox);
        buttonGroup.add(drawLine);
        buttonGroup.add(selectColor);
        shapeColorJCB = new com.intellij.openapi.ui.ComboBox();
        shapeTypeJCB = new com.intellij.openapi.ui.ComboBox();
        lineColorJCB = new com.intellij.openapi.ui.ComboBox();

        lineColorJCB.setEnabled(false);

        shapeColorJCB.addItem(RED_COLOR);
        shapeColorJCB.addItem(GREEN_COLOR);
        shapeColorJCB.addItem(BLUE_COLOR);

        shapeTypeJCB.addItem(RECTANGLE);
        shapeTypeJCB.addItem(ELLIPSE);

        lineColorJCB.addItem(RED_COLOR);
        lineColorJCB.addItem(GREEN_COLOR);
        lineColorJCB.addItem(BLUE_COLOR);

        shapeColorJCB.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                if (shapeColorJCB.getSelectedItem().toString()
                        .equalsIgnoreCase(RED_COLOR)) {
                    drawingJLabel.setColor(Color.RED);
                } else if (shapeColorJCB.getSelectedItem().toString()
                        .equalsIgnoreCase(BLUE_COLOR)) {
                    drawingJLabel.setColor(Color.BLUE);
                } else if (shapeColorJCB.getSelectedItem().toString()
                        .equalsIgnoreCase(GREEN_COLOR)) {
                    drawingJLabel.setColor(Color.GREEN);
                }
            }
        });

        shapeTypeJCB.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                drawingJLabel.setShape(shapeTypeJCB.getSelectedItem()
                        .toString());
            }
        });

        lineColorJCB.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                if (lineColorJCB.getSelectedItem().toString()
                        .equalsIgnoreCase(RED_COLOR)) {
                    drawingJLabel.setColor(Color.RED);
                } else if (lineColorJCB.getSelectedItem().toString()
                        .equalsIgnoreCase(BLUE_COLOR)) {
                    drawingJLabel.setColor(Color.BLUE);
                } else if (lineColorJCB.getSelectedItem().toString()
                        .equalsIgnoreCase(GREEN_COLOR)) {
                    drawingJLabel.setColor(Color.GREEN);
                }
            }
        });

        drawBox.addActionListener(this);
        drawLine.addActionListener(this);
        selectColor.addActionListener(this);

        applyBtn.addActionListener(this);
        clearBtn.addActionListener(this);

    }

    private void addComponents() {
        add(drawBox).setBounds(10, 20, 100, 30);
        add(shapeTypeJCB).setBounds(30, 60, 80, 30);
        add(shapeColorJCB).setBounds(120, 60, 80, 30);
        add(drawLine).setBounds(10, 100, 100, 30);
        add(lineColorJCB).setBounds(30, 140, 80, 30);
        add(selectColor).setBounds(10, 180, 100, 30);

        add(drawingJLabel).setBounds(400, 60, image.getWidth(),
                image.getHeight());
        add(clearBtn).setBounds(530, image.getHeight() + 120, 70, 30);
        add(applyBtn).setBounds(610, image.getHeight() + 120, 70, 30);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equalsIgnoreCase("Apply")) {
            Shape shape = drawingJLabel.getShape();
            if (shape != null) {
                removeAlpha(shape, null);
            }
        } else if (e.getActionCommand().equalsIgnoreCase("Clear")) {
            drawingJLabel.clearDrawing();
        } else if (e.getActionCommand().equalsIgnoreCase("Draw Shape")) {
            lineColorJCB.setEnabled(false);
            shapeColorJCB.setEnabled(true);
            shapeTypeJCB.setEnabled(true);

            if (shapeColorJCB.getSelectedItem().toString()
                    .equalsIgnoreCase(RED_COLOR)) {
                drawingJLabel.setColor(Color.RED);
            } else if (shapeColorJCB.getSelectedItem().toString()
                    .equalsIgnoreCase(BLUE_COLOR)) {
                drawingJLabel.setColor(Color.BLUE);
            } else if (shapeColorJCB.getSelectedItem().toString()
                    .equalsIgnoreCase(GREEN_COLOR)) {
                drawingJLabel.setColor(Color.GREEN);
            }


            drawingJLabel.setShape(shapeTypeJCB.getSelectedItem().toString());
        } else if (e.getActionCommand().equalsIgnoreCase("Draw Line")) {
            lineColorJCB.setEnabled(true);
            shapeColorJCB.setEnabled(false);
            shapeTypeJCB.setEnabled(false);

            if (lineColorJCB.getSelectedItem().toString()
                    .equalsIgnoreCase(RED_COLOR)) {
                drawingJLabel.setColor(Color.RED);
            } else if (lineColorJCB.getSelectedItem().toString()
                    .equalsIgnoreCase(BLUE_COLOR)) {
                drawingJLabel.setColor(Color.BLUE);
            } else if (lineColorJCB.getSelectedItem().toString()
                    .equalsIgnoreCase(GREEN_COLOR)) {
                drawingJLabel.setColor(Color.GREEN);
            }

            drawingJLabel.setShape(LINE);
        } else if (e.getActionCommand().equalsIgnoreCase("Select Color")) {
            drawingJLabel.clearDrawing();
            lineColorJCB.setEnabled(false);
            shapeColorJCB.setEnabled(false);
            shapeTypeJCB.setEnabled(false);

            drawingJLabel.setShape("COLOR");
        }
    }

    public void removeAlpha(Shape shape, Color selectedPixelColor) {
        int width = image.getWidth();
        int height = image.getHeight();

        WritableRaster raster = image.getRaster();

        Point2D.Float point = null;

        if (selectedPixelColor != null) {

            for (int xx = 0; xx < width; xx++) {
                for (int yy = 0; yy < height; yy++) {
                    Color currentPixelColor = new Color(image.getRGB(xx, yy),
                            true);
                    if ((currentPixelColor.getRed() == selectedPixelColor
                            .getRed())
                            && (currentPixelColor.getBlue() == selectedPixelColor
                            .getBlue())
                            && (currentPixelColor.getGreen() == selectedPixelColor
                            .getGreen())) {
                        int[] pixels = raster.getPixel(xx, yy, (int[]) null);
                        pixels[0] = 0;
                        pixels[1] = 0;
                        pixels[2] = 0;
                        pixels[3] = 0;
                        raster.setPixel(xx, yy, pixels);
                    }
                }
            }

        } else if (shape instanceof Path2D.Float) {
            ArrayList<Point2D.Float> arraylist = new ArrayList<Point2D.Float>();
            PathIterator pi = shape.getPathIterator(null);
            float coords[] = new float[6];
            while (!pi.isDone()) {
                pi.currentSegment(coords);
                float x = coords[0];
                float y = coords[1];
                point = new Point2D.Float(x, y);
                arraylist.add(point);
                pi.next();
            }
            for (int xx = 0; xx < width; xx++) {
                for (int yy = 0; yy < height; yy++) {
                    point = new Point2D.Float(xx, yy);
                    if (arraylist.contains(point)) {
                        int[] pixels = raster.getPixel(xx, yy, (int[]) null);
                        pixels[0] = 0;
                        pixels[1] = 0;
                        pixels[2] = 0;
                        pixels[3] = 0;
                        raster.setPixel(xx, yy, pixels);
                    }
                }
            }
        } else {
            for (int xx = 0; xx < width; xx++) {
                for (int yy = 0; yy < height; yy++) {
                    point = new Point2D.Float(xx, yy);
                    if (shape.contains(point)) {
                        int[] pixels = raster.getPixel(xx, yy, (int[]) null);
                        pixels[0] = 0;
                        pixels[1] = 0;
                        pixels[2] = 0;
                        pixels[3] = 0;
                        raster.setPixel(xx, yy, pixels);
                    }
                }
            }
        }

        drawingJLabel.setIcon(new ImageIcon(image));
        drawingJLabel.clearDrawing();
    }
}
