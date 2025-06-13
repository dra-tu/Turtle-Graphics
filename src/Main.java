import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        JFrame f = new JFrame("Swing Paint Demo");
        f.setLayout(new BoxLayout(f.getContentPane(), BoxLayout.Y_AXIS));
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        TurtelPanel draw = new TurtelPanel();
        f.add(draw);
        draw.moveTurtel(100,100);
        draw.moveTurtel(150,100);
        draw.moveTurtel(-250,-35);

        Controller controller = new Controller();
        f.add(controller);

        f.pack();
        f.setResizable(false);
        f.setVisible(true);
    }
}