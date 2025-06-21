import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        JFrame f = new JFrame("Turtel");
        f.setIconImage(Turtel.TURTEL_IMG);
        f.setLayout(new BoxLayout(f.getContentPane(), BoxLayout.X_AXIS));
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Turtel turtel = new Turtel();
        Controller controller = new Controller(turtel);
        turtel.setController(controller);

        f.add(controller);
        f.add(turtel);

        f.pack();
        f.setVisible(true);
    }
}