package Boilerplates;

import gate.Annotation;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author rittfe1
 */
public class Boilerplate65c extends Boilerplate {

    /**
     * fills BP65c with required information
     *
     * @param an
     * @return the filled boilerplate with the features required from an as a
     * string
     */
    String completeSystemText, functionDescriptionText;
    
    public Boilerplate65c() {
        typ = "65c";
    }
    
    @Override
    public Boilerplate formatBP(Annotation an) {
        map = an.getFeatures();
        completeSystemText = (String) map.get("completeSystemNameText");
        functionDescriptionText = (String) map.get("functionDescriptionText");
        text = "The complete system <<" + completeSystemText + ">> shall <<" + functionDescriptionText + ">>.";
        
        return this;
    }

    public String getCompleteSystemText() {
        return completeSystemText;
    }

    public String getFunctionDescriptionText() {
        return functionDescriptionText;
    }

    
}
