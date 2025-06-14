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
        setPreferredSize(new Dimension(maxX, maxY));

        lines = new ArrayList<>();
        turtelPos = new Point();
        reset();
    }

    public void reset() {
        lines.clear();
        turtelPos.x = maxX / 2;
        turtelPos.y = maxY / 2;
        angel = 0;
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

    public void executeCommands(String commandList) {
        String[] commands = commandList.lines()
                .filter(l -> !l.isBlank() && !l.startsWith("#"))
                .toArray(String[]::new);

        lines.clear();

        for (int i = 0; i < commands.length; i++) {
            System.out.println("Line " + i + ": " + commands[i]);

            String[] command = commands[i].split(" ", 2);

            switch (command[0]) {
                case "MOVE":
                    if (command.length != 2) continue;
                    int length = Integer.parseInt(command[1]);
                    move(length);
                    break;
                case "ROTATE":
                    if (command.length != 3) continue;
                    int angel = Integer.parseInt(command[2]);
                    rotate(command[1], angel);
                    break;
            }
        }

        repaint();
    }
}
