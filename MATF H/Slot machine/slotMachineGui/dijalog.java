package slotMachineGui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

class dijalog extends JDialog implements ActionListener {
    JButton jbnOk;

    dijalog(SlotMachineGUI slotMachineGUI, String title, boolean modal, double dob){
        //super(slotMachineGUI, title, modal);
        setBackground(Color.black);
        this.setLayout( new FlowLayout(FlowLayout.CENTER));
        JPanel p1 = new JPanel(new FlowLayout(FlowLayout.CENTER));
        
        StringBuffer text = new StringBuffer();
        text.append("ČESTITAMO, DOBILI STE ");
        text.append(dob);
        
        JTextArea jtAreaAbout = new JTextArea(5, 21);
        jtAreaAbout.setText(text.toString());
        jtAreaAbout.setFont(new Font("Times New Roman", 1, 40));
        jtAreaAbout.setEditable(false);
        jtAreaAbout.setAlignmentX(CENTER_ALIGNMENT);
 
        p1.add(jtAreaAbout);
        //p1.setBackground(Color.red);
        getContentPane().add(p1, BorderLayout.CENTER);

        JPanel p2 = new JPanel(new FlowLayout(FlowLayout.CENTER));
        jbnOk = new JButton(" OK ");
        jbnOk.addActionListener(this);

        p2.add(jbnOk);
        getContentPane().add(p2, BorderLayout.SOUTH);

        setLocation(100,100);
        setResizable(false);

        addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e)
                {
                    Window aboutDialog = e.getWindow();
                    aboutDialog.dispose();
                }
            } 
        );

        pack();
    }

    public void actionPerformed(ActionEvent e)
    {
        if(e.getSource() == jbnOk)    {
            this.dispose();
        }
    }
}