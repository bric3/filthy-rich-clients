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

/**
 *
 * @author Romain Guy <romain.guy@mac.com>
 */
public class ApplicationFrame extends JFrame {
    private final JLayeredPane layeredPane;
    private Loupe loupe;

    public ApplicationFrame() {
        super("Layered Pane Layout");

        layeredPane = new JLayeredPane();

        addLayersControl();
        loadImagesInLayers();
        createLoupe();

        setSize(540, 350);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ApplicationFrame().setVisible(true));
    }

    private void loadImagesInLayers() {
        layeredPane.setLayout(new FlowLayout());

        for (var i = 2; i <= 5; i++) {
            var name = "images/photo" + i + ".jpg";
            var url = getClass().getResource(name);
            Icon icon = new ImageIcon(url);
            var label = new JLabel(icon);

            layeredPane.add(label,
                    (Integer) (JLayeredPane.DEFAULT_LAYER + (i - 1) * 2));
        }

        add(layeredPane);
    }

    private void addLayersControl() {
        var panel = new JPanel(new FlowLayout(FlowLayout.LEADING));

        // noinspection unchecked
        var layerSelection = new JComboBox<>(new String[]{
                "Layer 0", "Layer 1", "Layer 2", "Layer 3", "Layer 4"
        });
        layerSelection.addActionListener(actionEvent -> {
            @SuppressWarnings("unchecked") var layerSelection1 = (JComboBox<String>) actionEvent.getSource();
            var layerId = layerSelection1.getSelectedIndex();
            layeredPane.setLayer(loupe,
                    JLayeredPane.DEFAULT_LAYER + layerId * 2 + 1);
        });
        panel.add(new JLabel("Loupe Layer: "));
        panel.add(layerSelection);

        var zoomSelection = new JSlider(1, 16, 2);
        zoomSelection.setPaintTicks(true);
        zoomSelection.setSnapToTicks(true);
        zoomSelection.setPaintLabels(true);
        zoomSelection.addChangeListener(changeEvent -> {
            var zoomSelection1 = (JSlider) changeEvent.getSource();
            loupe.setZoomLevel(zoomSelection1.getValue());
        });

        panel.add(Box.createHorizontalStrut(24));
        panel.add(new JLabel("Zoom: "));
        panel.add(new JLabel("1"));
        panel.add(zoomSelection);
        panel.add(new JLabel("16"));

        add(panel, BorderLayout.NORTH);
    }

    private void createLoupe() {
        loupe = new Loupe(layeredPane);
        var size = loupe.getPreferredSize();
        layeredPane.add(loupe,
                (Integer) (JLayeredPane.DEFAULT_LAYER + 1));
    }
}
