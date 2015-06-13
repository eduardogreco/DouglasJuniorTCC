/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.utfpr.cm.JGitMinerWeb.bsas;

import java.util.ArrayList;
import com.aliasi.matrix.Vector;

/**
 *
 * @author samuel
 */
public class Element {

    public String dataset; //NOME DO PROJETO
    //public String algoritmo; //ALGORITMO DE CLASSIFICACAO
    //public String attributeEvaluator; //METODO DE AVALIACAO DA SELECAO DE ATRIBUTO
    //public String searchMethod; // METODO DE PESQUISA DOS ATRIBUTOS
    // public double roc; // MEDIDA DE AVALIACAO DA CLASSIFICACAO (passar lista de metricas)
    public Vector metrics;
    public double distance; // Distance do arquivo para o cluster que ele pertence

    //centroide todas metricas
    public ArrayList<String> features; // METRICAS (NOMES)
    public int amountFeatures; //QUANTIDADE

    public Element(String dataset, Vector metrics) {
        this.dataset = dataset;
//        this.algoritmo = algoritmo;
//        this.attributeEvaluator = attributeEvaluator;
//        this.searchMethod = searchMethod;
        this.metrics = metrics;
        this.features = new ArrayList<String>();
        this.amountFeatures = 0;
    }

    public String getDataset() {
        return this.dataset;
    }

//    public String getAlgoritmo() {
//        return this.algoritmo;
//    }
//
//    public String getAttributeEvaluator() {
//        return this.attributeEvaluator;
//    }
//
//    public String getSearchMethod() {
//        return this.searchMethod;
//    }

    public double getDistance() {
        return this.distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public void addFeature(String feature) {
        this.features.add(feature);
        this.amountFeatures++;
    }

    public int getAmountFeatures() {
        return this.amountFeatures;
    }

    public ArrayList<String> getFeatures() {
        return this.features;
    }

    public double getMetricsSum() {
        double sum = 0;
        for (int i = 0; i < metrics.numDimensions(); i++) {
            sum += metrics.value(i);
        }
        return sum;
    }
}
