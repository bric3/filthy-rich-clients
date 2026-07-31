/**
 * Copyright (c) 2006, Sun Microsystems, Inc
 * All rights reserved.
 * <p>
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * <p>
 * * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided
 * with the distribution.
 * * Neither the name of the TimingFramework project nor the names of its
 * contributors may be used to endorse or promote products derived
 * from this software without specific prior written permission.
 * <p>
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
import javax.swing.border.EmptyBorder;
import java.awt.*;

class HeaderPanel extends JPanel {

    private final ImageIcon icon;

    HeaderPanel(ImageIcon icon,
                String title,
                String help1,
                String help2) {
        super(new BorderLayout());

        this.icon = icon;

        var titlesPanel = new JPanel(new GridLayout(3, 1));
        titlesPanel.setOpaque(false);
        titlesPanel.setBorder(new EmptyBorder(12, 0, 12, 0));

        var headerTitle = new JLabel(title);
        var police = headerTitle.getFont().deriveFont(Font.BOLD);
        headerTitle.setFont(police);
        headerTitle.setBorder(new EmptyBorder(0, 12, 0, 0));
        titlesPanel.add(headerTitle);

        JLabel message;

        titlesPanel.add(message = new JLabel(help1));
        police = headerTitle.getFont().deriveFont(Font.PLAIN);
        message.setFont(police);
        message.setBorder(new EmptyBorder(0, 24, 0, 0));

        titlesPanel.add(message = new JLabel(help2));
        police = headerTitle.getFont().deriveFont(Font.PLAIN);
        message.setFont(police);
        message.setBorder(new EmptyBorder(0, 24, 0, 0));

        message = new JLabel(this.icon);
        message.setBorder(new EmptyBorder(0, 0, 0, 12));

        add(BorderLayout.WEST, titlesPanel);
        add(BorderLayout.EAST, message);
        add(BorderLayout.SOUTH, new JSeparator(JSeparator.HORIZONTAL));

        setPreferredSize(new Dimension(500, this.icon.getIconHeight() + 24));
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (!isOpaque()) {
            return;
        }

        var bounds = g.getClipBounds();

        var control = UIManager.getColor("control");
        var width = getWidth();

        var g2 = (Graphics2D) g;
        var storedPaint = g2.getPaint();
        g2.setPaint(new GradientPaint(this.icon.getIconWidth(), 0, Color.white, width, 0, control));
        g2.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
        g2.setPaint(storedPaint);
    }
}
