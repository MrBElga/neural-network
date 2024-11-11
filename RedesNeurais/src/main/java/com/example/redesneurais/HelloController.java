package com.example.redesneurais;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class HelloController {

    @FXML
    private RadioButton linearRadioButton;

    @FXML
    private RadioButton logisticaRadioButton;

    @FXML
    private RadioButton hiperbolicaRadioButton;

    @FXML
    private Label filePathLabel;  // Label para exibir o caminho do arquivo

    private ToggleGroup transferFunctionGroup;

    @FXML
    public void initialize() {
        // Inicializa o ToggleGroup e associa os RadioButtons
        transferFunctionGroup = new ToggleGroup();
        linearRadioButton.setToggleGroup(transferFunctionGroup);
        logisticaRadioButton.setToggleGroup(transferFunctionGroup);
        hiperbolicaRadioButton.setToggleGroup(transferFunctionGroup);
    }

    @FXML
    private void handleCarregarArquivo() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));

        // Abre o seletor de arquivos
        File selectedFile = fileChooser.showOpenDialog(new Stage());

        if (selectedFile != null) {
            // Define o caminho do arquivo no Label
            filePathLabel.setText("Caminho do Arquivo: " + selectedFile.getAbsolutePath());

            // Chama o método para ler o arquivo CSV
            readCSVFile(selectedFile);
        } else {
            // Caso o usuário cancele a seleção, exibe uma mensagem padrão
            filePathLabel.setText("Nenhum arquivo selecionado.");
        }
    }

    // Método para ler o conteúdo do arquivo CSV
    private void readCSVFile(File file) {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Aqui processamos cada linha do CSV (exibe no console)
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
