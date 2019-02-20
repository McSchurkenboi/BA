package R2BC;


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

/**
 * creates GATE resources and manages GATE pipeline
 *
 * @author rittfe1
 */
public class AnnieHandler {

    /**
     * the GATE corpus for the document
     */
    public Corpus corpus;

    /**
     * the document loaded in the GUI
     */
    public Document doc;

    /**
     * the controller, which consists of an annie module-pipeline
     */
    public CorpusController controller;

    
    BoilerplateHandler bpHandler;

    /**
     * @TODO create pipeline in java, we use initFromRestore*() instead
     */
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

    /**
     * create pipeline from restored .gapp file in the program folder
     */
    public void initFromRestore() {
        try {

            // initialise the GATE library
            Gate.init();

            //restore from File
            controller = (CorpusController) 
                    PersistenceManager.loadObjectFromFile(new File("C:\\Users\\rittfe1\\Documents\\NetBeansProjects\\BA\\src\\gateSachen\\ANNIE Saves\\GazetteerTest.gapp"));

        } catch (GateException ex) {
            System.out.println("Gate-Fehler");
            ex.printStackTrace();
        } catch (IOException ex) {
            Logger.getLogger(AnnieHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * starts the ANNIE pipeline in the (GATE-)controller when convert button is
     * pressed, so that text is annotaded.
     *
     * Then buils up internal program structure out of annotations, finds and
     * converts boilerplates, and stores each of them in internal lists
     */
    public void convert() {
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

            //Traverse BP-Annotations and add to sentenceList
            bpHandler.findPossibleConversions();

            //convert annotated sentences to matching BPs, store Boilerplates
            bpHandler.convertBPs();

        } catch (ExecutionException | MalformedURLException | ResourceInstantiationException ex) {
            Logger.getLogger(AnnieHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
