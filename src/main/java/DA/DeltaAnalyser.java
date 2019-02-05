/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DA;

import Boilerplates.Boilerplate;
import Boilerplates.Boilerplate65c;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author rittfe1
 */
public class DeltaAnalyser {

    /**
     * bpList1 and bp1 represent OEM-Req., e.g. the new Req. bpList2 and bp2
     * represent Internal-Req, e.g. the old Req.
     */
    LinkedList<Boilerplate> bpList1;
    LinkedList<Boilerplate> bpList2;
    LinkedList<Boilerplate> tempBPList;
    int equalCount = 0, comparableCount = 0, differentCount = 0;
    BoilerplateComparator bpComparator;

    public DeltaAnalyser() {
        //    bpList1 = new LinkedList<>();
        //    bpList2 = new LinkedList<>();
        bpComparator = new BoilerplateComparator();
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
        for (Boilerplate boi : tempBPList) {
            System.out.println(boi.getText());
        }
    }

    public void analyseDeltas() {
        initializeIDs();
        int result;
        Boilerplate tempBP = new Boilerplate65c();
        for (Boilerplate bp1 : bpList1) {
            result = 6;
            for (Boilerplate bp2 : bpList2) {
                switch (bpComparator.compare(bp1, bp2)) {
                    case 0: //Identical
                        result = 0;
                        tempBP = bp2;
                        break;
                    case 1: //Comparable with Deltas
                        //If we can reach a smaller result
                        if (result > 1 && annotationCompare(bp1, bp2) < result) {
                            //Just do it
                            result = annotationCompare(bp1, bp2);
                            tempBP = bp2;
                        }
                        break;
                    case 2: //Completely different
                        if (result > 5) {
                            result = 5;
                            tempBP = bp2;
                        }
                        break;
                }
                System.out.println(result);
                if (result == 0) {
                    break;
                }
            }
            switch (result) {
                case 0:
                    System.out.println("OEM-Anf " + bp1.getReqID() + " und interne Anf " + tempBP.getReqID() + " sind identisch:" + bp1.getText());
                    break;
                case 1:
                    System.out.println("OEM-Anf " + bp1.getReqID()+ ": " + bp1.getText() + " und interne Anf " + tempBP.getReqID() + ": " + tempBP.getText() + " beinhalten UNTERSCHIEDLICHE INFORMATIOEN zum SELBEN SYSTEM.");
                    break;
                case 2:
                    System.out.println("OEM-Anf " + bp1.getReqID()+ ": " + bp1.getText() + " und interne Anf " + tempBP.getReqID() + ": " + tempBP.getText() + " beinhalten UNTERSCHIEDLICHE INFORMATIOEN zum GLEICHEN SYSTEMTYP. " + Vehicle.class.getName());
                    break;
                case 6:
                    System.out.println("No matches found for " + bp1.getReqID()+ ": " + bp1.getText());
            }
        }
    }

    public int annotationCompare(Boilerplate bp1, Boilerplate bp2) {
        switch (bp2.getTyp()) {
            case "65c": //Type Boilerplate65c
                Boilerplate65c boi1 = (Boilerplate65c) bp1;
                Boilerplate65c boi2 = (Boilerplate65c) bp2;
                if (boi1.getCompleteSystemText().equals(boi2.getCompleteSystemText())) {
                    return 1;
                    //Check if type of CompleteSystem matches or generalization is used
                } else if ((Vehicle.contains(boi1.getCompleteSystemText().toLowerCase()) && Vehicle.contains(boi2.getCompleteSystemText().toLowerCase()))) {
                    return 2;
                    //Systems are different, but same BP.
                } else {
                    return 3;
                }
            case "85": //Type Boilerplate85 ....TODO
        }
        return 4;
    }
    
    public void initializeIDs(){
        //goes through both BP-lists and gives each BP an unique ID (in the same BP-List)
        //could later be replaced by reading the IDs for the req specification in the R2BC
        int oemID=1;
        int hellaID=1;
        for (Boilerplate bp1 : bpList1) {
            bp1.setReqID(oemID++);
        }
        for (Boilerplate bp2 : bpList2) {
            bp2.setReqID(hellaID++);
        }
    }
}
