
package com.example.redesneurais.util;

import com.example.redesneurais.entities.Atributo;
import com.example.redesneurais.entities.LinhaCSV;
import com.example.redesneurais.entities.Normalizacao;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Arquivo {

    private BufferedReader csvReader;
    private int outputLayer;
    private int inputLayer;
    private int hiddenLayer;
    private List<LinhaCSV> linhas;
    private List<Normalizacao> normalizacaos;
    private List<String> classes;
    private String nome_arquivo;
    private String path;

    public Arquivo(String path, String nome_arquivo) {

        this.path = path;
        this.nome_arquivo = nome_arquivo;
        this.outputLayer = this.inputLayer = this.hiddenLayer = 0;
        this.normalizacaos = new ArrayList<Normalizacao>();

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

    public List<LinhaCSV> getLinhas() {
        return linhas;
    }

    public List<String> getClasses() {
        return classes;
    }

    private void normalizar() {

        double valorAntigo, novoValor;
        List<Atributo> listAux;

        for (int i = 0; i < normalizacaos.size(); i++) {

            normalizacaos.get(i).setIntervalo();
        }

        for (int i = 0; i < linhas.size(); i++) {

            listAux = linhas.get(i).getAtributos();

            for (int j = 0; j < listAux.size(); j++) {

                // novo valor = (valor antigo - menor valor)/ intervalo
                valorAntigo = listAux.get(j).getValor();
                
                if(normalizacaos.get(j).getIntervalo() == 0)
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
        // Inicialização das variáveis e listas
        String row;
        String[][] data = new String[70500][800];
        String[] atributos = new String[790];
        String classe = "";
        LinhaCSV linha;
        double valor;
        int j = 0;
        int qtdClasses = 0;
        String classeAnt = "";
        boolean isTeste = false;
        int pos;
        this.classes = new ArrayList();
        this.linhas = new ArrayList<LinhaCSV>();
        boolean isminist = false;

        try {
            // Verifica se o arquivo é um dataset MNIST e inicializa os atributos
            if (nome_arquivo.contains("mnist")) {
                atributos = preencheAtributos();
                isminist = true;
                row = csvReader.readLine(); // Ignora a primeira linha (cabeçalho)
            } else {
                row = csvReader.readLine();
                atributos = row.split(",");
            }

            // Popula a lista de normalizações, caso não esteja em modo de teste ou MNIST
            if (!nome_arquivo.contains("teste") && !nome_arquivo.contains("test")) {
                normalizacaos = new ArrayList<>();
                for (int i = 0; i < atributos.length; i++) {
                    if (!atributos[i].isEmpty() && !atributos[i].equals("classe")) {
                        normalizacaos.add(new Normalizacao(atributos[i], 0.0, 0.0));
                    }
                }
            } else {
                isTeste = true;
            }

            inputLayer = normalizacaos.size();

            // Leitura das linhas do arquivo CSV
            while ((row = csvReader.readLine()) != null) {
                data[j] = row.split(",");
                classe = data[j][data[j].length - 1];
                linha = new LinhaCSV(classe);

                for (int i = 0; i < data[j].length; i++) {
                    if (i != data[j].length - 1) {
                        valor = Double.parseDouble(data[j][i]);
                        linha.setAtributo(atributos[i], valor);

                        // Atualiza os valores mínimo e máximo para normalização
                        if (!isTeste && !isminist) {
                            if (i < normalizacaos.size()) { // Verifica se o índice é válido
                                Normalizacao normalizacao = normalizacaos.get(i);
                                if (valor < normalizacao.getMenorValor()) {
                                    normalizacao.setMenorValor(valor);
                                } else if (valor > normalizacao.getMaiorValor()) {
                                    normalizacao.setMaiorValor(valor);
                                }
                            }
                        }
                    } else {
                        pos = classes.indexOf(data[j][i]);
                        if (pos == -1) {
                            qtdClasses++;
                            classes.add(data[j][i]);
                        }
                    }
                }
                linhas.add(linha);
                j++;
            }
            csvReader.close();

            outputLayer = qtdClasses;
            this.hiddenLayer = (inputLayer + outputLayer) / 2;

            // Chama a normalização se houver atributos para normalizar
            if (!normalizacaos.isEmpty()) {
                normalizar();
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


    public void setNome_arquivo(String nome_arquivo) {
        this.nome_arquivo = nome_arquivo;
    }

    public void setPath(String path) {
        this.path = path;
    }

}
