/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.utfpr.cm.JGitMinerWeb.bsas;

import br.edu.utfpr.cm.JGitMinerWeb.services.matrix.auxiliary.AuxRepositoryManyMetricas;
import com.aliasi.matrix.EuclideanDistance;
import com.aliasi.matrix.Vector;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 *
 * @author samuel
 */
public class BSAS {

    /**
     *
     * @param elements - Todos os vetores que vão ser classificados em clusters.
     * @param value - Uma distância de tolerância para a criação dos clusters.
     * @param maximumCluster - Quantidade maxima de clusters que pode ser
     * criado.
     * @return - Retorna um Hash contendo os clusters que foram formados.
     */
    public HashMap<Integer, Cluster> runBSAS(List<Element> elements, double value, int maximumCluster) {
        HashMap<Integer, Cluster> hm = new HashMap<Integer, Cluster>();
        ArrayList<Element> list;

        int qtdCluster = 1;
        int contList = 0;
        int key = 1;

        double distance;
        double maxRoc;

        if (elements.isEmpty()) {
            return null;
        }

        // Inserindo primeiro elemento no cluster   
        Element firstElement = this.getFirstVector(elements);
        firstElement.distance = 0; // A verificar
        hm.put(key, new Cluster());
        hm.get(key).addVector(firstElement);
        hm.get(key).setCentroid(firstElement.metrics);

        key++;

        while (contList < elements.size()) {
            Element aux = elements.get(contList);

            if (aux != firstElement) {
                Vector metrics = aux.metrics;

                int k = searchMinCluster(hm, metrics);// k é a chave do cluster de menor distância //

                list = hm.get(k).getVectors();// Obtendo cluster //

                distance = getMinDistance(list, metrics);

                if (distance > value && qtdCluster < maximumCluster) { //NOVO GRUPO
                    aux.distance = 0; // A verificar
                    hm.put(key, new Cluster());
                    hm.get(key).addVector(aux);
                    hm.get(key).setCentroid(aux.metrics);

                    key++;
                    qtdCluster++;
                } else {
                    hm.get(k).addVector(aux);
                    hm.get(k).recalculateCentroid();
                    hm.get(k).recalculateDistanceVectors();
                }
            }

            contList++;
        }

        return hm;
    }

    // Retorna a chave do cluster do qual o elemento mais se aproxima ///
    //PARA QUAL GRUPO ELE VAI
    public int searchMinCluster(HashMap<Integer, Cluster> hash, Vector metrics) {
        Set<Integer> listKey = hash.keySet();
        ArrayList<Element> listElement;
        int key = 0;
        double maxRoc;
        double minDistance = Float.POSITIVE_INFINITY;
        double distance;

        for (Integer k : listKey) {
            listElement = hash.get(k).getVectors();
            distance = getMinDistance(listElement, metrics);

            if (distance < minDistance) {
                key = k;
                minDistance = distance;
            }
        }

        return key;
    }

    // Retorna a menor distancia entre o arquivo que está sendo tratado e um cluster qualquer //
    public double getMinDistance(ArrayList<Element> list, Vector metrics) {
        double minDistance = Float.POSITIVE_INFINITY;
        double distance;

        EuclideanDistance ed = new EuclideanDistance();
        for (Element e : list) {
            distance = ed.distance(metrics, e.metrics);
            if (distance < minDistance) {
                minDistance = distance;
            }
        }

        return minDistance;
    }

    public Element getFirstVector(List<Element> vectors) {
        double maxRoc = Double.NEGATIVE_INFINITY;
        Element element = null;

        for (Element e : vectors) {
            double metricSum = e.getMetricsSum();
            if (metricSum > maxRoc) {
                maxRoc = metricSum;
                element = e;
            }
        }

        return element;
    }
}
