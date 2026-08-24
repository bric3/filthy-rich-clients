import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

// Final result is a list of Image
// Intermediate result is a message as a String
public class ImageLoadingWorker extends SwingWorker<List<Image>, String> {
    private final JTextArea log;
    private final JPanel viewer;
    private final String[] filenames;

    public ImageLoadingWorker(JTextArea log, JPanel viewer, String... filenames) {
        this.log = log;
        this.viewer = viewer;
        this.filenames = filenames;
    }

    // In the EDT
    @Override
    protected void done() {
        try {
            for (var image : get()) {
                viewer.add(new JLabel(new ImageIcon(image)));
                viewer.revalidate();
            }
        } catch (InterruptedException _) {
            Thread.currentThread().interrupt();
            log.append("Image loading interrupted\n");
        } catch (CancellationException _) {
            log.append("Image loading cancelled\n");
        } catch (ExecutionException e) {
            log.append("Image loading failed: " + e.getCause() + "\n");
        }
    }

    // In the EDT
    @Override
    protected void process(List<String> messages) {
        for (var message : messages) {
            log.append(message);
            log.append("\n");
        }
    }

    // In a thread
    @Override
    public List<Image> doInBackground() {
        List<Image> images = new ArrayList<>();
        for (var filename : filenames) {
            try {
                images.add(ImageIO.read(getClass().getClassLoader().getResource(filename)));
                publish("Loaded " + filename);
            } catch (Exception e) {
                publish("Error loading " + filename);
            }
        }
        return images;
    }
}
