package stereolab;
/* 
** Klasse:      AdvancedHistogrammPanel
** Autor:       Christian Werner <cwerner@informatik.hu-berlin.de>
** Version:     1.0 (vom 22. April 2002)
**
** Beschreibung:
**
** Klasse für das Projekt "StereoLab". Stellt Histogramm-Objekte als Panel grafisch dar.
*/


import java.util.*;
import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;


public class AdvancedHistogrammPanel extends JPanel {

                                                                      //-- alle Angaben in Pixeln:
        final private int H=100;        // preferred Height             -- ungefähre Größe des Plots (ohne Achsen und Beschriftung) max H=2147!
        final private int W=400;        // preferred Width
        final private int LOL=40;       // label offset left            -- Platz für Beschriftung (links)
        final private int LOB=30;       // label offset bottom          -- Platz für Beschriftung (unten)
        final private int LO=30;        // legend offset                -- Platz für "Legende"
        final private int LB=10;         // legend Border                -- Abstand der Legende zu den Zahlen der X-Achsenbeschr.
        
        final private int MYLC=10;      // max y-Axis label count       -- größtmögliche Anzahl von Beschriftungen an y-Achse.
        
        final private int BST=30;       // border space                 -- Abstand zu den Panel-Rändern
        final private int BSR=50;
        final private int BSB=10;
        final private int BSL=30;
        
        private AdvancedHistogramm h;

        private int width;              // Panel-Breite
        private int height;             // Panel-Höhe

        private int PH;                 // plot height                  -- Höhe des Plots
        private int PW;                 // plot width                   -- Breites des Plots
        
        private int CC;                 // column count                 -- Anzahl der dargestellten Säulen
        private int CW;                 // column width                 -- Breite einer Säule
        private int CD;                 // column distance              -- Abstand zwischen zwei Säulen

        private int LD;                 // label distance               -- Abstand zwischen zwei Beschriftungen
        private int min;                // kleinste Säulenbeschriftung
        private int max;                // größte Säulenbeschriftung
        private int ZCC;                // zero column count            -- Zahl nicht vorhandener Säulen zwischen li Beschriftung und 1. Säule
        private int RZCC;               // right zero column count      -- Zahl nicht vorhandener Säulen zwischen re Beschriftung und letzer Säule

        private int YLD;                // y-axis label distance        -- Abstand zwischen zwei Beschriftungen auf der y-Achse
        private int y_max;              // größte y-Achsenbeschriftung
        
        private ArrayList linesToDraw;
        private ArrayList blueRectColumnsToDraw;
        private ArrayList blueLineColumnsToDraw;
        private ArrayList redRectColumnsToDraw;
        private ArrayList redLineColumnsToDraw;
        private ArrayList stringShapesToDraw;
        private ArrayList yStringShapesToDraw;
        private ArrayList legendLineColumnsToDraw;
        private ArrayList legendRectColumnsToDraw;
        private Font gFont,kFont,dFont,tFont,mFont; // großer, kleiner und default Font. Außerdem -mini-Font und Font für Tau

        private int temp,temp2;
        
