package stereolab;
/* 
** Klasse:      DifferenceImage
** Autor:       Christian Werner <cwerner@informatik.hu-berlin.de>
** Version:     1.0 (vom 22. April 2002)
**
** Beschreibung:
**
** Hilfsklasse für das Projekt "StereoLab". Sie stellt die Datenstruktur für den "Differencer"
** Monitor zur Verfügung und liefert die Differnzbilder zu gegebenen Verschiebungswerten.
*/


import java.awt.image.*;

public class DifferenceImage {
        
        private byte[][] lb,rb,db;
        private RGBPixel[][] db_rgb;

        public DifferenceImage(byte[][] LB, byte[][] RB) {
                lb = LB;
                rb = RB;
                db = new byte[lb.length][lb[0].length];
                db_rgb = new RGBPixel[lb.length][lb[0].length];
                
                int zeilen = db_rgb.length;
                int spalten = db_rgb[0].length;
                int temp;
                
                for (int i=0; i<=zeilen-1;i++) {
                        for (int j=0; j<=spalten-1;j++) {
                                db_rgb[i][j] = new RGBPixel((byte)0);
                        }
                }
        }
        
        public byte[][] getDifferencePlotAt(int tau , int delta) {
                int zeilen = db.length;
                int spalten = db[0].length;
                
                for (int i=0; i<=zeilen-1;i++) {
                        for (int j=0; j<=spalten-1;j++) {
                                if (inRange(i,j+tau)) {
                                        db[i][j]=StereoToolkit.intToUnsignedByte(java.lang.Math.abs(StereoToolkit.unsignedByteToInt(lb[i][j])-StereoToolkit.unsignedByteToInt(rb[i][j+tau])));
                                } else db[i][j]=127;
                        }
                }
                return(db);
        }
        public RGBPixel[][] getHighlightedDifferencePlotAt(int tau , int delta) {
                int zeilen = db_rgb.length;
                int spalten = db_rgb[0].length;
                int temp;
                
                for (int i=0; i<=zeilen-1;i++) {
                        for (int j=0; j<=spalten-1;j++) {
                                if (inRange(i,j+tau)) {
                                        temp = StereoToolkit.unsignedByteToInt(lb[i][j])-StereoToolkit.unsignedByteToInt(rb[i][j+tau]);
                                        if (isNull(temp,delta)) db_rgb[i][j].set(  StereoToolkit.intToUnsignedByte(255),
                                                                                        StereoToolkit.intToUnsignedByte(0),
                                                                                        StereoToolkit.intToUnsignedByte(0)
                                                                                      );
                                        else {
                                                db_rgb[i][j].set(StereoToolkit.intToUnsignedByte(java.lang.Math.abs(temp)));
                                        }
                                } else db_rgb[i][j].set((byte)127);
                        }
                }
                return(db_rgb);
        }

        private boolean inRange(int i,int j){
                if ((i>=0)&(i<lb.length)&(j>=0)&(j<lb[0].length)) return(true);
                return(false);
        }
        
        private boolean isNull(int i, int delta) {
                  if (((i<=0) & (i>=0-delta)) | ((i>0) & (i<=0+delta))) return(true);
                  else return(false);
        }

}
