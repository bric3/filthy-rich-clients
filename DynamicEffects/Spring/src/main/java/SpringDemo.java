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

import org.jdesktop.animation.timing.interpolation.PropertySetter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Objects;

/// @author Romain Guy <romain.guy@mac.com></romain.guy@mac.com>
public class SpringDemo extends JFrame {
    private JList<Application> list;
    private SpringGlassPane glassPane;

    public SpringDemo() {
        super("Spring Demo");

        setupGlassPane();

        add(Box.createVerticalStrut(16), BorderLayout.NORTH);
        add(Box.createHorizontalStrut(16), BorderLayout.WEST);
        add(buildList());
        add(Box.createHorizontalStrut(16), BorderLayout.EAST);
        add(Box.createVerticalStrut(16), BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    private void setupGlassPane() {
        glassPane = new SpringGlassPane();
        setGlassPane(glassPane);
        glassPane.setVisible(true);
    }

    private JComponent buildList() {
        var elements = new Application[]{
                Application.of("Address Book", "x-office-address-book.png"),
                Application.of("Calendar", "x-office-calendar.png"),
                Application.of("Presentation", "x-office-presentation.png"),
                Application.of("Spreadsheet", "x-office-spreadsheet.png"),
        };

        list = new JList<>(elements);
        list.setCellRenderer(new ApplicationListCellRenderer());
        list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        list.setVisibleRowCount(2);
        list.setBorder(BorderFactory.createEtchedBorder());
        list.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    var index = list.getSelectedIndex();

                    var bounds = list.getCellBounds(index, index);
                    var location = new Point(bounds.x, bounds.y);
                    location = SwingUtilities.convertPoint(list, location, glassPane);
                    location.y -= 13;
                    bounds.setLocation(location);

                    glassPane.showSpring(bounds, list.getSelectedValue().icon.getImage());
                }
            }
        });

        var panel = new JPanel(new GridBagLayout());
        panel.add(new JLabel("Launcher"),
                new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0,
                        GridBagConstraints.LINE_START, GridBagConstraints.NONE,
                        new Insets(0, 0, 0, 0), 0, 0));
        panel.add(list, new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(0, 0, 0, 0), 0, 0));
        panel.add(new JLabel("Double-click an icon to launch the program"),
                new GridBagConstraints(0, 2, 1, 1, 1.0, 1.0,
                        GridBagConstraints.LINE_START, GridBagConstraints.NONE,
                        new Insets(0, 0, 0, 0), 0, 0));

        return panel;
    }

    public static class SpringGlassPane extends JComponent {
        private static final float MAGNIFY_FACTOR = 1.5f;

        private Rectangle bounds;
        private Image image;

        private float zoom = 0.0f;

        @Override
        protected void paintComponent(Graphics g) {
            if (image != null && bounds != null) {
                var width = image.getWidth(this);
                width += (int) (image.getWidth(this) * MAGNIFY_FACTOR * getZoom());

                var height = image.getHeight(this);
                height += (int) (image.getHeight(this) * MAGNIFY_FACTOR * getZoom());

                var x = (bounds.width - width) / 2;
                var y = (bounds.height - height) / 2;

                var g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                        RenderingHints.VALUE_INTERPOLATION_BILINEAR);

                g2.setComposite(AlphaComposite.SrcOver.derive(1.0f - getZoom()));
                g2.drawImage(image, x + bounds.x, y + bounds.y,
                        width, height, null);
            }
        }

        public void showSpring(Rectangle bounds, Image image) {
            this.bounds = bounds;
            this.image = image;

            var animator = PropertySetter.createAnimator(250, this,
                    "zoom", 0.0f, 1.0f);
            animator.setAcceleration(0.2f);
            animator.setDeceleration(0.4f);
            animator.start();

            repaint();
        }

        public float getZoom() {
            return zoom;
        }

        public void setZoom(float zoom) {
            this.zoom = zoom;
            repaint();
        }
    }

    private static class ApplicationListCellRenderer extends DefaultListCellRenderer {
        public Component getListCellRendererComponent(JList list, Object value,
                                                      int index, boolean isSelected, boolean cellHasFocus) {
            JLabel c;
            c = (JLabel) super.getListCellRendererComponent(list, value,
                    index, isSelected, cellHasFocus);

            var element = (Application) value;
            c.setBorder(BorderFactory.createEmptyBorder(32, 32, 32, 32));
            c.setFont(c.getFont().deriveFont(18.0f).deriveFont(Font.BOLD));
            c.setText(element.label);
            c.setIcon(element.icon);
            c.setHorizontalTextPosition(JLabel.CENTER);
            c.setVerticalTextPosition(JLabel.BOTTOM);
            if (isSelected) {
                c.setBackground(new Color(0, 0, 200, 20));
            }

            return c;
        }
    }

    private record Application(ImageIcon icon, String label) {
        public static Application of(String label, String icon) {
            return new Application(
                    new ImageIcon(Objects.requireNonNull(Application.class.getResource("images/" + icon))),
                    label
            );
        }
    }

    static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SpringDemo().setVisible(true));
    }
}
