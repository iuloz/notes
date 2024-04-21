package org.app.noteproject;

import javafx.application.Application;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
    private VBox tableBox;
    private List<Job> jobs = new ArrayList<>();
    private ObservableList<String> observableJobList;
    private TextField jobNameField;
    private Spinner<Integer> spinner1;
    private Spinner<Integer> spinner2;
    private RadioButton interval1;
    private RadioButton interval3;
    private RadioButton interval6;
    private RadioButton interval12;
    private Slider noteDurationSlider;
    private Slider noteDecaySlider;
    private Slider gapBtwNotesSlider;
    private Job selectedJob;


    @Override
    public void start(Stage stage) {
        jobNameField = new TextField();
        Button createNewJobBtn = new Button("Create new job");
        Button updateJobBtn = new Button("Update job");

        SpinnerValueFactory<Integer> valueFactory1 = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 127, 40, 1);
        SpinnerValueFactory<Integer> valueFactory2 = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 127, 120, 1);

        spinner1 = new Spinner<>();
        spinner2 = new Spinner<>();

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
        interval1 = new RadioButton("1");
        interval3 = new RadioButton("3");
        interval6 = new RadioButton("6");
        interval12 = new RadioButton("12");

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

        noteDurationSlider = new Slider(100, 5000, job.getNoteDuration());
        noteDecaySlider = new Slider(100, 4500, job.getNoteDecay());
        gapBtwNotesSlider = new Slider(100, 500, job.getNoteGap());

        noteDurationSlider.setShowTickMarks(true);
        noteDurationSlider.setMajorTickUnit(100);
        noteDurationSlider.setBlockIncrement(100);

        noteDecaySlider.setShowTickMarks(true);
        noteDecaySlider.setMajorTickUnit(100);
        noteDecaySlider.setBlockIncrement(100);

        gapBtwNotesSlider.setShowTickMarks(true);
        gapBtwNotesSlider.setMajorTickUnit(100);
        gapBtwNotesSlider.setBlockIncrement(100);

