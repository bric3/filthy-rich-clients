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

import org.progx.artemis.Application;
import org.progx.artemis.graphics.GraphicsUtilities;
import org.progx.artemis.image.ColorTintFilter;
import org.progx.artemis.image.GaussianBlurFilter;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ProgressGlassPane extends JComponent {
    private static final int BAR_WIDTH = 200;
    private static final int BAR_HEIGHT = 10;

    private static final Color TEXT_COLOR = new Color(0xFFFFFF);

    private static final float[] GRADIENT_FRACTIONS = new float[]{
            0.0f, 0.499f, 0.5f, 1.0f
    };
    private static final Color[] GRADIENT_COLORS = new Color[]{
            Color.GRAY, Color.DARK_GRAY, Color.BLACK, Color.GRAY
    };
    private static final Color GRADIENT_COLOR2 = Color.WHITE;
    private static final Color GRADIENT_COLOR1 = Color.GRAY;

    private final String message =
            Application.getResourceBundle().getString("wait.message");
    private int progress = 0;
    private BufferedImage backDrop = null;

    public ProgressGlassPane() {
        setBackground(Color.WHITE);
        setFont(new Font("Helvetica", Font.BOLD, 16)); // NON-NLS
        setOpaque(true);
    }

    public int getProgress() {
        return progress;
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            var mainFrame = Application.getMainFrame();
            backDrop = GraphicsUtilities.createCompatibleImage(mainFrame.getRootPane().getWidth(),
                    mainFrame.getRootPane().getHeight());
            var g2 = backDrop.createGraphics();
            mainFrame.getRootPane().paint(g2);
            g2.dispose();

            backDrop = GraphicsUtilities.createThumbnail(backDrop,
                    mainFrame.getRootPane().getWidth() / 2);
            backDrop = new ColorTintFilter(Color.BLACK, 0.10f).filter(backDrop, null);
            backDrop = new GaussianBlurFilter(12).filter(backDrop, null);
        } else {
            if (backDrop != null) {
                backDrop.flush();
            }
            backDrop = null;
        }
        super.setVisible(visible);
    }

    public void setProgress(int progress) {
        var oldProgress = this.progress;
        this.progress = progress;

        if (progress > oldProgress) {
            // computes the damaged area
            var metrics = getGraphics().getFontMetrics(getFont());
            var w = (int) (BAR_WIDTH * ((float) oldProgress / 100.0f));
            var x = w + (getWidth() - BAR_WIDTH) / 2;
            var y = (getHeight() - BAR_HEIGHT) / 2;
            y += metrics.getDescent() / 2 + 2;

            w = (int) (BAR_WIDTH * ((float) progress / 100.0f)) - w;
            var h = BAR_HEIGHT;

            repaint(x, y, w, h);
        } else {
            var metrics = getGraphics().getFontMetrics(getFont());
            var w = (int) (BAR_WIDTH * ((float) oldProgress / 100.0f));
            var x = w + (getWidth() - BAR_WIDTH) / 2;
            var y = (getHeight() - BAR_HEIGHT) / 2;
            y += metrics.getDescent() / 2 + 2;

            w = (int) (BAR_WIDTH * ((float) progress / 100.0f)) + w;
            var h = BAR_HEIGHT;

            repaint(x, y, w, h);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        // enables anti-aliasing
        var g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        var mainFrame = Application.getMainFrame();
        var width = mainFrame.getRootPane().getWidth();
        var height = mainFrame.getRootPane().getHeight();
        g2.drawImage(backDrop, 0, 0, width, height, null);

        // sets a 65% translucent composite
        var alpha = AlphaComposite.SrcOver.derive(0.75f);
        var composite = g2.getComposite();

        // centers the progress bar on screen
        var metrics = g.getFontMetrics();
        var x = (getWidth() - BAR_WIDTH) / 2;
        var y = (getHeight() - BAR_HEIGHT - metrics.getDescent()) / 2;

        g2.setComposite(alpha);
        g2.setColor(Color.BLACK);
        g2.fillRoundRect(x - 15, y - metrics.getAscent() - 7,
                BAR_WIDTH + 30, BAR_HEIGHT + 22 + metrics.getAscent(),
                20, 20);

        g2.setComposite(composite);

        // draws the text
        g2.setColor(TEXT_COLOR);
        g2.drawString(message, x, y);

        // goes to the position of the progress bar
        y += metrics.getDescent() + 2;

        // computes the size of the progress indicator
        var w = (int) (BAR_WIDTH * ((float) progress / 100.0f));
        var h = BAR_HEIGHT;

        // draws the content of the progress bar
        var paint = g2.getPaint();

        g2.setComposite(alpha);

        // bar's background
        Paint gradient = new GradientPaint(x, y, GRADIENT_COLOR1,
                x, y + h, GRADIENT_COLOR2);
        g2.setPaint(gradient);
        g2.fillRect(x, y, BAR_WIDTH, BAR_HEIGHT);

        // actual progress
        gradient = new LinearGradientPaint(x, y, x, y + h,
                GRADIENT_FRACTIONS, GRADIENT_COLORS);
        g2.setPaint(gradient);
        g2.fillRect(x, y, w, h);

        g2.setPaint(paint);

        // draws the progress bar border
        g2.setColor(Color.DARK_GRAY);
        g2.drawRect(x, y, BAR_WIDTH, BAR_HEIGHT);

        g2.setComposite(composite);

    }
}
