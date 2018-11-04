package stereolab;
/* 
** Klasse:      RangeProfile
** Autor:       Christian Werner <cwerner@informatik.hu-berlin.de>
** Version:     1.0 (vom 22. April 2002)
**
** Beschreibung:
**
** Repräsentiert eine Disparitätenmatrix für das Projekt "StereoLab". 
** Die Klasse enthält außerdem die Darsteller als Grauwertbild und 
** eine Boolean-Maske für ungültige (rot dargestellte) Bereiche.
*/

import java.io.*;
import javax.swing.*;

public class RangeProfile  implements Serializable {
        
        private int[][] profile;        // Profile (Ergebnis)
        private byte[][] normal;        // Profil (auf 256 Werte normalisiert)
        private boolean[][] valid;      // Wert an dieser Position mit Aussage?

        public RangeProfile(int width, int height) {
                profile = new int[width][height];
                valid = new boolean[width][height];
                normal = new byte[width][height];
        }
        
        public void setProfile(int i, int j, int value) {
                profile[i][j]=value;
        }
        
        public void setValid(int i, int j, boolean value) {
                valid[i][j]=value;
        }
        
        public boolean[][] getValidData() {
                return(valid);
        }
        
        public int[][] getRawProfileData() {
                return(profile);
        }
        
        public RGBPixel[][] getRGBImage() {
                int zeilen = profile.length;
                int spalten = profile[0].length;
                RGBPixel[][] data = new RGBPixel[zeilen][spalten];

                for (int i=0; i<=zeilen-1;i++) {
                        for (int j=0; j<=spalten-1;j++) {
                                data[i][j]=new RGBPixel(normal[i][j]);
                        }
                }
                return(data);
        }
        
        public RGBPixel[][] getRGBImageMasked() {
                int zeilen = profile.length;
                int spalten = profile[0].length;
                RGBPixel[][] data = new RGBPixel[zeilen][spalten];

                for (int i=0; i<=zeilen-1;i++) {
                        for (int j=0; j<=spalten-1;j++) {
                                if (valid[i][j]) data[i][j]=new RGBPixel(normal[i][j]); else
                                                 data[i][j]=new RGBPixel(StereoToolkit.intToUnsignedByte(255),
                                                                         StereoToolkit.intToUnsignedByte(0),
                                                                         StereoToolkit.intToUnsignedByte(0));
                        }
                }
                return(data);
        }

        public AdvancedHistogramm getHistogramm() {        
                return(new AdvancedHistogramm(StereoToolkit.intArray2d2IntArray(profile),StereoToolkit.booleanArray2d2BooleanArray(valid)));
        }
        
        public void validNormalize(AdvancedHistogramm h) {
                normal=h.validNormalize(profile);
        }

        public void globalNormalize(AdvancedHistogramm h) {
                normal=h.globalNormalize(profile);
        }
        
        public void saveNormalized(File f) {
                 try {
                        FileOutputStream ostream = new FileOutputStream(f);
                        ostream.write(StereoToolkit.ByteArray2d2ByteArray(normal));
                        ostream.close();
                } catch (Exception e) {
                        System.out.println(e.getMessage());
                }
        }
        
        public double  MAECompare(RangeProfile p, int RandObenUndUnten, int RandLinksUndRechts) {
                if (    (getRawProfileData().length!=p.getRawProfileData().length) ||
                        (getRawProfileData()[0].length!=p.getRawProfileData()[0].length)) 
                        return (Double.NaN);
                else {
                        int zeilen = profile.length;
                        int spalten = profile[0].length;
                        int t=0;
                        
                        for (int i=0; i<zeilen;i++) {
                                for (int j=0; j<spalten;j++) {
                                        if((i>RandObenUndUnten-1) && (i<zeilen-RandObenUndUnten) &&
                                           (j>RandLinksUndRechts-1) && (j<spalten-RandLinksUndRechts)) {
                                                t = t + Math.abs(p.getRawProfileData()[i][j]-getRawProfileData()[i][j]);
//                                                if (Math.abs(p.getRawProfileData()[i][j]-getRawProfileData()[i][j])!=0) System.out.println(String.valueOf(i)+" "+String.valueOf(j)+String.valueOf(getRawProfileData()[i][j])+" "+String.valueOf(p.getRawProfileData()[i][j]));
                                        }
                                }
                        }
//                        System.out.println(t);
                        return ((double) t / (double)((zeilen-2*RandObenUndUnten)*(spalten-2*RandLinksUndRechts)));
               }
        }
}        
