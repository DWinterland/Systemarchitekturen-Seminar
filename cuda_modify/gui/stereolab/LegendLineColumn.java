package stereolab;
/* 
** Klasse:      LegendLineColumn
** Autor:       Christian Werner <cwerner@informatik.hu-berlin.de>
** Version:     1.0 (vom 22. April 2002)
**
** Beschreibung:
**
** Hilfsklasse für das Klasse "AdvancecdHistogrammPanel".
** Eine Spezielle Form einer Line2D.Double: Sie hat auch eine Farbe.
*/

import java.awt.*;
import java.awt.geom.*;


public class LegendLineColumn extends Line2D.Double {
        
        private Color farbe;
        
        public LegendLineColumn(double x1, double y1, double x2, double y2, Color c) {
                super(x1,y1,x2,y2);
                farbe=c;
        }
        
        public Color getColor() {
                return(farbe);
        }
}
