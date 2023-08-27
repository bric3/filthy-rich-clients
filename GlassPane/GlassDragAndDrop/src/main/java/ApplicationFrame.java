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

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.InvalidDnDOperationException;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Access the drag-and-drop content to show a preview of the dragged image.
 *
 * Note this demo does not work on macOs X (using Ventura 13.5.1 and JDK 20).
 * AS the drag-and-drop feature does not allow accessing the content during the drag.
 *
 * @author Romain Guy
 */
public class ApplicationFrame extends javax.swing.JFrame {
    private PictureGlassPane glassPane = new PictureGlassPane();
    
    public ApplicationFrame() {
        setContentPane(new JPanel(new BorderLayout()) {
            private BufferedImage image = null;
            
            @Override
            public boolean isOpaque() {
                return imageList.getModel().getSize() > 0;
            }
            
            @Override
            protected void paintComponent(Graphics g) {
                Rectangle clip = g.getClipBounds();
                
                g.setColor(Color.WHITE);
                g.fillRect(clip.x, clip.y, clip.width, clip.height);
                
                if (image == null) {
                    try {
                        image = ImageIO.read(getClass().getResource("images/drop-here.png"));
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    }
                }
                
                g.drawImage(image, (getWidth() - image.getWidth()) / 2,
                        (getHeight() - image.getHeight()) / 2, null);
            }
        });
        
        initComponents();
        
        imageList.setOpaque(false);
        listScroller.setOpaque(false);
        listScroller.getViewport().setOpaque(false);
        
        setGlassPane(glassPane);
        imageList.setModel(new DefaultListModel());
        imageList.setTransferHandler(new FileDropHandler());
        imageList.addPropertyChangeListener("dropLocation", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                glassPane.moveIt(MouseInfo.getPointerInfo().getLocation());
            }
        });
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     *
     * This code has been converted from Netbeans/Jdesktop code
     * that made it to Swing's GroupLayout code in Java 1.6.
     * It is not anymore generated.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        javax.swing.JLabel jLabel1;
        javax.swing.JLabel jLabel2;
        javax.swing.JLabel jLabel3;
        javax.swing.JPanel jPanel1;
        javax.swing.JSeparator jSeparator1;

        listScroller = new javax.swing.JScrollPane();
        imageList = new javax.swing.JList();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Glass Drag and Drop");
        imageList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        imageList.setLayoutOrientation(javax.swing.JList.HORIZONTAL_WRAP);
        imageList.setVisibleRowCount(-1);
        listScroller.setViewportView(imageList);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/app-icon.png")));

        jLabel2.setFont(jLabel2.getFont().deriveFont(jLabel2.getFont().getStyle() | java.awt.Font.BOLD, jLabel2.getFont().getSize()+2));
        jLabel2.setText("Glass Drag and Drop");

        jLabel3.setText("Drag and drop image files (PNG or JPEG) onto the application.");

        GroupLayout jPanel1Layout = new GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3))
                .addContainerGap(158, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel3))
                    .addComponent(jLabel1))
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jSeparator1, GroupLayout.DEFAULT_SIZE, 618, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(listScroller, GroupLayout.DEFAULT_SIZE, 578, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jSeparator1, GroupLayout.PREFERRED_SIZE, 10, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(listScroller, GroupLayout.DEFAULT_SIZE, 372, Short.MAX_VALUE)
                .addContainerGap())
        );
        pack();
        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        java.awt.Dimension dialogSize = getSize();
        setLocation((screenSize.width-dialogSize.width)/2,(screenSize.height-dialogSize.height)/2);
    }// </editor-fold>//GEN-END:initComponents

    class FileDropHandler extends TransferHandler {
        private boolean imported = false;
        private List<File> fileList;
        
        public boolean canImport(TransferSupport support) {
            if (!support.isDrop()) {
                return false;
            }

            if (!support.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                return false;
            }

            boolean copySupported = (COPY & support.getSourceDropActions()) == COPY;

            if (copySupported) {
                support.setDropAction(COPY);

                if (!imported) {
                    Transferable t = support.getTransferable();

                    try {
                        // Exception in thread "AWT-EventQueue-0" java.awt.dnd.InvalidDnDOperationException: No drop current
                        //      at java.desktop/sun.awt.dnd.SunDropTargetContextPeer.getTransferData(SunDropTargetContextPeer.java:248)
                        //      at java.desktop/sun.awt.datatransfer.TransferableProxy.getTransferData(TransferableProxy.java:73)
                        //      at java.desktop/java.awt.dnd.DropTargetContext$TransferableProxy.getTransferData(DropTargetContext.java:387)
                        //      at ApplicationFrame$FileDropHandler.canImport(ApplicationFrame.java:222)
                        //      at java.desktop/javax.swing.TransferHandler$DropHandler.drop(TransferHandler.java:1530)
                        //      at java.desktop/java.awt.dnd.DropTarget.drop(DropTarget.java:455)
                        //      at java.desktop/javax.swing.TransferHandler$SwingDropTarget.drop(TransferHandler.java:1282)
                        //
                        // The DropTargetDropEvent only allows access to the transferable after acceptDrop has been called
                        // but Swing only calls this method after canImport returned true. This restriction may seem weird
                        // as you can access the transferable during the drag, but the possibility to access it during
                        // the drag is a newer addition (Java 5). Before that, the restriction may have looked reasonable.
                        //
                        // Also, this call can return null on macOs during drag.
                        Object data = t.getTransferData(DataFlavor.javaFileListFlavor);
                        this.fileList = (List<File>) data;
                        BufferedImage image = createImage(fileList);
                        
                        if (image != null) {
                            Point p = MouseInfo.getPointerInfo().getLocation();

                            glassPane.showIt(image, p);
                            imported = true;
                        }
                    } catch (UnsupportedFlavorException | IOException e) {
                        return false;
                    } catch (InvalidDnDOperationException ignored) {
                        // Since accessing the transferable at the moment the drop is getting released,
                        // Swing raises this exception, it's necessary to ignore it and to return true
                        // to be able to import the data, otherwise the drag-and-drop gets rejected.
                        return true;
                    }
                }

                return true;
            }
            
            return false;
        }
                
        private BufferedImage createImage(List<File> files) {
            if (files == null || files.isEmpty()) {
                return null;
            }
            List<File> imageFiles = new ArrayList<File>(3);
            for (File file : files) {
                if (file.getName().endsWith(".png") ||
                    file.getName().endsWith(".jpg")) {
                    imageFiles.add(file);
                    if (imageFiles.size() == 3) {
                        break;
                    }
                }
            }

            int width = 60 + (imageFiles.size() - 1) * 10;
            int height = 45 + (imageFiles.size() - 1) * 10;
            
            BufferedImage image =
                    GraphicsUtilities.createCompatibleTranslucentImage(width, height);
            Graphics2D g2 = image.createGraphics();
            
            for (int i = 0; i < imageFiles.size(); i++) {
                File imageFile = imageFiles.get(i);
                BufferedImage externalImage = null;
                try {
                    externalImage = GraphicsUtilities.loadCompatibleImage(imageFile.toURI().toURL());
                } catch (MalformedURLException ex) {
                    ex.printStackTrace();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                externalImage = GraphicsUtilities.createThumbnail(externalImage, 60, 45);
                g2.drawImage(externalImage, i * 10, i * 10, null);
            }
            
            g2.dispose();
            return image;
        }
        
        public boolean importData(TransferSupport support) {
            if (!canImport(support)) {
                return false;
            }
            
            if (!support.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                return false;
            }
            
            for (int i = 0; i < fileList.size(); i++) {
                File imageFile = fileList.get(i);
                BufferedImage externalImage = null;
                try {
                    externalImage = GraphicsUtilities.loadCompatibleImage(imageFile.toURI().toURL());
                    externalImage = GraphicsUtilities.createThumbnail(externalImage, 120);
                } catch (MalformedURLException ex) {
                    ex.printStackTrace();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                ((DefaultListModel) imageList.getModel()).add(0, new ImageIcon(externalImage));
            }

            glassPane.hideIt();
            imported = false;

            return true;
        }
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
    private javax.swing.JList imageList;
    private javax.swing.JScrollPane listScroller;
    // End of variables declaration//GEN-END:variables
    
}
