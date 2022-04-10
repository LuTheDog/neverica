package slotMachineGui;
 
import java.awt.*;
import javax.swing.*;
import java.util.Random;
import java.util.ArrayList;
import javax.swing.border.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import slotMachineGui.dijalog.*;
import javax.swing.GroupLayout.Alignment;
import java.util.HashMap;
 


public class SlotMachineGUI {
	
	
    private Timer timer;
    private JButton btnSpin;
    private JFrame frmFrame;
    private JLabel lblCredits, lblReel0_0, lblReel0_1, lblReel0_2, lblStatus;
    private JPanel pnlReels, pnlReel0_0, pnlReel0_1, pnlReel0_2;
    private double credits = 100.0, bet = 15.0, uplata=100.0;
    private int[] reel = {0, 5, 12};
    private int[][] lines = {{0, 0, 0}, {1, 1, 1}, {2, 2, 2}, {0, 1, 2}, {2, 1, 0}};
    private int[] reelDelay = {10, 20, 37};
    private HashMap<String, Double> multi = new HashMap<String, Double>();
    private ArrayList<ImageIcon> reel0Symbols = new ArrayList<ImageIcon>();
    private ArrayList<ImageIcon> reel1Symbols = new ArrayList<ImageIcon>();
    private ArrayList<ImageIcon> reel2Symbols = new ArrayList<ImageIcon>();
    private JPanel pnlReel1_0;
    private JLabel lblReel1_0;
    private JPanel pnlReel1_1;
    private JLabel lblReel1_1;
    private JLabel lblReel1_2;
    private JPanel pnlReel2_0;
    private JLabel lblReel2_0;
    private JPanel pnlReel2_1;
    private JLabel lblReel2_1;
    private JPanel pnlReel2_2;
    private JLabel lblReel2_2;
    private JPanel pnlReel1_2;
    
    private JPanel donjiPanel;
    private JPanel donjiPanel1;
    private JPanel donjiPanel2;
    private JPanel donjiPanel3;
    private JPanel donjiPanel4;
    private JPanel donjiPanel5;
    private JPanel donjiPanel6;
    
    private JPanel donjiPanelGore;
    private JPanel donjiPanelDole;

    private JButton strGore1;
    private JButton strDole1; 
    private JButton strGore2;
    private JButton strDole2;
    public JButton uplati;
    private JCheckBox sim;
    private JLabel labelaDole1;
    private JLabel labelaDole2;
    private JLabel rtp;
    
    private JDialog poruka;
    
    private Timer lightUpTimer;
    private int lineLightUpCounter;
    private boolean[] winningLine = new boolean[lines.length];
    
    private JPanel[][] panelMatrix;
    private JLabel[][] labelMatrix;
    private ArrayList<ArrayList<ImageIcon>> symbolMatrix;
    private int reelSize = 30;
    
    private boolean simulation = false;
    private double simSpent = 0, simWon = 0;
    private int simCount = 5_000_000;
    private int simWins = 0;
    
    private HashMap<String, java.awt.Color> symbolColors = new HashMap<String, java.awt.Color>();
    
    public SlotMachineGUI() {
    	initMulti();
        createForm();
        loadImages();
        addFields();
        addButtons();
        layoutFrame();
        layoutReels();
        layoutOther();
        initPanelMatrix();
        initLabelMatrix();
        initSymbolMatrix();
        initTimer();
        initSymbolColors();
        initLightUpTimer();
        setReelIcons();
    }
     
    private int numSpins[];
    
