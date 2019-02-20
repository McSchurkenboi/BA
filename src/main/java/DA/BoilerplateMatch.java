/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DA;

import Boilerplates.Boilerplate;

/**
 *
 * @author rittfe1
 */
public class BoilerplateMatch {
    String typ;
    int agrLevel;
    
    Boilerplate OEMBP;
    Boilerplate HELLABP;
    
    public BoilerplateMatch(Boilerplate bp1, Boilerplate bp2, int result){
        OEMBP = bp1;
        HELLABP= bp2;
        agrLevel = result;
        typ = bp1.getTyp();
    }

    public String getTyp() {
        return typ;
    }

    public int getAgrLevel() {
        return agrLevel;
    }

    public Boilerplate getOEMBP() {
        return OEMBP;
    }

    public Boilerplate getHELLABP() {
        return HELLABP;
    }
    
    
}
