/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package carsequencing;

import java.io.File;
import java.util.ArrayList;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;


/**
 * Tests of validate method, of class SolutionValidator.
 * <p>
 * Note that they are not true Unit tests : it depends on Dataproblem validity, and test
 * only the objective result and paint violation (not the number but the value which
 * is number multiply by objective coeff --> this could introduce malign errors..).
 * <p>
 * Particularly, we doesn't test the validity amount of each Contraint violation, but
 * the class doesn't allow it (for performances reasons), and maybe it has to...
 * 
 * @author Guillaume
 */
public class SolutionValidatorTest {
    
    DataProblem dat;
    String folderPath;
    
    @Before
    public void setUp() {
        folderPath = new File("").getAbsolutePath()  + File.separator 
                                                    + "Instances_set_A"
                                                    + File.separator
                                                    ;
    }
    

    
    @Test
    public void testValidate() {
        System.out.println("validate");
        dat = new DataProblem(folderPath + "064_38_2_EP_RAF_ENP_ch2");
        //Construct the basic solution (solution given by the original order of cars)
        // We know the result because of validator given by Renault for the file above.
        
//        Solution sol = new Solution(dat.getCars(),284650, new ArrayList<Long>(), 4600, 1L);    //Solution with old coefficients
        Solution sol = new Solution(dat.getCars(),28046050, new ArrayList<Long>(), 46000, 1L);   //Solution with new coefficients

        assertTrue(SolutionValidator.validate(sol, dat));
    }
    
    @Test
    public void testValidate2() {
        System.out.println("validate");
        dat = new DataProblem(folderPath + "022_3_4_EP_RAF_ENP");
        //Construct the basic solution (solution given by the original order of cars)
        // We know the result because of validator given by Renault for the file above.
        
        Solution sol = new Solution(dat.getCars(),2070002, new ArrayList<Long>(), 70000, 1L);   //Solution with new coefficients

        assertTrue(SolutionValidator.validate(sol, dat));
    }
    
    @Test
    public void testValidate3() {
        System.out.println("validate");
        dat = new DataProblem(folderPath + "022_3_4_RAF_EP_ENP");
        //Construct the basic solution (solution given by the original order of cars)
        // We know the result because of validator given by Renault for the file above.
        
        Solution sol = new Solution(dat.getCars(),11048005, new ArrayList<Long>(), 11000000, 1L);   //Solution with new coefficients

        assertTrue(SolutionValidator.validate(sol, dat));
    }
    
    @Test
    public void testValidate4() {
        System.out.println("validate");
        dat = new DataProblem(folderPath + "024_38_5_EP_ENP_RAF");
        //Construct the basic solution (solution given by the original order of cars)
        // We know the result because of validator given by Renault for the file above.
        
        Solution sol = new Solution(dat.getCars(),106134392, new ArrayList<Long>(), 392, 1L);   //Solution with new coefficients

        assertTrue(SolutionValidator.validate(sol, dat));
    }
    
    @Test
    public void testValidate5() {
        System.out.println("validate");
        dat = new DataProblem(folderPath + "039_38_4_EP_RAF_ch1");
        //Construct the basic solution (solution given by the original order of cars)
        // We know the result because of validator given by Renault for the file above.
        
        Solution sol = new Solution(dat.getCars(),115229000, new ArrayList<Long>(), 229000, 1L);   //Solution with new coefficients

        assertTrue(SolutionValidator.validate(sol, dat));
    }
    
    @Test
    public void testValidate6() {
        System.out.println("validate");
        dat = new DataProblem(folderPath + "039_38_4_RAF_EP_ch1");
        //Construct the basic solution (solution given by the original order of cars)
        // We know the result because of validator given by Renault for the file above.
        
        System.out.println("");
        
        Solution sol = new Solution(dat.getCars(),69392000, new ArrayList<Long>(), 69000000, 1L);   //Solution with new coefficients

        assertTrue(SolutionValidator.validate(sol, dat));
    }
}
