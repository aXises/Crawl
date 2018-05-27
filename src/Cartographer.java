import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Cartographer extends Canvas {
    // The graphicsContext object containing the canvas
    private GraphicsContext gc;
    // The starting room
    private Room root;
    // BoundsMapper object containing the max and min values of a map
    BoundsMapper bm ;
    // The length of a room
    private final int length = 50;

    /**
     * Constructor
     * @param startingRoom The starting room of the map
     */
    public Cartographer(Room startingRoom) {
        gc = getGraphicsContext2D();
        root = startingRoom;
        bm = new BoundsMapper(root);
        gc.setLineWidth(1.0);
    }

    /**
     * Updates the map and redraws any changes made to it
     */
    public void update() {
        bm.walk();
        setWidth((bm.xMax + abs(bm.xMin) + 2) * length);
        setHeight((bm.yMax + abs(bm.yMin) + 2) * length);
        gc.clearRect(0, 0, getWidth(), getHeight());
        for (Room key: bm.coords.keySet()) {
            drawRoom(bm.coords.get(key).x, bm.coords.get(key).y, key);
        }
    }

    /**
     * Draws a room at a given point
     * @param x The x position in the canvas
     * @param y The y position in the canvas
     * @param room The room to draw
     */
    private void drawRoom(int x, int y, Room room) {
        // Calculate mid point of one room with respect to the global mid point
        // of the canvas
        Double xMid = getMid()[0] - (length / 2) + (x * length);
        Double yMid = getMid()[1] - (length / 2) + (y * length);
        // Calculate offset based on the amount and coordinates of the rooms
        // in a map
        Double xOffset = (abs(bm.xMin) - bm.xMax) * length / 2;
        Double yOffset = (abs(bm.yMin) - bm.yMax) * length / 2;
        // Get the final coordinates to draw the room at
        Double xPos = xMid + xOffset;
        Double yPos = yMid + yOffset;
        // Draws the box
        gc.strokeRect(xPos, yPos, length, length);
        // Adds exits lines
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
        // Adds Thing to the room
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

    /**
     * Gets the mid point of the canvas
     * @return A coordinate pointing to the mid point
     */
    private double[] getMid() {
        double[] xy = {getWidth()/2, getHeight()/2};
        return xy;
    }

    /**
     * Takes the absolute value of a number
     * @param n Number to take the absolute value of
     * @return The absolute value of the number
     */
    private double abs(int n) {
        return n < 0 ? (-1 * n) : n;
    }

}
