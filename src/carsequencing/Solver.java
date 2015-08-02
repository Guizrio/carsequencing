/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package carsequencing;

import java.util.ArrayList;

/**
 *
 * @author Guillaume
 */
public interface Solver {
    
    public Solution solve();
    
    public ArrayList<String> getParams();
    public String getDescription();
    
}
