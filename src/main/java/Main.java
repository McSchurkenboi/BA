
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
    static File input;

    public static void main(String[] args) {
        initGUI();
        AnnieHandler annie = new AnnieHandler();
        annie.init();
    }

    public static void initGUI() {
        JFileChooser dialog = new JFileChooser();
        //Events

        java.awt.EventQueue.invokeLater(() -> {
            gui = new R2BMainWindow();

            gui.getLoadButton().addActionListener((ActionEvent e) -> {
                dialog.showOpenDialog(gui);
                input = dialog.getSelectedFile();
                if (input != null) {
                    try {
                        gui.getInputReq().read(new FileReader(input), e);
                    } catch (IOException e1) {
                        System.out.println("Fehler beim Lesen der Datei.");
                    }
                    System.out.println("Loaded:" + input.getName());
                }
            });

            gui.getExportButton().addActionListener((ActionEvent e) -> {
                dialog.showSaveDialog(gui);
            });

            gui.setVisible(true);
            System.out.println("GUI geladen.");
        });
    }
}
