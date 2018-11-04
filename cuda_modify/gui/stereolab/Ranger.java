package stereolab;
/* 
** Klasse:      Ranger
** Autor:       Christian Werner <cwerner@informatik.hu-berlin.de>
** Version:     1.0 (vom 22. April 2002)
**
** Beschreibung:
**
** Monitor zum berechnen von Disparitätenmatrizen.
*/

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.io.*;

public class Ranger extends Monitor implements ActionListener, ChangeListener, MouseMotionListener, MouseListener {

        private StereoLab s;
        private int percent;  // Zoom-Faktor in %
        private int shownZoom; // Zoom-Faktor, der zur Berechnung der aktuell sichtbaren Ergebnisdaten verwendet wurde
        private StereoAlgorithm algo, viewAlgo;
        private JPanel contentPanel;
        private TrackPanel panel;
        private JCheckBoxMenuItem mask,hist;
        private JMenuItem CPUAlgos, ProfileSave,ProfileCompare,ProfileViewSave;
        private JDialog CPUDialog;
        private JPanel CPUDialogPanel;
        private JPanel hwswPanel, hwPanel, verfahrenPanel, tauMaxPanel, schwellwertPanel, okPanel;
        private JCheckBox schwellwert,fenster;
        private JComboBox verfahrenBox, fensterBox;
        private JRadioButton cudaDistribButton,swButton, cudaButton;
        private StereoSlider tauMaxSlider,schwellwertSlider;
        private JLabel tauMaxLabel,tauMaxBeschriftung,schwellwertLabel,schwellwertBeschriftung;
        private JButton CPUDialogOK, CPUDialogAbbrechen;
        private File FPGAFile;
        private JPanel meterPanel;
        private JTextField FPGAFileName;
        private JFileChooser FPGADialog,mapFileChooser,profileFileChooser,profileSaveFileChooser;
        private TextFileFilter myTextFilter;
        private ProfileFileFilter myProfileFilter;
        private JRadioButton Profilformat, Textformat;
        private JPanel mapFileChooserPanel, profileSaveFileChooserPanel, northPanel, FPGAPanel,frequenzPanel, dateiPanel;
        private JComboBox TaktBox;
        private RangeProfile normProfile;
        private JLabel profileName;
        private JCheckBox useNormProfile;
        private JButton openNormProfile,openFPGAFile;
        private JButton rechne;
        private TimerProgressBar progress;
        private JPanel statusPanel;
        private JTextArea status;
        private RangeProfile profile;
        private JPanel mainPanel, histogrammPanel, auswertPanel;

        private JTextField punktBoxX, punktBoxY, pixelVersatzBox;
        private JLabel punktLabel,pixelVersatzLabel,berechnungLaeuft;        
        private AdvancedHistogrammPanel histogramm;

