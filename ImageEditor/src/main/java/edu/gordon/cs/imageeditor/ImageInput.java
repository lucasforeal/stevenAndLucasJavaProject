/**
 *  ImageInput.java
 *
 *  Part of ImageEditor project - perform various operations on an image represented
 *  as a 2-dimensional array of pixel values.
 *
 *  This class provides static methods for reading image files.  It is capable
 *  of reading an image in a "standard" format (.jpg, .gif, .png), and of reading
 *  images in the program's own format
 *
 *  Copyright (c) 2003, 2004, 2005, 2009 - Russell C. Bjork
 */

package edu.gordon.cs.imageeditor;

import java.awt.*;
import java.awt.image.*;
import java.io.*;

public class ImageInput
{
    /** Read an image from a file.
     *
     *  @param filename the name of the file to read
     *  @param forceGray true if the image must be forced to gray scale
     *  @return a 2-dimensional array of pixels read from the file.  This
     *         will be an array of values in the range of 0..255 if the original
     *         image was stored as gray scale, or forceGray is true.  Otherwise,
     *         it will be in the default 24 bit RGB format
     *
     *  @exception IOException if the file cannot be found or read
     */
    public static int [] [] readFile(File filename, boolean forceGray)
                                            throws IOException, AWTException
    {
        int [] [] pixels;

        // First see if we can load the image as a standard image file

        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Image image = toolkit.getImage(filename.toString());
        pixels = loadImage(image);

        // Handle conversion to gray scale if required

        if (forceGray)
        {
            convertToSimpleGrayScale(pixels);
        }

        return pixels;
    }

    /** Convert a "standard format" image (read from a file or URL) into a
     *  2 dimensional representation.
     *
     *  @param image the image to convert
     *
     *  @return 2 dimensional representation of the image
     *
     *  @exception AWTException if there is a problem reading the image.  Note
     *          that this is often detected by a timeout occurring when waiting
     *          for the toolkit's prepareImage method, which will fail if the
     *          toolkit can't handle the image.
     */
    public static int [] [] loadImage(final Image image) throws AWTException
    {
        ImageObserver observer = new ImageObserver() {
            public boolean imageUpdate(Image image, int infoflags,
                                        int x, int y, int width, int height)
            {
                if ((infoflags & ImageObserver.ALLBITS) == ImageObserver.ALLBITS)
                {
                    synchronized(image)
                    {
                        image.notify();
                    }
                    return false;
                }
                else
                    return true;
            }
        };

        // Get the data from the image

        boolean available;
        synchronized(image)
        {
            available = Toolkit.getDefaultToolkit().
                prepareImage(image, -1, -1, observer);
            while (! available)
            {
                try
                {
                    image.wait(2000);
                    available = true;
                }
                catch(InterruptedException e)
                { }
            }
        }

        int width = image.getWidth(null);
        int height = image.getHeight(null);
        int [] pixels1d = new int[width * height];

        PixelGrabber grabber =
            new PixelGrabber(image, 0, 0, width, height, pixels1d, 0, width);
        try
        {
            available = grabber.grabPixels();
        }
        catch(InterruptedException e)
        {
            available = false;
        }
        if (! available)
        {
            throw new AWTException("Error loading image - status = " +
                                    grabber.status());
        }

        // Convert the data from the image into a 2d array

        int [] [] pixels = new int [height] [width];

        for (int i = 0; i < height; i ++)
            for (int j = 0; j < width; j ++)
                pixels[i][j] = pixels1d[i * width + j];

        return pixels;
    }

    /** Test to see whether image data represents a simple gray scale image.
     *  Such an image contains no data outside the rightmost 8 bits; the
     *  occurrence of any data anywhere else denotes a packed color image.
     *
     *  @param pixels the pixels to check
     *  @return true if these pixels represent a simple gray scale image
     */
    public static boolean isSimpleGrayScale(int [] [] pixels)
    {
        for (int i = 0; i < pixels.length; i ++)
            for (int j = 0; j < pixels[0].length; j ++)
                if ((pixels[i][j] & 0xffffff00) != 0)
                    return false;

        return true;
    }

    /** Convert a color image to simple gray scale.  The array of pixels
     *  is changed in place.
     *
     *  @param pixels the array of pixels to be converted from color to
     *         gray scale
     */
    public static void convertToSimpleGrayScale(int [] [] pixels)
    {
        ColorModel colorModel = ColorModel.getRGBdefault();
        for (int i = 0; i < pixels.length; i ++)
            for (int j = 0; j < pixels[0].length; j ++)
            {
                int pixel = pixels[i][j];
                pixels[i][j] =
                    (colorModel.getRed(pixel) + colorModel.getGreen(pixel) +
                     colorModel.getBlue(pixel)) / 3;
            }
    }
}
