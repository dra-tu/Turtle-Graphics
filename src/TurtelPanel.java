import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class TurtelPanel extends JPanel {
    private final ArrayList<Line> lines;
    private final Point turtelPos;
    private int maxX;
    private int maxY;

    public TurtelPanel(int width, int height) {
        maxX = width;
        maxY = height;

        setBorder(BorderFactory.createLineBorder(Color.BLACK));
        lines = new ArrayList<>();
        turtelPos = new Point(maxX/2, maxY/2);
        setPreferredSize(new Dimension(maxX, maxY));
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

    public void executeCommands(String commands) {
    }
}
