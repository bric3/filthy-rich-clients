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
import java.awt.geom.Rectangle2D;

/**
 * @author Romain Guy
 */
public class ApplicationFrame extends JFrame {

    private AvatarChooser chooser;
    private CurvesPanel curves;

    public ApplicationFrame() throws HeadlessException {
        super("Stack Layout");

        buildContentPane();
        // buildDebugControls();

        startAnimation();

        setSize(640, 400);
        setLocationRelativeTo(null);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    private void startAnimation() {
        var timer = new Timer(50, e -> {
            curves.animate();
            curves.repaint();
        });
        timer.start();
    }

    private void buildDebugControls() {
        var pane = new JPanel(new BorderLayout());
        pane.setBackground(Color.WHITE);
        pane.add(new JSeparator(), BorderLayout.NORTH);

        final var grapher = new GraphPanel();
        var graphPane = new JPanel(new FlowLayout(FlowLayout.CENTER));
        graphPane.setOpaque(false);
        graphPane.add(grapher);

        var buttonsPane = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonsPane.setOpaque(false);

        var spacing = new JSlider(JSlider.HORIZONTAL, 0, 100, 40);
        spacing.setBackground(Color.WHITE);
        spacing.setPreferredSize(new Dimension(95, spacing.getPreferredSize().height));
        spacing.addChangeListener(e -> {
            var slider = (JSlider) e.getSource();
            var spacing1 = slider.getValue() / 100.0;
            chooser.setSpacing(spacing1);
            grapher.spacing = spacing1;
            grapher.repaint();
        });

        var sigma = new JSlider(JSlider.HORIZONTAL, 0, 100, 50);
        sigma.setBackground(Color.WHITE);
        sigma.setPreferredSize(new Dimension(95, sigma.getPreferredSize().height));
        sigma.addChangeListener(e -> {
            var slider = (JSlider) e.getSource();
            var sigma1 = slider.getValue() / 100.0;
            chooser.setSigma(sigma1);
            grapher.sigma = sigma1;
            grapher.repaint();
        });

        var position = new JSlider(JSlider.HORIZONTAL, -100, 100, 0);
        position.setBackground(Color.WHITE);
        position.setPreferredSize(new Dimension(95, position.getPreferredSize().height));
        position.addChangeListener(e -> {
            var slider = (JSlider) e.getSource();
            var position1 = slider.getValue() / 100.0;
            chooser.setPosition(position1);
            grapher.position = position1;
            grapher.repaint();
        });

        var amount = new JSlider(JSlider.HORIZONTAL, 1, 15, 5);
        amount.setBackground(Color.WHITE);
        amount.setPreferredSize(new Dimension(95, position.getPreferredSize().height));
        amount.addChangeListener(e -> {
            var slider = (JSlider) e.getSource();
            var amount1 = slider.getValue();
            chooser.setAmount(amount1);
            grapher.amount = amount1;
            grapher.repaint();
        });

        buttonsPane.add(new JLabel("Spacing: "));
        buttonsPane.add(spacing);
        buttonsPane.add(new JLabel("Sigma: "));
        buttonsPane.add(sigma);
        buttonsPane.add(new JLabel("Position: "));
        buttonsPane.add(position);
        buttonsPane.add(new JLabel("Amount: "));
        buttonsPane.add(amount);

        pane.add(buttonsPane, BorderLayout.NORTH);
        pane.add(graphPane, BorderLayout.CENTER);

        add(pane, BorderLayout.SOUTH);
    }

    private void buildContentPane() {
        var pane = new JPanel();
        pane.setLayout(new StackLayout());

        var gradient = new GradientPanel();
        chooser = new AvatarChooser();
        curves = new CurvesPanel();

        pane.add(gradient, StackLayout.TOP);
        pane.add(chooser, StackLayout.TOP);
        pane.add(curves, StackLayout.TOP);

        add(pane);
    }

    private class GraphPanel extends JComponent {
        private double spacing = 0.4;
        private double position = 0.0;
        private double sigma = 0.5;
        private int amount = 5;

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(200, 60);
        }

        @Override
        public boolean isOpaque() {
            return false;
        }

        @Override
        protected void paintComponent(Graphics g) {
            var g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(Color.BLACK);
            g2.drawLine(0, 50, 100, 50);
            g2.drawLine(50, 0, 50, 60);

            g2.setColor(Color.BLUE);
            var lastY = 50.0;
            for (var x = 0; x < 100; x++) {
                var y = chooser.computeModifier((50.0 - x) / 25.0) * 45.0 + 10;
                if (x == 0) {
                    lastY = y;
                }
                g2.drawLine(x - 1, 60 - (int) lastY, x, 60 - (int) y);
                lastY = y;
            }

            g2.setColor(Color.RED);

            for (var i = 0; i < amount; i++) {
                var offset = (((double) amount / 2) - i) * spacing;

                var x = (100.0 - 5.0) / 2.0;
                x += 25.0 * (position + offset);

                if (x > 100) {
                    continue;
                }

                var y = 60.0 - (chooser.computeModifier(position + offset) * 45.0 + 10);
                g2.fill(new Rectangle2D.Double(x, y - 1, 5.0, 5.0));
            }

            g2.setColor(Color.GREEN.darker());
            g2.drawLine(25, 0, 25, 60);
            g2.drawLine(75, 0, 75, 60);

            g2.setColor(Color.DARK_GRAY);
            g2.drawString("Sigma: " + sigma, 110.0f, 16.0f);
            g2.drawString("Spacing: " + spacing, 110.0f, 30.0f);
            g2.drawString("Position: " + position, 110.0f, 44.0f);
        }
    }

    static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (ClassNotFoundException | UnsupportedLookAndFeelException | InstantiationException |
                 IllegalAccessException ex) {
            ex.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            var tester = new ApplicationFrame();
            tester.setVisible(true);
        });
    }
}