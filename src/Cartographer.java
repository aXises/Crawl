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
