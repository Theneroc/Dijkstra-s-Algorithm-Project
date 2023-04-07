package com.example.dijkstras_algorithm_project;

import java.util.LinkedList;
public class Vertex implements Comparable<Vertex>{
    private String name;// maybe not really needed
    private LinkedList<Edge> adjacent;
    private double longitude;//x coordinates
    private double latitude;//y coordinates
    private int index;
    private double weight;

    public double getWeight() {
        return weight;
    }

    public Vertex() {}

    public Vertex(String name, double latitude, double longitude,int index) {
        this.name = name;
        this.longitude = longitude;
        this.latitude = latitude;
        weight = Integer.MAX_VALUE;
        adjacent = new LinkedList<>();
        this.index =index;
    }

    public int getIndex() {
        return index;
    }

    public double getLongitude() {
        return longitude;
    }


    public double getLatitude() {
        return latitude;
    }

    public String getName() {
        return name;
    }

    public LinkedList<Edge> getAdjacent() {
        return adjacent;
    }

    @Override
    public String toString() {
        return "Vertex{" +
                "name='" + name + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
    @Override
    public int compareTo(Vertex o) {
        return (int) (o.getWeight()-weight);
    }
}
