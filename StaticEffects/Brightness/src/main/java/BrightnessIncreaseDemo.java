import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.IOException;

/**
 *
 * @author Romain Guy <romain.guy@mac.com>
 */
public class BrightnessIncreaseDemo extends JFrame {
    private final JLabel textLabel;
    private final JLabel imageLabel;

    private BufferedImage image;

    public BrightnessIncreaseDemo() {
        super("Brightness Increase");

        getContentPane().setLayout(new FlowLayout());

        textLabel = new JLabel("Hover Me");
        textLabel.setForeground(new Color(0, 0, 120));
        textLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                increaseTextBrightness((JComponent) e.getSource());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                decreaseTextBrightness((JComponent) e.getSource());
            }
        });
        add(textLabel);

        try {
            image = GraphicsUtilities.loadCompatibleImage(getClass().getResource("./icon.png"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        imageLabel = new JLabel(new ImageIcon(image));
        imageLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                increaseImageBrightness((JLabel) e.getSource(), image);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                decreaseImageBrightness((JLabel) e.getSource(), image);
            }
        });
        add(imageLabel);

        pack();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void increaseImageBrightness(JLabel c, BufferedImage image) {
        var factors = new float[]{
                1.4f, 1.4f, 1.4f, 1.4f
        };
        var offsets = new float[]{
                0.0f, 0.0f, 0.0f, 0.0f
        };
        var op = new RescaleOp(factors, offsets, null);
        var brighter = op.filter(image, null);
        c.setIcon(new ImageIcon(brighter));
    }

    public static void decreaseImageBrightness(JLabel c, BufferedImage image) {
        c.setIcon(new ImageIcon(image));
    }

    public static void increaseTextBrightness(JComponent c) {
        var color = c.getForeground();
        c.putClientProperty("mouseover_brightness", color);
        var hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);

        hsb[2] = Math.min(1.0f, hsb[2] * 2.0f);
        c.setForeground(Color.getHSBColor(hsb[0], hsb[1], hsb[2]));
    }

    public static void decreaseTextBrightness(JComponent c) {
        var color = (Color) c.getClientProperty("mouseover_brightness");
        c.setForeground(color);
    }

    static void main(String... args) {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (UnsupportedLookAndFeelException | InstantiationException | IllegalAccessException |
                 ClassNotFoundException ex) {
            ex.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> new BrightnessIncreaseDemo().setVisible(true));
    }
}
