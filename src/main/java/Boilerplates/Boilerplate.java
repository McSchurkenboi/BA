package Boilerplates;

import gate.Annotation;
import gate.FeatureMap;
import java.io.Serializable;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * represents BPs
 *
 * @author rittfe1
 */
public abstract class Boilerplate implements Serializable {

    private static final long serialVersionUID = 1234;
    FeatureMap map;
    String bp;

    public abstract String formatBP(Annotation an);

    public String getBp() {
        return bp;
    }

}
