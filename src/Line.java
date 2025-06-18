import java.awt.*;

public record Line(int x0, int y0, int x1, int y1, Color color) {
    private static final int height = 100_000;

    public void draw(Graphics g) {
        g.drawLine(x0, y0, x1, y1);
    }

    public void drawPart(Graphics2D g, int x1, int y1) {
        g.drawLine(x0, y0, x1, y1);
    }

    public long length() {
        return Math.round(Math.sqrt(Math.pow((x0 - x1), 2) + Math.pow((y0 - y1), 2)));
    }
}
