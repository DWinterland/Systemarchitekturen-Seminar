package stereolab;
/* 
** Klasse:      StereoAlgorithm
** Autor:       Christian Werner <cwerner@informatik.hu-berlin.de>
** Version:     1.0 (vom 22. April 2002)
**
** Beschreibung:
**
** Zentrale Klasse für das Projekt "StereoLab". Diese abstakte Klasse definiert die Methoden,
** die ein Algorithmus zur Disparitätenberechnung implementieren muß.
*/

import javax.swing.*;

public abstract class StereoAlgorithm {

        protected int s;  // Schwellwert
        protected boolean useS,useF; // Schwellwert/Fensterfunktion zur Berechnung benutzen?
        protected int h;  // Fensterhöhe
        protected int b;  // Fensterbreite
        protected int tauMax;

        public StereoAlgorithm(int B, int H, int TauMax, boolean U, int S, boolean F) {
                b = B;
                h = H;
                tauMax = TauMax;
                useS = U;
                useF = F;
                s = S;
        }
        
        public boolean equals(StereoAlgorithm sa) {
                return((s==sa.s)&&(useS==sa.useS)&&(h==sa.h)&&(b==sa.b)&&(tauMax==sa.tauMax)&&(getClass().equals(sa.getClass())));
        }
        
        public abstract RangeProfile calc(byte[][] lb,byte[][] rb,TimerProgressBar p);
}
