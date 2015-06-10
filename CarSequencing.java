/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package carsequencing;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

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
        
        
//        DataProblem dat = new DataProblem(folderPath);
//        Algorithm algo = new Algorithm(dat);
//        Solution sol = algo.solve();
//        System.out.println(sol);
//        
//        Algo algori = new Swapper(dat);
//        Solution sol2 = algori.solve();
//        System.out.println(sol2);
        
        RatioConstraint i = new RatioConstraint("1/10", 10, 1, true, "premiere contrainte");
        RatioConstraint j = new RatioConstraint("2/10", 10, 1, true, "deuxième contrainte");
        
        ArrayList<Car> cars = new ArrayList<>();
        
        Car car1 = new Car(new Date(), 1, 1, 2);
        car1.addRationConstraint(i);
        
        Car car2 = new Car(new Date(), 2, 2, 2);
        
        Car car3 = new Car(new Date(), 3, 3, 2);
        car3.addRationConstraint(i);
        car3.addRationConstraint(j);
        
        Car car4 = new Car(new Date(), 4, 4, 2);
        car4.addRationConstraint(j);
        
        cars.add(car1);
        cars.add(car2);
        cars.add(car3);
        cars.add(car4);
        
        ArrayList<Car> cars2 = new ArrayList<>(cars);
        
//        ArrayList<RatioConstraint> highPrioConstI = cars2.get(0).getHighRatioConstraint();
//        
//        System.out.println(highPrioConstI.size());
//        System.out.println(cars2.get(0).getHighRatioConstraint().size());
//        
//        System.out.println("");
//        Collections.swap(cars2, 0, 1);
//        
//        System.out.println(highPrioConstI.size());
//        System.out.println(cars2.get(0).getHighRatioConstraint().size());
        
        
        
        
//        ArrayList<Car> highPrioConstI = new ArrayList<>();
//        highPrioConstI.add(cars2.get(0));
//        
//        System.out.println(highPrioConstI.get(0).getHighRatioConstraint().size());
//        System.out.println(cars2.get(0).getHighRatioConstraint().size());
//        
//        System.out.println("");
//        //Collections.swap(cars2, 0, 1);
//        
//        car1 = car2; //Ne touche pas cars2 car c'est une deep copy...
//        
//        System.out.println(highPrioConstI.get(0).getHighRatioConstraint().size());
//        System.out.println(cars2.get(0).getHighRatioConstraint().size());
//        System.out.println(car1.getHighRatioConstraint().size());
        
        
        
//        ArrayList<Car> highPrioConstI = new ArrayList<>();
//        highPrioConstI.add(cars.get(0));
//        
//        System.out.println(highPrioConstI.get(0).getHighRatioConstraint().size());
//        System.out.println(cars.get(0).getHighRatioConstraint().size());
//        
//        System.out.println("");
////        Collections.swap(cars, 0, 1);
//        
//        car1 = car2; //Devrait toucher cars... MAIS NON !!!! (oulalala ma tête...)
//        
//        System.out.println(highPrioConstI.get(0).getHighRatioConstraint().size());
//        System.out.println(cars.get(0).getHighRatioConstraint().size());
//        System.out.println(car1.getHighRatioConstraint().size());
        
        
        
        
//        ArrayList<Integer> entiers = new ArrayList<>();
//        
//        Integer entier1 = new Integer(3);
//        Integer entier2 = new Integer(4);
//        
//        entiers.add(entier1);
//        entiers.add(entier2);
//        
//        entier1 = new Integer(2);
//        
//        entiers.add(entier1);
//        
//        entier1 = entier2;
//        
//        entiers.add(entier1);
//        
//        for (int k = 0; k < entiers.size(); k++) {
//            System.out.println("entier1 = " + entiers.get(k));
//        }
        
        
        ArrayList<int[]> entiers = new ArrayList<>();
        
        int[] k ={1};
        int[] l= {2};
        
        entiers.add(k);
        entiers.add(l);
        
        k=l;
        
        entiers.add(k);
        
        for (int m = 0; m < entiers.size(); m++) {
            System.out.println(entiers.get(m));
        }
        
        ArrayList<int[]> entiersBis = entiers;
        
        entiersBis.set(0, l);
        
        for (int m = 0; m < entiers.size(); m++) {
            System.out.println(entiers.get(m));
        }
        
        
        
        Car[] voitures = new Car[2];
        voitures[0] = car1;
        voitures[1] = car2;
        
        car1=car2;
        
        voitures[0].setId(10);
        
        for (int m = 0; m < voitures.length; m++) {
            System.out.println(voitures[m].getId());
        }
        
    }
    
}
