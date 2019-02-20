/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DA;

import java.awt.event.ActionEvent;
import java.io.File;
import javax.swing.JFileChooser;

/**
 *
 * @author rittfe1
 */
public class Main {

    static DAMainWindow gui;
    static DeltaAnalyser DA;
    static File bp;

    public static void main(String[] args) {

        DA = new DeltaAnalyser();

        initGUI();
    }

    private static void initGUI() {

        JFileChooser dialog = new JFileChooser();
        java.awt.EventQueue.invokeLater(() -> {
            gui = new DAMainWindow();

            gui.getjLabel1().setVisible(false);
            gui.getjLabel2().setVisible(false);
            gui.getExportButton().setEnabled(false);

            gui.getBPImportButton1().addActionListener((ActionEvent e) -> {
                dialog.showOpenDialog(gui);
                bp = dialog.getSelectedFile();
                if (bp != null) {
                    DA.loadBPsFromFile();
                    gui.getjTextField1().setText(bp.getName());
                    gui.getjTextField1().setEditable(false);
                    gui.getjLabel1().setVisible(true);
                    DA.bpList1 = DA.tempBPList;
                    dialog.setSelectedFile(null);
                }
            });

            gui.getBPImportButton2().addActionListener((ActionEvent e) -> {
                dialog.showOpenDialog(gui);
                bp = dialog.getSelectedFile();
                if (bp != null) {
                    DA.loadBPsFromFile();
                    gui.getjTextField2().setText(bp.getName());
                    gui.getjTextField2().setEditable(false);
                    gui.getjLabel2().setVisible(true);
                    DA.bpList2 = DA.tempBPList;
                    dialog.setSelectedFile(null);
                }
            });

            gui.getAnalyseButton().addActionListener((ActionEvent e) -> {
                DA.analyseDeltas();
                gui.getExportButton().setEnabled(true);
                gui.getjLabel3().setText("Analysis finished");
            });

            gui.getExportButton().addActionListener((ActionEvent e) -> {
                
            });

            gui.setVisible(true);
            System.out.println("GUI geladen.");

        });
    }
}
