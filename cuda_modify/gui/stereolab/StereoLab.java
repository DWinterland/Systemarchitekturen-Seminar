package stereolab;
/* 
** Klasse:      StereoLab
** Autor:       Christian Werner <cwerner@informatik.hu-berlin.de>
** Version:     1.0 (vom 22. April 2002)
**
** Beschreibung:
**
** Hauptklasse für das Projekt "StereoLab". Diese Klasse enthält die main-Methode und
** initialisiert das GUI.
*/

import javax.swing.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;
import java.awt.*;
import java.awt.image.*;

public class StereoLab extends JPanel implements ActionListener, ItemListener {

        /*
        Diese beinhaltet alle GUI-Komponenten mit Ausnahme der "Internal Frames", die jeweils eigene
        Klassen haben. Außerdem implementiert diese Klasse alle Event-Listener und steuert die Kommunikation
        zwischen den einzelnen Objekt-Instanzen.
        */

        final static String VERSION = "1.0";
        
        static JFrame frame;                            // Frame für Applikation
        static StereoLab panel;                         // Panel auf Frame;
        static JMenuBar menuBar;

        StereoConfig config;                            // Status der GUI
                                                        // Dient allein zum Abspeichern (!) des GUI Status

        DataSet data;                                   // Datenobject. Verwaltet Informationen zu den
                                                        // Quelldaten
                                                        
        MonitorList monitors;                           // Verwaltet alle sichtbaren JInternalFrames
        
        StatusLine statusLine;                          // eine Status Zeile;
        
        
        
        JMenu datei, monitor, ask;                      // Menubar mit allem was dazugehört...
        JMenuItem oeffnen, beenden;
        JMenuItem linetracker,differencer,ranger;
        JMenuItem info;
       
        JFileChooser fc;                                // FileChooser
        JPanel acPanel;                                 // Accessory f. FC (Zeilen, Spalten, links ,rechts)

        JPanel zeilenSpaltenPanel;
        JLabel zeilenLabel, spaltenLabel;
        JNumberField zeilenBox, spaltenBox;

        JPanel lrPanel;
        JRadioButton lb, rb;
        ButtonGroup lrButtons;
        
        JDesktopPane desk;                              // Raum für JInternalFrames...
        
        public StereoLab() {
                config = StereoConfig.load();                         // Konfiguration öffnen
                                                                      // Falls File nicht da oder
                                                                      // Lesefehler gibt's Default-Config

                data = new DataSet(this);
                
                statusLine = new StatusLine();
                
                fc = new JFileChooser();                              // FileChooser erzeugen
                fc.addChoosableFileFilter(new StereoFileFilter());    // eigener FileFilter f. RAW-Files

                fc.setCurrentDirectory(config.getStateFCDir());
                
                acPanel = new JPanel();                               // das AccessoryPanel
                acPanel.setLayout(new BorderLayout());

                lrPanel = new JPanel();                               // Panel für link/rechts Auswahl

                lb = new JRadioButton("links",true);
                lb.setActionCommand("l");
                rb = new JRadioButton("rechts",false);
                rb.setActionCommand("r");
                lrButtons = new ButtonGroup();
                lrButtons.add(lb);
                lrButtons.add(rb);
                lrPanel.add(lb);
                lrPanel.add(rb);
                lrPanel.setBorder(BorderFactory.createTitledBorder("Lade Datensatz"));
                acPanel.add(lrPanel,BorderLayout.NORTH);
                zeilenSpaltenPanel = new JPanel();                    // Panel für Zeilen-, Spaltenzahl  
                zeilenSpaltenPanel.setBorder(BorderFactory.createTitledBorder("Importformat"));
                zeilenSpaltenPanel.setLayout(new GridLayout(2, 2));
                zeilenLabel = new JLabel("Zeilen:");
                spaltenLabel = new JLabel("Spalten:");
                zeilenBox = new JNumberField(4);
                zeilenBox.setText(config.getStateZeilenBox());
                spaltenBox = new JNumberField(4);
                spaltenBox.setText(config.getStateSpaltenBox());
                zeilenSpaltenPanel.add(spaltenLabel);
                zeilenSpaltenPanel.add(spaltenBox);
                zeilenSpaltenPanel.add(zeilenLabel);
                zeilenSpaltenPanel.add(zeilenBox);
                acPanel.add(zeilenSpaltenPanel,BorderLayout.SOUTH);
                fc.setAccessory(acPanel);
                Dimension d = fc.getPreferredSize();
                fc.setPreferredSize(new Dimension(d.width,d.height+12)); // Behebt Bug bei Win L&F
                
                desk = new JDesktopPane();                               // Desktop
                setLayout(new BorderLayout());
                add(desk,BorderLayout.CENTER);
                add(statusLine,BorderLayout.SOUTH);
                
                monitors = new MonitorList(this, desk);
                
                if (config.getLeftImage() != null) {
	                File leftFile = new File(config.getLeftImage());
	                if (leftFile.length()==zeilenBox.getIntValue()*spaltenBox.getIntValue())
	                	data.setLeftImage(leftFile, zeilenBox.getIntValue(), spaltenBox.getIntValue());
                }
                if (config.getRightImage() != null) {
	                File rightFile = new File(config.getRightImage());
	                if (rightFile.length()==zeilenBox.getIntValue()*spaltenBox.getIntValue())
	                	data.setRightImage(rightFile, zeilenBox.getIntValue(),spaltenBox.getIntValue());
                }

        }
        
