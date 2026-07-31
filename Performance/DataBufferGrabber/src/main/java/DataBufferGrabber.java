/*
 * DataBufferGrabber.java
 *
 * Created on May 2, 2007, 7:51 AM
 *
 * Copyright (c) 2007, Sun Microsystems, Inc
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   * Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above
 *     copyright notice, this list of conditions and the following
 *     disclaimer in the documentation and/or other materials provided
 *     with the distribution.
 *   * Neither the name of the TimingFramework project nor the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.Raster;
import java.util.Arrays;

/// @author Chet
public class DataBufferGrabber extends JComponent {

    private final int SWATCH_SIZE = 500;

    /// Creates a new instance of DataBufferGrabber
    public DataBufferGrabber() {
        setPreferredSize(new Dimension(2 * SWATCH_SIZE, SWATCH_SIZE));
    }

    /// Perform and time several drawImage() calls with the given parameters
    /// and return the number of milliseconds that the operation took.
    private long copyImage(Graphics g, BufferedImage image, int x, int y) {
        var startTime = System.nanoTime();
        // Do the operation several times to make the timings more significant
        for (var i = 0; i < 100; ++i) {
            g.drawImage(image, x, y, null);
        }
        // Make sure any graphics commands in hardware get flushed before
        // stopping the clock
        Toolkit.getDefaultToolkit().sync();
        var endTime = System.nanoTime();
        return (endTime - startTime) / 1000000;
    }

    protected void paintComponent(Graphics g) {
        // create an image
        var bImg = new BufferedImage(SWATCH_SIZE,
                SWATCH_SIZE, BufferedImage.TYPE_INT_RGB);
        var gImage = bImg.getGraphics();
        gImage.setColor(Color.WHITE);
        gImage.fillRect(0, 0, SWATCH_SIZE, SWATCH_SIZE);

        // Time how long it takes to copy the managed version
        var managedTime = copyImage(g, bImg, 0, 0);
        System.out.println("Managed: " + managedTime + " ms");

        // Now grab the pixel array, change the colors, re-run the test
        Raster raster = bImg.getRaster();
        var dataBuffer = (DataBufferInt) raster.getDataBuffer();
        var pixels = dataBuffer.getData();
        // Make all pixels black
        Arrays.fill(pixels, 0);

        // Time this un-managed copy
        var unmanagedTime = copyImage(g, bImg, SWATCH_SIZE, 0);
        System.out.println("Unmanaged: " + unmanagedTime + " ms");
    }


    private static void createAndShowGUI() {
        var f = new JFrame("DataBufferGrabber");
        f.getContentPane().setLayout(new FlowLayout());
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(100, 100);
        f.add(new DataBufferGrabber());
        f.validate();
        f.pack();
        f.setVisible(true);
    }

    static void main(String[] args) {
        Runnable doCreateAndShowGUI = DataBufferGrabber::createAndShowGUI;
        SwingUtilities.invokeLater(doCreateAndShowGUI);
    }

}
