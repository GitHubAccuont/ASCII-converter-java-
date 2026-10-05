package org.example.ui;

import org.example.conversion.SimpleConverter;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


public class ImagePreviewForm {


    private JSlider gammaSlider;
    private JSlider contrastSlider;
    private JTextField heightField;
    private JTextField widthField;
    private JLabel selectedFileLabel;
    private JCheckBox reverseCheckbox;
    private JPanel mainPanel;
    private JTextArea outputText;
    private JButton processButton;
    private JButton fileSelectionButton;

    private final SimpleConverter converter;
    private File file;

    public ImagePreviewForm() {
        attachFileSelectionHandler();
        attachProcessHandler();

        outputText.setFont(new Font("Monospaced", Font.PLAIN, 12));
        converter = new SimpleConverter();
    }


    private void attachFileSelectionHandler() {
        fileSelectionButton.addActionListener(e -> {

            JFileChooser chooser = new JFileChooser();
            chooser.setCurrentDirectory(new File(System.getProperty("user.home")));
            chooser.setDialogTitle("Select image to convert into ascii");
            FileFilter imageFilter = new FileNameExtensionFilter("Image files", "png", "jpg", "jpeg", "gif");
            chooser.addChoosableFileFilter(imageFilter);
            chooser.setFileFilter(imageFilter);

            int result = chooser.showOpenDialog(null);

            if (result == JFileChooser.APPROVE_OPTION) {

                file = chooser.getSelectedFile();
                selectedFileLabel.setText("Selected file:" + file.getAbsolutePath());
                return;
            }
            JOptionPane.showMessageDialog(null, "No correct file was selected");
        });
    }

    private void attachProcessHandler() {
        processButton.addActionListener(e -> {
            if (file == null) {
                JOptionPane.showMessageDialog(null, "Cannot open selected file");
                return;
            }
            BufferedImage image;
            try {
                image = ImageIO.read(file);

                if (!syncConverterFromUi()) {

                    JOptionPane.showMessageDialog(null, "Incorrect values used for width and height");
                    return;
                }
                String result = converter.convertToAscii(image);

                if (result == null) {
                    JOptionPane.showMessageDialog(null, "Incorrect final width and height were set");
                    return;
                }

                outputText.setText(result);
                // Add result to clipboard immidiately (remove?)
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(new StringSelection(result), null);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

    }


    private boolean syncConverterFromUi() {
        converter.setGamma((double) gammaSlider.getValue() / 100);
        converter.setContrast((double) contrastSlider.getValue() / 100);
        converter.setInverted(reverseCheckbox.isSelected());
        try {
            converter.setWidth(Integer.parseInt(widthField.getText().trim()));
            converter.setHeight(Integer.parseInt(heightField.getText().trim()));
        } catch (NumberFormatException e) {

            widthField.setText("80");
            heightField.setText("40");
            return false;
        }
        return true;
    }

    public JPanel getContentPane() {
        return mainPanel;
    }
}
