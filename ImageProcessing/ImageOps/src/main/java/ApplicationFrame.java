/*
 * Copyright (c) 2007, Romain Guy
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
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.image.*;
import java.io.IOException;

/**
 * <p>Demos of buffered image operations.</p>
 *
 * @author Romain Guy <romain.guy@mac.com>
 */
public class ApplicationFrame extends JFrame {
    private BufferedImage sourceImage;

    public ApplicationFrame() {
        super("Image Ops Demo");

        loadSourceImage();
        buildTabbedPane();

        pack();

        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    static void main(String... args) {
        SwingUtilities.invokeLater(() -> new ApplicationFrame().setVisible(true));
    }

    private void buildTabbedPane() {
        var tabbedPane = new JTabbedPane();

        buildNoOpTab(tabbedPane);
        buildAffineTransformOpTab(tabbedPane);
        buildColorConvertOpTab(tabbedPane);
        buildConvolveOpTab(tabbedPane);
        buildLookupOpTab(tabbedPane);
        buildRescaleOpTab(tabbedPane);

        add(tabbedPane);
    }

    private void loadSourceImage() {
        try {
            // Load a compatible image for performance
            sourceImage = GraphicsUtilities.loadCompatibleImage(
                    getClass().getResource("./images/mirror_lake.jpg"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void buildNoOpTab(JTabbedPane tabbedPane) {
        tabbedPane.add("No Op", new JLabel(new ImageIcon(sourceImage)));
    }

    private void buildAffineTransformOpTab(JTabbedPane tabbedPane) {
        BufferedImage dstImage = null;
        var transform = AffineTransform.getScaleInstance(0.5, 0.5);
        var op = new AffineTransformOp(transform,
                AffineTransformOp.TYPE_BILINEAR);
        dstImage = op.filter(sourceImage, null);

        tabbedPane.add("Affine Transform", new JLabel(new ImageIcon(dstImage)));
    }

    private void buildColorConvertOpTab(JTabbedPane tabbedPane) {
        BufferedImage dstImage = null;
        var colorSpace = ColorSpace.getInstance(ColorSpace.CS_GRAY);
        var op = new ColorConvertOp(colorSpace, null);
        dstImage = op.filter(sourceImage, null);

        tabbedPane.add("Color Convert", new JLabel(new ImageIcon(dstImage)));
    }

    private void buildConvolveOpTab(JTabbedPane tabbedPane) {
        BufferedImage dstImage = null;
        var sharpen = new float[]{
                0.0f, -1.0f, 0.0f,
                -1.0f, 5.0f, -1.0f,
                0.0f, -1.0f, 0.0f
        };
        var kernel = new Kernel(3, 3, sharpen);
        var op = new ConvolveOp(kernel);
        dstImage = op.filter(sourceImage, null);

        tabbedPane.add("Convolve", new JLabel(new ImageIcon(dstImage)));
    }

    private void buildLookupOpTab(JTabbedPane tabbedPane) {
        BufferedImage dstImage = null;
        var data = new short[256];
        for (var i = 0; i < 256; i++) {
            data[i] = (short) (255 - i);
        }
        LookupTable lookupTable = new ShortLookupTable(0, data);
        var op = new LookupOp(lookupTable, null);
        dstImage = op.filter(sourceImage, null);

        tabbedPane.add("Lookup", new JLabel(new ImageIcon(dstImage)));
    }

    private void buildRescaleOpTab(JTabbedPane tabbedPane) {
        BufferedImage dstImage = null;
        var factors = new float[]{
                1.4f, 1.4f, 1.4f
        };
        var offsets = new float[]{
                0.0f, 0.0f, 30.0f
        };
        var op = new RescaleOp(factors, offsets, null);
        dstImage = op.filter(sourceImage, null);

        tabbedPane.add("Rescale", new JLabel(new ImageIcon(dstImage)));
    }
}
