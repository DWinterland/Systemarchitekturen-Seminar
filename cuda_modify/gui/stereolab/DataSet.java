package stereolab;
/* 
** Klasse:      DataSet
** Autor:       Christian Werner <cwerner@informatik.hu-berlin.de>
** Version:     1.0 (vom 22. April 2002)
**
** Beschreibung:
**
** Zentrale Datenbklasse für das Projekt "StereoLab". Sie speichert Referenzen auf die beiden
** Stereobilder und auch eine Referenz auf das "Mutterobjekt" vom Typ StereoLab
*/


import java.io.*;
import java.util.*;

public class DataSet {

private Raw8bitGrayImage rImage;        // rechtes Bild
private Raw8bitGrayImage lImage;        // linkes Bild
private StereoLab s;                    // SteroLab-Panel
        
        
        public DataSet(StereoLab S) {
                lImage = null;
                rImage = null;
                s = S;
        }

        public Raw8bitGrayImage getLeftImage() {
                return(lImage);
        }
        
        public Raw8bitGrayImage getRightImage() {
                return(rImage);
        }

        public int getPresetZoom() {

                int percent;
                int xSize;              // sinnvollen Zoom-Wert festlegen:
                int ySize;
                
                if ((getLeftImage().getWidth()) > (getRightImage().getWidth())) {
                        xSize = getLeftImage().getWidth();
                } else {
                        xSize = getRightImage().getWidth();
                }
                
                if ((getLeftImage().getHeight()) > (getRightImage().getHeight())) {
                        ySize = getLeftImage().getHeight();
                } else {
                        ySize = getRightImage().getHeight();
                }

                percent=100;                                
                if((xSize>=200) | (ySize>=200)) if (xSize > ySize) percent=(10/(xSize/200))*10; else percent=(10/(ySize/200))*10;
                if (percent==0) percent=10;
                return(percent);
        }
        

        public void setLeftImage(File f, int h, int w) {
                lImage = new Raw8bitGrayImage(f, h, w);
                s.getStatusLine().setLeftDescription(lImage.getDescription());
        }
        
        public void setRightImage(File f, int h, int w) {
                rImage = new Raw8bitGrayImage(f, h, w);
                s.getStatusLine().setRightDescription(rImage.getDescription());
        }
        
        public boolean isImageSizeEqual() {
                if ((rImage.getHeight()==lImage.getHeight()) & (rImage.getHeight()==lImage.getHeight()))
                return(true); else
                return(false);
        }
}









