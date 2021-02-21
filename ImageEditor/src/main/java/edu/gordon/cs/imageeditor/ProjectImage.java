/*
 * ProjectImage.java
 *
 * Part of ImageEditor project - perform various operations on an image
 * represented  as a 2-dimensional array of pixel values.  Completion of
 * the methods of this class constitutes CPS122 Project 1
 *
 * Copyright (c) 2003, 2004, 2005, 2008, 2009, 2013 - Russell C. Bjork
 * Copyright (c) 2021 - Russ Tuck
 *
 * Student author:
 */

package edu.gordon.cs.imageeditor;

import java.io.*;
import java.awt.image.ColorModel;
import java.util.Random;

public class ProjectImage
{
  // Image data
  private ColorModel colorModel;
  private int[][] pixels;
  private int width;
  private int height;

  // Constants
  private static final int LIGHTEN_DARKEN_AMOUNT = 3;
  private static final int MAX_BRIGHTNESS = 255;
  private static final int MIN_BRIGHTNESS = 0;
  private static final int MAX_RANDOM = 256 * 256 * 256;

  /** Constructor
   *
   *  @param colorModel the color model to use for interpreting the
   *         pixel values
   *  @param pixels the data content of this image - a two-dimensional array
   *      having height rows, each containing width values.  If this is a
   *      grayscale image, then each element lies in the range 0 .. 255.  If
   *      this is a color image, then each element is a packed 24 bit color
   *      with alpha value
   */
  public ProjectImage(ColorModel colorModel, int[][] pixels) {
    this.colorModel = colorModel;
    this.pixels = pixels;
    this.height = pixels.length;
    this.width = pixels[0].length;
  }

  /**************************************************************************
   * Accessor for information about this image
   *************************************************************************/

  /** Find out if this class is capable of handling color images.  This method
   *  is made static so it can be called before an object of this class is
   *  created.
   *
   *  @return true if this class can handle color images.
   *
   *  If the return value is false, this class will assume that images are
   *  represented by grayscale values in the range 0 .. 255.  If the return
   *  value is true, this class can also handle images that are represented by
   *  packed color values, to be interpreted by colorModel.
   */
  public static boolean isColorCapable() {
    return false;
  }

  /**************************************************************************
   * Accessors for information about this image
   *************************************************************************/

  /** Get the pixels
   *
   *  @return the pixels for this image - represented as a 2 dimensional
   *          array of integers, to be interpreted according to the
   *          color model
   */
  public int [] [] getPixels() {
    return pixels;
  }

  /** Get the pixels of this image as a one-dimensional array of packed RGB
   *  values in the standard representation used internally by the Java
   *  image routines
   *
   *  @return the pixels of this image - represented as a 1 dimensional
   *          array of integers representing packed RGB values
   */
  public int [] getPixelsIntRGB() {
    int [] result = new int[height * width];
    for (int row = 0; row < height; row ++) {
      for (int col = 0; col < width; col ++) {
        result[row * width + col] = isColor()
          ? pixels[row][col]
          : pixels[row][col] * 0x10101; // Makes all three colors same
      }
    }
    return result;
  }

  /** Get the width of this image
   *
   *  @return the width of this image
   */
  public int getWidth() {
    return width;
  }

  /** Get the height of this image
   *
   *  @return the height of this image
   */
  public int getHeight() {
    return height;
  }

  /** Get the color model used by this image
   *
   *  @return the color model for this image
   */
  public ColorModel getColorModel() {
    return colorModel;
  }

  /** Check to see whether this image is color
   *
   *  @return true if this image is color
   */
  public boolean isColor() {
    return ! (colorModel instanceof GrayScaleColorModel);
  }

  /**************************************************************************
   * Mutators to alter this image.  Some of these will alter the
   * image "in place", while others will change the width and/or height,
   * resulting in the creation of a new array of pixels.
   *************************************************************************/

  /** Lighten the image by a small fixed amount
   */
  public void lighten() {
    for (int row = 0; row < height; row ++) {
      for (int col = 0; col < width; col ++) {
        pixels[row][col] = keepBelowMaxBrightness(pixels[row][col] + LIGHTEN_DARKEN_AMOUNT);
      }
    }
  }
  /** Darken the image by the same fixed amount as lighten()
   */
  public void darken() {
    for (int row = 0; row < height; row ++) {
      for (int col = 0; col < width; col ++) {
        pixels[row][col] = keepAboveMinBrightness(pixels[row][col] - LIGHTEN_DARKEN_AMOUNT);
      }
    }
  }     

