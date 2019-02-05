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

    private static final long serialVersionUID = 123;
    FeatureMap map;
    String text= "missing text";

    
    String typ= "missing type";
    int reqID = -1;

    public int getReqID() {
        return reqID;
    }

    public void setReqID(int reqID) {
        this.reqID = reqID;
    }
    
    public abstract Boilerplate formatBP(Annotation an);

    public String getText() {
        return text;
    }
    
    public void setText(String text) {
        this.text = text;
    }
    
    public String getTyp(){
        return typ;
    }
    
    public FeatureMap getMap() {
        return map;
    }

    public void setMap(FeatureMap map) {
        this.map = map;
    }
    
    public boolean equals(Boilerplate bp){
        return this.text.equals(bp.getText());
    }

}
