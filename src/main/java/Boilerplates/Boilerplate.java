package Boilerplates;


import gate.Annotation;
import gate.FeatureMap;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * represents BPs
 * @author rittfe1
 */
public abstract class Boilerplate {
    FeatureMap map;
    public abstract String formatBP(Annotation an);
}
