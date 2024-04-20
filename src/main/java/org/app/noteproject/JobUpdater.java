package org.app.noteproject;

import javafx.application.Application;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;
import javafx.scene.layout.HBox;
import javafx.scene.canvas.Canvas;

import java.util.ArrayList;
import java.util.List;

public class JobUpdater extends Application {

    private Job job = new Job("Initial Job");
    private RadioButton selectedRadioButton;
    private TableView<Note> noteTableView;
    private ListView<String> jobListView;
    private Label colorLabel;
    private VBox tableBox;
    private List<String> jobs;
    ObservableList<String> observableJobList;
    ObservableList<Note> noteList;
    List<Note> notes;


    @Override
    public void start(Stage stage) {
        TextField jobNameField = new TextField();
        Button updateJobBtn = new Button("Update job");

        SpinnerValueFactory<Integer> valueFactory1 = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 127, 40, 1);
        SpinnerValueFactory<Integer> valueFactory2 = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 127, 120, 1);

        Spinner<Integer> spinner1 = new Spinner<>();
        Spinner<Integer> spinner2 = new Spinner<>();

        spinner1.setValueFactory(valueFactory1);
        spinner2.setValueFactory(valueFactory2);

        Label textFieldLabel = new Label("Job name");
        Label spinner1Label = new Label("Start note");
        Label spinner2Label = new Label("End note");
        Label noteDurationLabel = new Label("Note duration: " + job.getNoteDuration() + " ms");
        Label noteDecayLabel = new Label("Note decay: " + job.getNoteDecay() + " ms");
        Label gapBtwNotesLabel = new Label("Gap between notes: " + job.getNoteGap() + " ms");

        GridPane gridPane = new GridPane();

        // Adding elements to grid pane
        gridPane.setPadding(new Insets(10));
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.add(textFieldLabel, 0, 0);
        gridPane.add(jobNameField, 1, 0);
        gridPane.add(spinner1, 1, 1);
        gridPane.add(spinner2, 1, 2);
        gridPane.add(spinner1Label, 0, 1);
        gridPane.add(spinner2Label, 0, 2);

        // Radio buttons for interval settings
        ToggleGroup toggleGroup = new ToggleGroup();
        RadioButton interval1 = new RadioButton("1");
        RadioButton interval3 = new RadioButton("3");
        RadioButton interval6 = new RadioButton("6");
        RadioButton interval12 = new RadioButton("12");

        // Adding radio buttons to toggle group
        interval1.setToggleGroup(toggleGroup);
        interval3.setToggleGroup(toggleGroup);
        interval6.setToggleGroup(toggleGroup);
        interval12.setToggleGroup(toggleGroup);

        // Event handler for radio buttons
        toggleGroup.selectedToggleProperty().addListener((observable, prevButton, newButton) -> {
            if (newButton != null) {
                selectedRadioButton = (RadioButton) newButton; // Store the selected radio button
            }
        });

        Slider noteDurationSlider = new Slider(100, 5000, job.getNoteDuration());
        Slider noteDecaySlider = new Slider(100, 4500, job.getNoteDecay());
        Slider gapBtwNotesSlider = new Slider(100, 500, job.getNoteGap());

        noteDurationSlider.setShowTickMarks(true);
        noteDurationSlider.setMajorTickUnit(100);
        noteDurationSlider.setBlockIncrement(100);

        noteDecaySlider.setShowTickMarks(true);
        noteDecaySlider.setMajorTickUnit(100);
        noteDecaySlider.setBlockIncrement(100);

        gapBtwNotesSlider.setShowTickMarks(true);
        gapBtwNotesSlider.setMajorTickUnit(100);
        gapBtwNotesSlider.setBlockIncrement(100);

        this.jobs = new ArrayList<>();
        this.jobs.add(this.job.toString()); // setting initial project to job list view

        // Updating job with button click
        updateJobBtn.setOnAction(event -> {
            String newName = jobNameField.getText().trim();
            int fromNote = spinner1.getValue();
            int toNote = spinner2.getValue();
            if (!newName.isEmpty() && newName.length() < 21) {
                job.setName(newName);
                job.setFromNote(fromNote);
                job.setToNote(toNote);

                // Accessing selected radio button
                if (selectedRadioButton != null) {
                    if (selectedRadioButton.equals(interval1)) {
                        job.setInterval(Job.Interval.ONE);
                    } else if (selectedRadioButton.equals(interval3)) {
                        job.setInterval(Job.Interval.THREE);
                    } else if (selectedRadioButton.equals(interval6)) {
                        job.setInterval(Job.Interval.SIX);
                    } else if (selectedRadioButton.equals(interval12)) {
                        job.setInterval(Job.Interval.TWELVE);
                    }
                }
                this.jobs.add(this.job.toString());
                this.observableJobList.clear();
                this.observableJobList.addAll(this.jobs);
                this.jobListView = new ListView<>(this.observableJobList);
                createNoteTable();
            }
        });

        // Changing job name with ENTER key, when text field is in focus
        jobNameField.setOnKeyPressed(event -> {
            String newName = jobNameField.getText().trim();
            int fromNote = spinner1.getValue();
            int toNote = spinner2.getValue();
            if (!newName.isEmpty() && newName.length() < 21) {
                if (event.getCode() == KeyCode.ENTER) {
                    job.setName(newName);
                    job.setFromNote(fromNote);
                    job.setToNote(toNote);

                    // Accessing selected radio button
                    if (selectedRadioButton != null) {
                        if (selectedRadioButton.equals(interval1)) {
                            job.setInterval(Job.Interval.ONE);
                        } else if (selectedRadioButton.equals(interval3)) {
                            job.setInterval(Job.Interval.THREE);
                        } else if (selectedRadioButton.equals(interval6)) {
                            job.setInterval(Job.Interval.SIX);
                        } else if (selectedRadioButton.equals(interval12)) {
                            job.setInterval(Job.Interval.TWELVE);
                        }
                    }
                    this.jobs.add(this.job.toString());
                    this.observableJobList.clear();
                    this.observableJobList.addAll(this.jobs);
                    this.jobListView = new ListView<>(this.observableJobList);
//                    System.out.println(job.toString());
                }
            }
        });

        // Creating canvas
        int height = 30;
        Canvas noteTimingCanvas = new Canvas(500, height);
        GraphicsContext gc = noteTimingCanvas.getGraphicsContext2D();
        gc.setFill(Color.LIGHTGRAY);
        gc.fillRect(
                0,
                0,
                noteTimingCanvas.getWidth(),
                noteTimingCanvas.getHeight());

        gc.setFill(Color.BLUE);
        int durationWidth = job.getNoteDuration() / 20;
        gc.fillRect(0, 0, durationWidth, height);
        gc.strokeRect(0, 0, durationWidth, height);

        gc.setFill(Color.LIGHTBLUE);
        int decayWidth = job.getNoteDecay() / 20;
        gc.fillRect(durationWidth, 0, decayWidth, height);
        gc.strokeRect(durationWidth, 0, decayWidth, height);

        gc.setFill(Color.GRAY);
        int gapWidth = job.getNoteGap() / 20;
        gc.fillRect(durationWidth + decayWidth, 0, gapWidth, height);
        gc.strokeRect(durationWidth + decayWidth, 0, gapWidth, height);

        // Event handlers to update slider labels
        noteDurationSlider.valueProperty().addListener((ov, oldValue, newValue) -> {
            int duration = newValue.intValue();
            job.setNoteDuration(duration);
            noteDurationLabel.setText("Note Duration: " + duration + " ms");

            // Redrawing canvas
            gc.clearRect((job.getNoteDuration() + job.getNoteDecay() + job.getNoteGap()) / 20.0, 0, noteTimingCanvas.getWidth(), noteTimingCanvas.getHeight());
            gc.setFill(Color.LIGHTGRAY);
            gc.fillRect((job.getNoteDuration() + job.getNoteDecay() + job.getNoteGap()) / 20.0, 0, noteTimingCanvas.getWidth(), noteTimingCanvas.getHeight());

            // Drawing duration block
            gc.setFill(Color.BLUE);
            final int durationW = duration / 20;
            gc.fillRect(0, 0, durationW, height);
            gc.strokeRect(0, 0, durationW, height);

            // Below decay and gap blocks are drawn here in order to retain their color when duration block is changed
            // Drawing decay block
            gc.setFill(Color.LIGHTBLUE);
            final int decayW = job.getNoteDecay() / 20;
            gc.fillRect(job.getNoteDuration() / 20.0, 0, decayW, height);
            gc.strokeRect(job.getNoteDuration() / 20.0, 0, decayW, height);

            // Drawing gap block
            gc.setFill(Color.GRAY);
            final int gapW = job.getNoteGap() / 20;
            gc.fillRect((job.getNoteDuration() + job.getNoteDecay()) / 20.0, 0, gapW, height);
            gc.strokeRect((job.getNoteDuration() + job.getNoteDecay()) / 20.0, 0, gapW, height);

//            System.out.println(job.toString());
        });

        noteDecaySlider.valueProperty().addListener((ov, oldValue, newValue) -> {
            int decay = newValue.intValue();
            job.setNoteDecay(decay);
            noteDecayLabel.setText("Note Decay: " + decay + " ms");

            // Redrawing canvas
            gc.clearRect((job.getNoteDuration() + job.getNoteDecay() + job.getNoteGap()) / 20.0, 0, noteTimingCanvas.getWidth(), noteTimingCanvas.getHeight());
            gc.setFill(Color.LIGHTGRAY);
            gc.fillRect((job.getNoteDuration() + job.getNoteDecay() + job.getNoteGap()) / 20.0, 0, noteTimingCanvas.getWidth(), noteTimingCanvas.getHeight());

            // Drawing decay block
            gc.setFill(Color.LIGHTBLUE);
            final int decayW = job.getNoteDecay() / 20;
            gc.fillRect(job.getNoteDuration() / 20.0, 0, decayW, height);
            gc.strokeRect(job.getNoteDuration() / 20.0, 0, decayW, height);

            // Below gap block is drawn here in order to retain its color when decay block is changed
            // Drawing gap block
            gc.setFill(Color.GRAY);
            final int gapW = job.getNoteGap() / 20;
            gc.fillRect((job.getNoteDuration() + job.getNoteDecay()) / 20.0, 0, gapW, height);
            gc.strokeRect((job.getNoteDuration() + job.getNoteDecay()) / 20.0, 0, gapW, height);

//            System.out.println(job.toString());
        });

        gapBtwNotesSlider.valueProperty().addListener((ov, oldValue, newValue) -> {
            int gap = newValue.intValue();
            job.setNoteGap(gap);
            gapBtwNotesLabel.setText("Note Gap: " + gap + " ms");

            // Redrawing canvas
            gc.clearRect((job.getNoteDuration() + job.getNoteDecay() + job.getNoteGap()) / 20.0, 0, noteTimingCanvas.getWidth(), noteTimingCanvas.getHeight());
            gc.setFill(Color.LIGHTGRAY);
            gc.fillRect((job.getNoteDuration() + job.getNoteDecay() + job.getNoteGap()) / 20.0, 0, noteTimingCanvas.getWidth(), noteTimingCanvas.getHeight());

            // Drawing gap block
            gc.setFill(Color.GRAY);
            final int gapW = job.getNoteGap() / 20;
            gc.fillRect((job.getNoteDuration() + job.getNoteDecay()) / 20.0, 0, gapW, height);
            gc.strokeRect((job.getNoteDuration() + job.getNoteDecay()) / 20.0, 0, gapW, height);

//            System.out.println(job.toString());
        });

        HBox hbox = new HBox(10);
        hbox.setAlignment(Pos.CENTER);
        hbox.setPadding(new Insets(10));
        hbox.getChildren().addAll(interval1, interval3, interval6, interval12);

        VBox slidersVbox = new VBox(10);
        slidersVbox.setPadding(new Insets(5));
        slidersVbox.getChildren().addAll(noteDurationLabel, noteDurationSlider, noteDecayLabel, noteDecaySlider, gapBtwNotesLabel, gapBtwNotesSlider, noteTimingCanvas);

        TitledPane titledPane = new TitledPane("Interval Settings", hbox);
        TitledPane slidersTitlePane = new TitledPane("Note times", slidersVbox);

        gridPane.add(titledPane, 0, 3, 2, 1);
        gridPane.add(slidersTitlePane, 0, 4, 2, 1);
        gridPane.add(updateJobBtn, 0, 5, 2, 1);

        GridPane.setHalignment(updateJobBtn, HPos.CENTER);

        createNoteTable();
        createJobList();

        SplitPane splitPaneWithTable = new SplitPane();
        splitPaneWithTable.setOrientation(Orientation.VERTICAL); // orientation of pane
        splitPaneWithTable.setDividerPositions(0.25); // how much width occupies on window
        splitPaneWithTable.getItems().addAll(gridPane, this.tableBox);

        SplitPane rootSplitPane = new SplitPane();
        rootSplitPane.setOrientation(Orientation.HORIZONTAL); // orientation of pane
        rootSplitPane.setDividerPositions(0.3); // how much width occupies on window
        rootSplitPane.getItems().addAll(this.jobListView, splitPaneWithTable);

        Scene scene = new Scene(rootSplitPane, 800, 700);
        stage.setScene(scene);
        stage.setTitle("Job UpdaterV3");
        stage.show();
    }



    public static void main(String[] args) {
        launch();
    }



    public void createNoteTable() {
        List<Integer> velocities = new ArrayList<>();
        velocities.add(90);
        this.job.setSpecificVelocities(velocities);
//        System.out.println(this.job);
        this.notes = new ArrayList<>();
        int noteStartTime = 0;
        int noteEndTime = 0;
        for (int note : this.job.getNotes()) {
            for (int velocity : this.job.getVelocities()) {
                noteEndTime = noteStartTime + this.job.getNoteDuration();
                Note n = new Note(note, velocity, noteStartTime, noteEndTime);
                this.notes.add(n);
                noteStartTime += this.job.getNoteDuration() + this.job.getNoteDecay() + this.job.getNoteGap();
            }
        }
        System.out.println("Total number of notes: " + this.notes.size());

        this.noteList = FXCollections.observableArrayList();
        this.noteList.addAll(this.notes);

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
        System.out.println(this.noteList);
        this.noteTableView.setItems(this.noteList);
        this.tableBox = new VBox(this.noteTableView);
    }



    public void processListSelection(ObservableValue<? extends String> val, String oldValue, String newValue) {
        this.colorLabel.setText(newValue);
    }



    public void createJobList() {
        this.observableJobList = FXCollections.observableArrayList();
        this.observableJobList.addAll(this.jobs);
        this.jobListView = new ListView<>(this.observableJobList);
        this.jobListView.getSelectionModel().select(0); // initial/default selection inside the list
//        this.jobListView.getSelectionModel().selectedItemProperty().addListener(this::processListSelection); // Listens to what element in the list is selected
//        this.colorLabel = new Label(observableJobList.get(0)); // default value shown in pane
//        StackPane colorPane = new StackPane(this.colorLabel);
    }
}