        private JMenu aufloesung;
        private ButtonGroup a;
        
        
        public Ranger(StereoLab S) {
                super("Disparitätenberechnung");
                s = S;
                percent = s.getData().getPresetZoom();
                shownZoom = 0;
                rechne = new JButton();
                rechne.setActionCommand("rechne");
                rechne.addActionListener(this);

                progress = new TimerProgressBar(0, StereoToolkit.resize(s.getData().getLeftImage().getHeight(),percent));
                progress.setValueNow(0);
                progress.setStringPainted(true);
                
                statusPanel = new JPanel();
                statusPanel.setBorder(BorderFactory.createTitledBorder("Status der Anzeige"));
                statusPanel.setLayout(new BoxLayout(statusPanel,BoxLayout.Y_AXIS));
                status = new JTextArea(6,47);
                status.setFont(status.getFont().deriveFont(11f));
                status.setText("Es sind noch keine Berechnungsergebnisse\n"+
                               "vorhanden. Bitte wählen Sie zunächst im \n"+
                               "Menüpunkt \"Berechnung\" geeignete Einstellun-\n"+
                               "gen aus. Benutzen Sie dann den Knopf\n"+
                               "\"Berechnung starten\", um die\n"+
                               "Anzeige zu aktualisieren."); 
                statusPanel.add(status);
                
                berechnungLaeuft = new JLabel("Berechnung läuft! Anzeige wird akualisiert...");
                
                ProfileSave = new JMenuItem("Profil speichern...");
                ProfileSave.setActionCommand("save");
                ProfileSave.addActionListener(this);
                ProfileSave.setEnabled(false);
                addDatei(ProfileSave,0);
                addDatei(new JSeparator(),1);

                profileFileChooser=new JFileChooser();
                myProfileFilter = new ProfileFileFilter();
                myTextFilter = new TextFileFilter();
                profileFileChooser.addChoosableFileFilter(myProfileFilter);
                profileFileChooser.addChoosableFileFilter(myTextFilter);
                profileFileChooser.setCurrentDirectory(s.getConfig().getStateFCDir());

                profileSaveFileChooser=new JFileChooser();
                profileSaveFileChooser.addChoosableFileFilter(new ProfileFileFilter());
                profileSaveFileChooser.setCurrentDirectory(s.getConfig().getStateFCDir());
                profileSaveFileChooserPanel = new JPanel();
                profileSaveFileChooserPanel.setLayout(new GridLayout(0,1));
                profileSaveFileChooserPanel.setBorder(BorderFactory.createTitledBorder("Speicherformat auswählen:"));
                Textformat = new JRadioButton("Text (Hexadezimal-Darstellung)");
                Textformat.setActionCommand("TextFormatSelected");
                Textformat.addActionListener(this);
                Profilformat = new JRadioButton("Java Profil-Objekt");
                Profilformat.setActionCommand("ProfilFormatSelected");
                Profilformat.addActionListener(this);
                ButtonGroup SpeicherFormatButtons = new ButtonGroup();
                SpeicherFormatButtons.add(Textformat);
                SpeicherFormatButtons.add(Profilformat);
                profileSaveFileChooserPanel.add(Profilformat);
                profileSaveFileChooserPanel.add(Textformat);
                Profilformat.setSelected(true);
                profileSaveFileChooser.setAccessory(profileSaveFileChooserPanel);
            
                
                
                ProfileViewSave = new JMenuItem("Darstellung des Profils speichern...");
                ProfileViewSave.setActionCommand("viewSave");
                ProfileViewSave.addActionListener(this);
                ProfileViewSave.setEnabled(false);
                addDatei(ProfileViewSave,1);
                
                mapFileChooser = new JFileChooser();
                mapFileChooser.addChoosableFileFilter(new StereoFileFilter());
                mapFileChooser.setCurrentDirectory(s.getConfig().getStateFCDir());

                mapFileChooserPanel = new JPanel();
                mapFileChooserPanel.setLayout(new BorderLayout());
                mapFileChooserPanel.setBorder(BorderFactory.createTitledBorder("Normierung anderem Profil anpassen"));
                northPanel = new JPanel();
                northPanel.setLayout(new BoxLayout(northPanel,BoxLayout.X_AXIS));
                useNormProfile=new JCheckBox("aktiv");
                useNormProfile.setActionCommand("useNormProfile");
                useNormProfile.addActionListener(this);
                useNormProfile.setBorder(BorderFactory.createEmptyBorder(15,0,0,0));
                northPanel.add(Box.createRigidArea(new Dimension(5,0)));                
                northPanel.add(useNormProfile);
                northPanel.add(Box.createHorizontalGlue());
                openNormProfile=new JButton("Profil öffnen...");
                openNormProfile.setActionCommand("openNormProfile");
                openNormProfile.addActionListener(this);
                northPanel.add(Box.createRigidArea(new Dimension(60,0)));
                northPanel.add(openNormProfile);
                northPanel.add(Box.createRigidArea(new Dimension(5,0)));

                mapFileChooserPanel.add(northPanel, BorderLayout.NORTH);
                
                profileName = new JLabel("Es ist noch kein Profil geöffnet.");
                profileName.setVerticalAlignment(JLabel.CENTER);
                profileName.setHorizontalAlignment(JLabel.LEFT);
                profileName.setBorder(BorderFactory.createEmptyBorder(0,5,0,0));

                normProfile=null;
                profileName.setEnabled(false);
                openNormProfile.setEnabled(false);
                
                mapFileChooserPanel.add(profileName, BorderLayout.CENTER);
                mapFileChooser.setAccessory(mapFileChooserPanel);
                
                aufloesung = new JMenu("Auflösung der Eingangsdaten");
                a = new ButtonGroup();
                for (int i=0; i <= 9; i++) {
                        JRadioButtonMenuItem temp=new JRadioButtonMenuItem(String.valueOf(100-i*10)+"%");
                        temp.setActionCommand("zoom");
                        temp.addActionListener(this);
                        a.add(temp);
                        aufloesung.add(temp);
                        if ((100-i*10)==percent) temp.setSelected(true);
                }
                addBerechnung(aufloesung,0);
                addBerechnung(new JSeparator(),1);

                CPUAlgos = new JMenuItem("Algorithmen...");
                CPUAlgos.setActionCommand("cpu");
                CPUAlgos.addActionListener(this);
                addBerechnung(CPUAlgos,2);                

                ProfileCompare = new JMenuItem("Profil vergleichen...");
                ProfileCompare.setActionCommand("compare");
                ProfileCompare.addActionListener(this);
                ProfileCompare.setEnabled(false);
                addBerechnung(new JSeparator(),3);
                addBerechnung(ProfileCompare,4);
                
                CPUDialog = new JDialog(S.getFrame(),"Algorithmenauswahl",true);
                CPUDialog.setResizable(false);
                CPUDialogPanel=new JPanel();
                CPUDialogPanel.setLayout(new GridLayout(0,1));
                verfahrenPanel = new JPanel();
                verfahrenPanel.setLayout(new GridLayout(1,0));
                JPanel p = new JPanel();
                p.setBorder(BorderFactory.createTitledBorder("Berechnung auf"));
               
                
                swButton = new JRadioButton("Host-CPU");
                swButton.setActionCommand("sw");
                swButton.addActionListener(this);
                cudaDistribButton = new JRadioButton("CUDA WS");
                cudaDistribButton.setActionCommand("cudaDis");
                cudaDistribButton.addActionListener(this);
                cudaButton = new JRadioButton("CUDA");
                cudaButton.setActionCommand("cuda");
                cudaButton.addActionListener(this);
                ButtonGroup hwswButtons = new ButtonGroup();
                hwswButtons.add(swButton);
                hwswButtons.add(cudaDistribButton);
                hwswButtons.add(cudaButton);
                swButton.setSelected(true);
                p.setLayout(new BoxLayout(p,BoxLayout.Y_AXIS));
                p.add(Box.createVerticalGlue());
                p.add(swButton);
                p.add(Box.createVerticalGlue());
                p.add(cudaDistribButton);
                p.add(Box.createVerticalGlue());
                p.add(cudaButton);
                p.add(Box.createVerticalGlue());
                verfahrenPanel.add(p);
                p = new JPanel();
                p.setBorder(BorderFactory.createTitledBorder("Rechenverfahren"));
                verfahrenBox=new JComboBox();
                verfahrenBox.addItem("Betrag der Differenzen");
                verfahrenBox.addItem("Quadrat der Differenzen");
                verfahrenBox.addItem("Quadrat der Diff. (normiert)");
                verfahrenBox.addItem("Pearson'scher Korr.-Koeff.");
                verfahrenBox.setActionCommand("verfahren");
                verfahrenBox.setSelectedIndex(0);
                verfahrenBox.addActionListener(this);
                p.add(verfahrenBox);
                verfahrenPanel.add(p);
                p = new JPanel();
                p.setBorder(BorderFactory.createTitledBorder("Fenstergröße (b x h)"));
                fensterBox = new JComboBox();
                fensterBox.addItem("3 x 1");
                fensterBox.addItem("7 x 1");
                fensterBox.addItem("15 x 1");
                fensterBox.addItem("31 x 1");
                fensterBox.addItem("3 x 3");
                fensterBox.addItem("7 x 3");
                fensterBox.addItem("15 x 3");
                fensterBox.addItem("3 x 7");
                fensterBox.addItem("7 x 7");
                fensterBox.addItem("1 x 15");
                fensterBox.addItem("15 x 15");
                fensterBox.addItem("1 x 31");
                fensterBox.setSelectedIndex(8);
                p.add(fensterBox);
                verfahrenPanel.add(p);
                p = new JPanel();
                p.setBorder(BorderFactory.createTitledBorder("Optionen"));
                p.setLayout(new GridLayout(0,1));
                schwellwert = new JCheckBox("Schwellwert aktiv");
                schwellwert.setActionCommand("schwellwert");
                schwellwert.addActionListener(this);
                p.add(schwellwert);
                fenster= new JCheckBox("Fenster verwenden");
                p.add(fenster);
                verfahrenPanel.add(p);
                
                
                CPUDialogPanel.add(verfahrenPanel);
                
                tauMaxPanel    = new JPanel();
                tauMaxPanel.setLayout(new BorderLayout());
                tauMaxPanel.setBorder(BorderFactory.createTitledBorder("maximaler horizontaler Versatz:"));
                tauMaxSlider = new StereoSlider(1,(s.getData().getLeftImage().getWidth()*percent+50)/200,10);
                tauMaxSlider.addChangeListener(this);
                JPanel centerPanel=new JPanel();
                centerPanel.setLayout(new BoxLayout(centerPanel,BoxLayout.X_AXIS));
                centerPanel.add(tauMaxSlider);
                tauMaxPanel.add(centerPanel,BorderLayout.CENTER);
                JPanel eastPanel = new JPanel();
                eastPanel.setLayout(new BoxLayout(eastPanel,BoxLayout.X_AXIS));
                eastPanel.setBorder(BorderFactory.createEmptyBorder(0,5,0,5));
                tauMaxBeschriftung = new JLabel("<html>&#964;<sub>max</sub></html>");
                tauMaxBeschriftung.setPreferredSize(new Dimension(30,tauMaxBeschriftung.getPreferredSize().height));
                eastPanel.add(tauMaxBeschriftung);
                eastPanel.add(new JLabel("<html>=</html>"));
                tauMaxLabel=new JLabel("\u00b1"+Integer.toString(tauMaxSlider.getValue()),JLabel.RIGHT);
                tauMaxLabel.setPreferredSize(new Dimension(40,tauMaxLabel.getPreferredSize().height));
                eastPanel.add(tauMaxLabel);
                tauMaxPanel.add(eastPanel,BorderLayout.EAST);
                CPUDialogPanel.add(tauMaxPanel);

                schwellwertPanel = new JPanel();
                schwellwertPanel.setLayout(new BorderLayout());
                schwellwertPanel.setBorder(BorderFactory.createTitledBorder("Schwellwert:"));
                schwellwertSlider = new StereoSlider(0, 1000, 60);
                schwellwertSlider.addChangeListener(this);
                schwellwertSlider.setAlignmentY(CENTER_ALIGNMENT);

                FPGAPanel = new JPanel();
                FPGAPanel.setBorder(BorderFactory.createTitledBorder("FPGA Konfiguration:"));
                FPGAPanel.setLayout(new BorderLayout());
                openFPGAFile = new JButton("Datei öffnen...");
                openFPGAFile.setActionCommand("openFPGAFile");
                openFPGAFile.addActionListener(this);
                FPGAFileName = new JTextField("Es ist noch keine Datei geöffnet.",40);
                FPGAFileName.setEditable(false);
                p = new JPanel();
                p.add(openFPGAFile);
                p.add(FPGAFileName);
                FPGAPanel.add(p,BorderLayout.WEST);
                TaktBox = new JComboBox();
                for (int i=1;i<=99;i++) TaktBox.addItem(new Integer(i));
                p = new JPanel();
                p.add(new JLabel("Taktfrequenz:"));
                p.add(TaktBox);
                p.add(new JLabel("MHz"));
                FPGAPanel.add(p,BorderLayout.EAST);
                                
                FPGADialog = new JFileChooser();
                FPGADialog.addChoosableFileFilter(new BitFileFilter());
                FPGADialog.setCurrentDirectory(s.getConfig().getStateFCDir());

                JPanel centerPanel2=new JPanel();
                centerPanel2.setLayout(new BoxLayout(centerPanel2,BoxLayout.X_AXIS));
                centerPanel2.add(schwellwertSlider);
                schwellwertPanel.add(centerPanel2,BorderLayout.CENTER);
                JPanel eastPanel2 = new JPanel();
                eastPanel2.setLayout(new BoxLayout(eastPanel2,BoxLayout.X_AXIS));
                eastPanel2.setBorder(BorderFactory.createEmptyBorder(0,5,0,5));
                schwellwertBeschriftung = new JLabel("<html>S</html>");
                schwellwertBeschriftung.setPreferredSize(new Dimension(30,schwellwertBeschriftung.getPreferredSize().height));
                eastPanel2.add(schwellwertBeschriftung);
                eastPanel2.add(new JLabel("<html>=</html>"));
                schwellwertLabel=new JLabel(Integer.toString(schwellwertSlider.getValue()),JLabel.RIGHT);
                schwellwertLabel.setPreferredSize(new Dimension(40,schwellwertLabel.getPreferredSize().height));
                eastPanel2.add(schwellwertLabel);
                schwellwertPanel.add(eastPanel2,BorderLayout.EAST);
                if (schwellwert.isSelected()) CPUDialogPanel.add(schwellwertPanel);                
                okPanel        = new JPanel();
                okPanel.setLayout(new BoxLayout(okPanel,BoxLayout.X_AXIS));
                CPUDialogOK = new JButton("OK");
                CPUDialogOK.setAlignmentX(JComponent.RIGHT_ALIGNMENT);
                CPUDialogOK.setAlignmentY(JComponent.CENTER_ALIGNMENT);
                CPUDialogOK.setActionCommand("cpudialogok");
                CPUDialogOK.addActionListener(this);
                CPUDialogAbbrechen = new JButton("Abbrechen");
                CPUDialogAbbrechen.setAlignmentX(JComponent.RIGHT_ALIGNMENT);
                CPUDialogAbbrechen.setAlignmentY(JComponent.CENTER_ALIGNMENT);
                CPUDialogAbbrechen.setActionCommand("cpudialogabbrechen");
                CPUDialogAbbrechen.addActionListener(this);
                okPanel.add(Box.createHorizontalGlue());
                okPanel.add(CPUDialogOK);
                okPanel.add(Box.createRigidArea(new Dimension(6,0)));
                okPanel.add(CPUDialogAbbrechen);
                okPanel.add(Box.createRigidArea(new Dimension(3,0)));                
                CPUDialogPanel.add(okPanel);
                                
                CPUDialog.getContentPane().add(CPUDialogPanel);
                CPUDialog.pack();
                CPUDialog.setLocationRelativeTo(this);
                
                mask = new JCheckBoxMenuItem("ungültige Daten maskieren", true);
                mask.setActionCommand("mask");
                mask.addActionListener(this);

                hist = new JCheckBoxMenuItem("Histogramm/Legende anzeigen", false);
                hist.setActionCommand("hist");
                hist.addActionListener(this);
                
                addAnsicht(mask,0);
                addAnsicht(hist,1);
                addAnsicht(new JSeparator(),2);
                
                // panal provisorisch auf richtiger Größe initialisieren
                contentPanel=new JPanel();
                contentPanel.setLayout(new BoxLayout(contentPanel,BoxLayout.Y_AXIS));
                panel=new TrackPanel((s.getData().getLeftImage().getHeight()*percent+50)/100, (s.getData().getLeftImage().getWidth()*percent+50)/100);
                panel.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
                panel.addMouseMotionListener(this);
                panel.addMouseListener(this);

                mainPanel = new JPanel();
                
                mainPanel.setLayout(new BoxLayout(mainPanel,BoxLayout.Y_AXIS));


                auswertPanel=new JPanel();
                auswertPanel.setBorder(BorderFactory.createTitledBorder("Auswertung"));
                auswertPanel.setLayout(new GridLayout(2, 2));
                punktLabel = new JLabel("Punkt ( x / y ):");
                pixelVersatzLabel = new JLabel("Versatz (in Pixeln):");
                punktBoxX = new JTextField(4);
                punktBoxX.setEditable(false);
                punktBoxX.setHorizontalAlignment(JTextField.RIGHT);
                punktBoxY = new JTextField(4);
                punktBoxY.setEditable(false);                
                punktBoxY.setHorizontalAlignment(JTextField.RIGHT);
                JPanel punktBoxPanel = new JPanel();
                punktBoxPanel.setLayout(new GridLayout(1, 2));
                punktBoxPanel.add(punktBoxX);
                punktBoxPanel.add(punktBoxY);
                pixelVersatzBox = new JTextField(4);
                pixelVersatzBox.setEditable(false);                
                pixelVersatzBox.setHorizontalAlignment(JTextField.RIGHT);
                auswertAus();
                auswertPanel.add(punktLabel);
                auswertPanel.add(punktBoxPanel);
                auswertPanel.add(pixelVersatzLabel);
                auswertPanel.add(pixelVersatzBox);
                addButton(statusPanel);
                addButton(mainPanel);
                addButton(auswertPanel);
                addButton(rechne);
                
                contentPanel.add(panel);

                histogrammPanel = new JPanel();
                contentPanel.add(histogrammPanel);
                addContent(contentPanel);
                initAlgorithm();
                checkButtonColor();
        }
        
