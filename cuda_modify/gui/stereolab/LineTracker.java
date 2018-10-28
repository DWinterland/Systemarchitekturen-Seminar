package stereolab;
/* 
** Klasse:      LineTracker
** Autor:       Christian Werner <cwerner@informatik.hu-berlin.de>
** Version:     1.0 (vom 22. April 2002)
**
** Beschreibung:
**
** Hilfsklasse für das Projekt "StereoLab".
** Implementiert den Vorschau-Monitor für Stereobilder. Enthält inzwischen
** ungenutzte Zusatzfunktionen.
*/

import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;
import javax.swing.*;
import java.awt.image.*;

public class LineTracker extends Monitor implements ActionListener {

        private StereoLab s;
        private int percent;  // Zoom-Faktor in %
        private JPanel panel;
        private TrackPanel lPanel, rPanel;
        private JMenu aufloesung;
        private ButtonGroup a;
        
        private byte[][] leftData;        // gezoomte Bilddaten
        private byte[][] rightData;
        
        

        public LineTracker(StereoLab S) {
                super("Bild Vorschau");
                s = S;
                percent = s.getData().getPresetZoom();
                
                panel=new JPanel();

                leftData=StereoToolkit.resizeImage(s.getData().getLeftImage().getData(),percent);
                rightData=StereoToolkit.resizeImage(s.getData().getRightImage().getData(),percent);
                
                lPanel=new TrackPanel(leftData);
                rPanel=new TrackPanel(rightData);

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
                
                panel.add(lPanel);
                panel.add(rPanel);
                addContent(panel);
        }

       
       public void actionPerformed(ActionEvent event) {
                super.actionPerformed(event);
                String cmd = event.getActionCommand();
                if (cmd.equals("zoom")) {
                        JRadioButtonMenuItem b = (JRadioButtonMenuItem)event.getSource();
                        String temp = b.getText();
                        percent=Integer.parseInt(temp.substring(0,temp.length()-1));
                        leftData=StereoToolkit.resizeImage(s.getData().getLeftImage().getData(),percent);
                        rightData=StereoToolkit.resizeImage(s.getData().getRightImage().getData(),percent);
                        lPanel.setData(leftData);
                        rPanel.setData(rightData);
                        validate();
                }        
        }
        public void doDefaultCloseAction() {
                super.doDefaultCloseAction();           // dürft gar nichts tun...
                s.getMonitorList().closeLT(this);
        }
}
