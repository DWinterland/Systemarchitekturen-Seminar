package stereolab;
/* 
** Klasse:      TimerProgressBar
** Autor:       Christian Werner <cwerner@informatik.hu-berlin.de>
** Version:     1.0 (vom 22. April 2002)
**
** Beschreibung:
**
** Hilfslasse für Ranger. Implementiert eine ProgressBar, die sich in bestimmten Zeit-
** abständen selbständig aktualisiert.
*/

import java.awt.event.*;
import java.awt.*;
import javax.swing.*;

public class TimerProgressBar extends JProgressBar implements ActionListener {
        
        private Timer t;
        private int v=0;
        
        public TimerProgressBar(int i, int j) {
                super(i,j);
        }
        
        public void setValue(int V) {
                v=V;
        }
        
        public void setValueNow(int V) {
                v=V;
                super.setValue(V);
        }
        
        public void startUpdateTimer(int ms) {
                t=new Timer(ms,this);
                t.start();
        }
        public void stopUpdateTimer() {
                t.stop();
        }
        
        public void actionPerformed(ActionEvent event) {
                super.setValue(v);
        }
}
