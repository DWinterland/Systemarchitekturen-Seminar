package stereolab;
/* 
** Klasse:      StereoFPGAAlgo
** Autor:       Christian Werner <cwerner@informatik.hu-berlin.de>
** Version:     1.0 (vom 22. April 2002)
**
** Beschreibung:
**
** Algorithmenklasse für das Projekt "StereoLab". Stellt die Anbindung an die
** Celoxica-RC1000PP-Karte her.
*/

import java.io.*;
import javax.swing.*;
public class StereoFPGAAlgo extends StereoAlgorithm {

        private File f;
        private int freq;

        public StereoFPGAAlgo(File F,int FREQ) {
                super(0,0,0,false,0,false);
                freq=FREQ;
                f = F;
        }
        
        public String toString() {
                String st =     "Algorithmus:              Hardware\n"+
                                "Datei:                    "+((f.getName().length()>20)?"..."+f.getName().substring(f.getName().length()-17):f.getName())+"\n"+
                                "Taktfrequenz:             "+String.valueOf(freq)+" MHz";
                return (st);
        }
                
        public boolean equals(StereoFPGAAlgo sa) {
                return((freq==sa.freq)&&(f==sa.f)&&(getClass().equals(sa.getClass())));
        }
        
        public RangeProfile calc(byte[][] lb,byte[][] rb,TimerProgressBar p) {
                int zeilen = lb.length;
                int spalten = lb[0].length;
                
                int[] ausgangssignal;
                
                RangeProfile profile = new RangeProfile(zeilen,spalten);
                try {
                        int handle=Celoxica.openFirstCard();    // Liefert ID der ersten Celoxica am PCI-Bus
                        Celoxica.setMClock(handle,((double)freq) * 1000000d);
                        Celoxica.configureFromFile(handle,f.getAbsolutePath());
                                                                // FPGA programmieren
                        Celoxica.setGPO(handle,true);           // Reset-Signal für's FPGA Design
                        Celoxica.setGPO(handle,false);          // Reset-Signal wieder ausschalten
                        Celoxica.requestMemoryBank(handle, true, true, false, false);
                                                                // Zugriff auf Spreicherbänke 1 und 2 anfordern
                        Celoxica.writeToRam(handle, StereoToolkit.unsignedByteArray2d2IntArray(lb), 0x00000);
                        Celoxica.writeToRam(handle, StereoToolkit.unsignedByteArray2d2IntArray(rb), 0x80000);                        
                        Celoxica.releaseMemoryBank(handle, true, true, false, false);
                        Celoxica.writeControl(handle, (byte)1); // Sendet 1 an FPGA Steuerport.
                                                                // 1 bedeutet "losrechnen";
                        //System.out.println("Karte liefert Statusmeldung: "+Celoxica.readStatus(handle));
                                                                // Wartet auf Statusmeldung des FPGAs
                                                                // Statusmeldung sollte 1 sein.
                                                                // 1 heißt fertig.
                        Celoxica.requestMemoryBank(handle, false, false, true, false);
                                                                // Zugriff auf Spreicherbank 2 anfordern
                        ausgangssignal=Celoxica.readIntArrayFromRam(handle, 0x100000, zeilen*spalten);
                                                                // Ergebnisdaten liegen am Anfang von
                                                                // Bank 2.  Werte lesen.
                        Celoxica.releaseMemoryBank(handle, false, false, true, false);
                                                                // Bank 0 wieder freigeben
                        Celoxica.closeCard(handle);             // Celoxica-Karte freigeben
                }  catch (CeloxicaException e) {
                        JOptionPane.showMessageDialog(null,
                                "Fehler beim initialisieren der Celoxica Karte:\n"+
                                e.getMessage()+"\n"+
                                "Bitte stellen sie sicher, daß der Celoxica-Treiber geladen ist!",
                                "Celoxica-Fehler",
                                JOptionPane.ERROR_MESSAGE);
                                ausgangssignal=new int[zeilen*spalten];
                                try {   // falls FPGA "abgestürtzt: Karte freigeben.
                                       int handle=Celoxica.openFirstCard();
                                       Celoxica.closeCard(handle);
                                } catch (CeloxicaException f) {}
                }
                for (int i=0;i<zeilen;i++) {
                        p.setValue(i);
                        for (int j=0;j<spalten;j++) {
                                profile.setProfile(i,j,ausgangssignal[i*spalten+j]);
                                profile.setValid(i,j,true);
                        }
                }
                return(profile);
        }
}
