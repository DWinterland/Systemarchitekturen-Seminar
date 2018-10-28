package stereolab;
/* 
** Klasse:      RGBPixel
** Autor:       Christian Werner <cwerner@informatik.hu-berlin.de>
** Version:     1.0 (vom 22. April 2002)
**
** Beschreibung:
**
** Hilfskllasse für das Projekt "StereoLab". Beschreibt einen 24-Bit-RGB-Abtastwert.
*/


public class RGBPixel {
        public byte red;
        public byte green;
        public byte blue;
        
        public RGBPixel(byte r, byte g, byte b) {
                red=r;
                green=g;
                blue=b;
        }
        
        public RGBPixel(byte grey) {
                red=grey;
                green=grey;
                blue=grey;
        }
        
        public void set(byte r, byte g, byte b) {
                red=r;
                green=g;
                blue=b;
        }
        
        public void set(byte grey) {
                red=grey;
                green=grey;
                blue=grey;
        }
        
        public int[] getIntArray() {
                int[] array = new int[3];
                array[0]=StereoToolkit.unsignedByteToInt(red);
                array[1]=StereoToolkit.unsignedByteToInt(green);
                array[2]=StereoToolkit.unsignedByteToInt(blue);
                return(array);
        }
}
                