        public static void main(String args[]) {
                                try {
                                        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                                } catch (Exception e) {}

                frame = new JFrame("StereoLab "+VERSION);
                frame.addWindowListener(new WindowAdapter() {
                                                                public void windowClosing(WindowEvent e) {
                                                                        panel.exit();
                                                                }
                });

                panel = new StereoLab();
                frame.getContentPane().add("Center", panel);
                menuBar = new JMenuBar();
                panel.makeMenuBar();
                frame.setJMenuBar(menuBar);
                ImageIcon i = new ImageIcon(StereoLab.class.getResource("sub_adv.gif"));
                frame.setIconImage(i.getImage());
                frame.setSize(1024,768);
//              frame.pack();
                if (args.length==4) {
                        int spalten=Integer.parseInt(args[0]);
                        int zeilen=Integer.parseInt(args[1]);
                        panel.getData().setLeftImage(new File(args[2]),zeilen,spalten);
                        panel.getData().setRightImage(new File(args[3]),zeilen,spalten);
                }
                        
                frame.setVisible(true);
        }



        public void actionPerformed(ActionEvent event) { // wird bei Action-Events aufgerufen.

                String cmd = event.getActionCommand();

                if (cmd.equals("openButton")) {
                        if (monitors.activeMonitors()>0) showWaitForFinishDialog();
                        else {
                                if (monitors.monitorsOpen()>0) {
                                        if (showMonitorCloseDialog()==0) {
                                                monitors.closeAll();
                                                showFileDialog();
                                        }
                                } else showFileDialog();
                        } 
                }

                if (cmd.equals("exit")) {
                        exit();
                }

                if (cmd.equals("linetracker")) {
                        if (ImageDimEqual())monitors.createLT(); else showImageDimMessage();
                }
                
                if (cmd.equals("differencer")) {
                        if (ImageDimEqual())monitors.createDifferencer(); else showImageDimMessage();
                }

                if (cmd.equals("ranger")) {
                        if (ImageDimEqual())monitors.createRanger(); else showImageDimMessage();
                }
                
                if (cmd.equals("info")) {
                        showInfoDialog();
                }
        }

        public void itemStateChanged(ItemEvent event)  {

                boolean state;

                JMenuItem source = (JMenuItem)(event.getSource());
                
                if (event.getStateChange() == ItemEvent.SELECTED) state=true; else state=false;
                String s = source.getText();

                // Im Moment unbenutzt
        }
        
        public void showFileSizeMessage(File file){
                JOptionPane.showMessageDialog(frame,
                "Sie haben versucht, die Datei "+file.getName()+" zu öffnen.\n"+
                "Diese Datei ist "+file.length()+ " Bytes groß.\n"+
                "Diese Dateigröße paßt jedoch nicht zu den von Ihnen gemachten Angaben\n"+
                "bezüglich der Zeilen- und Spaltenzahl des zu öffnenden Bildes.\n\n"+
                "Bitte korriegieren Sie Ihre Angaben zum Importformat oder öffnen Sie eine\n"+
                "andere Datei!","Importformatangaben fehlerhaft", JOptionPane.ERROR_MESSAGE);
        }

        public void showInfoDialog(){
                    ImageIcon i = new ImageIcon(StereoLab.class.getResource("sub_adv.gif"));
                    JOptionPane.showMessageDialog(frame,
                    "StereoLab ist eine modular aufgebaute Software zur Unter-\n"+
                    "suchung unterschiedlicher Verfahren zur Disparitätenberechnung.\n\n"+

                    "Es wird eine Schnittstelle zur Celoxica RC1000PP FPGA-Karte\n"+
                    "realisiert, die es ermöglicht, die Disparitätenberechnung\n"+
                    "auch auf einem FPGA ablaufen zu lassem.\n\n\n"+
                    "Version: "+VERSION+"\n"+
                    "Autor:   Christian Werner (2001/2002)",
                    "Info zu StereoLab Version "+VERSION,
                    JOptionPane.PLAIN_MESSAGE,i);

        }

