/**
 *  ImageCanvas.java
 *
 *  Part of ImageEditor project - perform various operations on an image represented
 *  as a 2-dimensional array of pixel values.
 *
 *  This is the canvas that actually displays an image.
 *
 *  Copyright (c) 2003, 2004, 2005, 2008, 2009 - Russell C. Bjork
 */

package edu.gordon.cs.imageeditor;

import javax.swing.*;
import java.awt.*;
import java.awt.image.ColorModel;

/** Canvas that displays the image */

class ImageCanvas extends JComponent
{
    /** Constructor
     */
    ImageCanvas()
    {
        image = null;
    }

    /** Set the data for the image to be displayed by this canvas
     *
     *  @param colorModel the color model to use for interpreting the
     *         pixel values
     *  @param pixels the 2 dimensional array of pixel values to
     *         be converted to an image.
     */
    public void setData(ColorModel colorModel, int [] [] pixels)
    {
        Array2dImageProducer producer =
            new Array2dImageProducer(colorModel, pixels);
        image = getToolkit().createImage(producer);
    }

    /** Record that the size of this image has changed.  The next time the
     *  image is painted, its background will first need to be cleared.
     */
    public void setSizeChanged()
    {
        sizeHasChanged = true;
    }

    /** Get the preferred size for this canvas
     */
    public Dimension getPreferredSize()
    {
        if (image == null)
            return new Dimension(1, 1);
        else
            return new Dimension(image.getWidth(null),
                                 image.getHeight(null));
    }


    /** Paint this canvas
     *
     *  @param Graphics the graphics object to do the painting
     */
    public void paint(Graphics graphics)
    {
        if (image != null)
        {
            int iWidth = image.getWidth(null);
            int iHeight = image.getHeight(null);

            graphics.drawImage(image,
                iWidth >= getSize().width ? 0
                          : (getSize().width - iWidth) / 2,
                iHeight >= getSize().height ? 0
                          : (getSize().height - iHeight) / 2,
                null);
        }
    }

    /** Override update to not clear the background in cases where image size is
     *  not changing, to prevent flicker
     */
    public void update(Graphics graphics)
    {
        if (! sizeHasChanged)
        {
            // Skip clearing the background
            paint(graphics);
        }
        else
        {
            // The inherited version clears the background before painting
            // the image
            super.update(graphics);
            sizeHasChanged = false;
        }
    }

    private Image image;            // The image we are showing
    private boolean sizeHasChanged; // True if the size of this canvas has
                                    // been changed due to image size change
                                    // since the last time it was painted
}
