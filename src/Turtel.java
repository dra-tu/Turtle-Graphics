import exeptions.NotAColorException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class Turtel extends JPanel implements MouseWheelListener, MouseMotionListener {
    private final static Color DEFAOULT_COLOR = Color.WHITE;

    private final ArrayList<Line> lines;
    private final Point turtelPos;
    private double angel;
    private Color lineColor;
    private long totalLineLength;
    private long toPrintLine;
    private int maxX;
    private int maxY;

    private long lastDragTime;
    private Point lastDragPoint;
    private final Point viewTranslation;

    private double scale;
    public long stepLength;
    public double targetFPS;

    public Turtel(int width, int height) {
        this.setBackground(Color.BLACK);

        this.addMouseWheelListener(this);
        this.addMouseMotionListener(this);
        scale = 1;

        maxX = width;
        maxY = height;

        stepLength = 10L;
        targetFPS = 120.0;

        setPreferredSize(new Dimension(maxX, maxY));

        lineColor = DEFAOULT_COLOR;
        viewTranslation = new Point(0, 0);
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
        lineColor = DEFAOULT_COLOR;
    }

    public void start() {
        Thread t = new Thread(() -> {
            toPrintLine = 0L;

            double delta = 0.0;
            long lastTime = System.nanoTime();
            long currentTime;

            while (toPrintLine < totalLineLength) {
                double drawInterval = 1_000_000_000.0 / targetFPS;
                currentTime = System.nanoTime();
                delta += (currentTime - lastTime) / drawInterval;
                lastTime = currentTime;

                if (delta >= 1) {
                    repaint();
                    toPrintLine = Math.min(toPrintLine + stepLength, totalLineLength);
                    delta--;
                }
            }
        });

        t.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        g2.scale(scale, scale);

        g2.translate(viewTranslation.x, viewTranslation.y);

        // draw Lines
        if (!lines.isEmpty()) {
            long printed = 0L;
            int i = 0;
            while (printed < toPrintLine) {
                Line line = lines.get(i);
                g2.setColor(line.color);
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
        g2.translate(turtelPos.x, turtelPos.y);
        g2.rotate(angel);
        g2.drawChars(new char[]{'>'}, 0, 1, 0, 0);
        g2.rotate(-angel);
        g2.translate(-turtelPos.x, -turtelPos.y);

        g2.scale(1 / scale, 1 / scale);
    }

    public void move(int length) {
        int newX = (int) Math.round(turtelPos.x + length * Math.cos(angel));
        int newY = (int) Math.round(turtelPos.y + length * Math.sin(angel));

        Line line = new Line(turtelPos.x, turtelPos.y, newX, newY, lineColor);
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

    public void setColor(int r, int g, int b) throws NotAColorException {
        try {
            lineColor = new Color(r, g, b);
        } catch (IllegalArgumentException ig) {
            throw new NotAColorException();
        }
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        int upDown = e.getWheelRotation();
        double factor = upDown * 0.1;
        scale = Math.clamp(scale - factor, 0.1, 5.0);
        repaint();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        // 100ms = 0.1s
        if (lastDragPoint == null || (System.currentTimeMillis() - lastDragTime) >= 100L) {
            lastDragPoint = e.getPoint();
            lastDragTime = System.currentTimeMillis();
            return;
        }

        Point thisPoint = e.getPoint();

        viewTranslation.x += (int) Math.round((thisPoint.x - lastDragPoint.x) * (1 / scale));
        viewTranslation.y += (int) Math.round((thisPoint.y - lastDragPoint.y) * (1 / scale));

        lastDragPoint = thisPoint;
        lastDragTime = System.currentTimeMillis();

        repaint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }
}