        public AdvancedHistogrammPanel (AdvancedHistogramm Hist, boolean valid_legend) {
                super();
                setBackground(Color.white);
                gFont = Font.decode("Dialog plain 14");
                kFont = Font.decode("Dialog plain 10");
                tFont = new Font("Times New Roman",Font.PLAIN,16);
                mFont = Font.decode("Dialog plain 8");
                
                h = Hist;

                CC = h.getGlobalData().length;// soviele H-Säulen gibt es
                int CS = W/CC;          // column space                 -- verfügbare Pixel pro Säule
                if (CS > 30) CS = 30;   // "Riesensäulen" sollen nicht sein! ;-)
CW=CS;                CW = CS / 2;            // diesen Raum gleichmäßig für Säulen und Abstände dazwischen nutzen
CD=0;//                CD = CW;
                if (CW%2==0) CW++;      // bei geraden Werten oder 0 ein Pixel breiter machen.
                
                
                if (CW<2) LD=100; else LD=10;
                
                min = h.getGlobalMin();       // linke und rechte Beschriftung auf Vielfaches von LD setzen.
                if (min<0) ZCC=LD+(min%LD); else ZCC = min%LD;
                if (ZCC==0) ZCC=LD;
                min=min-ZCC;

                max = h.getGlobalMax();
                if (max>0) RZCC=(LD-(max%LD)); else RZCC=-(max%LD);
                if( RZCC==0) RZCC=LD;
                max=max+RZCC;
                
                PW = (ZCC+CC+RZCC-1)*(CW+CD);
                width = BSL+LOL+PW+BSR;
                
                // sinnvolle Einteilung der y-Achse finden...
                int y_max=(h.getGlobalData()[h.getGlobalModalwert()]);
                YLD = 1;
                while ((y_max/YLD>MYLC-1)) {
                        YLD=YLD*10;                     // Abstand verzehnfachen und
                        y_max=y_max+(YLD-(y_max%YLD));  // zur nächsten Dezimal-Stelle runden.
                } 
                int yScale = (H*1000000) / y_max;       // Einheit: 1.000.000stel!
                PH  = (y_max*yScale+500000)/1000000;

                height =BSB+LO+LOB+PH+BST;

                setPreferredSize(new Dimension(width,height));
//                System.out.println("Width: "+width+"  Height: "+height+"  CC: "+CC+"  CD: "+CD+"  CW: "+CW+"  LD: "+LD+  "min: "+min+"  max: "+max+ "  ZCC: "+ZCC+"  RZCC: "+RZCC);

                linesToDraw = new ArrayList();
                blueRectColumnsToDraw = new ArrayList();
                blueLineColumnsToDraw = new ArrayList();
                redRectColumnsToDraw = new ArrayList();
                redLineColumnsToDraw = new ArrayList();
                stringShapesToDraw = new ArrayList();
                yStringShapesToDraw = new ArrayList();
                legendRectColumnsToDraw = new ArrayList();
                legendLineColumnsToDraw = new ArrayList();

                // y-Achse:                
                linesToDraw.add(new Line2D.Double(     BSL+LOL,BST+PH,
                                                        BSL+LOL,BST
                                                   ));
                                                   
                // y-Achsenbeschriftung;
                for (int i=0; i<=y_max;i=i+YLD) {
                         linesToDraw.add(new Line2D.Double(     BSL+LOL-5,BST+PH-(i*yScale+50000)/1000000,
                                                                BSL+LOL,BST+PH-(i*yScale+50000)/1000000
                                                          ));
                         yStringShapesToDraw.add( new StringShape(java.lang.String.valueOf(i),BSL+LOL-10,BST+PH-(i*yScale+50000)/1000000
                                                ));                                 
                 }
                 
                // x-Achse:
                
                
                
                linesToDraw.add(new Line2D.Double(     BSL+LOL,BST+PH,
                                                        BSL+LOL+PW,BST+PH
                                                  ));

                linesToDraw.add(new Line2D.Double(     BSL+LOL,BST+PH+LOB+LB+10,          // für Legende
                                                        BSL+LOL+PW,BST+PH+LOB+LB+10
                                                  ));
                
                linesToDraw.add(new Line2D.Double(     BSL+LOL,BST+PH+LOB+LB+15,          // für Legende
                                                        BSL+LOL+PW,BST+PH+LOB+LB+15
                                                  ));

                // x-Achsenbeschriftung:
                
                for (int i=0; i<ZCC+CC+RZCC;i++) {
                                                
                       if (i%LD==0) {
                               linesToDraw.add( new Line2D.Double(     BSL+LOL+i*(CW+CD),BST+PH,
                                                                        BSL+LOL+i*(CW+CD),BST+PH+10
                                                ));
                               linesToDraw.add( new Line2D.Double(     BSL+LOL+i*(CW+CD),BST+PH+LOB+LB,
                                                                        BSL+LOL+i*(CW+CD),BST+PH+LOB+LB+10
                                                ));
                                                
                               stringShapesToDraw.add( new StringShape(java.lang.String.valueOf(min+i),BSL+LOL+i*(CW+CD),BST+PH+LOB
                                                ));
                       } else
                       if ((i%(LD/2)==0) & (LD%2==0)) {
                               linesToDraw.add( new Line2D.Double(      BSL+LOL+i*(CW+CD),BST+PH,
                                                                         BSL+LOL+i*(CW+CD),BST+PH+5
                                                ));
                               linesToDraw.add( new Line2D.Double(      BSL+LOL+i*(CW+CD),BST+PH+LOB+LB+5,      // für Legende
                                                                         BSL+LOL+i*(CW+CD),BST+PH+LOB+LB+10
                                                ));
                       } else 
                       if ((i%(LD/10)==0) & (LD%10==0)) {
                               linesToDraw.add( new Line2D.Double(      BSL+LOL+i*(CW+CD),BST+PH,
                                                                         BSL+LOL+i*(CW+CD),BST+PH+2
                                                ));

                               linesToDraw.add( new Line2D.Double(      BSL+LOL+i*(CW+CD),BST+PH+LOB+LB+8,
                                                                         BSL+LOL+i*(CW+CD),BST+PH+LOB+LB+10
                                                ));
                       }
                       if ((i>=ZCC) & (i<ZCC+CC)) {
                               int j = i-ZCC;
                               int bluerectheight = (h.getValidDataElement(j)*yScale+50000)/1000000;
                               if ((bluerectheight==0) & (h.getValidDataElement(j)!=0)) bluerectheight=1;
                               int redrectheight = (h.getInvalidDataElement(j)*yScale+50000)/1000000;
                               if ((redrectheight==0) & (h.getInvalidDataElement(j)!=0)) redrectheight=1;
                               if (bluerectheight!=0) {
                                        if (CW>1) blueRectColumnsToDraw.add( new Rectangle2D.Double( BSL+LOL+i*(CW+CD)-CW/2,BST+PH-bluerectheight,
                                                                         CW,bluerectheight
                                                                        ));
                                        else      blueLineColumnsToDraw.add( new Line2D.Double( BSL+LOL+i*(CW+CD),BST+PH-bluerectheight,
                                                                        BSL+LOL+i*(CW+CD),BST+PH-1
                                                                        ));
                               }
                               if (redrectheight!=0) {
                                        if (CW>1) redRectColumnsToDraw.add( new Rectangle2D.Double( BSL+LOL+i*(CW+CD)-CW/2,BST+PH-redrectheight-bluerectheight,
                                                                         CW,redrectheight
                                                                        ));
                                        else      redLineColumnsToDraw.add( new Line2D.Double( BSL+LOL+i*(CW+CD),BST+PH-redrectheight-bluerectheight,
                                                                        BSL+LOL+i*(CW+CD),BST+PH-1-bluerectheight
                                                                        ));
                               }

                               int grauwert;

                               if (valid_legend) grauwert = h.validNormalize(h.getGlobalMin()+j); else grauwert=h.globalNormalize(h.getGlobalMin()+j);
                               
                               if (CW>1) legendRectColumnsToDraw.add( new LegendRectColumn( BSL+LOL+i*(CW+CD)-CW/2,BST+PH+LOB+LB+11,
                                                                       CW,4,new Color(grauwert,grauwert,grauwert)
                                                                  ));
                               else      legendLineColumnsToDraw.add( new LegendLineColumn( BSL+LOL+i*(CW+CD),BST+PH+LOB+LB+11,
                                                                        BSL+LOL+i*(CW+CD),BST+PH+LOB+LB+14,new Color(grauwert,grauwert,grauwert)
                                                                  ));


                       }
                }


                repaint();
                invalidate();
       }

