import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.InvalidDnDOperationException;
import java.io.File;
import java.io.IOException;
import java.util.List;

/// A test based on Oracle's TopLevelTransferHandlerDemo
///
/// Source :
void main() {
    SwingUtilities.invokeLater(() -> {
        var frame = new JFrame("Simple Drop Test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        var testPanel = new JPanel();
        testPanel.setBackground(Color.white);
        testPanel.setPreferredSize(new Dimension(500, 500));

        frame.add(testPanel, BorderLayout.CENTER);
        frame.setTransferHandler(new MyTransferHandler());

        frame.pack();
        frame.setVisible(true);

    });
}

private static class MyTransferHandler extends TransferHandler {
    private boolean isFileAcceptable(File file) {
        if (file != null) {
            var name = file.getName().toLowerCase();
            return name.endsWith("png")
                   || name.endsWith("gif")
                   || name.endsWith("jpg")
                   || name.endsWith("jpeg");
        }
        return false;
    }

    public boolean canImport(TransferSupport support) {
        var copySupported = (COPY & support.getSourceDropActions()) == COPY;
        if (!copySupported) {
            return false;
        }
        if (!support.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
            IO.println("canImport support.isDataFlavorSupported false");
            return false;
        }

            /*
             We wish to test the content of the transfer data and determine if they are (a) files and (b) files
             we are actually interested in processing. So we need to call getTransferData() so that we can inspect
             the file names. Unfortunately, this will not always work. Under Windows, the Transferable instance
             will have transfer data ONLY while the mouse button is depressed.  However, when the user releases
             the mouse button, this method will be called one last time.  And when this method attempts to
             getTransferData, Java will throw an InvalidDnDOperationException.  Since we know that the exception
             is coming, we simply catch it and ignore it.
             */
        var t = support.getTransferable();

        try {
            var acceptFlag = false;
            @SuppressWarnings("unchecked") var fileList = (List<File>) t.getTransferData(DataFlavor.javaFileListFlavor);
            IO.println("canImport called, about to get transfer data: " + fileList);
            if (fileList != null) {
                for (var f : fileList) {
                    if (isFileAcceptable(f)) {
                        acceptFlag = true;
                        break;
                    }
                }
            }
            if (acceptFlag) {
                support.setDropAction(TransferHandler.COPY);
                return true;
            } else {
                return false;
            }
        } catch (UnsupportedFlavorException | IOException e) {
            return false;
        } catch (InvalidDnDOperationException dontCare) {
            IO.println("InvalidDnDOperationException");
            return true;
        }
    }

    public boolean importData(TransferSupport support) {
        System.err.println("importData called");
        var t = support.getTransferable();
        List<File> fileList;
        try {
            // noinspection unchecked
            fileList = (List<File>) t.getTransferData(DataFlavor.javaFileListFlavor);
            if (fileList != null) {
                for (var f : fileList) {
                    if (isFileAcceptable(f)) {
                        IO.println("  Would Process " + f.getName());
                    }
                }
            }
            return true;
        } catch (UnsupportedFlavorException | IOException ex) {
            ex.printStackTrace(System.err);
        }
        return false;
    }
}