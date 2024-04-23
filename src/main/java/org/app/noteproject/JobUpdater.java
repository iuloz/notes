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
    private int currentNoteDuration;
    private int currentNoteDecay;
    private int currentNoteGap;
    private int currentInterval = 6;
    private int spinner1Value;
    private int spinner2Value;


    @Override
    public void start(Stage stage) {
        this.jobNameField = new TextField();
        Button createNewJobBtn = new Button("Create new job");
        Button updateJobBtn = new Button("Update job");

        SpinnerValueFactory<Integer> valueFactory1 = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 127, 40, 1);
        SpinnerValueFactory<Integer> valueFactory2 = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 127, 120, 1);

        this.spinner1 = new Spinner<>();
        this.spinner2 = new Spinner<>();

        this.spinner1.setValueFactory(valueFactory1);
        this.spinner2.setValueFactory(valueFactory2);

        this.spinner1Value = this.job.getFromNote();
        this.spinner2Value = this.job.getToNote();

        // Listener for spinner 1
        this.spinner1.valueProperty().addListener((observable, oldValue, newValue) -> {
            this.spinner1Value = newValue;
            populateNoteTable();
        });

        // Listener for spinner 2
        this.spinner2.valueProperty().addListener((observable, oldValue, newValue) -> {
            this.spinner2Value = newValue;
            populateNoteTable();
        });

        Label textFieldLabel = new Label("Job name");
        Label spinner1Label = new Label("Start note");
        Label spinner2Label = new Label("End note");
        Label noteDurationLabel = new Label("Note duration: " + this.job.getNoteDuration() + " ms");
        Label noteDecayLabel = new Label("Note decay: " + this.job.getNoteDecay() + " ms");
        Label gapBtwNotesLabel = new Label("Gap between notes: " + this.job.getNoteGap() + " ms");

        GridPane gridPane = new GridPane();

        // Adding elements to grid pane
        gridPane.setPadding(new Insets(10));
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.add(textFieldLabel, 0, 0);
        gridPane.add(this.jobNameField, 1, 0);
        gridPane.add(this.spinner1, 1, 1);
        gridPane.add(this.spinner2, 1, 2);
        gridPane.add(spinner1Label, 0, 1);
        gridPane.add(spinner2Label, 0, 2);

        // Radio buttons for interval settings
        ToggleGroup toggleGroup = new ToggleGroup();
        this.interval1 = new RadioButton("1");
        this.interval3 = new RadioButton("3");
        this.interval6 = new RadioButton("6");
        this.interval12 = new RadioButton("12");

        // Adding radio buttons to toggle group
        this.interval1.setToggleGroup(toggleGroup);
        this.interval3.setToggleGroup(toggleGroup);
        this.interval6.setToggleGroup(toggleGroup);
        this.interval12.setToggleGroup(toggleGroup);

        this.interval6.setSelected(true);

        // Event handler for radio buttons
        toggleGroup.selectedToggleProperty().addListener((observable, prevButton, newButton) -> {
            if (newButton != null) {
                this.selectedRadioButton = (RadioButton) newButton; // Store the selected radio button
                if (this.selectedRadioButton.equals(this.interval1)) {
                    this.currentInterval = 1;
                } else if (this.selectedRadioButton.equals(this.interval3)) {
                    this.currentInterval = 3;
                } else if (this.selectedRadioButton.equals(this.interval6)) {
                    this.currentInterval = 6;
                } else if (this.selectedRadioButton.equals(this.interval12)) {
                    this.currentInterval = 12;
                }
                populateNoteTable();
            }
        });

        // Setting note timing sliders
        this.noteDurationSlider = new Slider(100, 5000, this.job.getNoteDuration());
        this.noteDecaySlider = new Slider(100, 4500, this.job.getNoteDecay());
        this.gapBtwNotesSlider = new Slider(100, 500, this.job.getNoteGap());

        this.noteDurationSlider.setShowTickMarks(true);
        this.noteDurationSlider.setMajorTickUnit(100);
        this.noteDurationSlider.setBlockIncrement(100);

        this.noteDecaySlider.setShowTickMarks(true);
        this.noteDecaySlider.setMajorTickUnit(100);
        this.noteDecaySlider.setBlockIncrement(100);

        this.gapBtwNotesSlider.setShowTickMarks(true);
        this.gapBtwNotesSlider.setMajorTickUnit(100);
        this.gapBtwNotesSlider.setBlockIncrement(100);

        // Creating new job with button click
        createNewJobBtn.setOnAction(event -> {
            String newName = this.jobNameField.getText().trim();
            int fromNote = this.spinner1Value;
            int toNote = this.spinner2Value;
            if (!newName.isEmpty() && newName.length() < 21) {
                this.job = new Job(newName);
                this.job.setFromNote(fromNote);
                this.job.setToNote(toNote);
                this.job.setNoteDuration(this.currentNoteDuration);
                this.job.setNoteDecay(this.currentNoteDecay);
                this.job.setNoteGap(this.currentNoteGap);

                // Accessing selected radio button
                if (this.selectedRadioButton != null) {
                    if (this.selectedRadioButton.equals(this.interval1)) {
                        this.job.setInterval(Job.Interval.ONE);
                    } else if (this.selectedRadioButton.equals(this.interval3)) {
                        this.job.setInterval(Job.Interval.THREE);
                    } else if (this.selectedRadioButton.equals(this.interval6)) {
                        this.job.setInterval(Job.Interval.SIX);
                    } else if (this.selectedRadioButton.equals(this.interval12)) {
                        this.job.setInterval(Job.Interval.TWELVE);
                    }
                }
                this.jobs.add(this.job);
                this.observableJobList.add(this.job.getName()); // Adding the name of newly created job
                this.jobListView.getSelectionModel().selectLast(); // Selecting the newly added job
                populateNoteTable(); // Update the note table
            }
        });

        // Creating new job with ENTER key, when text field is in focus
        this.jobNameField.setOnKeyPressed(event -> {
            String newName = this.jobNameField.getText().trim();
            int fromNote = this.spinner1Value;
            int toNote = this.spinner2Value;
            if (!newName.isEmpty() && newName.length() < 21) {
                if (event.getCode() == KeyCode.ENTER) {
                    this.job = new Job(newName);
                    this.job.setFromNote(fromNote);
                    this.job.setToNote(toNote);
                    this.job.setNoteDuration(this.currentNoteDuration);
                    this.job.setNoteDecay(this.currentNoteDecay);
                    this.job.setNoteGap(this.currentNoteGap);

                    // Accessing selected radio button
                    if (this.selectedRadioButton != null) {
                        if (this.selectedRadioButton.equals(this.interval1)) {
                            this.job.setInterval(Job.Interval.ONE);
                        } else if (this.selectedRadioButton.equals(this.interval3)) {
                            this.job.setInterval(Job.Interval.THREE);
                        } else if (this.selectedRadioButton.equals(this.interval6)) {
                            this.job.setInterval(Job.Interval.SIX);
                        } else if (this.selectedRadioButton.equals(this.interval12)) {
                            this.job.setInterval(Job.Interval.TWELVE);
                        }
                    }
                    this.jobs.add(this.job);
                    this.observableJobList.add(this.job.getName()); // Adding the name of newly created job
                    this.jobListView.getSelectionModel().selectLast(); // Selecting the newly added job
                    populateNoteTable(); // Update the note table
                }
            }
        });

        // Updating selected job
        updateJobBtn.setOnAction(event -> {
            String newName = this.jobNameField.getText().trim();
            int fromNote = this.spinner1Value;
            int toNote = this.spinner2Value;
            if (!newName.isEmpty() && newName.length() < 21 && this.selectedJob != null) {
                this.selectedJob.setName(newName);
                this.selectedJob.setFromNote(fromNote);
                this.selectedJob.setToNote(toNote);
                this.selectedJob.setNoteDuration(this.currentNoteDuration);
                this.selectedJob.setNoteDecay(this.currentNoteDecay);
                this.selectedJob.setNoteGap(this.currentNoteGap);

                // Accessing selected radio button
                if (this.selectedRadioButton != null) {
                    if (this.selectedRadioButton.equals(this.interval1)) {
                        this.selectedJob.setInterval(Job.Interval.ONE);
                    } else if (this.selectedRadioButton.equals(this.interval3)) {
                        this.selectedJob.setInterval(Job.Interval.THREE);
                    } else if (this.selectedRadioButton.equals(this.interval6)) {
                        this.selectedJob.setInterval(Job.Interval.SIX);
                    } else if (this.selectedRadioButton.equals(this.interval12)) {
                        this.selectedJob.setInterval(Job.Interval.TWELVE);
                    }
                }
                this.observableJobList.set(this.jobListView.getSelectionModel().getSelectedIndex(), this.selectedJob.getName()); // Updating the observable list with the updated job name
                populateNoteTable(); // Update the note table
            }
        });


        // Creating canvas
        int height = 30;
        Canvas noteTimingCanvas = new Canvas(350, height);
        GraphicsContext gc = noteTimingCanvas.getGraphicsContext2D();
        gc.setFill(Color.LIGHTGRAY);
        gc.fillRect(
                0,
                0,
                noteTimingCanvas.getWidth(),
                noteTimingCanvas.getHeight());

        gc.setFill(Color.BLUE);
        int durationWidth = this.job.getNoteDuration() / 20;
        gc.fillRect(0, 0, durationWidth, height);
        gc.strokeRect(0, 0, durationWidth, height);

        gc.setFill(Color.LIGHTBLUE);
        int decayWidth = this.job.getNoteDecay() / 20;
        gc.fillRect(durationWidth, 0, decayWidth, height);
        gc.strokeRect(durationWidth, 0, decayWidth, height);

        gc.setFill(Color.GRAY);
        int gapWidth = this.job.getNoteGap() / 20;
        gc.fillRect(durationWidth + decayWidth, 0, gapWidth, height);
        gc.strokeRect(durationWidth + decayWidth, 0, gapWidth, height);

        // Default note timings to be shown initially in note table
        this.currentNoteDuration = this.job.getNoteDuration();
        this.currentNoteDecay = this.job.getNoteDecay();
        this.currentNoteGap = this.job.getNoteGap();

        // Event handlers to update slider labels and graphical representation
        this.noteDurationSlider.valueProperty().addListener((ov, oldValue, newValue) -> {
            int duration = newValue.intValue();
            this.currentNoteDuration = duration;
            noteDurationLabel.setText("Note Duration: " + duration + " ms");

            // Redrawing canvas
            gc.clearRect((this.currentNoteDuration + this.currentNoteDecay + this.currentNoteGap) / 20.0, 0, noteTimingCanvas.getWidth(), noteTimingCanvas.getHeight());
            gc.setFill(Color.LIGHTGRAY);
            gc.fillRect((this.currentNoteDuration + this.currentNoteDecay + this.currentNoteGap) / 20.0, 0, noteTimingCanvas.getWidth(), noteTimingCanvas.getHeight());

            // Drawing duration block
            gc.setFill(Color.BLUE);
            final int durationW = duration / 20;
            gc.fillRect(0, 0, durationW, height);
            gc.strokeRect(0, 0, durationW, height);

            // Below decay and gap blocks are drawn here in order to retain their color when duration block is changed
            // Drawing decay block
            gc.setFill(Color.LIGHTBLUE);
            final int decayW = this.currentNoteDecay / 20;
            gc.fillRect(this.currentNoteDuration / 20.0, 0, decayW, height);
            gc.strokeRect(this.currentNoteDuration / 20.0, 0, decayW, height);

            // Drawing gap block
            gc.setFill(Color.GRAY);
            final int gapW = this.currentNoteGap / 20;
            gc.fillRect((this.currentNoteDuration + this.currentNoteDecay) / 20.0, 0, gapW, height);
            gc.strokeRect((this.currentNoteDuration + this.currentNoteDecay) / 20.0, 0, gapW, height);

            populateNoteTable();
        });

        this.noteDecaySlider.valueProperty().addListener((ov, oldValue, newValue) -> {
            int decay = newValue.intValue();
            this.currentNoteDecay = decay;
            noteDecayLabel.setText("Note Decay: " + decay + " ms");

            // Redrawing canvas
            gc.clearRect((this.currentNoteDuration + this.currentNoteDecay + this.currentNoteGap) / 20.0, 0, noteTimingCanvas.getWidth(), noteTimingCanvas.getHeight());
            gc.setFill(Color.LIGHTGRAY);
            gc.fillRect((this.currentNoteDuration + this.currentNoteDecay + this.currentNoteGap) / 20.0, 0, noteTimingCanvas.getWidth(), noteTimingCanvas.getHeight());

            // Drawing decay block
            gc.setFill(Color.LIGHTBLUE);
            final int decayW = this.currentNoteDecay / 20;
            gc.fillRect(this.currentNoteDuration / 20.0, 0, decayW, height);
            gc.strokeRect(this.currentNoteDuration / 20.0, 0, decayW, height);

            // Below gap block is drawn here in order to retain its color when decay block is changed
            // Drawing gap block
            gc.setFill(Color.GRAY);
            final int gapW = this.currentNoteGap / 20;
            gc.fillRect((this.currentNoteDuration + this.currentNoteDecay) / 20.0, 0, gapW, height);
            gc.strokeRect((this.currentNoteDuration + this.currentNoteDecay) / 20.0, 0, gapW, height);

            populateNoteTable();
        });

        this.gapBtwNotesSlider.valueProperty().addListener((ov, oldValue, newValue) -> {
            int gap = newValue.intValue();
            this.currentNoteGap = gap;
            gapBtwNotesLabel.setText("Note Gap: " + gap + " ms");

            // Redrawing canvas
            gc.clearRect((this.currentNoteDuration + this.currentNoteDecay + this.currentNoteGap) / 20.0, 0, noteTimingCanvas.getWidth(), noteTimingCanvas.getHeight());
            gc.setFill(Color.LIGHTGRAY);
            gc.fillRect((this.currentNoteDuration + this.currentNoteDecay + this.currentNoteGap) / 20.0, 0, noteTimingCanvas.getWidth(), noteTimingCanvas.getHeight());

            // Drawing gap block
            gc.setFill(Color.GRAY);
            final int gapW = this.currentNoteGap / 20;
            gc.fillRect((this.currentNoteDuration + this.currentNoteDecay) / 20.0, 0, gapW, height);
            gc.strokeRect((this.currentNoteDuration + this.currentNoteDecay) / 20.0, 0, gapW, height);

            populateNoteTable();
        });

        // Horizontal box to store radio buttons
        HBox hbox = new HBox(10);
        hbox.setAlignment(Pos.CENTER);
        hbox.setPadding(new Insets(10));
        hbox.getChildren().addAll(this.interval1, this.interval3, this.interval6, this.interval12);

        // Vertical box to store sliders and their labels
        VBox slidersVbox = new VBox(10);
        slidersVbox.setPadding(new Insets(5));
        slidersVbox.getChildren().addAll(noteDurationLabel, this.noteDurationSlider, noteDecayLabel, this.noteDecaySlider, gapBtwNotesLabel, this.gapBtwNotesSlider, noteTimingCanvas);

        TitledPane titledPane = new TitledPane("Interval Settings", hbox);
        TitledPane slidersTitlePane = new TitledPane("Note times", slidersVbox);

        // Horizontal box to store buttons
        HBox buttonsBox = new HBox(20);
        buttonsBox.getChildren().addAll(updateJobBtn, createNewJobBtn);
        buttonsBox.setAlignment(Pos.CENTER);

        gridPane.add(titledPane, 0, 3, 2, 1);
        gridPane.add(slidersTitlePane, 0, 4, 2, 1);
        gridPane.add(buttonsBox, 1, 5, 2, 1);

        // Initial creation of note table and job list on app start
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

        Scene scene = new Scene(rootSplitPane, 600, 700);
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
        List<Integer> notes = new ArrayList<>();
        int fromNote = this.spinner1Value;
        int toNote = this.spinner2Value;
        while (fromNote <= toNote) {
            notes.add(fromNote);
            fromNote += this.currentInterval;
        }
        List<Note> notesToTable = new ArrayList<>();
        int noteStartTime = 0;
        int noteEndTime = 0;
        if (this.selectedJob != null) { // when interacting with selected job from the job list
            for (int note : notes) {
                for (int velocity : this.selectedJob.getVelocities()) {
                    noteEndTime = noteStartTime + this.currentNoteDuration;
                    Note n = new Note(note, velocity, noteStartTime, noteEndTime);
                    notesToTable.add(n);
                    noteStartTime += this.currentNoteDuration + this.currentNoteDecay + this.currentNoteGap;
                }
            }
        } else { // when no jobs created yet
            for (int note : notes) {
                for (int velocity : this.job.getVelocities()) {
                    noteEndTime = noteStartTime + this.currentNoteDuration;
                    Note n = new Note(note, velocity, noteStartTime, noteEndTime);
                    notesToTable.add(n);
                    noteStartTime += this.currentNoteDuration + this.currentNoteDecay + this.currentNoteGap;
                }
            }
        }
        ObservableList<Note> noteList = FXCollections.observableArrayList();
        noteList.addAll(notesToTable);

        // Clearing and updating the noteTableView
        this.noteTableView.getItems().clear();
        this.noteTableView.getItems().addAll(noteList);
    }


    public void createJobList() {
        this.observableJobList = FXCollections.observableArrayList();
        for (Job job : this.jobs) {
            this.observableJobList.add(job.getName());
        }
        this.jobListView = new ListView<>(this.observableJobList);
        this.jobListView.getSelectionModel().selectedItemProperty().addListener(this::processListSelection); // Listens to what element in the list is selected
    }

    // Searches for the job in the list of jobs by its name to determine selected job
    public Job findJobByName(String name) {
        for (Job job : this.jobs) {
            if (job.getName().equals(name)) {
                return job;
            }
        }
        return null; // Job not found
    }

    // Sets values in job edit view (sliders, name, radio buttons, etc.) for selected job
    public void processListSelection(ObservableValue<? extends String> val, String oldValue, String newValue) {
        // Find the selected job by its name
        this.selectedJob = findJobByName(newValue);
        if (this.selectedJob != null) {
            this.jobNameField.setText(this.selectedJob.getName());
            this.spinner1.getValueFactory().setValue(this.selectedJob.getFromNote());
            this.spinner2.getValueFactory().setValue(this.selectedJob.getToNote());
            // Update interval radio buttons
            switch (this.selectedJob.getInterval()) {
                case ONE:
                    this.interval1.setSelected(true);
                    this.currentInterval = 1;
                    break;
                case THREE:
                    this.interval3.setSelected(true);
                    this.currentInterval = 3;
                    break;
                case SIX:
                    this.interval6.setSelected(true);
                    this.currentInterval = 6;
                    break;
                case TWELVE:
                    this.interval12.setSelected(true);
                    this.currentInterval = 12;
                    break;
            }
            // Update sliders
            this.noteDurationSlider.setValue(this.selectedJob.getNoteDuration());
            this.noteDecaySlider.setValue(this.selectedJob.getNoteDecay());
            this.gapBtwNotesSlider.setValue(this.selectedJob.getNoteGap());

            // Update note table
            populateNoteTable();
        }
    }
}