         public void actionPerformed(ActionEvent event) {
                super.actionPerformed(event);
                String cmd = event.getActionCommand();
                if (cmd.equals("hw")) {
                        tauMaxSlider.setEnabled(false);
                        schwellwertSlider.setEnabled(false);
                        schwellwert.setEnabled(false);
                        fenster.setEnabled(false);
                        verfahrenBox.setEnabled(false);
                        fensterBox.setEnabled(false);
                        CPUDialogPanel.add(FPGAPanel,1);
                        CPUDialogPanel.remove(tauMaxPanel);
                        CPUDialog.validate();
                        CPUDialog.pack();
                        CPUDialog.repaint();
                }
                if (cmd.equals("sw")) {
                        tauMaxSlider.setEnabled(true);
                        schwellwertSlider.setEnabled(true);
                        schwellwert.setEnabled(true);
                        fenster.setEnabled(true);
                        verfahrenBox.setEnabled(true);
                        fensterBox.setEnabled(true);
                        CPUDialogPanel.add(tauMaxPanel,1);
                        CPUDialogPanel.remove(FPGAPanel);
                        CPUDialog.validate();
                        CPUDialog.pack();
                        CPUDialog.repaint();
                }
                if (cmd.startsWith("cuda")) {
                    tauMaxSlider.setEnabled(true);
                    schwellwertSlider.setEnabled(true);
                    schwellwert.setEnabled(true);
                    fenster.setEnabled(true);
                    verfahrenBox.setEnabled(false);
                    fensterBox.setEnabled(true);
                    CPUDialogPanel.add(tauMaxPanel,1);
                    CPUDialogPanel.remove(FPGAPanel);
                    CPUDialog.validate();
                    CPUDialog.pack();
                    CPUDialog.repaint();
                }
                
                if (cmd.equals("zoom")) {
                        JRadioButtonMenuItem b = (JRadioButtonMenuItem)event.getSource();
                        String temp = b.getText();
                        percent=Integer.parseInt(temp.substring(0,temp.length()-1));
                        tauMaxSlider.setMaximum((s.getData().getLeftImage().getWidth()*percent+50)/200);
                        checkButtonColor();
                }
                
                if (cmd.equals("openFPGAFile")) {
                        int returnVal = FPGADialog.showOpenDialog(this);
                        if (returnVal == JFileChooser.APPROVE_OPTION) {
                                 FPGAFile = FPGADialog.getSelectedFile();
                                 FPGAFileName.setText(FPGAFile.getName());
                        }
                }
                
                if (cmd.equals("ProfilFormatSelected")) {
                        profileSaveFileChooser.resetChoosableFileFilters();
                        profileSaveFileChooser.addChoosableFileFilter(myTextFilter);
                        profileSaveFileChooser.addChoosableFileFilter(myProfileFilter);
                }
                if (cmd.equals("TextFormatSelected")) {
                        profileSaveFileChooser.resetChoosableFileFilters();
                        profileSaveFileChooser.addChoosableFileFilter(myProfileFilter);
                        profileSaveFileChooser.addChoosableFileFilter(myTextFilter);

                }
                if (cmd.equals("mask")) {
                        if (profile!=null) { 
                                paintData();
                                histogramm = new AdvancedHistogrammPanel(profile.getHistogramm(),mask.isSelected());
                                if (hist.isSelected()) updateHistogramm(histogramm);
                        }
                        
                }

                if (cmd.equals("hist") & (profile!=null)) {
                       if (hist.isSelected()) updateHistogramm(histogramm); else removeHistogramm();
                }
                
                if (cmd.equals("rechne")) {
                        updatePanel();
                }

                if (cmd.equals("verfahren")) {
                        if (verfahrenBox.getSelectedIndex()==3) schwellwertLabel.setText(StereoToolkit.tausendstelString(schwellwertSlider.getValue()));
                        else schwellwertLabel.setText(Integer.toString(schwellwertSlider.getValue())); 
                }
                
                if (cmd.equals("cpu")) {
                        CPUDialog.show();
                }
                
                if(cmd.equals("schwellwert")) {
                        if (schwellwert.isSelected()) CPUDialogPanel.add(schwellwertPanel,2); else CPUDialogPanel.remove(schwellwertPanel);
                        CPUDialog.validate();
                        CPUDialog.pack();
                }
                
                if (cmd.equals("cpudialogabbrechen")) {
                        CPUDialog.hide();
                }

                if (cmd.equals("cpudialogok")) {
                                initAlgorithm();
                                checkButtonColor();
                                CPUDialog.hide();
                }
                
                if (cmd.equals("viewSave")) {
                        File file;
                        int returnVal = mapFileChooser.showSaveDialog(this);
                        if (returnVal == JFileChooser.APPROVE_OPTION) {
                               file = mapFileChooser.getSelectedFile();
                              
                               if (useNormProfile.isSelected() && (normProfile!=null)) {
                                        profile.globalNormalize(normProfile.getHistogramm());
                                        profile.saveNormalized(file);
                                        if (mask.isSelected()) profile.validNormalize(profile.getHistogramm()); // alte Normierung für Anzeige wiederherstellen
                                        else profile.globalNormalize(profile.getHistogramm());
                               } else {
                                        profile.saveNormalized(file);
                               }
                        }
                }
                
                if (cmd.equals("save")) {
                        File file;
                        int returnVal = profileSaveFileChooser.showSaveDialog(this);
                        if (returnVal == JFileChooser.APPROVE_OPTION) {
                               file = profileSaveFileChooser.getSelectedFile();
                               try {
                                        FileOutputStream ostream = new FileOutputStream(file);
                                        
                                        if (Textformat.isSelected()) {
                                                String s;
                                                int[][] temp = profile.getRawProfileData();
                                                int zeilen = temp.length;
                                                int spalten = temp[0].length;
                                                PrintWriter p = new PrintWriter(ostream);
                                                for (int i=0; i<=zeilen-1;i++) {
                                                        for (int j=0; j<=spalten-1;j++) {
                                                                s = Integer.toHexString(temp[i][j])+" ";
                                                                for(int k=0; s.length() < 9;k++) s="0"+s;
                                                                p.print(s);
                                                        }
                                                        p.println();
                                                }
                                                p.flush();
                                        } else {
                                                ObjectOutputStream p = new ObjectOutputStream(ostream);
                                                p.writeObject(profile);
                                                p.flush();
                                        }
                                        ostream.close();
                                } catch (Exception e) {
                                                System.out.println(e.getMessage());
                                }
                        }
                }
                
                if (cmd.equals("useNormProfile")) {
                        if (useNormProfile.isSelected()) {
                                profileName.setEnabled(true);
                                openNormProfile.setEnabled(true);
                        } else {
                                profileName.setEnabled(false);
                                openNormProfile.setEnabled(false);
                        }
                }

                if (cmd.equals("openNormProfile")) {
                        File file;
                        int returnVal = profileFileChooser.showOpenDialog(this);
                        if (returnVal == JFileChooser.APPROVE_OPTION) {
                               file = profileFileChooser.getSelectedFile();
                               profileName.setText("Geöffnet: "+file.getName());                               
                               try {
                                        FileInputStream istream = new FileInputStream(file);
                                        ObjectInputStream p = new ObjectInputStream(istream);
                                        normProfile = (RangeProfile) p.readObject();
                                        istream.close();
                                } catch (Exception e) {
                                        System.out.println("Fehler bei Laden des Profils: "+e.getMessage());
                                }
                        }
                }
                
                if (cmd.equals("compare")) {
                        File file;
                        String text;
                        int returnVal = profileFileChooser.showOpenDialog(this);
                        if (returnVal == JFileChooser.APPROVE_OPTION) {
                                file = profileFileChooser.getSelectedFile();
                                RangeProfile rp=null;
                
                                try {
                                        FileInputStream istream = new FileInputStream(file);
                                        ObjectInputStream p = new ObjectInputStream(istream);
                                        rp = (RangeProfile) p.readObject();
                                        istream.close();
                                } catch (Exception e) {
                                        System.out.println("Fehler bei Laden des Profils: "+e.getMessage());
                                }
                                
                                double d = Double.NaN;
                                if (rp!=null) d = profile.MAECompare(rp,3,13);
                                if (Double.isNaN(d)) text="Fehler beim Vergleich! Stimmen die Dimensionen\n"+
                                                          "des geladenen Profils mit denen des berechneten\n"+
                                                          "Profils überein?";
                                else text="Der Mittelere Absolute Fehler beträgt: "+String.valueOf(d);
                                JOptionPane.showMessageDialog(this, text);
                        }
                }
        }

