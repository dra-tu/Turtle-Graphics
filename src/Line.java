import java.awt.*;

public record Line(int x0, int y0, int x1, int y1, Color color) {
    private static final int height = 100_000;

    public void draw(Graphics g) {
        int width = x1 - x0;
        g.fillRect(x0, y0, width, height);
    }

    public void drawPart(Graphics2D g, int x1, int y1) {
        int width = x1 - x0;
        g.fillRect(x0, y0, width, height);
    }

    public long length() {
        return Math.round(Math.sqrt(Math.pow((x0 - x1), 2) + Math.pow((y0 - y1), 2)));
    }
}
