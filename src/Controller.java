import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Controller extends JPanel implements ActionListener {
    private static final String START = "Start";
    private static final String CLEAR = "Clear";

    private final JTextArea inputArea;
    private final TurtelPanel turtelPanel;

    public Controller(int width, int height, TurtelPanel turtelPanel) {
        this.turtelPanel = turtelPanel;

        inputArea = new JTextArea();
        inputArea.setBorder(BorderFactory.createLineBorder(Color.RED));

        JButton startButton = new JButton(START);
        JButton clearButton = new JButton(CLEAR);
        startButton.addActionListener(this);
        clearButton.addActionListener(this);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.add(startButton);
        buttonPanel.add(clearButton);

        JScrollPane scroll = new JScrollPane(inputArea);
        scroll.setPreferredSize(new Dimension(width, height));

        this.add(scroll);
        this.add(buttonPanel);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton button = (JButton) e.getSource();
        switch (button.getText()) {
            case START:
                turtelPanel.executeCommands(inputArea.getText());
                break;
            case CLEAR:
                inputArea.setText("");
                break;
        }
    }
}
