module jftf.app {
    requires javafx.controls;
    requires javafx.fxml;
    requires jftf.core;
    requires jftf.lib;
    requires org.apache.commons.io;

    opens jftf.app to javafx.fxml;
    exports jftf.app;
}