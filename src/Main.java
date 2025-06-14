import javax.swing.*;

public class Main {
    private static final int MAX_X = 500;
    private static final int MAX_Y = 500;

    public static void main(String[] args) {
        JFrame f = new JFrame("Swing Paint Demo");
        f.setLayout(new BoxLayout(f.getContentPane(), BoxLayout.Y_AXIS));
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        TurtelPanel draw = new TurtelPanel(MAX_X, MAX_Y);
        f.add(draw);
        draw.move(100);
        draw.rotate("L", 90);
        draw.move(150);
        draw.rotate("R", 55);
        draw.move(250);

        Controller controller = new Controller(MAX_X, 50, draw);
        f.add(controller);

        f.pack();
        f.setResizable(false);
        f.setVisible(true);
    }
}