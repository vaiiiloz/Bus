package Models.PSO;

import Entity.Matrix;
import Models.ACO.Entity.Ant;
import Models.PSO.Entity.Particle;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PSO_framework {
    private Matrix distance_matrix;
    private ArrayList<Integer> nodes;
    private ArrayList<Particle> swarm;
    private int swarm_size;
    private float global_solution_index;
    private Particle global_solution;
    private int iteration;

    public PSO_framework(ArrayList<Integer> nodes, Matrix distance_matrix, int swarm_size, int iteration){
        this.swarm_size = swarm_size;
        this.nodes = nodes;
        this.distance_matrix = distance_matrix;
        this.iteration = iteration;
        swarm = new ArrayList<>();
    }

    public Particle fit() throws InterruptedException {
        Random rand = new Random();

        //initlize particle
        for (int i=0;i<swarm_size;i++){
            Particle newParticle = new Particle(distance_matrix);
            newParticle.init(0, nodes);
            swarm.add(newParticle);
        }

        global_solution = swarm.stream().min(Comparator.comparing(particle -> particle.fitness)).get().clone();

        for (Particle particle:swarm) particle.setSwarm_solution(global_solution);
        for (int i=0;i<iteration;i++){

            ExecutorService executorService = Executors.newFixedThreadPool(swarm_size);
            //start multithread ant
            for (Particle particle:swarm) executorService.execute(particle);

            executorService.shutdown();

            while (!executorService.isTerminated()){

            }


            global_solution = swarm.stream().min(Comparator.comparing(particle -> particle.fitness)).get().clone();

            for (Particle particle:swarm) particle.update(global_solution);
            System.out.println(global_solution.fitness + " " + global_solution.best_fitness);
        }
        return global_solution;

    }
}
