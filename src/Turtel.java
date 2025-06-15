import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class Turtel extends JPanel {
    private final ArrayList<Line> lines;
    private final Point turtelPos;
    private double angel;
    private long totalLineLength;
    private long toPrintLine;
    private int maxX;
    private int maxY;

    public Turtel(int width, int height) {
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
        totalLineLength = 0L;
    }

    public void start() {
        Thread t = new Thread(() -> {
            toPrintLine = 0L;

            double drawInterval = 1_000_000_000.0/120.0;
            double delta = 0.0;
            long lastTime = System.nanoTime();
            long currentTime;

            while (toPrintLine < totalLineLength) {
                currentTime = System.nanoTime();
                delta += (currentTime - lastTime) / drawInterval;
                lastTime = currentTime;

                if (delta >= 1) {
                    repaint();
                    toPrintLine = Math.min(toPrintLine + 10L, totalLineLength);
                    delta--;
                }
            }
        });

        t.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // draw Lines
        if (!lines.isEmpty()) {
            long printed = 0L;
            int i = 0;
            while (printed < toPrintLine) {
                Line line = lines.get(i);
                if ((line.length() + printed) <= toPrintLine) {
                    line.draw(g);
                    printed += line.length();
                    i++;
                } else {
                    long diff = toPrintLine - printed;
                    float c = ((float) diff) / ((float) line.length());

                    int newX = Math.round((line.x1 - line.x0) * c);
                    int newY = Math.round((line.y1 - line.y0) * c);
                    newX += line.x0;
                    newY += line.y0;

                    g.drawLine(line.x0, line.y0, newX, newY);
                    break;
                }
            }
        }

        // draw "turtel"
        Graphics2D g2 = (Graphics2D) g;
        g2.translate(turtelPos.x, turtelPos.y);
        g2.rotate(angel);
        g2.drawChars(new char[]{'>'}, 0, 1, 0, 0);
        g2.rotate(-angel);
        g2.translate(-turtelPos.x, -turtelPos.y);
    }

    public void move(int length) {
        int newX = (int) Math.round(turtelPos.x + length * Math.cos(angel));
        int newY = (int) Math.round(turtelPos.y + length * Math.sin(angel));

        Line line = new Line(turtelPos.x, turtelPos.y, newX, newY);
        lines.add(line);
        totalLineLength += line.length();

        turtelPos.x = newX;
        turtelPos.y = newY;
    }

    public void rotate(String direction, int angelDeg) {
        switch (direction) {
            case "R":
                angel = (angel + Math.toRadians(angelDeg)) % (2 * Math.PI);
                break;
            case "L":
                angel = (angel - Math.toRadians(angelDeg)) % (2 * Math.PI);
                break;
        }
    }
}
