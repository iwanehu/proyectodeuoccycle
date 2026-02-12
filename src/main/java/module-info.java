module edu.uoc.uocycle {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.json;
    requires com.google.gson;

    opens edu.uoc.uocycle.view to javafx.fxml;
    exports edu.uoc.uocycle;
}
