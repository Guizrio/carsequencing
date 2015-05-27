/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package carsequencing;

import java.io.File;

/**
 *
 * @author Guillaume
 */
public class CarSequencing {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String folderPath = new File("").getAbsolutePath()  + File.separator 
                                                    + "Instances_set_A"
                                                    + File.separator   
                                                    +"Instances"
                                                    + File.separator
                                                    + "022_3_4_EP_RAF_ENP";
        
        
        DataProblem dat = new DataProblem(folderPath);
        Algorithm algo = new Algorithm(dat);
        Solution sol = algo.solve();
        System.out.println(sol);
        
        Algo algori = new Swapper(dat);
        Solution sol2 = algori.solve();
        System.out.println(sol2);
        
    }
    
}
