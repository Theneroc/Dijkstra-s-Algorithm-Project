package com.example.dijkstras_algorithm_project;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.LinkedList;

public class Driver extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Driver.class.getResource("dijkstra.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1204, 793.5);
        stage.setTitle("Dijkstra Project by Khalil Khawaja");
        stage.setScene(scene);
        stage.setMaximized(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch();

    }

}