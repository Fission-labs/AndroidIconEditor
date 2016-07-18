package com.editor;

import com.intellij.util.ui.UIUtil;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.dnd.DropTarget;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

public class MainUi extends JFrame implements ActionListener {

    private final static String TITLE = "Android Icon Editor";
    //make reference for Main class
    private static MainUi m;
    public BufferedImage originalImage, copyImage;
    int image_Xpos, image_Ypos;
    private JTextField resPathJTF, toColorCodeJTF, fromColorCodeJTF;
    private JLabel imagePreviewJL;
    private JLabel colorPreviewBox;
    private JButton openColorChooserBtn;
    private JButton resetBtn;
    private JColorChooser jColorChooser;
    private boolean isColorChooserVisible = false;
    private int red, green, blue;

    public MainUi() {
        m = this;
        setLayout(null);
        setVisible(true);
        setSize(900, 500);
        setTitle(TITLE);
        setResizable(false);
        //setLocation(GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().width - this.getWidth(), 100);
        setLocationRelativeTo(null);
        // creating border
        Border border = BorderFactory.createEtchedBorder();

        JMenu fileMenu = new JMenu("File");
        JMenu editMenu = new JMenu("Edit");
        JMenuBar menuBar = new JMenuBar();
        String[] fileItems = {"Open", "Save"};
        for (String fileItem : fileItems) {
            JMenuItem item = new JMenuItem(fileItem);
            item.addActionListener(this);
            fileMenu.add(item);
        }
        String[] editItems = {"Remove Alpha", "Resize"};
        for (String editItem : editItems) {
            JMenuItem item = new JMenuItem(editItem);
            item.addActionListener(this);
            editMenu.add(item);
        }

        menuBar.add(fileMenu);
        menuBar.add(editMenu);

        jColorChooser = new JColorChooser();
        resPathJTF = new JTextField();
        toColorCodeJTF = new JTextField();
        toColorCodeJTF.setFont(new Font("Times New Roman", Font.BOLD, 16));
        toColorCodeJTF.setToolTipText("Enter 7 Charcters Color HashCode or Select Color from ColorPicker");
        fromColorCodeJTF = new JTextField();
        fromColorCodeJTF.setFont(new Font("Times New Roman", Font.BOLD, 16));
        fromColorCodeJTF.setToolTipText("Click on any part of the image which you want to apply color changes");

        JLabel resPathJL = new JLabel("Browse your image file here");
        JLabel toColorCodeJL = new JLabel("Enter Color Code");
        JLabel fromColorCodeJL = new JLabel("Pick Color From Image");
        colorPreviewBox = new JLabel();
        imagePreviewJL = new JLabel();
        JButton addBtn = new JButton("Add");
        openColorChooserBtn = new JButton("Show Color Picker");
        resetBtn = new JButton("Reset");

        colorPreviewBox.setOpaque(true);
        imagePreviewJL.setBorder(border);
        imagePreviewJL.setHorizontalAlignment(JLabel.CENTER);
        imagePreviewJL.setVerticalAlignment(JLabel.CENTER);

        add(resPathJL).setBounds(20, 50, 170, 30);
        add(resPathJTF).setBounds(190, 50, 150, 30);
        add(addBtn).setBounds(350, 50, 100, 30);

        add(fromColorCodeJL).setBounds(20, 90, 170, 30);
        add(fromColorCodeJTF).setBounds(190, 90, 150, 30);
        add(colorPreviewBox).setBounds(350, 90, 30, 30);
        add(toColorCodeJL).setBounds(20, 130, 170, 30);
        add(toColorCodeJTF).setBounds(190, 130, 150, 30);
        add(openColorChooserBtn).setBounds(20, 170, 160, 30);
        add(resetBtn).setBounds(190, 170, 160, 30);

        add(imagePreviewJL).setBounds(600, 0, 300, 500);

        setJMenuBar(menuBar);
        revalidate();

        resPathJTF.setEditable(false);
        fromColorCodeJTF.setEditable(false);
        toColorCodeJTF.setEnabled(false);
        openColorChooserBtn.setEnabled(false);
        resetBtn.setEnabled(false);

        //register listener
        addBtn.addActionListener(this);
        openColorChooserBtn.addActionListener(this);
        resetBtn.addActionListener(this);
        toColorCodeJTF.setActionCommand("ColorCode");
        toColorCodeJTF.addActionListener(this);

        setDragAndDropListener();
        setMouseListener();
        setResDocumentListener();
        setKeyListener();
        setColorChooserListener();

        setColorChooserDialog();

    }

