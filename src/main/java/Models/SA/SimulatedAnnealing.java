package Models.SA;

import Entity.Matrix;
import Models.SA.Entity.Solution;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class SimulatedAnnealing extends Thread{

    private List<Integer> nodes;
    private Matrix distance_matrix;
    private int N;
    private float T;
    private float alpha = 0.995f;
    private int start;
    private float stopping_temperature = 1e-8f;
    private int stopping_iter = 100000;
    private Solution best_solution;
    private Solution current_solution;
    private List<Float> fitness_list;

    public SimulatedAnnealing(List<Integer> nodes, int start, Matrix distance_matrix, float T, float alpha, float stopping_temperature, int stopping_iter){
        this.nodes = nodes;
        this.distance_matrix = distance_matrix;
        this.start = start;
        this.N = nodes.size();
        best_solution = new Solution();
        best_solution.fitness = 1f/0f;
        fitness_list = new ArrayList<>();

        if (T!=-1) this.T = T;
        if (alpha!=-1) this.alpha = alpha;
        if (stopping_temperature!=-1) this.stopping_temperature = stopping_temperature;
        if (stopping_iter!=-1) this.stopping_iter = stopping_iter;
    }

    private Solution initial_solution(){
        Random rand = new Random();
        Solution solution = new Solution();
        int current_node = start;
        solution.solution.add(current_node);

        List<Integer> free_nodes = new ArrayList<>(nodes);
        free_nodes.remove(Integer.valueOf(current_node));

        // create solution
        while (free_nodes.size()>0){
            //find next node
//            int next_node = free_nodes.get(rand.nextInt(free_nodes.size())); //random

            int finalCurrent_node = current_node;
            int next_node = free_nodes.stream().min(Comparator.comparing(node -> distance_matrix.get(finalCurrent_node, node))).get();//nearest
            free_nodes.remove(Integer.valueOf(next_node));
            solution.solution.add(next_node);
            current_node = next_node;
        }

        float current_fit = cal_fitness(solution);
        solution.fitness = current_fit;
        if (current_fit < best_solution.fitness){
            best_solution.fitness = current_fit;
            best_solution.solution = solution.solution;
        }
        fitness_list.add(current_fit);
        return solution;
    }

    private float cal_fitness(Solution solution){
        float current_fit = 0;
        for (int i=0;i<solution.solution.size()-1;i++){
            current_fit += distance_matrix.get(solution.solution.get(i), solution.solution.get(i+1));
        }
        return current_fit;
    }

    private double p_accept(Solution candidate){
        return Math.exp(-Math.abs(candidate.fitness - current_solution.fitness)/T);
    }

    private void accept(Solution candidate){
        // accept with probability 1 if candidate is better than current
        // accept with probability p_accept(..) if candidate is worse
        Random rand = new Random();
        float candidate_fitness = cal_fitness(candidate);
        candidate.fitness = candidate_fitness;
        if (candidate_fitness < current_solution.fitness){
            current_solution = candidate;
            if (candidate_fitness < best_solution.fitness){
                best_solution = candidate;
            }
        }else{
            if (rand.nextFloat() < p_accept(candidate)) current_solution = candidate;
        }
    }

    @Override
    public void run() {
        search();
    }

    public void search(){
        // Simulated annealing algorithm

        Random rand = new Random();
        // Initialize with the greed solution
        current_solution = initial_solution();
        int iteration = 1;
        while (T >= stopping_temperature && iteration < stopping_iter){
            Solution candidate = current_solution.clone();
            int i = rand.nextInt(N-3);
            int l = 2 + rand.nextInt(N-i-3);
            //reversed candidate solution from i to i+l
            for (int index = 0; index<l/2;index++){
                int temp  = candidate.solution.get(i + index);
                candidate.solution.set(i + index, candidate.solution.get(i + l - index));
                candidate.solution.set(i + l -index, temp);
            }

            accept(candidate);
            T = T*alpha;
            iteration +=1;
            fitness_list.add(current_solution.fitness);
        }
        System.out.println("Best fitness obtained: "+best_solution.fitness);

    }

    public Solution getBest_solution() {
        return best_solution;
    }
}
