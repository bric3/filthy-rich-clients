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
 * @author Romain Guy
 */
public class ApplicationFrame extends JFrame {
    public ApplicationFrame() {
        initComponents();
        setGlassPane(new WatermarkGlassPane());
        getGlassPane().setVisible(true);
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * <p>
     * This code has been converted from Netbeans/Jdesktop code
     * that made it to Swing's GroupLayout code in Java 1.6.
     * It is not anymore generated.
     */
    private void initComponents() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("Glass Pane and Mouse Cursor");


        var jList = new JList<String>();
        jList.setModel(new AbstractListModel<>() {
            final String[] strings = {"Jean-Baptiste Freymann", "Nicolas Igot", "Matthieu Grandvallet", "Frédéric Hofmann"};

            public int getSize() {
                return strings.length;
            }

            public String getElementAt(int i) {
                return strings[i];
            }
        });
        jList.setSelectedIndex(0);
        var listScrollPane1 = new JScrollPane(jList);

        var jLabel1 = new JLabel();
        jLabel1.setText("First Name");

        var jLabel2 = new JLabel();
        jLabel2.setText("Last Name");

        var jLabel3 = new JLabel();
        jLabel3.setText("Phone");

        var jLabel4 = new JLabel();
        jLabel4.setText("Email");

        var jLabel5 = new JLabel();
        jLabel5.setText("Address");

        var jTextField1 = new JTextField();
        jTextField1.setText("Jean-Baptiste");

        var jTextField2 = new JTextField();
        jTextField2.setText("Freymann");

        var jTextField3 = new JTextField();
        jTextField3.setText("(555) 462-1492");

        var jTextField4 = new JTextField();
        jTextField4.setText("roukin@acme.com");

        var jTextArea = new JTextArea();
        jTextArea.setColumns(15);
        jTextArea.setRows(5);
        jTextArea.setText("462 Park View Drive\n54999 Santa Clara, CA\nUSA");
        var textAreaScrollPane = new JScrollPane(jTextArea);

        var layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(listScrollPane1, GroupLayout.PREFERRED_SIZE, 152, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel5, GroupLayout.Alignment.TRAILING)
                                        .addComponent(jLabel4, GroupLayout.Alignment.TRAILING)
                                        .addComponent(jLabel3, GroupLayout.Alignment.TRAILING)
                                        .addComponent(jLabel2, GroupLayout.Alignment.TRAILING)
                                        .addComponent(jLabel1, GroupLayout.Alignment.TRAILING))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                        .addComponent(jTextField1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jTextField2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jTextField3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jTextField4, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(textAreaScrollPane, GroupLayout.PREFERRED_SIZE, 209, GroupLayout.PREFERRED_SIZE))
                                .addContainerGap())
        );

        layout.linkSize(SwingConstants.HORIZONTAL, textAreaScrollPane, jTextField1, jTextField2, jTextField3, jTextField4);

        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                        .addComponent(jLabel1)
                                                        .addComponent(jTextField1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                        .addComponent(jLabel2)
                                                        .addComponent(jTextField2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                        .addComponent(jLabel3)
                                                        .addComponent(jTextField3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                        .addComponent(jLabel4)
                                                        .addComponent(jTextField4, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                        .addComponent(jLabel5)
                                                        .addComponent(textAreaScrollPane, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
                                        .addComponent(listScrollPane1, GroupLayout.DEFAULT_SIZE, 210, Short.MAX_VALUE))
                                .addContainerGap())
        );
        var screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width - 489) / 2, (screenSize.height - 266) / 2, 489, 266);
    }

    /**
     * @param args the command line arguments
     */
    static void main(String... args) {
        EventQueue.invokeLater(() -> new ApplicationFrame().setVisible(true));
    }
}
