package stereolab;
/* 
** Klasse:      StereoToolkit
** Autor:       Christian Werner <cwerner@informatik.hu-berlin.de>
** Version:     1.0 (vom 22. April 2002)
**
** Beschreibung:
**
** Hilfsklasse für das Projekt "StereoLab". Definiert universell nützliche Static-Methoden. 
*/

import java.awt.image.*;

public class StereoToolkit {
                
        public static byte[][] resizeImage(byte[][] ImageDaten, int percent) {

                int xSize = ImageDaten[0].length;
                int ySize = ImageDaten.length;
                
                int prevX = resize(xSize,percent);
                int prevY = resize(ySize,percent);
                
                byte[][] PreviewDaten = new byte [prevY][prevX];
                for (int i = 0; i <= prevX - 1; i++) {
                        for (int j = 0; j <= prevY - 1; j++) {
                                PreviewDaten[j][i]=ImageDaten[j*ySize/prevY][i*xSize/prevX];
                        }
                }
                return(PreviewDaten);
        }
        
        public static int resize(int i, int percent) {
                return((i*percent+50)/100);
        }

        public static BufferedImage getBufferedImage(byte[][] PreviewDaten) {
                int prevX = PreviewDaten[0].length;
                int prevY = PreviewDaten.length;

                
                BufferedImage myImage = new BufferedImage(prevX,prevY,BufferedImage.TYPE_BYTE_GRAY);
                WritableRaster wras = myImage.getRaster();
                wras.setSamples(0, 0, prevX, prevY, 0, ByteArray2d2IntArray(PreviewDaten));
                return(myImage);
        }

        public static BufferedImage getBufferedImage(RGBPixel[][] PreviewDaten) {
                int Spalten = PreviewDaten[0].length;
                int Zeilen = PreviewDaten.length;
                BufferedImage myImage = new BufferedImage(Spalten,Zeilen,BufferedImage.TYPE_3BYTE_BGR);
                WritableRaster wras = myImage.getRaster();

                for (int i=0;i<=Zeilen-1;i++) {
                        for (int j=0;j<=Spalten-1;j++) {
                                wras.setPixel(j, i, PreviewDaten[i][j].getIntArray());
                        }
                }
                return(myImage);
        }
        
        public static int[] ByteArray2d2IntArray(byte[][] ba) {
                int Zeilen=ba.length;
                int Spalten=ba[0].length;
                
                int[] ia = new int[Zeilen*Spalten];
                for (int i=0;i<=Zeilen-1;i++) {
                        for (int j=0;j<=Spalten-1;j++) {
                                ia[i*Spalten+j]=ba[i][j];
                        }
                }
                return(ia);
        }
        public static int[] unsignedByteArray2d2IntArray(byte[][] ba) {
                int Zeilen=ba.length;
                int Spalten=ba[0].length;
                
                int[] ia = new int[Zeilen*Spalten];
                for (int i=0;i<=Zeilen-1;i++) {
                        for (int j=0;j<=Spalten-1;j++) {
                                ia[i*Spalten+j]=unsignedByteToInt(ba[i][j]);
                        }
                }
                return(ia);
        }
        public static byte[] ByteArray2d2ByteArray(byte[][] ba) {
                int Zeilen=ba.length;
                int Spalten=ba[0].length;
                
                byte[] ia = new byte[Zeilen*Spalten];
                for (int i=0;i<=Zeilen-1;i++) {
                        for (int j=0;j<=Spalten-1;j++) {
                                ia[i*Spalten+j]=ba[i][j];
                        }
                }
                return(ia);
        }
        
        public static byte[][] ByteArray2ByteArray2d(byte[] ba,int Zeilen,int Spalten) {
                
                byte[][] ia = new byte[Zeilen][Spalten];
                
                for (int i=0;i<=Zeilen-1;i++) {
                        for (int j=0;j<=Spalten-1;j++) {
                                ia[i][j]=ba[i*Spalten+j];
                        }
                }
                return(ia);
        }
        
        public static int[] intArray2d2IntArray(int[][] ia) {
                int Zeilen=ia.length;
                int Spalten=ia[0].length;
                
                int[] nia = new int[Zeilen*Spalten];
                for (int i=0;i<=Zeilen-1;i++) {
                        for (int j=0;j<=Spalten-1;j++) {
                                nia[i*Spalten+j]=ia[i][j];
                        }
                }
                return(nia);
        }        

        
        public static long[] longArray2d2LongArray(long[][] ia) {
                int Zeilen=ia.length;
                int Spalten=ia[0].length;
                
                long[] nia = new long[Zeilen*Spalten];
                for (int i=0;i<=Zeilen-1;i++) {
                        for (int j=0;j<=Spalten-1;j++) {
                                nia[i*Spalten+j]=ia[i][j];
                        }
                }
                return(nia);
        }

