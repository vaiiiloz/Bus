package Entity;

import java.util.Arrays;

public class Matrix {
    private float[][] matrix;


    public Matrix(int size){
        matrix = new float[size+1][size+1];
    }

    public Matrix(int size, float value){
        matrix = new float[size+1][size+1];
        for (int i=0;i<size;i++){
            Arrays.fill(matrix[i], value);
        }
    }

    public int len() {
        return matrix.length;
    }



    public float get(int start, int end) {
        return matrix[start][end];
    }

    public void set(int start, int end, float v) {
        matrix[start][end] = v;
    }

    public void add(int start, int end, float v) {
        matrix[start][end] += v;
    }
}
