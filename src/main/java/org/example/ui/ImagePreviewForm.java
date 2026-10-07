package org.example.ui;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import org.example.conversion.SimpleConverter;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Locale;


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
    private File file;

    public ImagePreviewForm() {
        $$$setupUI$$$();
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
                    fontSize = Math.clamp(fontSize, 1f, 40f);
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

                file = chooser.getSelectedFile();
                String name = file.getName();
                if (name.length() > 30) {
                    name = name.substring(0, 27) + "...";
                }
                selectedFileLabel.setText("Selected: " + name);
            } else if (result == JFileChooser.CANCEL_OPTION) {
                JOptionPane.showMessageDialog(null, "No correct file was selected");
            }
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
                imageSizeLabel.setText("Current size:" + image.getWidth() + "x" + image.getHeight() + "px");

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
                if (copyToClipboard) {
                    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                    clipboard.setContents(new StringSelection(result), null);
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "Could not read the image file.");

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

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayoutManager(1, 2, new Insets(5, 5, 5, 5), -1, -1));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(3, 1, new Insets(0, 0, 0, 0), -1, -1));
        mainPanel.add(panel1, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(200, -1), new Dimension(250, -1), null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(10, 1, new Insets(0, 0, 0, 5), -1, -1));
        panel1.add(panel2, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_HORIZONTAL, 1, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(150, 100), null, 0, false));
        processButton = new JButton();
        processButton.setText("Process");
        panel2.add(processButton, new GridConstraints(9, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        gammaSlider = new JSlider();
        gammaSlider.setMaximum(250);
        gammaSlider.setMinimum(30);
        gammaSlider.setPaintTicks(false);
        gammaSlider.setSnapToTicks(false);
        gammaSlider.setToolTipText("Use to set  the Gamma adjustment");
        gammaSlider.setValue(100);
        panel2.add(gammaSlider, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        contrastSlider = new JSlider();
        contrastSlider.setMaximum(250);
        contrastSlider.setMinimum(30);
        contrastSlider.setToolTipText("Use to set  the Contrast adjustment");
        contrastSlider.setValue(100);
        contrastSlider.setValueIsAdjusting(false);
        panel2.add(contrastSlider, new GridConstraints(7, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(200, 27), null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("Contrast adjustment");
        panel2.add(label1, new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(120, 20), null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Gamma adjustment");
        panel2.add(label2, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_SOUTHWEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(120, 20), null, 0, false));
        reverseCheckbox = new JCheckBox();
        reverseCheckbox.setText("Reverse Palette");
        panel2.add(reverseCheckbox, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        Font label3Font = this.$$$getFont$$$(null, -1, 16, label3.getFont());
        if (label3Font != null) label3.setFont(label3Font);
        label3.setHorizontalTextPosition(0);
        label3.setText("Other settings");
        panel2.add(label3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        gammaValueDisplay = new JLabel();
        Font gammaValueDisplayFont = this.$$$getFont$$$(null, -1, 10, gammaValueDisplay.getFont());
        if (gammaValueDisplayFont != null) gammaValueDisplay.setFont(gammaValueDisplayFont);
        gammaValueDisplay.setText("1.00");
        panel2.add(gammaValueDisplay, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(20, 10), null, 0, false));
        contrastValueDisplay = new JLabel();
        Font contrastValueDisplayFont = this.$$$getFont$$$(null, -1, 10, contrastValueDisplay.getFont());
        if (contrastValueDisplayFont != null) contrastValueDisplay.setFont(contrastValueDisplayFont);
        contrastValueDisplay.setText("1.00");
        panel2.add(contrastValueDisplay, new GridConstraints(8, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(20, 10), null, 0, false));
        clipboardOptionCheckbox = new JCheckBox();
        clipboardOptionCheckbox.setText("Copy to Clipboard");
        panel2.add(clipboardOptionCheckbox, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(3, 1, new Insets(0, 0, 0, 5), -1, -1));
        panel1.add(panel3, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_HORIZONTAL, 1, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        widthField = new JTextField();
        widthField.setText("80");
        widthField.setToolTipText("Width");
        panel3.add(widthField, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, new Dimension(80, -1), new Dimension(80, -1), null, 0, false));
        final JLabel label4 = new JLabel();
        Font label4Font = this.$$$getFont$$$(null, -1, 16, label4.getFont());
        if (label4Font != null) label4.setFont(label4Font);
        label4.setHorizontalTextPosition(0);
        label4.setText("Final Width and Heght");
        panel3.add(label4, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        heightField = new JTextField();
        heightField.setText("40");
        heightField.setToolTipText("Height");
        panel3.add(heightField, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, new Dimension(80, -1), new Dimension(80, -1), null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridLayoutManager(4, 1, new Insets(0, 0, 0, 5), -1, -1));
        panel1.add(panel4, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_HORIZONTAL, 1, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label5 = new JLabel();
        Font label5Font = this.$$$getFont$$$(null, -1, 16, label5.getFont());
        if (label5Font != null) label5.setFont(label5Font);
        label5.setText("Selected file:");
        panel4.add(label5, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        selectedFileLabel = new JLabel();
        Font selectedFileLabelFont = this.$$$getFont$$$(null, Font.ITALIC, -1, selectedFileLabel.getFont());
        if (selectedFileLabelFont != null) selectedFileLabel.setFont(selectedFileLabelFont);
        selectedFileLabel.setText("None");
        panel4.add(selectedFileLabel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        fileSelectionButton = new JButton();
        fileSelectionButton.setText("Select File");
        panel4.add(fileSelectionButton, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        imageSizeLabel = new JLabel();
        imageSizeLabel.setText("Current size: none");
        imageSizeLabel.setToolTipText("The final metrics of converted image cannot be above its initial");
        panel4.add(imageSizeLabel, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JScrollPane scrollPane1 = new JScrollPane();
        mainPanel.add(scrollPane1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(350, -1), null, 0, false));
        outputText = new JTextArea();
        outputText.setText("");
        scrollPane1.setViewportView(outputText);
    }

    /**
     * @noinspection ALL
     */
    private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
        if (currentFont == null) return null;
        String resultName;
        if (fontName == null) {
            resultName = currentFont.getName();
        } else {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
                resultName = fontName;
            } else {
                resultName = currentFont.getName();
            }
        }
        Font font = new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
        boolean isMac = System.getProperty("os.name", "").toLowerCase(Locale.ENGLISH).startsWith("mac");
        Font fontWithFallback = isMac ? new Font(font.getFamily(), font.getStyle(), font.getSize()) : new StyleContext().getFont(font.getFamily(), font.getStyle(), font.getSize());
        return fontWithFallback instanceof FontUIResource ? fontWithFallback : new FontUIResource(fontWithFallback);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }
}
