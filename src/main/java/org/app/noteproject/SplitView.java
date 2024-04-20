package org.app.noteproject;

import javafx.application.Application;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class SplitView extends Application {

    private ListView<String> listView;
    private Label colorLabel;

    public static void main(String[] args) {
        launch(args);
    }

    public void processListSelection(ObservableValue<? extends String> val, String oldValue, String newValue) {
        this.colorLabel.setText(newValue);
    }

    @Override
    public void start(Stage stage) {

        String[] colors = {"red", "blue", "green"};
        ObservableList<String> observableList = FXCollections.observableArrayList();
        observableList.addAll(colors);
        this.listView = new ListView<>(observableList);

        this.listView.getSelectionModel().select(0); // initial/default selection inside the list
        this.listView.getSelectionModel().selectedItemProperty().addListener(this::processListSelection);

        this.colorLabel = new Label(observableList.get(0)); // default value shown in pane
        StackPane colorPane = new StackPane(this.colorLabel);

        SplitPane splitPane = new SplitPane();
        splitPane.setOrientation(Orientation.HORIZONTAL); // orientation of pane
        splitPane.setDividerPositions(0.25); // how much width occupies on window
        splitPane.getItems().addAll(this.listView, colorPane);

        Scene scene = new Scene(splitPane, 500, 300);
        stage.setTitle("Jobs");
        stage.setScene(scene);
        stage.show();
    }
}
