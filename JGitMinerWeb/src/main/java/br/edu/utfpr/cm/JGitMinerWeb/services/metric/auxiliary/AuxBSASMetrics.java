/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.utfpr.cm.JGitMinerWeb.services.metric.auxiliary;

/**
 *
 * @author eduardogreco
 */
public class AuxBSASMetrics {

    private int cluster;
    private String project;
    private double distance;
    private double cohesion;

    public AuxBSASMetrics(int cluster, String project, double distance, double cohesion) {
        this.cluster = cluster;
        this.project = project;
        this.distance = distance;
        this.cohesion = cohesion;
    }

    public int getCluster() {
        return cluster;
    }

    public void setCluster(int cluster) {
        this.cluster = cluster;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getCohesion() {
        return cohesion;
    }

    public void setCohesion(double cohesion) {
        this.cohesion = cohesion;
    }

    @Override
    public String toString() {
        return cluster + ";" + project + ";" + distance + ";" + cohesion;
    }

}
