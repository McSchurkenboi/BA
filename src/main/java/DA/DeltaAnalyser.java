/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DA;

import Boilerplates.Boilerplate;
import Boilerplates.Boilerplate65c;
import Boilerplates.Boilerplate85;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;
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
    /**
     * Boilerplate List for new document
     */
    LinkedList<Boilerplate> bpList1;

    /**
     * Boilerplate List for old document
     */
    LinkedList<Boilerplate> bpList2;

    /**
     * Boilerplate List for loaded file
     */
    LinkedList<Boilerplate> tempBPList;
    int equalCount = 0, comparableCount = 0, differentCount = 0;

    /**
     * Compares two boilerplates by its annotations and contained Strings
     */
    BoilerplateComparator bpComparator;

    /**
     * the result String that is written into file system
     */
    String outputString;

    public DeltaAnalyser() {
        //    bpList1 = new LinkedList<>();
        //    bpList2 = new LinkedList<>();
        bpComparator = new BoilerplateComparator();
    }

    /**
     * initialize List of boilerplate
     */
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

    /**
     * iterates over all old requiremtns for each new requirement finds best
     * matching boilerplate by type, annotation, and text part comparison
     */
    public void analyseDeltas() {
        initializeIDs();
        int result;
        Boilerplate tempBP = new Boilerplate65c();
        try {

            FileOutputStream fos = new FileOutputStream(new File("C:\\Users\\rittfe1\\Desktop\\delta-report.txt"));
            BufferedWriter bos = new BufferedWriter(new OutputStreamWriter(fos));

            System.out.println("Saved to file");

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

                System.out.println(printResult(bp1, tempBP, result));
                bos.write(printResult(bp1, tempBP, result));
                bos.newLine();
            }
            bos.write("Übereinstimmend: " + equalCount + " von " + bpList1.size());
            bos.write("Keine Zuweisung möglich" + differentCount + " von " + bpList1.size());
            bos.flush();
        } catch (IOException ex) {
        }
    }

    /**
     * finds the agreement level of annotations from two boilerplates of the
     * same type
     *
     * @param bp1 new req in boilerplate
     * @param bp2 old requirement in boilerplate
     * @return
     */
    public int annotationCompare(Boilerplate bp1, Boilerplate bp2) {
        switch (bp2.getTyp()) {
            case "65c": //Type Boilerplate65c
                Boilerplate65c b651 = (Boilerplate65c) bp1;
                Boilerplate65c b652 = (Boilerplate65c) bp2;
                if (b651.getCompleteSystemText().equals(b652.getCompleteSystemText())) {
                    //Same CompleteSystem
                    return 1;

                } else if ((Vehicle.contains(b651.getCompleteSystemText().toLowerCase()) && Vehicle.contains(b652.getCompleteSystemText().toLowerCase()))) {
                    //Generalization of CompleteSystem is used
                    return 2;

                } else {
                    //Systems are different, but same BP.
                    return 3;
                }
            case "85": //Type Boilerplate85
                Boilerplate85 b851 = (Boilerplate85) bp1;
                Boilerplate85 b852 = (Boilerplate85) bp2;
                if (b851.getConditionalActorText().equals(b852.getConditionalActorText()) && b851.getConditionText().equals(b852.getConditionText())) {
                    //Same Actor and Condition, but different function
                    return 1;
                } else if (b851.getConditionalActorText().equals(b852.getConditionalActorText()) && b851.getFunctionDescriptionText().equals(b852.getFunctionDescriptionText())) {
                    //Same Actor and Function, but different condition
                    return 2;
                } else if ((Vehicle.contains(b851.getConditionalActorText().toLowerCase()) && Vehicle.contains(b852.getConditionalActorText().toLowerCase())) && b851.getConditionText().equals(b852.getConditionText())) {
                    //Same generalization of Actor and Condition, but different function
                    return 3;
                } else if ((Vehicle.contains(b851.getConditionalActorText().toLowerCase()) && Vehicle.contains(b852.getConditionalActorText().toLowerCase())) && b851.getFunctionDescriptionText().equals(b852.getFunctionDescriptionText())) {
                    //Same generalization of Actor and function, but different condition
                    return 4;
                } else {
                    //Systems are different, but same BP
                    return 5;
                }
        }
        return 4;
    }

    /**
     * goes through both BP-lists and gives each BP an unique ID (in the same
     * BP-List) could later be replaced by reading the IDs for the req
     * specification in the R2BC
     */
    public void initializeIDs() {
        int oemID = 1;
        int hellaID = 1;
        for (Boilerplate bp1 : bpList1) {
            bp1.setReqID(oemID++);
        }
        for (Boilerplate bp2 : bpList2) {
            bp2.setReqID(hellaID++);
        }
    }

    /**
     * processes output for the results of comparison for two boilerplates based
     * on their agreement level
     *
     * @param bp1 new requirement in boilerplate
     * @param tempBP old requirement in boilerplate
     * @param result result of comparsion that defines agreement level
     * @return valuation as String
     */
    private String printResult(Boilerplate bp1, Boilerplate tempBP, int result) {
        switch (tempBP.getTyp()) {
            case "65c":
                switch (result) {
                    case 0:
                        equalCount++;
                        return "OEM-Anf " + bp1.getReqID() + " und interne Anf " + tempBP.getReqID() + " sind identisch:" + bp1.getText();
                    case 1:
                        return "OEM-Anf " + bp1.getReqID() + ": " + bp1.getText() + " und interne Anf " + tempBP.getReqID() + ": " + tempBP.getText() + " beinhalten UNTERSCHIEDLICHE INFORMATIONEN zum SELBEN SYSTEM.";
                    case 2:
                        return "OEM-Anf " + bp1.getReqID() + ": " + bp1.getText() + " und interne Anf " + tempBP.getReqID() + ": " + tempBP.getText() + " beinhalten UNTERSCHIEDLICHE INFORMATIONEN zum GLEICHEN SYSTEMTYP. " + Vehicle.class.getName();
                    case 3:
                        return "OEM-Anf " + bp1.getReqID() + ": " + bp1.getText() + " und interne Anf " + tempBP.getReqID() + ": " + tempBP.getText() + " sind ohne inhaltliche Übereinstimmung, aber in der gleichen Boilerplate.";
                    case 6:
                        differentCount++;
                        return "No matches found for " + bp1.getReqID() + ": " + bp1.getText();
                }
                return "No results found";
            case "85":
                switch (result) {
                    case 0:
                        equalCount++;
                        return "OEM-Anf " + bp1.getReqID() + " und interne Anf " + tempBP.getReqID() + " sind identisch:" + bp1.getText();
                    case 1:
                        return "OEM-Anf " + bp1.getReqID() + ": " + bp1.getText() + " und interne Anf " + tempBP.getReqID() + ": " + tempBP.getText() + " beinhhalten UNTERSCHIEDLICHE INFORMATIONEN zum SELBEN SYSTEM zur SELBEN BEDINGUNG";
                    case 2:
                        return "OEM-Anf " + bp1.getReqID() + ": " + bp1.getText() + " und interne Anf " + tempBP.getReqID() + ": " + tempBP.getText() + " beinhhalten UNTERSCHIEDLICHE BEDINGUNGEN zum SELBEN SYSTEM zur SELBEN INFORMATION";
                    case 3:
                        return "OEM-Anf " + bp1.getReqID() + ": " + bp1.getText() + " und interne Anf " + tempBP.getReqID() + ": " + tempBP.getText() + " beinhhalten UNTERSCHIEDLICHE INFORMATIONEN zum GLEICHEN SYSTEMTYP zur SELBEN BEDINGUNG";
                    case 4:
                        return "OEM-Anf " + bp1.getReqID() + ": " + bp1.getText() + " und interne Anf " + tempBP.getReqID() + ": " + tempBP.getText() + " beinhhalten UNTERSCHIEDLICHE BEDINGUNGEN zum GLEICHEN SYSTEMTYP zur SELBEN INFORMATION";
                    case 5:
                        return "OEM-Anf " + bp1.getReqID() + ": " + bp1.getText() + " und interne Anf " + tempBP.getReqID() + ": " + tempBP.getText() + " sind ohne inhaltliche Übereinstimmung, aber in der gleichen Boilerplate.";
                    case 6:
                        differentCount++;
                        return "No matches found for " + bp1.getReqID() + ": " + bp1.getText();
                }
        }
        return "fehler";
    }
}