//        this.jobs.add(this.job); // setting initial project to job list view

        // Updating job with button click
        createNewJobBtn.setOnAction(event -> {
            String newName = jobNameField.getText().trim();
            int fromNote = spinner1.getValue();
            int toNote = spinner2.getValue();
            int noteDuration = job.getNoteDuration();
            int noteDecay = job.getNoteDecay();
            int noteGap = job.getNoteGap();
            if (!newName.isEmpty() && newName.length() < 21) {
                this.job = new Job(newName);
                job.setFromNote(fromNote);
                job.setToNote(toNote);
                job.setNoteDuration(noteDuration);
                job.setNoteDecay(noteDecay);
                job.setNoteGap(noteGap);

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
                this.jobs.add(this.job);
                this.observableJobList.add(this.job.getName()); // Add only the name
                jobListView.getSelectionModel().selectLast(); // Select the newly added job
                populateNoteTable(); // Update the note table
            }
        });


        // Creating job with ENTER key, when text field is in focus
        jobNameField.setOnKeyPressed(event -> {
            String newName = jobNameField.getText().trim();
            int fromNote = spinner1.getValue();
            int toNote = spinner2.getValue();
            if (!newName.isEmpty() && newName.length() < 21) {
                if (event.getCode() == KeyCode.ENTER) {
                    this.job = new Job(newName);
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
                    this.jobs.add(this.job);
                    this.observableJobList.add(this.job.getName()); // Add only the name
                    jobListView.getSelectionModel().selectLast(); // Select the newly added job
                    populateNoteTable(); // Update the note table
                }
            }
        });

        updateJobBtn.setOnAction(event -> {
            String newName = jobNameField.getText().trim();
            int fromNote = spinner1.getValue();
            int toNote = spinner2.getValue();
            if (!newName.isEmpty() && newName.length() < 21 && selectedJob != null) {
                selectedJob.setName(newName);
                selectedJob.setFromNote(fromNote);
                selectedJob.setToNote(toNote);

                // Accessing selected radio button
                if (selectedRadioButton != null) {
                    if (selectedRadioButton.equals(interval1)) {
                        selectedJob.setInterval(Job.Interval.ONE);
                    } else if (selectedRadioButton.equals(interval3)) {
                        selectedJob.setInterval(Job.Interval.THREE);
                    } else if (selectedRadioButton.equals(interval6)) {
                        selectedJob.setInterval(Job.Interval.SIX);
                    } else if (selectedRadioButton.equals(interval12)) {
                        selectedJob.setInterval(Job.Interval.TWELVE);
                    }
                }

                // Update the observable list with the updated job name
                observableJobList.set(jobListView.getSelectionModel().getSelectedIndex(), selectedJob.getName());
                populateNoteTable(); // Update the note table
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

        HBox buttonsBox = new HBox(20);
        buttonsBox.getChildren().addAll(updateJobBtn, createNewJobBtn);
        buttonsBox.setAlignment(Pos.CENTER);

        gridPane.add(titledPane, 0, 3, 2, 1);
        gridPane.add(slidersTitlePane, 0, 4, 2, 1);
        gridPane.add(buttonsBox, 1, 5, 2, 1);

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
        // Create the TableView
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

        // Populate TableView with data
        populateNoteTable();

        this.tableBox = new VBox(this.noteTableView);
    }


    public void populateNoteTable() {
        if (selectedJob != null) {
            List<Note> notes = new ArrayList<>();
            int noteStartTime = 0;
            int noteEndTime = 0;
            for (int note : this.selectedJob.getNotes()) {
                for (int velocity : this.selectedJob.getVelocities()) {
                    noteEndTime = noteStartTime + this.selectedJob.getNoteDuration();
                    Note n = new Note(note, velocity, noteStartTime, noteEndTime);
                    notes.add(n);
                    noteStartTime += this.selectedJob.getNoteDuration() + this.selectedJob.getNoteDecay() + this.selectedJob.getNoteGap();
                }
            }
            System.out.println("Total number of notes: " + notes.size());

            ObservableList<Note> noteList = FXCollections.observableArrayList();
            noteList.addAll(notes);

            // Clearing and updating the noteTableView
            this.noteTableView.getItems().clear();
            this.noteTableView.getItems().addAll(noteList);
        }
    }


    public void createJobList() {
        this.observableJobList = FXCollections.observableArrayList();
        for (Job job : this.jobs) {
            this.observableJobList.add(job.getName());
        }
        this.jobListView = new ListView<>(this.observableJobList);
        this.jobListView.getSelectionModel().select(0); // initial/default selection inside the list
        this.jobListView.getSelectionModel().selectedItemProperty().addListener(this::processListSelection); // Listens to what element in the list is selected
//        this.colorLabel = new Label(observableJobList.get(0)); // default value shown in pane
//        StackPane colorPane = new StackPane(this.colorLabel);
    }


    public Job findJobByName(String name) {
        // Search for the job in the list of jobs by its name
        for (Job job : jobs) {
            if (job.getName().equals(name)) {
                return job;
            }
        }
        return null; // Job not found
    }


    public void processListSelection(ObservableValue<? extends String> val, String oldValue, String newValue) {
        // Find the selected job by its name
        selectedJob = findJobByName(newValue);
        if (selectedJob != null) {
            System.out.println(selectedJob.getNoteDuration());
            // Update text fields, sliders, labels, etc. with values from the selected job
            jobNameField.setText(selectedJob.getName());
            spinner1.getValueFactory().setValue(selectedJob.getFromNote());
            spinner2.getValueFactory().setValue(selectedJob.getToNote());
            // Update interval radio buttons
            switch (selectedJob.getInterval()) {
                case ONE:
                    interval1.setSelected(true);
                    break;
                case THREE:
                    interval3.setSelected(true);
                    break;
                case SIX:
                    interval6.setSelected(true);
                    break;
                case TWELVE:
                    interval12.setSelected(true);
                    break;
            }
            // Update sliders
            noteDurationSlider.setValue(selectedJob.getNoteDuration());
            noteDecaySlider.setValue(selectedJob.getNoteDecay());
            gapBtwNotesSlider.setValue(selectedJob.getNoteGap());

            // Update note table
            populateNoteTable();
        }
    }
}
