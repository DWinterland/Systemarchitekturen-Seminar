package stereolab;
/* 
** Klasse:      Celoxica
** Autor:       Christian Werner <cwerner@informatik.hu-berlin.de>
** Version:     0.5 (vom 2. September 2001)
**
** Beschreibung:
**
** Diese Klasse stellt Java-Programmen Zugriff auf Karten vom Typ Celoxica RC1000PP zur Verfügung.
** Sie funktioniert wahrscheinlich auch mit anderen Celoxica-Karten, was ich aber nicht getestet habe.
**
** Um diese Klasse benutzen zu können, müssen folgende Voraussetzungen erfüllt sein:
**
** - Rechner mit eingebauter Celoxica-Karte
** - Windows-Betriebbssystem mit installierten Celoxica-Treibern
** - Celoxica.dll im Classpath
**
** Die Celoxica-Karte stellt verschiedene Ressourcen zur Verfügung, die mit Hilfe dieser Klasse
** angesprochen werden können:
**
** 1. GPI und GPO
**
** Dies sind zwei 1-Bit-Signale. GPI wird vom FPGA gesetzt und vom Host-Programm gelesen.
** Bei GPO ist dies genau umgekehrt. Die Verwendung dieser Signale hängt vom Design ab, mit dem
** der FPGA konfiguriert ist.
**
** 2. Status und Control
**
** Dies sind zwei 8-bit-Signale. Status wird vom FPGA gesetzt und vom Host-Programm gelesen.
** Bei Control ist dies genau umgekehrt. Diese Signale bilden eine Schnittstelle, um Nachrichten 
** zwischen Host-Programm und FPGA auszutauschen. Die genaue Verwenung hängt vom Design ab, mit welchem
** der FPGA konfiguriert ist.
**
** 3. RAM-Bänke:
**
** Die Celoxica-Karte stellt dem Benutzer vier RAM-Bänke (Bank 0 bis 3) zur Verfügung. Jede Bank ist
** 2 Megabyte groß. Um Zugriffskonflikt zu vermeiden ist auf der Celoxica-Karte eine Kontroll-Logik
** realisiert, die sicherstellt, daß entweder der FPGA oder das Host-Programm eine Bank verwenden.
** Bevor eine Bank also benutzt werden kann (lesend oder schreibend), muß der Zugriff auf diese Bank
** angefordert weden (->requestMemoryBank()). Nach der Benutzung der Bank, sollte diese wieder
** freigegene werden (->releaseMemoryBank()). Die Adressierung des Speichers der Ram-Bänke wird
** von der jeweiligen Zugriffsmethoden dieser Klasse separat geregelt und dort auch erklärt.
**
** 4. Taktgeneratoren:
**
** MCLOCK und VCLOCK heißen die beiden Taktgeneratoren der Celoxica-Karte. Sie lassen sich im Bereich
** von 400 kHz bis 100 MHz programmieren.
*/



public class Celoxica {

        public static native int openFirstCard() throws CeloxicaException;
                /*
                ** Liefert die ID der ersten am PCI gefundenen Celoxica-Karte und registriert
                ** die Anwendung beim Treiber für den Zugriff auf Ressourcen dieser Karte.
                ** Nachdem kein Zugriff mehr auf die Ressourcen der Karte benötigt wird, sollte
                ** closeCard(int) aufgerufen werden.
                */
        
        public static native void closeCard(int handle) throws CeloxicaException;
                /*
                ** Gibt die Celoxica-Karte mit der ID <handle> wieder frei
                */
                
        public static native void requestMemoryBank(int handle, boolean b0, boolean b1, boolean b2, boolean b3) throws CeloxicaException;
                /*
                ** Fordert Zugriff auf Speicherbänke an:
                ** <b0> == true ==> Zugriff auf Bank 0 angefordert.
                ** ...
                ** <b3> == true ==> Zugriff auf Bank 3 angefordert.
                */

        public static native void releaseMemoryBank(int handle, boolean b0, boolean b1, boolean b2, boolean b3) throws CeloxicaException;
                /*
                ** Gibt Speicherbänke wieder frei:
                ** <b0> == true ==> Zugriff auf Bank 0 wird freigegeben.
                ** ...
                ** <b3> == true ==> Zugriff auf Bank 3 wird freigegeben.
                */
 
