
import gate.Corpus;
import gate.CorpusController;
import gate.Factory;
import gate.Gate;
import gate.creole.ANNIEConstants;
import gate.creole.ConditionalSerialAnalyserController;
import gate.creole.Plugin;
import gate.creole.ResourceReference;
import gate.creole.SerialAnalyserController;
import gate.util.GateException;
import gate.util.persistence.PersistenceManager;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 * @author rittfe1
 */
public class AnnieHandler {

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
            System.out.println(anniePlugin.getDescription()); 
        } catch (GateException | URISyntaxException | IOException ex) {
            Logger.getLogger(AnnieHandler.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void initFromRestore() {
        try {
            Gate.init();
            CorpusController controller = (CorpusController) PersistenceManager.loadObjectFromFile(new File("C:\\Users\\rittfe1\\Desktop\\gateSachen\\MyANNIE.gapp"));

            Corpus corpus = Factory.newCorpus("MyCorpus");
            controller.setCorpus(corpus);

            corpus.add(Factory.newDocument(new File("C:\\Users\\rittfe1\\Desktop\\gateSachen\\Lastenheft.txt").toURI().toURL()));

            controller.execute();

            corpus.clear();

        } catch (GateException ex) {
            System.out.println("Gate-Fehler");
            ex.printStackTrace();
        } catch (IOException ex) {
            Logger.getLogger(AnnieHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
