package org.example;

import org.example.ui.ImagePreviewForm;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

                    JFrame frame = new JFrame("ASCII Converter");
                    frame.setContentPane(new ImagePreviewForm().getContentPane());
                    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    frame.pack();
                    frame.setVisible(true);
                }
        );
    }
}