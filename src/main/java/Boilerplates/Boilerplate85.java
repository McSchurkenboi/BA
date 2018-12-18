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
    @Override
    public String formatBP(Annotation an) {
        map = an.getFeatures();
        return "Under the condition: <<" + map.get("conditionText") + ">> the actor: <<" + map.get("conditionalActorText") + ">> shall <<" + map.get("functionDescriptionText") + ">>.";
    }

}
