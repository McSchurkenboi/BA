
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import javax.swing.JFileChooser;
import java.net.Authenticator;
import java.net.URL;
import java.net.URLConnection;

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
    static File input, output;

    public static void main(String[] args) throws IOException {
        
        //initGUI();
        
        System.getProperties().put("http.proxyHost", "proxy1.hella.com");
        System.getProperties().put("http.proxyPort", "3128");
        System.getProperties().put("http.proxyUser", "rittfe1");
        System.getProperties().put("http.proxyPassword", "MacSchnurke_1");
        System.setProperty("http.nonProxyHosts", "localhost|127.0.0.1");

        final String authUser = "rittfe1";
        final String authPassword = "MacSchnurke_1";
        Authenticator.setDefault(
                new Authenticator() {
            @Override
            public PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(
                        authUser, authPassword.toCharArray());
            }
        }
        );
        System.setProperty("java.net.useSystemProxies", "true");
        System.setProperty("http.proxyHost", "proxy1.hella.com");
        System.setProperty("http.proxyPort", "3128");
        System.setProperty("http.proxyUser", authUser);
        System.setProperty("http.proxyPassword", authPassword);

        Authenticator.setDefault(new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {

                return new PasswordAuthentication(authUser, authPassword.toCharArray());
            }
        });
/*        URL url = new URL("http://www.google.com/");
        URLConnection con = url.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(
                con.getInputStream()));

// Read it ...
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            System.out.println(inputLine);
        }

        in.close();
      */
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
