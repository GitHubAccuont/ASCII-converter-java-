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

    private float fontSize = 10f;
    private boolean copyToClipboard = false;

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
    private JLabel contrastValueDisplay;
    private JLabel gammaValueDisplay;
    private JCheckBox clipboardOptionCheckbox;
    private JLabel imageSizeLabel;

    private final SimpleConverter converter;
    private BufferedImage image;

    public ImagePreviewForm() {
        buildUI();
        attachFileSelectionHandler();
        attachProcessHandler();
        attachUiControls();
        attachPreviewControls();

        outputText.setFont(new Font("Monospaced", Font.PLAIN, 12));
        converter = new SimpleConverter();
    }

    private void attachPreviewControls() {
        outputText.addMouseWheelListener(e -> {
            if (e.isControlDown()) {
                if (e.getWheelRotation() != 0) {
                    if (e.getWheelRotation() < 0) {
                        fontSize += 0.1f;
                    } else {
                        fontSize -= 0.1f;
                    }
                    fontSize = Math.max(Math.min(fontSize, 40.0f), 1.0f);
                    outputText.setFont(outputText.getFont().deriveFont(fontSize));
                }

            }
        });
    }

    private void attachUiControls() {

        contrastSlider.addChangeListener(e -> {
            contrastValueDisplay
                    .setText(
                            String.format("%.2f", ((double) contrastSlider.getValue() / 100)));
        });
        gammaSlider.addChangeListener(e -> {
            gammaValueDisplay
                    .setText(
                            String.format("%.2f", ((double) gammaSlider.getValue() / 100)));
        });
        clipboardOptionCheckbox.addChangeListener(e -> {
            copyToClipboard = clipboardOptionCheckbox.isSelected();
        });
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

                File file = chooser.getSelectedFile();
                String name = file.getName();
                if (name.length() > 30) {
                    name = name.substring(0, 27) + "...";
                }

                try {
                    image = ImageIO.read(file);
                    selectedFileLabel.setText("Selected: " + name);
                    imageSizeLabel.setText("Current size:" + image.getWidth() + "x" + image.getHeight() + "px");
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(null, "Could not read the image file.");

                }
            } else if (result == JFileChooser.CANCEL_OPTION) {
                JOptionPane.showMessageDialog(null, "No correct file was selected");
            }
        });
    }

    private void attachProcessHandler() {
        processButton.addActionListener(e -> {

            if (image == null) {
                JOptionPane.showMessageDialog(null, "Cannot open selected file");
                return;
            }

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
            // Add result to clipboard immidiately
            if (copyToClipboard) {
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(new StringSelection(result), null);
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

    private void buildUI() {
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setPreferredSize(new Dimension(600, 400));

        // Center: scrollable output
        outputText = new JTextArea();
        outputText.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputText);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // East: right-hand column
        JPanel rightColumn = new JPanel();
        rightColumn.setLayout(new BoxLayout(rightColumn, BoxLayout.Y_AXIS));
        mainPanel.add(rightColumn, BorderLayout.EAST);

        rightColumn.add(buildFileSection());
        rightColumn.add(buildSizeSection());
        rightColumn.add(buildSettingsSection());
    }

    private JPanel buildFileSection() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));

        JLabel heading = new JLabel("Selected file:");
        heading.setFont(heading.getFont().deriveFont(16f));
        heading.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(heading);

        selectedFileLabel = new JLabel("None");
        selectedFileLabel.setFont(selectedFileLabel.getFont().deriveFont(Font.ITALIC));
        selectedFileLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(selectedFileLabel);

        imageSizeLabel = new JLabel("Current size: none");
        imageSizeLabel.setToolTipText("The final metrics of converted image cannot be above its initial");
        imageSizeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(imageSizeLabel);

        fileSelectionButton = new JButton("Select File");
        fileSelectionButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(fileSelectionButton);

        return panel;
    }

    private JPanel buildSizeSection() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));

        JLabel heading = new JLabel("Final Width and Height");
        heading.setFont(heading.getFont().deriveFont(16f));
        heading.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(heading);

        widthField = new JTextField("80");
        widthField.setToolTipText("Width");
        widthField.setMaximumSize(new Dimension(80, widthField.getPreferredSize().height));
        widthField.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(widthField);

        heightField = new JTextField("40");
        heightField.setToolTipText("Height");
        heightField.setMaximumSize(new Dimension(80, heightField.getPreferredSize().height));
        heightField.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(heightField);

        return panel;
    }

    private JPanel buildSettingsSection() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));

        JLabel heading = new JLabel("Other settings");
        heading.setFont(heading.getFont().deriveFont(16f));
        heading.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(heading);

        clipboardOptionCheckbox = new JCheckBox("Copy to Clipboard");
        clipboardOptionCheckbox.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(clipboardOptionCheckbox);

        reverseCheckbox = new JCheckBox("Reverse Palette");
        reverseCheckbox.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(reverseCheckbox);

        JLabel gammaLabel = new JLabel("Gamma adjustment");
        gammaLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(gammaLabel);

        gammaSlider = new JSlider(30, 250, 100);
        gammaSlider.setToolTipText("Use to set the Gamma adjustment");
        gammaSlider.setAlignmentX(Component.LEFT_ALIGNMENT);
        gammaSlider.setMaximumSize(new Dimension(Integer.MAX_VALUE, gammaSlider.getPreferredSize().height));
        panel.add(gammaSlider);

        gammaValueDisplay = new JLabel("1.00");
        gammaValueDisplay.setFont(gammaValueDisplay.getFont().deriveFont(10f));
        gammaValueDisplay.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(gammaValueDisplay);

        JLabel contrastLabel = new JLabel("Contrast adjustment");
        contrastLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(contrastLabel);

        contrastSlider = new JSlider(30, 250, 100);
        contrastSlider.setToolTipText("Use to set the Contrast adjustment");
        contrastSlider.setAlignmentX(Component.LEFT_ALIGNMENT);
        contrastSlider.setMaximumSize(new Dimension(Integer.MAX_VALUE, contrastSlider.getPreferredSize().height));
        panel.add(contrastSlider);

        contrastValueDisplay = new JLabel("1.00");
        contrastValueDisplay.setFont(contrastValueDisplay.getFont().deriveFont(10f));
        contrastValueDisplay.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(contrastValueDisplay);

        processButton = new JButton("Process");
        processButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        processButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, processButton.getPreferredSize().height));
        panel.add(processButton);

        return panel;
    }
}
