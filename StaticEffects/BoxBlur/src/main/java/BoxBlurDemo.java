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
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.IOException;
import java.util.Arrays;

/// @author Romain Guy <romain.guy@mac.com></romain.guy@mac.com>
public class BoxBlurDemo extends JFrame {
    private final BlurTestPanel blurTestPanel;
    private final JSlider radiusSlider;
    private final JCheckBox fasterBlurCheck;

    public BoxBlurDemo() {
        super("Box Blur");

        blurTestPanel = new BlurTestPanel();
        add(blurTestPanel);

        radiusSlider = new JSlider(1, 10, 1);
        radiusSlider.addChangeListener(e -> blurTestPanel.setRadius(radiusSlider.getValue()));

        fasterBlurCheck = new JCheckBox("2-steps blur");
        fasterBlurCheck.addChangeListener(e -> blurTestPanel.setFastBlur(fasterBlurCheck.isSelected()));

        var controls = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controls.add(new JLabel("Radius: 1px"));
        controls.add(radiusSlider);
        controls.add(new JLabel("10px"));

        controls.add(Box.createHorizontalStrut(12));
        controls.add(fasterBlurCheck);

        add(controls, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private static class BlurTestPanel extends JPanel {
        private BufferedImage image = null;
        private BufferedImage imageA;
        private int radius = 1;
        private boolean fasterBlur = false;

        public BlurTestPanel() {
            try {
                imageA = GraphicsUtilities.loadCompatibleImage(getClass().getResource("scenery.jpg"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            setOpaque(false);
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(imageA.getWidth(), imageA.getHeight());
        }

        @Override
        protected void paintComponent(Graphics g) {
            if (image == null) {
                image = new BufferedImage(imageA.getWidth() + 2 * radius,
                        imageA.getHeight() + 2 * radius,
                        BufferedImage.TYPE_INT_ARGB);
                var g2 = image.createGraphics();
                g2.drawImage(imageA, radius, radius, null);
                g2.dispose();

                var start = System.nanoTime();
                if (!fasterBlur) {
                    image = getBlurFilter(radius).filter(image, null);
                } else {
                    image = getBlurFilter(radius, 0).filter(image, null);
                    image = getBlurFilter(0, radius).filter(image, null);
                }
                var delay = System.nanoTime() - start;
                System.out.println("time = " + (delay / 1000.0f / 1000.0f) + "ms");
            }

            var x = (getWidth() - image.getWidth()) / 2;
            var y = (getHeight() - image.getHeight()) / 2;
            g.drawImage(image, x, y, null);
        }

        public void setRadius(int radius) {
            this.radius = radius;
            image = null;
            repaint();
        }

        private void setFastBlur(boolean fasterBlur) {
            this.fasterBlur = fasterBlur;
            image = null;
            repaint();
        }
    }

    public static ConvolveOp getBlurFilter(int horizontalRadius,
                                           int verticalRadius) {
        var width = horizontalRadius * 2 + 1;
        var height = verticalRadius * 2 + 1;

        var weight = 1.0f / (width * height);
        var data = new float[width * height];

        Arrays.fill(data, weight);

        var kernel = new Kernel(width, height, data);
        return new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
    }

    public static ConvolveOp getBlurFilter(int radius) {
        if (radius < 1) {
            throw new IllegalArgumentException("Radius must be >= 1");
        }

        var size = radius * 2 + 1;
        var weight = 1.0f / (size * size);
        var data = new float[size * size];

        Arrays.fill(data, weight);

        var kernel = new Kernel(size, size, data);
        return new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
    }

    static void main(String... args) {
        SwingUtilities.invokeLater(() -> new BoxBlurDemo().setVisible(true));
    }
}
