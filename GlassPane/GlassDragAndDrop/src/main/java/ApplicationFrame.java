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
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.InvalidDnDOperationException;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/// Access the drag-and-drop content to show a preview of the dragged image.
///
/// Note this demo does not work on macOs X (using Ventura 13.5.1 and JDK 20).
/// AS the drag-and-drop feature does not allow accessing the content during the drag.
///
/// @author Romain Guy
public class ApplicationFrame extends JFrame {
    private final PictureGlassPane glassPane = new PictureGlassPane();

    public ApplicationFrame() {
        setContentPane(new JPanel(new BorderLayout()) {
            private BufferedImage image = null;

            @Override
            public boolean isOpaque() {
                return imageList.getModel().getSize() > 0;
            }

            @Override
            protected void paintComponent(Graphics g) {
                var clip = g.getClipBounds();

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
        // noinspection unchecked
        imageList.setModel(new DefaultListModel<>());
        var fileDropHandler = new FileDropHandler();
        imageList.setTransferHandler(fileDropHandler);
        imageList.addPropertyChangeListener("dropLocation", event -> {
            if (event.getNewValue() == null) {
                fileDropHandler.reset();
            } else {
                var pointerInfo = MouseInfo.getPointerInfo();
                if (pointerInfo != null) {
                    glassPane.moveIt(pointerInfo.getLocation());
                }
            }
        });
    }

    /// This method is called from within the constructor to
    /// initialize the form.
    ///
    /// This code has been converted from Netbeans/Jdesktop code
    /// that made it to Swing's GroupLayout code in Java 1.6.
    /// It is not anymore generated.
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        JLabel jLabel1;
        JLabel jLabel2;
        JLabel jLabel3;
        JPanel jPanel1;
        JSeparator jSeparator1;

        listScroller = new JScrollPane();
        imageList = new JList<>();
        jPanel1 = new JPanel();
        jLabel1 = new JLabel();
        jLabel2 = new JLabel();
        jLabel3 = new JLabel();
        jSeparator1 = new JSeparator();

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("Glass Drag and Drop");
        imageList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        imageList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        imageList.setVisibleRowCount(-1);
        listScroller.setViewportView(imageList);

        jPanel1.setBackground(new Color(255, 255, 255));
        jLabel1.setIcon(new ImageIcon(getClass().getResource("/images/app-icon.png")));

        jLabel2.setFont(jLabel2.getFont().deriveFont(jLabel2.getFont().getStyle() | Font.BOLD, jLabel2.getFont().getSize() + 2));
        jLabel2.setText("Glass Drag and Drop");

        jLabel3.setText("Drag and drop image files (PNG or JPEG) onto the application.");

        var jPanel1Layout = new GroupLayout(jPanel1);
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

        var layout = new GroupLayout(getContentPane());
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
        var screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        var dialogSize = getSize();
        setLocation((screenSize.width - dialogSize.width) / 2, (screenSize.height - dialogSize.height) / 2);
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

            var copySupported = (COPY & support.getSourceDropActions()) == COPY;

            if (copySupported) {
                support.setDropAction(COPY);

                if (!imported) {
                    var t = support.getTransferable();

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
                        // Also, this call can return null on macOS during drag.
                        var data = t.getTransferData(DataFlavor.javaFileListFlavor);
                        // noinspection unchecked
                        this.fileList = (List<File>) data;
                        var image = createImage(fileList);

                        if (image != null) {
                            var p = MouseInfo.getPointerInfo().getLocation();

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
            List<BufferedImage> thumbnails = new ArrayList<>(3);
            for (var file : files) {
                if (!isSupportedImage(file)) {
                    continue;
                }

                GraphicsUtilities.loadCompatibleImage(file)
                        .map(image -> GraphicsUtilities.createThumbnail(image, 60, 45))
                        .ifPresent(thumbnails::add);

                if (thumbnails.size() == 3) {
                    break;
                }
            }

            if (thumbnails.isEmpty()) {
                return null;
            }

            var width = 60 + (thumbnails.size() - 1) * 10;
            var height = 45 + (thumbnails.size() - 1) * 10;

            var image =
                    GraphicsUtilities.createCompatibleTranslucentImage(width, height);
            var g2 = image.createGraphics();

            for (var i = 0; i < thumbnails.size(); i++) {
                g2.drawImage(thumbnails.get(i), i * 10, i * 10, null);
            }

            g2.dispose();
            return image;
        }

        private boolean isSupportedImage(File file) {
            var name = file.getName().toLowerCase(Locale.ROOT);
            return name.endsWith(".png") ||
                   name.endsWith(".jpg") ||
                   name.endsWith(".jpeg");
        }

        public boolean importData(TransferSupport support) {
            try {
                if (!canImport(support)) {
                    return false;
                }

                if (fileList == null ||
                    !support.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                    return false;
                }

                var importedAny = false;
                for (var imageFile : fileList) {
                    if (!isSupportedImage(imageFile)) {
                        continue;
                    }

                    var image = GraphicsUtilities.loadCompatibleImage(imageFile)
                            .map(source -> GraphicsUtilities.createThumbnail(source, 120));
                    if (image.isPresent()) {
                        // noinspection unchecked
                        ((DefaultListModel<ImageIcon>) imageList.getModel()).add(
                                0, new ImageIcon(image.get()));
                        importedAny = true;
                    }
                }

                return importedAny;
            } finally {
                reset();
            }
        }

        private void reset() {
            glassPane.hideIt();
            imported = false;
            fileList = null;
        }
    }

    /// @param args the command line arguments
    static void main(String[] args) {
        EventQueue.invokeLater(() -> new ApplicationFrame().setVisible(true));
    }

    private JList<ImageIcon> imageList;
    private JScrollPane listScroller;
}
