package main.java.Entity;

import java.util.ArrayList;

public class Q_table implements Cloneable{
    private double[][] value;
    private int n; // in this problem, n=m

    public Q_table(int n) {
        this.n = n;
        this.value = new double[n][n];
        for(int i = 0; i < n; i ++){
            for(int j = 0; j < n; j ++){
                this.value[i][j] = 0;
            }
        }
    }

    public Q_table(int n, double[][] V) {
        this.n = n;
        this.value = new double[n][n];
        for(int i = 0; i < n; i ++){
            for(int j = 0; j < n; j ++) {
                this.value[i][j] = V[i][j];
            }
        }
    }

    public int argmax_city(int current, ArrayList<Integer> mask){
        int next_node = 0;
        double arg_max = -Double.MAX_VALUE;
        for (int u : mask) {
            if (value[current][u] > arg_max) {
                arg_max = value[current][u];
                next_node = u;
            }
        }
        return next_node;
    }

    public double argmax_value(int current, ArrayList<Integer> mask){
        double arg_max = -Double.MAX_VALUE;
        for(int i = 0; i < mask.size(); i ++){
            int u = mask.get(i);
            if(value[current][u] > arg_max){
                arg_max = value[current][u];
            }
        }
        return arg_max;
    }

    public void eps_greedy_update(
            double[][] distance,
            double epsilon,
            double gamma,
            double lr
            ){
        ArrayList<Integer> route = new ArrayList<>();
        route.add(0);
        ArrayList<Integer> mask = new ArrayList<>();
        for(int i = 1; i < n; i ++)
            mask.add(i);
        for(int i = 1; i < n; i ++){
            // for each i : chose ith city to visit
            int current = route.get(route.size() - 1);
            int next_visit;
            double argmax;
            double reward;
            if(mask.size() == 1){
                next_visit = mask.get(0);
                reward = -distance[current][next_visit];
                // Reward for finishing the route
                argmax = -distance[next_visit][route.get(0)];
            }
            else {
                double u = Math.random();
                if (u < epsilon) {
                    next_visit = mask.get((int) (Math.random() * mask.size()));
                } else {
                    next_visit = argmax_city(current, mask);
                }
                // update mask and route
                mask.remove(Integer.valueOf(next_visit));
                route.add(next_visit);
                reward = -distance[current][next_visit];
                argmax = argmax_value(next_visit, mask); ///
            }
            // updating Q
            double upd_val = get_val(current, next_visit) + lr * (reward + gamma * argmax
            - get_val(current, next_visit));
            this.set_val(current, next_visit, upd_val);
        }
    }

    public int[] greedy_road(){
        int[] route = new int[n + 1];
        ArrayList<Integer> mask = new ArrayList<>();
        int current = 0;
        for(int i = 1; i < n; i ++)
            mask.add(i);
        route[0] = current;
        for(int i = 1; i < n; i ++){
            int next_city = argmax_city(current, mask);
            mask.remove(Integer.valueOf(next_city));
            current = next_city;
            route[i] = current;
        }
        route[n] = 0;
        return route;
    }

    public double calc_route_val(int[] route, double[][] distance){
        int pre = route[0];
        double total = 0;
        for(int i = 1; i <= n; i ++){
            int cur = route[i];
            total += distance[pre][cur];
            pre = cur;
        }
        return total;
    }

    public double compute_value_of_q_table(double[][] distance){
        int[] route = greedy_road();
        return calc_route_val(route, distance);
    }

//    public calc_val(ArrayList <Integer> route){
//        int cur = route[0];
//        for(int i = 1; i < route.size(); i ++){
//
//        }
//    }
    public double get_val(int i, int j) {
        return value[i][j];
    }

    public void set_val(int i, int j, double val) {
        this.value[i][j] = val;
    }

    public double[][] getValue() {
        return value;
    }

    public int getN() {
        return n;
    }
    public Q_table clone() throws CloneNotSupportedException {
        Q_table student = (Q_table) super.clone();
        return student;        // return deep copy
    }
}
