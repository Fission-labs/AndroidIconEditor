package com.editor;

import com.intellij.util.ui.UIUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ResizePanel extends JPanel {
    public JSpinner widhtJS, heightJS;

    public ResizePanel(int initial_width, int initial_height) {
        /*Border border = BorderFactory.createRaisedBevelBorder();
        setBorder(border);*/
        setLayout(null);
        JLabel widthJL = new JLabel("Width");
        JLabel heightJL = new JLabel("Height");
        SpinnerModel spinnerModelWidth = new SpinnerNumberModel(initial_width, 10, initial_width + 20, 1);
        SpinnerModel spinnerModelHeight = new SpinnerNumberModel(initial_height, 10, initial_height + 20, 1);
        widhtJS = new JSpinner(spinnerModelWidth);
        heightJS = new JSpinner(spinnerModelHeight);

        add(widthJL).setBounds(70, 30, 80, 30);
        add(widhtJS).setBounds(150, 30, 80, 30);
        add(heightJL).setBounds(70, 70, 80, 30);
        add(heightJS).setBounds(150, 70, 80, 30);

    }

    public BufferedImage getScaledInstance(BufferedImage img,
                                           int targetWidth,
                                           int targetHeight,
                                           Object hint,
                                           boolean higherQuality) {
        int type = (img.getTransparency() == Transparency.OPAQUE) ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
        BufferedImage ret = (BufferedImage) img;
        int w, h;
        if (higherQuality) {
// Use multi-step technique: start with original size, then
// scale down in multiple passes with drawImage()
// until the target size is reached
            w = img.getWidth();
            h = img.getHeight();
        } else {
// Use one-step technique: scale directly from original
// size to target size with a single drawImage() call
            w = targetWidth;
            h = targetHeight;
        }

        do {
            if (higherQuality && w < targetWidth) {
                w *= 2;
                if (w > targetWidth) {
                    w = targetWidth;
                }
            } else {
                if (w > targetWidth) {
                    w /= 2;
                    if (w < targetWidth) {
                        w = targetWidth;
                    }
                }
            }

            if (higherQuality && h < targetHeight) {
                h *= 2;
                if (h > targetHeight) {
                    h = targetHeight;
                }
            } else {
                if (h > targetHeight) {
                    h /= 2;
                    if (h < targetHeight) {
                        h = targetHeight;
                    }
                }
            }


            BufferedImage tmp = UIUtil.createImage(w, h, type);
            Graphics2D g2 = tmp.createGraphics();
            //g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, hint);
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.drawImage(ret, 0, 0, w, h, null);
            g2.dispose();

            ret = tmp;
        } while (w != targetWidth || h != targetHeight);

        return ret;
    }

}
