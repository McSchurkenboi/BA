
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.JFileChooser;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author rittfe1
 */
public class Main {
    
    static R2BMainWindow gui;
    static File inputFile, outputFile;
    static AnnieHandler annie;

    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        
        annie = new AnnieHandler();
        
        annie.initFromRestore();
        
        initGUI();

        //outputFile = new File("C:\\Users\\rittfe1\\Desktop\\output.txt");
    }

    /**
     * defines functions for the GUI elements and builds initial config
     *
     * shows GUI
     */
    public static void initGUI() {
        JFileChooser dialog = new JFileChooser();
        //Events

        java.awt.EventQueue.invokeLater(() -> {
            gui = new R2BMainWindow();
            
            gui.getPbLabel1().setText("0");
            gui.getPbLabel2().setText("0");
            
            gui.getConfirmButton().setEnabled(false);
            gui.getSkipButton().setEnabled(false);
            gui.getReviewButton().setEnabled(false);
            gui.getConvertButton().setEnabled(false);
            gui.getExportButton().setEnabled(false);
            
            gui.getjProgressBar1().setStringPainted(true);
            
            gui.getInputReq().setLineWrap(true);
            gui.getInputReq().setWrapStyleWord(true);
            gui.getOutputReq1().setLineWrap(true);
            gui.getOutputReq1().setWrapStyleWord(true);
            gui.getOutputReq2().setLineWrap(true);
            gui.getOutputReq2().setWrapStyleWord(true);
            gui.getOutputReq3().setLineWrap(true);
            gui.getOutputReq3().setWrapStyleWord(true);
            
            gui.getLoadButton().addActionListener((ActionEvent e) -> {
                dialog.showOpenDialog(gui);
                inputFile = dialog.getSelectedFile();
                if (inputFile != null) {
                    final long startTime = System.currentTimeMillis();
                    try {
                        gui.getInputReq().read(new FileReader(inputFile), e);
                    } catch (IOException e1) {
                        System.out.println("Fehler beim Lesen der Datei.");
                    }
                    System.out.println("Loaded:" + inputFile.getName());
                    dialog.setSelectedFile(null);
                    System.out.println("Lade-Zeit: " + (System.currentTimeMillis() - startTime));
                }
                gui.getConvertButton().setEnabled(true);
            });
            
            gui.getExportButton().addActionListener((ActionEvent e) -> {
                dialog.showSaveDialog(gui);
                outputFile = dialog.getSelectedFile();
                if (outputFile != null) {
                    annie.bpHandler.exportToFileSystem();
                }
                dialog.setSelectedFile(null);
            });
            
            gui.getSkipButton().addActionListener((ActionEvent e) -> {
                annie.bpHandler.loadNextReq();
            });
            
            gui.getConfirmButton().addActionListener((ActionEvent e) -> {
                annie.bpHandler.storeProcessedReq();
                annie.bpHandler.loadNextReq();
            });
            
            gui.getConvertButton().addActionListener((ActionEvent e) -> {
                final long startTime = System.currentTimeMillis();
                annie.execute();
                gui.getConfirmButton().setEnabled(true);
                gui.getSkipButton().setEnabled(true);
                gui.getReviewButton().setEnabled(true);
                System.out.println("Convert-Zeit: " + (System.currentTimeMillis() - startTime));
            });
            
            gui.setVisible(true);
            System.out.println("GUI geladen.");
            
        });
    }
}
