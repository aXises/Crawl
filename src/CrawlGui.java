import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class CrawlGui extends Application {
    private static Room startingRoom;
    private static Player player;
    private Room currentRoom;
    private GridPane buttonArea = new GridPane();
    private TextArea messageArea = new TextArea();
    private Cartographer cartographer;

    public static void main(String args[]) {
        if (args.length != 1) {
            System.out.println("Usage: java CrawlGui mapname");
            System.exit(1);
        }
        if (MapIO.loadMap(args[0]) != null) {
            Object content[] = MapIO.loadMap(args[0]);
            startingRoom = (Room) content[1];
            player = (Player) content[0];

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
        generateButtons();
        startingRoom.enter(player);
        currentRoom = startingRoom;
        cartographer = new Cartographer(startingRoom);
        cartographer.update();

        BorderPane top = new BorderPane();
        top.setRight(buttonArea);
        top.setCenter(cartographer);

        BorderPane root = new BorderPane();
        root.setTop(top);

        messageArea.setEditable(false);
        root.setCenter(messageArea);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        log("You find your self in " + currentRoom.getDescription());
    }

    private void generateButtons() {
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
        north.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                tryExit(north.getText());
            }
        });
        south.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                tryExit(south.getText());
            }
        });
        east.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                tryExit(east.getText());
            }
        });
        west.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                tryExit(west.getText());
            }
        });
    }

    private void tryExit(String exitName) {
        if (currentRoom.getExits().containsKey(exitName)) {
            if (currentRoom.leave(player)) {
                currentRoom = currentRoom.getExits().get(exitName);
                currentRoom.enter(player);
            } else
                log("Something prevents you from leaving");
        } else
            log("No door that way");
        cartographer.update();
    }

    private void log(String text) {
        messageArea.appendText(text + "\n");
    }
}
