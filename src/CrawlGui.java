
public class CrawlGui extends Application {
    private static Room startingRoom;

    public static void main(String args[]) {
        if (args.length != 1) {
            System.out.println("Usage: java CrawlGui mapname");
            System.exit(1);
        }
        if (MapIO.loadMap(args[0]) != null) {
            startingRoom = (Room) MapIO.loadMap(args[0])[1];

        } else if (MapIO.deserializeMap(args[0]) != null) {
            startingRoom = MapIO.deserializeMap(args[0]);
        }
        else {
            System.out.println("Unable to load file");
            System.exit(2);
        }
        launch(args);
    }
    public void start(Stage stage) {

    }
}
