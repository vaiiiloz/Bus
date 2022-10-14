package Models.PSO.Entity;

import Entity.Matrix;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Particle extends Thread implements Comparable<Particle>{
    public ArrayList<Integer> solution;
    public float fitness;
    public ArrayList<Integer> best_solution;
    public float best_fitness;
    public Matrix distance_matrix;
    private float p1 = 0.8f;
    private float p2 = 0.1f;
    private float p3 = 0.1f;
    private int max_no_improv = 3;

    public Particle getSwarm_solution() {
        return swarm_solution;
    }

    public void setSwarm_solution(Particle swarm_solution) {
        this.swarm_solution = swarm_solution;
    }

    private Particle swarm_solution;

    public Particle(){}

    public Particle(Matrix distance_matrix){
        solution = new ArrayList<>();
        fitness = 1f/0f;
        best_solution = new ArrayList<>();
        best_fitness = 1f/0f;
        this.distance_matrix = distance_matrix;
    }

    public void init(int start, ArrayList<Integer> nodes){
        ArrayList<Integer> init = new ArrayList<>();
        init.add(start);
        ArrayList<Integer> free_nodes = (ArrayList<Integer>) nodes.clone();
        free_nodes.remove(Integer.valueOf(start));
        Collections.shuffle(free_nodes);
        init.addAll(free_nodes);

        solution = init;
        fitness = cal_fitness(init);
        best_fitness = fitness;
        best_solution = (ArrayList<Integer>) init.clone();
    }

    @Override
    public void run() {
        // define particles movement
        float[] weight = {p1, p2, p3};
        int velocity = define_velocity(weight);

        switch (velocity){
            case 0:
                move_solution_independently();
                break;
            case 1:
                move_solution_to_personal_best();
                break;
            case 2:
                move_solution_to_swarm_best();
                break;
        }

        if (fitness<best_fitness){
            best_fitness = fitness;
            best_solution = (ArrayList<Integer>) solution.clone();
        }

    }

    private int define_velocity(float[] weight) {
        Random rand = new Random();
        float tmp = 0;

        float value = rand.nextFloat();
        for (int i=0;i<weight.length;i++){
            tmp += weight[i];
            if (value<tmp){
                return i;
            }
        }
        return weight.length;
    }

    private Particle path_relinking_search(List<Integer> target, float target_cost){
        Particle best_target = new Particle();
        best_target.solution = new ArrayList<Integer>(target);
        best_target.fitness = target_cost;

        int target_value = target.get(0);
        int target_index = solution.indexOf(Integer.valueOf(target_value));
        ArrayList<Integer> seq = lshift(solution, target_index);

        int n = best_target.solution.size();
        for (int i=1; i<n;i++){
            target_value = target.get(i);
            ArrayList<Integer> right_seq = new ArrayList<>();
            for (int j=i;j<n;j++){
                right_seq.add(seq.get(j));
            }
            target_index = right_seq.indexOf(Integer.valueOf(target_value));
            ArrayList<Integer> temp = lshift(right_seq, target_index);
            for (int j=i;j<n;j++){
                seq.set(j, temp.get(j-i));
            }
            float cost = cal_fitness(seq);
            if (cost<best_target.fitness){
                best_target.solution = (ArrayList<Integer>) seq.clone();
                best_target.fitness = cost;
            }

        }
        return best_target;

    }

    private ArrayList<Integer> lshift(List<Integer> seq, int k){
        int n = seq.size();
        k = k>n ? (k-k/n*n):k;

        ArrayList<Integer> shifted_seq = new ArrayList<>();
        for (int i=k;i<n;i++){
            shifted_seq.add(seq.get(i));
        }

        for (int i=0;i<k;i++){
            shifted_seq.add(seq.get(i));
        }
        return shifted_seq;
    }

    private void move_solution_to_swarm_best() {
        Particle newSolution = path_relinking_search(swarm_solution.solution, swarm_solution.fitness);
        solution = newSolution.solution;
        fitness = newSolution.fitness;
    }

    private void move_solution_to_personal_best() {
        Particle newSolution = path_relinking_search(best_solution, best_fitness);
        solution = newSolution.solution;
        fitness = newSolution.fitness;
    }

    private void move_solution_independently(){
        float delta_cost = neighborhood_inversion_search();
        fitness += delta_cost;
    }

    private float cal_fitness(List<Integer> seq){
        float fitness = 0;
        int n = seq.size();
        for (int i=1;i<n;i++){
            fitness+=distance_matrix.get(i-1,i);
        }
        return fitness;
    }

    private float neighborhood_inversion_search(){
        Random rand = new Random();

        float best_delta_cost = 0;
        int best_i = 0;
        int best_j = 0;

        int N = solution.size();
        int m = 2;
        int no_improv_count = 0;

        while (N-m>1){
            int i = 1 + rand.nextInt(N-2);
            int j = i + m - 1;
            j = j<N ? j:(j-N);

            int ia = solution.get(i-1);
            int ib = solution.get(i);
            int ja = solution.get(j);
            int jb = solution.get(j+1<N ? j+1:0);

            float cost0 = distance_matrix.get(ia, ib) + distance_matrix.get(ja, jb);
            float cost1 = distance_matrix.get(ia, ja) + distance_matrix.get(ib, jb);
            float delta_cost = cost1 - cost0;

            if (delta_cost < best_delta_cost){
                best_delta_cost = delta_cost;
                best_i = i;
                best_j = j;
                m+=1;
                no_improv_count = 0;
            }else{
                no_improv_count+=1;
                if (no_improv_count>=max_no_improv){
                    m+=1;
                    no_improv_count = 0;
                }
            }
        }
        solution = neighborhood_inversion(solution, best_i, best_j);
        return best_delta_cost;
    }
    
    private ArrayList<Integer> neighborhood_inversion(ArrayList<Integer> solution, int i, int j){
        ArrayList<Integer> newSolution = (ArrayList<Integer>) solution.clone();
        if (j>i){
            for (int index = 0;index<j-i+1;index++){
                newSolution.set(index+i, solution.get(j-index));
            }
        }else{
            int n = solution.size();
            List<Integer> neighborhood = new ArrayList<>();
            for (int index = i; index<n;index++){
                neighborhood.add(solution.get(index));
            }
            for (int index = 0; index<j+1;index++){
                neighborhood.add(solution.get(index));
            }

            Collections.reverse(neighborhood);
            for (int index = 0; index<n-i;index++){
                newSolution.set(i+index, neighborhood.get(index));
            }
            for (int index = 0; index<j+1;index++){
                newSolution.set(index, neighborhood.get(n-i+index));
            }
        }
        return newSolution;
    }

    @Override
    public int compareTo(Particle o) {
        return Float.compare(this.fitness, o.fitness);
    }

    public Particle clone(){
        Particle newParticle = new Particle();
        newParticle.fitness = this.fitness;
        newParticle.solution = new ArrayList<>(this.solution);
        newParticle.best_fitness = this.best_fitness;
        newParticle.best_solution = new ArrayList<>(this.best_solution);
        return newParticle;
    }

    public void update(Particle global_solution) {
        swarm_solution = global_solution.clone();
        p1 *=0.5;
        p2*=1.01;
        p3=1-(p1+p2);
    }

    @Override
    public String toString() {
        String a = "";
        for (int i=0;i<best_solution.size();i++){
            a+=best_solution.get(i)+" ";

        }
        a+="\n";
        return a;
    }
}
