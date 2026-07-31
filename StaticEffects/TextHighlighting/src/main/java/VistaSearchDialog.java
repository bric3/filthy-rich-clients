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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.util.Map;

public class VistaSearchDialog extends JComponent {
    private static final int BLUR_SIZE = 7;
    private BufferedImage image;
    private float alpha = 0.0f;

    public VistaSearchDialog(JFrame frame) {
        Container contentPane = frame.getRootPane();
        image = GraphicsUtilities.createCompatibleTranslucentImage(contentPane.getWidth() +
                                                                   2 * BLUR_SIZE,
                contentPane.getHeight() +
                2 * BLUR_SIZE);
        var g2 = image.createGraphics();
        g2.translate(BLUR_SIZE, BLUR_SIZE);
        contentPane.paint(g2);
        g2.translate(-BLUR_SIZE, -BLUR_SIZE);
        g2.dispose();

        // 1.5 second vs 0.3 second
//        long start = System.currentTimeMillis();
        image = changeImageWidth(image, image.getWidth() / 2);
        var gaussianFilter = getGaussianBlurFilter(BLUR_SIZE, true);
        image = gaussianFilter.filter(image, null);
        gaussianFilter = getGaussianBlurFilter(BLUR_SIZE, false);
        image = gaussianFilter.filter(image, null);
        var colorMixFilter = new ColorTintFilter(Color.WHITE, 0.4f);
        image = colorMixFilter.filter(image, null);
        image = changeImageWidth(image, image.getWidth() * 2);
//        System.out.println("time = " +
//                           ((System.currentTimeMillis() - start) / 1000.0f));

        setBorder(new DropShadowBorder(Color.BLACK, 0, 11, .2f, 16,
                false, true, true, true));
        setLayout(new BorderLayout());

        initComponents();
    }

    public static BufferedImage changeImageWidth(BufferedImage image, int width) {
        var ratio = (float) image.getWidth() / (float) image.getHeight();
        var height = (int) (width / ratio);

        var temp = new BufferedImage(width, height,
                image.getType());
        var g2 = temp.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(image, 0, 0, temp.getWidth(), temp.getHeight(), null);
        g2.dispose();

        return temp;
    }

    public static ConvolveOp getGaussianBlurFilter(int radius,
                                                   boolean horizontal) {
        if (radius < 1) {
            throw new IllegalArgumentException("Radius must be >= 1");
        }

        var size = radius * 2 + 1;
        var data = new float[size];

        var sigma = radius / 3.0f;
        var twoSigmaSquare = 2.0f * sigma * sigma;
        var sigmaRoot = (float) Math.sqrt(twoSigmaSquare * Math.PI);
        var total = 0.0f;

        for (var i = -radius; i <= radius; i++) {
            float distance = i * i;
            var index = i + radius;
            data[index] = (float) Math.exp(-distance / twoSigmaSquare) / sigmaRoot;
            total += data[index];
        }

        for (var i = 0; i < data.length; i++) {
            data[i] /= total;
        }

        Kernel kernel = null;
        if (horizontal) {
            kernel = new Kernel(size, 1, data);
        } else {
            kernel = new Kernel(1, size, data);
        }
        return new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
    }

    private void initComponents() {
        var titleBar =
                new TitleBar("Search in this Message");
        add(titleBar, BorderLayout.NORTH);
        var contentPane = new SearchPanel();
        contentPane.setOpaque(false);
        contentPane.setBorder(BorderFactory.createEmptyBorder(16, 2, 16, 2));
        add(contentPane);
    }

    @Override
    public boolean isOpaque() {
        return false;
    }

    @Override
    protected void paintComponent(Graphics g) {
        setupGraphics((Graphics2D) g);

        var location = getLocation();
        location.x = -location.x - BLUR_SIZE;
        location.y = -location.y - BLUR_SIZE;

        var insets = getInsets();
        var oldClip = g.getClip();
        g.setClip(insets.left, insets.top,
                getWidth() - insets.left - insets.right,
                getHeight() - insets.top - insets.bottom);
        g.drawImage(image, location.x, location.y, null);
        g.setClip(oldClip);
    }

    public float getAlpha() {
        return alpha;
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
        repaint();
    }

    private static void setupGraphics(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        var tk = Toolkit.getDefaultToolkit();
        var desktopHints = (Map) (tk.getDesktopProperty("awt.font.desktophints"));
        if (desktopHints != null) {
            g2.addRenderingHints(desktopHints);
        }
    }

    private class TitleBar extends JComponent {
        private final String title;

