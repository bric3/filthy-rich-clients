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

import uk.co.caprica.vlcj.player.component.CallbackMediaPlayerComponent;

import javax.swing.*;
import java.awt.*;

/**
 *
 * @author Romain Guy <romain.guy@mac.com>
 */
public class RepaintManagerDemo extends JFrame {
    private ReflectionPanel reflectionPanel;
    private CallbackMediaPlayerComponent mediaPlayerComponent;
    private final String media;

    public RepaintManagerDemo(String media) {
        super("Repaint Manager Demo");
        this.media = media;

        setContentPane(new GradientPanel());
        getContentPane().setLayout(new GridBagLayout());

        add(buildReflectionPanel(), new GridBagConstraints(
                0, 0, 1, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(96, 96, 96, 96),
                0, 0
        ));

        pack();
        setLocationRelativeTo(null);
        setResizable(false);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    @Override
    public void dispose() {
        var component = mediaPlayerComponent;
        mediaPlayerComponent = null;
        try {
            if (component != null) {
                component.release();
            }
        } finally {
            super.dispose();
        }
    }

    private JComponent buildReflectedComponent() {
        if (media == null || media.trim().isEmpty()) {
            System.err.println("No media specified; showing the Swing fallback. "
                               + "Pass a media file or URL as the first argument to play video.");
            return new DummyPanel();
        }

        CallbackMediaPlayerComponent component = null;
        try {
            component = new CallbackMediaPlayerComponent();
            if (!component.mediaPlayer().media().play(media)) {
                component.release();
                System.err.println("VLC could not start media '" + media
                                   + "'; showing the Swing fallback.");
                return new DummyPanel();
            }

            mediaPlayerComponent = component;
            return component;
        } catch (RuntimeException | LinkageError e) {
            if (component != null) {
                component.release();
            }
            System.err.println("VLC is unavailable or could not open '" + media
                               + "'; showing the Swing fallback: " + e);
            return new DummyPanel();
        }
    }

    private JComponent buildReflectionPanel() {
        reflectionPanel = new ReflectionPanel();
        reflectionPanel.add(buildReflectedComponent());

        return reflectionPanel;
    }

    private static class GradientPanel extends JPanel {
        GradientPanel() {
            super(new BorderLayout());
        }

        @Override
        protected void paintComponent(Graphics g) {
            var g2 = (Graphics2D) g.create();
            g2.setPaint(new GradientPaint(0.0f, getHeight() * 0.22f,
                    new Color(0x202737),
                    0.0f, getHeight() * 0.7f,
                    Color.BLACK, true));
            Rectangle clip = g.getClipBounds();
            g2.fillRect(clip.x, clip.y, clip.width, clip.height);
            g2.dispose();
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (IllegalAccessException
                 | InstantiationException
                 | UnsupportedLookAndFeelException
                 | ClassNotFoundException ex) {
            ex.printStackTrace();
        }

        final String media = args.length > 0 ? args[0] : null;
        SwingUtilities.invokeLater(() -> new RepaintManagerDemo(media).setVisible(true));
    }
}
