package jftf.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class JftfControlApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        stage = FXMLLoader.load(Objects.requireNonNull(JftfControlApplication.class.getResource("jftf_control.fxml")));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}