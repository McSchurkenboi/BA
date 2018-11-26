
import gate.Annotation;
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
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 * @author rittfe1
 */
public class AnnieHandler {

    public Corpus corpus;
    public Document doc;
    public CorpusController controller;
    public ArrayList<LinkedList<Annotation>> parList;
    BoilerplateHandler bpHandler;

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
            corpus = Factory.newCorpus("MyCorpus");
            controller.setCorpus(corpus);
            File source = Main.inputFile;
            doc = Factory.newDocument(source.toURI().toURL());
            corpus.add(doc);

            controller.execute();

            bpHandler = new BoilerplateHandler(this);
            
            //Load paragraphs and initialize Lists
            bpHandler.initializeParagraphs();
            
            //Traverse BP-Annotations and add to parList
            bpHandler.findPossibleConversions();
            
            //convert annotated sentences to matching BPs, store strings
            bpHandler.convertBPs();

        } catch (ExecutionException | MalformedURLException | ResourceInstantiationException ex) {
            Logger.getLogger(AnnieHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void outputProcessedReq(String output) {

        System.out.println(output);
        // Write output files
        Main.gui.getOutputReq1().setText(output);

    }

}