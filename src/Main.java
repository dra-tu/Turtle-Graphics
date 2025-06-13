import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        JFrame f = new JFrame("Swing Paint Demo");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        DrawPanel draw = new DrawPanel();
        f.add(draw);
        draw.addLine(new DrawPanel.Line(0,0, 100,100));
        draw.addLine(new DrawPanel.Line(100,100, 200,323));
        draw.addLine(new DrawPanel.Line(0,0, 200,323));

        f.setVisible(true);
    }
}