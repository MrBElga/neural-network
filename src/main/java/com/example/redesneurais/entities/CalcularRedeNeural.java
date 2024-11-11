package com.example.redesneurais.entities;

import static java.lang.Double.NaN;
import java.util.ArrayList;
import java.util.List;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

public class CalcularRedeNeural {

    private int camadaOculta;
    private double erroMinimo;
    private int maximaInteract;
    private double taxaAprend;
    private int funcaoTrans;
    private int camadaSaida;
    private int camadaEntrada;
    private List<Neuronio> camadaOcultaMLP;
    private List<Neuronio> camadaSaidaMLP;
    private List<Double> errosdaRede;
    private int saidaEsperada[];
    private List<String> classes;
    private int matrizConfusão[][];
    private List<Double> listAcuracia;
    private double acuraciaTotal;
    @FXML
    private AnchorPane painel;
    
    public CalcularRedeNeural(int camadaOculta, double erroMinimo, int maximaInteract, double taxaAprend, int funcaoTrans, int camadaSaida, int camadaEntrada,
            List<String> classes) {
        this.camadaOculta = camadaOculta;
        this.erroMinimo = erroMinimo;
        this.maximaInteract = maximaInteract;
        this.taxaAprend = taxaAprend;
        this.funcaoTrans = funcaoTrans;
        this.camadaSaida = camadaSaida;
        this.camadaEntrada = camadaEntrada;
        this.saidaEsperada = new int[camadaSaida];
        this.classes = classes;

    }

    private void setSaidaEsperada(String classeAtual) {

        int posClasse = classes.indexOf(classeAtual);
        this.saidaEsperada = new int[camadaSaida];

        this.saidaEsperada[posClasse] = 1;

        /**/
        if(funcaoTrans == 3){
            
            for (int i = 0; i < classes.size(); i++) {
                
                if(posClasse != i){
                    
                     this.saidaEsperada[i] = -1;
                }
            }
        }
    }

    private void calculaNeuronios() {

        this.camadaOcultaMLP = new ArrayList();
        this.camadaSaidaMLP = new ArrayList();

        // criando os neuronios da camada oculta
        for (int i = 0; i < camadaOculta; i++) {

            this.camadaOcultaMLP.add(new Neuronio(camadaEntrada));
        }

        // criando os neuronios da camada de saida
        for (int i = 0; i < camadaSaida; i++) {

            this.camadaSaidaMLP.add(new Neuronio(camadaOculta));
        }

    }

    private double linear(double net) {

        return  (net / 10.0);
    }

    private double linearDerivada() {

        return  0.1;
    }

    private double logistica(double net) {
                   
        return (1.0 / (1.0 + Math.exp(-net)));
    }

    private double logisticaDerivada(double saida) {

        return  (saida * (1.0 - saida));
    }

    private double Hiperbolica(double net) {
   
        return Math.tanh(net);
    }

    private double HiperbolicaDerivada(double saida) {

        return (1.0 - Math.pow(saida, 2.0));
    }

    private double calcularErroRede() {

        double soma = 0;
        double pow;

        for (int i = 0; i < camadaSaida; i++) {

            pow = camadaSaidaMLP.get(i).getErro() * camadaSaidaMLP.get(i).getErro();

            soma += pow;
        }

        soma += 0.5 * soma; 

        return soma;
    }

    private double retornaSaida(double net) {

        double valor = 0;

        if (funcaoTrans == 1) {

            valor = linear(net);
        } else if (funcaoTrans == 2) {

            valor = logistica(net);
        } else {

            valor = Hiperbolica(net);
        }

        return valor;
    }

    private double retornaGradiente(double saida) {

        double valor = 0;

        if (funcaoTrans == 1) {

            valor = linearDerivada();
        } else if (funcaoTrans == 2) {

            valor = logisticaDerivada(saida);
        } else {

            valor = HiperbolicaDerivada(saida);
        }

        return valor;
    }

