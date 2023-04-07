module com.example.dijkstras_algorithm_project {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.dijkstras_algorithm_project to javafx.fxml;
    exports com.example.dijkstras_algorithm_project;
}