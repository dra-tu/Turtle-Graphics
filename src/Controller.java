import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Controller extends JPanel implements ActionListener, DocumentListener, ChangeListener {
    private static final String START = "Start";
    private static final String CLEAR = "Clear";
    private static final String DEFAULT = "default";

    private final JTextPane inputArea;
    private final TurtelCommands turtelCom;
    private final TurtelAnimationControl turtelAnim;
    private final StyledDocument doc;

    private final JSpinner spinnerFPS;
    private final JSpinner spinnerStep;

    public Controller(int width, Turtel turtel) {
        this.turtelCom = new TurtelCommands(turtel);
        this.turtelAnim = new TurtelAnimationControl(turtel);

        // input Area
        inputArea = new JTextPane();
        doc = inputArea.getStyledDocument();
        int tapWidth = inputArea.getFontMetrics( inputArea.getFont() ).charWidth(' ') * 4;
        Style defStyle = doc.getStyle(DEFAULT);
        StyleConstants.setTabSet(defStyle, new TabSet(new TabStop[]{
                new TabStop(tapWidth),
        }));
        doc.addDocumentListener(this);
        inputArea.setBorder(BorderFactory.createLineBorder(Color.RED));
        JScrollPane scroll = new JScrollPane(inputArea);

        // Buttons
        JButton startButton = new JButton(START);
        JButton clearButton = new JButton(CLEAR);
        startButton.addActionListener(this);
        clearButton.addActionListener(this);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(startButton);
        buttonPanel.add(clearButton);
        Dimension d = startButton.getSize();
        d.width = width;
        buttonPanel.setMaximumSize(d);
        buttonPanel.setBorder(BorderFactory.createLineBorder(Color.BLUE));

        // Number input
        SpinnerNumberModel modelFPS = new SpinnerNumberModel(turtel.targetFPS, 1, Integer.MAX_VALUE, 1);
        SpinnerNumberModel modelStep = new SpinnerNumberModel(turtel.stepLength, 1, Integer.MAX_VALUE, 1);
        spinnerFPS = new JSpinner(modelFPS);
        spinnerStep = new JSpinner(modelStep);
        spinnerFPS.addChangeListener(this);
        spinnerStep.addChangeListener(this);

        JPanel spinnerPanel = new JPanel();
        spinnerPanel.add(spinnerFPS);
        spinnerPanel.add(spinnerStep);
        d = new Dimension(width, 75);
        spinnerPanel.setMaximumSize(d);
        spinnerPanel.setMinimumSize(d);
        spinnerPanel.setLayout(new BoxLayout(spinnerPanel, BoxLayout.X_AXIS));

        // finish
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.add(buttonPanel);
        this.add(spinnerPanel);
        this.add(scroll);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton button = (JButton) e.getSource();
        switch (button.getText()) {
            case START:
                turtelCom.executeCommands(inputArea.getText());
                break;
            case CLEAR:
                inputArea.setText("");
                break;
        }
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        JSpinner sp = (JSpinner) e.getSource();

        Number num = (Number) sp.getValue();

        if (sp == spinnerFPS) {
            turtelAnim.setTargetFPS(num.doubleValue());
        } else if (sp == spinnerStep) {
            turtelAnim.setStepLength(num.longValue());
        }
    }

    private void highlight() {
        String text = inputArea.getText().replace("\n", "");
        SwingUtilities.invokeLater(() -> {
            doc.setCharacterAttributes(0, text.length(), doc.getStyle(DEFAULT), true);

            int wordStart = 0;
            int wordEnd = 0;
            for (int i = 0; i < text.length(); i++) {
                wordEnd++;

                if (!Character.isWhitespace(text.charAt(i)) && i != text.length() - 1) continue;
                if (wordStart == wordEnd) {
                    wordStart = i + 1;
                    wordEnd = i + 1;
                }
                if (wordEnd != text.length()) {
                    wordEnd--;
                } else if (Character.isWhitespace(text.charAt(wordEnd-1))) {
                    wordEnd--;
                }

                String word = text.substring(wordStart, wordEnd);
                AttributeSet set = SpecialWords.getStyle(word);
                set = set != null ? set : doc.getStyle(DEFAULT);

                doc.setCharacterAttributes(wordStart, wordEnd - wordStart, set, true);

                wordStart = i + 1;
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
