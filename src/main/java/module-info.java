module com.example.redesneurais {
    requires javafx.controls;
    requires javafx.fxml;

    requires net.synedra.validatorfx;
    requires org.kordamp.bootstrapfx.core;

    opens com.example.redesneurais to javafx.fxml;
    exports com.example.redesneurais;
    exports com.example.redesneurais.entities;
    opens com.example.redesneurais.entities to javafx.fxml;
}