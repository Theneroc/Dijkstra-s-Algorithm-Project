package com.example.dijkstras_algorithm_project;
public class Edge implements Comparable<Edge>{
    private Vertex source;
    private Vertex destination;
    private double weight;

    public Edge(){}

    public Edge(Vertex source, Vertex destination) {
        this.source = source;
        this.destination = destination;
        if(source!=destination)
            setWeight();
        else
            weight=0;
    }
    public Edge(Vertex source,Vertex destination,double weight){
        this.source = source;
        this.destination = destination;
        this.weight = weight;
    }
    public Edge(Vertex destination,double weight){
        this.destination = destination;
        this.weight =weight;
    }

    public Vertex getSource() {
        return source;
    }

    public void setSource(Vertex source) {
        this.source = source;
    }

    public Vertex getDestination() {
        return destination;
    }

    public void setDestination(Vertex destination) {
        this.destination = destination;
    }


    public double getWeight() {
        return weight;
    }



    public void setWeight() {// gets the distance between vertices using the pythagorean theorem
        double sourceLatRad = Math.toRadians(source.getLatitude());
        double sourceLonRad = Math.toRadians(source.getLongitude());
        double destLatRad = Math.toRadians(destination.getLatitude());
        double destLonRad = Math.toRadians(destination.getLongitude());

        double y = destLonRad - sourceLonRad;
        //formula
        weight = Math.acos(Math.sin(sourceLatRad)*Math.sin(destLatRad)+Math.cos(sourceLatRad)*Math.cos(destLatRad)*Math.cos(y))*6371;//6371 = radius of earth in km
    }

    @Override
    public String toString() {
        return "Edge{" +
                "source=" + source +
                ", destination=" + destination +
                ", weight=" + weight +
                '}';
    }

    @Override
    public int compareTo(Edge o) {
        return (int) (o.getWeight()-weight);
    }
}
