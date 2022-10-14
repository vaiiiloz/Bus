package Models.ACO.utils;

import Entity.Matrix;

public class utils {
    public static Matrix _init_matrix(int size) {
        return new Matrix(size);
    }

    public static Matrix _init_matrix(int size, int i) {
        return new Matrix(size, i);
    }
}
