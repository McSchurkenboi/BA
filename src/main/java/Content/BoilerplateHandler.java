package Content;

import gate.Annotation;
import gate.AnnotationSet;
import gate.Utils;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import Boilerplates.*;

/**
 * summarizes BP conversion
 *
 * @author rittfe1
 */
class BoilerplateHandler {

    /**
     * keeps track of the current paragraph and contained sentence for
     * traversing
     */
    int currentReq = -1;

    /**
     * the annie system the informations are received from
     */
    private final AnnieHandler annie;

    /**
     * stores the clean strings from each paragraph: input sentence, possible
     * conversions made by BPs
     */
    private ArrayList<LinkedList<String>> outputList;
//    private Map<String, String> boilerplateMap;

    /**
     * creates a new BoilerplateHandler, providing requirement processing to
     * ReqPad-powered BPs
     *
     * @param annie
     */
    public BoilerplateHandler(AnnieHandler annie) {
        this.annie = annie;
//        for (int i = 0; i < 100; i++) {
//            boilerplateMap.put("Boilerplate" + i, null);
//        }
    }

    /**
     * initializes the paragraph list from Annie Handler (parList) with the
     * sentence annotation for each paragraph. i.e. the paragraphs are splitted
     * sentencewise and can now each be traversed by the sentence annotation.
     * Must be called before BPs can be discovered over the sentences
     */
    public void initializeParagraphs() {
        //ArrayList with size of sentence count
        int sentenceCount = annie.doc.getAnnotations().get("Sentence").size();
        // System.out.println(sentenceCount);
        annie.parList = new ArrayList<>(sentenceCount);

        outputList = new ArrayList<>(sentenceCount);

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

            //System.out.println("Satz eingefügt: " + Utils.cleanStringFor(annie.doc, anno));
        });

        Main.gui.getConvertButton().setEnabled(false);
        Main.gui.getjProgressBar1().setMaximum(sentenceCount);
        Main.gui.getPbLabel2().setText(String.valueOf(sentenceCount));
    }

    /**
     * Fill parList with possible BP conversions, discovers BP annotaion within
     * span of each sentence
     */
    public void findPossibleConversions() {
        //Traverse each paragraph in parList

        //TODO reqgular expression BoilerplateXXX
        AnnotationSet bpSet = annie.doc.getAnnotations("Boilerplates").get();

        annie.parList.forEach((paragraph) -> {

            //find matching BPs within span of paragraph
            bpSet.stream().filter((bpAnno) -> (bpAnno.withinSpanOf(paragraph.get(0)))).forEachOrdered((e) -> paragraph.add(e));
        });

    }

    /**
     * converts for each BP possibility sentencewise, later maybe uses knowledge
     * about all possible BPs and finds best, so just one conversion is made and
     * stored to outputList
     */
    public void convertBPs() {

        int i = 0;

        //for each paragraph, the sentence is processed matching to the found boilerplate annotations
        boolean found = false;
        int count = 0;
        int count65 = 0;
        int count85 = 0;

        //Hierarchy of BPs: if 85 is found, 65c is always wrong and should not processed
        for (LinkedList<Annotation> paragraph : annie.parList) {

            //find if BP85 in contained
            boolean found85 = false, found65c = false;
            //run for lowest hierarchy level
            for (Annotation an : paragraph) {
                if ("Boilerplate85".equals(an.getType())) {
                    found85 = true;
                    outputList.get(i).add(new Boilerplate85().formatBP(an));
                    count85++;
                    count++;
                }
            }

            if (!found85) {
                for (Annotation an : paragraph) {
                    if ("Boilerplate65c".equals(an.getType())) {
                        found65c = true;
                        outputList.get(i).add(new Boilerplate65c().formatBP(an));
                        count65++;
                        count++;
                    }
                }
            }

            /*
            //OLD: Without hierarchy, just convert any found Boilerplates
            Iterator<Annotation> it = paragraph.iterator();
            it.next();

            while (it.hasNext()) {
                Annotation an = it.next();

                //all possible BPs, other way might be reflection/java maps with BP names and function pointers
                switch (an.getType()) {
                    case "Boilerplate65c":
                        outputList.get(i).add(new Boilerplate65c().formatBP(an));
                        count65++;
                        found = true;
                        break;
                    case "Boilerplate85":
                        outputList.get(i).add(new Boilerplate85().formatBP(an));
                        count85++;
                        if (found) {
                            countboth++;

                        }
                        found = true;
                }
            }
             */
            i++;
        }

        System.out.println("Sentences with found conversions: " + count);
        System.out.println("65: " + count65);
        System.out.println("85: " + count85);

        loadNextReq();
    }

    /**
     * saves changes from the GUI to the outputList
     *
     * the first element for the sentence in the outputList is overwritten with
     * the change
     */
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

    /**
     * load next paragraph to the GUI from the outputList,
     *
     * (clean string in upper text field, BP conversions fill the lower fields)
     */
    public void loadNextReq() {
        currentReq++;
        Main.gui.getjProgressBar1().setValue(currentReq);

        Main.gui.getPbLabel1().setText(String.valueOf(currentReq));
        /*
        while (currentReq < annie.parList.size() && (outputList.get(currentReq).size() == 1 || outputList.get(currentReq).get(0).contains("H_ObjectContent : ") || outputList.get(currentReq).get(0).contains("ID : "))) {
            currentReq++;
            Main.gui.getjProgressBar1().setValue(currentReq);

            Main.gui.getPbLabel1().setText(String.valueOf(currentReq));
        }
         */
        //Load next sentence and its annotations from outputList
        if (currentReq < annie.parList.size()) {

            Iterator<String> it = outputList.get(currentReq).iterator();

            Main.gui.getReqSelectionButtons().clearSelection();
            //Show the source sentence in the upper TextField
            Main.gui.getInputReq().setText(it.next());

            Main.gui.getOutputReq1().setText("");
            Main.gui.getOutputReq2().setText("");
            Main.gui.getOutputReq3().setText("");

            Main.gui.getReq1Button().setEnabled(false);
            Main.gui.getReq2Button().setEnabled(false);
            Main.gui.getReq3Button().setEnabled(false);

            //If no BPs were annotated
            if (!it.hasNext()) {
                Main.gui.getOutputReq1().setText("No boilerplate conversions were found.");
                Main.gui.getConfirmButton().setEnabled(false);
//                Main.gui.getjProgressBar1().setValue(++currentReq);

            } else {
                //Iterate over the BP annotated for this sentence and print them as suggestions
                Main.gui.getOutputReq1().setText(it.next());
                Main.gui.getReq1Button().setEnabled(true);

                if (it.hasNext()) {
                    Main.gui.getOutputReq2().setText(it.next());
                    Main.gui.getReq2Button().setEnabled(true);

                }
                if (it.hasNext()) {
                    Main.gui.getOutputReq3().setText(it.next());
                    Main.gui.getReq3Button().setEnabled(true);

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

    /**
     * exports to the file selected from the Export Button @GUI
     */
    public void exportToFileSystem() {
        try {

            FileOutputStream fos = new FileOutputStream(Main.outputFile);
            try (BufferedWriter bos = new BufferedWriter(new OutputStreamWriter(fos))) {
                for (LinkedList<String> list : outputList) {
                    bos.write(list.getFirst());
                    bos.newLine();
                    bos.newLine();
                }
            }
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
