module jftf.app {
    requires javafx.controls;
    requires javafx.fxml;

    opens jftf.app to javafx.fxml;
    exports jftf.app;
}