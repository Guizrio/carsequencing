/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package solvers;

import data_object.DataProblem;

/**
 *
 * @author Guillaume
 */
public class SolverBuilder{
        
        private static Solver solver;
        
        public SolverBuilder(String solverName, DataProblem dat){
            switch(solverName){
                case "Algorithm":
                    solver = new Algorithm(dat);
                    break;
                case "Swapper":
                    solver = new Swapper(dat);
                    break;
                case "BetterSwap":
                    solver = new BetterSwap(dat);
                    break;
                case "GeneticAlgo":
                    solver = new GeneticAlgo(dat, false);
                    break;
                default:
                    throw new IllegalArgumentException(solverName + " is not a implemented valid class for SolverBuilder constructor.");
            }
        }
        
        public static Solver getSolver(){
            return solver;
        }
        
    }