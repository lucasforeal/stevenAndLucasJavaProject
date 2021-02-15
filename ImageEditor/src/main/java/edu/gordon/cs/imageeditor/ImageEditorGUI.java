/**
 *  ImageEditorGUI.java
 *
 *  Part of ProjectImage project - perform various operations on an image represented
 *  as a 2-dimensional array of pixel values.
 *
 *  This class represents the main Panel for the GUI.
 *
 *  Copyright (c) 2003, 2004, 2005, 2008, 2009, 2013 - Russell C. Bjork
 */

package edu.gordon.cs.imageeditor;

import javax.swing.*;
import javax.swing.filechooser.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.imageio.*;
import java.io.*;

public class ImageEditorGUI extends JPanel
{
    /** Constructor
     *
     *  @param frame the frame in which this GUI is being displayed, if run as
     *          an application.  Null if run as an applet
     */
    ImageEditorGUI(final JFrame frame)
    {
        this.frame = frame;

        grayScaleColorModel = new GrayScaleColorModel();
        useColor = ProjectImage.isColorCapable();

        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        cardLayout = new CardLayout();
        setLayout(cardLayout);

        // Create the main card

        JPanel mainCard = new JPanel();
        add("Main", mainCard);

        mainCard.setLayout(new BorderLayout(0, 20));

        imageCanvas = new ImageCanvas();
        imageScrollPane = new JScrollPane(imageCanvas);
        imageScrollPane.getViewport().setBackground(this.getBackground());
        JPanel imageWrapper = new JPanel();
        imageWrapper.add(imageScrollPane);
        mainCard.add(imageWrapper, BorderLayout.CENTER);

        mainCard.add(new ButtonPanel(this), BorderLayout.SOUTH);

        // Create the card for displaying the histogram

        histogramCard = new HistogramCard(this, cardLayout, "Main");
        add("Histogram", histogramCard);

        // Create the Menubar for the frame if program is run as an application

        if (frame != null)
            frame.setMenuBar(createMenuBar(frame));
    }

