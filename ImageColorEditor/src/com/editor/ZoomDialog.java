package com.editor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

public class ZoomDialog extends JDialog implements ActionListener {
    private JLabel jLabel;
    private JButton plusBtn, minusBtn;
    private ResizePanel resizePanel;
    private BufferedImage bufferedImage;

    public ZoomDialog(JFrame parent) {
        super(parent, true);
        setSize(500, 500);
        setLayout(null);
        setResizable(false);
        setLocationRelativeTo(null);
        jLabel = new JLabel();
        plusBtn = new JButton("+");
        minusBtn = new JButton("-");
        jLabel.setHorizontalAlignment(JLabel.CENTER);
        jLabel.setVerticalAlignment(JLabel.CENTER);
        add(plusBtn).setBounds(200, 10, 50, 30);
        add(minusBtn).setBounds(260, 10, 50, 30);
        add(jLabel).setBounds(0, 50, 500, 450);

        plusBtn.addActionListener(this);
        minusBtn.addActionListener(this);
    }

    public void showZoom(ResizePanel resizePanel, BufferedImage image) {
        plusBtn.setEnabled(true);
        minusBtn.setEnabled(false);
        this.resizePanel = resizePanel;
        bufferedImage = image;
        jLabel.setIcon(new ImageIcon(image));

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        BufferedImage image = (BufferedImage) ((ImageIcon) jLabel.getIcon())
                .getImage();
        if (e.getActionCommand().equals("+")) {
            if (!minusBtn.isEnabled()) {
                minusBtn.setEnabled(true);
            }
            if (image.getWidth() >= jLabel.getWidth()
                    || image.getHeight() >= jLabel.getHeight()) {
                plusBtn.setEnabled(false);
            } else {
                image = resizePanel.getScaledInstance(bufferedImage,
                        image.getWidth() + 10, image.getHeight() + 10,
                        RenderingHints.VALUE_INTERPOLATION_BICUBIC, true);
            }
        } else if (e.getActionCommand().equals("-")) {
            if (!plusBtn.isEnabled()) {
                plusBtn.setEnabled(true);
            }
            if (image.getWidth() <= bufferedImage.getWidth()
                    || image.getHeight() <= bufferedImage.getHeight()) {
                minusBtn.setEnabled(false);
            } else {
                image = resizePanel.getScaledInstance(bufferedImage,
                        image.getWidth() - 10, image.getHeight() - 10,
                        RenderingHints.VALUE_INTERPOLATION_BICUBIC, true);
            }
        }
        jLabel.setIcon(new ImageIcon(image));
    }
}
