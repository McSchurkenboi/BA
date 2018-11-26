
import gate.Annotation;
import gate.AnnotationSet;
import gate.FeatureMap;
import gate.SimpleAnnotationSet;
import gate.Utils;
import gate.annotation.AnnotationSetImpl;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author rittfe1
 */
class BoilerplateHandler {

    int currentReq = -1;
    private final AnnieHandler annie;
    private ArrayList<LinkedList<String>> outputList;
//    private Map<String, String> boilerplateMap;

    public BoilerplateHandler(AnnieHandler annie) {
        this.annie = annie;
//        for (int i = 0; i < 100; i++) {
//            boilerplateMap.put("Boilerplate" + i, null);
//        }
    }

    public void initializeParagraphs() {
        //ArrayList with size of sentence count
        int sentenceCount = annie.doc.getAnnotations().get("Sentence").size();
        System.out.println(sentenceCount);
        annie.parList = new ArrayList<>(sentenceCount);

        outputList = new ArrayList<>(sentenceCount);
//        LinkedList<Annotation> annotations = new LinkedList<>();
//        annie.doc.getAnnotations().get("Sentence").forEach(a -> annotations.add(a));

        AnnotationSet annotations = annie.doc.getAnnotations().get("Sentence");

        annotations.forEach((anno) -> {
            //Initialize parList
            LinkedList<Annotation> liste = new LinkedList<>();
            liste.add(anno);
            annie.parList.add(liste);

            //Initialize outputList with source sentence as first element
            LinkedList<String> output = new LinkedList<>();
            output.add(Utils.stringFor(annie.doc, anno));
            outputList.add(output);

            System.out.println("Satz eingefügt: " + Utils.cleanStringFor(annie.doc, anno));
        });

        Main.gui.getConvertButton().setEnabled(false);
        Main.gui.getjProgressBar1().setMaximum(sentenceCount);
        Main.gui.getPbLabel2().setText(String.valueOf(sentenceCount));
        //Load first element to the GUI

        /*    //For each sentence, a list of possible BP conversions is created, first element is source sentence
        LinkedList sentences = new LinkedList(annie.doc.getAnnotations().get(new HashSet<>(Arrays.asList("Sentence"))));
        Iterator it = sentences.iterator();
        int i = 0;
        for (Object o : sentences) {
            LinkedList<String> liste = new LinkedList<>();
            liste.add((String) o);
            annie.parList.add(i, liste);
            i++;
        }

        //annie.doc.getAnnotations().get(new HashSet<>(Arrays.asList("Sentence"))).forEach(a -> System.err.format("%s - \"%s\" [%d to %d]\n",
        //        a.getType(), Utils.stringFor(annie.doc, a),
        //        a.getStartNode().getOffset(), a.getEndNode().getOffset()));
        //LinkedList liste = new LinkedList();
        for (LinkedList o : annie.parList) {
            for(String ob : o){
                System.out.println(ob);
            }
        }
         */
        //System.out.println("Element:" + liste.getFirst());
    }

    //Fill parList with possible conversions
    public void findPossibleConversions() {
        //Traverse each paragraph in parList

        //TODO reqgular expression BoilerplateXXX
        AnnotationSet bpSet = annie.doc.getAnnotations("Boilerplates").get();

        annie.parList.forEach((paragraph) -> {

            //find matching BPs within span of paragraph
            bpSet.stream().filter((bpAnno) -> (bpAnno.withinSpanOf(paragraph.get(0)))).forEachOrdered((e) -> paragraph.add(e));
        });

    }

    //converts for each BP possibility sentencewise, 
    //later maybe uses knowledge about all possible BPs and finds best, so just one conversion is made and stored to outputList
    public void convertBPs() {

        int i = 0;

        //for each paragraph, the sentence is processed matching to the found boilerplate annotations
        for (LinkedList<Annotation> paragraph : annie.parList) {
            Iterator<Annotation> it = paragraph.iterator();
            it.next();

            while (it.hasNext()) {
                Annotation an = it.next();
                //all possible BPs, other way might be reflection/java maps with BP names and function pointers
                switch (an.getType()) {
                    case "Boilerplate65c":
                        outputList.get(i).add(formatBP65c(an));
                        break;
                    case "Boilerplate85":
                        outputList.get(i).add(formatBP85(an));
                }
            }
            i++;
        }

        loadNextReq();
    }

    private String formatBP65c(Annotation an) {
        FeatureMap map = an.getFeatures();
        return "The complete system <<" + map.get("completeSystemNameText") + ">> shall <<" + map.get("functionDescriptionText") + ">>.";
//return "The " //+ Utils.stringFor(annie.doc, an.getFeatures().get("CompleteSystemNameText"));
    }