        public void checkButtonColor() {
                if ((viewAlgo!=null) && (algo.equals(viewAlgo)) && (percent==shownZoom)) {
                                rechne.setText("<html><font face=ARIAL size=-1 color=black>Berechnung starten</font></html>");
                        } else {
                                rechne.setText("<html><font face=ARIAL size=-1 color=green>Berechnung starten</font></html>");
                        }
        }
        
        public void stateChanged(ChangeEvent e) { 
 	    JSlider slider = (JSlider)e.getSource(); 

            if (slider.equals(tauMaxSlider)){
                    tauMaxLabel.setText("\u00b1"+Integer.toString(tauMaxSlider.getValue()));
            }

            if (slider.equals(schwellwertSlider)){
                        if (verfahrenBox.getSelectedIndex()==3) schwellwertLabel.setText(StereoToolkit.tausendstelString(schwellwertSlider.getValue()));
                        else schwellwertLabel.setText(Integer.toString(schwellwertSlider.getValue()));
            }

 	}
        
        public void blockComponents() {
                 setClosable(false);
                 rechne.setEnabled(false);
                 rechne.setText("<html><font face=ARIAL size=-1 color=gray>Berechnung starten</font></html>");
                 mask.setEnabled(false);
                 hist.setEnabled(false);
                 ProfileCompare.setEnabled(false);
                 ProfileSave.setEnabled(false);
                 ProfileViewSave.setEnabled(false);
         }
        
