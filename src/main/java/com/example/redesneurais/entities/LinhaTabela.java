package com.example.redesneurais.entities;
import java.util.List;


public class LinhaTabela {
    
    private List<Float> valores;
    private String classe;

    public LinhaTabela(List<Float> valores, String classe) {
        this.valores = valores;
        this.classe = classe;
    }

    public List<Float> getValores() {
        return valores;
    }

    public void setValores(List<Float> valores) {
        this.valores = valores;
    }

    public String getClasse() {
        return classe;
    }

    public void setClasse(String classe) {
        this.classe = classe;
    }
    
    
}
