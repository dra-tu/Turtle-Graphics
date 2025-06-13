import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class DrawPanel extends JPanel {
    private ArrayList<Line> lines;

    public DrawPanel() {
        lines = new ArrayList<>();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (Line line : lines) {
            g.drawLine(line.x0, line.y0, line.x1, line.y1);
        }
    }

    public void addLine(Line line) {
        lines.add(line);
    }

    public static class Line {
        public int x0;
        public int y0;
        public int x1;
        public int y1;

        public Line(int x0, int y0, int x1, int y1) {
            this.x0 = x0;
            this.y0 = y0;
            this.x1 = x1;
            this.y1 = y1;
        }
    }
}
