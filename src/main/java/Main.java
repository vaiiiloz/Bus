import Entity.Matrix;
import Models.ACO.ACO_algorithm_framework;
import Models.ACO.Entity.Node;
import Models.GA.GeneticAlgorithm.Framework;
import Models.PSO.PSO_framework;
import Models.SA.SA_framework;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Main {

    public static void main(String[] args){
        try {
            int start = 0;
            ArrayList<Integer> nodes = new ArrayList<>();
            for (int i = 0; i < 48; i++) {
                nodes.add(i);
            }

//            HashMap<Integer, Node> nodes = new HashMap<>();
//            for (int i=0;i<48;i++){
//                nodes.put(i, new Node(i));
//            }

            Matrix distance = readDistanceMatrix("Data_48_33523.txt");
            PSO_framework pso = new PSO_framework(nodes, distance, 1000, 1000);
            pso.fit();
//            ACO_algorithm_framework aco = new ACO_algorithm_framework(nodes, distance, 0, 100, 1.5f, 1.2f, 0.1f, 1.0f, 100);
//            aco.search();
//            SA_framework sa = new SA_framework(nodes, 0, distance, 10000, 0.995f, 1e-8f, 100000,1);
//            sa.search();
//            Framework ga = new Framework("E:\\Android Project\\Bus\\src\\main\\java\\Models\\GA\\Config\\config.properties", distance, nodes);
//            ga.startAll();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Matrix readDistanceMatrix(String arg) throws IOException {
        System.out.println("Read input");
        String url = "./src/main/java/Data/" + arg;
        FileInputStream fileInputStream = new FileInputStream(url);
        Scanner sc = new Scanner(fileInputStream);
        int n = sc.nextInt();
        Matrix distance = new Matrix(n);
        for(int i = 0; i < n; i ++){
            for(int j = 0; j < n; j ++){
                distance.set(i, j, sc.nextFloat());
            }
        }
        sc.close();
        fileInputStream.close();
        return distance;
    }
}