        private TitleBar(String title) {
            this.title = title;
            setName("vistaTitleBar");
            setFont(new Font("Dialog", Font.BOLD, 12));
            setLayout(new GridBagLayout());

            var button = new JButton();
            button.setMargin(new Insets(0, 0, 0, 0));
            button.setBorder(null);
            button.setIconTextGap(0);
            button.setVerticalAlignment(SwingConstants.TOP);
            button.setContentAreaFilled(false);
            button.setBorderPainted(false);
            button.setFocusPainted(false);
            button.setIcon(new ImageIcon(getClass().getResource("close-title-bar.png")));
            button.setRolloverIcon(new ImageIcon(getClass().getResource("close-title-bar-rollover.png")));
            button.setOpaque(false);
            button.setName("vistaCloseButton");
            add(Box.createVerticalStrut(24),
                    new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0,
                            GridBagConstraints.LINE_START,
                            GridBagConstraints.HORIZONTAL,
                            new Insets(0, 0, 0, 0), 0, 0));
            add(button, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.FIRST_LINE_END,
                    GridBagConstraints.VERTICAL,
                    new Insets(0, 0, 0, 0), 0, 0));
            button.addActionListener(e -> VistaSearchDialog.this.setVisible(false));

            var locator = new Locator();
            addMouseListener(locator);
            addMouseMotionListener(locator);
        }

        @Override
        public boolean isOpaque() {
            return false;
        }

        @Override
        protected void paintComponent(Graphics g) {
            var g2 = (Graphics2D) g;
            setupGraphics(g2);

            var oldPaint = g2.getPaint();

            var rgb = new Color(0xe9efff).getRGBColorComponents(null);

            g2.setPaint(new GradientPaint(0.0f, 0.0f,
                    new Color(rgb[0], rgb[1], rgb[2], 0.2f * getAlpha()),
                    0.0f, getHeight(),
                    new Color(rgb[0], rgb[1], rgb[2], 0.8f * getAlpha())));
            g2.fillRect(0, 0, getWidth(), getHeight());
            drawText(g2, 3, 0.8f);

            g2.setPaint(oldPaint);

            g2.setColor(new Color(rgb[0], rgb[1], rgb[2], 0.6f * getAlpha()));
            g2.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
            g2.drawLine(0, getHeight() - 2, getWidth(), getHeight() - 2);
        }

        private void drawText(Graphics2D g2, int size, float opacity) {
            var oldComposite = g2.getComposite();
            var preAlpha = 1.0f;
            if (oldComposite instanceof AlphaComposite &&
                ((AlphaComposite) oldComposite).getRule() == AlphaComposite.SRC_OVER) {
                preAlpha = ((AlphaComposite) oldComposite).getAlpha();
            }

            g2.setFont(getFont());
            var metrics = g2.getFontMetrics();
            var ascent = metrics.getAscent();
            var heightDiff = (metrics.getHeight() - ascent) / 2;

            g2.setColor(Color.BLACK);

            var tx = 2.0;
            var ty = 2.0 + heightDiff - size;
            g2.translate(tx, ty);

            for (var i = -size; i <= size; i++) {
                for (var j = -size; j <= size; j++) {
                    double distance = i * i + j * j;
                    var alpha = opacity;
                    if (distance > 0.0d) {
                        alpha = (float) (1.0f / ((distance * size) * opacity));
                    }
                    alpha *= preAlpha;
                    if (alpha > 1.0f) {
                        alpha = 1.0f;
                    }
                    g2.setComposite(AlphaComposite.SrcOver.derive(alpha));
                    g2.drawString(title, i + size, j + size + ascent);
                }
            }

            g2.setComposite(oldComposite);
            g2.setColor(Color.WHITE);
            g2.drawString(title, size, size + ascent);

            g2.translate(-tx, -ty);
        }
    }

    private class Locator extends MouseAdapter {
        private Point startPoint;

        @Override
        public void mousePressed(MouseEvent e) {
            startPoint = e.getPoint();
            SwingUtilities.convertPointToScreen(startPoint, (Component) e.getSource());
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            VistaSearchDialog.this.setCursor(Cursor.getDefaultCursor());
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            var point = e.getPoint();
            SwingUtilities.convertPointToScreen(point, (Component) e.getSource());
            var distance_x = point.x - startPoint.x;
            var distance_y = point.y - startPoint.y;

            VistaSearchDialog.this.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));

            var location = VistaSearchDialog.this.getLocation();
            var oldLocation = (Point) location.clone();
            location.x += distance_x;
            location.y += distance_y;

            VistaSearchDialog.this.setLocation(location);

            var clip = new Rectangle(oldLocation.x, oldLocation.y,
                    VistaSearchDialog.this.getWidth(),
                    VistaSearchDialog.this.getHeight());
            clip.intersects(new Rectangle(location.x, location.y,
                    VistaSearchDialog.this.getWidth(),
                    VistaSearchDialog.this.getHeight()));

            VistaSearchDialog.this.getParent().repaint(clip.x, clip.y,
                    clip.width, clip.height);

            startPoint = point;
        }
    }

}
