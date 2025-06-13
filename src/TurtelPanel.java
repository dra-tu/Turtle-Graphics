import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class TurtelPanel extends JPanel {
    private ArrayList<Line> lines;
    private Point turtelPos;
    private static final int MAX_X = 500;
    private static final int MAX_Y = 500;

    public TurtelPanel() {
        lines = new ArrayList<>();
        turtelPos = new Point(MAX_X/2, MAX_Y/2);
        setPreferredSize(new Dimension(MAX_X, MAX_Y));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        for (Line line : lines) {
            line.draw(g);
        }
    }

    public void moveTurtel(int dx, int dy) {
        int newX = turtelPos.x + dx;
        int newY = turtelPos.y + dy;
        lines.add(new Line(turtelPos.x, turtelPos.y, newX, newY));

        turtelPos.x = newX;
        turtelPos.y = newY;
    }
}
