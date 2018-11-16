
import gate.AnnotationSet;
import gate.Corpus;
import gate.CorpusController;
import gate.Document;
import gate.Factory;
import gate.Gate;
import gate.creole.ANNIEConstants;
import gate.creole.ConditionalSerialAnalyserController;
import gate.creole.ExecutionException;
import gate.creole.Plugin;
import gate.creole.ResourceInstantiationException;
import gate.creole.ResourceReference;
import gate.util.GateException;
import gate.util.persistence.PersistenceManager;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
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
 * @author rittfe1
 */
public class AnnieHandler {

    static CorpusController controller;
    static ArrayList parList;

    public void init() {
        try {

            // initialise the GATE library
            Gate.init();
            // load the ANNIE plugin
            Plugin anniePlugin = new Plugin.Maven("uk.ac.gate.plugins", "annie", "8.5");
            Gate.getCreoleRegister().registerPlugin(anniePlugin);

            // load ANNIE application from inside the plugin
            ConditionalSerialAnalyserController controller = (ConditionalSerialAnalyserController) PersistenceManager.loadObjectFromUrl(new ResourceReference(
                    anniePlugin, "resources/" + ANNIEConstants.DEFAULT_FILE)
                    .toURL());
        } catch (GateException | URISyntaxException | IOException ex) {
            Logger.getLogger(AnnieHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void initFromRestore() {
        try {

            // initialise the GATE library
            Gate.init();

            //restore from File
            controller = (CorpusController) PersistenceManager.loadObjectFromFile(new File("src\\gateSachen\\ANNIE Saves\\KonstantinJape.gapp"));

        } catch (GateException ex) {
            System.out.println("Gate-Fehler");
            ex.printStackTrace();
        } catch (IOException ex) {
            Logger.getLogger(AnnieHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void execute() {
        try {
            //create Corpus for document from file system
            Corpus corpus = Factory.newCorpus("MyCorpus");
            controller.setCorpus(corpus);
            File source = Main.inputFile;
            Document doc = Factory.newDocument(source.toURI().toURL());
            corpus.add(doc);

            //ArrayList with size of sentence count
            parList = new ArrayList(doc.getAnnotations("paragraph").size());
            
            //For each sentence, a list of possible BP conversions is created
            for (int i = 0 ; i<parList.size(); i++){
                parList.set(i, new LinkedList());
            }

            System.out.println(controller.getPRs().toString());

            controller.execute();

            //create List of BPs to iterate
            List annotTypes = new ArrayList();
            annotTypes.add("Boilerplate65c");

            if (annotTypes != null) {
                // Create a temporary Set to hold the annotations we wish to write out
                Set annotationsToWrite = new HashSet();

                // we only extract annotations from the default (unnamed) AnnotationSet
                // in this example
                AnnotationSet defaultAnnots = doc.getAnnotations();
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
                outputProcessedReq(doc.toXml(annotationsToWrite));
            }
            /*            Set annotationsToWrite = new HashSet();
            AnnotationSet annSet = doc.getAnnotations("");
            annotationsToWrite.addAll(annSet.get("Boilerplate65c"));
            outputProcessedReq(doc.toXml(annotationsToWrite));
             */

            //System.out.println(corpus.get(0).toXml());
        } catch (ExecutionException | MalformedURLException | ResourceInstantiationException ex) {
            Logger.getLogger(AnnieHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void outputProcessedReq(String output) {

        System.out.println(output);
        // Write output files
        Main.gui.getOutputReq().setText(output);

    }

    public void exportToFileSystem() {
        try {

            FileOutputStream fos = new FileOutputStream(Main.outputFile);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            OutputStreamWriter out;
            out = new OutputStreamWriter(bos);

            for (Object o : parList) {
                out.write(o.toString());
            }

            out.close();

        } catch (IOException ex) {
            Logger.getLogger(AnnieHandler.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