    private String formatBP85(Annotation an) {
        FeatureMap map = an.getFeatures();
        return "Under the condition: <<" + map.get("conditionText") + ">> the actor: <<" + map.get("conditionalActorText") + ">> shall <<" + map.get("functionDescriptionText") + ">>."; 
    }

    //saves changes from the GUI to the outputList
    public void storeProcessedReq() {
        //If changed, than append selected requirement from the text boxes to the output List
        if (!"".equals(Main.gui.getOutputReq1().getText())) {
            if (Main.gui.getReq1Button().isSelected()) {
                outputList.get(currentReq).set(0, Main.gui.getOutputReq1().getText());
            } else if (Main.gui.getReq2Button().isSelected()) {
                outputList.get(currentReq).set(0, Main.gui.getOutputReq2().getText());
            } else if (Main.gui.getReq3Button().isSelected()) {
                outputList.get(currentReq).set(0, Main.gui.getOutputReq3().getText());
            }
        }
    }

    //load next paragraph to the GUI
    public void loadNextReq() {
        Main.gui.getjProgressBar1().setValue(++currentReq);

        Main.gui.getPbLabel1().setText(String.valueOf(currentReq));

        //Load next sentence and its annotations from parList
        if (currentReq < annie.parList.size()) {
            Iterator<String> it = outputList.get(currentReq).iterator();

            //Show the source sentence in the upper TextField
            Main.gui.getInputReq().setText(it.next());

            Main.gui.getOutputReq1().setText("");
            Main.gui.getOutputReq2().setText("");
            Main.gui.getOutputReq3().setText("");

            //If no BPs were annotated
            if (!it.hasNext()) {
                Main.gui.getOutputReq1().setText("No boilerplate conversions were found.");
                Main.gui.getConfirmButton().setEnabled(false);
//                Main.gui.getjProgressBar1().setValue(++currentReq);

            } else {
                //Iterate over the BP annotated for this sentence and print them as suggestions
                Main.gui.getOutputReq1().setText(it.next());

                if (it.hasNext()) {
                    Main.gui.getOutputReq2().setText(it.next());
                }
                if (it.hasNext()) {
                    Main.gui.getOutputReq3().setText(it.next());
                }

                Main.gui.getConfirmButton().setEnabled(true);
            }

        } else {
            //End of requirements reached
            Main.gui.getInputReq().setText("All requirements processed. \n Please press export to save the results.");
            Main.gui.getOutputReq1().setText("");

            Main.gui.getConfirmButton().setEnabled(false);
            Main.gui.getSkipButton().setEnabled(false);
            Main.gui.getExportButton().setEnabled(true);

        }
    }

    public void exportToFileSystem() {
        try {

            FileOutputStream fos = new FileOutputStream(Main.outputFile);
            BufferedWriter bos = new BufferedWriter(new OutputStreamWriter(fos));

            for (LinkedList<String> list : outputList) {
                bos.write(list.getFirst());
                bos.newLine();
            }

            bos.close();
            System.out.println("Saved to file:" + Main.outputFile);

        } catch (IOException ex) {
            Logger.getLogger(AnnieHandler.class
                    .getName()).log(Level.SEVERE, null, ex);
        }

    }

}

//OLD: findBPAnnotations

/*
        //Load List of BPs for a sentence
        LinkedList<Annotation> bpList = (LinkedList<Annotation>) annie.parList.get(0);
        //bpList.add("Boilerplate65c");
        System.out.println("Anzahl Sätze:" + annie.parList.size());

        //create List of BPs to iterate
        List annotTypes = new ArrayList();
        annotTypes.add("Boilerplate65c");

        if (annotTypes != null) {
            // Create a temporary Set to hold the annotations we wish to write out
            Set annotationsToWrite = new HashSet();

            // we only extract annotations from the default (unnamed) AnnotationSet
            // in this example
            AnnotationSet defaultAnnots = annie.doc.getAnnotations();
            Iterator annotTypesIt = annotTypes.iterator();

            //Find sentences matching to the BPs
            while (annotTypesIt.hasNext()) {
                // extract all the annotations of each requested type and add them to
                // the temporary set
                String current = (String) annotTypesIt.next();
                AnnotationSet annotsOfThisType = defaultAnnots.get(current);

                // If taggings were found, add them to the output Set
                if (annotsOfThisType != null) {
                    annotationsToWrite.addAll(annotsOfThisType);
                }
            }

            //
            annie.outputProcessedReq(annie.doc.toXml(annotationsToWrite));
        }
        /*            Set annotationsToWrite = new HashSet();
            AnnotationSet annSet = doc.getAnnotations("");
            annotationsToWrite.addAll(annSet.get("Boilerplate65c"));
            outputProcessedReq(doc.toXml(annotationsToWrite));
 */
        //System.out.println(corpus.get(0).toXml());
