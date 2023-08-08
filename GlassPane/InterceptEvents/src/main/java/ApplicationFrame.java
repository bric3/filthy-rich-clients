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

/**
 *
 * @author Romain Guy
 */
public class ApplicationFrame extends javax.swing.JFrame {
    private static final int MAX_DELAY = 300;
    
    private ProgressGlassPane glassPane;
    
    public ApplicationFrame() {
        initComponents();
        setGlassPane(glassPane = new ProgressGlassPane());
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     *
     * This code has been converted from Netbeans/Jdesktop code
     * that made it to Swing's GroupLayout code in Java 1.6.
     * It is not anymore generated.
     */
    private void initComponents() {
        javax.swing.JButton buttonDownload;
        javax.swing.JLabel jLabel1;
        javax.swing.JScrollPane jScrollPane1;

        jLabel1 = new javax.swing.JLabel();
        buttonDownload = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        filesTable = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Glass Pane Painting");
        setResizable(false);
        jLabel1.setText("Pick a file for download");

        buttonDownload.setText("Start Download");
        buttonDownload.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonDownloadActionPerformed(evt);
            }
        });

        filesTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"aerith.png", "/www/progx/images/", "PNG", "5/17/2006"},
                {"blog.html", "/www/progx", "HTML", "3/1/2006"},
                {"index.html", "/www/progx", "HTML", "9/12/2006"},
                {"pictures.zip", "/www/progx", "ZIP", "10/8/2006"}
            },
            new String [] {
                "Name", "Path", "Type", "Date"
            }
        ));
        jScrollPane1.setViewportView(filesTable);

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(buttonDownload))
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                            .addContainerGap()
                            .addComponent(jLabel1))
                        .addGroup(layout.createSequentialGroup()
                            .addContainerGap()
                            .addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 513, Short.MAX_VALUE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jLabel1)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, GroupLayout.PREFERRED_SIZE, 275, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonDownload)
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-553)/2, (screenSize.height-394)/2, 553, 394);
    }

    private void buttonDownloadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonDownloadActionPerformed
        getGlassPane().setVisible(true);
        startDownloadThread();
    }//GEN-LAST:event_buttonDownloadActionPerformed
    
    private void startDownloadThread() {
        Thread downloader = new Thread(new Runnable() {
            public void run() {
                int i = 0;
                do {
                    try {
                        Thread.sleep(30 + (int) (Math.random() * MAX_DELAY));
                    } catch (InterruptedException ex) {
                        // who cares here?
                    }
                    i += (int) (Math.random() * 5);
                    glassPane.setProgress(i);
                } while (i < 100);
                glassPane.setVisible(false);
                glassPane.setProgress(0);
            }
        });
        downloader.start();
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ApplicationFrame().setVisible(true);
            }
        });
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable filesTable;
    // End of variables declaration//GEN-END:variables
    
}
