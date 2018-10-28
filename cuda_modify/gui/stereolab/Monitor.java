package stereolab;
/* 
** Klasse:      Monitor
** Autor:       Christian Werner <cwerner@informatik.hu-berlin.de>
** Version:     1.0 (vom 22. April 2002)
**
** Beschreibung:
**
** Zentrale Monitorklasse für das Projekt "StereoLab".
** Von ihr erbt jeder Monitor, der aus dem Pull-Down-Menü des Hauptfensters auswählbar ist.
*/

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

public class Monitor extends JInternalFrame implements ActionListener {
        protected boolean isActive;
         
        private JMenuBar menubar;
        private JMenu datei, berechnung, ansicht;                      // Menubar mit allem was dazugehört...
        private JMenuItem beenden;
        
        private JMenuItem pack;
        private JPanel panel;
        private JPanel oPanel;
        private JPanel histogrammPanel;

        private JPanel uPanel;
        private JScrollPane scroll,scroll2;
      
        public Monitor(String St) {
                super(St,true,true,true,false);
                setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
                isActive=false;
                menubar = new JMenuBar();
                setJMenuBar(menubar);
                
                datei = new JMenu("Datei");
                berechnung = new JMenu("Berechnung");
                ansicht = new JMenu("Ansicht");
                
                menubar.add(datei);
                menubar.add(berechnung);
                menubar.add(ansicht);
                
                beenden = new JMenuItem("Monitor schließen");
                beenden.setActionCommand("exit");
                beenden.addActionListener(this);
                datei.add(beenden);
                
                pack = new JMenuItem("Fenstergröße anpassen");
                pack.setActionCommand("pack");
                pack.addActionListener(this);
                pack.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0));

                ansicht.add(pack);
                
                panel = new JPanel();
                oPanel = new JPanel();
                oPanel.setAlignmentX(LEFT_ALIGNMENT);
                oPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
                histogrammPanel = new JPanel();
                histogrammPanel.setAlignmentX(CENTER_ALIGNMENT);
                histogrammPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
//                histogrammPanel.setBorder(BorderFactory.createTitledBorder("Histogramm Panel"));
                uPanel = new JPanel();
                scroll = new JScrollPane(uPanel);
                scroll2= new JScrollPane(histogrammPanel,JScrollPane.VERTICAL_SCROLLBAR_NEVER,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
                
                panel.setLayout(new BorderLayout());
                panel.add(scroll,BorderLayout.CENTER);
                getContentPane().add("Center", panel);
        }

        public void addButton(JComponent jc) {
                if (! java.util.Arrays.asList(panel.getComponents()).contains(oPanel)) panel.add(oPanel,BorderLayout.NORTH);
                oPanel.add(jc);
        }
        
        public void addContent(JComponent jc) {
                uPanel.add(jc);
        }
       
        public void addBerechnung(Component c, int i) {
                berechnung.add(c,i);
        }
        
        public void addAnsicht(Component c,int i) {
                ansicht.add(c,i);
        }
        
        public void addDatei(Component c,int i) {
                datei.add(c,i);
        }
        
        public void addMenu(Component c,int i) {
                menubar.add(c,i);
        }
        
        public void updateHistogramm(AdvancedHistogrammPanel h) {
                histogrammPanel.removeAll();
                histogrammPanel.add(h);
                panel.add(scroll2,BorderLayout.SOUTH);
                revalidate();
                repaint();
        }
        
        public void removeHistogramm() {
                histogrammPanel.removeAll();
                panel.remove(scroll2);
                revalidate();
        }
        
        public void actionPerformed(ActionEvent event) { // wird bei Action-Events aufgerufen.

                String cmd = event.getActionCommand();
                if (cmd.equals("pack")) {
                        pack();
                }
                if (cmd.equals("exit")) {
                        doDefaultCloseAction();
                }
        }

        public void setClosable(boolean b) {
                super.setClosable(b);
                beenden.setEnabled(b);
        }
        
        public boolean isActive() {
                return(isActive);
        }
        
}
