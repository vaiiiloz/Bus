package Models.GA.Entity;

import java.util.ArrayList;

public class Individual implements Comparable<Individual>{
    public ArrayList<Integer> solution;
    private float fitness;

    public float getFitness() {
        return fitness;
    }

    public void setFitness(float fitness) {
        this.fitness = fitness;
    }

    public Individual clone(){
        Individual newIndividual = new Individual();
        newIndividual.setFitness(fitness);
        newIndividual.solution = (ArrayList<Integer>) this.solution.clone();
        return newIndividual;
    }


    @Override
    public int compareTo(Individual o) {
        if (fitness == o.getFitness()){
            return 0;
        }else{
            if (fitness > o.fitness){
                return 1;
            }else{
                return -1;
            }
        }
    }

    @Override
    public String toString() {
        String a = "";
        for (int i=0;i<solution.size();i++){
            a+=solution.get(i)+" ";

        }
        a+="\n";
        return a;
    }

    public boolean check(){
        for (int i=0;i<solution.size()-1;i++){
            for (int j=i+1;j<solution.size();j++){
                if (solution.get(i) == solution.get(j)){
                    return false;
                }
            }
        }
        return true;
    }
}
