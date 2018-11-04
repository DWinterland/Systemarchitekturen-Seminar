package stereolab;
/* 
** Klasse:      StereoMSDnorm
** Autor:       Christian Werner <cwerner@informatik.hu-berlin.de>
** Version:     1.0 (vom 22. April 2002)
**
** Beschreibung:
**
** Algorithmenklasse für das Projekt "StereoLab". Flächenbasierter Algorithmus
** zur Disparitätenberechnung, der den quadrierte und normierte Grauwertdifferenzen
** als Ahnlichkeitskriterium benutzt.
*/


import javax.swing.*;

public class StereoMSDnorm extends StereoAlgorithm {

        public StereoMSDnorm(int B, int H, int TauMax, boolean U, int S,boolean F) {
                super(B,H,TauMax,U,S,F);
        }
        
        public String toString() {
                String st =     "Algorithmus:              Q. d. Diff. normiert\n"+
                                "Fenstergöße:              "+String.valueOf(b)+" x "+String.valueOf(h)+"\n"+
                                "Tau max:                  "+String.valueOf(tauMax)+"\n"+
                                "Schwellwert/Fenster-Fkt.: "+(useS?"ja":"nein")+"/"+(useF?"ja":"nein");
                if (useS) st=st+"\nSchwellwert:              "+String.valueOf(s);
                return (st);
        }
        
        public RangeProfile calc(byte[][] lb, byte[][] rb, TimerProgressBar p) {
                int zeilen = lb.length;
                int spalten = lb[0].length;
                int xu = -b/2;
                int xo = (b%2==1)?b/2:b/2-1;
                int yu = -h/2;
                int yo = (h%2==1)?h/2:h/2-1;
                int wc = b/2+h/2+1; // Eintrag in der "Mitte" der Fensterfkt.
                double[][] li,re; // linkes rechtes Bild als double und ggf. mit Fenster gewichtet!
                double l_sum,r_sum;
                int optIndex;
                double optWert;
                double wert;
                double temp;
                boolean val;
                
                RangeProfile profile = new RangeProfile(zeilen,spalten);
                li = new double[h][b];
                re = new double[h][b];
                
                for (int i=0;i<zeilen;i++) {
                        p.setValue(i);
                        for (int j=0;j<spalten;j++) {
                                if ( (i+yu<0) | (i+yo>=zeilen) | (j+xu-tauMax<0) | (j+xo+tauMax>=spalten) ) {
                                        profile.setProfile(i,j,0);
                                        profile.setValid(i,j,false);
                                } else {
                                        optWert=Double.MAX_VALUE;
                                        optIndex=0;
                                        val=false;
                                        //li setzen und l_sum berechnen
                                        l_sum=0d;
                                        for (int k=xu;k<=xo;k++) {
                                                        for (int l=yu;l<=yo;l++) {
                                                                temp=(double)StereoToolkit.unsignedByteToInt(lb[i+l][j+k]);
                                                                if (useF) temp=temp*(double)(wc-Math.abs(l)-Math.abs(k));
                                                                li[l-yu][k-xu]=temp;
                                                                l_sum=l_sum+temp; 
                                                        }
                                        }

                                        for (int tau=-tauMax;tau<=tauMax;tau++) {
                                                //re setzen und r_sum berechnen
                                                r_sum=0d;
                                                for (int k=xu;k<=xo;k++) {
                                                             for (int l=yu;l<=yo;l++) {
                                                                       temp=(double)StereoToolkit.unsignedByteToInt(rb[i+l][j+k+tau]);
                                                                       if (useF) temp=temp*(double)(wc-Math.abs(l)-Math.abs(k));
                                                                       re[l-yu][k-xu]=temp;
                                                                       r_sum=r_sum+temp; 
                                                             }
                                                }
                                                
                                                wert=0d;                                                
                                                for (int k=0;k<b;k++) {
                                                        for (int l=0;l<h;l++) {
                                                                wert=wert+Math.pow(li[l][k]-re[l][k],2d);
                                                        }
                                                }
                                                temp=Math.sqrt(Math.pow(l_sum,2d)*Math.pow(r_sum,2d));
                                                if (temp!=0d) wert=wert/temp; else wert=0d;
                                                //fertig
                                                if (wert<optWert) {optWert=wert; optIndex=tau; val=true;} else
                                                if (wert==optWert) {val=false;}
                                        }
                                        if ((useS) && (optWert>s)) val=false;
                                        profile.setProfile(i,j,optIndex);
                                        profile.setValid(i,j,val);
                                }
                        }
                }
                return(profile);
        }
}