    /** Create MenuBar for application version
     *
     *  @param frame the frame to which the MenuBar will belong
     */
    private MenuBar createMenuBar(final JFrame frame)
    {
        MenuBar menuBar = new MenuBar();

        // Create the file menu

        Menu fileMenu = new Menu("File");
        menuBar.add(fileMenu);

        openItem = new MenuItem("Open ...", new MenuShortcut('O'));
        fileMenu.add(openItem);
        openItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                File filename;

                JFileChooser chooser =
                    new JFileChooser(System.getProperty("user.dir") + "/images");
                if (chooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION)
                    filename = chooser.getSelectedFile();
                else
                    return;

                int [] [] newData;

                try
                {
                    newData = ImageInput.readFile(filename, ! useColor);
                }
                catch(IOException exception)
                {
                    JOptionPane.showMessageDialog(frame,
                        exception,
                        "Error reading file",
                        JOptionPane.ERROR_MESSAGE);

                    return;
                }
                catch(Throwable t)
                {
                   JOptionPane.showMessageDialog(frame,
                        "File type not recognized as an image",
                        "Error reading file",
                        JOptionPane.ERROR_MESSAGE);

                    return;
                }

                if (useColor && ! ImageInput.isSimpleGrayScale(newData))
                {
                    image = new ProjectImage(ColorModel.getRGBdefault(), newData);
                }
                else
                {
                    image = new ProjectImage(grayScaleColorModel, newData);
                }

                setImagePaneSize();
                redisplayImage(true);
            }
        });

        saveAsItem = new MenuItem("Save As ...", new MenuShortcut('S'));
        fileMenu.add(saveAsItem);
        saveAsItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                if (image == null) return;

                // Allow user to choose file name

                File filename = null;
                String extension;
                do
                {
                    JFileChooser chooser =
                        new JFileChooser(System.getProperty("user.dir") + "/images");
                    if (chooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION)
                        filename = chooser.getSelectedFile();
                    else
                        return;

                    String filenameString = filename.toString();
                    int dotPosition = filenameString.lastIndexOf(".");
                    if (dotPosition >= 0)
                        extension = filenameString.substring(dotPosition + 1).toLowerCase();
                    else
                        extension = "";
                    if (! extension.equals("png"))
                    {
                        JOptionPane.showMessageDialog(frame,
                                                      "Can only write png files",
                                                      "Inappropriate file type",
                                                      JOptionPane.ERROR_MESSAGE);
                        filename = null;
                    }

                }
                while (filename == null);

                // Turn image into a standard Java 2D BufferedImage

                BufferedImage bufferedImage = new BufferedImage(
                        image.getWidth(),
                        image.getHeight(),
                        BufferedImage.TYPE_INT_RGB);
                bufferedImage.setRGB(0,
                                     0,
                                     image.getWidth(),
                                     image.getHeight(),
                                     image.getPixelsIntRGB(),
                                     0,
                                     image.getWidth());

                // Write it out

                try
                {
                    ImageIO.write(bufferedImage, "png", filename);
                }
                catch(IOException exception)
                {
                    JOptionPane.showMessageDialog(frame,
                                                  exception,
                                                  "Error writing file",
                                                  JOptionPane.ERROR_MESSAGE);
                }
            }
        });


        quitItem =
            new MenuItem("Quit", new MenuShortcut('Q'));
        fileMenu.add(quitItem);
        quitItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                frame.setVisible(false);
                frame.dispose();
                System.exit(0);
            }
        });

        // Create a menu item to allow turning color on and off if the
        // image is color capable

        if (ProjectImage.isColorCapable())
        {
            Menu colorMenu = new Menu("Color");
            menuBar.add(colorMenu);

            final CheckboxMenuItem colorItem =
                new CheckboxMenuItem("Use Color", true);
            colorMenu.add(colorItem);
            colorItem.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent e)
                {
                    setUseColor(colorItem.getState());
                }
            });
        }

        return menuBar;
    }

    /** Get the image currently being displayed in this GUI
     *
     *  @return the image
     */
    ProjectImage getImage()
    {
        return image;
    }

    /** Set the image.
     *
     *  @param image the image to set
     */
    public void setImage(ProjectImage image)
    {
        this.image = image;
    }

    /** Update the image displayed by the GUI when an editing operation has
     *  been done
     *
     *  @param sizeMayHaveChanged if true, this indicates that the image
     *      operation may have changed the size of the image
     */
    void redisplayImage(boolean sizeMayHaveChanged)
    {
        if (image == null) return;
        imageCanvas.setData(image.getColorModel(), image.getPixels());
        if (sizeMayHaveChanged)
        {
            imageCanvas.setSizeChanged();
            imageScrollPane.invalidate();
            imageScrollPane.validate();
            imageScrollPane.getHorizontalScrollBar().setValue(
                imageScrollPane.getHorizontalScrollBar().getMinimum());
            imageScrollPane.getVerticalScrollBar().setValue(
                imageScrollPane.getVerticalScrollBar().getMinimum());
        }
        imageScrollPane.repaint();
    }

    /** Set the size for the pane that displays the image
     *
     */
    void setImagePaneSize()
    {
        // Set the size of the viewport

        imageCanvas.setData(image.getColorModel(), image.getPixels());
        imageScrollPane.getViewport().setViewSize(
            new Dimension(image.getWidth(), image.getHeight()));

        // Let the pane calculate its preferred size based on
        // the new image size + scroll bar allowance

        imageScrollPane.setPreferredSize(null); // Forces recalculation
        Dimension scrollPaneSize = imageScrollPane.getPreferredSize();

        // Set the pane showing the image to be a square to allow
        // for rotates.  Since the preferred size is set based on the
        // image's original size, if the image is made smaller by Halve,
        // it will appear in the center of the pane; if it is made
        // larger by Zoom, the scroll bars will be used

        int maximumDimension = Math.max(scrollPaneSize.width,
                                        scrollPaneSize.height);
        imageScrollPane.setPreferredSize(new Dimension(maximumDimension,
                                                       maximumDimension));
        if (frame != null)
            frame.pack();
    }

    /** Get the frame in which this GUI is displayed
     *
     *  @return the frame - null if run as an applet
     */
    JFrame getFrame()
    {
        return frame;
    }

    /** Set color usage.  The applet version uses this at startup; the
     *  application version uses this when color usage is toggled in the
     *  color menu.
     *
     *  @param useColor true to use color, false for gray-scale
     */
    void setUseColor(boolean useColor)
    {
        this.useColor = useColor;
    }

    /** Show a histogram
     *
     *  @param histogram the histogram to display.  If this parameter is null,
     *         the professor's version will be shown
     */
    void showHistogram(int [] histogram)
    {
        histogramCard.setHistogram(histogram);
        cardLayout.show(ImageEditorGUI.this, "Histogram");
    }

    /** Force an open event - used when starting up the application version to give
     *  user a choice as to which image to display.  (Not used by applet version)
     */
    void forceOpen()
    {
        Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(
                new ActionEvent(openItem, ActionEvent.ACTION_PERFORMED, "Open"));
/* ~~~ Replaced by the above
        openItem.dispatchEvent(new ActionEvent(openItem,
                               ActionEvent.ACTION_PERFORMED, "Open"));
*/
    }

    // The layout for the GUI

    private CardLayout cardLayout;

    // The image that carries out commands given to the GUI

    private ProjectImage image;

    // The canvas on which the image is displayed, and the scroll pane that
    // contains it

    private ImageCanvas imageCanvas;
    private JScrollPane imageScrollPane;

    // Menu items

    private MenuItem openItem, saveAsItem, quitItem;

    // The frame used by the application version of the program - will
    // be null if run as an applet

    private JFrame frame;

    // True if color images should be used as color, not gray scale

    private boolean useColor;

    // Allow the same GUI code to work with both color and grayscale images

    private ColorModel grayScaleColorModel;

    // The card used to display the image's histogram

    private HistogramCard histogramCard;
}
