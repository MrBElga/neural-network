package com.example.redesneurais.util;

import com.example.redesneurais.entities.Atribute;
import com.example.redesneurais.entities.CSVline;
import com.example.redesneurais.entities.Normalization;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Arq {

    private BufferedReader csvReader;
    private int outputLayer;
    private int inputLayer;
    private int hiddenLayer;
    private List<CSVline> linhas;
    private List<Normalization> normalizacaos;
    private List<String> classes;
    private String nome_arquivo;
    private String path;

    public void Arquivo(String path, String nome_arquivo) {

        this.path = path;
        this.nome_arquivo = nome_arquivo;
        this.outputLayer = this.inputLayer = this.hiddenLayer = 0;
        this.normalizacaos = new ArrayList<Normalization>();

        openArq();
    }

    public void openArq() {

        try {
            csvReader = new BufferedReader(new FileReader(path));

        } catch (IOException ex) {
            System.out.println(ex);
        }
    }

    public int getOutputLayer() {
        return outputLayer;
    }

    public void setOutputLayer(int outputLayer) {
        this.outputLayer = outputLayer;
    }

    public int getInputLayer() {
        return inputLayer;
    }

    public void setInputLayer(int inputLayer) {
        this.inputLayer = inputLayer;
    }

    public int getHiddenLayer() {
        return hiddenLayer;
    }

    public List<CSVline> getLinhas() {
        return linhas;
    }

    public List<String> getClasses() {
        return classes;
    }

    private void normalizar() {

        double valorAntigo, novoValor;
        List<Atribute> listAux;

        for (Normalization normalizacao : normalizacaos) {

            normalizacao.setIntervalo();
        }

        for (CSVline linha : linhas) {

            listAux = linha.getAtributos();

            for (int j = 0; j < listAux.size(); j++) {

                // novo valor = (valor antigo - menor valor)/ intervalo
                valorAntigo = listAux.get(j).getValor();

                if (normalizacaos.get(j).getIntervalo() == 0)
                    novoValor = 0;
                else
                    novoValor = ((valorAntigo - normalizacaos.get(j).getMenorValor()) / normalizacaos.get(j).getIntervalo());

                listAux.get(j).setValor(novoValor);
            }
        }
    }

    public String[] preencheAtributos() {

        String[] atributos = new String[784];

        for (int i = 0; i < 784; i++) {

            atributos[i] = "pixel" + (i + 1);
        }

        return atributos;
    }

    public void lerArquivo() {

        String row;
        String[][] data = new String[70500][800];
        String[] atributos = new String[790];
        String classe = "";
        CSVline linha;
        double valor;
        int j = 0;
        int qtdClasses = 0;
        String classeAnt = "";
        boolean isTeste = false;
        int pos;
        this.classes = new ArrayList();
        this.linhas = new ArrayList<CSVline>();
        boolean isminist = false;
        try {

            if (nome_arquivo.contains("mnist")) {

                atributos = preencheAtributos();
                isminist = true;

                row = csvReader.readLine();
            } else {

                row = csvReader.readLine();
                atributos = row.split(",");
            }

            if (!nome_arquivo.contains("teste") && !nome_arquivo.contains("test")) {

                normalizacaos = new ArrayList<>();

                for (int i = 0; i < atributos.length; i++) {

                    if (atributos[i] != "" && !atributos[i].equals("classe")) {

                        normalizacaos.add(new Normalization(atributos[i],0.0,0.0));
                    }
                }
            } else {
                isTeste = true;
            }

            inputLayer = normalizacaos.size();
            /*
            for (int i = 0; i < normalizacaos.size(); i++) {

                System.out.println(normalizacaos.get(i).getAtributo());
            }*/

            while ((row = csvReader.readLine()) != null) {

                data[j] = row.split(",");
                classe = data[j][data[j].length - 1];

                linha = new CSVline(classe);

                for (int i = 0; i < data[j].length; i++) {

                    if (i != data[j].length - 1) {

                        valor = Double.parseDouble(data[j][i]);
                        linha.setAtributo(atributos[i], valor);

                        if (!isTeste && !isminist) {

                            if (valor < normalizacaos.get(i).getMenorValor()) {
                                normalizacaos.get(i).setMenorValor(valor);
                            } else if (valor > normalizacaos.get(i).getMaiorValor()) {
                                normalizacaos.get(i).setMaiorValor(valor);
                            }
                        }

                    } else {

                        if (classeAnt.isEmpty()) {
                            qtdClasses++;
                            this.classes.add(data[j][i]);
                            classeAnt = data[j][i];
                        } else {

                            pos = classes.indexOf(data[j][i]);

                            if (pos == -1) {

                                qtdClasses++;
                                this.classes.add(data[j][i]);
                            }
                        }

                    }

                }

                linhas.add(linha);

                j++;
            }

            csvReader.close();

        } catch (IOException ignored) {
        }

        outputLayer = qtdClasses;
        this.hiddenLayer = (inputLayer + outputLayer) / 2;

        normalizar();

    }

    public void setNome_arquivo(String nome_arquivo) {
        this.nome_arquivo = nome_arquivo;
    }

    public void setPath(String path) {
        this.path = path;
    }

}