        public static native void setMClock(int handle, double freq) throws CeloxicaException;
                /*
                ** Programmiert den Taktgenerator MCLOCK. <freq> ist eine Angabe in Hertz.
                */

        public static native void setVClock(int handle, double freq) throws CeloxicaException;
                /*
                ** Programmiert den Taktgenerator VCLOCK. <freq> ist eine Angabe in Hertz.
                */

        public static native void configureFromFile(int handle, String filename) throws CeloxicaException;
                /*
                ** Programmiert den FPGA mit dem durch <filename> charakterisierten Bitstrom.
                */ 
        
        public static native void setGPO(int handle, boolean wert) throws CeloxicaException;
                /*
                ** Setzt 1-bit-Sigal GPO:
                **
                ** <wert> == true ==> GPO = 1
                ** <wert> == false ==> GPO = 0
                */
        
        public static native boolean readGPI(int handle) throws CeloxicaException;
                /*
                ** Liest 1-bit-Sigal GPI:
                **
                ** GPI == 1 ==> Methode liefert true.
                ** GPU == 0 ==> Methode liefert false.
                */

        
        public static native void writeControl (int handle, byte wert) throws CeloxicaException;
                /*
                ** Schreibt <wert> auf den Control-Port
                ** Methode blockiert solange, bis der FPGA diesen Wert gelesen hat!
                */
        
        public static native byte readStatus (int handle) throws CeloxicaException;
                /*
                ** Liefert den Wert auf dem Status-Port. Die Methode blockiert solange, bis
                ** der FPGA einen Wert auf den Sttus-Port geschrieben hat.
                */
        
//geplant:  public static native void writeToRam(int handle, short[] buffer, int adr);
                /*
                ** Schreibt das short-Array <buffer> ins Celoxica-RAM ab Adresse <adr>.
                ** Ein short ist in Java 16-bit lang und signed.
                ** Alle vier Bänke bilden einen fortlaufenden Adressraum, es werden 16-bit-Worte
                ** adressiert. D.h.:
                **
                ** Bank 0: 000000h bis 0FFFFFh
                ** Bank 1: 100000h bis 1FFFFFh 
                ** Bank 2: 200000h bis 2FFFFFh
                ** Bank 3: 300000h bis 3FFFFFh
                */

//geplant:  public static native short[] readShortArrayFromRam(int handle, int adr, int length);
                /*
                ** Gibt den Speicherinhalt des Celoxica-RAMs im Bereich die <adr>...<adr+length>
                ** als short-Array zurück. Ein short ist in Java 16-bit lang und signed.
                ** Alle vier Bänke bilden einen fortlaufenden Adressraum, es werden 16-bit-Worte
                ** adressiert. D.h.:
                **
                ** Bank 0: 000000h bis 0FFFFFh
                ** Bank 1: 100000h bis 1FFFFFh 
                ** Bank 2: 200000h bis 2FFFFFh
                ** Bank 3: 300000h bis 3FFFFFh
                */
        public static native void writeToRam(int handle, int[] buffer, int adr) throws CeloxicaException;
                /*
                ** Schreibt das int-Array <buffer> ins Celoxica-RAM ab Adresse <adr>.
                ** Ein int ist in Java 32-bit lang und signed.
                ** Alle vier Bänke bilden einen fortlaufenden Adressraum, es werden 32-bit-Worte
                ** adressiert. D.h.:
                **
                ** Bank 0: 000000h bis 07FFFFh
                ** Bank 1: 080000h bis 0FFFFFh 
                ** Bank 2: 100000h bis 17FFFFh
                ** Bank 3: 180000h bis 1FFFFFh
                */

        public static native int[] readIntArrayFromRam(int handle, int adr, int length) throws CeloxicaException;
                /*
                ** Gibt den Speicherinhalt des Celoxica-RAMs im Bereich die <adr>...<adr+length>
                ** als int-Array zurück. Ein intt ist in Java 16-bit lang und signed.
                ** Alle vier Bänke bilden einen fortlaufenden Adressraum, es werden 32-bit-Worte
                ** adressiert. D.h.:
                **
                ** Bank 0: 000000h bis 07FFFFh
                ** Bank 1: 080000h bis 0FFFFFh 
                ** Bank 2: 100000h bis 17FFFFh
                ** Bank 3: 180000h bis 1FFFFFh
                */
        static {
                System.loadLibrary("Celoxica");
        }
}
