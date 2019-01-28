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
public class Boilerplate85 extends Boilerplate {

    /**
     * fills BP85 with required information
     *
     * @param an
     * @return the filled boilerplate with the features required from an as a
     * string
     */
    String conditionText, conditionalActorText, functionDescriptionText;
    
    @Override
    public String formatBP(Annotation an) {
        map = an.getFeatures();
        conditionText = (String) map.get("conditionText");
        conditionalActorText = (String) map.get("conditionalActorText");
        functionDescriptionText = (String) map.get("functionDescriptionText");
        bp = "Under the condition: <<" + conditionText + ">> the actor: <<" + conditionalActorText + ">> shall <<" + functionDescriptionText + ">>.";
        return bp;
    }

}
