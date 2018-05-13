import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Cartographer extends Canvas {
    public GraphicsContext gc;
    private Room root;
    private final int length = 50;
    public Cartographer(Room startingRoom) {
        gc = getGraphicsContext2D();
        root = startingRoom;
        gc.setLineWidth(1.0);
    }

    public void update() {
        BoundsMapper bm = new BoundsMapper(root);
        bm.walk();
        setWidth((bm.xMax + abs(bm.xMin) + 1) * 100);
        setHeight((bm.yMax + abs(bm.yMin) + 1) * 100);
        gc.clearRect(0, 0, getWidth(), getHeight());
        gc.strokeRect(0, 0, getWidth(), getHeight());
        }
    }

