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

    private int abs(int n) {
      return n < 0 ? (-1 * n) : n;
    }

    public void update() {
        BoundsMapper bm = new BoundsMapper(root);
        bm.walk();
        setWidth((bm.xMax + abs(bm.xMin) + 1) * 100);
        setHeight((bm.yMax + abs(bm.yMin) + 1) * 100);
        gc.clearRect(0, 0, getWidth(), getHeight());
        gc.strokeRect(0, 0, getWidth(), getHeight());
        for (Room key: bm.coords.keySet()) {
            drawRoom(bm.coords.get(key).x, bm.coords.get(key).y, key);
        }
    }
    private void drawRoom(int x, int y, Room room) {
        Double xMid = getMid()[0] - (length / 2) + (x * length);
        Double yMid = getMid()[1] - (length / 2) + (y * length);
        gc.strokeRect(xMid, yMid, length, length);
        for (String exit : room.getExits().keySet()) {
            switch(exit) {
                case "North":
                    gc.strokeLine(xMid + (length / 2), yMid - 5, xMid + (length
                            / 2), yMid);
                    break;
                case "South":
                    gc.strokeLine(xMid + (length / 2), yMid + length, xMid +
                            (length / 2), yMid + length + 5);
                    break;
                case "East":
                    gc.strokeLine(xMid + length, yMid + (length / 2), xMid +
                            length + 5, yMid + (length / 2));
                    break;
                case "West":
                    gc.strokeLine(xMid, yMid + (length / 2), xMid - 5, yMid +
                            (length / 2));
                    break;
                default:
                    break;
            }
        }
        for (Thing thing : room.getContents()) {
            if (thing instanceof Player) {
                gc.fillText("@", xMid, yMid + length / 4);
            }
            else if (thing instanceof Treasure) {
                gc.fillText("$", xMid + (length / 2), yMid + (length / 4));
            }
            else if (thing instanceof Critter) {
                gc.fillText("M", xMid, yMid + (length / 1.5));
            }
        }
    }

    private Double[] getMid() {
        Double[] xy = {getWidth()/2, getHeight()/2};
        return xy;
    }
}