    public static MainUi getInstance() {
        return m;
    }

    // main() method
    public static void main(String args[]) {
        m = new MainUi();
    }

//		################   Listeners ##############################

    // selected color apply to image
    private void changeColor(Color colorFromChooser, String colorCode) {
        Color c;
        if (colorCode != null) {
            c = Color.decode(colorCode);
        } else {
            c = colorFromChooser;
        }
        //We should pass this value to SearchAndSave class, to apply same color to other files

        int width = originalImage.getWidth();
        int height = originalImage.getHeight();
        copyImage = UIUtil.createImage(width, height, BufferedImage.TYPE_INT_ARGB);
        copyImage.setData(originalImage.getData());
        WritableRaster raster = copyImage.getRaster();

        for (int xx = 0; xx < width; xx++) {
            for (int yy = 0; yy < height; yy++) {
                Color originalColor = new Color(originalImage.getRGB(xx, yy), true);
                int[] pixels = raster.getPixel(xx, yy, (int[]) null);
                pixels[0] = c.getRed();
                pixels[1] = c.getGreen();
                pixels[2] = c.getBlue();
                pixels[3] = (originalColor.getAlpha() * c.getAlpha()) / 255;
                if ((originalColor.getRed() == red) && (originalColor.getBlue() == blue) && (originalColor.getGreen() == green)) {
                    raster.setPixel(xx, yy, pixels);
                }

            }
        }
        imagePreviewJL.setIcon(new ImageIcon(copyImage));
    }