  /** Reverses the value of pixels
   */
  public void negative() {
    for (int row = 0; row < height; row ++) {
      for (int col = 0; col < width; col ++) {
        pixels[row][col] = MAX_BRIGHTNESS - pixels[row][col];
      }
    }
  }
  /** With the average pixel value as reference, it takes all pixels in the array,
   *  and makes all values closer to the average (if they are not average values yet).
   */
  public void reduceContrast() {
    int avgPixelValue = getAvgPixelValue(pixels);
    for (int row = 0; row < height; row ++) {
      for (int col = 0; col < width; col ++) {
        if (pixels[row][col] < avgPixelValue) {
          pixels[row][col]++;
        } else if (pixels[row][col] > avgPixelValue) {
          pixels[row][col]--;
        }
      }
    }
  }
  /** With the average pixel value as reference, it takes all pixels in the array,
   *  and makes all values further from the average (if they are not average values yet).
   */
  public void enhanceContrast() {
    int avgPixelValue = getAvgPixelValue(pixels);
    for (int row = 0; row < height; row ++) {
      for (int col = 0; col < width; col ++) {
        if (pixels[row][col] < avgPixelValue) {
          pixels[row][col] = keepAboveMinBrightness(pixels[row][col] - 1);
        } else if (pixels[row][col] > avgPixelValue) {
          pixels[row][col] = keepBelowMaxBrightness(pixels[row][col] + 1);
        }
      }
    }
  }
  /** Keeps the pixel value below the MAX_BRIGHTNESS limit
   * 
   *  @param the pixel to be changed/kept
   *  @return the value of the pixel
   */
  private int keepBelowMaxBrightness(int pixel) {
    if (pixel > MAX_BRIGHTNESS) {
      pixel = MAX_BRIGHTNESS;
    }
    return pixel;
  }
  /** Keeps the pixel value above the MIN_BRIGHTNESS limit
   * 
   *  @param the pixel to be changed/kept
   *  @return the value of the pixel
   */
  private  int keepAboveMinBrightness(int pixel) {
    if (pixel < MIN_BRIGHTNESS) {
      pixel = MIN_BRIGHTNESS;
    }
    return pixel;
  }
  /** Takes all the pixels in a picture and calculates the average pixel value
   * 
   *  @param pixels the 2D array of pixels; the picture
   *  @return the integer average pixel value
   */
  private int getAvgPixelValue(int [][] pixels) {
    int totalValue = 0, numOfPixels = 0;
    for (int [] row: pixels) {
      for (int pixel: row) {
        totalValue += pixel;
        numOfPixels++;
      }
    }
    return totalValue / numOfPixels;
  }
//L SH Work on line 171, methods above. check negative button and encrypt/decrypt
public void encryptDecrypt() {
  int randomValue = (int) (Math.random() * MAX_BRIGHTNESS);
  // Something about nextInt(MAX_BRIGHTNESS+1)
  for (int row = 0; row < height; row++) {
    for (int col = 0; col < width; col++) {
      pixels[row][col] += randomValue;
    }
  }
}

public void flipHorizontally() {
  int [][] newPixels = new int [height][width];
  for (int row = 0; row < height; row++) {
    for (int col = 0; col < width; col++) {
      newPixels[row][col] = pixels[row][width - col - 1];
    }
  }
  pixels = newPixels;
}
public void flipVertically() {
  int [][] newPixels = new int [height][width];
  for (int row = 0; row < height; row++) {
    for (int col = 0; col < width; col++) {
      newPixels[row][col] = pixels[height - row - 1][col];
    }
  }
  pixels = newPixels;

}
public void rotate() {
  int [][] newPixels = new int [width][height];
  for (int row = 0; row < height; row++) {
    for (int col = 0; col < width; col++) {
      newPixels[row][col] = pixels[col][row];// Check syntax
      // Regarding the [y][x] (?) format, which one is the height,
      // and which one is the width?
    }
  }
}
  /** Scale the image by a factor of 0.5 in each dimension
   */
  public void halve() {
    // We need to build a new image in a separate array, and then make
    // it our current image

    int newWidth = width / 2;
    int newHeight = height / 2;
    int [] [] newPixels = new int [newHeight] [newWidth];

    // Each pixel in the new image is an average of a 2 x 2 square of pixels
    // in the original image

    for (int row = 0; row < newHeight; row ++) {
      for (int col = 0; col < newWidth; col ++) {
        newPixels[row][col] = (pixels[2*row][2*col]
                               + pixels[2*row+1][2*col]
                               + pixels[2*row][2*col+1]
                               + pixels[2*row+1][2*col+1])/4;
      }
    }

    // Now replace the current image with the one we just created
    width = newWidth;
    height = newHeight;
    pixels = newPixels;
  }

  /* *************************************************************************
   * Utility method for working with colorized images
   * ************************************************************************/

  /** If we are using a full color representation of the image, then pack
   *  three colors into a single pixel, along with a 100% alpha value.
   *  If any color value is greater than the maximum or less than the minimum,
   *  it is forced to the maximum or minimum value (as the case may be)
   *  before packing.
   *
   *  @param red the red value to pack
   *  @param green the green value to pack
   *  @param blue the blue value to pack
   *  @return the packed RGB representation for this pixel
   */
  private int pack(int red, int green, int blue) {
    if (red > MAX_BRIGHTNESS) {
      red = MAX_BRIGHTNESS;
    } else if (red < MIN_BRIGHTNESS) {
      red = MIN_BRIGHTNESS;
    }

    if (green > MAX_BRIGHTNESS) {
      green = MAX_BRIGHTNESS;
    } else if (green < MIN_BRIGHTNESS) {
      green = MIN_BRIGHTNESS;
    }

    if (blue > MAX_BRIGHTNESS) {
      blue = MAX_BRIGHTNESS;
    } else if (blue < MIN_BRIGHTNESS) {
      blue = MIN_BRIGHTNESS;
    }

    // If we are using a simple 8 bit gray-scale representation for the
    // image, then all colors are the same and we can simply return any one
    // of them.  Otherwise, we must let the color model pack them correctly

    if (colorModel instanceof GrayScaleColorModel) {
      return red;
    } else {
      int [] components = { red, green, blue, 255 };
      return colorModel.getDataElement(components, 0);
    }
  }

}