        public void unblockComponents() {
                 setClosable(true);
                 rechne.setEnabled(true);
                 rechne.setText("<html><font face=ARIAL size=-1 color=black>Berechnung starten</font></html>");                 
                 mask.setEnabled(true);
                 hist.setEnabled(true);
                 ProfileCompare.setEnabled(true);
                 ProfileSave.setEnabled(true);
                 ProfileViewSave.setEnabled(true);
        }
        
        private void paintData() {
                if (mask.isSelected()) {
                        profile.validNormalize(profile.getHistogramm());
                        panel.setData(profile.getRGBImageMasked()); 
                } else {
                        profile.globalNormalize(profile.getHistogramm());
                        panel.setData(profile.getRGBImage());
                }
                revalidate();
        }

        private void initAlgorithm() {
                if (cudaDistribButton.isSelected()) {
                	String S = (String) fensterBox.getSelectedItem();
                	algo = new CUDADistribDiff(parseB(S),parseH(S),tauMaxSlider.getValue(),schwellwert.isSelected(),schwellwertSlider.getValue(),fenster.isSelected());                    
                } else if (cudaButton.isSelected()) {
                	String S = (String) fensterBox.getSelectedItem();
                	algo = new CUDADiff(parseB(S),parseH(S),tauMaxSlider.getValue(),schwellwert.isSelected(),schwellwertSlider.getValue(),fenster.isSelected());                    
                } else {
                	String S = (String) fensterBox.getSelectedItem();
                        switch (verfahrenBox.getSelectedIndex()) {
                                case 0 : algo = new StereoDiff(parseB(S),parseH(S),tauMaxSlider.getValue(),schwellwert.isSelected(),schwellwertSlider.getValue(),fenster.isSelected()); break;
                                case 1 : algo = new StereoMSD(parseB(S),parseH(S),tauMaxSlider.getValue(),schwellwert.isSelected(),schwellwertSlider.getValue(),fenster.isSelected()); break;
                                case 2 : algo = new StereoMSDnorm(parseB(S),parseH(S),tauMaxSlider.getValue(),schwellwert.isSelected(),schwellwertSlider.getValue(),fenster.isSelected()); break;
                                case 3 : algo = new StereoCorr(parseB(S),parseH(S),tauMaxSlider.getValue(),schwellwert.isSelected(),schwellwertSlider.getValue(),fenster.isSelected()); break;
                        }
                }
        }
        
