import javax.swing.*;
import java.awt.*;

public class Controller extends JPanel {
    public Controller(int width, int height) {
        JTextArea inputArea = new JTextArea();
        inputArea.setPreferredSize(new Dimension(width, height));
        inputArea.setBorder(BorderFactory.createLineBorder(Color.RED));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.add(new JButton("Start"));
        buttonPanel.add(new JButton("Clear"));

        this.add(inputArea);
        this.add(buttonPanel);
    }
}
