package org.app.noteproject;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class NoteTable extends Application {

    private TableView<Note> noteTableView;
    private Job job;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        this.job = new Job();
        List<Integer> velocities = new ArrayList<>();
        velocities.add(60);
        velocities.add(80);
        velocities.add(100);
        this.job.setSpecificVelocities(velocities);
        System.out.println(this.job);

        List<Note> notes = new ArrayList<>();
        int noteStartTime = 0;
        int noteEndTime = 0;
        for (int note: this.job.getNotes()) {
            for (int velocity: this.job.getVelocities()) {
                noteEndTime = noteStartTime + this.job.getNoteDuration();
                Note n = new Note(note, velocity, noteStartTime, noteEndTime);
                notes.add(n);
                noteStartTime += this.job.getNoteDuration() + this.job.getNoteDecay() + this.job.getNoteGap();
            }
        }
        System.out.println("Total number of notes: " + notes.size());

        ObservableList<Note> noteList = FXCollections.observableArrayList();
        noteList.addAll(notes);

        this.noteTableView = new TableView<>();

        TableColumn<Note, Integer> noteColumn = new TableColumn<>("Note");
        TableColumn<Note, Integer> velocityColumn = new TableColumn<>("Velocity");
        TableColumn<Note, Integer> noteStartColumn = new TableColumn<>("Start (ms)");
        TableColumn<Note, Integer> noteEndColumn = new TableColumn<>("End (ms)");

        noteColumn.setCellValueFactory(new PropertyValueFactory<>("number"));
        velocityColumn.setCellValueFactory(new PropertyValueFactory<>("velocity"));
        noteStartColumn.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        noteEndColumn.setCellValueFactory(new PropertyValueFactory<>("endTime"));

        this.noteTableView.getColumns().addAll(
                noteColumn,
                velocityColumn,
                noteStartColumn,
                noteEndColumn
        );

        this.noteTableView.setItems(noteList);

        VBox box = new VBox(this.noteTableView);
        stage.setTitle("Note table");
        Scene scene = new Scene(box, 400, 400);
        stage.setScene(scene);
        stage.show();

    }
}
