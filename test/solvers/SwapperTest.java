/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package solvers;

import carsequencing.Solution;
import carsequencing.SolutionValidator;
import tools.Time;
import data_object.DataProblem;
import data_object.Car;
import solvers.Swapper;
import java.io.File;
import java.util.ArrayList;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Guillaume
 */
public class SwapperTest {
    
    Swapper algori;
    DataProblem dat;
    
    Swapper algoriLim;  
    DataProblem datLim;     //light version for testing all combinations of 2 consecutives swappings
    
    
    @Before
    public void setUp() {
        String folderPath = new File("").getAbsolutePath()  + File.separator 
                                                    + "Instances_set_A"
                                                    + File.separator
                                                    + "064_38_2_EP_RAF_ENP_ch2";
        
        dat = new DataProblem(folderPath);
        algori = new Swapper(dat);
        
        String folderPath2 = new File("").getAbsolutePath()  + File.separator 
                                                    + "Instances_set_A"
                                                    + File.separator
                                                    + "LimitedVersion";
        
        datLim = new DataProblem(folderPath2);
        algoriLim = new Swapper(datLim);
    }
    
    
    //================================Single Swap Test=====================================================

    /**
     * Test of swap method, of class Swapper.
     * <p>
     * Assert that identity permutation doesn't change a solution (except time to compute :
     * see the equal method of Solution class...)
     */
    @Test
    public void testSwapId() {
        System.out.println("SwapId");
        
        ArrayList<Car> allCars = new ArrayList<>(dat.getCars());
        Time timeStart = new Time();
        Solution expResult = algori.InitialyzeSolutionValue(allCars, timeStart);
        
        //Because objViolAtPosition isn't initialyzed in sol yet.
//        Solution expResult = new Solution(dat.getCars(),sol.getObjSol(),algori.getObjViolAtPosition(),sol.getPaintViol(), 0L);
        
        //assertEquals(expResult.getObjSol(), 284650);      //This add dependance with setUp method (bad thing)
        
        for (int i = dat.getNbCarsDayJMinus1(); i < dat.getNbCars(); i++) {
            Solution result = algori.swap(expResult, i, i);
            assertEquals(expResult, result);
            System.out.println("\tPermutation identité i = " + i + " passée avec succès !");
        }
    }
    
    
    
    
    
    
     /**
     * Test of swap method, of class Swapper.
     * <p>
     * Assert that swapping first and last element gives a correct solution.
     */
    @Test
    public void testSwapFirstLast() {
        System.out.println("SwapFirstLast");
        
        ArrayList<Car> allCars = new ArrayList<>(dat.getCars());
        Time timeStart = new Time();
        
        //Fist we initialyze base solution (verified by algoTest)
        Solution result = algori.InitialyzeSolutionValue(allCars, timeStart);
        
        //Swap
        result = algori.swap(result, dat.getNbCarsDayJMinus1(), dat.getNbCars()-1);
        
        //Test
        assertTrue(SolutionValidator.validate(result, dat));
    }
    
    
    /**
     * Test of swap method, of class Swapper.
     * <p>
     * Assert that swapping any element gives a correct solution.
     */
    @Test
    public void testSwapAny() {
        System.out.println("SwapAny");
        
        ArrayList<Car> allCars = new ArrayList<>(dat.getCars());
        Time timeStart = new Time();
        
        //Fist we initialyze base solution (verified by algoTest)
        Solution solBase = algori.InitialyzeSolutionValue(allCars, timeStart);
        
        
        for (int i = dat.getNbCarsDayJMinus1(); i < dat.getNbCars()-1; i++) {
            for (int j = i+1; j < dat.getNbCars(); j++) {
                Solution result = algori.swap(solBase, dat.getNbCarsDayJMinus1(), dat.getNbCars()-1);
                assertTrue(SolutionValidator.validate(result, dat));
            }
        }        
    }
    //=====================================================================================================
    
    
    //================================Combin Swap Test=====================================================
    /*Note : If at least one of the previous tests is false, each of Next tests are unprevisible
              i.e : even they are validated, they could be wrong.
    
    NoteBis : tests bellow could be very very long (as SwapAny() takes nearly 25 seconds to process
              on a 4ghz machine, testing every combinaison of 2 consecutive swapping...)
              
              This is why we will take an more restreint dataTest,
              the data test is the same than above, but limited to */
    
    
    
    
    /**
     * Test of swap method, of class Swapper.
     * <p>
     * Assert that any combination of 2 swappings gives a correct solution.
     */
    @Test
    public void testSwapCombin2Any() {
        System.out.println("SwapCombin2Any");
        
        ArrayList<Car> allCars = new ArrayList<>(datLim.getCars());
        Time timeStart = new Time();
        
        //Fist we initialyze base solution (verified by algoTest)
        Solution solBase = algoriLim.InitialyzeSolutionValue(allCars, timeStart);
        
        
        for (int i = datLim.getNbCarsDayJMinus1(); i < datLim.getNbCars()-1; i++) {
            for (int j = i+1; j < datLim.getNbCars(); j++) {
                for(int k = datLim.getNbCarsDayJMinus1(); k < datLim.getNbCars()-1; k++){
                    Solution result = algoriLim.swap(algoriLim.swap(solBase, i, j), j, k);
                    assertTrue(SolutionValidator.validate(result, datLim));
                    System.out.println("\ttest permutation (i <-> j <-> k) for i = " + i + ", j = " + j + ", k = " + k + " sucessfully passed !");
                }
            }
        }        
    }
    
    
    /**
     * Test serves uniquelly to isolates problem swapping objectives calculation
     */
    @Test
    public void testSwapCombin2() {
        System.out.println("SwapCombin2");
        
        ArrayList<Car> allCars = new ArrayList<>(dat.getCars());
        Time timeStart = new Time();
        
        //Fist we initialyze base solution (verified by algoTest)
        Solution solBase = algori.InitialyzeSolutionValue(allCars, timeStart);
        
        Solution resultSwap1 = algori.swap(solBase, 20, 51);
        Solution resultSwap2 = algori.swap(resultSwap1, 51, 101);
        
//        assertTrue(SolutionValidator.validate(resultSwap1, dat));    //Useless due to test SwapAny
        assertTrue(SolutionValidator.validate(resultSwap2, dat));
                      
    }
    
    
    
}
