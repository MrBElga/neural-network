
package com.example.redesneurais.util;

import com.example.redesneurais.entities.CalcularRedeNeural;
import com.example.redesneurais.entities.LinhaCSV;
import java.util.ArrayList;
import java.util.List;


public class Controladora {

    private Arquivo arq;
    private CalcularRedeNeural redeNeural;
    private List<Double> listaErros;
    private int matrizConfusao[][];
    private List<LinhaCSV> testeMNIST;
      
    public Controladora() {
     
    }

    public CalcularRedeNeural getRedeNeural() {
        return redeNeural;
    }

    public void resetaDados(){
     
        testeMNIST = null;
    }
    
    public void AbrirArquivo(String path, String nome_arquivo) {
        
        if(arq == null)
             arq = new Arquivo(path, nome_arquivo);
        else{
            
            arq.setNome_arquivo(nome_arquivo);
            arq.setPath(path);
            
         
        }
        
        arq.openArq();

        arq.lerArquivo();       

    }

    public Arquivo getArq() {
        return this.arq;
    }

   
    public void chamarAlgoritmo(int camadaoculta, double errominimo, int maximainteract, double taxaaprend, int funcaoTrans,
            boolean isTest) {

            if (isTest) {
                redeNeural.testar(arq.getLinhas());
                matrizConfusao = redeNeural.getMatrizConfusão();
            } else {
                redeNeural = new CalcularRedeNeural(camadaoculta, errominimo, maximainteract, taxaaprend, funcaoTrans,
                        arq.getOutputLayer(), arq.getInputLayer(), arq.getClasses());
                listaErros = redeNeural.treinar(arq.getLinhas());
            }
    }

    public List<Double> getListaErros() 
    {
        return listaErros;
    }

    public int[][] getMatrizConfusao() {
        return matrizConfusao;
    }
}
