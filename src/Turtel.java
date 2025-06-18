import exeptions.InvalidLengthException;
import exeptions.NotAColorException;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class Turtel extends JPanel implements MouseWheelListener, MouseMotionListener {
    private final static Color DEFAOULT_COLOR = Color.WHITE;
    private final static int TURTLE_IMG_SIZE = 20;
    public final static BufferedImage TURTEL_IMG;

    private final ArrayList<Line> lines;
    private int turtelPosX;
    private int turtelPosY;
    private double angel;
    private Color lineColor;
    private long totalLineLength;
    private long toPrintLine;
    private final int START_X;
    private final int START_Y;

    private long lastDragTime;
    private Point lastDragPoint;
    private final Point viewTranslation;

    private double scale;
    public long stepLength;
    public double targetFPS;

    private boolean drawing;

    static {
        try {
            TURTEL_IMG = ImageIO.read(Objects.requireNonNull(Turtel.class.getResourceAsStream("turtle.png")));
        } catch (IOException | NullPointerException e) {
            throw new RuntimeException(e);
        }
    }

    public Turtel(int width, int height) {
        this.setBackground(Color.BLACK);

        this.addMouseWheelListener(this);
        this.addMouseMotionListener(this);
        scale = 0.3;

        START_X = width / 2;
        START_Y = height / 2;

        stepLength = 10L;
        targetFPS = 120.0;

        drawing = true;

        viewTranslation = new Point(0, 0);
        lines = new ArrayList<>();
        reset();
    }

    public void reset() {
        lines.clear();
        turtelPosX = START_X;
        turtelPosY = START_Y;
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

        int lastX = turtelPosX;
        int lastY = turtelPosY;
        // draw Lines
        if (!lines.isEmpty()) {
            long printed = 0L;
            int i = 0;
            while (printed < toPrintLine) {
                Line line = lines.get(i);
                g2.setColor(line.color());
                if ((line.length() + printed) <= toPrintLine) {
                    line.draw(g);
                    printed += line.length();
                    lastX = line.x1();
                    lastY = line.y1();
                    i++;
                } else {
                    long diff = toPrintLine - printed;
                    float c = ((float) diff) / ((float) line.length());

                    lastX = Math.round((line.x1() - line.x0()) * c) + line.x0();
                    lastY = Math.round((line.y1() - line.y0()) * c) + line.y0();

                    line.drawPart(g2, lastX, lastY);
                    break;
                }
            }
        }

        // draw "turtel"
        g2.translate(lastX, lastY);
        g2.rotate(angel);
        g2.drawImage(TURTEL_IMG, -(TURTLE_IMG_SIZE / 2), -(TURTLE_IMG_SIZE / 2), TURTLE_IMG_SIZE, TURTLE_IMG_SIZE, null);
        g2.rotate(-angel);
        g2.translate(-lastX, -lastY);

        g2.scale(1 / scale, 1 / scale);
    }

    public void move(int length) throws InvalidLengthException {
        if (length <= 0) throw new InvalidLengthException();

        int newX = (int) Math.round(turtelPosX + length * Math.cos(angel));
        int newY = (int) Math.round(turtelPosY + length * Math.sin(angel));

        if (drawing) {
            Line line = new Line(turtelPosX, turtelPosY, newX, newY, lineColor);
            lines.add(line);
            totalLineLength += line.length();
        }

        turtelPosX = newX;
        turtelPosY = newY;
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

    public void setDrawing(boolean drawing) {
        this.drawing = drawing;
    }

    public void switchDrawing() {
        this.drawing = !this.drawing;
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        int upDown = e.getWheelRotation();
        double factor = upDown * 0.5;
        scale = scale - factor;
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
