import javax.swing.*;
import java.awt.Dimension;
import java.awt.Color;

public class Controller extends JPanel {
    public Controller() {
        JTextArea inputArea = new JTextArea();
        inputArea.setPreferredSize(new Dimension(500, 50));
        inputArea.setBorder(BorderFactory.createLineBorder(Color.RED));
        this.add(inputArea);
    }
}