        public static boolean[] booleanArray2d2BooleanArray(boolean[][] ba) {
                int Zeilen=ba.length;
                int Spalten=ba[0].length;
                
                boolean[] nba = new boolean[Zeilen*Spalten];
                for (int i=0;i<=Zeilen-1;i++) {
                        for (int j=0;j<=Spalten-1;j++) {
                                nba[i*Spalten+j]=ba[i][j];
                        }
                }
                return(nba);
        }

        public static int unsignedByteToInt(byte b) {
                return (int) b & 0xFF;
        }
        
        public static byte intToUnsignedByte(int i) {
                if (i<=0) return (0); else
                if (i>=255) return (-1); else
                return((byte)(i));
        }
        public static int square(int i) {
                return(i*i);
        }
        
        public static int pow(int x, int y) {  // x^y
                int z=1;
                for (int i=0;i<y;i++) {
                        z=z*x;
                }
                return(z);
        }

        public static int[] unsignedByteArray2IntArray(byte[] ba) {
                int[] ia = new int[ba.length];
                for (int i=0;i<ba.length;i++) {
                          ia[i]=unsignedByteToInt(ba[i]);
                }
                return(ia);
        }        
        
        public static double[] unsignedByteArray2DoubleArray(byte[] ba) {
                double[] da = new double[ba.length];
                for (int i=0;i<ba.length;i++) {
                          da[i]=(double)unsignedByteToInt(ba[i]);
                }
                return(da);
        }
        
        public static double[] elimMittelwert(byte[] ba) {
                double mittelwert=0;
                double[] da=unsignedByteArray2DoubleArray(ba);
                
                for (int i=0;i<da.length;i++) {
                          mittelwert=mittelwert+da[i];
                }

                mittelwert=mittelwert/da.length;

                for (int i=0;i<da.length;i++) {
                          da[i]=da[i]-mittelwert;
                }
                return(da);

        }
        
        public static int[] elimIntMittelwert(byte[] ba) {
                int mittelwert=0;
                int[] ia=unsignedByteArray2IntArray(ba);
                
                for (int i=0;i<ia.length;i++) {
                          mittelwert=mittelwert+ia[i];
                }

                mittelwert=(mittelwert*10+5)/(ia.length*10);

                for (int i=0;i<ia.length;i++) {
                          ia[i]=ia[i]-mittelwert;
                }
                return(ia);

        }
        
        public static double cov(byte[] b1, byte[] b2) {
                double temp=0;
                double[] d1 = elimMittelwert(b1);
                double[] d2 = elimMittelwert(b2);
                
                for (int i=0; i<b1.length;i++){
                        temp = temp + d1[i]*d2[i];
                }
                temp=temp/b1.length;
                
                return(temp);
        }
        
        public static long korrKoef(byte[] b1, byte[] b2) {
                double d = Math.sqrt(cov(b1,b1)*cov(b2,b2));
                if (d == 0d) return(0); else d = cov(b1,b2)/d;
                return(Math.round(d*1000));
        }
        
        public static long diffKoef(byte[] b1, byte[] b2) {
                long l = 0;
//                int[] ia1 = elimIntMittelwert(b1);
//                int[] ia2 = elimIntMittelwert(b2);
                int[] ia1 = unsignedByteArray2IntArray(b1);
                int[] ia2 = unsignedByteArray2IntArray(b2);
                for (int i=0; i < b1.length; i++) {
                        l=l+Math.abs(ia1[i]-ia2[i])/(Math.abs(b1.length/2-i)+1);
                }
                return(l);
        }
        
        public static void korrKoefDebug(byte[] b1, byte[] b2) {
                System.out.println("11: "+cov(b1,b2));
                System.out.println("12: "+cov(b1,b1));
                System.out.println("22: "+cov(b2,b2));
        }

        public static void ArrayPrint(byte[] ba) {
                for (int i=0;i < ba.length;i++) System.out.print(unsignedByteToInt(ba[i])+" ");
                System.out.println();

        }
        public static void ArrayPrint(int[] ia) {
                for (int i=0;i < ia.length;i++) System.out.print(ia[i]+" ");
                System.out.println();

        }
        
         public static void ArrayPrint(String[] sa) {
                for (int i=0;i < sa.length;i++) System.out.println(sa[i]);
        }
        
        public static String tausendstelString(int i) { // 576 -> 0.576, 1 -> 0.001
                String snk = Integer.toString(i%1000);          // nach dem Komma
                String svk = Integer.toString(i/1000)+",";      // vor dem Komma
                int nk_laenge = snk.length();
                for (int j=0; j<3 - nk_laenge;j++) snk="0"+snk;
                return(svk+snk); 
        }                
}