        public void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                super.paintComponent( g );
                
                for (int i=0;i<linesToDraw.size();i++) {
                        Shape s = (Shape) linesToDraw.get(i);
                        g2.draw(s);
                }
                
                for (int i=0;i<stringShapesToDraw.size();i++) {         // Strings zentriert rendern!
                        StringShape ss = (StringShape) stringShapesToDraw.get(i);
                        g2.drawString(ss.string,ss.xPos-g2.getFontMetrics().stringWidth(ss.string)/2,ss.yPos);
                }
                
                for (int i=0;i<yStringShapesToDraw.size();i++) {         // Strings vertikal zentriert und rechtsbündig rendern!
                        StringShape ss = (StringShape) yStringShapesToDraw.get(i);
                        g2.drawString(ss.string,ss.xPos-g2.getFontMetrics().stringWidth(ss.string),ss.yPos+g2.getFontMetrics().getAscent()/2);
                }

                dFont = g2.getFont();                
                g2.setFont(gFont);
                g2.drawString("X",BSL+LOL+PW+10,BST+PH+g2.getFontMetrics().getAscent()/2);
                temp=g2.getFontMetrics().stringWidth("X")+2;
                temp2=g2.getFontMetrics().getAscent()/2;
                g2.setFont(tFont);
                g2.drawString("\u03C4",BSL+LOL+PW+10+temp,BST+PH+g2.getFontMetrics().getAscent()/2+temp2);
                temp=temp+g2.getFontMetrics().stringWidth("\u03C4")+2;
                temp2=temp2+g2.getFontMetrics().getAscent()/2;
                g2.setFont(mFont);
                g2.drawString("opt",BSL+LOL+PW+10+temp,BST+PH+temp2);
                g2.setFont(gFont);
                temp = g2.getFontMetrics().stringWidth("H")/2;
                temp2= g2.getFontMetrics().getAscent()/2;
                g2.drawString("H",BSL+LOL-temp,BST-10);
                g2.setFont(kFont);
                g2.drawString("abs",BSL+LOL+temp,BST-10+temp2);
                g2.setFont(dFont);
                g2.setColor(Color.blue);
                for (int i=0;i<blueLineColumnsToDraw.size();i++) {
                        Shape s = (Shape) blueLineColumnsToDraw.get(i);
                        g2.draw(s);
                }

                for (int i=0;i<blueRectColumnsToDraw.size();i++) {
                        Shape s = (Shape) blueRectColumnsToDraw.get(i);
                        g2.fill(s);
                }

                g2.setColor(Color.red);
                for (int i=0;i<redLineColumnsToDraw.size();i++) {
                        Shape s = (Shape) redLineColumnsToDraw.get(i);
                        g2.draw(s);
                }

                for (int i=0;i<redRectColumnsToDraw.size();i++) {
                        Shape s = (Shape) redRectColumnsToDraw.get(i);
                        g2.fill(s);
                }
                
                for (int i=0;i<legendRectColumnsToDraw.size();i++) {
                        LegendRectColumn lrc = (LegendRectColumn) legendRectColumnsToDraw.get(i);
                        g2.setColor(lrc.getColor());
                        g2.fill((Shape) lrc);
                }
                
                for (int i=0;i<legendLineColumnsToDraw.size();i++) {
                        LegendLineColumn llc = (LegendLineColumn) legendLineColumnsToDraw.get(i);
                        g2.setColor(llc.getColor());
                        g2.draw((Shape) llc);
                }
                
                
                
        }
}                
