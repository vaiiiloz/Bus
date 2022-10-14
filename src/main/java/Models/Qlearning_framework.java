package main.java.Models;

import main.java.Data.Input_Data;
import main.java.Entity.Q_table;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
/*
EPOCHS = 4000
LEARNING_RATE = 0.2
GAMMA = 0.95
EPSILON = 0.1
 */

public class Qlearning_framework {
    private Input_Data inputData;
    private double[][] distance;
    private Q_table Qtable;
    private int epochs = 5000;
    private float epsilon = 0.9f;
    private float epsilon_decay = 0.999f;
    private float epsilon_min = 0.1f;
    private int exploit_time = 0;
    // explore rate, when agent dont know about environment
    // agent will go randomly action
    private float gamma = 0.95f;
    private float lr = 0.2f;
    public Qlearning_framework(String arg) throws IOException {
        inputData = new Input_Data(arg);
        distance = inputData.Distance();
        Qtable = new Q_table(inputData.getSize());
    }

    public void Q_learning() throws CloneNotSupportedException {
        File file = new File("output.csv");
        try {
            FileWriter outputfile = new FileWriter(file);
            int n = inputData.getSize();
            Q_table CompQtable = new Q_table(Qtable.getN(), Qtable.getValue());
            for (int ep = 0; ep < epochs; ep++) {
//                if(ep == epochs/2){
//                    epsilon = 0.1f;
//                }
                if(ep > exploit_time && epsilon >= epsilon_min){
                    epsilon = epsilon * epsilon_decay;
                }
                CompQtable.eps_greedy_update(distance, epsilon, gamma, lr); // upd
                double greedy_cost = Qtable.compute_value_of_q_table(distance); //calc
                double greedy_cost_comp = CompQtable.compute_value_of_q_table(distance);
                if (greedy_cost_comp < greedy_cost) {
                    Qtable = new Q_table(CompQtable.getN(), CompQtable.getValue());
                }
                if(ep % 1 == 0)
                    outputfile.write(ep + ","  + greedy_cost + "," + greedy_cost_comp + "\n");
            }
            outputfile.close();
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
//        System.out.println(epsilon);
        System.out.println(epsilon);
        System.out.println(Qtable.compute_value_of_q_table(distance));
    }
    public void display(){
//        int n = inputData.getSize();
//        for(int i = 0; i < n; i ++){
//            for(int j = 0; j < n; j ++){
//                System.out.print(inputData.getDistance(i, j) + " ");
//            }
//            System.out.println("");
//        }
    }
}
