module org.jftf.jftfapp {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.jftf.jftfapp to javafx.fxml;
    exports org.jftf.jftfapp;
}