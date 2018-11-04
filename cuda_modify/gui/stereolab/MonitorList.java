package stereolab;
/* 
** Klasse:      MonitorList
** Autor:       Christian Werner <cwerner@informatik.hu-berlin.de>
** Version:     1.0 (vom 22. April 2002)
**
** Beschreibung:
**
** Hilfsklasse für das Projekt "StereoLab". Sie speichert alle offenen Monitore als ArrayList
** und bietet aus Funktionen zu schließen von Monitor-Fenstern.
*/

import java.util.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;

public class MonitorList {

        private ArrayList ltList, differencerList, rangerList;
        private StereoLab s;
        private JDesktopPane desk;
        
        public MonitorList(StereoLab S, JDesktopPane DESK) {
                ltList = new ArrayList();
                differencerList = new ArrayList();
                rangerList = new ArrayList();
                s=S;
                desk=DESK;
        }
        
        public void createLT() {
                LineTracker lt = new LineTracker(s);
                lt.addInternalFrameListener(new InternalFrameAdapter() {
                                                                public void internalFrameClosing(InternalFrameEvent e) {
                                                                        ltList.remove(e.getSource());
                                                                }
                });
                ImageIcon i = new ImageIcon(MonitorList.class.getResource("sub_adv.gif"));
                lt.setFrameIcon(i);
                lt.setSize(200,200);
                lt.setLocation(100,100);
                desk.add(lt);
                lt.pack();
                lt.setVisible(true);
                ltList.add(lt);
        }

        public void createDifferencer() {
                Differencer d = new Differencer(s);
                d.addInternalFrameListener(new InternalFrameAdapter() {
                                                                public void internalFrameClosing(InternalFrameEvent e) {
                                                                        differencerList.remove(e.getSource());
                                                                }
                });
                ImageIcon i = new ImageIcon(MonitorList.class.getResource("sub_adv.gif"));
                d.setFrameIcon(i);
                d.setSize(200,200);
                d.setLocation(100,100);
                desk.add(d);
                d.pack();
                d.setVisible(true);
                differencerList.add(d);
        }

        public void createRanger() {
                Ranger k = new Ranger(s);
                k.addInternalFrameListener(new InternalFrameAdapter() {
                                                                public void internalFrameClosing(InternalFrameEvent e) {
                                                                        rangerList.remove(e.getSource());
                                                                }
                });
                ImageIcon i = new ImageIcon(MonitorList.class.getResource("sub_adv.gif"));
                k.setFrameIcon(i);
                k.setSize(200,200);
                k.setLocation(100,100);
                desk.add(k);
                k.pack();
                k.setVisible(true);
                rangerList.add(k);
        }

        public ArrayList getLineTrackerList(){
                return(ltList);
        }
        
        public int monitorsOpen() {
                return(ltList.size()+differencerList.size()+rangerList.size());
        }
        
        public void closeLT(LineTracker lt) {
                ltList.remove(lt);
                lt.dispose();
        }
        
        public void closeDifferencer(Differencer d) {
                differencerList.remove(d);
                d.dispose();
        }
        
        public void closeRanger(Ranger r) {
                rangerList.remove(r);
                r.dispose();
        }
        
        public void closeAll() {
                while (0<ltList.size()) {
                         LineTracker lt = (LineTracker) ltList.get(0);
                         closeLT(lt);
                }
                while (0<differencerList.size()) {
                         Differencer d = (Differencer) differencerList.get(0);
                         closeDifferencer(d);
                }
                while (0<rangerList.size()) {
                         Ranger r = (Ranger) rangerList.get(0);
                         closeRanger(r);
                 }
        }
        
        public int activeMonitors() {
                int temp=0;

                for (int i=0; i<ltList.size(); i++) {
                        LineTracker lt = (LineTracker) ltList.get(i);
                        if (lt.isActive()) temp++;
                }
                for (int i=0; i<differencerList.size(); i++) {
                         Differencer d = (Differencer) differencerList.get(i);
                         if (d.isActive()) temp++;
                }
                for (int i=0; i<rangerList.size(); i++) {
                         Ranger r = (Ranger) rangerList.get(i);                        
                         if (r.isActive()) temp++;
                }
                return(temp);
        }
}
