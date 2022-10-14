package main.java.Data;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Input_Data {
    private int n;
    private double[][] distance;
    public Input_Data(String arg) throws IOException {
        System.out.println("Read input");
        String url = "./src/main/java/Data/" + arg;
        FileInputStream fileInputStream = new FileInputStream(url);
        Scanner sc = new Scanner(fileInputStream);
        n = sc.nextInt();
        distance = new double[n][n];
        for(int i = 0; i < n; i ++){
            for(int j = 0; j < n; j ++){
                distance[i][j] = sc.nextDouble();
            }
        }
        sc.close();
        fileInputStream.close();
    }



    public int getSize(){
        return n;
    }
    public double[][] Distance() {
        return distance;
    }
}
