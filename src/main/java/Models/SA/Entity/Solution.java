package Models.SA.Entity;

import java.util.ArrayList;
import java.util.List;

public class Solution {
    public float fitness;
    public ArrayList<Integer> solution;

    public Solution(){
        solution = new ArrayList<>();
    }

    public Solution clone(){
        Solution newSolution = new Solution();
        newSolution.fitness = this.fitness;
        newSolution.solution = (ArrayList<Integer>) solution.clone();
        return newSolution;
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
}
