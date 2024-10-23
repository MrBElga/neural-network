module com.example.redeneural {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.redeneural to javafx.fxml;
    exports com.example.redeneural;
}