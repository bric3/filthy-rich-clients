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

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

/// @author Romain Guy
public class SourceInDemo extends JFrame {
    private final JCheckBox shadow;

    public SourceInDemo() {
        super("Source In");

        add(new ImageViewer(), BorderLayout.CENTER);
        var panel = new JPanel(new FlowLayout(FlowLayout.LEADING));
        panel.add(shadow = new JCheckBox("Drop Shadow"));
        shadow.addChangeListener(_ -> repaint());
        add(panel, BorderLayout.SOUTH);

        setSize(350, 250);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private class ImageViewer extends JComponent {
        private BufferedImage image, landscape;

        private ImageViewer() {
            try {
                image = ImageIO.read(Objects.requireNonNull(getClass().getResource("picture.png")));
                landscape = ImageIO.read(Objects.requireNonNull(getClass().getResource("landscape.jpg")));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            var temp = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
            var g2 = temp.createGraphics();

            var x = (getWidth() - image.getWidth()) / 2;
            var y = (getHeight() - image.getHeight()) / 2;
            if (shadow.isSelected()) {
                g2.drawImage(image, x + 4, y + 10, null);

                var oldComposite = g2.getComposite();
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_IN, 0.75f));
                g2.setColor(Color.BLACK);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setComposite(oldComposite);
                g2.drawImage(image, x, y, null);
            } else {
                g2.drawImage(image, x, y, null);

                var oldComposite = g2.getComposite();
                g2.setComposite(AlphaComposite.SrcIn);
                x = (getWidth() - landscape.getWidth()) / 2;
                y = (getHeight() - landscape.getHeight()) / 2;
                g2.drawImage(landscape, x, y, null);
                g2.setComposite(oldComposite);
            }

            g2.dispose();
            g.drawImage(temp, 0, 0, null);
        }
    }

    static void main(String... args) {
        SwingUtilities.invokeLater(() -> new SourceInDemo().setVisible(true));
    }
}