        public void showWaitForFinishDialog(){
                    JOptionPane.showMessageDialog(frame,
                    "Sie können keine neuen Datensätze laden, bevor alle laufenden\n"+
                    "Berechnungen abgeschlossen sind.\n\n"+
                    "Bitte warten Sie auf den Abschluss aller Berechnungen, schließen\n"+
                    "Sie alle Monitore und versuchen Sie es dann erneut!",
                    "Berechnungen noch nicht abgeschlossen!",
                    JOptionPane.PLAIN_MESSAGE);
        }
        
        public int showMonitorCloseDialog() {
                Object[] options = {   "Ja, Monitore schließen",
                                        "Nein, abbrechen"};
                int n = JOptionPane.showOptionDialog(frame,
                "Um einen neue Datensätze läden zu können, müssen zuvor alle\n"+
                "Monitore geschlossen werden!\n\n"+
                "Wollen Sie fortfahren?\n",
                "Monitore schließen?",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[1]);
                return(n);
        }
        
        public void showFileDialog(){
                 int returnVal = fc.showOpenDialog(StereoLab.this);
                 if (returnVal == JFileChooser.APPROVE_OPTION) {
                         File file = fc.getSelectedFile();
                         if (file.length()!=zeilenBox.getIntValue()*spaltenBox.getIntValue()) {
                                showFileSizeMessage(file);
                                showFileDialog();
                         } else {
                                if (lb.isSelected()) {
                                	data.setLeftImage(file, zeilenBox.getIntValue(), spaltenBox.getIntValue());
                                	config.setLeftImage(file.getAbsolutePath());
                                }
                                else if (rb.isSelected()) {
                                	data.setRightImage(file, zeilenBox.getIntValue(),spaltenBox.getIntValue());
                                	config.setRightImage(file.getAbsolutePath());
                                }
                                
                                //System.out.println("Error: kein Links-Rechts-Auswahl getroffen");
                        }
                 } else {
                 }
        }

        public void makeMenuBar() {
                datei = new JMenu("Datei");
                oeffnen = new JMenuItem("Öffnen...");
                oeffnen.setActionCommand("openButton");
                oeffnen.addActionListener(this);
                beenden = new JMenuItem("Beenden");
                beenden.setActionCommand("exit");
                beenden.addActionListener(this);

                monitor = new JMenu("Monitor");
                linetracker = new JMenuItem("Bild Vorschau");
                linetracker.setActionCommand("linetracker");
                linetracker.addActionListener(this);
                
                differencer = new JMenuItem("Bilddifferenz-Monitor");
                differencer.setActionCommand("differencer");
                differencer.addActionListener(this);
                
                ranger = new JMenuItem("Disparitätenberechnung");
                ranger.setActionCommand("ranger");
                ranger.addActionListener(this);
                
                ask = new JMenu("?");
                info = new JMenuItem("Info");
                info.setActionCommand("info");
                info.addActionListener(this);
                
                datei.add(oeffnen);
                datei.add(beenden);
                
                monitor.add(linetracker);
                monitor.add(differencer);
                monitor.add(ranger);
                
                ask.add(info);
                
                menuBar.add(datei);
                menuBar.add(monitor);
                menuBar.add(ask);
                
        }
        
        public void save() {            // config aktualisieren und abspeichern
                config.setStateZeilenBox(zeilenBox.getText());
                config.setStateSpaltenBox(spaltenBox.getText());
                config.setStateFCDir(fc.getCurrentDirectory());
                StereoConfig.save(config);        
        }
        
        public void exit() {
                save();
                System.exit(0);
        }

        public DataSet getData() {
                return(data);
        }
        
        public StereoConfig getConfig() {
                return(config);
        }
        
        public MonitorList getMonitorList() {
                return(monitors);
        }

        public JDesktopPane getDesk() {
                return(desk);
        }
        
        public StatusLine getStatusLine() {
                return(statusLine);
        }
        
        public JFrame getFrame() {
                return(frame);
        }

        public boolean ImageDimEqual() {
                if ((data.getLeftImage() != null) & (data.getRightImage() != null))
                if ((data.getLeftImage().getWidth() == data.getRightImage().getWidth()) &
                    (data.getLeftImage().getHeight() == data.getRightImage().getHeight()))
                    return(true);
                return(false);
        }
        
        public void showImageDimMessage() {
                JOptionPane.showMessageDialog(frame,
                "Sie müssen zunächst zwei Dateien mit gleicher Zeilen- und Spaltenzahl\n"+
                "laden, bevor Sie einen Monitor öffnen können.\n","Fehler in Quelldaten",
                JOptionPane.ERROR_MESSAGE);
        }
        
} // End of KorrDemo.java




