import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Cartographer extends Canvas {
    public GraphicsContext gc;
    private Room root;
    BoundsMapper bm ;
    private final int length = 50;
    public Cartographer(Room startingRoom) {
        gc = getGraphicsContext2D();
        root = startingRoom;
        bm = new BoundsMapper(root);
        gc.setLineWidth(1.0);
    }

    private double abs(int n) {
      return n < 0 ? (-1 * n) : n;
    }

    public void update() {
        bm.walk();
        setWidth((bm.xMax + abs(bm.xMin) + 2) * length);
        setHeight((bm.yMax + abs(bm.yMin) + 2) * length);
        gc.clearRect(0, 0, getWidth(), getHeight());
        for (Room key: bm.coords.keySet()) {
            drawRoom(bm.coords.get(key).x, bm.coords.get(key).y, key);
        }
    }

    private void drawRoom(int x, int y, Room room) {
        Double xMid = getMid()[0] - (length / 2) + (x * length);
        Double yMid = getMid()[1] - (length / 2) + (y * length);
        Double xOffset = (abs(bm.xMin) - bm.xMax) * length / 2;
        Double yOffset = (abs(bm.yMin) - bm.yMax) * length / 2;
        Double xPos = xMid + xOffset;
        Double yPos = yMid + yOffset;
        gc.strokeRect(xPos, yPos, length, length);
        for (String exit : room.getExits().keySet()) {
            switch(exit) {
                case "North":
                    gc.strokeLine(xPos + (length / 2), yPos - 5,
                            xPos + (length / 2), yPos);
                    break;
                case "South":
                    gc.strokeLine(xPos + (length / 2), yPos + length, xPos +
                            (length / 2), yPos + length + 5);
                    break;
                case "East":
                    gc.strokeLine(xPos + length, yPos + (length / 2), xPos +
                            length + 5, yPos + (length / 2));
                    break;
                case "West":
                    gc.strokeLine(xPos, yPos + (length / 2), xPos - 5, yPos +
                            (length / 2));
                    break;
                default:
                    break;
            }
        }
        for (Thing thing : room.getContents()) {
            if (thing instanceof Player) {
                gc.fillText("@", xPos, yPos + length / 4);
            }
            else if (thing instanceof Treasure) {
                gc.fillText("$", xPos + (length / 2), yPos + (length / 4));
            }
            else if (thing instanceof Critter) {
                gc.fillText("M", xPos, yPos + (length / 1.5));
            }
        }
    }

    private double[] getMid() {
        double[] xy = {getWidth()/2, getHeight()/2};
        return xy;
    }
}
