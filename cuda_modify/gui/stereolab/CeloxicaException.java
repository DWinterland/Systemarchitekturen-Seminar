package stereolab;
/* 
** Klasse:      CeloxicaException
** Autor:       Christian Werner <cwerner@informatik.hu-berlin.de>
** Version:     1.0 (vom 2. September 2001)
**
** Beschreibung:
**
** Exception-Klasse zur Kapselung der Celoxica PP1000 Fehlercodes.
** Die genaue Fehlerbeschreibung erhält man durch Aufruf der Methode getMessage().
*/

public class CeloxicaException extends Exception {
        
        public CeloxicaException() {
                super();
        }
 
        public CeloxicaException(String s) {
                super(s);
        }
}
