package stereolab;
/* 
** Klasse:      StatusLine
** Autor:       Christian Werner <cwerner@informatik.hu-berlin.de>
** Version:     1.0 (vom 22. April 2002)
**
** Beschreibung:
**
** Grafik-Objekt für das Projekt "StereoLab". Status-Leiste am unteren Rand des Haupt-Programmfensters.
*/


import javax.swing.*;
import java.awt.*;

public class StatusLine extends JPanel {

        private JTextField lStatus;
        private JTextField rStatus;
        
        public  StatusLine() {
                super();
                setLayout(new GridLayout(1,2));
                lStatus = new JTextField(" links: Keine Datei geladen.");
                rStatus = new JTextField(" rechts: Keine Datei geladen.");
                lStatus.setEditable(false);
                rStatus.setEditable(false);
                lStatus.setBackground(Color.lightGray);
                rStatus.setBackground(Color.lightGray);
                add(lStatus);
                add(rStatus);
        }
        public void setLeftDescription(String s) {
                lStatus.setText(" links: "+s);
        }
                
        public void setRightDescription(String s) {
                rStatus.setText(" rechts: "+s);
        }
}
