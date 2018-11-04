package stereolab;
/* 
** Klasse:      StringShape
** Autor:       Christian Werner <cwerner@informatik.hu-berlin.de>
** Version:     1.0 (vom 22. April 2002)
**
** Beschreibung:
**
** Hilfsklasse für "AdvancedHistogrammPanel". Ein String-Objekt mit Positionsangabe.
*/


public class StringShape {
        public String string;
        public int xPos;
        public int yPos;
        
        public StringShape(String S, int X, int Y) {
                string = S;
                xPos = X;
                yPos = Y;
        }
}
