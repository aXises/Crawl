import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

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
        stage.setTitle("Crawl");
        TextArea t2=new TextArea("bot");
        GridPane buttonArea = new GridPane();
        Button north = new Button("North");
        Button west = new Button("West");
        Button east = new Button("East");
        Button south = new Button("South");
        Button look = new Button("Look");
        Button examine = new Button("Examine");
        Button drop = new Button("Drop");
        Button take = new Button("Take");
        Button fight = new Button("Fight");
        Button save = new Button("Save");
        buttonArea.add(north, 1 ,0);
        buttonArea.add(west, 0 ,1);
        buttonArea.add(east, 2 ,1);
        buttonArea.add(south, 1 ,3);
        buttonArea.add(look, 0, 4);
        buttonArea.add(examine, 1, 4);
        buttonArea.add(drop, 0, 5);
        buttonArea.add(take, 1, 5);
        buttonArea.add(fight, 0, 6);
        buttonArea.add(save, 0, 7);

        BorderPane top = new BorderPane();
        Cartographer cartographer = new Cartographer(startingRoom);
        cartographer.update();
        top.setCenter(cartographer);

        top.setRight(buttonArea);

        BorderPane root = new BorderPane();
        root.setTop(top);
        root.setCenter(t2);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
