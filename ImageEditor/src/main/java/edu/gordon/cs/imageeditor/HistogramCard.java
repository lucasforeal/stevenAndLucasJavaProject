    /**
 *  HistogramCard.java
 *
 *  Part of ImageEditor project - perform various operations on an image represented
 *  as a 2-dimensional array of pixel values.
 *
 *  This is the card that displays a histogram of the image
 *
 *  Copyright (c) 2003, 2004, 2005, 2008, 2009 - Russell C. Bjork
 */

package edu.gordon.cs.imageeditor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.ColorModel;

/** Card that displays the histogram */

class HistogramCard extends JPanel
{
    /** Constructor
     *
     *  param mainGUI the main gui
     *  @param layout the card layout for the main gui
     *  @param returnTo the name of the card to return to when the user
     *      dismisses the histogram
     */
    HistogramCard(final Container mainGUI,
                  final CardLayout layout,
                  final String returnTo)
    {
        super();

        setLayout(new BorderLayout());
        JComponent histogramCanvas = new JComponent() {

            public void paint(Graphics graphics)
            {
                // Get the sum and the maximum of the values in the array -
                // determines what corresponds to a maximum length bar.  Also
                // determine which brightness value corresponds to max

                int sum = 0; int max = 0;
                int maxForBrightness = 0;
                for (int i = 0; i < histogram.length; i ++)
                {
                    sum += histogram[i];
                    if (histogram[i] > max)
                    {
                        max = histogram[i];
                        maxForBrightness = i;
                    }
                }

                // Calculate the height to be used for actually drawing the
                // histogram

                int histogramHeight = getSize().height - 30;

                // Center the histogram in the available space

                int leftBorder = (getSize().width - histogram.length) / 2;
                int topBorder = (getSize().height - histogramHeight) / 2;

                // Draw the horizontal labels

                graphics.drawString("0", leftBorder, histogramHeight + 29);
                graphics.drawString("" + maxForBrightness,
                    leftBorder + maxForBrightness -
                        graphics.getFontMetrics().stringWidth(""+maxForBrightness)/2,
                    histogramHeight + 29);
                graphics.drawString("255",
                    leftBorder + 255 -
                        graphics.getFontMetrics().stringWidth("255"),
                    histogramHeight + 29);

                // Draw the vertical label

                graphics.drawString("" + max,
                    leftBorder - graphics.getFontMetrics().stringWidth("" + max),
                    20);

                // Draw the histogram.  The longest bar will just fill the
                // available space

                for (int i = 0; i < histogram.length; i ++)
                    graphics.drawLine(leftBorder + i,
                                      topBorder + histogramHeight,
                                      leftBorder + i,
                                      topBorder + (int) (histogramHeight *
                                            (1 - (double) histogram[i] / max)));
            }
        };

        label = new JLabel(" ", JLabel.CENTER);
        add(label, BorderLayout.NORTH);

        add(histogramCanvas, BorderLayout.CENTER);

        JPanel okButtonPanel = new JPanel();
        add(okButtonPanel, BorderLayout.SOUTH);
        JButton okButton = new JButton("OK");
        okButtonPanel.add(okButton);
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                layout.show(mainGUI, returnTo);
            }
        });
    }

    /** Set the histogram to be displayed by this card
     *
     *  @param histogram the histogram to display
     */
    public void setHistogram(int [] histogram)
    {
        this.histogram = histogram;
    }

    /** The minimum size for a histogram is 300 wide by 300 high
     *
     *  @return minimum size as specified above
     */
    public Dimension getMinimumSize()
    {
        return new Dimension(300, 300);
    }

    private JLabel label;       // Label to indicate professor's version is being
                                // used
    private int [] histogram;   // The histogram we are showing
}
