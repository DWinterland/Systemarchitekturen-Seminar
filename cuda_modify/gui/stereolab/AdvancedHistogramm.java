package stereolab;
/* 
** Klasse:      AdvancedHistogramm
** Autor:       Christian Werner <cwerner@informatik.hu-berlin.de>
** Version:     1.0 (vom 22. April 2002)
**
** Beschreibung:
**
** Historgrammklasse für das Projekt "StereoLab". Ermöglicht verschiene Operationen mit "Profil"-Objekten.
** Dazu zählt auch die Überführung eines "Profil"-Objektes in eine Grauwertbildmatrix.
*/

import java.util.*;

public class AdvancedHistogramm {
        private int valid_min, invalid_min, global_min;
        private int valid_max, invalid_max, global_max;
        private int[] valid_data, invalid_data, global_data;
        private int valid_modalwert, invalid_modalwert, global_modalwert;
        private int global_norm, valid_norm;                                    // Normalisierungsfaktor (in Millionstel)
        private int global_values, valid_values;
                
        public AdvancedHistogramm(int[] fx, boolean[] valid) {
                int[] sortedArray = new int[fx.length];
                
                valid_min=fx[0];
                invalid_min=fx[0];
                global_min=fx[0];
                
                valid_max=fx[0];
                invalid_max=fx[0];
                global_max=fx[0];
                
                valid_modalwert=fx[0];
                invalid_modalwert=fx[0];
                global_modalwert=fx[0];
                                                
                for (int i=0;i<fx.length;i++) { // *_min und *_max finden

                       if (global_min > fx[i]) global_min=fx[i];
                       if (global_max < fx[i]) global_max=fx[i];

                        if (valid[i]) {
                                if (valid_min > fx[i]) valid_min=fx[i];
                                if (valid_max < fx[i]) valid_max=fx[i];
                        } else {
                                if (invalid_min > fx[i]) invalid_min=fx[i];
                                if (invalid_max < fx[i]) invalid_max=fx[i];
                        }
                }
                valid_data   = new int[valid_max   - valid_min   +1];
                invalid_data = new int[invalid_max - invalid_min +1];
                global_data  = new int[global_max  - global_min  +1];

                valid_modalwert=0;
                invalid_modalwert=0;
                global_modalwert=0;
                
                for (int i=0;i<fx.length;i++) { // Modalwerte finden und Histogramm "hochzählen"
                        global_data[fx[i]-global_min]++;
                        if (global_data[global_modalwert] < global_data[fx[i]-global_min]) global_modalwert=fx[i]-global_min;
                        if (valid[i]) {
                                valid_data[fx[i]-valid_min]++; 
                                if (valid_data[valid_modalwert] < valid_data[fx[i]-valid_min]) valid_modalwert=fx[i]-valid_min;
                        } else {
                                invalid_data[fx[i]-invalid_min]++;
                                if (invalid_data[invalid_modalwert] < invalid_data[fx[i]-invalid_min]) invalid_modalwert=fx[i]-invalid_min;
                        }
                }
                
                valid_values = (getValidMax()-getValidMin())+1;
                valid_norm = (1000000 * 255 + 500000) / valid_values;
                
                global_values = (getGlobalMax()-getGlobalMin())+1;
                global_norm = (1000000 * 255 + 500000) / global_values;
        }

        public byte[][] validNormalize(int[][] profile) {
                int zeilen = profile.length;
                int spalten = profile[0].length;

                byte[][] normal = new byte[zeilen][spalten];
                
//                System.out.println("Valid  -- getMax: "+getValidMax()+"   getMin: "+getValidMin()+"   Values: "+valid_values+"   Norm: "+valid_norm);

                for (int i=0; i<=zeilen-1;i++) {
                        for (int j=0; j<=spalten-1;j++) {
                                normal[i][j]=StereoToolkit.intToUnsignedByte(((profile[i][j]-getValidMin())*valid_norm+500000)/1000000);
                        }
                }

                return(normal);
        }

        public byte[][] globalNormalize(int[][] profile) {
                int zeilen = profile.length;
                int spalten = profile[0].length;

                byte[][] normal = new byte[zeilen][spalten];
                
                System.out.println("Global -- getMax: "+getGlobalMax()+"   getMin: "+getGlobalMin()+"   Values: "+global_values+"   Norm: "+global_norm);

                for (int i=0; i<=zeilen-1;i++) {
                        for (int j=0; j<=spalten-1;j++) {
                                normal[i][j]=StereoToolkit.intToUnsignedByte(((profile[i][j]-getGlobalMin())*global_norm+500000)/1000000);
                        }
                }
                return(normal);
        }
        public int globalNormalize(int profile) {
                return(((profile-getGlobalMin())*global_norm+500000)/1000000);
        }
 
        public int validNormalize(int profile) {
                if ((profile<valid_min) | (profile > valid_max)) return(255);
                return(((profile-getValidMin())*valid_norm+500000)/1000000);
        }

        public int[] getValidData() {
                return(valid_data);
        }

        public int[] getInvalidData() {
                return(invalid_data);
        }

        public int[] getGlobalData() {
                return(global_data);
        }

        public int getValidDataElement(int i) {        // Indizierung des Arrays wie bei GlobalData
                int delta=getValidMin()-getGlobalMin();
                int index=i-delta;
                if ((index<0) | (index>getValidData().length-1)) return(0); else return(valid_data[index]);
        }

        public int getInvalidDataElement(int i) {
                int delta=getInvalidMin()-getGlobalMin();
                int index=i-delta;
                if ((index<0) | (index>getInvalidData().length-1)) return(0); else return(invalid_data[index]);
        }

        public int getValidMin() {
                return(valid_min);
        }

        public int getInvalidMin() {
                return(invalid_min);
        }

        public int getGlobalMin() {
                return(global_min);
        }

        public int getValidMax() {
                return(valid_max);
        }

        public int getInvalidMax() {
                return(invalid_max);
        }

        public int getGlobalMax() {
                return(global_max);
        }

        public int getValidModalwert() {
                return(valid_modalwert);
        }
        
        public int getInvalidModalwert() {
                return(invalid_modalwert);
        }

        public int getGlobalModalwert() {
                return(global_modalwert);
        }

}    