    private void calcularCamadaOculta(List<Atributo> linha) {

        double netNeuronio = 0;
        double valorLinha;
        double saida;

        for (int i = 0; i < camadaOculta; i++) {

            netNeuronio = 0;
            saida = 0;

            for (int j = 0; j < camadaEntrada; j++) {

                valorLinha = linha.get(j).getValor();
                netNeuronio += valorLinha * this.camadaOcultaMLP.get(i).getPeso(j);
            }

            saida = retornaSaida(netNeuronio);

            this.camadaOcultaMLP.get(i).setNet(netNeuronio);
            this.camadaOcultaMLP.get(i).setSaida(saida);

        }

    }

    private void calcularCamadaSaida() {

        double netNeuronio = 0;
        double valorLinha;
        double saida;
        double erro;
        double retorno;

        for (int i = 0; i < camadaSaida; i++) {

            netNeuronio = 0;
            for (int j = 0; j < camadaOculta; j++) {

                valorLinha = this.camadaOcultaMLP.get(j).getSaida();
                netNeuronio += valorLinha * this.camadaSaidaMLP.get(i).getPeso(j);
            }

            saida = retornaSaida(netNeuronio);
            retorno =  retornaGradiente(saida);
            erro = (saidaEsperada[i] - saida) * retorno;

            if(erro == NaN)
                System.out.println("tesste");
            
            this.camadaSaidaMLP.get(i).setNet(netNeuronio);
            this.camadaSaidaMLP.get(i).setSaida(saida);
            this.camadaSaidaMLP.get(i).setErro(erro);
        }

    }

    private void calculaErroCamadaOculta() {

        double retorno;
        double erro = 0, erroGrad;
        double valorLinha;

        for (int i = 0; i < camadaOculta; i++) {

            erro = 0;
            
            for (int j = 0; j < camadaSaida; j++) {

                valorLinha = this.camadaSaidaMLP.get(j).getErro();
                erro += valorLinha * this.camadaSaidaMLP.get(j).getPeso(i);
            }

            retorno = retornaGradiente(camadaOcultaMLP.get(i).getSaida());
            erroGrad = erro * retorno;

             if(erroGrad == NaN)
                System.out.println("tesste");
            this.camadaOcultaMLP.get(i).setErro(erroGrad);
        }
    }

    public void calcularNovosPesosCamadaSaida() {

        double saidaOculta;
        double valorErro, novoPeso, valorPeso;
        List<Double> novosPesos;

        for (int i = 0; i < camadaSaida; i++) {

            valorErro = this.camadaSaidaMLP.get(i).getErro();
            novosPesos = new ArrayList();

            for (int j = 0; j < camadaOculta; j++) {

                saidaOculta = this.camadaOcultaMLP.get(j).getSaida();
                valorPeso = this.camadaSaidaMLP.get(i).getPeso(j);

                novoPeso = (valorPeso + taxaAprend * valorErro * saidaOculta);

                novosPesos.add(j, novoPeso);
            }

            this.camadaSaidaMLP.get(i).resetaPesos(novosPesos);
        }
    }

    public void calcularNovosPesosCamadaOculta(List<Atributo> atributos) {

        double entrada;
        double valorErro, novoPeso, valorPeso;
        List<Double> novosPesos;

        for (int i = 0; i < camadaOculta; i++) {

            valorErro = this.camadaOcultaMLP.get(i).getErro();
            novosPesos = new ArrayList();

            for (int j = 0; j < camadaEntrada; j++) {

                entrada = atributos.get(j).getValor();
                valorPeso = this.camadaOcultaMLP.get(i).getPeso(j);

                novoPeso = (valorPeso + taxaAprend * valorErro * entrada);

                novosPesos.add(j, novoPeso);
            }

            this.camadaOcultaMLP.get(i).resetaPesos(novosPesos);
        }
    }

    public Double mediaRede(List<Double> errosdaRedetest){
     
        double acomula = 0;
        
        for (int i = 0; i < errosdaRedetest.size(); i++) {
            
            acomula += errosdaRedetest.get(i);
        }
        
        acomula = acomula / errosdaRedetest.size();
        return acomula;
    }
    
