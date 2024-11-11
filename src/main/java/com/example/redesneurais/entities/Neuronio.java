package com.example.redesneurais.entities;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class Neuronio {
      
    private double net;
    private double saida;
    private double erro;
    private List<Double> pesos;
    private final int qtdPesos;

    public Neuronio(int qtdPessos) {
        this.net = 0;
        this.saida = 0;
        this.erro = 0;
        this.pesos = new ArrayList<>();
        this.qtdPesos = qtdPessos;
        setPeso();
    }

    public double getNet() {
        return net;
    }

    public void setNet(double net) {        
      
        this.net = net;
    }

    public double getSaida() {
        return saida;
    }

    public void setSaida(double saida) {        
       
        this.saida = saida;
    }

    public double getErro() {
        
     
        return erro;
    }

    public void setErro(double erro) {        
      
        this.erro = erro;
    }
    
    public void resetaPesos(List<Double> novospesos){
          
        this.pesos = null;
        this.pesos = novospesos;    
    }
    
    public void setPeso(int pos, double valor){
                              
        this.pesos.add(pos, valor);
    }
    
    private void setPeso(){
        
        double rand, rand2;
        double max = 4;
        
        for (int i = 0; i < qtdPesos; i++) {
            
            rand = new Random().nextDouble()* max;
            rand2 =  Math.round(new Random().nextInt());
            
            if(rand2 <= 0.5)
                rand = -rand;            
                           
          
            this.pesos.add(i, rand);
        }
        
    }
    
    public double getPeso(int pos){
        
        return this.pesos.get(pos);
    }
}


