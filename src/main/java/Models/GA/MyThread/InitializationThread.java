package Models.GA.MyThread;


import Models.GA.Entity.Individual;
import Models.GA.ObjectiveFunction.FitnessCalculator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InitializationThread implements Runnable{

    private BlockingBuffer population;
    private List<Integer> nodes;

    private FitnessCalculator fitnessCalculator;

    public InitializationThread() {

    }

    public InitializationThread(BlockingBuffer population, List<Integer> nodes, FitnessCalculator fitnessCalculator){
        this.population = population;
        this.nodes = nodes;
        this.fitnessCalculator = fitnessCalculator;
    }

    @Override
    public void run() {
        //create new Individual
        Individual newIndividual = generateIndividual();

        //add to buffer queues
        try {
            population.push(newIndividual);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private Individual generateIndividual(){
        Individual newIndividual = new Individual();

        ArrayList<Integer> free_node = new ArrayList<>(nodes);

        Collections.shuffle(free_node);

        newIndividual.solution = free_node;

        newIndividual.setFitness(fitnessCalculator.calculateFitnessSolution(newIndividual.solution));
//        System.out.println("Create new Individual");

        return newIndividual;
    }
}
