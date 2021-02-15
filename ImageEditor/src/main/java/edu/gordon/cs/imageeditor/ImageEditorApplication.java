/**
 *  ImageEditorApplication.java
 *
 *  Main class for ImageEditor project - perform various operations on an image
 *  represented as a 2-dimensional array of pixel values.
 *
 *
 *  This class contains a main() method that serves as the main program for
 *  the application.
 *
 *  Copyright (c) 2003, 2004, 2005, 2008, 2009 - Russell C. Bjork
 */

package edu.gordon.cs.imageeditor;

import javax.swing.*;
import java.awt.*;

public class ImageEditorApplication
{
    /** Main method for program
     */
    public static void main(String [] args)
    {
        // Create the GUI

        final JFrame frame = new JFrame("Image Editor Demonstration");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        ImageEditorGUI gui = new ImageEditorGUI(frame);
        frame.getContentPane().add(gui, BorderLayout.CENTER);

        // Pack the GUI to fit, then show it

        frame.pack();

        // Force an open event to occur, which will allow user to choose
        // initial image

        gui.forceOpen();

        frame.setVisible(true);
    }
}
