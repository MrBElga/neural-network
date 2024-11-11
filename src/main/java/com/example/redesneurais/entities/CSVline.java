package com.example.redesneurais.entities;


import java.util.ArrayList;
import java.util.List;

public class CSVline {

    private String valorclasse;
    private List<Atribute> atributes;

    public CSVline(String valorclasse) {
        this.valorclasse = valorclasse;
        this.atributes = new ArrayList();
    }

    public String getValorclasse() {
        return valorclasse;
    }

    public void setValorclasse(String valorclasse) {
        this.valorclasse = valorclasse;
    }

    public List<Atribute> getAtributos() {
        return atributes;
    }

    public void setAtributo(String nome, double valor){

        this.atributes.add(new Atribute(nome, valor));
    }

    public List<Double> retornaValores(){

        List<Double> valores = new ArrayList();

        for (int i = 0; i < atributes.size(); i++) {

            valores.add(atributes.get(i).getValor());
        }

        return valores;
    }
}

