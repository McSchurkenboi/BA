
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
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
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
    public ArrayList<LinkedList<String>> parList;

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

            BoilerplateHandler bpHandler = new BoilerplateHandler(this);
            bpHandler.initializeParagraphs();
            //bpHandler.findPossibleConversions();
            
        } catch (ExecutionException | MalformedURLException | ResourceInstantiationException ex) {
            Logger.getLogger(AnnieHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    
    public void outputProcessedReq(String output) {

        System.out.println(output);
        // Write output files
        Main.gui.getOutputReq().setText(output);

    }

    public void exportToFileSystem() {
        try {

            FileOutputStream fos = new FileOutputStream(Main.outputFile);
            BufferedWriter bos = new BufferedWriter(new OutputStreamWriter(fos));

            for (LinkedList<String> list : parList) {
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
