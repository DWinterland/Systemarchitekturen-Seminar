package stereolab;
/* 
** Klasse:      StereoCorr
** Autor:       Christian Werner <cwerner@informatik.hu-berlin.de>
** Version:     1.0 (vom 22. April 2002)
**
** Beschreibung:
**
** Algorithmenklasse für das Projekt "StereoLab". Flächenbasierter Algorithmus
** zur Disparitätenberechnung, der den Pearson'schen Korrelationskoeffizienten als
** Ahnlichkeitskriterium benutzt.
*/


import javax.swing.*;

public class StereoCorr extends StereoAlgorithm {

        public StereoCorr(int B, int H, int TauMax, boolean U,int S,boolean F) {
                super(B,H,TauMax,U,S,F);
        }
        
        public String toString() {
                String st =     "Algorithmus:              Pearson'scher KKF\n"+
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
                int ws = zeilen*spalten; // Fenstergröße
                int optIndex;
                double optWert;
                double wert;
                double m_l; // mittelwert links
                double m_r; // Mittelwert rechts
                double cov,var_l,var_r;
                boolean val;
                double temp;
                
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
                                        optWert=Double.MIN_VALUE;
                                        optIndex=0;
                                        val=false;
                                        //li setzen und linken Mittelwert berechnen
                                        m_l=0d;
                                        for (int k=xu;k<=xo;k++) {
                                                        for (int l=yu;l<=yo;l++) {
                                                                temp=(double)StereoToolkit.unsignedByteToInt(lb[i+l][j+k]);
                                                                if (useF) temp=temp*(double)(wc-Math.abs(l)-Math.abs(k));
                                                                li[l-yu][k-xu]=temp;
                                                                m_l=m_l+temp; 
                                                        }
                                        }
                                        m_l = m_l / ws;
                                        //linke Streuung berechnen:
                                        var_l=0d;
                                        for (int k=0;k<b;k++) {
                                                 for (int l=0;l<h;l++) {
                                                           var_l=var_l + Math.pow(li[l][k] - m_l,2);
                                                 }
                                        }
                                        var_l= var_l/ws;
                                        
                                        for (int tau=-tauMax;tau<=tauMax;tau++) {
                                                wert=0;
                                                //re setzen und rechten Mittelwert berechnen
                                                m_r=0d;
                                                for (int k=xu;k<=xo;k++) {
                                                        for (int l=yu;l<=yo;l++) {
                                                                temp=(double) StereoToolkit.unsignedByteToInt(rb[i+l][j+k+tau]);
                                                                if (useF) temp=temp*(double)(wc-Math.abs(l)-Math.abs(k));
                                                                re[l-yu][k-xu]=temp;
                                                                m_r=m_r+temp; 
                                                        }
                                                }
                                                m_r = m_r / ws;
                                        
                                                //rechte Streuung berechnen:
                                                var_r=0d;
                                                for (int k=0;k<b;k++) {
                                                        for (int l=0;l<h;l++) {
                                                                   var_r=var_r + Math.pow(re[l][k] - m_r,2);
                                                        }
                                                }
                                                var_r= var_r/ws;
                                                
                                                // Covarianz berechnen:
                                                cov=0d;
                                                for (int k=0;k<b;k++) {
                                                        for (int l=0;l<h;l++) {
                                                                   cov=cov + (li[l][k] - m_l)*(re[l][k] - m_r);
                                                        }
                                                }
                                                cov = cov / ws;
                                                wert = Math.sqrt(var_l*var_r);
   /*                                             System.out.print("wert:"+wert);
                                                System.out.print(" m_l:"+m_l);
                                                System.out.print(" m_r:"+m_r);
                                                System.out.print(" var_l:"+var_l);
                                                System.out.print(" var_r:"+var_r);
                                                System.out.println(" cov:"+cov);
*/                                                if (wert != 0d) wert = cov / wert;
                                                //fertig...
                                                
                                                if (wert>optWert) {optWert=wert; optIndex=tau; val=true;} else
                                                if (wert==optWert) {val=false;}
                                        }
                                        if ((useS) && (optWert<((double)s)/1000d)) val=false;
                                        profile.setProfile(i,j,optIndex);
                                        profile.setValid(i,j,val);
                                }
                        }
                }
                return(profile);
        }
}
