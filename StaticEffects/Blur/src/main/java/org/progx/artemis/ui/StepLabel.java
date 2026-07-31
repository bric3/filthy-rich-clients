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

package org.progx.artemis.ui;

import org.progx.artemis.graphics.GraphicsUtilities;

import javax.swing.*;
import java.awt.*;

class StepLabel extends JLabel {
    private int step;
    private boolean rebuildIcon;

    public StepLabel() {
        setBackground(Color.BLACK);
        setForeground(Color.WHITE);
        setHorizontalAlignment(CENTER);
        setIconTextGap(12);
        // setOpaque(true);
        setFont(new Font("Helvetica", Font.PLAIN, 24));
        setStep(1);
        setText(" ");
    }

    public void setStep(int step) {
        this.step = step;
        this.rebuildIcon = true;
        repaint();
    }

    public int getStep() {
        return step;
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (rebuildIcon) {
            var fontMetrics = g.getFontMetrics();
            var height = (int) (fontMetrics.getHeight() / 1.5 + 2);

            var image = GraphicsUtilities.createTranslucentCompatibleImage(height, height);
            var g2 = image.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

            g2.setFont(getFont().deriveFont(Font.BOLD, 20));
            fontMetrics = g2.getFontMetrics();

            var s = String.valueOf(step);
            var x = (height - fontMetrics.stringWidth(s)) / 2;
            var y = height - fontMetrics.getAscent() / 8;

            // g2.setColor(Color.WHITE);
            var paint = g2.getPaint();
            g2.setPaint(new GradientPaint(0.0f, 0.0f, Color.WHITE,
                    0.0f, height, Color.GRAY));
            g2.fillOval(0, 0, height, height);
            g2.setPaint(paint);
            // g2.setColor(Color.BLACK);
            g2.setComposite(AlphaComposite.Clear);
            g2.drawString(s, x, y);

            g2.dispose();

            setIcon(new ImageIcon(image));
            rebuildIcon = false;
        }

        super.paintComponent(g);
    }
}
