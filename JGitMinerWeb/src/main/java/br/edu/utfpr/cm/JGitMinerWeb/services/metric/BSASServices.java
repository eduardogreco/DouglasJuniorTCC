/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.utfpr.cm.JGitMinerWeb.services.metric;

import br.edu.utfpr.cm.JGitMinerWeb.bsas.BSAS;
import br.edu.utfpr.cm.JGitMinerWeb.bsas.Cluster;
import br.edu.utfpr.cm.JGitMinerWeb.bsas.Element;
import br.edu.utfpr.cm.JGitMinerWeb.dao.GenericDao;
import br.edu.utfpr.cm.JGitMinerWeb.model.matrix.EntityMatrix;
import br.edu.utfpr.cm.JGitMinerWeb.model.matrix.EntityMatrixNode;
import br.edu.utfpr.cm.JGitMinerWeb.services.matrix.FiltersServices;
import br.edu.utfpr.cm.JGitMinerWeb.services.metric.auxiliary.AuxBSASMetrics;
import br.edu.utfpr.cm.JGitMinerWeb.util.OutLog;
import br.edu.utfpr.cm.JGitMinerWeb.util.Util;
import com.aliasi.matrix.DenseVector;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author eduardogreco
 */
public class BSASServices extends AbstractMetricServices {

    public BSASServices(GenericDao dao, OutLog out) {
        super(dao, out);
    }

    public BSASServices(GenericDao dao, EntityMatrix matrix, Map params, OutLog out) {
        super(dao, matrix, params, out);
    }

    @Override
    public void run() {
        System.out.println(params);

        if (getMatrix() == null && !getAvailableMatricesPermitted().contains(getMatrix().getClassServicesName())) {
            throw new IllegalArgumentException("Selecione uma matriz gerada pelo Service");
        }

        System.out.println("Selecionado matriz com " + getMatrix().getNodes().size() + " nodes.");

        List<Element> elements = createElements();

        double toleranceValue = 100000;//0.034;
        int qtdMaxCluster = 10000000;

        HashMap<Integer, Cluster> clusters = new BSAS().runBSAS(elements, toleranceValue, qtdMaxCluster);
        for (Integer k : clusters.keySet()) {
            clusters.get(k).calculateCohesion();
        }

        List<AuxBSASMetrics> fileBSAS = new ArrayList<>();

        for (Integer i : clusters.keySet()) {
            Cluster c = clusters.get(i);
            for (Element e : c.getVectors()) {
                fileBSAS.add(new AuxBSASMetrics(i, e.getDataset(), e.getDistance(), c.getCohesion()));
            }
        }

        addToEntityMetricNodeList(fileBSAS);

    }

    @Override
    public String getHeadCSV() {
        return "cluster;project;distance;coesion";
    }

    @Override
    public List<String> getAvailableMatricesPermitted() {
        return Arrays.asList(FiltersServices.class.getName());
    }

    private List<Element> createElements() {
        List<Element> elements = new ArrayList<>();
        for (EntityMatrixNode node : getMatrix().getNodes()) {
            String[] columns = node.getLine().split(";");
            List<Double> metrics = new ArrayList<>();
            for (int i = 1; i < columns.length; i++) {
                if (!columns[i].equals("not_calculated")) {
                    double calculed = Util.tratarStringParaDouble(columns[i]);
                    metrics.add(calculed);
                }
            }
            double[] values = new double[metrics.size()];
            for (int i = 0; i < values.length; i++) {
                values[i] = metrics.get(i);
            }
            elements.add(new Element(columns[0], new DenseVector(values)));
        }
        return elements;
    }

}
