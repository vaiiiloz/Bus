package Models.GA.ObjectiveFunction;


import Entity.Matrix;
import Models.GA.Entity.Individual;
import Models.SA.Entity.Solution;

import java.util.ArrayList;

public class FitnessCalculator {

    private Matrix distance_matrix;
    private double[] weights;

    public FitnessCalculator(Matrix distance_matrix, double[] weights) {
        this.weights = weights;
        this.distance_matrix = distance_matrix;
    }

    public float calculateFitess(Object solution){
        if (solution instanceof Individual){
            float fitness = 0;
            Individual individual = ((Individual) solution);
            for (int i=0;i<individual.solution.size()-1;i++) fitness += distance_matrix.get(individual.solution.get(i), individual.solution.get(i+1));
            return fitness;
        }else{
            return -1;
        }

    }

    public float calculateFitnessSolution(ArrayList<Integer> solution){
        float fitness = 0;
        for (int i=0;i<solution.size()-1;i++) fitness += distance_matrix.get(solution.get(i), solution.get(i+1));
        return fitness;
    }
}
