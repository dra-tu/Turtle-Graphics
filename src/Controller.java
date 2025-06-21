import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class Controller extends JPanel implements ActionListener, DocumentListener, ChangeListener {
    private static final String START = "Start";
    private static final String CLEAR = "Clear";
    private static final int WIDTH = 200;
    private static final String DEFAULT = "default";

    private static final Dimension ZERO_DIMENSION = new Dimension(0, 0);

    private final JTextArea errorBox;
    private final JScrollPane scrollError;
    private final JTextPane inputArea;
    private final JLabel parseState;
    private final JLabel drawPercent;
    private final TurtelCommands turtelCom;
    private final TurtelAnimationControl turtelAnim;
    private final StyledDocument doc;

    private final JSpinner spinnerFPS;
    private final JSpinner spinnerStep;

    private boolean textChanged;

    public Controller(Turtel turtel) {
        this.turtelCom = new TurtelCommands(turtel);
        this.turtelAnim = new TurtelAnimationControl(turtel);
        textChanged = false;

        // input Area
        inputArea = new JTextPane();
        doc = inputArea.getStyledDocument();
        int tapWidth = inputArea.getFontMetrics(inputArea.getFont()).charWidth(' ') * 4;
        Style defStyle = doc.getStyle(DEFAULT);
        StyleConstants.setTabSet(defStyle, new TabSet(new TabStop[]{
                new TabStop(tapWidth),
        }));
        doc.addDocumentListener(this);
        inputArea.setBorder(BorderFactory.createLineBorder(Color.RED));
        JScrollPane scrollInput = new JScrollPane(inputArea);

        // Error Box
        errorBox = new JTextArea();
        errorBox.setEditable(false);
        scrollError = new JScrollPane(errorBox);
        scrollError.setMaximumSize(ZERO_DIMENSION);

        // Buttons
        JButton startButton = new JButton(START);
        JButton clearButton = new JButton(CLEAR);
        startButton.addActionListener(this);
        clearButton.addActionListener(this);

        JLabel parseText = new JLabel("Parse State");
        parseState = new JLabel("-,--s");
        parseState.setForeground(Color.GREEN);

        JLabel drawText = new JLabel("Draw Percent");
        drawPercent = new JLabel("--%");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(drawText);
        buttonPanel.add(drawPercent);
        buttonPanel.add(parseText);
        buttonPanel.add(parseState);
        buttonPanel.add(startButton);
        buttonPanel.add(clearButton);
        Dimension d = startButton.getSize();
        d.width = Integer.MAX_VALUE;
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
        spinnerPanel.setLayout(new BoxLayout(spinnerPanel, BoxLayout.X_AXIS));
        Dimension e = spinnerPanel.getSize();
        e.width = Integer.MAX_VALUE;
        spinnerPanel.setMaximumSize(e);

        // finish
        this.setMaximumSize(new Dimension(WIDTH, Integer.MAX_VALUE));
        this.setMinimumSize(new Dimension(WIDTH, 0));
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.add(buttonPanel);
        this.add(spinnerPanel);
        this.add(scrollError);
        this.add(scrollInput);
        setKeyBindings();
    }

    private void setKeyBindings() {
        InputMap im = inputArea.getInputMap(JComponent.WHEN_FOCUSED);
        ActionMap am = inputArea.getActionMap();

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F10, 0), "clearKey");
        am.put("clearKey", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clear();
            }
        });

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0), "startKey");
        am.put("startKey", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                start();
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton button = (JButton) e.getSource();
        switch (button.getText()) {
            case START:
                start();
                break;
            case CLEAR:
                clear();
                break;
        }
    }

    public void setDrawPercent(int percent) {
        drawPercent.setText(percent+"%");
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

    private void start() {
        if (textChanged) {
            parseState.setForeground(Color.YELLOW);
            parseState.setText("PARSING");
            inputArea.setEditable(false);
            turtelCom.parseText(inputArea.getText(), this);
        } else {
            turtelCom.wark();
        }
    }

    public void parseDone(double parsTime) {
        updateErrors();
        parseState.setText(parsTime + "s");
        if (errorBox.getText().isEmpty()) {
            parseState.setForeground(Color.GREEN);
        } else {
            parseState.setForeground(Color.RED);
        }
        inputArea.setEditable(true);
        textChanged = false;
        turtelCom.wark();
    }

    private void clear() {
        inputArea.setText("");
        turtelCom.reset();
        turtelCom.draw();
        textChanged = true;
        updateErrors();
    }

    private void updateErrors() {
        ArrayList<String> errors = turtelCom.getErrors();
        if (!errors.isEmpty()) {
            StringBuilder strBuild = new StringBuilder();
            for (String error : errors) {
                strBuild.append(error).append("\n");
            }

            errorBox.setText(strBuild.toString());
            scrollError.setMaximumSize(null);
        } else {
            errorBox.setText("");
            scrollError.setMaximumSize(ZERO_DIMENSION);
        }

        revalidate();
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
                } else if (Character.isWhitespace(text.charAt(wordEnd - 1))) {
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
        textChanged = true;
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        highlight();
        textChanged = true;
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
    }
}
