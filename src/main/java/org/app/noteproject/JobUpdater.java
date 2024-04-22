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
        jobNameField = new TextField();
        Button createNewJobBtn = new Button("Create new job");
        Button updateJobBtn = new Button("Update job");

        SpinnerValueFactory<Integer> valueFactory1 = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 127, 40, 1);
        SpinnerValueFactory<Integer> valueFactory2 = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 127, 120, 1);

        spinner1 = new Spinner<>();
        spinner2 = new Spinner<>();

        spinner1.setValueFactory(valueFactory1);
        spinner2.setValueFactory(valueFactory2);

        spinner1Value = job.getFromNote();
        spinner2Value = job.getToNote();

        // Listener for spinner 1
        spinner1.valueProperty().addListener((observable, oldValue, newValue) -> {
            spinner1Value = newValue;
            populateNoteTable();
        });

        // Listener for spinner 2
        spinner2.valueProperty().addListener((observable, oldValue, newValue) -> {
            spinner2Value = newValue;
            populateNoteTable();
        });

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

        interval6.setSelected(true);

        // Event handler for radio buttons
        toggleGroup.selectedToggleProperty().addListener((observable, prevButton, newButton) -> {
            if (newButton != null) {
                selectedRadioButton = (RadioButton) newButton; // Store the selected radio button
                if (selectedRadioButton.equals(interval1)) {
                    currentInterval = 1;
                } else if (selectedRadioButton.equals(interval3)) {
                    currentInterval = 3;
                } else if (selectedRadioButton.equals(interval6)) {
                    currentInterval = 6;
                } else if (selectedRadioButton.equals(interval12)) {
                    currentInterval = 12;
                }
                populateNoteTable();
            }
        });

        // Setting note timing sliders
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

        // Creating new job with button click
        createNewJobBtn.setOnAction(event -> {
            String newName = jobNameField.getText().trim();
            int fromNote = spinner1Value;
            int toNote = spinner2Value;
            if (!newName.isEmpty() && newName.length() < 21) {
                this.job = new Job(newName);
                job.setFromNote(fromNote);
                job.setToNote(toNote);
                job.setNoteDuration(currentNoteDuration);
                job.setNoteDecay(currentNoteDecay);
                job.setNoteGap(currentNoteGap);

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
                this.observableJobList.add(this.job.getName()); // Adding the name of newly created job
                jobListView.getSelectionModel().selectLast(); // Selecting the newly added job
                populateNoteTable(); // Update the note table
            }
        });

        // Creating new job with ENTER key, when text field is in focus
        jobNameField.setOnKeyPressed(event -> {
            String newName = jobNameField.getText().trim();
            int fromNote = spinner1Value;
            int toNote = spinner2Value;
            if (!newName.isEmpty() && newName.length() < 21) {
                if (event.getCode() == KeyCode.ENTER) {
                    this.job = new Job(newName);
                    job.setFromNote(fromNote);
                    job.setToNote(toNote);
                    job.setNoteDuration(currentNoteDuration);
                    job.setNoteDecay(currentNoteDecay);
                    job.setNoteGap(currentNoteGap);

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
                    this.observableJobList.add(this.job.getName()); // Adding the name of newly created job
                    jobListView.getSelectionModel().selectLast(); // Selecting the newly added job
                    populateNoteTable(); // Update the note table
                }
            }
        });

        // Updating selected job
        updateJobBtn.setOnAction(event -> {
            String newName = jobNameField.getText().trim();
            int fromNote = spinner1Value;
            int toNote = spinner2Value;
            if (!newName.isEmpty() && newName.length() < 21 && selectedJob != null) {
                selectedJob.setName(newName);
                selectedJob.setFromNote(fromNote);
                selectedJob.setToNote(toNote);
                selectedJob.setNoteDuration(currentNoteDuration);
                selectedJob.setNoteDecay(currentNoteDecay);
                selectedJob.setNoteGap(currentNoteGap);

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
                observableJobList.set(jobListView.getSelectionModel().getSelectedIndex(), selectedJob.getName()); // Updating the observable list with the updated job name
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

        // Default note timings to be shown initially in note table
        currentNoteDuration = job.getNoteDuration();
        currentNoteDecay = job.getNoteDecay();
        currentNoteGap = job.getNoteGap();

        // Event handlers to update slider labels and graphical representation
        noteDurationSlider.valueProperty().addListener((ov, oldValue, newValue) -> {
            int duration = newValue.intValue();
            currentNoteDuration = duration;
            noteDurationLabel.setText("Note Duration: " + duration + " ms");

            // Redrawing canvas
            gc.clearRect((currentNoteDuration + currentNoteDecay + currentNoteGap) / 20.0, 0, noteTimingCanvas.getWidth(), noteTimingCanvas.getHeight());
            gc.setFill(Color.LIGHTGRAY);
            gc.fillRect((currentNoteDuration + currentNoteDecay + currentNoteGap) / 20.0, 0, noteTimingCanvas.getWidth(), noteTimingCanvas.getHeight());

            // Drawing duration block
            gc.setFill(Color.BLUE);
            final int durationW = duration / 20;
            gc.fillRect(0, 0, durationW, height);
            gc.strokeRect(0, 0, durationW, height);

            // Below decay and gap blocks are drawn here in order to retain their color when duration block is changed
            // Drawing decay block
            gc.setFill(Color.LIGHTBLUE);
            final int decayW = currentNoteDecay / 20;
            gc.fillRect(currentNoteDuration / 20.0, 0, decayW, height);
            gc.strokeRect(currentNoteDuration / 20.0, 0, decayW, height);

            // Drawing gap block
            gc.setFill(Color.GRAY);
            final int gapW = currentNoteGap / 20;
            gc.fillRect((currentNoteDuration + currentNoteDecay) / 20.0, 0, gapW, height);
            gc.strokeRect((currentNoteDuration + currentNoteDecay) / 20.0, 0, gapW, height);

            populateNoteTable();
        });

        noteDecaySlider.valueProperty().addListener((ov, oldValue, newValue) -> {
            int decay = newValue.intValue();
            currentNoteDecay = decay;
            noteDecayLabel.setText("Note Decay: " + decay + " ms");

            // Redrawing canvas
            gc.clearRect((currentNoteDuration + currentNoteDecay + currentNoteGap) / 20.0, 0, noteTimingCanvas.getWidth(), noteTimingCanvas.getHeight());
            gc.setFill(Color.LIGHTGRAY);
            gc.fillRect((currentNoteDuration + currentNoteDecay + currentNoteGap) / 20.0, 0, noteTimingCanvas.getWidth(), noteTimingCanvas.getHeight());

            // Drawing decay block
            gc.setFill(Color.LIGHTBLUE);
            final int decayW = currentNoteDecay / 20;
            gc.fillRect(currentNoteDuration / 20.0, 0, decayW, height);
            gc.strokeRect(currentNoteDuration / 20.0, 0, decayW, height);

            // Below gap block is drawn here in order to retain its color when decay block is changed
            // Drawing gap block
            gc.setFill(Color.GRAY);
            final int gapW = currentNoteGap / 20;
            gc.fillRect((currentNoteDuration + currentNoteDecay) / 20.0, 0, gapW, height);
            gc.strokeRect((currentNoteDuration + currentNoteDecay) / 20.0, 0, gapW, height);

            populateNoteTable();
        });

        gapBtwNotesSlider.valueProperty().addListener((ov, oldValue, newValue) -> {
            int gap = newValue.intValue();
            currentNoteGap = gap;
            gapBtwNotesLabel.setText("Note Gap: " + gap + " ms");

            // Redrawing canvas
            gc.clearRect((currentNoteDuration + currentNoteDecay + currentNoteGap) / 20.0, 0, noteTimingCanvas.getWidth(), noteTimingCanvas.getHeight());
            gc.setFill(Color.LIGHTGRAY);
            gc.fillRect((currentNoteDuration + currentNoteDecay + currentNoteGap) / 20.0, 0, noteTimingCanvas.getWidth(), noteTimingCanvas.getHeight());

            // Drawing gap block
            gc.setFill(Color.GRAY);
            final int gapW = currentNoteGap / 20;
            gc.fillRect((currentNoteDuration + currentNoteDecay) / 20.0, 0, gapW, height);
            gc.strokeRect((currentNoteDuration + currentNoteDecay) / 20.0, 0, gapW, height);

            populateNoteTable();
        });

        // Horizontal box to store radio buttons
        HBox hbox = new HBox(10);
        hbox.setAlignment(Pos.CENTER);
        hbox.setPadding(new Insets(10));
        hbox.getChildren().addAll(interval1, interval3, interval6, interval12);

        // Vertical box to store sliders and their labels
        VBox slidersVbox = new VBox(10);
        slidersVbox.setPadding(new Insets(5));
        slidersVbox.getChildren().addAll(noteDurationLabel, noteDurationSlider, noteDecayLabel, noteDecaySlider, gapBtwNotesLabel, gapBtwNotesSlider, noteTimingCanvas);

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
        int fromNote = spinner1Value;
        int toNote = spinner2Value;
        while (fromNote <= toNote) {
            notes.add(fromNote);
            fromNote += currentInterval;
        }
        List<Note> notesToTable = new ArrayList<>();
        int noteStartTime = 0;
        int noteEndTime = 0;
        if (selectedJob != null) { // when interacting with selected job from the job list
            for (int note : notes) {
                for (int velocity : this.selectedJob.getVelocities()) {
                    noteEndTime = noteStartTime + currentNoteDuration;
                    Note n = new Note(note, velocity, noteStartTime, noteEndTime);
                    notesToTable.add(n);
                    noteStartTime += currentNoteDuration + currentNoteDecay + currentNoteGap;
                }
            }
        } else { // when no jobs created yet
            for (int note : notes) {
                for (int velocity : this.job.getVelocities()) {
                    noteEndTime = noteStartTime + currentNoteDuration;
                    Note n = new Note(note, velocity, noteStartTime, noteEndTime);
                    notesToTable.add(n);
                    noteStartTime += currentNoteDuration + currentNoteDecay + currentNoteGap;
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
        for (Job job : jobs) {
            if (job.getName().equals(name)) {
                return job;
            }
        }
        return null; // Job not found
    }

    // Sets values in job edit view (sliders, name, radio buttons, etc.) for selected job
    public void processListSelection(ObservableValue<? extends String> val, String oldValue, String newValue) {
        // Find the selected job by its name
        selectedJob = findJobByName(newValue);
        if (selectedJob != null) {
            jobNameField.setText(selectedJob.getName());
            spinner1.getValueFactory().setValue(selectedJob.getFromNote());
            spinner2.getValueFactory().setValue(selectedJob.getToNote());
            // Update interval radio buttons
            switch (selectedJob.getInterval()) {
                case ONE:
                    interval1.setSelected(true);
                    currentInterval = 1;
                    break;
                case THREE:
                    interval3.setSelected(true);
                    currentInterval = 3;
                    break;
                case SIX:
                    interval6.setSelected(true);
                    currentInterval = 6;
                    break;
                case TWELVE:
                    interval12.setSelected(true);
                    currentInterval = 12;
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
