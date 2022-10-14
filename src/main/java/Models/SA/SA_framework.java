package Models.SA;

import Entity.Matrix;
import Models.SA.Entity.Solution;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SA_framework {

    private List<Integer> nodes;
    private Matrix distance_matrix;
    private int N;
    private float T;
    private float alpha = 0.995f;
    private float stopping_temperature = 1e-8f;
    private int stopping_iter = 100000;
    private Solution best_solution;
    private int batch;
    private int fixThreadNum = 10;
    private int start;

    public SA_framework(List<Integer> nodes, int start, Matrix distance_matrix, float T, float alpha, float stopping_temperature, int stopping_iter, int batch){
        this.nodes = nodes;
        this.distance_matrix = distance_matrix;
        this.start = start;

        if (T!=-1) this.T = T;
        if (alpha!=-1) this.alpha = alpha;
        if (stopping_temperature!=-1) this.stopping_temperature = stopping_temperature;
        if (stopping_iter!=-1) this.stopping_iter = stopping_iter;
        this.batch = batch;
    }

    public void search() throws InterruptedException {
        List<SimulatedAnnealing> threads = new ArrayList<>();
        for (int i=0;i<batch;i++){
            SimulatedAnnealing thread = new SimulatedAnnealing(nodes, start, distance_matrix, T, alpha, stopping_temperature, stopping_iter);
            threads.add(thread);
        }
        ExecutorService executorService = Executors.newFixedThreadPool(batch);
        //start multithread ant
//        for (SimulatedAnnealing simulatedAnnealing:threads) executorService.execute(simulatedAnnealing);
//
//        executorService.shutdown();
//
//        while (!executorService.isTerminated()){
//
//        }

        for (SimulatedAnnealing sa:threads) sa.search();

        float best_fitness = 1f/0f;
        for (int i=0;i<threads.size();i++){
            if (threads.get(i).getBest_solution().fitness<best_fitness){
                best_fitness = threads.get(i).getBest_solution().fitness;
            }
        }
        System.out.println("Last best solution: "+ best_fitness);

    }


}
