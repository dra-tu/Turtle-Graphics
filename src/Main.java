import javax.swing.*;

public class Main {
    private static final int MAX_X = 500;
    private static final int MAX_Y = 500;

    public static void main(String[] args) {
        JFrame f = new JFrame("Turtel");
        f.setIconImage(Turtel.TURTEL_IMG);
        f.setLayout(new BoxLayout(f.getContentPane(), BoxLayout.X_AXIS));
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Turtel turtel = new Turtel(MAX_X, MAX_Y);

        Controller controller = new Controller(200, turtel);

        f.add(controller);
        f.add(turtel);

        f.pack();
        f.setVisible(true);
    }
}