        private void updatePanel() {
                isActive=true;
                blockComponents();
                Dimension d = statusPanel.getSize();
                statusPanel.removeAll();
                statusPanel.add(Box.createVerticalGlue());
                statusPanel.add(berechnungLaeuft);
                statusPanel.add(Box.createVerticalGlue());
                statusPanel.add(progress);
                statusPanel.add(Box.createVerticalGlue());
                statusPanel.setPreferredSize(d);
                validate();
                statusPanel.repaint();
                progress.setMaximum(StereoToolkit.resize(s.getData().getLeftImage().getHeight(),percent)-1);
                progress.setString(null);
                progress.setValueNow(0);
                progress.startUpdateTimer(500);
                final SwingWorker worker = new SwingWorker() {

                        public Object construct() {
                                profile=algo.calc(      StereoToolkit.resizeImage(s.getData().getLeftImage().getData(),percent), 
                                                        StereoToolkit.resizeImage(s.getData().getRightImage().getData(),percent),
                                                        progress
                                                  );
                                return profile;
                        }

                        //Runs on the event-dispatching thread.
                        public void finished() {
                                progress.stopUpdateTimer();
                                paintData();
                                histogramm = new AdvancedHistogrammPanel(profile.getHistogramm(),mask.isSelected());
                                if (hist.isSelected()) updateHistogramm(histogramm);
                                progress.setValueNow(0);
                                progress.setString("fertig");
                                unblockComponents();
                                shownZoom=percent;
                                viewAlgo=algo;
                                status.setText(algo.toString()+"\nAuflösung der Daten:      "+String.valueOf(percent)+"%");
                                statusPanel.removeAll();
                                statusPanel.add(status);
                                statusPanel.repaint();
                                statusPanel.setPreferredSize(null);
                                validate();
                                isActive=false;
                        }
                };
                worker.start();  //required for SwingWorker 3
        }

