/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package carsequencing;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
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
public class AlgoTest {
    
    private class AlgoImpl extends Algo {

        public AlgoImpl(DataProblem dat) {
            super(dat);
        }

        @Override
        public Solution solve() {
            return null;
        }

        @Override
        public ArrayList<String> getParams() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public String getDescription() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }
    
    AlgoImpl algori;
    DataProblem dat;
    
    @Before
    public void setUp() {
        String folderPath = new File("").getAbsolutePath()  + File.separator 
                                                    + "Instances_set_A"
                                                    + File.separator
                                                    + "064_38_2_EP_RAF_ENP_ch2";
        
        dat = new DataProblem(folderPath);
        algori = new AlgoImpl(dat);
    }

    /**
     * Test of InitialyzeSolutionValue method, of class Algo.
     * <p>
     * It test if the initial solution (the solution given in the order of file)
     * is a correct solution (except for objViolAtPosition).
     */
    @Test
    public void testInitialyzeBaseSolutionValue() {
        System.out.println("InitialyzeBaseSolutionValue");
        
        Solution result = algori.InitialyzeSolutionValue(dat.getCars(), new Time());
        
        assertTrue(SolutionValidator.validate(result, dat));
    }
    
    
    /**
     * Test of InitialyzeSolutionValue method, of class Algo.
     * <p>
     * It test if any permutation sheduling has a correct objective value and paint violation by 
     * calling  InitialyzeSolutionValue() method.
     */
    @Test
    public void testInitialyzeSolutionValue() {
        System.out.println("InitialyzeBaseSolutionValue");
        
        for (int i = dat.getNbCarsDayJMinus1(); i < dat.getNbCars()-1; i++) {
            for (int j = i+1; j < dat.getNbCars(); j++) {
                ArrayList<Car> swapCars = new ArrayList<>(dat.getCars());
                Collections.swap(swapCars, i , j);

                Solution result = algori.InitialyzeSolutionValue(swapCars, new Time());
                assertTrue(SolutionValidator.validate(result, dat));
                
                System.out.println("\tInitialyzed swapped data for i = " + i + ", j = " + j + " sucessfully passed !");
                
            }
        }        
    } 
}
