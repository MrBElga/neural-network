package com.example.redesneurais;

import com.example.redesneurais.entities.Atributo;
import com.example.redesneurais.util.Controladora;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
//import javafx.scene.layout.ScrollPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class HelloController implements Initializable {

    public Button btTreino;
    @FXML
    private TextField txEntrada;
    @FXML
    private TextField txSaida;
    @FXML
    private TextField txOculta;
    @FXML
    private TextField txErro;
    @FXML
    private TextField txIteracoes;
    @FXML
    private TextField txAprendizado;
    @FXML
    private RadioButton chLinear;
    @FXML
    private RadioButton chLogistica;
    @FXML
    private RadioButton txHiperbolica;
    @FXML
    private RadioButton cbTeste;

    private File projeto = null;
    private Controladora control;
    @FXML
    private TextField txCaminho;
    @FXML
    private AnchorPane anchorPainel;


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        control = new Controladora();
        System.out.println("txEntrada is: " + txEntrada);
        valoresIniciais();
    }

    private void valoresIniciais() {
        txEntrada.setText("0");
        txSaida.setText("0");
        txCaminho.setText("nenhum arquivo");
        txOculta.setText("0");
        txAprendizado.setText("0.1");
        txErro.setText("0.01");
        txIteracoes.setText("2000");
    }

    @FXML
    private void evtCarregar(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("C:\\"));
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv")
        );
        projeto = fileChooser.showOpenDialog(null);
        if (projeto != null) {
            control.AbrirArquivo(projeto.getPath(), projeto.getName());
            txEntrada.setText(String.valueOf(control.getArq().getInputLayer()));
            txOculta.setText(String.valueOf(control.getArq().getHiddenLayer()));
            txSaida.setText(String.valueOf(control.getArq().getOutputLayer()));
            txCaminho.setText(projeto.getPath());
        } else {
            txCaminho.setText("Nenhum arquivo selecionado");
        }
    }

    @FXML
    private void handleCarregarArquivo(ActionEvent event) {
        // Implement file selection logic
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Selecione um Arquivo");
        File selectedFile = fileChooser.showOpenDialog(new Stage());
        if (selectedFile != null) {
            txCaminho.setText("Arquivo carregado: " + selectedFile.getAbsolutePath());
        } else {
            txCaminho.setText("Nenhum arquivo selecionado");
        }
    }

    private boolean validaDouble(String valor, String campo) {
        try {
            double parsedValue = Double.parseDouble(valor);
            if (parsedValue <= 0) {
                showAlert("Digite um valor maior que 0 para o campo: " + campo);
                return false;
            }
            return true;
        } catch (NumberFormatException e) {
            showAlert("Digite um valor válido para o campo: " + campo);
            return false;
        }
    }

    private boolean validaInt(String valor, String campo) {
        try {
            int parsedValue = Integer.parseInt(valor);
            if (parsedValue <= 0) {
                showAlert("Digite um valor maior que 0 para o campo: " + campo);
                return false;
            }
            return true;
        } catch (NumberFormatException e) {
            showAlert("Digite um valor válido para o campo: " + campo);
            return false;
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.CLOSE);
        alert.showAndWait();
    }

    @FXML
    private void evtAbrir(ActionEvent event) {
        if (projeto == null || control.getArq() == null) {
            showAlert("Nenhum arquivo carregado. Por favor, carregue um arquivo antes de prosseguir.");
            return;
        }

        if (validaInt(txOculta.getText(), "Camada Oculta") &&
                validaDouble(txErro.getText(), "Erro") &&
                validaInt(txIteracoes.getText(), "Número máximo de iterações") &&
                validaDouble(txAprendizado.getText(), "Taxa de Aprendizagem")) {

            int camadaOculta = Integer.parseInt(txOculta.getText());
            int numeroMax = Integer.parseInt(txIteracoes.getText());
            double valorErro = Double.parseDouble(txErro.getText());
            double taxaAprend = Double.parseDouble(txAprendizado.getText());
            int funcao = chLinear.isSelected() ? 1 : chLogistica.isSelected() ? 2 : 3;

            control.chamarAlgoritmo(camadaOculta, valorErro, numeroMax, taxaAprend, funcao, cbTeste.isSelected());

            if (cbTeste.isSelected()) {
                mostrarMatrizConfusao();
            } else {
                exibeErro();
            }
        }
    }

    private void exibeErro() {
        List<Double> erros = control.getListaErros();
        double tamanho = erros.remove(erros.size() - 1);
        int tamEpoca = (int) (erros.size() / tamanho);
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Épocas");
        yAxis.setLabel("Erro");

        LineChart<Number, Number> grafico = new LineChart<>(xAxis, yAxis);
        XYChart.Series<Number, Number> series = new XYChart.Series<>();

        for (int i = 0; i < tamanho - 1; i++) {
            double media = 0;
            for (int j = i * tamEpoca; j < (i + 1) * tamEpoca; j++) {
                media += erros.get(j);
            }
            media /= tamEpoca;
            series.getData().add(new XYChart.Data<>(i, media));
        }

        grafico.getData().add(series);

        ScrollPane root = new ScrollPane(grafico);
        root.setMinSize(1300, 800);
        grafico.setMinSize(root.getMinWidth(), root.getMinHeight() - 20);

        Stage stage = new Stage();
        stage.setScene(new Scene(root, 1215, 768));
        stage.show();
    }


    private void mostrarMatrizConfusao() {
        List<String> classes = control.getArq().getClasses();
        int[][] matrizConfusao = control.getMatrizConfusao();
        System.out.println("\nMatriz de Confusão:");
        System.out.print("       ");

        for (String classe : classes) {
            System.out.print(classe + "    ");
        }
        System.out.println();

        for (int i = 0; i < matrizConfusao.length; i++) {
            System.out.print(classes.get(i) + "     ");
            for (int j : matrizConfusao[i]) {
                System.out.printf("%-5d", j);
            }
            System.out.println();
        }
    }

    @FXML
    private void evtLinear(ActionEvent event) {
        chLogistica.setSelected(false);
        txHiperbolica.setSelected(false);
    }

    @FXML
    private void evtLogistica(ActionEvent event) {
        chLinear.setSelected(false);
        txHiperbolica.setSelected(false);
    }

    @FXML
    private void evtHiperbolica(ActionEvent event) {
        chLogistica.setSelected(false);
        chLinear.setSelected(false);
    }
}
