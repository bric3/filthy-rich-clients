/*
 * $Id: BlendCompositeDemo.java,v 1.3 2007/02/10 02:19:33 gfx Exp $
 *
 * Dual-licensed under LGPL (Sun and Romain Guy) and BSD (Romain Guy).
 *
 * Copyright 2006 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 *
 * Copyright (c) 2006 Romain Guy <romain.guy@mac.com>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package composite;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

/**
 * SwingX is now defunct {@link org.jdesktop.swingx.graphics.BlendComposite},
 * the code was copied here to {@link BlendComposite}
 *
 * @author Romain Guy <romain.guy@mac.com>
 */
public class BlendCompositeDemo extends JFrame {
    private final CompositeTestPanel compositeTestPanel;
    private final JComboBox<BlendComposite.BlendingMode> combo;
    private final JSlider slider;

    public BlendCompositeDemo() {
        super("Blend Composites");

        compositeTestPanel = new CompositeTestPanel();
        compositeTestPanel.setComposite(BlendComposite.Average);
        add(compositeTestPanel);

        slider = new JSlider(0, 100, 100);
        slider.addChangeListener(_ -> {
            var blend = (BlendComposite) compositeTestPanel.getComposite();
            blend = blend.derive(slider.getValue() / 100.0f);
            compositeTestPanel.setComposite(blend);
        });

        combo = new JComboBox<>(BlendComposite.BlendingMode.values());
        combo.addActionListener(_ -> compositeTestPanel.setComposite(
                BlendComposite.getInstance(
                        BlendComposite.BlendingMode.valueOf(Objects.requireNonNull(combo.getSelectedItem()).toString()),
                        slider.getValue() / 100.0f
                )));


        var controls = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controls.add(combo);
        controls.add(new JLabel("0%"));
        controls.add(slider);
        controls.add(new JLabel("100%"));
        add(controls, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private static class CompositeTestPanel extends JPanel {
        private BufferedImage image = null;
        private Composite composite = AlphaComposite.Src;
        private BufferedImage imageA;
        private BufferedImage imageB;
        private boolean repaint = false;

        public CompositeTestPanel() {
            setOpaque(false);
            try {
                imageA = GraphicsUtilities.loadCompatibleImage(getClass().getResource("images/A.jpg"));
                imageB = GraphicsUtilities.loadCompatibleImage(getClass().getResource("images/B.jpg"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(imageA.getWidth(), imageA.getHeight());
        }

        @Override
        protected void paintComponent(Graphics g) {
            if (image == null) {
                image = new BufferedImage(imageA.getWidth(),
                        imageA.getHeight(),
                        BufferedImage.TYPE_INT_ARGB);
                repaint = true;
            }

            if (repaint) {
                var g2 = image.createGraphics();
                g2.setComposite(AlphaComposite.Clear);
                g2.fillRect(0, 0, image.getWidth(), image.getHeight());

                g2.setComposite(AlphaComposite.Src);
                g2.drawImage(imageA, 0, 0, null);
                g2.setComposite(getComposite());
                g2.drawImage(imageB, 0, 0, null);
                g2.dispose();

                repaint = false;
            }

            var x = (getWidth() - image.getWidth()) / 2;
            var y = (getHeight() - image.getHeight()) / 2;
            g.drawImage(image, x, y, null);
        }

        public void setComposite(Composite composite) {
            if (composite != null) {
                this.composite = composite;
                this.repaint = true;
                repaint();
            }
        }

        public Composite getComposite() {
            return this.composite;
        }
    }

    static void main(String... args) {
        SwingUtilities.invokeLater(() -> new BlendCompositeDemo().setVisible(true));
    }
}
