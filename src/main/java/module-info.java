module org.app.noteproject {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens org.app.noteproject to javafx.fxml;
    exports org.app.noteproject;
}