    //remove all tabs from color chooser except RGB
    private void setColorChooserDialog() {
        jColorChooser.setPreviewPanel(new JPanel());
        AbstractColorChooserPanel[] panels = jColorChooser.getChooserPanels();
        for (AbstractColorChooserPanel p : panels) {
            String displayName = p.getDisplayName();
            switch (displayName.toUpperCase()) {
                case "HSV":
                    jColorChooser.removeChooserPanel(p);
                    break;
                case "HSL":
                    jColorChooser.removeChooserPanel(p);
                    break;
                case "CMYK":
                    jColorChooser.removeChooserPanel(p);
                    break;
                case "SWATCHES":
                    jColorChooser.removeChooserPanel(p);
                    break;
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO Auto-generated method stub
        switch (e.getActionCommand()) {
            case "Add":
            case "Open":
                JFileChooser jFileChooser = new JFileChooser();
                jFileChooser.setFileFilter(new FileNameExtensionFilter("Image files only", "png", "jpg"));
                int result = jFileChooser.showOpenDialog(this);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File file = jFileChooser.getSelectedFile();
                    setImageIcon(file);
                }

                break;
            case "Remove Alpha":

                if (resPathJTF.getText() != null
                        && !resPathJTF.getText().equalsIgnoreCase("")) {

                    Icon icon = imagePreviewJL.getIcon();
                    BufferedImage bi = UIUtil.createImage(icon.getIconWidth(),
                            icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
                    Graphics g = bi.createGraphics();
                    // paint the Icon to the BufferedImage.
                    icon.paintIcon(null, g, 0, 0);
                    g.dispose();

                    AlphaEditPanel alphaEditPanel = new AlphaEditPanel(bi);
                    alphaEditPanel.setPreferredSize(new Dimension(700, 400));

                    int selectedOption1 = JOptionPane
                            .showConfirmDialog(this, alphaEditPanel,
                                    "Remove Alpha", JOptionPane.OK_CANCEL_OPTION,
                                    JOptionPane.PLAIN_MESSAGE);
                    if (selectedOption1 == 0) {
                        resetBtn.setEnabled(true);
                        if (copyImage != null) {
                            copyImage = bi;
                        } else {
                            originalImage = bi;
                        }
                        imagePreviewJL.setIcon(new ImageIcon(bi));
                    }

                } else {
                    JOptionPane.showMessageDialog(null,
                            "You should select image first");
                }

                break;
            case "Resize":
                if (resPathJTF.getText() != null && !resPathJTF.getText().equalsIgnoreCase("")) {
                    resetBtn.setEnabled(true);
                    ResizePanel resizePanel = new ResizePanel(originalImage.getWidth(), originalImage.getHeight());
                    resizePanel.setPreferredSize(new Dimension(300, 150));
                    int selectedOption = JOptionPane.showConfirmDialog(this, resizePanel, "Edit Size", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                    if (selectedOption == 0) {
                        //Apply given size to image
                        if (copyImage != null)
                            originalImage = resizePanel.getScaledInstance(copyImage, (int) resizePanel.widhtJS.getValue(), (int) resizePanel.heightJS.getValue(), RenderingHints.VALUE_INTERPOLATION_BICUBIC, true);
                        else
                            originalImage = resizePanel.getScaledInstance(originalImage, (int) resizePanel.widhtJS.getValue(), (int) resizePanel.heightJS.getValue(), RenderingHints.VALUE_INTERPOLATION_BICUBIC, true);

                        imagePreviewJL.setIcon(new ImageIcon(originalImage));
                        // Reinitialize with new values since image size changed
                        image_Xpos = imagePreviewJL.getWidth() / 2 - originalImage.getWidth() / 2;
                        image_Ypos = imagePreviewJL.getHeight() / 2 - originalImage.getHeight() / 2;
                        copyImage = originalImage;
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "You should select image first");
                }

                break;
            case "Show Color Picker":
            case "Close Color Picker":
                if (isColorChooserVisible) {
                    openColorChooserBtn.setText("Show Color Picker");
                    this.remove(jColorChooser);
                    isColorChooserVisible = false;
                    this.validate();
                    this.repaint();
                } else {
                    openColorChooserBtn.setText("Close Color Picker");
                    this.add(jColorChooser).setBounds(0, 180, 600, 300);
                    isColorChooserVisible = true;
                    this.validate();
                    this.repaint();
                }

                break;
            case "Save":
                if (resPathJTF.getText() != null && !resPathJTF.getText().equalsIgnoreCase("")) {

                    File file = new File(resPathJTF.getText());
                    jFileChooser = new JFileChooser();
                    jFileChooser.setSelectedFile(file);
                    int choose_option = jFileChooser.showSaveDialog(this);

                    if (choose_option == JFileChooser.APPROVE_OPTION) {
                        File file1 = jFileChooser.getSelectedFile();
                        try {
                            if (copyImage != null)
                                ImageIO.write(copyImage, "png", file1);
                            else
                                ImageIO.write(originalImage, "png", file1);
                            JOptionPane.showMessageDialog(this, "File saved successfully");
                        } catch (IOException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                            JOptionPane.showMessageDialog(this, "File not saved", "Warning", JOptionPane.WARNING_MESSAGE);
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "File not saved", "Warning", JOptionPane.WARNING_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "You should select image first");
                }
//               new SearchAndSave(requiredColor, image, file.getName(), file.getParentFile().getName(), file.getParentFile().getParentFile().getPath());
                break;
            case "Reset":
                reSet();
                break;
            case "ColorCode":
                String colorCodeStr = toColorCodeJTF.getText();
                if (colorCodeStr.length() != 7) {
                    return;
                }
                changeColor(null, colorCodeStr);
                break;
            default:
                break;

        }

    }

    private void setDragAndDropListener() {
        DragAndDropListener listener = new DragAndDropListener();
        new DropTarget(imagePreviewJL, listener);
    }

    private void setColorChooserListener() {

        jColorChooser.getSelectionModel().addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                // TODO Auto-generated method stub
                Color c = jColorChooser.getColor();
                String argb = String.format("#%02X%02X%02X%02X", c.getAlpha(), c.getRed(), c.getGreen(), c.getBlue());
                changeColor(c, null);
            }
        });
    }

    private void setMouseListener() {
        imagePreviewJL.addMouseListener(new MouseListener() {

            @Override
            public void mouseReleased(MouseEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void mousePressed(MouseEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void mouseExited(MouseEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void mouseEntered(MouseEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void mouseClicked(MouseEvent e) {
                // TODO Auto-generated method stub
                if ((image_Xpos <= e.getX()) && ((image_Xpos + originalImage.getWidth()) >= e.getX())) {
                    if ((image_Ypos <= e.getY()) && ((image_Ypos + originalImage.getHeight()) >= e.getY())) {
                        // Enable all views
                        setUp();

                        int xpos = Math.abs(image_Xpos - e.getX());
                        int ypos = Math.abs(image_Ypos - e.getY());
                        Color c = new Color(originalImage.getRGB(xpos, ypos), true);
                        red = c.getRed();
                        green = c.getGreen();
                        blue = c.getBlue();
                        String argb = String.format("#%02X%02X%02X", c.getRed(), c.getGreen(), c.getBlue());
                        fromColorCodeJTF.setText(argb);
                        colorPreviewBox.setBackground(c);
                        if (copyImage != null) {
                            originalImage = copyImage;
                        }
                    }
                }
            }
        });
    }

    private void setResDocumentListener() {
        resPathJTF.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void removeUpdate(DocumentEvent arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void insertUpdate(DocumentEvent arg0) {
                // TODO Auto-generated method stub
                reSet();
            }

            @Override
            public void changedUpdate(DocumentEvent arg0) {
                // TODO Auto-generated method stub
            }
        });
    }

    private void setKeyListener() {
        toColorCodeJTF.addKeyListener(new KeyAdapter() {

            public void keyTyped(KeyEvent e) {
                char keyChar = e.getKeyChar();
                if (Character.isLowerCase(keyChar)) {
                    e.setKeyChar(Character.toUpperCase(keyChar));
                }
            }
        });
    }

    private void setUp() {
        toColorCodeJTF.setEnabled(true);
        openColorChooserBtn.setEnabled(true);
        resetBtn.setEnabled(true);
    }

    private void reSet() {
        try {
            copyImage = null;
            originalImage = ImageIO.read(new File(resPathJTF.getText()));
            if (originalImage.getWidth() <= (imagePreviewJL.getWidth() - 20) && originalImage.getHeight() <= (imagePreviewJL.getHeight() - 20)) {
                imagePreviewJL.setIcon(new ImageIcon(originalImage));
                image_Xpos = imagePreviewJL.getWidth() / 2 - originalImage.getWidth() / 2;
                image_Ypos = imagePreviewJL.getHeight() / 2 - originalImage.getHeight() / 2;
            } else {
                JOptionPane.showMessageDialog(this, "Icon size is too large");
            }
            colorPreviewBox.setBackground(null);
            toColorCodeJTF.setText("");
            fromColorCodeJTF.setText("");
            toColorCodeJTF.setEnabled(false);
            if (isColorChooserVisible) {
                openColorChooserBtn.setText("Show Color Picker");
                this.remove(jColorChooser);
                isColorChooserVisible = false;
                this.validate();
                this.repaint();
            }
            openColorChooserBtn.setEnabled(false);
            resetBtn.setEnabled(false);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void setImageIcon(File file) {
        if (file.getAbsoluteFile().toString().contains(".png") ||
                file.getAbsoluteFile().toString().contains(".jpg")) {

            resPathJTF.setText(file.getAbsolutePath());
        } else {
            JOptionPane.showMessageDialog(this, "Selected file is not an image file");
        }
    }

}

	
