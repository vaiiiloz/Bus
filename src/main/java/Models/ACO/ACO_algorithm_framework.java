package Models.ACO;

import Models.ACO.Entity.Ant;
import Entity.Matrix;
import Models.ACO.Entity.Node;
import Models.ACO.utils.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ACO_algorithm_framework {

    private HashMap<Integer, Node> nodes;
    private int start;
    private Matrix distance_matrix;
    private Matrix pheromone_map;
    private Matrix ant_updated_pheromone_map;
    private float alpha;
    private float beta;
    private float pheromone_evaporation_coefficient;
    private float pheromone_constant;
    private int iterations;
    private boolean first_pass = true;
    private int ant_count;
    private ArrayList<Ant> ants;
    private float shortest_distance = -1;
    private ArrayList<Integer> shortest_path_seen;

    public ACO_algorithm_framework(HashMap<Integer, Node> nodes, Matrix distance_matrix, int start, int ant_count, float alpha, float beta, float pheromone_evaporation_coefficient, float pheromone_constant, int iterations){
        this.nodes = nodes;
        this.distance_matrix = distance_matrix;
        this.pheromone_map = utils._init_matrix(nodes.size());
        this.ant_updated_pheromone_map = utils._init_matrix(nodes.size());

        this.start = start;
        this.ant_count = ant_count;
        this.alpha = alpha;
        this.beta = beta;
        this.pheromone_evaporation_coefficient = pheromone_evaporation_coefficient;
        this.pheromone_constant = pheromone_constant;
        this.iterations = iterations;
        this.ants = _init_ants(start);
    }

    private ArrayList<Ant> _init_ants(int start) {
        //init colony in first phase
        if (first_pass){

            ArrayList<Ant> init_colony = new ArrayList<>();
            for (int i=0;i<ant_count;i++) init_colony.add(new Ant(start, new ArrayList<>(nodes.keySet()), pheromone_map, distance_matrix, alpha, beta, first_pass));
            return init_colony;
        }

        // else reset the ant
        for (Ant ant: ants) {
            ant.init(start, new ArrayList<>(nodes.keySet()), pheromone_map, distance_matrix, alpha, beta, first_pass);
        }
        return ants;
    }

    private void _update_pheromone_map(){
        //update pheromone map by decayings values contained
        //add pheromone values from all ant

        for (int start=0;start<pheromone_map.len();start++){
            for (int end = 0; end < pheromone_map.len(); end++){
                //decay
                pheromone_map.set(start, end, (1-pheromone_evaporation_coefficient)*pheromone_map.get(start, end));
                //add all ant contribution
                pheromone_map.add(start, end, ant_updated_pheromone_map.get(start, end));

            }
        }
    }

    private void _populate_ant_update_pheromone_map(Ant ant){
        ArrayList<Integer> route = ant.get_route();

        for (int i=0;i<route.size()-1;i++){
            float current_pheromone_value = (float) (ant_updated_pheromone_map.get(route.get(i), route.get(i+1)));


            float new_pheromone_value = pheromone_constant/ant.get_distance_traveled();
//            if (first_pass) System.out.println(new_pheromone_value);

            ant_updated_pheromone_map.set(route.get(i), route.get(i+1), current_pheromone_value + new_pheromone_value);
            ant_updated_pheromone_map.set(route.get(i+1), route.get(i), current_pheromone_value + new_pheromone_value);

        }
    }

    public ArrayList<Integer> search() throws InterruptedException {
        for (int i=0;i<iterations;i++){
            ExecutorService executorService = Executors.newFixedThreadPool(ant_count);
            //start multithread ant
            for (Ant ant:ants) executorService.execute(ant);

            executorService.shutdown();

            while (!executorService.isTerminated()){

            }
//            for (Ant ant:ants) ant.search();
            for (Ant ant:ants){
                _populate_ant_update_pheromone_map(ant);

                if (shortest_distance<0){
                    shortest_distance = ant.get_distance_traveled();
                    shortest_path_seen = ant.get_route();
                }

                if (ant.get_distance_traveled() < shortest_distance){
                    shortest_distance = ant.get_distance_traveled();
                    shortest_path_seen = ant.get_route();
                }
            }

            //decay and add pheromone
            _update_pheromone_map();

            if (first_pass) first_pass = false;

            _init_ants(start);

            ant_updated_pheromone_map = utils._init_matrix(nodes.size(), 0);
            System.out.println("Shortest " + shortest_distance);


        }
        return shortest_path_seen;
    }




}
