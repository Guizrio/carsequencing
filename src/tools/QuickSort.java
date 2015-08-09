/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tools;

import carsequencing.Solution;
import java.util.ArrayList;

/**
 *
 * @author Guillaume
 */
public class QuickSort {
    
    public static void quicksort(ArrayList<Solution> solutions, int low, int high) {
        int i = low, j = high;
        Solution pivot = solutions.get(low + (high-low)/2);

        while (i <= j) {
            while (solutions.get(i).getObjSol() < pivot.getObjSol()) {
                i++;
            }

            while (solutions.get(j).getObjSol() > pivot.getObjSol()) {
                j--;
            }

            if (i <= j) {
                exchange(solutions, i, j);
                i++;
                j--;
            }
        }

        if (low < j) {
            quicksort(solutions, low, j);
        }

        if (i < high) {
            quicksort(solutions, i, high);
        }
    }

    private static void exchange(ArrayList<Solution> solutions, int i, int j) {
        Solution temp = solutions.get(i);
        solutions.set(i, solutions.get(j));
        solutions.set(j, temp);
    }
} 