import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FreezeEDT extends JFrame implements ActionListener {
    public FreezeEDT() {
        super("Freeze");
        var freezer = new JButton("Freeze");
        freezer.addActionListener(this);
        add(freezer);
        pack();
    }

    public void actionPerformed(ActionEvent e) {
        try {
            Thread.sleep(4000);
        } catch (InterruptedException _) {
        }
    }

    static void main(String... args) {
        var edt = new FreezeEDT();
        edt.setVisible(true);
    }
}