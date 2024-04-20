module org.app.noteproject {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.app.noteproject to javafx.fxml;
    exports org.app.noteproject;
}