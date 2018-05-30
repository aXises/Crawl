import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.List;
import java.util.Optional;

public class CrawlGui extends Application {
    // Starting room of the map
    private static Room startingRoom;
    // The player object
    private static Player player;
    // The current room the player is in
    private Room currentRoom;
    // Upper Pane containing all the buttons
    private GridPane buttonAreaUpper = new GridPane();
    // Lower Pane containing all the buttons
    private GridPane buttonAreaLower = new GridPane();
    // The message area which displays text messages
    private TextArea messageArea = new TextArea();
    // Cartographer which draws the map
    private Cartographer cartographer;

    /**
     * Entry point of the program
     * @param args Arguments to the program
     */
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
        stage.setTitle("Crawl - Explore");
        generateButtons();
        startingRoom.enter(player);
        currentRoom = startingRoom;
        cartographer = new Cartographer(startingRoom);
        cartographer.update();


        VBox buttonBox = new VBox();
        buttonBox.getChildren().addAll(buttonAreaUpper, buttonAreaLower);

        BorderPane top = new BorderPane();
        top.setRight(buttonBox);
        top.setCenter(cartographer);

        BorderPane root = new BorderPane();
        root.setTop(top);

        messageArea.setEditable(false);
        root.setBottom(messageArea);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        log("You find your self in " + currentRoom.getDescription());
    }

    /**
     * Method to generate and add functionality of all the buttons.
     */
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
        // Add buttons to the grid pane
        buttonAreaUpper.add(north, 1 ,0);
        buttonAreaUpper.add(west, 0 ,1);
        buttonAreaUpper.add(east, 2 ,1);
        buttonAreaUpper.add(south, 1 ,3);
        buttonAreaLower.add(look, 0, 4);
        buttonAreaLower.add(examine, 1, 4);
        buttonAreaLower.add(drop, 0, 5);
        buttonAreaLower.add(take, 1, 5);
        buttonAreaLower.add(fight, 0, 6);
        buttonAreaLower.add(save, 0, 7);
        // Attempt to interact with the north exit
        north.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                tryExit(north.getText());
            }
        });
        // Attempt to interact with the south exit
        south.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                tryExit(south.getText());
            }
        });
        // Attempt to interact with the east exit
        east.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                tryExit(east.getText());
            }
        });
        // Attempt to interact with the west exit
        west.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                tryExit(west.getText());
            }
        });
        // Handle functionality of the look button
        look.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                log(currentRoom.getDescription() + " - you see:");
                for (Thing thing: currentRoom.getContents()) {
                    log(" " + thing.getShort());
                }
                log("You are carrying:");
                double totalValue = 0;
                for (Thing thing : player.getContents()) {
                    log(" " + thing.getShort());
                    totalValue += thing instanceof Lootable ?
                            ((Lootable) thing).getValue() : 0;
                }
                log("worth " + String.format("%.1f", totalValue)
                        + " in total");
            }
        });
        // Handle functionality of the example button
        examine.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                TextInputDialog input = new TextInputDialog();
                input.initStyle(StageStyle.UTILITY);
                input.setGraphic(null);
                input.setTitle("Examine what?");
                input.setHeaderText("Examine what?");
                Optional<String> res = input.showAndWait();
                if (res.isPresent()) {
                    Thing thing;
                    if ((thing = getThing(res.get(), player.getContents()))
                            != null)
                        log(thing.getDescription());
                    else if ((thing = getThing(res.get(), currentRoom
                            .getContents())) != null)
                        log(thing.getDescription());
                    else
                        log("Nothing found with that name");
                }
            }
        });
        // Handle functionality of the drop button
        drop.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                TextInputDialog input = new TextInputDialog();
                input.initStyle(StageStyle.UTILITY);
                input.setGraphic(null);
                input.setTitle("Item to drop?");
                input.setHeaderText("Item to drop?");
                Optional<String> res = input.showAndWait();
                if (res.isPresent()) {
                    Thing thing;
                    if ((thing = player.drop(res.get())) != null) {
                        currentRoom.enter(thing);
                        cartographer.update();
                    }
                    else
                        log("Nothing found with that name");
                }
            }
        });
        // Handle functionality of the take button
        take.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                TextInputDialog input = new TextInputDialog();
                input.initStyle(StageStyle.UTILITY);
                input.setGraphic(null);
                input.setTitle("Take what?");
                input.setHeaderText("Take what?");
                Optional<String> res = input.showAndWait();
                if (res.isPresent()) {
                    Thing t = null;
                    for (Thing thing : currentRoom.getContents()) {
                        if (thing.getShort().equals(res.get())) {
                            if (!(thing instanceof Player)) {
                                if (thing instanceof Mob && !((Mob) thing)
                                        .isAlive()) {
                                    t = thing;
                                } else {
                                    t = thing;
                                }
                            }
                        }
                    }
                    if (currentRoom.leave(t)) {
                        player.add(t);
                        cartographer.update();
                    }
                }
            }
        });
        // Handle functionality of the fight button
        fight.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                TextInputDialog input = new TextInputDialog();
                input.initStyle(StageStyle.UTILITY);
                input.setGraphic(null);
                input.setTitle("Fight what?");
                input.setHeaderText("Fight what?");
                Optional<String> res = input.showAndWait();
                if (res.isPresent()) {
                    Thing t = getThing(res.get(), currentRoom.getContents());
                    if (t instanceof Critter) {
                        player.fight((Critter) t);
                        if (player.getHealth() > 0) {
                            log("You won");
                        } else {
                            log("Game over");
                            for (Node button : buttonAreaUpper.getChildren()) {
                                button.setDisable(true);
                            }
                            for (Node button : buttonAreaLower.getChildren()) {
                                button.setDisable(true);
                            }
                        }
                    }
                }
            }
        });
        // Handle functionality of the save button
        save.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                TextInputDialog input = new TextInputDialog();
                input.initStyle(StageStyle.UTILITY);
                input.setGraphic(null);
                input.setTitle("Save filename?");
                input.setHeaderText("Save filename");
                Optional<String> res = input.showAndWait();
                if (res.isPresent()) {
                    log(MapIO.saveMap(startingRoom, res.get()) ? "Saved" :
                            "Unable to save");
                }
            }
        });
    }

    /**
     * Gets a Thing from a list by its description
     * @param name The name of the Thing
     * @param list The list to iterate through
     * @return the Thing object or null if not found
     */
    private Thing getThing(String name, List<Thing> list) {
        for (Thing thing : list) {
            if (thing.getShort().equals(name)) {
                return thing;
            }
        }
        return null;
    }

    /**
     * Attempt to interact with a exit
     * @param exitName The name of the exit to interact with
     */
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

    /**
     * Logs a string to the message area
     * @param text
     */
    private void log(String text) {
        messageArea.appendText(text + "\n");
    }
}
