package stereolab;
/* 
** Klasse:      TrackPanel
** Autor:       Christian Werner <cwerner@informatik.hu-berlin.de>
** Version:     1.0 (vom 22. April 2002)
**
** Beschreibung:
**
** Hilfsklasse für das Projekt "StereoLab".
** Spezielles Panel, um Bilder einfach darstellen zu können.
*/


import java.awt.image.*;
import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;

public class TrackPanel extends JPanel {
        
        private BufferedImage bi;               // dargestelltes Image
        
        public TrackPanel(byte[][] DATEN) {
                super();
                setData(DATEN);
        }

        public TrackPanel(int height, int width) {
                super();
                bi = new BufferedImage(width,height,BufferedImage.TYPE_BYTE_BINARY);
                setPreferredSize(new Dimension(bi.getWidth(),bi.getHeight()));
                repaint();
                invalidate();
        }

        public TrackPanel(RGBPixel[][] DATEN) {
                super();
                setData(DATEN);
        }                
        

        public void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                super.paintComponent( g );
                g2.drawImage(bi, 0, 0, this);
        }


        public void setData(byte[][] d) {
                bi=StereoToolkit.getBufferedImage(d);
                setPreferredSize(new Dimension(bi.getWidth(),bi.getHeight()));
                repaint();
                invalidate();
        }
        public void setData(RGBPixel[][] d) {
                bi=StereoToolkit.getBufferedImage(d);
                setPreferredSize(new Dimension(bi.getWidth(),bi.getHeight()));
                repaint();
                invalidate();
        }
}
