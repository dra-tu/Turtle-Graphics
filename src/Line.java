import java.awt.*;

public class Line {
    public int x0;
    public int y0;
    public int x1;
    public int y1;
    public Color color;

    public Line(int x0, int y0, int x1, int y1, Color color) {
        this.x0 = x0;
        this.y0 = y0;
        this.x1 = x1;
        this.y1 = y1;

        this.color = color;
    }

    public void draw(Graphics g) {
        g.drawLine(x0, y0, x1, y1);
    }

    public long length() {
        return  Math.round( Math.sqrt( Math.pow((x0-x1), 2) + Math.pow((y0 - y1), 2) ) );
    }
}
