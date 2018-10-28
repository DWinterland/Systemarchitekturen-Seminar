package stereolab;
/* 
** Klasse:      StereoSlider
** Autor:       Christian Werner <cwerner@informatik.hu-berlin.de>
** Version:     1.0 (vom 22. April 2002)
**
** Beschreibung:
**
** Hilfsklassen für das Projekt "StereoLab". Modifiziert einen JSlider zur
** Umgehung eines Bugs in Java 1.3.1
*/

import java.awt.*;
import javax.swing.*;

public class StereoSlider extends JSlider {     // Behebt BUG4220108

        private boolean primerCop = true;
        
        public StereoSlider(int i, int j, int k) {
                super(i,j,k);
        }

        public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (primerCop) {
			updateUI();
			primerCop = false;
		}
        }
}
