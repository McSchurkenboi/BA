/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DA;

import Boilerplates.Boilerplate;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author rittfe1
 */
public class DeltaAnalyser {

    LinkedList<Boilerplate> bpList1;
    LinkedList<Boilerplate> bpList2;
    LinkedList<Boilerplate> tempBPList;

    public DeltaAnalyser() {
        bpList1 = new LinkedList<>();
        bpList2 = new LinkedList<>();
    }

    public void loadBPsFromFile() {
        try {
            FileInputStream fis = new FileInputStream(Main.bp);
            ObjectInputStream ois = new ObjectInputStream(fis);
            tempBPList = (LinkedList<Boilerplate>) ois.readObject();
            
            
        } catch (IOException ex) {
            Logger.getLogger(DeltaAnalyser.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(DeltaAnalyser.class.getName()).log(Level.SEVERE, null, ex);
        }
        for(Boilerplate boi : tempBPList){
            System.out.println(boi.getBp());
        }
    }

}
