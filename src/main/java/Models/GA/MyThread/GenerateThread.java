package Models.GA.MyThread;



import Models.GA.Entity.Individual;
import Models.GA.ObjectiveFunction.FitnessCalculator;

import java.util.*;

public class GenerateThread implements Runnable{

    private List<Integer> nodes;
    private Individual parent1;
    private Individual parent2;
    private BlockingBuffer newPopulation;
    private ArrayList<Individual> children;
    private double mutationRate;
    private double crossoverRate;

    private FitnessCalculator fitnessCalculator;
    public GenerateThread(BlockingBuffer newPopulation, List<Integer> nodes, ArrayList<Individual> population, double crossoverRate, double mutationRate, FitnessCalculator fitnessCalculator) {
        this.nodes = nodes;
        this.newPopulation = newPopulation;
        this.fitnessCalculator = fitnessCalculator;
        this.crossoverRate = crossoverRate;
        this.mutationRate = mutationRate;


        Collections.shuffle(population);
        parent1 = population.get(0);
        parent2 = population.get(1);

        children = new ArrayList<>();
        children.add(parent1.clone());
        children.add(parent2.clone());
    }

    private void crossover(){

        Random rand = new Random();
        int n = parent1.solution.size();
        int l = rand.nextInt(n-1);
        int m = 2 + rand.nextInt(n-3);
        int r = l+m;
        r = r>n ? n:r;
        ArrayList<Integer> temp1 = lshifted(parent1.solution, r);
        ArrayList<Integer> temp2 = lshifted(parent2.solution, r);

        for (int i=l;i<r;i++){

            children.get(0).solution.set(i, parent1.solution.get(i));

            temp2.remove(Integer.valueOf(parent1.solution.get(i)));
            children.get(1).solution.set(i, parent2.solution.get(i));
            temp1.remove(Integer.valueOf(parent2.solution.get(i)));
        }

        for (int i=r;i<n;i++){

            children.get(0).solution.set(i, temp2.get(0));

            temp2.remove(0);
            children.get(1).solution.set(i, temp1.get(0));
            temp1.remove(0);
        }

        for (int i=0;i<l;i++){
            children.get(0).solution.set(i, temp2.get(0));
            temp2.remove(0);
            children.get(1).solution.set(i, temp1.get(0));

            temp1.remove(0);
        }

        children.get(0).setFitness(fitnessCalculator.calculateFitnessSolution(children.get(0).solution));
        children.get(1).setFitness(fitnessCalculator.calculateFitnessSolution(children.get(1).solution));



    }

    private ArrayList<Integer> lshifted(ArrayList<Integer> seq, int k){
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

    private void mutation(){

        Random rand = new Random();
        for (Individual child: children){
            for (int i=0;i<child.solution.size();i++){
                if (Math.random()<mutationRate){
                    int tmp = child.solution.get(i);
                    int new_index = rand.nextInt(child.solution.size());
                    if (new_index==i) new_index+=1;
                    if (new_index >= child.solution.size()) new_index = 0;
                    int new_node = child.solution.get(new_index);

                    checkImprove(new_index, new_node, i, tmp, child);
//                    child.solution.set(new_index, tmp);
//                    child.solution.set(i, new_node);
                }
            }
        }

    }

    private void checkImprove(int new_index, int new_value, int old_index, int old_value, Individual solution){
        ArrayList<Integer> tmp = (ArrayList<Integer>) solution.solution.clone();
        tmp.set(new_index, old_value);
        tmp.set(old_index, new_value);
        float candidateFitness = fitnessCalculator.calculateFitnessSolution(tmp);
        if ( candidateFitness< solution.getFitness()){
            solution.setFitness(candidateFitness);
            solution.solution = (ArrayList<Integer>) tmp.clone();
        }

    }

    @Override
    public void run() {
        //crossover
        if (Math.random() < crossoverRate){
            crossover();
        }

        //mutation
        mutation();


        children.forEach(i -> {
            try {
                newPopulation.push(i);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    public void test(){
//        System.out.println(children.get(0));
        //crossover
        if (Math.random() < crossoverRate){
            crossover();
        }

        //mutation
        mutation();
//        System.out.println(children.get(0));
//        System.out.println();

        children.forEach(i -> {
            try {
                newPopulation.push(i);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }
}