        public void mouseMoved(MouseEvent e) {

                if ((profile!=null) && (!isActive)){
                        punktBoxX.setBackground(Color.white);
                        punktBoxY.setBackground(Color.white);                      
                        pixelVersatzBox.setBackground(Color.white);                
                        if (profile.getValidData()[e.getY()][e.getX()]) pixelVersatzBox.setForeground(Color.black); else pixelVersatzBox.setForeground(Color.red);
                        punktBoxX.setText(String.valueOf(e.getX()));
                        punktBoxY.setText(String.valueOf(e.getY()));
                        pixelVersatzBox.setText(String.valueOf(profile.getRawProfileData()[e.getY()][e.getX()]));
                }
        }
        
        public void mouseDragged(MouseEvent e) {}
        public void mousePressed(MouseEvent e) {}
        public void mouseClicked(MouseEvent e) {}
        public void mouseReleased(MouseEvent e) {}

        public void mouseEntered(MouseEvent e) {
                auswertEin();
        }

        public void mouseExited(MouseEvent e) {
                auswertAus();
        }
        
        public void auswertEin() {
        }
        
        public void auswertAus() {
                punktBoxX.setText("");
                punktBoxY.setText("");                
                punktBoxX.setBackground(Color.lightGray);
                punktBoxY.setBackground(Color.lightGray);
                pixelVersatzBox.setText("");
                pixelVersatzBox.setBackground(Color.lightGray);
        }
        
        public void doDefaultCloseAction() {
                super.doDefaultCloseAction();           // dürft gar nichts tun...
                s.getMonitorList().closeRanger(this);
        }
        private int parseB(String S) {
                return(Integer.parseInt(S.substring(0,S.indexOf(" x "))));
        }

        private int parseH(String S) {
                return(Integer.parseInt(S.substring(S.indexOf(" x ")+3,S.length())));
        }

}