    private void initLightUpTimer() {
    	lightUpTimer = new Timer(500, new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				setReelIcons();
				int loopCounter = 0;
				while(!winningLine[lineLightUpCounter] && loopCounter < winningLine.length) {
					lineLightUpCounter = (lineLightUpCounter + 1) % winningLine.length;
					loopCounter ++; // protiv infinite loop
				}
				if (loopCounter < winningLine.length) { //ovde sam dodao
					String first, second, third;
					int[] i = lines[lineLightUpCounter];
					first = reel0Symbols.get((reel[0] + i[0]) % reel0Symbols.size()).getDescription();
		    		second = reel1Symbols.get((reel[1] + i[1]) % reel1Symbols.size()).getDescription();
		    		third  = reel2Symbols.get((reel[2] + i[2]) % reel2Symbols.size()).getDescription();
		    		boolean[] bools = new boolean[3];
		    		if (first == second && second == third) {
		    	
						bools[0] = bools[1] = bools[2] = true;
		    	
		    		} else if (((first == second && i[0] == i[1]) || (second == third && i[1] == i[2]))) {
		    			
		    			if (first == second) bools[0] = bools[1] = true;
		    			else bools[1] = bools[2] = true;
		    		}
		    		lightBorders(i, bools);
		    		lineLightUpCounter = (lineLightUpCounter + 1) % winningLine.length;
				}
			}
		});
    }
    
    private void initTimer() {
    	timer = new Timer(75, new ActionListener() {
			int skips = 0;
			int counter = 0;
			@Override
			public void actionPerformed(ActionEvent e) {
	    		if (skips == 0 || counter % skips == 0) {
					for (int j = 0; j < reel.length; j++) {
		    			if (numSpins[j] > 0) {
		    				numSpins[j]--;
		    				reel[j] = (reel[j] - 1 + symbolMatrix.get(j).size()) % symbolMatrix.get(j).size();
		    			}
		    		}
				}
	    		counter++;
	    		boolean getOut = true;
	    		setReelIcons();
	    		for (int i = 0; i < numSpins.length; i++) {
	    			if (numSpins[i] == 0) {
	    				skips = skips < 2 ? skips + 1 : 2;
	    			}
	    			if (numSpins[i] > 0) getOut = false;
	    		}
	    		if (getOut) {
    				timer.stop();
    				matchCheck();
    				btnSpin.setEnabled(true);
    				
    				lineLightUpCounter = 0; // counter za animaciju linija
    				lightUpTimer.start(); // timer za animaciju linija
    				
    				skips = counter = 0;
    			}
			}
		} );
    }
    
    private void initMulti() {
    	multi.put("Sljiva", 1.9);
    	multi.put("Dunja", 2.1);
    	multi.put("Kajsija", 2.3);
    	multi.put("Grozdje", 2.5);
    	multi.put("Visnja", 2.7);
    	multi.put("Kazan", 12.0);
    	multi.put("Pljoska", 15.0);
    	multi.put("Deda", 60.0);
    	multi.put("Bure", 0.0);
    }
    
    private void initPanelMatrix() {
    	 panelMatrix = new JPanel[][] {{pnlReel0_0, pnlReel1_0, pnlReel2_0}, 
					{pnlReel0_1, pnlReel1_1, pnlReel2_1}, 
					{pnlReel0_2, pnlReel1_2, pnlReel2_2}};
    }
    
    private void initLabelMatrix() {
   	 labelMatrix = new JLabel[][] {{lblReel0_0, lblReel1_0, lblReel2_0}, 
					{lblReel0_1, lblReel1_1, lblReel2_1}, 
					{lblReel0_2, lblReel1_2, lblReel2_2}};
    }
    
    private void initSymbolMatrix() {
    	symbolMatrix = new ArrayList<ArrayList<ImageIcon>>();
    	symbolMatrix.add(reel0Symbols);
    	symbolMatrix.add(reel1Symbols);
    	symbolMatrix.add(reel2Symbols);	
    }
    
    private void initSymbolColors() {
    	symbolColors.put("Sljiva", new Color(250, 228, 105));
    	symbolColors.put("Dunja", new Color(250, 228, 105));
    	symbolColors.put("Kajsija", new Color(250, 228, 105));
    	symbolColors.put("Grozdje", new Color(250, 228, 105));
    	symbolColors.put("Visnja", new Color(250, 228, 105));
    	symbolColors.put("Kazan", new Color(250, 187, 105));
    	symbolColors.put("Pljoska", new Color(250, 187, 105));
    	symbolColors.put("Deda", new Color(250, 149, 105));
    	symbolColors.put("Bure", new Color(204, 94, 255));
    	
    }
    
    /** Creates the JFrame and Panels. */
    private void createForm() {
        
        frmFrame = new JFrame();
        frmFrame.getContentPane().setMaximumSize(new Dimension(1000, 1000));
        frmFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frmFrame.setTitle("Radza slots");
        //frmFrame.setSize(1000,900);
        frmFrame.setVisible(true);
        frmFrame.setResizable(false);
         
        pnlReels = new JPanel();
        pnlReels.setMaximumSize(new Dimension(500, 32767));
        pnlReels.setBorder(BorderFactory.createEtchedBorder());
         
        pnlReel0_0 = new JPanel();
        pnlReel0_0.setBackground(new Color(255, 215, 0));
        pnlReel0_0.setBorder(new SoftBevelBorder(BevelBorder.LOWERED));
        pnlReel0_1 = new JPanel();
        pnlReel0_1.setBackground(new Color(255, 216, 0));
        pnlReel0_1.setBorder(new SoftBevelBorder(BevelBorder.LOWERED));
        pnlReel0_2 = new JPanel();
        pnlReel0_2.setBackground(new java.awt.Color(255, 215, 0));
        pnlReel0_2.setBorder(new SoftBevelBorder(BevelBorder.LOWERED));
         
    }
     
    /** Adds labels to the form. */
    private void addFields() {
         
        lblReel0_0 = new JLabel();
        lblReel0_1 = new JLabel();
        lblReel0_2 = new JLabel();
        lblCredits = new JLabel();
        lblCredits.setText("Credits: "+credits);
        lblCredits.setFont(new Font("Comic Sans", Font.PLAIN,24));
        lblStatus = new JLabel();
        lblStatus.setBackground(new Color(255, 255, 255));
        lblStatus.setFont(new Font("Arial", 1, 14));
        lblStatus.setHorizontalAlignment(SwingConstants.CENTER);
        lblStatus.setText(" ");
        
        lblReel0_0.setIcon(reel0Symbols.get(reel[0]));
        lblReel0_1.setIcon(reel1Symbols.get(reel[1]));
        lblReel0_2.setIcon(reel2Symbols.get(reel[2]));
        
    }
     
    /** Adds buttons to the form. */
    private void addButtons() {
    	
        btnSpin = new JButton();
        btnSpin.setFont(new Font("Tahoma", Font.PLAIN, 32));
        btnSpin.setBackground(new Color(50, 255, 50));
        btnSpin.setText("Zavrti");
        btnSpin.setToolTipText("Zavrti me");
        btnSpin.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        btnSpin.setInheritsPopupMenu(true);
        btnSpin.setMaximumSize(new Dimension(200, 50));
        btnSpin.setMinimumSize(new Dimension(200, 50));
        btnSpin.addActionListener(new SpinHandler());
    }
     
    /** Lays out the frame. */
    private void layoutFrame() {
        
         
        GroupLayout frameLayout = new GroupLayout(frmFrame.getContentPane());
        frameLayout.setHorizontalGroup(
        	frameLayout.createParallelGroup(Alignment.LEADING)
        		.addGap(0, 446, Short.MAX_VALUE)
        );
        frameLayout.setVerticalGroup(
        	frameLayout.createParallelGroup(Alignment.LEADING)
        		.addGap(0, 317, Short.MAX_VALUE)
        );
        frmFrame.getContentPane().setLayout(frameLayout);
    }
     // komentar
    /** Lays out the panels and reels.  */
    private void layoutReels() {
        
        pnlReel1_1 = new JPanel();
        pnlReel1_1.setBorder(new SoftBevelBorder(BevelBorder.LOWERED));
        pnlReel1_1.setBackground(new Color(255, 216, 0));
        
        lblReel1_1 = new JLabel();
        GroupLayout gl_pnlReel1_1 = new GroupLayout(pnlReel1_1);
        gl_pnlReel1_1.setHorizontalGroup(
        	gl_pnlReel1_1.createParallelGroup(Alignment.LEADING)
        		.addGap(0, 26, Short.MAX_VALUE)
        		.addGroup(gl_pnlReel1_1.createSequentialGroup()
        			.addContainerGap()
        			.addComponent(lblReel1_1)
        			.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        gl_pnlReel1_1.setVerticalGroup(
        	gl_pnlReel1_1.createParallelGroup(Alignment.LEADING)
        		.addGap(0, 28, Short.MAX_VALUE)
        		.addGroup(gl_pnlReel1_1.createSequentialGroup()
        			.addContainerGap()
        			.addComponent(lblReel1_1)
        			.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlReel1_1.setLayout(gl_pnlReel1_1);
        
        pnlReel2_2 = new JPanel();
        pnlReel2_2.setBorder(new SoftBevelBorder(BevelBorder.LOWERED));
        pnlReel2_2.setBackground(new Color(255, 216, 0));
        
        lblReel2_2 = new JLabel();
        GroupLayout gl_pnlReel2_2 = new GroupLayout(pnlReel2_2);
        gl_pnlReel2_2.setHorizontalGroup(
        	gl_pnlReel2_2.createParallelGroup(Alignment.LEADING)
        		.addGap(0, 26, Short.MAX_VALUE)
        		.addGroup(gl_pnlReel2_2.createSequentialGroup()
        			.addContainerGap()
        			.addComponent(lblReel2_2)
        			.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        gl_pnlReel2_2.setVerticalGroup(
        	gl_pnlReel2_2.createParallelGroup(Alignment.LEADING)
        		.addGap(0, 28, Short.MAX_VALUE)
        		.addGroup(gl_pnlReel2_2.createSequentialGroup()
        			.addContainerGap()
        			.addComponent(lblReel2_2)
        			.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlReel2_2.setLayout(gl_pnlReel2_2);
         
        GroupLayout gl_pnlReel0_0 = new GroupLayout(pnlReel0_0);
        pnlReel0_0.setLayout(gl_pnlReel0_0);
        gl_pnlReel0_0.setHorizontalGroup(
        gl_pnlReel0_0.createParallelGroup(GroupLayout.Alignment.LEADING)
        .addGroup(gl_pnlReel0_0.createSequentialGroup()
        .addContainerGap()
        .addComponent(lblReel0_0)
        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        gl_pnlReel0_0.setVerticalGroup(
        gl_pnlReel0_0.createParallelGroup(GroupLayout.Alignment.LEADING)
        .addGroup(gl_pnlReel0_0.createSequentialGroup()
        .addContainerGap()
        .addComponent(lblReel0_0)
        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
         
        GroupLayout gl_pnlReel0_1 = new GroupLayout(pnlReel0_1);
        pnlReel0_1.setLayout(gl_pnlReel0_1);
        gl_pnlReel0_1.setHorizontalGroup(
        gl_pnlReel0_1.createParallelGroup(GroupLayout.Alignment.LEADING)
        .addGroup(gl_pnlReel0_1.createSequentialGroup()
        .addContainerGap()
        .addComponent(lblReel0_1)
        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        gl_pnlReel0_1.setVerticalGroup(
        gl_pnlReel0_1.createParallelGroup(GroupLayout.Alignment.LEADING)
        .addGroup(gl_pnlReel0_1.createSequentialGroup()
        .addContainerGap()
        .addComponent(lblReel0_1)
        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
         
        GroupLayout gl_pnlReel0_2 = new GroupLayout(pnlReel0_2);
        pnlReel0_2.setLayout(gl_pnlReel0_2);
        gl_pnlReel0_2.setHorizontalGroup(
        gl_pnlReel0_2.createParallelGroup(GroupLayout.Alignment.LEADING)
        .addGroup(gl_pnlReel0_2.createSequentialGroup()
        .addContainerGap()
        .addComponent(lblReel0_2)
        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        gl_pnlReel0_2.setVerticalGroup(
        gl_pnlReel0_2.createParallelGroup(GroupLayout.Alignment.LEADING)
        .addGroup(gl_pnlReel0_2.createSequentialGroup()
        .addContainerGap()
        .addComponent(lblReel0_2)
        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlReels.setLayout(new GridLayout(0, 3, 0, 0));
        pnlReels.add(pnlReel0_0);
        pnlReels.add(pnlReel0_1);
        pnlReels.add(pnlReel0_2);
        
        pnlReel1_0 = new JPanel();
        pnlReel1_0.setBorder(new SoftBevelBorder(BevelBorder.LOWERED));
        pnlReel1_0.setBackground(new Color(255, 216, 0));
        
        lblReel1_0 = new JLabel();
        GroupLayout gl_pnlReel1_0 = new GroupLayout(pnlReel1_0);
        gl_pnlReel1_0.setHorizontalGroup(
        	gl_pnlReel1_0.createParallelGroup(Alignment.LEADING)
        		.addGap(0, 26, Short.MAX_VALUE)
        		.addGroup(gl_pnlReel1_0.createSequentialGroup()
        			.addContainerGap()
        			.addComponent(lblReel1_0)
        			.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        gl_pnlReel1_0.setVerticalGroup(
        	gl_pnlReel1_0.createParallelGroup(Alignment.LEADING)
        		.addGap(0, 28, Short.MAX_VALUE)
        		.addGroup(gl_pnlReel1_0.createSequentialGroup()
        			.addContainerGap()
        			.addComponent(lblReel1_0)
        			.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlReel1_0.setLayout(gl_pnlReel1_0);
        pnlReels.add(pnlReel1_0);
        pnlReels.add(pnlReel1_1);
        
        pnlReel1_2 = new JPanel();
        pnlReel1_2.setBorder(new SoftBevelBorder(BevelBorder.LOWERED));
        pnlReel1_2.setBackground(new Color(255, 216, 0));
        
        lblReel1_2 = new JLabel();
        GroupLayout gl_pnlReel1_2 = new GroupLayout(pnlReel1_2);
        gl_pnlReel1_2.setHorizontalGroup(
        	gl_pnlReel1_2.createParallelGroup(Alignment.LEADING)
        		.addGap(0, 26, Short.MAX_VALUE)
        		.addGroup(gl_pnlReel1_2.createSequentialGroup()
        			.addContainerGap()
        			.addComponent(lblReel1_2)
        			.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        gl_pnlReel1_2.setVerticalGroup(
        	gl_pnlReel1_2.createParallelGroup(Alignment.LEADING)
        		.addGap(0, 28, Short.MAX_VALUE)
        		.addGroup(gl_pnlReel1_2.createSequentialGroup()
        			.addContainerGap()
        			.addComponent(lblReel1_2)
        			.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlReel1_2.setLayout(gl_pnlReel1_2);
        pnlReels.add(pnlReel1_2);
        
        pnlReel2_0 = new JPanel();
        pnlReel2_0.setBorder(new SoftBevelBorder(BevelBorder.LOWERED));
        pnlReel2_0.setBackground(new Color(255, 216, 0));
        
        lblReel2_0 = new JLabel();
        GroupLayout gl_pnlReel2_0 = new GroupLayout(pnlReel2_0);
        gl_pnlReel2_0.setHorizontalGroup(
        	gl_pnlReel2_0.createParallelGroup(Alignment.LEADING)
        		.addGap(0, 26, Short.MAX_VALUE)
        		.addGroup(gl_pnlReel2_0.createSequentialGroup()
        			.addContainerGap()
        			.addComponent(lblReel2_0)
        			.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        gl_pnlReel2_0.setVerticalGroup(
        	gl_pnlReel2_0.createParallelGroup(Alignment.LEADING)
        		.addGap(0, 28, Short.MAX_VALUE)
        		.addGroup(gl_pnlReel2_0.createSequentialGroup()
        			.addContainerGap()
        			.addComponent(lblReel2_0)
        			.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlReel2_0.setLayout(gl_pnlReel2_0);
        pnlReels.add(pnlReel2_0);
        
        pnlReel2_1 = new JPanel();
        pnlReel2_1.setBorder(new SoftBevelBorder(BevelBorder.LOWERED));
        pnlReel2_1.setBackground(new Color(255, 216, 0));
        
        lblReel2_1 = new JLabel();
        GroupLayout gl_pnlReel2_1 = new GroupLayout(pnlReel2_1);
        gl_pnlReel2_1.setHorizontalGroup(
        	gl_pnlReel2_1.createParallelGroup(Alignment.LEADING)
        		.addGap(0, 26, Short.MAX_VALUE)
        		.addGroup(gl_pnlReel2_1.createSequentialGroup()
        			.addContainerGap()
        			.addComponent(lblReel2_1)
        			.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        gl_pnlReel2_1.setVerticalGroup(
        	gl_pnlReel2_1.createParallelGroup(Alignment.LEADING)
        		.addGap(0, 28, Short.MAX_VALUE)
        		.addGroup(gl_pnlReel2_1.createSequentialGroup()
        			.addContainerGap()
        			.addComponent(lblReel2_1)
        			.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        //----------------------------------------------------------------------------------------
        pnlReel2_1.setLayout(gl_pnlReel2_1);
        pnlReels.add(pnlReel2_1);
        pnlReels.add(pnlReel2_2);
        donjiPanel = new JPanel();
        donjiPanel1 = new JPanel();
        donjiPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        donjiPanelGore= new JPanel();
        donjiPanelGore.setLayout(new FlowLayout(FlowLayout.TRAILING,50,0));
        donjiPanelDole = new JPanel();
        donjiPanelDole.setLayout(new FlowLayout(FlowLayout.LEFT,50,0));
        
        labelaDole1 = new JLabel("Ulog: " + bet);
        labelaDole1.setFont(new Font("Comic Sans",Font.PLAIN,24));
        labelaDole2 = new JLabel("Uplata: " + uplata);
        labelaDole2.setFont(new Font("Comic Sans",Font.PLAIN,24));
        rtp = new JLabel();
        rtp.setVisible(false);
        
        
        strGore1 = new JButton();
        strGore1.setIcon(new ImageIcon("arrowup.png"));
        strGore1.addActionListener(new KreditiGore()); // nova klasa ovde!
        strDole1 = new JButton();
        strDole1.setBounds(0,0,60,20);
        strDole1.setIcon(new ImageIcon("arrowdown.png"));
        strDole1.addActionListener(new KreditiDole()); // nova klasa ovde!
        strGore1.setBounds(0,50,60,20);
        
        uplati=new JButton();
        uplati.setText("   Uplati   ");
        uplati.setFont(new Font("Arial", Font.BOLD, 20));
        uplati.addActionListener(new IzvrsiUplatu());
        
	    strGore2 = new JButton();
	    strGore2.setIcon(new ImageIcon("arrowup.png"));
	    strGore2.addActionListener(new UplataGore()); // nova klasa ovde!
	    strDole2 = new JButton();
	    strDole2.setBounds(0,0,60,20);
	    strDole2.setIcon(new ImageIcon("arrowdown.png"));
	    strDole2.addActionListener(new UplataDole()); // nova klasa ovde!
	    strGore2.setBounds(0,50,60,20);
      

        //donjiPanel1.setBounds(0,0,200,100);
        //donjiPanel1.setBackground(Color.white);
        donjiPanel2 = new JPanel();
        donjiPanel3 = new JPanel();
        donjiPanel4 = new JPanel();
        donjiPanel5 = new JPanel();
        donjiPanel6 = new JPanel();
        sim = new JCheckBox();
        sim.setText("Simulacija");
        sim.setFocusable(false);
        sim.setFont(new Font("Consolas",Font.PLAIN,24));
        //donjiPanel2.setBounds(20,0,100,50);
        donjiPanel3 = new JPanel();
        donjiPanel.setBounds(0,800,750,500);

        donjiPanel.add(donjiPanelGore);
        donjiPanel.add(donjiPanelDole);
        donjiPanelGore.add(donjiPanel1);
        donjiPanelGore.add(donjiPanel2);
        donjiPanelGore.add(donjiPanel3);
        donjiPanel1.add(strGore1);
        donjiPanel1.add(labelaDole1);
        donjiPanel1.add(strDole1);
        donjiPanel2.add(lblCredits);
        donjiPanel3.add(sim);
        

        donjiPanelDole.add(donjiPanel4);
        donjiPanelDole.add(donjiPanel5);
        donjiPanelDole.add(donjiPanel6);
        donjiPanel4.add(strGore2);
        donjiPanel4.add(labelaDole2);
        donjiPanel4.add(strDole2);
        donjiPanel5.add(uplati);
        donjiPanel6.add(rtp);
        frmFrame.getContentPane().add(donjiPanel);
        

        
        }
     
    /** lays out the remaining labels, check boxes, progress bars, etc. */
    private void layoutOther() {
         
        GroupLayout layout = new GroupLayout(frmFrame.getContentPane());
        layout.setHorizontalGroup(
        	layout.createParallelGroup(Alignment.LEADING)
        		.addGroup(layout.createSequentialGroup()
        			.addContainerGap()
        			.addGroup(layout.createParallelGroup(Alignment.LEADING)
        				.addComponent(pnlReels, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 426, Short.MAX_VALUE)
        				.addComponent(lblStatus, GroupLayout.DEFAULT_SIZE, 426, Short.MAX_VALUE)
        				.addGroup(layout.createSequentialGroup()
        					.addGap(2)
        					.addComponent(btnSpin, 200, 424, Short.MAX_VALUE)
        					.addGap(0)))
        			.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
        	layout.createParallelGroup(Alignment.LEADING)
        		.addGroup(layout.createSequentialGroup()
        			.addContainerGap()
        			.addComponent(pnlReels, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        			.addComponent(btnSpin, GroupLayout.PREFERRED_SIZE, 56, GroupLayout.PREFERRED_SIZE)
        			.addGap(33)
        			.addComponent(lblStatus, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE)
        			.addGap(81))
        );
        frmFrame.getContentPane().setLayout(layout);
         
        frmFrame.pack();
         
    }
     

    class KreditiGore implements ActionListener{
    	public void actionPerformed(ActionEvent event) {
            bet+=5;
            strDole1.setEnabled(true);
            labelaDole1.setText("Ulog: " + bet);
            labelaDole1.setFont(new Font("Comic Sans", Font.PLAIN,24));
        }
    }
    class KreditiDole implements ActionListener{
    	public void actionPerformed(ActionEvent event) {
    		if(bet==10){ strDole1.setEnabled(false);}
    		bet-=5;
           
            labelaDole1.setText("Ulog: " + bet);
            labelaDole1.setFont(new Font("Comic Sans", Font.PLAIN,24));
        }
    }
    
    class UplataGore implements ActionListener{
    	public void actionPerformed(ActionEvent event) {
            uplata+=5;
            strDole2.setEnabled(true);
            labelaDole2.setText("Uplata: " + uplata);
            labelaDole2	.setFont(new Font("Comic Sans", Font.PLAIN,24));
        }
    }
    class UplataDole implements ActionListener{
    	public void actionPerformed(ActionEvent event) {
    		if(uplata==10){ strDole2.setEnabled(false);}
    		uplata-=5;
           
            labelaDole2.setText("Uplata: " + uplata);
            labelaDole2.setFont(new Font("Comic Sans", Font.PLAIN,24));
        }
    }
     
    /** Performs action when Spin button is clicked. */
    class SpinHandler implements ActionListener {
    	public void actionPerformed(ActionEvent event) {
    		lightUpTimer.stop();
    		//setReelIcons();
    		btnSpin.setEnabled(false);
            for (int i = 0; i < panelMatrix.length; i++) {
                for (int j = 0; j < panelMatrix[i].length; j++) {
                    panelMatrix[i][j].setBackground(new java.awt.Color(255, 215, 0));
                }
            }
            if (sim.isSelected()) {
            	getRTP();
            } else {
            	genReelNumbers();
            }
        }
    }
    
    class IzvrsiUplatu implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			credits+=uplata;
			lblCredits.setText("Credits: " + (credits));
		}
    	
    }
     
    /** Generates the 3 reel numbers. */
    public void genReelNumbers() {
        Random rand = new Random();
        int[] randNums = new int[symbolMatrix.size()];
        if (!simulation) {
        	for (int i = 0; i < symbolMatrix.size(); i++) {
        		randNums[i] = rand.nextInt(symbolMatrix.get(i).size());
        	}
    		spin(randNums);
        } else {
        	for (int j = 0; j < simCount; j++) {
        		for (int i = 0; i < symbolMatrix.size(); i++) {
            		randNums[i] = rand.nextInt(symbolMatrix.get(i).size());
            	}
        		simSpin(randNums);
        	}
    	}
    }
    
    private void setReelIcons() {
    	for (int j = 0; j < labelMatrix.length; j++) {
			for (int k = 0; k < labelMatrix.length; k++) {
				labelMatrix[j][k].setIcon(symbolMatrix.get(j).get((reel[j] + k) % symbolMatrix.get(j).size()));
				panelMatrix[j][k].setBackground(symbolColors.get(symbolMatrix.get(j).get((reel[j] + k) % symbolMatrix.get(j).size()).getDescription()));
			}
		}
    }
    
    
    /** Sets the reels icon based on loaded image in images ArrayList. */
    public void spin(int[] randNums) {
    	timer.start();
    	credits -= bet;
    	lblCredits.setText("Credits: " + (credits)); // deduct bet amount from available credits.
    	numSpins = new int[randNums.length];
    	for (int i = 0; i < randNums.length; i++) {
    		numSpins[i] = reelSize + randNums[i] + reelDelay[i];
    	}	
    }
    
    public void simSpin(int[] randNums) {
    	for (int i = 0; i < reel.length; i++) {
    		reel[i] = reel[i] - randNums[i] - reelSize + 4 * symbolMatrix.get(i).size();
    		reel[i] %= symbolMatrix.get(i).size();
    	}
    	simSpent += 1;
    	double prize = getPrize(1);
    	simWon += prize;
    	if (prize > 0) simWins++;
    }
     
    /** Checks for number matches and adjusts score depending on result. */
    public void matchCheck() {
        credits += getPrize(bet);
        lblCredits.setText("Credits: " + (credits)); // deduct bet amount from available credits.
    }

    private void lightBorders(int a[], boolean[] bools) {
    	for (int i = 0; i < a.length; i++) {
    		if (bools[i]) panelMatrix[i][a[i]].setBackground(new java.awt.Color(255, 0, 0));
		}
    }
    
    private void lightBarrel() {
    	for (int j = 0; j < panelMatrix[2].length; j++) {
			if (reel2Symbols.get((reel[2] + j) % reel2Symbols.size()).getDescription().equals("Bure")) {
				panelMatrix[2][j].setBackground(new java.awt.Color(128, 0, 128));
			}
		}
    }
    
    /** calculates prize to be awarded for win based on number of matches and cheats. */
    public double getPrize(double bet) {
    	int prize = 0;
    	double lineMulti;
    	String first, second, third;
    	int cnt = 0;
    	int numPartial = 0;
    	
    	for (int i[] : lines) {
    		if (cnt < 3) lineMulti = 1.2;
    		else if (cnt < 5) lineMulti = 1.5;
    		else lineMulti = 0.8;
    		first = reel0Symbols.get((reel[0] + i[0]) % reel0Symbols.size()).getDescription();
    		second = reel1Symbols.get((reel[1] + i[1]) % reel1Symbols.size()).getDescription();
    		third  = reel2Symbols.get((reel[2] + i[2]) % reel2Symbols.size()).getDescription();
    		boolean[] bools = new boolean[3];
    		if (first == second && second == third) {
    			//
    			winningLine[cnt] = true;
				bools[0] = bools[1] = bools[2] = true;
    			prize += multi.get(first) * bet * lineMulti;
    			
				//poruka=new JDialog();
	           // poruka = new dijalog(this, "Cestitamo", true,prize);
	            //poruka.setVisible(true);
    		} else if (((first == second && i[0] == i[1]) || (second == third && i[1] == i[2]) && numPartial < 100)) {
    			prize += multi.get(second) * bet * lineMulti * 0.2;
    			if (first == second) bools[0] = bools[1] = true;
    			else bools[1] = bools[2] = true;
    			numPartial++;
    			winningLine[cnt] = true;
    		} else {
    			winningLine[cnt] = false;
    		}
    		
    		//lightBorders(i, bools);
    		cnt++;
    	}
    	for (int j = 0; j < panelMatrix[2].length; j++) {
			if (reel2Symbols.get((reel[2] + j) % reel2Symbols.size()).getDescription().equals("Bure")) {
				lightBarrel();
				prize *= 2;
			}
		}
    	return prize;
    }
     
    
    public void loadImages() {
    	reel0Symbols.add(createImageIcon("Sljiva.png", "Sljiva"));
    	reel0Symbols.add(createImageIcon("Sljiva.png", "Sljiva"));
    	reel0Symbols.add(createImageIcon("Visnja.png", "Visnja"));
    	reel0Symbols.add(createImageIcon("Kazan.png", "Kazan"));
    	reel0Symbols.add(createImageIcon("Pljoska.png", "Pljoska"));
    	reel0Symbols.add(createImageIcon("Kazan.png", "Kazan"));
    	reel0Symbols.add(createImageIcon("Dunja.png", "Dunja"));
    	reel0Symbols.add(createImageIcon("Kajsija.png", "Kajsija"));
    	reel0Symbols.add(createImageIcon("Deda.png", "Deda"));
    	reel0Symbols.add(createImageIcon("Visnja.png", "Visnja"));
    	reel0Symbols.add(createImageIcon("Sljiva.png", "Sljiva"));
    	reel0Symbols.add(createImageIcon("Grozdje.png", "Grozdje"));
    	reel0Symbols.add(createImageIcon("Grozdje.png", "Grozdje"));
    	reel0Symbols.add(createImageIcon("Dunja.png", "Dunja"));
    	reel0Symbols.add(createImageIcon("Dunja.png", "Dunja"));
    	reel0Symbols.add(createImageIcon("Kajsija.png", "Kajsija"));
    	reel0Symbols.add(createImageIcon("Kajsija.png", "Kajsija"));
    	reel0Symbols.add(createImageIcon("Sljiva.png", "Sljiva"));

    	reel1Symbols.add(createImageIcon("Sljiva.png", "Sljiva"));
    	reel1Symbols.add(createImageIcon("Sljiva.png", "Sljiva"));
    	reel1Symbols.add(createImageIcon("Kazan.png", "Kazan"));
    	reel1Symbols.add(createImageIcon("Pljoska.png", "Pljoska"));
    	reel1Symbols.add(createImageIcon("Visnja.png", "Visnja"));
    	reel1Symbols.add(createImageIcon("Kazan.png", "Kazan"));
    	reel1Symbols.add(createImageIcon("Grozdje.png", "Grozdje"));
    	reel1Symbols.add(createImageIcon("Dunja.png", "Dunja"));
    	reel1Symbols.add(createImageIcon("Grozdje.png", "Grozdje"));
    	reel1Symbols.add(createImageIcon("Kajsija.png", "Kajsija"));
    	reel1Symbols.add(createImageIcon("Deda.png", "Deda"));
    	reel1Symbols.add(createImageIcon("Sljiva.png", "Sljiva"));
    	reel1Symbols.add(createImageIcon("Pljoska.png", "Pljoska"));
    	reel1Symbols.add(createImageIcon("Visnja.png", "Visnja"));
    	reel1Symbols.add(createImageIcon("Visnja.png", "Visnja"));
    	reel1Symbols.add(createImageIcon("Dunja.png", "Dunja"));
    	reel1Symbols.add(createImageIcon("Dunja.png", "Dunja"));
    	reel1Symbols.add(createImageIcon("Kajsija.png", "Kajsija"));
    	reel1Symbols.add(createImageIcon("Kajsija.png", "Kajsija"));
    	reel1Symbols.add(createImageIcon("Sljiva.png", "Sljiva"));
    	
    	reel2Symbols.add(createImageIcon("Sljiva.png", "Sljiva"));
    	reel2Symbols.add(createImageIcon("Sljiva.png", "Sljiva"));
    	reel2Symbols.add(createImageIcon("Kazan.png", "Kazan"));
    	reel2Symbols.add(createImageIcon("Pljoska.png", "Pljoska"));
    	reel2Symbols.add(createImageIcon("Kazan.png", "Kazan"));
    	reel2Symbols.add(createImageIcon("Dunja.png", "Dunja"));
    	reel2Symbols.add(createImageIcon("Visnja.png", "Visnja"));
    	reel2Symbols.add(createImageIcon("Visnja.png", "Visnja"));
    	reel2Symbols.add(createImageIcon("Grozdje.png", "Grozdje"));
    	reel2Symbols.add(createImageIcon("Grozdje.png", "Grozdje"));
    	reel2Symbols.add(createImageIcon("Kajsija.png", "Kajsija"));
    	reel2Symbols.add(createImageIcon("Deda.png", "Deda"));
    	reel2Symbols.add(createImageIcon("Sljiva.png", "Sljiva"));
    	reel2Symbols.add(createImageIcon("Pljoska.png", "Pljoska"));
    	reel2Symbols.add(createImageIcon("Dunja.png", "Dunja"));
    	reel2Symbols.add(createImageIcon("Dunja.png", "Dunja"));
    	reel2Symbols.add(createImageIcon("Kajsija.png", "Kajsija"));
    	reel2Symbols.add(createImageIcon("Kajsija.png", "Kajsija"));
    	reel2Symbols.add(createImageIcon("Sljiva.png", "Sljiva"));
    	reel2Symbols.add(createImageIcon("Bure.png", "Bure"));

    }
     
    /** Create a new ImageIcon, unless the URL is not found. */
    public ImageIcon createImageIcon(String path, String description) {
        java.net.URL imgURL = getClass().getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL, description);
            } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }
    
    public double calculateRTP() {
    	double prize = 0.0;
    	double bet = 0.0;
    	for (int i = 0; i < reel0Symbols.size(); i++) {
    		for (int j = 0; j < reel1Symbols.size(); j++) {
    			for (int k = 0; k < reel2Symbols.size(); k++) {
    	    		reel[0] = i;
    	    		reel[1] = j; 
    	    		reel[2] = k;
    	    		bet++;
    	    		prize += getPrize(1);
    	    	}
        	}
    	}
    	DecimalFormat df = new DecimalFormat();
    	df.setMaximumIntegerDigits(4);
    	System.out.println("Calc RTP: " + df.format(prize / bet));
    	return prize / bet;
    }
     
    public double getRTP() {
    	simWon = simSpent = simWins = 0;
    	btnSpin.setEnabled(false);
    	simulation = true;
    	genReelNumbers();
    	simulation = false;
    	btnSpin.setEnabled(true);
    	double rtp2 =(double) Math.round(simWon / simSpent*10000)/10000;
    	rtp.setText("RTP: " + rtp2);
    	rtp.setFont(new Font("Consolas",Font.PLAIN,24));
    	rtp.setVisible(true);
    	System.out.println("Winrate: " + (double)simWins / simCount);
    	calculateRTP();
    	return rtp2;
    }
     
    public static void main(String args[]) {
         
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
            } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(SlotMachineGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(SlotMachineGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(SlotMachineGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(SlotMachineGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
         
        java.awt.EventQueue.invokeLater(new Runnable() {
             
            public void run() {
                new SlotMachineGUI();
            }
        });
         
    }
}