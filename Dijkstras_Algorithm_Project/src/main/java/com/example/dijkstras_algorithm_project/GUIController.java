package com.example.dijkstras_algorithm_project;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.FileChooser;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class GUIController {

    private static File file;//the file used for input
    private static Vertex[] graph;//graph of vertices
    private static double[][] relativeLocation;// for locations on the map

    private static int[] vertexAdjCount;// gives the count of adjacents for each vertex
    private static boolean[][] adjacentExists;// boolean to check if the adjacent has already been created
    private static boolean sourceWasChosen = false;//true if a source location has been chosen
    private static boolean destinationWasChosen = false;//true if a source location has been chosen
    private static GraphTable table = new GraphTable();// the graph table
    private static int numOfEdges = 0;// number of edges
    private static int numOfVertices = 0;// number of vertices
    @FXML
    private  Circle[] locations;// country locations on the map as circles
    private static LinkedList<Arrow> arrows = new LinkedList<>();// for visually representing the path from one vertex to another after computing the shortest path

    private double[] getCoordsOnMap(double latDeg,double lonDeg){// converts coords of vertices in the real world to the map displayed on the UI
        double mapWidth = mapIV.getFitWidth();
        double mapHeight = mapIV.getFitHeight();

        double latRad = Math.toRadians(latDeg);
        double lonRad = Math.toRadians(lonDeg+180);

        double radius = mapWidth/(2*Math.PI);

        double x = lonRad*radius;

        double verticalOffsetFromEquator = radius *Math.log(Math.tan(Math.PI/4 +latRad/2));
        double y = mapHeight/2 - verticalOffsetFromEquator;

        double[] coords = {x,y};
        return coords;
    }
    private void randomizeAdjacents(){//takes vertices and makes adjacents out of them and prints them on the same file
        try {
            vertexAdjCount = new int[graph.length];
            adjacentExists = new boolean[graph.length][graph.length];

            PrintWriter printWriter= new PrintWriter(new FileWriter(file,true));
            printWriter.println("Adjacents:");
            int count = 0;
            int edgeCount = 0;

                while(count<numOfEdges*3){
                    int i = (int) (Math.random() * (graph.length - 1));// start index
                    int j = (int) (Math.random() * (graph.length - 1));// destination index
                    if ((vertexAdjCount[i] <= numOfEdges / numOfVertices) && (i != j) && (!adjacentExists[i][j])) {
                        printWriter.println(graph[i].getName() + "," + graph[j].getName());
                        vertexAdjCount[i]++;
                        adjacentExists[i][j] = true;
                        edgeCount++;
                        }
                    count++;
                }
            System.out.println(edgeCount);
            printWriter.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    private void findShortestPath(GraphTable table,Vertex start) {// uses Dijkstra's algorithm to find the shortest path possible between two vertices with a priority queue
        PriorityQueue<Vertex> queue = new PriorityQueue<>();
        Vertex v, w;
        queue.add(graph[start.getIndex()]);
        while(!queue.isEmpty()){//--> V
            v = queue.poll();
            table.getKnown()[v.getIndex()] = true;
            for (int i =0;i<v.getAdjacent().size();i++){//for each adjacent vertex to v--> E
                w =v.getAdjacent().get(i).getDestination();// for ex--> (Antarctica,Australia) , (Antarctica,Palestine)
                if(!table.getKnown()[w.getIndex()]){// if the adjacent hasn't already been visited, continue

                    if(table.getDistance()[v.getIndex()] + v.getAdjacent().get(i).getWeight()<table.getDistance()[w.getIndex()]){
                        table.getDistance()[w.getIndex()] = table.getDistance()[v.getIndex()] + v.getAdjacent().get(i).getWeight();
                        table.getPath()[w.getIndex()] = v;
                        queue.add(w);
                    }
                }
            }
        }
    }



    @FXML
    public void calculate(ActionEvent e){//creates the graph table and displays all info needed (distance,path in text, path on map)
        if(graph==null){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("NULL GRAPH");
            alert.setContentText("You must fetch a file to use.");
            alert .show();
        }
        else {
            distTF.clear();
            pathTA.clear();
            sourceBox.cancelEdit();
            destBox.cancelEdit();
            anchorPane.getChildren().removeAll(arrows);
            arrows = new LinkedList<>();

            for (int i = 0; i < graph.length; i++) {
                if (sourceBox.getValue().equalsIgnoreCase(graph[i].getName())) {
                    sourceWasChosen = true;
                }
                if (destBox.getValue().equalsIgnoreCase(graph[i].getName())) {
                    destinationWasChosen = true;
                }

            }
            if (sourceWasChosen && destinationWasChosen) {//if the source country and destination countries are chosen, calculate.
                Vertex start = new Vertex();
                Vertex end = new Vertex();

                for (int i = 0; i < graph.length; i++) {//finding starting vertex from combo box value
                    if (graph[i].getName().equals(sourceBox.getValue())) {
                        start = graph[i];
                    }
                }
                for (int i = 0; i < graph.length; i++) {//finding ending vertex from combo box value
                    if (graph[i].getName().equals(destBox.getValue())) {
                        end = graph[i];
                    }
                }
                //starting vertex is set
                table = new GraphTable(start, graph);//table is set

                findShortestPath(table, start);//computes the shortest path and saves it to the table

                table.printTable();

                //the following code is for creating the path on the map
                LinkedList<Vertex> path = new LinkedList<>();
                Vertex currPath = table.getVertices()[end.getIndex()];//starts from the destination and goes back to the source

                Vertex nextPath = table.getPath()[end.getIndex()];
                Arrow arrow = new Arrow();
                arrow.setEndX(relativeLocation[currPath.getIndex()][0]);
                arrow.setEndY(relativeLocation[currPath.getIndex()][1]);
                if (nextPath != null) {
                    arrow.setStartX(relativeLocation[nextPath.getIndex()][0]);
                    arrow.setStartY(relativeLocation[nextPath.getIndex()][1]);
                    arrows.add(arrow);
                    path.add(currPath);
                    while (nextPath != null) {
                        arrow = new Arrow();
                        arrow.setEndX(relativeLocation[nextPath.getIndex()][0]);
                        arrow.setEndY(relativeLocation[nextPath.getIndex()][1]);
                        path.add(nextPath);
                        nextPath = table.getPath()[nextPath.getIndex()];
                        if (nextPath != null) {
                            arrow.setStartX(relativeLocation[nextPath.getIndex()][0]);
                            arrow.setStartY(relativeLocation[nextPath.getIndex()][1]);
                            arrows.add(arrow);
                        }
                    }
                    anchorPane.getChildren().addAll(arrows);
                    // map path created

                    distTF.setText((int) table.getDistance()[end.getIndex()] + " km");//prints distance in text field
                    for (int j = path.size() - 1; j >= 0; j--) {//prints names of countries on path in order
                        if (path.get(j).getName() != end.getName())
                            pathTA.appendText(path.get(j).getName() + "-->\n");
                        else
                            pathTA.appendText(path.get(j).getName());

                    }
                }
            }
        }

    }
    @FXML
    public void getFile(ActionEvent e) {// retrieves file, saves vertices, and generates random adjacents, also readies for calculation
        FileChooser fileChooser = new FileChooser();
        file = fileChooser.showOpenDialog(fileSelect.getScene().getWindow());

        String line;
        int lineNum = 0;
        boolean adjacentsExist = false;
        if(file !=null && file.getName().contains(".txt")) {
            try {
                int index = 0;
                BufferedReader firstInput = new BufferedReader(new FileReader(file));

                line = firstInput.readLine();//for number of vertices and adjacents
                lineNum++;
                numOfVertices = Integer.parseInt(line.substring(0, line.indexOf(",")));
                graph = new Vertex[numOfVertices];
                numOfEdges = Integer.parseInt(line.substring(line.indexOf(",") + 1));

                while ((line = firstInput.readLine()) != null) {//stores vertices (countries and coordinates) and adjacents
                    if (line.equals("Adjacents:")) {
                        adjacentsExist = true;
                    } else if (!adjacentsExist) {
                        String country = line.substring(0, line.indexOf(","));

                        line = line.substring(line.indexOf(',') + 1);

                        double latitude = Double.parseDouble(line.substring(0, line.indexOf(',')));

                        line = line.substring(line.indexOf(',') + 1);

                        double longitude = Double.parseDouble(line);

                        graph[index] = (new Vertex(country, latitude, longitude, index));
                        index++;
                        lineNum++;
                    }
                }
                firstInput.close();
                System.out.println(lineNum);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            if (!adjacentsExist)
                randomizeAdjacents();

            try {
                BufferedReader nextInput = new BufferedReader(new FileReader(file));

                for (int i = 0; i < lineNum + 1; i++)
                    nextInput.readLine();

                while ((line = nextInput.readLine()) != null) {
                    Vertex source = new Vertex();
                    Vertex destination = new Vertex();
                    String sourceString = line.substring(0, line.indexOf(","));
                    for (int i = 0; i < graph.length; i++)//find the source from the linked list
                        if (graph[i].getName().equals(sourceString)) {
                            source = graph[i];
                            break;
                        }

                    line = line.substring(line.indexOf(',') + 1);

                    String destinationString = line;
                    for (int i = 0; i < graph.length; i++)// finds the destination from the linked list
                        if (graph[i].getName().equals(destinationString)) {
                            destination = graph[i];
                            break;
                        }

                    for (int i = 0; i < graph.length; i++) {// adds adjacent to the source vertex chosen
                        if (source.equals(graph[i])) {
                            graph[i].getAdjacent().add(new Edge(source, destination));// adjacent is added to the vertex
                        }
                    }
                }
                nextInput.close();

                //finished taking vertices
                relativeLocation = new double[graph.length][2];
                locations = new Circle[graph.length];
                for (int i = 0; i < graph.length; i++) {//to fill the combo boxes and map with the vertices' names and locations

                    sourceBox.getItems().add(graph[i].getName());
                    destBox.getItems().add(graph[i].getName());

                    locations[i] = new Circle();
                    locations[i].setRadius(5);
                    locations[i].setFill(Paint.valueOf("#861212"));
                    locations[i].setStrokeWidth(1);

                    double[] coords = getCoordsOnMap(graph[i].getLatitude(), graph[i].getLongitude());
                    locations[i].setLayoutX(coords[0]);
                    locations[i].setLayoutY(coords[1]);
                    relativeLocation[i][0] = coords[0];
                    relativeLocation[i][1] = coords[1];
                    Label label = new Label();
                    anchorPane.getChildren().add(locations[i]);
                    int finalI = i;
                    AtomicBoolean clicked = new AtomicBoolean(false);
                    //events for labeling and highlighting circles
                    locations[i].addEventHandler(MouseEvent.MOUSE_CLICKED, c -> {// highlights circle on map and checks booleans
                        Circle picked = (Circle) c.getTarget();
                        if (clicked.get() == false) {
                            label.setLayoutX(picked.getLayoutX() - 5);
                            label.setLayoutY(picked.getLayoutY());
                            label.setText(String.valueOf(graph[finalI].getName()));
                            label.setOpacity(5);
                            label.setTextFill(Color.WHITE);
                            label.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii(0), new BorderWidths(1))));
                            label.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));
                            anchorPane.getChildren().add(label);
                            clicked.set(true);
                        } else {
                            clicked.set(false);
                            anchorPane.getChildren().remove(label);
                        }


                        if (!picked.getFill().equals(Color.LIGHTSEAGREEN)) {

                            picked.setFill(Color.LIGHTSEAGREEN);

                            for (int j = 0; j < graph.length; j++) {
                                if (relativeLocation[j][0] == picked.getLayoutX() && relativeLocation[j][1] == picked.getLayoutY()) {
                                    if (!sourceWasChosen) {
                                        sourceBox.setValue(graph[j].getName());
                                        sourceWasChosen = true;
                                    } else if (!destinationWasChosen) {
                                        destBox.setValue(graph[j].getName());
                                        destinationWasChosen = true;
                                    }
                                }
                            }
                        } else {
                            picked.setFill(Paint.valueOf("#861212"));

                            for (int j = 0; j < graph.length; j++) {
                                if (relativeLocation[j][0] == picked.getLayoutX() && relativeLocation[j][1] == picked.getLayoutY()) {
                                    if (sourceBox.getValue() == graph[j].getName()) {
                                        sourceBox.setValue("");
                                        sourceWasChosen = false;
                                    }
                                    if (destBox.getValue() == graph[j].getName()) {
                                        destBox.setValue("");
                                        destinationWasChosen = false;
                                    }
                                }
                            }
                        }
                    });

                }//done


            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        else{
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR: FILE INPUT");
            alert.setContentText("Please choose the right text file.");
            alert.show();
        }
    }

    @FXML
    private Button fileSelect;
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private ComboBox<String> destBox;
    @FXML
    private TextField distTF;
    @FXML
    private ImageView mapIV;
    @FXML
    private TextArea pathTA;
    @FXML
    private ComboBox<String> sourceBox;


}
