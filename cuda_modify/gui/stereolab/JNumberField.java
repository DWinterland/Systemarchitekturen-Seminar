package stereolab;
/* 
** Klasse:      JNumberField
** Autor:       Christian Werner <cwerner@informatik.hu-berlin.de>
** Version:     1.0 (vom 22. April 2002)
**
** Beschreibung:
**
** Hilfsklasse für das Projekt "StereoLab".
** JTextField, welches ausschließlich Zahlen enthalten kann. Die Länge ist auf vier Zeichen beschränkt.
*/



import java.awt.Toolkit;
import javax.swing.*;
import javax.swing.text.*;
import java.lang.*;


public class JNumberField extends JTextField {
     
     public JNumberField(int cols) {
             super(cols);
             setHorizontalAlignment(JTextField.RIGHT);
     }
 
     protected Document createDefaultModel() {
 	      return new NumberDocument();
     }
 
     public int getIntValue() {
        if (getText().equals("")) return(0); else
        return(Integer.parseInt(getText()));
     }

     static class NumberDocument extends PlainDocument {

              public void insertString(int offs, String str, AttributeSet a) 
 	          throws BadLocationException {
 
 	          if (str == null) {
 		      return;
 	          }
 	          char[] numbers = str.toCharArray();
 	          for (int i = 0; i < numbers.length; i++) {
 		      if ((!Character.isDigit(numbers[i])) || (getLength()>=4)) {
                        Toolkit.getDefaultToolkit().beep();                         
                        return;
                      }
 	          }
                  super.insertString(offs, new String(numbers), a);
 	      }
     }
 }

