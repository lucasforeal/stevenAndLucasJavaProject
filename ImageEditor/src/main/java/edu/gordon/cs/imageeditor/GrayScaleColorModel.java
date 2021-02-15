/*
 * GrayScaleColorModel.java
 *
 *  Part of ImageEditor project - perform various operations on an image represented
 *  as a 2-dimensional array of pixel values.
 *
 *  A simple color model for use with image data represented by a single
 *  brightness value in the range of 0 .. 255.  All three color components
 *  will be equal to this value, and alpha will always be 1.0.  This is
 *  defined as a separate class so objects of this class can be recognized by
 *  an instanceof test.
 *
 *  Copyright (c) 2005, 2008, 2013 - Russell C. Bjork
 */

package edu.gordon.cs.imageeditor;

import java.awt.image.ColorModel;

public class GrayScaleColorModel extends ColorModel
{
    /** Constructor
     */
    public GrayScaleColorModel()
    {
        super(32);
    }

    // Implementation of abstract methods required by base class

    /** Get the red component for the specified pixel
     *
     *  @param pixel the pixel - a grayscale value in the range 0 .. 255
     *  @return the red value - which will simply be the value of the pixel
     */
    public int getRed(int pixel)
    {
        return pixel;
    }

    /** Get the green component for the specified pixel
     *
     *  @param pixel the pixel - a grayscale value in the range 0 .. 255
     *  @return the green value - which will simply be the value of the pixel
     */
    public int getGreen(int pixel)
    {
        return pixel;
    }

    /** Get the blue component for the specified pixel
     *
     *  @param pixel the pixel - a grayscale value in the range 0 .. 255
     *  @return the blue value - which will simply be the value of the pixel
     */
    public int getBlue(int pixel)
    {
        return pixel;
    }

    /** Get the alpha component for the specified pixel
     *
     *  @param pixel the pixel - which will be ignored
     *  @return an alpha value equivalent to total opacity
     */
    public int getAlpha(int pixel)
    {
        return 255;
    }
}
