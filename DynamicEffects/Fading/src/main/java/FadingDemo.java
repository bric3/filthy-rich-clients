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

import org.jdesktop.core.animation.timing.Animator;
import org.jdesktop.core.animation.timing.KeyFrames;
import org.jdesktop.core.animation.timing.PropertySetter;
import org.jdesktop.core.animation.timing.TimingTargetAdapter;
import org.jdesktop.core.animation.timing.interpolators.AccelerationInterpolator;
import org.jdesktop.swing.animation.timing.sources.SwingTimerTimingSource;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/// @author Romain Guy <romain.guy@mac.com>
public class FadingDemo extends JFrame {
    private static final SwingTimerTimingSource TIMING_SOURCE = createTimingSource();

    private ImageViewer imageViewer;

    private JButton nextButton;

    private HelpGlassPane glass;

    private final JTextField titleField;

    public FadingDemo() {
        super("Fading Demo");

        add(titleField = new JTextField("Suzhou"), BorderLayout.NORTH);
        add(buildImageViewer(), BorderLayout.CENTER);
        add(buildControls(), BorderLayout.SOUTH);

        pack();

        setupGlassPane();

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    private void setupGlassPane() {
        glass = new HelpGlassPane();
        setGlassPane(glass);
        glass.setVisible(true);
    }

    public static void setTextAndAnimate(final JTextComponent textComponent,
                                         final String text) {
        var c = textComponent.getForeground();

        var opaque = new Color(c.getRed(), c.getGreen(), c.getBlue(), 255);
        var transparent = new Color(c.getRed(), c.getGreen(), c.getBlue(), 0);
        var keyFrames = new KeyFrames.Builder<>(opaque)
                .addFrame(transparent)
                .addFrame(opaque)
                .build();
        var setter = PropertySetter.getTarget(textComponent, "foreground", keyFrames);

        var animator = new Animator.Builder(TIMING_SOURCE)
                .setDuration(200, TimeUnit.MILLISECONDS)
                .addTarget(setter)
                .addTarget(new TimingTargetAdapter() {
                    private boolean textSet = false;

                    @Override
                    public void timingEvent(Animator source, double fraction) {
                        if (fraction >= 0.5 && !textSet) {
                            textComponent.setText(text);
                            textSet = true;
                        }
                    }
                })
                .build();
        animator.start();
    }

    private static SwingTimerTimingSource createTimingSource() {
        var timingSource = new SwingTimerTimingSource();
        timingSource.init();
        return timingSource;
    }

    private JComponent buildControls() {
        var panel = new JPanel(new FlowLayout(FlowLayout.LEADING));

        var previousButton = new JButton("Previous");
        panel.add(previousButton);
        previousButton.addActionListener(e -> {
            imageViewer.previous();
            setTextAndAnimate(titleField, "Suzhou");
        });
        panel.add(nextButton = new JButton("Next"));
        nextButton.addActionListener(e -> {
            imageViewer.next();
            setTextAndAnimate(titleField, "Shanghai");

            if (glass.isVisible()) {
                var animator = new Animator.Builder(TIMING_SOURCE)
                        .setDuration(200, TimeUnit.MILLISECONDS)
                        .setInterpolator(new AccelerationInterpolator(.2f, .4f))
                        .addTarget(PropertySetter.getTargetTo(glass, "alpha", 0.0f))
                        .build();
                animator.start();
            }
        });

        return panel;
    }

    private JComponent buildImageViewer() {
        return imageViewer = new ImageViewer();
    }

    public class HelpGlassPane extends JComponent {
        private BufferedImage helpImage;
        private float alpha = 1.0f;

        private HelpGlassPane() {
            try {
                helpImage = GraphicsUtilities.loadCompatibleImage(
                        getClass().getResource("images/help.png"));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    var animator = new Animator.Builder(TIMING_SOURCE)
                            .setDuration(200, TimeUnit.MILLISECONDS)
                            .setInterpolator(new AccelerationInterpolator(.2f, .4f))
                            .addTarget(PropertySetter.getTargetTo(
                                    HelpGlassPane.this, "alpha", 0.0f))
                            .build();
                    animator.start();
                }
            });
        }

        public void setAlpha(float alpha) {
            this.alpha = alpha;
            if (alpha <= 0.01f) {
                setVisible(false);
            }
            repaint();
        }

        public float getAlpha() {
            return this.alpha;
        }

        @Override
        protected void paintComponent(Graphics g) {
            var g2 = (Graphics2D) g.create();

            var p = nextButton.getLocationOnScreen();

            p.x += nextButton.getWidth() / 2 - 16;
            p.y += nextButton.getHeight() / 2 - helpImage.getHeight() + 10;

            SwingUtilities.convertPointFromScreen(p, this);

            g2.setComposite(AlphaComposite.SrcOver.derive(alpha));
            g2.drawImage(helpImage, p.x, p.y, null);
        }
    }

    public static class ImageViewer extends JComponent {
        private BufferedImage firstImage;
        private BufferedImage secondImage;

        private float alpha = 0.0f;

        private ImageViewer() {
            try {
                firstImage = GraphicsUtilities.loadCompatibleImage(
                        getClass().getResource("images/suzhou.jpg"));
                secondImage = GraphicsUtilities.loadCompatibleImage(
                        getClass().getResource("images/shanghai.jpg"));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(firstImage.getWidth(), firstImage.getHeight());
        }

        public void next() {
            var animator = new Animator.Builder(TIMING_SOURCE)
                    .setDuration(1000, TimeUnit.MILLISECONDS)
                    .setInterpolator(new AccelerationInterpolator(.2f, .4f))
                    .addTarget(PropertySetter.getTargetTo(this, "alpha", 1.0f))
                    .build();
            animator.start();
        }

        public void previous() {
            var animator = new Animator.Builder(TIMING_SOURCE)
                    .setDuration(1000, TimeUnit.MILLISECONDS)
                    .setInterpolator(new AccelerationInterpolator(.2f, .4f))
                    .addTarget(PropertySetter.getTargetTo(this, "alpha", 0.0f))
                    .build();
            animator.start();
        }

        public void setAlpha(float alpha) {
            this.alpha = alpha;
            repaint();
        }

        public float getAlpha() {
            return this.alpha;
        }

        @Override
        protected void paintComponent(Graphics g) {
            var g2 = (Graphics2D) g.create();

            g2.setComposite(AlphaComposite.SrcOver.derive(1.0f - alpha));
            g2.drawImage(firstImage, 0, 0, null);
            g2.setComposite(AlphaComposite.SrcOver.derive(alpha));
            g2.drawImage(secondImage, 0, 0, null);
        }
    }

    static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FadingDemo().setVisible(true));
    }
}
