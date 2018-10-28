package stereolab;
/* 
** Klasse:      Differencer
** Autor:       Christian Werner <cwerner@informatik.hu-berlin.de>
** Version:     1.0 (vom 22. April 2002)
**
** Beschreibung:
**
** Monitorklasse für das Projekt "StereoLab". Implementiert einen Differencer-Monitor, der
** aus der Pull-Down-Menüleiste des Hauptfensters auszuwählen ist.
*/

import java.awt.event.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;

public class Differencer extends Monitor implements ActionListener, ChangeListener {

        private StereoLab s;
        private int percent;  // Zoom-Faktor in %
        private TrackPanel panel;
        private StereoSlider tauSlider, deltaSlider;
        private JLabel tauLabel, deltaLabel, tauBeschriftung, deltaBeschriftung;
        private JPanel tauPanel, deltaPanel, mainPanel;
        private JMenu aufloesung;
        private JCheckBoxMenuItem highlight;
        private ButtonGroup a;
        private DifferenceImage image;
        
        public Differencer(StereoLab S) {
                super("Bilddifferenz-Monitor");
                s = S;
                percent = s.getData().getPresetZoom();
                
                image = new DifferenceImage(StereoToolkit.resizeImage(s.getData().getLeftImage().getData(),percent) , 
                                            StereoToolkit.resizeImage(s.getData().getRightImage().getData(),percent));
                
                mainPanel = new JPanel();
                tauPanel = new JPanel();
                deltaPanel = new JPanel();

                mainPanel.setLayout(new BoxLayout(mainPanel,BoxLayout.Y_AXIS));
                tauPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
                tauPanel.setAlignmentX(LEFT_ALIGNMENT);                
                deltaPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
                deltaPanel.setAlignmentX(LEFT_ALIGNMENT);
                
                mainPanel.add(tauPanel);
                
                
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
                
                highlight = new JCheckBoxMenuItem("Nullstellen hervorheben",false);
                highlight.setActionCommand("highlight");
                highlight.addActionListener(this);
                addAnsicht(highlight,0);
                addAnsicht(new JSeparator(),1);
                
                tauSlider = new StereoSlider(- s.getData().getLeftImage().getWidth(), s.getData().getLeftImage().getWidth(), 0);
                tauSlider.addChangeListener(this);
                tauBeschriftung = new JLabel("<html>&#964;</html>");
                tauBeschriftung.setPreferredSize(new Dimension(30,tauBeschriftung.getPreferredSize().height));
                tauLabel=new JLabel(Integer.toString(tauSlider.getValue()));
                tauLabel.setPreferredSize(new Dimension(40,tauLabel.getPreferredSize().height));
                tauLabel.setHorizontalAlignment(JLabel.RIGHT);
                
                deltaSlider = new StereoSlider(0,5,0);
                deltaSlider.setMajorTickSpacing(1);
                deltaSlider.setSnapToTicks(true);
                deltaSlider.addChangeListener(this);

                deltaBeschriftung=new JLabel("<html>&#916;<sub>max</sub></html>");
                deltaBeschriftung.setPreferredSize(new Dimension(30,deltaBeschriftung.getPreferredSize().height));
                deltaLabel=new JLabel(Integer.toString(deltaSlider.getValue()));
                deltaLabel.setPreferredSize(new Dimension(40,deltaLabel.getPreferredSize().height));
                deltaLabel.setHorizontalAlignment(JLabel.RIGHT);
                
                if (highlight.isSelected()) panel=new TrackPanel(image.getHighlightedDifferencePlotAt(tauSlider.getValue(),deltaSlider.getValue()));
                else panel=new TrackPanel(image.getDifferencePlotAt(tauSlider.getValue(),deltaSlider.getValue()));

                tauPanel.add(tauSlider);
                tauPanel.add(tauBeschriftung);
                tauPanel.add(new JLabel("<html>=</html>"));
                tauPanel.add(tauLabel);
                
                deltaPanel.add(deltaSlider);
                deltaPanel.add(deltaBeschriftung);
                deltaPanel.add(new JLabel("<html>=</html>"));
                deltaPanel.add(deltaLabel);
                
                addContent(panel);
                addButton(mainPanel);
        }
        
        public void actionPerformed(ActionEvent event) {
                super.actionPerformed(event);
                String cmd = event.getActionCommand();
                if (cmd.equals("zoom")) {
                        JRadioButtonMenuItem b = (JRadioButtonMenuItem)event.getSource();
                        String temp = b.getText();
                        percent=Integer.parseInt(temp.substring(0,temp.length()-1));
                        image=new DifferenceImage(StereoToolkit.resizeImage(s.getData().getLeftImage().getData(),percent) , 
                                                  StereoToolkit.resizeImage(s.getData().getRightImage().getData(),percent));
                        updateData();
                        validate();
                }
                
                if (cmd.equals("highlight")) {
                        if (highlight.isSelected()) mainPanel.add(deltaPanel); else mainPanel.remove(deltaPanel);
                        updateData();
                        validate();
                }
                
        }
        
        public void stateChanged(ChangeEvent e) { 
 	    JSlider slider = (JSlider)e.getSource(); 
            if (slider.equals(tauSlider)){
                    updateData();
                    tauLabel.setText(Integer.toString(tauSlider.getValue()));
            }
            if (slider.equals(deltaSlider)){
                    updateData();
                    deltaLabel.setText(Integer.toString(deltaSlider.getValue()));
            }
 	}
        private void updateData() {
                if (highlight.isSelected()) panel.setData(image.getHighlightedDifferencePlotAt(tauSlider.getValue(),deltaSlider.getValue()));
                else panel.setData(image.getDifferencePlotAt(tauSlider.getValue(),deltaSlider.getValue()));
        }
        
        public void doDefaultCloseAction() {
                super.doDefaultCloseAction();           // dürft gar nichts tun...
                s.getMonitorList().closeDifferencer(this);
        }
}
