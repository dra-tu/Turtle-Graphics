import javax.swing.text.AttributeSet;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.util.Objects;

public class SpecialWords {
    private static final String[] KEY_WORDS = {"MOVE", "ROTATE", "VAL", "CALL_IF", "FUN", "END", "CALL", "COLOR", "PEN_UP", "PEN_DOWN", "PEN__SWITCH"};

    private static final String[] VAL_MODIFIERS = {"CALC"};

    private final static MutableAttributeSet KEY_WORD_STYLE;
    private final static MutableAttributeSet VAL_MOD_STYLE;

    static {
        KEY_WORD_STYLE = new SimpleAttributeSet();
        StyleConstants.setForeground(KEY_WORD_STYLE, Color.BLUE);
        StyleConstants.setBold(KEY_WORD_STYLE, true);

        VAL_MOD_STYLE = new SimpleAttributeSet();
        StyleConstants.setForeground(VAL_MOD_STYLE, Color.MAGENTA);
        StyleConstants.setBold(KEY_WORD_STYLE, true);
    }

    public static AttributeSet getStyle(String word) {
        for (String keyWord : KEY_WORDS) {
            if (Objects.equals(word, keyWord)) {
                return KEY_WORD_STYLE;
            }
        }

        for (String modifier : VAL_MODIFIERS) {
            if (word.equals(modifier)) {
                return VAL_MOD_STYLE;
            }
        }

        return null;
    }
}
