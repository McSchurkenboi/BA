/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DA;

import Boilerplates.Boilerplate;
import java.util.Comparator;

/**
 *
 * @author rittfe1
 */
public class BoilerplateComparator implements Comparator<Boilerplate>{

    /**
     * 
     * @param bp1 
     * @param bp2
     * @return 0, if different bp type. 1, if identical. 2, if type match
     */
    @Override
    public int compare(Boilerplate bp1, Boilerplate bp2) {
        if(bp1.getTyp().equals(bp2.getTyp())){
            if(bp1.equals(bp2)) {return 0;}//bps are equal
            else if(true/*comaparability condition*/){return 1;}//bps are comparable 
        } 
        return 2; //bps are entirely different
    }   
}
