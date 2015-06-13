/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.utfpr.cm.JGitMinerWeb.bsas;

import com.aliasi.matrix.DenseVector;
import com.aliasi.matrix.EuclideanDistance;
import java.util.ArrayList;
import com.aliasi.matrix.Vector;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author samuel
 */
public final class Cluster {

    private ArrayList<Element> vectors; //DEVE SER PROJETO DO MESMO CLUSTER
    private double cohesion; // MEDIDA DE AVALIACAO DO CLUSTER
    private Vector centroid; // PONTO CENTRAL DE UM AGRUPADO (CLUSTER)

    private Map<Integer, Double> sumDistances; // Para não precisar percorrer todos os vetorem sempre que o centroide for recalculado
    private int qtdVectors;

    Cluster() {
        this.vectors = new ArrayList<Element>();
        setCohesion(Double.POSITIVE_INFINITY);
        this.centroid = null;
        this.sumDistances = new HashMap<>(); // Armazena a soma dos valores ROC de cada vetor
        this.qtdVectors = 0;
    }

    public void setCohesion(double cohesion) {
        this.cohesion = cohesion;
    }

    public void setCentroid(Vector centroid) {
        this.centroid = centroid;
    }

    public double getCohesion() {
        return this.cohesion;
    }

    public Vector getCentroid() {
        return this.centroid;
    }

//    public int getQtdVectors(){
//        return this.qtdVectors;
//    }
    public ArrayList<Element> getVectors() {
        return this.vectors;
    }

    public void recalculateCentroid() {
//        double c = 0; // Vai armazenar o somatório de todas as distâncias do cluster
//        
//        for(Element e: this.vectors){
//            c += e.distance;
//        }
//        
//        setCentroid(c / this.vectors.size());
        for (int i = 0; i < centroid.numDimensions(); i++) {
            double value = this.sumDistances.get(i) == null ? 0.0 : this.sumDistances.get(i);

            if (this.qtdVectors == 0) {
                centroid.setValue(i, 0);
            } else {
                centroid.setValue(i, value / this.qtdVectors);
            }
        }
        //this.sumDistances += this.vectors.get(this.vectors.size() - 1).getDistance();
        //setCentroid(this.sumDistances / this.qtdVectors);
    }

    public void calculateCohesion() {
        double sum = 0;

        for (Element e : this.getVectors()) {
            sum += e.getDistance() * e.getDistance();
        }

        this.setCohesion(sum);
    }

    public void recalculateDistanceVectors() {
        for (Element e : this.vectors) {
            double distance = new EuclideanDistance().distance(centroid, e.metrics);
            e.setDistance(distance);
        }
    }

    public void addVector(Element e) {
        this.vectors.add(e);
        this.qtdVectors++;
        for (int i = 0; i < e.metrics.numDimensions(); i++) {
            Double value = sumDistances.get(i) == null ? 0.0 : sumDistances.get(i);
            sumDistances.put(i, e.metrics.value(i) + value);
        }
    }
}
