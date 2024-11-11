package com.example.redesneurais;

import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;

public class HelloController {

    @FXML
    private RadioButton linearRadioButton;

    @FXML
    private RadioButton logisticaRadioButton;

    @FXML
    private RadioButton hiperbolicaRadioButton;

    private ToggleGroup transferFunctionGroup;

    @FXML
    public void initialize() {
        // Inicializa o ToggleGroup e associa os RadioButtons
        transferFunctionGroup = new ToggleGroup();
        linearRadioButton.setToggleGroup(transferFunctionGroup);
        logisticaRadioButton.setToggleGroup(transferFunctionGroup);
        hiperbolicaRadioButton.setToggleGroup(transferFunctionGroup);
    }
}
