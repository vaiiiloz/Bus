package Models.ACO.Entity;

import Entity.Matrix;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Ant extends Thread{
    private Integer init_location;
    private ArrayList<Integer> possible_location;
    private ArrayList<Integer> route;
    private float distance_traveled = 0;
    private Integer location;
    private Matrix pheromoneMap;
    private Matrix distanceMatrix;
    private float alpha;
    private float beta;
    private boolean first_pass;
    private boolean tour_complete = false;

    public Ant(Integer init_location, ArrayList<Integer> possible_location, Matrix pheromoneMap, Matrix distanceMatrix, float alpha, float beta, boolean first_pass){
        init(init_location, possible_location, pheromoneMap, distanceMatrix, alpha, beta, first_pass);
    }

    public void init(Integer init_location, ArrayList<Integer> possible_location, Matrix pheromoneMap, Matrix distanceMatrix, float alpha, float beta, boolean first_pass){
        this.init_location = init_location;
        this.possible_location = possible_location;
        this.route = new ArrayList<>();
        this.location = init_location;
        this.pheromoneMap = pheromoneMap;
        this.distanceMatrix = distanceMatrix;
        this.alpha = alpha;
        this.beta = beta;
        this.first_pass = first_pass;
        distance_traveled = 0;

        // append start location to route
        this._update_route(init_location);
        this.tour_complete = false;
    }

    private int _pick_path(){
        Random random = new Random();
        // total random on the first pass
        if (first_pass){

            return possible_location.get(random.nextInt(possible_location.size()));
        }

        if (possible_location.size()==1){
            return possible_location.get(0);
        }

        HashMap<Integer, Float> attractiveness = new HashMap<>();
        float sum_total = 0;
        // fot each possible location , find its attactiveness
        for (Integer possible_next_location : possible_location){

            float pheromone_amount = pheromoneMap.get(location, possible_next_location);
            float distance = distanceMatrix.get(location, possible_next_location);

            // formular
            attractiveness.put(possible_next_location, Float.valueOf((float) (Math.pow(pheromone_amount, alpha)*Math.pow(1/distance, beta))));
            sum_total += attractiveness.get(possible_next_location);
//            System.out.println(sum_total);
        }

//        System.out.println("Last "+ sum_total);
//        System.out.println();
        if (sum_total == 0){
            return possible_location.get(random.nextInt(possible_location.size()));
        }

        //cumulative probability behavior + random chose next path
        float toss = random.nextFloat();


        float cummulative = 0;
        for (Map.Entry<Integer, Float> entry: attractiveness.entrySet()){
            float weight = (entry.getValue()/sum_total);
            if (toss <= weight+cummulative) return entry.getKey();
            cummulative += weight;
        }
        return -1;
    }

//    private float next_up(float x){
//        if ((Float.isNaN(x)) || (Float.isInfinite(x) && x>0) ){
//            return x;
//        }
//    }

    private void _traverse(int start, int end){
        _update_route(end);
        _update_distance_traveled(start, end);
        this.location = end;
    }

    private void _update_distance_traveled(int start, int end) {
        this.distance_traveled += (float) (distanceMatrix.get(start, end));
    }

    private void _update_route(int newNode) {
        this.route.add(newNode);
        this.possible_location.remove(Integer.valueOf(newNode));
    }

    public ArrayList<Integer> get_route(){
        if (this.tour_complete) return route;
        return null;
    }

    public float get_distance_traveled(){
        if (tour_complete) return distance_traveled;
        return -1;
    }

    @Override
    public void run() {
        // until possible locations is empty
        // pick path to find a next node to traverse
        // traverse to update route and distance

        search();
    }

    public void search(){
        while (possible_location.size() > 0){
            int next = _pick_path();
            _traverse(location, next);
        }

        tour_complete = true;
    }

    @Override
    public String toString() {
        String a = "";
        for (int i=0;i<route.size();i++){
            a+=route.get(i)+" ";

        }
        a+="\n";
        return a;
    }
}
