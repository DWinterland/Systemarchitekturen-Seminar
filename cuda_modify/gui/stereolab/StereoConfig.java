package stereolab;
/* 
** Klasse:      StereoConfig
** Autor:       Christian Werner <cwerner@informatik.hu-berlin.de>
** Version:     1.0 (vom 22. April 2002)
**
** Beschreibung:
**
** Konfigurations-Klasse für das Projekt "StereoLab". Hier werden z.B. die Pfade der FileChooser
** abgespeichert, so daß sich das Programm diese Pfade zwischen den Starts merkt (StereoLab.cfg)
*/


import java.lang.*;
import java.io.*;

public class StereoConfig implements Serializable {
        
        final static boolean AKTIV = false;
        final static boolean INAKTIV = true;

        private String zeilenBox;       // Wert für "Zeilen" im FileChooser
        private String spaltenBox;      // Wert für "Spalten" im FileChooser
        private File fcDir;             // Verzeichnis im FileChooser

        private String leftImage;
        private String rightImage;
        
        public String getLeftImage() {
			return leftImage;
		}

		public void setLeftImage(String leftImage) {
			this.leftImage = leftImage;
		}

		public String getRightImage() {
			return rightImage;
		}

		public void setRightImage(String rightImage) {
			this.rightImage = rightImage;
		}

		public StereoConfig() {         // Klassenvariablen init.
                zeilenBox="0";
                spaltenBox="0";
                fcDir=new File(".");
        }

        public String getStateZeilenBox() {
                return(zeilenBox);
        }

        public void setStateZeilenBox(String s) {
                zeilenBox=s;
        }

        public String getStateSpaltenBox() {
                return(spaltenBox);
        }

        public void setStateSpaltenBox(String s) {
                spaltenBox=s;
        }


        public File getStateFCDir() {
                return(fcDir);
        }

        public void setStateFCDir(File f) {
                fcDir=f;
        }


        public static StereoConfig load() {
                StereoConfig sc = new StereoConfig();
                
                try {
                        FileInputStream istream = new FileInputStream("StereoLab.cfg");
                        ObjectInputStream p = new ObjectInputStream(istream);
                        sc = (StereoConfig) p.readObject();
                        istream.close();
                } catch (Exception e) {
                        System.out.println("Fehler bei Laden der Konfiguration: "+e.getMessage());
                }
                
                return(sc);
        }

        public static void save(StereoConfig sc) {
                try {
                        FileOutputStream ostream = new FileOutputStream("StereoLab.cfg");
                        ObjectOutputStream p = new ObjectOutputStream(ostream);
                        p.writeObject(sc);
                        p.flush();
                        ostream.close();
                } catch (Exception e) {
                        System.out.println(e.getMessage());
                }

        }
}
