import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class TurtelPanel extends JPanel {
    private final ArrayList<Line> lines;
    private final Point turtelPos;
    private double angel;
    private int maxX;
    private int maxY;

    public TurtelPanel(int width, int height) {
        maxX = width;
        maxY = height;

        setBorder(BorderFactory.createLineBorder(Color.BLACK));
        lines = new ArrayList<>();
        turtelPos = new Point(maxX / 2, maxY / 2);
        angel = 0;
        setPreferredSize(new Dimension(maxX, maxY));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        for (Line line : lines) {
            line.draw(g);
        }
    }

    public void moveForword(int lenght) {
        int newX = turtelPos.x + r * Math.cos(angel);
        int newY = turtelPos.y + r * Math.sin(angel);

        lines.add(new Line(turtelPos.x, turtelPos.y, newX, newY));

        turtelPos.x = newX;
        turtelPos.y = newY;
    }

    public void rotate_right(int dAngel) {
        angel = (angel + Math.toRadians(dAngel)) % 2*Math.PI;
    }
    public void rotate_left(int dAngel) {
        angel = (angel - Math.toRadians(dAngel)) % 2*Math.PI;
    }

    private final static String NEW_LINE = "\n";

    public void executeCommands(String commands) {
    }
}
