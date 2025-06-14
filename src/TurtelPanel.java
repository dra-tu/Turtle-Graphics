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

    public void move(int length) {
        int newX = (int) (turtelPos.x + length * Math.cos(angel));
        int newY = (int) (turtelPos.y + length * Math.sin(angel));

        lines.add(new Line(turtelPos.x, turtelPos.y, newX, newY));

        turtelPos.x = newX;
        turtelPos.y = newY;
    }

    public void rotate(String direction, int angelDeg) {
        switch (direction) {
            case "R":
                angel = (angel + Math.toRadians(angelDeg)) % (2*Math.PI);
                break;
            case "L":
                angel = (angel - Math.toRadians(angelDeg)) % (2*Math.PI);
                break;
        }
    }

    private final static String NEW_LINE = "\n";

    public void executeCommands(String commands) {
    }
}
