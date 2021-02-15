/*
 * Array2dImageProducer.java
 *
 *  Part of ImageEditor project - perform various operations on an image represented
 *  as a 2-dimensional array of pixel values.
 *
 *  Convert a two-dimensional array of pixel values into an image object.
 *  The array is represented as an array of ints, to be interpreted
 *  according to the color model specified when an object of this class
 *  is created.
 *
 * copyright (c) 2003, 2004, 2005, 2008, 2009 - Russell C. Bjork
 *
 */

package edu.gordon.cs.imageeditor;

import java.awt.image.ImageProducer;
import java.awt.image.ImageConsumer;
import java.awt.image.ColorModel;
import java.util.Hashtable;
import java.util.Vector;

public class Array2dImageProducer implements ImageProducer
{
    /** Constructor
     *
     *  @param colorModel the color model to use for interpreting the
     *         pixel values
     *  @param pixels the 2 dimensional array of pixel values to
     *         be converted to an image.
     */
    public Array2dImageProducer(ColorModel colorModel, int [] [] pixels)
    {
        this.colorModel = colorModel;
        width = pixels[0].length;
        height = pixels.length;
        this.pixels = pixels;

        // Set up to handle requirements of the ImageProducer interface

        consumers = new Vector<ImageConsumer>();
        properties = new Hashtable();
    }

    /* ************************************************************************
     * Methods required by the ImageProducer interface
     * ***********************************************************************/

    /** Register a consumer with this producer
     *
     *  @param consumer the consumer to register
     */
    public void addConsumer(ImageConsumer consumer)
    {
        if (! consumers.contains(consumer))
            consumers.addElement(consumer);
    }

    /** Check to see if a consumer is registered with this producer
     *
     *  @param consumer the possibly null consumer to check
     */
    public boolean isConsumer(ImageConsumer consumer)
    {
        if (consumer == null)
            return false;
        else
            return consumers.contains(consumer);
    }

    /** Remove a consumer from list registered with this producer
     *
     *  @param consumer the consumer to remove
     */
    public void removeConsumer(ImageConsumer consumer)
    {
        consumers.removeElement(consumer);
    }

    /** Send image to all of the registered consumers - then deregister each
     *  Can specify a consumer to add to the list of registered consumers
     *  before doing so
     *
     *  @param consumer the consumer initiating the request
     */
    public void startProduction(ImageConsumer consumer)
    {
        if (consumer != null)
            addConsumer(consumer);

        // Loop through list of consumers backwards - must do in this order
        // because we remove each consumer after sending image to it

        for (int i = consumers.size() - 1; i >= 0; i --)
        {
            ImageConsumer oneConsumer = consumers.elementAt(i);
            sendImage(oneConsumer);
            removeConsumer(oneConsumer);
        }
    }

    /** Request top-down left-right retransmission of an image - shouldn't
     *  be a problem because that's what we always do!
     *
     *  @param consumer the consumer initiating the request
     */
    public void requestTopDownLeftRightResend(ImageConsumer consumer)
    {
        sendImage(consumer);
    }

    // Private method - actually send the image to one consumer.
    // Parameter: the consumer to send the image to

    private void sendImage(ImageConsumer consumer)
    {
        consumer.setDimensions(width, height);
        consumer.setProperties(properties);
        consumer.setColorModel(colorModel);
        consumer.setHints(ImageConsumer.SINGLEPASS |
                              ImageConsumer.SINGLEFRAME |
                              ImageConsumer.TOPDOWNLEFTRIGHT);
        for (int row = 0; row < height; row ++)
            consumer.setPixels(0, row, width, 1, colorModel, pixels[row], 0, width);
        consumer.imageComplete(ImageConsumer.STATICIMAGEDONE);
    }

    private Vector<ImageConsumer> consumers;
    // List of image consumers registered with this
    private Hashtable properties;   // Properties of this image

    // The current image

    private int width, height;      // The size of the image
    private ColorModel colorModel;  // Color model for interpreting the image's
                                    // pixels
    private int [][] pixels;        // The pixel data in the image
}
