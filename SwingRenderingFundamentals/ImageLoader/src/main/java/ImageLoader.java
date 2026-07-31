import javax.swing.*;
import java.awt.*;

public class ImageLoader extends JFrame {
    private final JTextArea log;
    private final JPanel viewer;

    public ImageLoader() {
        super("Image Loader");

        this.log = new JTextArea(4, 4);
        this.viewer = new JPanel();

        var start = new JButton("Start");
        start.addActionListener(e -> {
            var files = new String[]{
                    "Bodie_small.png", "Carmela_small.png",
                    "Unknown.png", "Denied.png",
                    "Death Valley_small.png", "Lake_small.png"
            };
            new ImageLoadingWorker(log, viewer, files).execute();
        });

        add(new JScrollPane(log), BorderLayout.NORTH);
        add(new JScrollPane(viewer), BorderLayout.CENTER);
        add(start, BorderLayout.SOUTH);

        setSize(360, 280);
    }

    static void main(String... args) {
        SwingUtilities.invokeLater(() -> new ImageLoader().setVisible(true));
    }
}