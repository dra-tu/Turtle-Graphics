import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Controller extends JPanel implements ActionListener, DocumentListener {
    private static final String START = "Start";
    private static final String CLEAR = "Clear";
    private static final String DEFAULT = "default";

    private final JTextPane inputArea;
    private final TurtelCommands turtel;
    private final StyledDocument doc;

    public Controller(int width, int height, TurtelCommands turtel) {
        this.turtel = turtel;

        inputArea = new JTextPane();
        doc = inputArea.getStyledDocument();
        int tapWidth = inputArea.getFontMetrics( inputArea.getFont() ).charWidth(' ') * 4;
        Style defStyle = doc.getStyle(DEFAULT);
        StyleConstants.setTabSet(defStyle, new TabSet(new TabStop[]{
                new TabStop(tapWidth),
        }));
        doc.addDocumentListener(this);
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
                turtel.executeCommands(inputArea.getText());
                break;
            case CLEAR:
                inputArea.setText("");
                break;
        }
    }

    private void highlight() {
        String text = inputArea.getText().replace("\n", "");
        SwingUtilities.invokeLater(() -> {
            doc.setCharacterAttributes(0, text.length(), doc.getStyle(DEFAULT), true);

            int wordStard = 0;
            int wordEnd = 0;
            for (int i = 0; i < text.length(); i++) {
                wordEnd++;

                if (!Character.isWhitespace(text.charAt(i)) && i != text.length() - 1) continue;
                if (wordStard == wordEnd) {
                    wordStard = i + 1;
                    wordEnd = i + 1;
                }
                if (wordEnd != text.length()) {
                    wordEnd--;
                } else if (Character.isWhitespace(text.charAt(wordEnd-1))) {
                    wordEnd--;
                }

                String word = text.substring(wordStard, wordEnd);
                AttributeSet set = SpecialWords.getStyle(word);
                set = set != null ? set : doc.getStyle(DEFAULT);

                doc.setCharacterAttributes(wordStard, wordEnd - wordStard, set, true);

                wordStard = i + 1;
                wordEnd = i + 1;
            }
        });
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        highlight();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        highlight();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
    }
}
