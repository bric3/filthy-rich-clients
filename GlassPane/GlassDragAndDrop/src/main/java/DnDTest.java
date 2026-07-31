import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.*;
import java.io.File;
import java.io.IOException;
import java.util.List;

void main() {
    SwingUtilities.invokeLater(() -> {
        var panel = new JPanel();
        panel.setBackground(Color.WHITE);
        var dropTarget = new DropTarget(panel, new DropTargetImplementation(panel));


        // JPanel panel = new JPanel();
        // panel.setMinimumSize(new Dimension(200, 200));
        // panel.setTransferHandler(new FileDropHandler());


        var frame = new JFrame("Drag and Drop Test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 400);
        frame.setLocationRelativeTo(null);
        frame.getContentPane().add(panel);
        frame.setVisible(true);
    });
}

private static boolean printFiles(String method, Transferable transferable) {
    IO.println(method);
    List<File> files;
    try {
        // noinspection unchecked
        files = (List<File>) transferable.getTransferData(DataFlavor.javaFileListFlavor);
    } catch (InvalidDnDOperationException | UnsupportedFlavorException | IOException ex) {
        ex.printStackTrace();
        // should never happen (or JDK is buggy)
        return true;
    }

    if (files == null) {
        return false;
    }

    for (var file : files) {
        IO.println("    " + file.getAbsolutePath());
    }
    return false;
}

static final class FileDropHandler extends TransferHandler {
    @Override
    public boolean canImport(TransferSupport support) {
        var drop = support.isDrop();
        IO.println("Drop: " + drop);
        if (!drop) {
            return false;
        }

        for (var flavor : support.getDataFlavors()) {
            if (flavor.isFlavorJavaFileListType()) {
                printFiles("canImport", support.getTransferable());
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean importData(TransferSupport support) {
        if (!this.canImport(support))
            return false;

        return !printFiles("importData", support.getTransferable());
    }

}

private static class DropTargetImplementation extends DropTargetAdapter {
    private final JPanel panel;

    public DropTargetImplementation(JPanel panel) {
        this.panel = panel;
    }

    private static void collectDrop(DropTargetDropEvent dtde, String method) {
        var transferable = dtde.getTransferable();
        try {
            if (transferable.isDataFlavorSupported(DataFlavor.imageFlavor)) {
                dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
                var image = (Image) transferable.getTransferData(DataFlavor.imageFlavor);
                dtde.getDropTargetContext().dropComplete(true);
                IO.println(method + ": success image");
            } else if (transferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
                var fileList = printFiles(method, transferable);
                dtde.getDropTargetContext().dropComplete(true);
                IO.println(method + ": success file list");
            } else {
                System.err.println("DROP::That wasn't an image!");
                dtde.rejectDrop();
            }
        } catch (IOException | UnsupportedFlavorException ioe) {
            ioe.printStackTrace();
            dtde.rejectDrop();
        }
    }

    private static void collectDrag(DropTargetDragEvent dtde, String method) {
        var transferable = dtde.getTransferable();
        try {
            if (transferable.isDataFlavorSupported(DataFlavor.imageFlavor)) {
                dtde.acceptDrag(DnDConstants.ACTION_COPY_OR_MOVE);
                var image = (Image) transferable.getTransferData(DataFlavor.imageFlavor);
                // dtde.getDropTargetContext().dropComplete(true);
                IO.println(method + ": success image");
            } else if (transferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                dtde.acceptDrag(DnDConstants.ACTION_COPY_OR_MOVE);
                var fileList = printFiles(method, transferable);
                // dtde.getDropTargetContext().dropComplete(true);
                IO.println(method + ": success file list");
            } else {
                System.err.println("DRAG::That wasn't an image or a file list!");
            }
        } catch (IOException | UnsupportedFlavorException ioe) {
            ioe.printStackTrace();
        }
    }

    public void drop(DropTargetDropEvent e) {
        System.err.println("The DropPanel received the DropEvent");

        collectDrop(e, "drop");
    }

    public void dragEnter(DropTargetDragEvent e) {
        // called when the user is dragging and enters our target
        panel.setBackground(Color.GREEN);

        collectDrag(e, "dragEnter");
    }

    public void dragExit(DropTargetEvent e) {
        // called when the user is dragging and leaves our target
        panel.setBackground(Color.WHITE);
    }

    public void dragOver(DropTargetDragEvent e) {
        // called when the user is dragging and moves over our target
        panel.setBackground(Color.GREEN);

        collectDrag(e, "dragOver");
    }
}
