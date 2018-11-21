
import gate.Annotation;
import gate.AnnotationSet;
import gate.Utils;
import java.awt.event.ActionEvent;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
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

    int currentReq = 0;
    private final AnnieHandler annie;
    public ArrayList<LinkedList<String>> outputList;

    public BoilerplateHandler(AnnieHandler annie) {
        this.annie = annie;
        Main.gui.getConfirmButton().addActionListener((ActionEvent e) -> {
            storeProcessedReq();
            loadNextReq();
        });
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

        for (Annotation anno : annotations) {
            //Initialize parList
            LinkedList<Annotation> liste = new LinkedList<>();
            liste.add(anno);
            annie.parList.add(liste);

            //Initialize outputList with source sentence as first element
            LinkedList<String> output = new LinkedList<>();
            output.add(Utils.stringFor(annie.doc, anno));
            outputList.add(output);

            System.out.println("Satz eingefügt: " + Utils.cleanStringFor(annie.doc, anno));
        }

        Main.gui.getConvertButton().setEnabled(false);
        //Load first element to the GUI
        loadNextReq();

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
        Main.gui.getjProgressBar1().setMaximum(sentenceCount);
        Main.gui.getPbLabel2().setText(String.valueOf(sentenceCount));
    }

    //Fill parList with possible conversions
    public void findPossibleConversions() {

        for (Annotation anno : annie.parList.get(currentReq)) {

        }

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
    }

    private void storeProcessedReq() {
        //If changed, than append processed requirement to the output List
        if (!"".equals(Main.gui.getOutputReq().getText())) {
            outputList.get(currentReq).set(0,Main.gui.getOutputReq().getText());
        }
        Main.gui.getjProgressBar1().setValue(++currentReq);
    }

    private void loadNextReq() {
        Main.gui.getPbLabel1().setText(String.valueOf(currentReq));

        //Load next sentence and its annotations from parList
        if (currentReq < annie.parList.size()) {
            Iterator<String> it = outputList.get(currentReq).iterator();
            
            //Show the source sentence in the upper TextField
            Main.gui.getInputReq().setText(it.next());

            //Iterate over the BP annotated for this sentence and print them as suggestions
            while (it.hasNext()) {
                Main.gui.getOutputReq().append(it.next());
            }
        } else {
            Main.gui.getInputReq().setText("All requirements processed. \n Please press export to save the results.");
            Main.gui.getConfirmButton().setEnabled(false);

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
            Logger.getLogger(AnnieHandler.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
