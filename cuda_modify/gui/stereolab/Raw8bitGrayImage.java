package stereolab;
/* 
** Klasse:      BitFileFilter
** Autor:       Christian Werner <cwerner@informatik.hu-berlin.de>
** Version:     1.0 (vom 22. April 2002)
**
** Beschreibung:
**
** Daten-Klasse für das Projekt "StereoLab". Repräsentiert ein 8-Bit-Grauwert-Bild
** im Raw-Format.
*/


import java.io.*;
import java.awt.*;
import java.awt.image.*;

public class Raw8bitGrayImage {

        private byte[][] BildDaten;
        
        private File file;
        private int xSize;
        private int ySize;
        
        public Raw8bitGrayImage(File f, int Zeilen, int Spalten) {
                BildDaten = new byte [Zeilen][Spalten];
                try {
                        FileInputStream BildDatei = new FileInputStream(f);
                        for (int i = 0; i <= Zeilen - 1; i++) {
                                    BildDatei.read(BildDaten[i]);
                        }
                        BildDatei.close();
                } catch (Exception e) {};
                ySize = Zeilen;
                xSize = Spalten;
                file = f;
        }
        
        public byte[][] getData(){
                return(BildDaten);
        }
        
        public File getFile() {
                return(file);       
        }
        public String getDescription() {
                return(file.getAbsolutePath()+" ("+getWidth()+" x "+getHeight()+")");
        }
        
        public int getHeight() {
                return(ySize);
        }

        public int getWidth() {
                return(xSize);
        }
}



