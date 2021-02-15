/**
 *  HoldableButton.java
 *
 *  Part of ImageEditor project - perform various operations on an image represented
 *  as a 2-dimensional array of pixel values.
 *
 *  This is a special kind of button that "clicks itself" repeatedly if held
 *  down.
 *
 *  Copyright (c) 2005, 2009 - Russell C. Bjork
 */

package edu.gordon.cs.imageeditor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

class HoldableButton extends JButton
{
    /** Constructor
     *
     *  @param label the label for this button
     */
    HoldableButton(String label)
    {
        super(label);

        // We associate a thread with this button that clicks it repeatedly
        // if it is held down

        final ClickerThread clickerThread = new ClickerThread(this);
        clickerThread.start();

        addMouseListener(new MouseAdapter() {

            public void mousePressed(MouseEvent e)
            {
                clickerThread.setRunning(true);
            }

            public void mouseReleased(MouseEvent e)
            {
                clickerThread.setRunning(false);
            }
        });
    }

    // Amount of time button must be held down before it is clicked the
    // first time

    private static int DELAY_TO_FIRST_CLICK = 1000; // ms

    // Amount of time between simulated clicks

    private static int DELAY_BETWEEN_CLICKS = 200;  // ms

    // We associate a thread with each holdable button which simulates
    // repeated clicks if the button is held down

    private class ClickerThread extends Thread
    {
        /** Constructor
         *
         *  @param button the button this thread should click periodically
         */
        ClickerThread(HoldableButton button)
        {
            this.button = button;
            running = false;
        }

        /** Set the running state of this thread
         *
         *  @param running the new value of running
         */
        public synchronized void setRunning(boolean running)
        {
            this.running = running;
            if (running)
                notify();
            else
                interrupt();
        }

        /** Run method required by base class Thread
         */
        public void run()
        {
            while(true)
            {
                while (! running)
                {
                    try
                    {
                        synchronized(this)
                        {
                            wait();
                        }
                    }
                    catch(InterruptedException e)
                    { }
                }

                // Sleep here ensures that button is held down for a while before
                // simulating clicking

                try
                {
                    sleep(DELAY_TO_FIRST_CLICK);
                }
                catch(InterruptedException e)
                { }

                while(running)
                {
                    // Simulate a click

                    button.doClick();

                    try
                    {
                        sleep(DELAY_BETWEEN_CLICKS);
                    }
                    catch(InterruptedException e)
                    { }
                }
            }
        }

        private HoldableButton button;
        private boolean running;
    }
}
