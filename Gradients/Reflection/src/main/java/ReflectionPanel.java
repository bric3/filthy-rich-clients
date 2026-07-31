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

/**
 *
 * @author Romain Guy
 */
public class ReflectionPanel extends JPanel {
    private BufferedImage image = null;

    /**
     * Creates a new instance of ReflectionPanel
     */
    public ReflectionPanel() {
        try {
            image = ImageIO.read(Objects.requireNonNull(getClass().getResource("Mirror Lake.jpg")));
            image = createReflection(image);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        setOpaque(false);
    }

    private BufferedImage createReflection(BufferedImage image) {
        var height = image.getHeight();

        var result = new BufferedImage(image.getWidth(), height * 2,
                BufferedImage.TYPE_INT_ARGB);
        var g2 = result.createGraphics();

        // Paints original image
        g2.drawImage(image, 0, 0, null);

        // Paints mirrored image
        g2.scale(1.0, -1.0);
        g2.drawImage(image, 0, -height - height, null);
        g2.scale(1.0, -1.0);

        // Move to the origin of the clone
        g2.translate(0, height);

        // Creates the alpha mask
        GradientPaint mask;
        mask = new GradientPaint(0, 0, new Color(1.0f, 1.0f, 1.0f, 0.5f),
                0, (float) height / 2, new Color(1.0f, 1.0f, 1.0f, 0.0f));
        var oldPaint = g2.getPaint();
        g2.setPaint(mask);

        // Sets the alpha composite
        g2.setComposite(AlphaComposite.DstIn);

        // Paints the mask
        g2.fillRect(0, 0, image.getWidth(), height);

        g2.dispose();
        return result;
    }

    @Override
    protected void paintComponent(Graphics g) {
        var g2 = (Graphics2D) g;

        g2.translate(20, 20);
        g2.drawImage(image, 0, 0, null);
        g2.translate(-20, -20);
    }
}
