package com.example.dijkstras_algorithm_project;

import java.util.Arrays;
import java.util.LinkedList;

public class GraphTable {
    private Vertex[] vertices;
    private boolean[] known;
    private double[] distance;
    private Vertex[] path;// name of the vertex
    private Vertex start;
    private int size;
    public GraphTable(){}

    public int size() {
        return size;
    }


    public void printTable() {
        for (int i=0;i<size;i++){
            System.out.println(vertices[i].getName() + " "+known[i]+" "+path[i]+" "+distance[i]);
        }
    }

    public GraphTable(Vertex start, Vertex[] graph) {
        size = graph.length;
        vertices = new Vertex[size];
        known = new boolean[size];
        distance = new double[size];
        path = new Vertex[size];

       for(int i = 0;i<size;i++) {
           vertices[i]= graph[i];
           known[i] = false;
           distance[i] = Integer.MAX_VALUE;// can also make it -1 same space consumption...
           if(graph[i]==start) {// makes the starting vertex distance 0
               distance[i] = 0;
           }
           path[i] = null;// not needed only for the sake of doc
       }

    }

    public Vertex getStart() {
        return start;
    }

    public void setStart(Vertex start) {
        this.start = start;
    }

    public Vertex[] getVertices() {
        return vertices;
    }

    public void setVertices(LinkedList<Vertex> vertices) {
        for(int i =0;i<vertices.size();i++)
            this.vertices[i] = vertices.get(i);
    }

    public boolean[] getKnown() {
        return known;
    }

    public void setKnown(boolean[] known) {
        this.known = known;
    }

    public double[] getDistance() {
        return distance;
    }

    public void setDistance(double[] distance) {
        this.distance = distance;
    }

    public Vertex[] getPath() {
        return path;
    }

    public void Vertex(Vertex[] path) {
        this.path = path;
    }



}