    public List<Double> treinar(List<LinhaCSV> linhasCSV) {

        LinhaCSV linha;
        double soma;
        boolean erroMin = false;
        int i;
        this.errosdaRede = new ArrayList();
        calculaNeuronios();
        List<Double> errosdaRedetest = new ArrayList();
        double erroMedia = 0;

        for (i = 0; i < maximaInteract && !erroMin; i++) {

            for (int j = 0; j < linhasCSV.size() && !erroMin; j++) {

                linha = linhasCSV.get(j);
                setSaidaEsperada(linha.getValorclasse()); // seta matriz de resultado esperado

                calcularCamadaOculta(linha.getAtributos()); // calcula o net e a saida dos neuronios da camada oculta
                calcularCamadaSaida(); // calcular net, saida e erro da camada de saida
                soma = calcularErroRede(); // erro da rede

                this.errosdaRede.add(soma);
                errosdaRedetest.add(soma);
                calculaErroCamadaOculta(); // calcula o erro da camada oculta
                calcularNovosPesosCamadaSaida();// atualizar pesos da camada de saida
                calcularNovosPesosCamadaOculta(linha.getAtributos());// atualizar pessos da  camada oculta

            }
            
            System.out.println("EPOCA: "+i);
            erroMedia = mediaRede(errosdaRedetest);
              System.out.println("ERRO: "+erroMedia);
            if(erroMedia < erroMinimo){
             
                erroMin = true;
                
            }
            
            errosdaRedetest = new ArrayList();            
          
        }
       
        errosdaRede.add((double) i);
        return errosdaRede;
    }

    public int retornaPosResultado(){
        
        double maiorvalor = 0;
        int posMaior = 0;
        
        for (int i = 0; i < camadaSaidaMLP.size(); i++) {
            
            if(camadaSaidaMLP.get(i).getSaida() > maiorvalor){
                
                maiorvalor = camadaSaidaMLP.get(i).getSaida();
                posMaior = i;
            }
        }
        
        return posMaior;
    }
    
    public void calcularAcuracia(){
     
        listAcuracia = new ArrayList();
        double predicoesTotais = 0, acuraciaAux = 0;
        double aux;
        
        for (int i = 0; i < camadaSaida; i++) {
            
            predicoesTotais = 0;
            for (int j = 0; j < camadaSaida; j++) {
                
                predicoesTotais += matrizConfusão[i][j];
            }
            
            aux = matrizConfusão[i][i] / predicoesTotais;
            
            listAcuracia.add(aux * 100);
            acuraciaAux += aux * 100;
        }
        
        this.acuraciaTotal = acuraciaAux / classes.size();
    }
    
    public void testar(List<LinhaCSV> linhasCSV) {

        matrizConfusão = new int[camadaSaida][camadaSaida];
        int pos, posClasse;
        LinhaCSV linha;
        Label novo = new Label();
        
        for (int i = 0; i < linhasCSV.size(); i++) {

            linha = linhasCSV.get(i);
            
            calcularCamadaOculta(linha.getAtributos()); 
            calcularCamadaSaida();
            pos = retornaPosResultado();
            posClasse = classes.indexOf(linha.getValorclasse());
            
            matrizConfusão[posClasse][pos]++;
            
        }
        
        calcularAcuracia();
        
        for (int i = 0; i < camadaSaida; i++) {
            
            System.out.println("Classe: "+classes.get(i) + " Acuracia: "+ listAcuracia.get(i));
            System.out.println("Classe: "+classes.get(i) + " Erro: "+ (100 - listAcuracia.get(i)));
        }
        
        System.out.println("Acuracia total: "+acuraciaTotal);
        System.out.println("Erro total: "+(100 - acuraciaTotal));

    }

    public List<Double> getErrosdaRede() {
        return errosdaRede;
    }

    public int[][] getMatrizConfusão() {
        return matrizConfusão;
    }
    
    public List<Double> getListAcuracia() {
        return listAcuracia;
    }

    public double getAcuraciaTotal() {
        return acuraciaTotal;
    }

}

