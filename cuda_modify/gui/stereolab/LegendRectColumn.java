package stereolab;
/* 
** Klasse:      LegendRectColumn
** Autor:       Christian Werner <cwerner@informatik.hu-berlin.de>
** Version:     1.0 (vom 22. April 2002)
**
** Beschreibung:
**
** Hilfsklasse für das Klasse "AdvancecdHistogrammPanel".
** Eine Spezielle Form eines Rectangle2D.Double: Es hat auch eine Farbe.
*/

import java.awt.*;
import java.awt.geom.*;


public class LegendRectColumn extends Rectangle2D.Double {
        
        private Color farbe;
        
        public LegendRectColumn(double x, double y, double w, double h, Color c) {
                super(x,y,w,h);
                farbe=c;
        }
        
        public Color getColor() {
                return(farbe);
        }
}
