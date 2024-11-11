package com.example.redesneurais.entities;

public class Normalization {
    private String atributo;
    private double menorValor;
    private double maiorValor;
    private double intervalo;

    public Normalization(String atributo, double v, double v1) {
    }

    public String getAtributo() {
        return atributo;
    }

    public void setAtributo(String atributo) {
        this.atributo = atributo;
    }

    public double getMenorValor() {
        return menorValor;
    }

    public void setMenorValor(double menorValor) {
        this.menorValor = menorValor;
    }

    public double getMaiorValor() {
        return maiorValor;
    }

    public void setMaiorValor(double maiorValor) {
        this.maiorValor = maiorValor;
    }

    public double getIntervalo() {
        return intervalo;
    }

    public void setIntervalo() {
        this.intervalo = maiorValor - menorValor;
    }

    public void Normalizacao(String atributo, double menorValor, double maiorValor) {
        this.atributo = atributo;
        this.menorValor = menorValor;
        this.maiorValor = maiorValor;
        this.intervalo = maiorValor - menorValor;
    }
}
