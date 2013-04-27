/*
 * This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 */
package de.uni_leipzig.asv.toolbox.ChineseWhispers.gui;

import java.io.File;
import com.sun.org.apache.xerces.internal.parsers.JAXPConfiguration;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SpinnerListModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import de.uni_leipzig.asv.toolbox.ChineseWhispers.algorithm.ChineseWhispers;
import de.uni_leipzig.asv.toolbox.ChineseWhispers.db.DBConnect;
import de.uni_leipzig.asv.toolbox.ChineseWhispers.graph.MyGraphGUI;
import de.uni_leipzig.asv.toolbox.ChineseWhispers.parameterHandler.PropertyLoader;
import de.uni_leipzig.asv.toolbox.ChineseWhispers.statistics.Diagram;
import de.uni_leipzig.asv.utils.IOIteratorException;
import de.uni_leipzig.asv.utils.IOWrapperException;
import de.uni_leipzig.asv.toolbox.util.commonFileChooser.*;
/**
 * @author Rocco & Seb
 *
 */
public class Controller extends JTabbedPane implements ActionListener{
	
    PropertyLoader pl_expert = new PropertyLoader("CW_ExpertProperties.ini");
    PropertyLoader pl_DB = new PropertyLoader("CW_DBPoperties.ini.");
    PropertyLoader pl_LastFiles = new PropertyLoader("CW_LastFiles.ini");
    
    
    public static boolean running_hack;
    public static final String NODES_TMP_FILENAME="nodelist.tmp";
	public static final String EDGES_TMP_FILENAME="edgelist.tmp";
	public static final String CLASSES_TMP_FILENAME="colors.tmp";

	public static final String [] COLUMN_NAMES_FOR_DB_OUT={"node_id","node_label","cluster_id","cluster_1","strength_1","cluster_2","strength_2"};
	
	private static final String display_node_degree_default		= "0";
	private static final String display_edges_default		= "3000";
	private static final String scale_default			= "1000 x 1000";
	private static final String minweight_edges_default		= "0";
	private static final String iterations_default			= "20";
	private static final String alg_param_default			= "top";
        private static final String mut_option_default			= "constant";
        private static final String update_param_default		= "continuous";
	private static final String vote_value_default			= " 0.5 ";
  	private static final String keepclass_value_default		= " 0.0 ";    
        private static final String mut_value_default			= " 0.0 ";
        
        
	private static final boolean display_sub_default			= false;

	private static final String hostnameString_default="localhost";
	private static final String databaseString_default="cw"; 
	private static final String rusernameString_default="root"; 
	private static final String nodeList_DBtable_default="words";
	private static final String node_ids_DBcol_default="w_id";
	private static final String node_labels_DBcol_default="word";
	private static final String edgeList_DBtable_default="co_s";
	private static final String edgeList_DBcol1_default="w1_id";
	private static final String edgeList_DBcol2_default="w2_id";
	private static final String edgeList_DBcolweight_default="sig";
	private static final String result_DBtable_default="clustering";
	private static final int portNr_default=3306;

	private String display_node_degree_current, 
                display_edges_current, 
                scale_current, 
                minweight_edges_current, 
                iterations_current, 
                alg_param_current, 
                vote_value_current;
        private String keepclass_value_current, 
                mut_option_current, 
                mut_value_current, 
                update_param_current;
        
	private boolean display_sub_current;
	
	private boolean is_already_renumbered = false;
	
	private int display_edges_temp, scale_temp, display_degree_temp;
	
	private boolean is_alg_started= false;
	
	private boolean isGraphStarted=false, 
                isDiagStarted=false,
                isFileOutStarted=false,
                isDBOutStarted=false;
	
	private JSpinner nodeDegreeSpinner, 
                displayEdgesSpinner, 
                scaleSpinner,
                minweightSpinner,
                iterationsSpinner;
        
	private ButtonGroup Alg_param;
        private ButtonGroup mutationParameter;
        private ButtonGroup Update_param;
        
	private JRadioButton top; 
	private JRadioButton dist_log;
	private JRadioButton dist_nolog;
	private JRadioButton vote;
        private JRadioButton dec;
        private JRadioButton constant;
        private JRadioButton stepwise;
	private JRadioButton continuous;
        
	private JPanel radios;
	private JSpinner vote_value;
        private JSpinner mut_value;
        private JSpinner keep_value;
        
	private JTextField nodefileText;
	private JTextField edgeFileText;        
        
	private JButton nodeFileBrowseButton;
	private JButton edgeFileBrowseButton;
	private JButton setdefault;
	private JButton save;
	private JButton start;
	private JCheckBox only_sub;
	private JRadioButton UseFile,UseDB;
	
	private JButton AsGraph;
	private JButton AsDia;
	private JButton AsFile;
	private JButton AsDB;
	
	public DBConnect dbc_out;
	public DBConnect dbc_in;
	private boolean is_already_read_from_DB=false;
	
	public JProgressBar loadFromFileProgress;
	public JProgressBar loadFromDBProgress;
	public JLabel loadFRomDBLabel;
	
	public JProgressBar writeIntoDBProgress;
	public JLabel writeIntoDBLabel;
	
	public JProgressBar calculateCWProgress;
	private JLabel CalculateCWLabel;
	
	public JProgressBar writeIntoFiles;
	
	private JCheckBox FileOutBox;    
	private JCheckBox DBOutBox;
	private JCheckBox FilesNumbered;
        private JCheckBox GraphInMemory;
        
	private JTextField FileOutField;
 
        private JButton MultFileOutBrowse;  // ??
        
	private JButton FileOutBrowseButton;
	private JButton startFileDB, dbtake, dbdefault;
	private JLabel startGraph, startDiagramm;
	
	public boolean isDBReadInUse=false;//is momentarily being read from DB?
	public boolean isDBWriteInUse=false;//is momentarily being written to DB?
	public boolean isFileWriteInUse=false;//is momentarily being written to files?	
	private boolean isFileReadInUsed=true;
	private boolean isDBInUsed=false; 
	private boolean isFileOutselected = false;
	private boolean isDBOutselected = false;
	
	private boolean isAlgStartedForGraph=false;
	
	private boolean dBOutError = false;
	private boolean dBInError = false;
	
	//DB
	private JTextField 
                rhostfield, 
                ruserfield, 
                rportfield,
                rdatabasefield, 
                rtablename1efield, 
                colnodeIdfield, 
                colnodeLabelfield, 
                rtablename2efield, 
                coledge1field, 
                coledge2field, 
                colweightfield,
                otablenamefield;
	private String 
                hostnameString,
                databaseString, 
                usernameString, 
                passwdString,
                NodeTableString,
                NodeIDColString,
                NodeLabelColString,
                EdgeTableString,
                EdgeCo1String,
                EdgeCol2String,
                EdgeWeightColString,
                resultTableString;
	private int portNr;
	private boolean isDBValuesSet=false;
	private JPasswordField rpasswdfield;
	
	private ChineseWhispers cw;
	private MyGraphGUI g;
	private JPanel pg;

	private JPanel  P1 = new JPanel(null);
	private JPanel  P2 = new JPanel(null);
	private JPanel  P3 = new JPanel(null);
	private JPanel welcomePanel = new JPanel(new GridLayout(3,1));
	JFrame window;
	private LinkedList L;
	
	/**
	 * Set and controll the GUI, handles IOs.
	 *
	 */
	public Controller(){

	    cw = new ChineseWhispers();
            pg = new JPanel();
            //set display mode
            try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                    SwingUtilities.updateComponentTreeUI(this);
                    UIManager.getLookAndFeelDefaults().put("Label.font", new Font(null,Font.ROMAN_BASELINE,11));
                    UIManager.getLookAndFeelDefaults().put("RadioButton.font", new Font(null,Font.ROMAN_BASELINE,11));
                    UIManager.getLookAndFeelDefaults().put("TextField.font", new Font(null,Font.ROMAN_BASELINE,11));
                    UIManager.getLookAndFeelDefaults().put("Button.font", new Font(null,Font.BOLD,11));
                    UIManager.getLookAndFeelDefaults().put("CheckBox.font", new Font(null,Font.ROMAN_BASELINE,11));
            } catch (Exception e2) {System.out.println("Error in GUI");} 
            getSavedValues();
            getSavedDBValues();

//		P1.setBackground(myColor);
//		P2.setBackground(myColor);
//		P3.setBackground(myColor);


//main panel*********************************
            //elemente definieren
            JLabel text0 = new JLabel("<html><u><b>INPUT</b></u></html>");
            JLabel text1 = new JLabel("Nodes:");
            nodefileText = new JTextField(25);
            nodeFileBrowseButton = new JButton("Browse");
            nodeFileBrowseButton.setActionCommand("nodelist");
            JLabel text2 =new JLabel("Edges:");
            edgeFileText = new JTextField(25);
            edgeFileBrowseButton = new JButton("Browse");
            edgeFileBrowseButton.setActionCommand("edgelist");
            fillFilenames();//insert values into nodelist and edgelist

            FilesNumbered = new JCheckBox("Input is pre-numbered");
            FilesNumbered.setBackground(P1.getBackground());
            FilesNumbered.setToolTipText("skips process of renumbering (applies to input from database and files).");

            GraphInMemory = new JCheckBox("keep graph in RAM");
            GraphInMemory.setBackground(P1.getBackground());
            GraphInMemory.setToolTipText("Whether to keep graph in RAM or on disc. Uncheck for large graphs");
            GraphInMemory.setSelected(true);

            UseFile =  new JRadioButton("Input from file", true);
            UseFile.setToolTipText("read graph from files. Browse for node list and edge list.");
            UseFile.setBackground(P1.getBackground());

            loadFromFileProgress = new JProgressBar(JProgressBar.HORIZONTAL,0,100);
            loadFromFileProgress.setStringPainted(true);
            loadFromFileProgress.setBackground(Color.LIGHT_GRAY);
            loadFromFileProgress.setVisible(false);


            UseFile.setMargin(new Insets(0,0,0,0));
            UseFile.setActionCommand("UseFile");
            UseFile.addActionListener(this);

            UseDB =  new JRadioButton("Input from database", false);
            UseDB.setToolTipText("Use database as specified in database panel");
            UseDB.setBackground(P1.getBackground());
            UseDB.setMargin(new Insets(0,0,0,0));
            UseDB.setActionCommand("UseDB");
            UseDB.addActionListener(this);

            loadFromDBProgress = new JProgressBar(JProgressBar.HORIZONTAL,0,100);
            loadFromDBProgress.setStringPainted(true);
            loadFromDBProgress.setBackground(Color.LIGHT_GRAY);
            loadFromDBProgress.setVisible(false);

            JLabel text3 = new JLabel("<html><u><b>OUTPUT</b></u></html>");

            DBOutBox = new JCheckBox("Output to database");
            DBOutBox.setBackground(P1.getBackground());
            //DBOutBox.setEnabled(false);

            DBOutBox.setActionCommand("DBOutBox");
            DBOutBox.addActionListener(this);
            DBOutBox.setSelected(false);
            DBOutBox.setToolTipText("Use database for results as specified in the database tab.");

            writeIntoDBProgress = new JProgressBar(JProgressBar.HORIZONTAL,0,100);
            writeIntoDBProgress.setStringPainted(true);
            writeIntoDBProgress.setBackground(Color.LIGHT_GRAY);
            writeIntoDBProgress.setVisible(false);

            FileOutBox= new JCheckBox("Output to file");
            FileOutBox.setBackground(P1.getBackground());
            FileOutBox.setActionCommand("FileOutBox");
            FileOutBox.addActionListener(this);
            FileOutBox.setSelected(false);
            FileOutBox.setToolTipText("Use file for output.");

            writeIntoFiles = new JProgressBar(JProgressBar.HORIZONTAL,0,100);
            writeIntoFiles.setStringPainted(true);
            writeIntoFiles.setBackground(Color.LIGHT_GRAY);
            writeIntoFiles.setVisible(false);

            FileOutField= new JTextField(25);
            FileOutField.setBackground(Color.LIGHT_GRAY);
            FileOutField.setEnabled(false);

            FileOutBrowseButton = new JButton("browse");
            FileOutBrowseButton.setActionCommand("FileOutBrowse");
            FileOutBrowseButton.addActionListener(this);
            FileOutBrowseButton.setEnabled(false);

            MultFileOutBrowse = new JButton("browse");
            MultFileOutBrowse.setActionCommand("FileOutBrowse");
            MultFileOutBrowse.addActionListener(this);
            MultFileOutBrowse.setEnabled(false);

            startFileDB= new JButton("start");
            startFileDB.setActionCommand("startFileDB");
            startFileDB.addActionListener(this);
            startFileDB.setEnabled(false);
            startFileDB.setToolTipText("Starts Chinese Whispers and writes into file or database.");

            CalculateCWLabel = new JLabel("calculating chinese whispers");
            CalculateCWLabel.setVisible(false);

            calculateCWProgress = new JProgressBar(JProgressBar.HORIZONTAL,0,100);
            calculateCWProgress.setStringPainted(true);
            calculateCWProgress.setBackground(Color.LIGHT_GRAY);
            calculateCWProgress.setVisible(false);

            startGraph= new JLabel("Start and open the graph display");
            startDiagramm= new JLabel("Start and open the diagram display");

            String s = System.getProperty("file.separator");

            AsGraph = new JButton(new ImageIcon(Controller.class.getResource("pics/graphicon.gif")));
            AsGraph.setToolTipText("Start and open the graph display");
            AsDia	= new JButton(new ImageIcon(Controller.class.getResource("pics/diaicon.gif")));
            AsDia.setToolTipText("Starts and open the diagram display.");
            AsFile	= new JButton("File");
            AsDB	= new JButton("Database");

            //element size and positions
            text0.setBounds(10,20,100,20);
            UseFile.setBounds(10,50,140,20);
            FilesNumbered.setBounds(146,50,200,20);
            GraphInMemory.setBounds(346,50,250,20);
            loadFromFileProgress.setBounds(210,57,90,10);
            loadFromFileProgress.setBorderPainted(false);
            loadFromFileProgress.setStringPainted(false);
            loadFromFileProgress.setBackground(Color.WHITE);

            text1.setBounds(10,75,100,20);
            text2.setBounds(10,100,100,20);
            nodefileText.setBounds(150,75,150,20);
            edgeFileText.setBounds(150,100,150,20);
            nodeFileBrowseButton.setBounds(320,75,80,20);
            edgeFileBrowseButton.setBounds(320,100,80,20);

            UseDB.setBounds(10,135,200,20);
            loadFromDBProgress.setBounds(210,142,90,10);
            loadFromDBProgress.setBorderPainted(false);
            loadFromDBProgress.setStringPainted(false);
            loadFromDBProgress.setBackground(Color.WHITE);

            text3.setBounds(10,170,100,20);
            DBOutBox.setBounds(10,200,130,20);
            writeIntoDBProgress.setBounds(210,207,90,10);
            writeIntoDBProgress.setBackground(Color.WHITE);
            writeIntoDBProgress.setBorderPainted(false);
            writeIntoDBProgress.setStringPainted(false);

            FileOutBox.setBounds(10,225,120,20);
            FileOutField.setBounds(150,225,150,20);

            writeIntoFiles.setBounds(210,232,90,10);
            writeIntoFiles.setBackground(Color.WHITE);
            writeIntoFiles.setBorderPainted(false);
            writeIntoFiles.setStringPainted(false);

            FileOutBrowseButton.setBounds(320,225,80,20);

            startFileDB.setBounds(10,255,80,20);

            startGraph.setBounds(10,290,180,20);
            startDiagramm.setBounds(200,290,180,20);

            AsGraph.setBounds(10,315,51,51);
            AsDia.setBounds(200,315,50,50);
            
            CalculateCWLabel.setBounds(419,325,200,20);
            calculateCWProgress.setBounds(420,345,150,10);
            calculateCWProgress.setBackground(Color.WHITE);
            calculateCWProgress.setBorderPainted(false);
            calculateCWProgress.setStringPainted(false);

            //add elements to GUI		
            P1.add(UseFile);
            P1.add(FilesNumbered);
            P1.add(GraphInMemory);

            P1.add(loadFromFileProgress);
            P1.add(text0);
            P1.add(text1);
            P1.add(text2);
            P1.add(nodefileText);
            P1.add(edgeFileText);
            P1.add(nodeFileBrowseButton);
            P1.add(edgeFileBrowseButton);
            P1.add(UseDB);
            P1.add(loadFromDBProgress);
            P1.add(text3);

            P1.add(DBOutBox);
            P1.add(writeIntoDBProgress);
            P1.add(FileOutBox);
         
            P1.add(writeIntoFiles);

            P1.add(AsGraph);
            P1.add(AsDia);
            P1.add(AsFile);
            P1.add(AsDB);
            P1.add(startFileDB);
            P1.add(startGraph);
            P1.add(startDiagramm);

            P1.add(CalculateCWLabel);
            P1.add(calculateCWProgress);

            //listeners
            nodeFileBrowseButton.addActionListener(new ActionListener(){public void actionPerformed(ActionEvent e) {openFile(e);}});
            edgeFileBrowseButton.addActionListener (new ActionListener(){public void actionPerformed(ActionEvent e) {openFile(e);}});
            AsGraph.addActionListener (new ActionListener(){
                public void actionPerformed(ActionEvent e) {
                            pl_LastFiles.setParam("nodelist-file",nodefileText.getText());
                            pl_LastFiles.setParam("edgelist-file",edgeFileText.getText());
                            startGraph(); 
                    }
                });
            AsDia.addActionListener (new ActionListener(){
                public void actionPerformed(ActionEvent e) {
                            pl_LastFiles.setParam("nodelist-file",nodefileText.getText());
                            pl_LastFiles.setParam("edgelist-file",edgeFileText.getText());
                            startDia(); 
                    }
                    });
            AsFile.addActionListener (new ActionListener(){public void actionPerformed(ActionEvent e) { }});
            AsDB.addActionListener (new ActionListener(){public void actionPerformed(ActionEvent e) { }});

//************************************************************************
//expert panel******************************************************		

            //define elements
            JLabel textg = new JLabel("<html><b><u>GRAPH SETTINGS</u></b></html>");
            JLabel text4 = new JLabel("<html><b>- degree of nodes:</b><br>"+
                                                              "<smaller>"+	
                                              "<b>value &lt; 0</b>, display nodes with higher degree<br>" +
                                              "<b>value &gt; 0</b>, display nodes with lower degree<br>" +
                                              "<b>value = 0</b>, display all nodes" +
                                              "</smaller></html>");
            L = new LinkedList();
            for(int i = -300; i<=300; i+=1){
                    L.add(Integer.toString(i));
            }
            nodeDegreeSpinner = new JSpinner(new SpinnerListModel(L) );
            nodeDegreeSpinner.setValue(display_node_degree_current);

            JLabel text5 = new JLabel("<html><b>- max. number of edges to draw:</b></html>");
            L = new LinkedList();
            L.add("all");
            for(int i = 100; i<=100000; i+=10){
                    L.add(Integer.toString(i));
            }
            displayEdgesSpinner = new JSpinner(new SpinnerListModel(L) );
            displayEdgesSpinner.setValue(display_edges_current);

            //*
            JLabel text6 = new JLabel("<html><b>- scale:</b></html>");
            L = new LinkedList();
            L.add("600 x 600");
            L.add("1000 x 1000");
            L.add("2000 x 2000");
            L.add("3000 x 3000");
            L.add("4000 x 4000");
            L.add("5000 x 5000");
            L.add("6000 x 6000");
            scaleSpinner = new JSpinner(new SpinnerListModel(L) );
            scaleSpinner.setValue(scale_current);

            //*
            only_sub = new JCheckBox("<html><b> starts with an empty subgraph panel</b></html>");
            only_sub.setBackground(P1.getBackground());
            only_sub.setMargin(new Insets(0,0,0,0));
            only_sub.setSelected(display_sub_current);

            //*
            JLabel texta = new JLabel("<html><b><u>ALGORITHM</u></b></html>");
            JLabel text7 = new JLabel("<html><b>- minimum edge weight threshold</b></html>");
            L = new LinkedList();
            for(int i = 0; i<=10000; i+=1){
                    L.add(Integer.toString(i));
            }
            minweightSpinner = new JSpinner(new SpinnerListModel(L) );
            minweightSpinner.setValue(minweight_edges_current);

            //*
            JLabel text8 = new JLabel("<html><b>- number of iterations:</b></html>");
            L = new LinkedList();
            for(int i = 10; i<=200; i+=1){
                    L.add(Integer.toString(i));
            }
            iterationsSpinner = new JSpinner(new SpinnerListModel(L) );
            iterationsSpinner.setValue(iterations_current);

            
            JLabel text9 = new JLabel("<html><b>- algorithm strategy:</b></html>");
            JLabel text10 = new JLabel("<html><b>- mutation:</b></html>");
            JLabel text11 = new JLabel("<html><b>- update strategy:</b></html>");
            JLabel text12 = new JLabel("<html><b>- keep color rate:</b></html>");


            Alg_param = new ButtonGroup();
            mutationParameter =  new ButtonGroup();
            Update_param =  new ButtonGroup();

            dec = new JRadioButton("exp. decreasing",mut_option_current.equals("dec"));
            dec.setActionCommand("dec");
            dec.setBackground(P1.getBackground());
            dec.setMargin(new Insets(0,0,0,0));
            dec.addActionListener(this);

            constant = new JRadioButton("constant",mut_option_current.equals("constant"));
            constant.setActionCommand("constant");
            constant.setBackground(P1.getBackground());
            constant.setMargin(new Insets(0,0,0,0));
            constant.addActionListener(this);

            stepwise = new JRadioButton("stepwise",update_param_current.equals("stepwise"));
            stepwise.setActionCommand("stepwise");
            stepwise.setBackground(P1.getBackground());
            stepwise.setMargin(new Insets(0,0,0,0));
            stepwise.addActionListener(this);

            continuous = new JRadioButton("continuous",update_param_current.equals("continuous"));
            continuous.setActionCommand("continuous");
            continuous.setBackground(P1.getBackground());
            continuous.setMargin(new Insets(0,0,0,0));
            continuous.addActionListener(this);


            top = new JRadioButton("top",alg_param_current.equals("top"));
            top.setActionCommand("top");
            top.setBackground(P1.getBackground());
            top.setMargin(new Insets(0,0,0,0));
            top.addActionListener(this);

            dist_log   = new JRadioButton("dist log",alg_param_current.equals("dist log"));
            dist_log.setActionCommand("dist log");
            dist_log.setBackground(P1.getBackground());
            dist_log.setMargin(new Insets(0,0,0,0));
            dist_log.addActionListener(this);

            dist_nolog   = new JRadioButton("dist nolog",alg_param_current.equals("dist nolog"));
            dist_nolog.setActionCommand("dist nolog");
            dist_nolog.setBackground(P1.getBackground());
            dist_nolog.setMargin(new Insets(0,0,0,0));
            dist_nolog.addActionListener(this);

            vote   = new JRadioButton("vote",alg_param_current.equals("vote"));
            vote.setActionCommand("vote");
            vote.setBackground(P1.getBackground());
            vote.setMargin(new Insets(0,0,0,0));
            vote.addActionListener(this);

            LinkedList L5 = new LinkedList();
            L5.add(" 0.0 ");
            L5.add(" 0.1 ");
            L5.add(" 0.2 ");
            L5.add(" 0.3 ");
            L5.add(" 0.4 ");
            L5.add(" 0.5 ");
            L5.add(" 0.6 ");
            L5.add(" 0.7 ");
            L5.add(" 0.8 ");
            L5.add(" 0.9 ");
            L5.add(" 1.0 ");
            vote_value = new JSpinner(new SpinnerListModel(L5) );
            vote_value.setValue(vote_value_current);


            LinkedList L6 = new LinkedList();
            L6.add(" 0.0 ");
            L6.add(" 0.1 ");
            L6.add(" 0.2 ");
            L6.add(" 0.3 ");
            L6.add(" 0.4 ");
            L6.add(" 0.5 ");
            L6.add(" 0.6 ");
            L6.add(" 0.7 ");
            L6.add(" 0.8 ");
            L6.add(" 0.9 ");
            L6.add(" 1.0 ");
            L6.add(" 2.0 ");
            L6.add(" 3.0 ");
            L6.add(" 4.0 ");
            L6.add(" 5.0 ");

            mut_value = new JSpinner(new SpinnerListModel(L6) );
            mut_value.setValue(mut_value_current);


            LinkedList L7 = new LinkedList();
            L7.add(" 0.0 ");
            L7.add(" 0.01 ");
            L7.add(" 0.05 ");
            L7.add(" 0.1 ");
            L7.add(" 0.2 ");
            L7.add(" 0.3 ");
            L7.add(" 0.4 ");
            L7.add(" 0.5 ");
            L7.add(" 0.6 ");
            L7.add(" 0.7 ");
            L7.add(" 0.8 ");
            L7.add(" 0.9 ");
            L7.add(" 1.0 ");
            keep_value = new JSpinner(new SpinnerListModel(L7) );
            keep_value.setValue(keepclass_value_current);


            //inaktive for all options but 'vote'
            if(!alg_param_current.equals("vote")) vote_value.setEnabled(false); 

            Alg_param.add(top);
            Alg_param.add(dist_log);
            Alg_param.add(dist_nolog);
            Alg_param.add(vote);

            mutationParameter.add(dec);
            mutationParameter.add(constant);

            Update_param.add(stepwise);
            Update_param.add(continuous);


            setdefault = new JButton("default");
            setdefault.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    setDefaultValues();
                }
            });

            save = new JButton("apply");
            save.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    saveValues();
                }
            });

            textg.setBounds(10,20,100,20);
            text4.setBounds(10,50,250,60);
            text4.setVerticalAlignment(JLabel.TOP);
            nodeDegreeSpinner.setBounds(260,50,100,20);

            text5.setBounds(10,120,250,20);
            displayEdgesSpinner.setBounds(260,120,100,20);

            text6.setBounds(10,150,250,20);
            scaleSpinner.setBounds(260,150,100,20);

            only_sub.setBounds(10,180,250,20);
            texta.setBounds(10,220,100,20);

            text7.setBounds(10,245,250,20);
            minweightSpinner.setBounds(260,245,100,20);

            text8.setBounds(10,275,250,20);
            iterationsSpinner.setBounds(260,275,100,20);

            text9.setBounds(10,305,250,20);
            top.setBounds(260,305,50,20);
            dist_log.setBounds(310,305,70,20);
            dist_nolog.setBounds(380,305,80,20);
            vote.setBounds(460,305,50,20);
            vote_value.setBounds(510,305,50,20);

            text10.setBounds(10,335,250,20);
            dec.setBounds(260,335,100,20);
            constant.setBounds(380,335,70,20);
            mut_value.setBounds(450,335,50,20);

            text11.setBounds(10,365,250,20);
            stepwise.setBounds(260,365,100,20);
            continuous.setBounds(380,365,100,20);

            text12.setBounds(10,395,250,20);
            keep_value.setBounds(260,395,50,20);

            save.setBounds(10,450,80,20);
            setdefault.setBounds(95,450,80,20);

            P2.add(textg);
            P2.add(text4);
            P2.add(nodeDegreeSpinner);
            P2.add(text6);
            P2.add(scaleSpinner);
            P2.add(only_sub);
            P2.add(texta);
            P2.add(text7);
            P2.add(minweightSpinner);
            P2.add(text8);
            P2.add(iterationsSpinner);
            P2.add(text9);
            P2.add(top);
            P2.add(dist_log);
            P2.add(dist_nolog);
            P2.add(vote);
            P2.add(vote_value);
            P2.add(text10);
            P2.add(dec);
            P2.add(constant);
            P2.add(mut_value);

            P2.add(text11);
            P2.add(stepwise);
            P2.add(continuous);
            P2.add(text12);
            P2.add(keep_value);

            P2.add(save);
            P2.add(setdefault);

            P2.add(text5);
            P2.add(displayEdgesSpinner);
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////		
//database panel
            JLabel rdatabase = new JLabel("<html><u><b>DATABASE</b></u></html>");
            rdatabase.setBounds(10,20,100,20);

            JLabel rhostname = new JLabel("Hostname:");
            rhostname.setBounds(20,40,100,20);
            rhostfield = new JTextField(25);
            rhostfield.setBounds(20,60,150,20);
            rhostfield.setText(hostnameString);
            rhostfield.setEnabled(false);
            rhostfield.setBackground(Color.LIGHT_GRAY);
            rhostfield.setToolTipText("Enter the hostname of database server.");

            JLabel rdatabasee = new JLabel("Database:");
            rdatabasee.setBounds(190,40,100,20);
            rdatabasefield = new JTextField(25);
            rdatabasefield.setBounds(190,60,150,20);
            rdatabasefield.setText(databaseString);
            rdatabasefield.setEnabled(false);
            rdatabasefield.setBackground(Color.LIGHT_GRAY);
            rdatabasefield.setToolTipText("Enter the database name");

            JLabel rport = new JLabel("Port:");
            rport.setBounds(360,40,100,20);
            rportfield = new JTextField(25);
            rportfield.setBounds(360,60,150,20);
            rportfield.setText(new Integer(portNr).toString());
            rportfield.setEnabled(false);
            rportfield.setBackground(Color.LIGHT_GRAY);
            rportfield.setToolTipText("Enter the port of database server (default 3306).");

            JLabel rusername = new JLabel("Username:");
            rusername.setBounds(20,80,100,20);
            ruserfield = new JTextField(25);
            ruserfield.setBounds(20,100,150,20);
            ruserfield.setText(usernameString);
            ruserfield.setEnabled(false);
            ruserfield.setBackground(Color.LIGHT_GRAY);
            ruserfield.setToolTipText("Enter your username for database connection.");

            JLabel rpasswd = new JLabel("Password:");
            rpasswd.setBounds(190,80,100,20);
            rpasswdfield = new JPasswordField(25);
            rpasswdfield.setBounds(190,100,150,20);
            rpasswdfield.setText(passwdString);
            rpasswdfield.setEnabled(false);
            rpasswdfield.setBackground(Color.LIGHT_GRAY);
            rpasswdfield.setToolTipText("Enter the password for database connection.");

            JLabel rtablename1 = new JLabel("<html><u><b>TABLE CONTAINING NODES</b></u></html>");
            rtablename1.setBounds(10,120,250,20);
            JLabel rtablename1e = new JLabel("Table name:");
            rtablename1e.setBounds(20,140,100,20);
            rtablename1efield = new JTextField(25);
            rtablename1efield.setBounds(20,160,150,20);
            rtablename1efield.setText(NodeTableString);
            rtablename1efield.setEnabled(false);
            rtablename1efield.setBackground(Color.LIGHT_GRAY);
            rtablename1efield.setToolTipText("Enter the table name for nodes.");

            JLabel colNodeID = new JLabel("Column node ID:");
            colNodeID.setBounds(190,140,200,20);
            colnodeIdfield = new JTextField(25);
            colnodeIdfield.setBounds(190,160,150,20);
            colnodeIdfield.setText(NodeIDColString);
            colnodeIdfield.setEnabled(false);
            colnodeIdfield.setBackground(Color.LIGHT_GRAY);
            colnodeIdfield.setToolTipText("Enter the column name for node IDs.");

            JLabel colNodeLabels = new JLabel("Colum node labels:");
            colNodeLabels.setBounds(360,140,200,20);
            colnodeLabelfield = new JTextField(25);
            colnodeLabelfield.setBounds(360,160,150,20);
            colnodeLabelfield.setText(NodeLabelColString);
            colnodeLabelfield.setEnabled(false);
            colnodeLabelfield.setBackground(Color.LIGHT_GRAY);
            colnodeLabelfield.setToolTipText("Enter the column name for node labels.");

            JLabel rtablename2 = new JLabel("<html><u><b>TABLE CONTAINING EDGES</b></u></html>");
            rtablename2.setBounds(10,180,250,20);
            JLabel rtablename2e = new JLabel("Table name:");
            rtablename2e.setBounds(20,200,100,20);
            rtablename2efield = new JTextField(25);
            rtablename2efield.setBounds(20,220,150,20);
            rtablename2efield.setText(EdgeTableString);
            rtablename2efield.setEnabled(false);
            rtablename2efield.setBackground(Color.LIGHT_GRAY);
            rtablename2efield.setToolTipText("Enter the table name for edges.");

            JLabel colwn1 = new JLabel("Col. node ID 1:");
            colwn1.setBounds(20,240,150,20);
            coledge1field = new JTextField(25);
            coledge1field.setBounds(20,260,150,20);
            coledge1field.setText(EdgeCo1String);
            coledge1field.setEnabled(false);
            coledge1field.setBackground(Color.LIGHT_GRAY);
            coledge1field.setToolTipText("Enter the column name of node ID 1.");

            JLabel colwn2 = new JLabel("Col. node ID 2:");
            colwn2.setBounds(190,240,150,20);
            coledge2field = new JTextField(25);
            coledge2field.setBounds(190,260,150,20);
            coledge2field.setText(EdgeCol2String);
            coledge2field.setEnabled(false);
            coledge2field.setBackground(Color.LIGHT_GRAY);
            coledge2field.setToolTipText("Enter the column name of node ID 2.");

            JLabel colsig = new JLabel("Col. weight:");
            colsig.setBounds(360,240,150,20);
            colweightfield = new JTextField(25);
            colweightfield.setBounds(360,260,150,20);
            colweightfield.setText(EdgeWeightColString);
            colweightfield.setEnabled(false);
            colweightfield.setBackground(Color.LIGHT_GRAY);
            colweightfield.setToolTipText("Enter the column name for weights.");

            JLabel otablename = new JLabel("<html><u><b>TABLE USED FOR OUTPUT</b></u></html>");
            otablename.setBounds(10,280,250,20);
            JLabel otablenamee = new JLabel("Table name:");
            otablenamee.setBounds(20,300,150,20);
            otablenamefield=new JTextField(25);
            otablenamefield.setBounds(20,320,150,20);
            otablenamefield.setText(resultTableString);
            otablenamefield.setEnabled(false);
            otablenamefield.setBackground(Color.LIGHT_GRAY);
            otablenamefield.setToolTipText("Enter the table name for output in DB.");

            //setDefaultDBValues();
            dbtake = new JButton("apply");
            dbtake.setBounds(10,350,80,20);
            dbtake.setEnabled(false);
            dbtake.setToolTipText("Use current values for db connection and operations.");
            dbtake.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    saveDBValues();
                }
            });
            dbdefault = new JButton("default");
            dbdefault.setBounds(95,350,80,20);
            dbdefault.setEnabled(false);
            dbdefault.setToolTipText("Get default values.");
            dbdefault.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    setDefaultDBValues();
                }
            });

            P3.add(rhostname);
            P3.add(rhostfield);
            P3.add(rport);
            P3.add(rportfield);
            P3.add(rusername);
            P3.add(ruserfield);

            P3.add(rdatabase);
            P3.add(rdatabasee);
            P3.add(rdatabasefield);
            P3.add(rpasswd);
            P3.add(rpasswdfield);

            P3.add(rtablename1);
            P3.add(rtablename1e);
            P3.add(rtablename1efield);
            P3.add(colNodeID);
            P3.add(colnodeIdfield);
            P3.add(colNodeLabels);
            P3.add(colnodeLabelfield);

            P3.add(rtablename2);
            P3.add(rtablename2e);
            P3.add(rtablename2efield);
            P3.add(colwn1);
            P3.add(coledge1field);
            P3.add(colwn2);
            P3.add(coledge2field);
            P3.add(colsig);
            P3.add(colweightfield);

            P3.add(otablename);
            P3.add(otablenamee);
            P3.add(otablenamefield);

            P3.add(dbtake);
            P3.add(dbdefault);

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//************************************************************************************************************		
		
//welcome panel
        JLabel l1 =new JLabel("<html><h1><u>Chinese Whispers</u></h1>" +
				"<p align=\"center\">A Graph Clustering Algorithm<br>" +
				"linear in the number of edges</p></html>");
        l1.setHorizontalAlignment(JLabel.CENTER);
        l1.setVerticalAlignment(JLabel.BOTTOM);
        l1.setBackground(welcomePanel.getBackground());
        
            welcomePanel.add(l1);

            JLabel l2 = new JLabel(new ImageIcon(Controller.class.getResource("pics/whisper1.jpg")));
            l2.setHorizontalAlignment(JLabel.CENTER);
            l2.setVerticalAlignment(JLabel.CENTER);
            l2.setBackground(welcomePanel.getBackground());
            welcomePanel.add(l2);

            JLabel l3 = new JLabel("<html><p align=\"center\">ASV Toolbox 2005<br>" +
                            "Authors:<br>" +
                            "C. Biemann, " +
                            "R. Gwizdziel, " +
                            "S. Gottwald</p></html>");
            l3.setHorizontalAlignment(JLabel.CENTER);
            l3.setVerticalAlignment(JLabel.TOP);
            l3.setBackground(welcomePanel.getBackground());
            welcomePanel.add(l3);


            addTab("welcome",welcomePanel);
            addTab("main",P1);
            addTab("expert",P2);
            addTab("database",P3);
	}
	
	//DB	
	/**
	 * Writes DB-parameters into property-file.
	 */
	private void saveDBValues(){
	    pl_DB.setParam("Hostname",(String)rhostfield.getText());
	    pl_DB.setParam("Port",(String)rportfield.getText());
	    pl_DB.setParam("Username",(String)ruserfield.getText());
	    
	    String pw="";
	    for(int i=0; i<rpasswdfield.getPassword().length;i++){
	        pw+=rpasswdfield.getPassword()[i];
	    }
	    pl_DB.setParam("Password",pw);
	    pl_DB.setParam("Database",(String)rdatabasefield.getText());
	    pl_DB.setParam("nodeTableName",(String)rtablename1efield.getText());
	    pl_DB.setParam("nodeColIDName",(String)colnodeIdfield.getText());
	    pl_DB.setParam("nodeColLabelName",(String)colnodeLabelfield.getText());
	    pl_DB.setParam("edgeTableName",(String)rtablename2efield.getText());
	    pl_DB.setParam("edgeCol1Name",(String)coledge1field.getText());
	    pl_DB.setParam("edgeCol2Name",(String)coledge2field.getText());
	    pl_DB.setParam("edgeColWeightName",(String)colweightfield.getText());
	    pl_DB.setParam("resultTableName",(String)otablenamefield.getText());
	    
	    is_alg_started = false;
	    is_already_read_from_DB=false;
	    is_already_renumbered=false;
	} // saveDBvalues

	/**
	 * Set all default DB-paramater.
	 *
	 */
	private void setDefaultDBValues(){
	    rhostfield.setText(hostnameString_default);
	    rportfield.setText((new Integer(portNr_default)).toString());
	    rdatabasefield.setText(databaseString_default);
	    ruserfield.setText(rusernameString_default);
	    rpasswdfield.setText("");
	    rtablename1efield.setText(nodeList_DBtable_default);
	    colnodeIdfield.setText(node_ids_DBcol_default);
	    colnodeLabelfield.setText(node_labels_DBcol_default);
	    rtablename2efield.setText(edgeList_DBtable_default);
	    coledge1field.setText(edgeList_DBcol1_default);
	    coledge2field.setText(edgeList_DBcol2_default);
	    colweightfield.setText(edgeList_DBcolweight_default);
	    otablenamefield.setText(result_DBtable_default);
	    
	    is_alg_started=false;
	    is_already_read_from_DB=false;
	    is_already_renumbered=false;
	}

	/**
	 * Get all DB-values from property-file and set them as actual values.
	 *
	 */
	private void getSavedDBValues(){
		boolean isSet []=new boolean[12];
		for(int i=0;i<12;i++)isSet[i]=false;
		
		if(pl_DB.getParam("Hostname")!= null){hostnameString= pl_DB.getParam("Hostname"); isSet[0]=true;}	else hostnameString=hostnameString_default;
		if(pl_DB.getParam("Port")!= null){
		    try{
		        portNr= Integer.parseInt(pl_DB.getParam("Port"));
		    }catch(NumberFormatException e){
		        portNr=portNr_default;
		    }
		    isSet[1]=true;
		}else portNr=portNr_default;
		if(pl_DB.getParam("Database")!= null){databaseString= pl_DB.getParam("Database"); isSet[2]=true;}else databaseString=databaseString_default;
		if(pl_DB.getParam("Username")!= null){usernameString= pl_DB.getParam("Username"); isSet[3]=true;}else usernameString=rusernameString_default;
		if(pl_DB.getParam("Password")!= null){passwdString= pl_DB.getParam("Password"); }else passwdString="";
		if(pl_DB.getParam("nodeTableName")!= null){NodeTableString= pl_DB.getParam("nodeTableName");isSet[4]=true;}else NodeTableString=nodeList_DBtable_default;
		if(pl_DB.getParam("nodeColIDName")!= null){NodeIDColString= pl_DB.getParam("nodeColIDName");isSet[5]=true;}else NodeIDColString=node_ids_DBcol_default;
		if(pl_DB.getParam("nodeColLabelName")!= null){NodeLabelColString= pl_DB.getParam("nodeColLabelName");isSet[6]=true;}else NodeLabelColString=node_labels_DBcol_default;
		if(pl_DB.getParam("edgeTableName")!= null){EdgeTableString= pl_DB.getParam("edgeTableName");isSet[7]=true;}else EdgeTableString=edgeList_DBtable_default;
		if(pl_DB.getParam("edgeCol1Name")!= null){EdgeCo1String = pl_DB.getParam("edgeCol1Name");isSet[8]=true;}else EdgeCo1String=edgeList_DBcol1_default;
		if(pl_DB.getParam("edgeCol2Name")!= null){EdgeCol2String = pl_DB.getParam("edgeCol2Name");isSet[9]=true;}else EdgeCol2String=edgeList_DBcol2_default;
		if(pl_DB.getParam("edgeColWeightName")!= null){EdgeWeightColString = pl_DB.getParam("edgeColWeightName");isSet[10]=true;}else EdgeWeightColString=edgeList_DBcolweight_default;
		if(pl_DB.getParam("resultTableName")!= null){resultTableString = pl_DB.getParam("resultTableName");isSet[11]=true;}else resultTableString=result_DBtable_default;

		for(int i=0;i<12;i++){
		    if(isSet[i]) isDBValuesSet=true;
		    else{isDBValuesSet=false;break;}
		}
		
		is_alg_started=false;
		is_already_read_from_DB=false;
		is_already_renumbered=false;
	}

	/**
	 * Same as saveDBValues but for algorithm options.
	 *
	 */
	private void saveValues(){
		pl_expert.setParam("displayNodeDegree",(String)nodeDegreeSpinner.getValue());
		pl_expert.setParam("displayEdges",(String)displayEdgesSpinner.getValue());
		pl_expert.setParam("scale",(String)scaleSpinner.getValue());
		pl_expert.setParam("minWeight",(String)minweightSpinner.getValue());
		pl_expert.setParam("iterations",(String)iterationsSpinner.getValue());
		pl_expert.setParam("mutationParameter",mutationParameter.getSelection().getActionCommand());
                pl_expert.setParam("Update_param",Update_param.getSelection().getActionCommand());
                pl_expert.setParam("vote_value",(String)vote_value.getValue());
                pl_expert.setParam("keep_value",(String)keep_value.getValue());              
                pl_expert.setParam("mut_value",(String)mut_value.getValue());                            
		pl_expert.setParam("only_sub", new Boolean(only_sub.isSelected()).toString());
		
		is_alg_started = false;
	} // end saveValues
	/**
	 * Same as setDefaultDBValues but for algorithm options.
	 *
	 */
	private void setDefaultValues(){
		nodeDegreeSpinner.setValue(display_node_degree_default);
		displayEdgesSpinner.setValue(display_edges_default);
		scaleSpinner.setValue(scale_default);
		minweightSpinner.setValue(minweight_edges_default);
		iterationsSpinner.setValue(iterations_default);
		vote_value.setValue(vote_value_default);
                mut_value.setValue(mut_value_default);
                keep_value.setValue(keepclass_value_default);
               
		only_sub.setSelected(display_sub_default);
		top.setSelected(true);
                continuous.setSelected(true);
                dec.setSelected(true);
		vote_value.setEnabled(false);		
		is_alg_started=false;
	}
	/**
	 * Same as getSavedDBValues but for algorithm options.
	 *
	 */
	private void getSavedValues(){
		
		if(pl_expert.getParam("displayNodeDegree")!= null){
			display_node_degree_current= pl_expert.getParam("displayNodeDegree");
		}else{
			display_node_degree_current= display_node_degree_default;
		}
		
		if(pl_expert.getParam("displayEdges")!= null){
			display_edges_current= pl_expert.getParam("displayEdges");
		}else{
			display_edges_current=  display_edges_default;
		}
		
		if(pl_expert.getParam("scale")!= null){
			scale_current= pl_expert.getParam("scale");
		}else{
			scale_current= scale_default;
		}
		
		if(pl_expert.getParam("minWeight")!= null){
			minweight_edges_current= pl_expert.getParam("minWeight");
		}else{
			minweight_edges_current=  minweight_edges_default;
		}
		
		if(pl_expert.getParam("iterations")!= null){
			iterations_current= pl_expert.getParam("iterations");
		}else{
			iterations_current= iterations_default;
		}
		
		if(pl_expert.getParam("vote_value")!= null){
			vote_value_current= pl_expert.getParam("vote_value");
		}else{
			vote_value_current= vote_value_default;
		}
                
                if(pl_expert.getParam("keep_value")!= null){
			keepclass_value_current= pl_expert.getParam("keep_value");
		}else{
			keepclass_value_current= keepclass_value_default;
		}

                if(pl_expert.getParam("mut_value")!= null){
			mut_value_current= pl_expert.getParam("mut_value");
		}else{
			mut_value_current= mut_value_default;
		}

                if(pl_expert.getParam("mutationParameter")!= null){
			mut_option_current = pl_expert.getParam("mutationParameter");
		}else{
			mut_option_current = mut_option_default; 			
		}
		
                if(pl_expert.getParam("Update_param")!= null){
			update_param_current = pl_expert.getParam("Update_param");
		}else{
			update_param_current = update_param_default; 			
		}
			
		if(pl_expert.getParam("Alg_param")!= null){
			alg_param_current = pl_expert.getParam("Alg_param");
		}else{
			alg_param_current = alg_param_default; 			
		}
		
		if(pl_expert.getParam("only_sub")!= null){
			display_sub_current= new Boolean(pl_expert.getParam("only_sub")).booleanValue();
		}else{
			display_sub_current= display_sub_default;
		}
		
		is_alg_started=false;
	}
	
	
	/**
	 * Handels Button-pushed events.
	 * @param e The ActionEvent.
	 */
	public void actionPerformed(ActionEvent e) {
	    String ec = e.getActionCommand();
	    if(ec.equals("vote")){ 
	        vote_value.setEnabled(true);
	    }
		else if(ec.equals("top")||ec.equals("dist log")||ec.equals("dist nolog")){
		    vote_value.setEnabled(false);
		}
		
            if(ec.equals("UseDB")){
		           
                UseDB.setSelected(true);
		UseFile.setSelected(false);
                setFileInUsed(UseFile.isSelected());
		setDBInUsed(UseDB.isSelected());
        	nodefileText.setEnabled(false);
		nodefileText.setBackground(Color.LIGHT_GRAY);
		edgeFileText.setEnabled(false);
		edgeFileText.setBackground(Color.LIGHT_GRAY);
		nodeFileBrowseButton.setEnabled(false);
		edgeFileBrowseButton.setEnabled(false);

		rhostfield.setEnabled(true); 
		ruserfield.setEnabled(true); 
		rportfield.setEnabled(true);
		rdatabasefield.setEnabled(true); 
		rtablename1efield.setEnabled(true); 
		colnodeIdfield.setEnabled(true); 
		colnodeLabelfield.setEnabled(true); 
		rtablename2efield.setEnabled(true); 
		coledge1field.setEnabled(true); 
		coledge2field.setEnabled(true); 
		colweightfield.setEnabled(true);
		rpasswdfield.setEnabled(true);

		rhostfield.setBackground(Color.WHITE); 
		ruserfield.setBackground(Color.WHITE); 
		rportfield.setBackground(Color.WHITE);
		rdatabasefield.setBackground(Color.WHITE); 
		rtablename1efield.setBackground(Color.WHITE); 
		colnodeIdfield.setBackground(Color.WHITE); 
		colnodeLabelfield.setBackground(Color.WHITE); 
		rtablename2efield.setBackground(Color.WHITE); 
		coledge1field.setBackground(Color.WHITE); 
		coledge2field.setBackground(Color.WHITE); 
		colweightfield.setBackground(Color.WHITE);
		rpasswdfield.setBackground(Color.WHITE);
            }
            else if(ec.equals("UseFile")){
		    
                UseDB.setSelected(false);
                UseFile.setSelected(true);

                setFileInUsed(UseFile.isSelected());
                setDBInUsed(UseDB.isSelected());
                
		nodefileText.setEnabled(true);
		nodefileText.setBackground(Color.WHITE);
		edgeFileText.setEnabled(true);
		edgeFileText.setBackground(Color.WHITE);
		nodeFileBrowseButton.setEnabled(true);
		edgeFileBrowseButton.setEnabled(true);
		
                if(!isDBOutselected()){
		    rhostfield.setEnabled(false); 
		    ruserfield.setEnabled(false); 
		    rportfield.setEnabled(false);
		    rdatabasefield.setEnabled(false); 
                    rpasswdfield.setEnabled(false);
                    rhostfield.setBackground(Color.LIGHT_GRAY); 
                    ruserfield.setBackground(Color.LIGHT_GRAY); 
                    rportfield.setBackground(Color.LIGHT_GRAY);
                    rdatabasefield.setBackground(Color.LIGHT_GRAY); 
                    rpasswdfield.setBackground(Color.LIGHT_GRAY);
		}
		rtablename1efield.setEnabled(false); 
		colnodeIdfield.setEnabled(false); 
		colnodeLabelfield.setEnabled(false); 
		rtablename2efield.setEnabled(false); 
		coledge1field.setEnabled(false); 
		coledge2field.setEnabled(false); 
		colweightfield.setEnabled(false);

		rtablename1efield.setBackground(Color.LIGHT_GRAY); 
		colnodeIdfield.setBackground(Color.LIGHT_GRAY); 
		colnodeLabelfield.setBackground(Color.LIGHT_GRAY); 
		rtablename2efield.setBackground(Color.LIGHT_GRAY); 
		coledge1field.setBackground(Color.LIGHT_GRAY); 
		coledge2field.setBackground(Color.LIGHT_GRAY); 
		colweightfield.setBackground(Color.LIGHT_GRAY);
            }
            if ((ec.equals("FileOutBox"))||(ec.equals("MultFileOutBox"))||(ec.equals("DBOutBox"))) {
                if(FileOutBox.isSelected()){
		        FileOutField.setEnabled(true);
		        FileOutField.setBackground(Color.WHITE);
		        FileOutBrowseButton.setEnabled(true);
		}
                
                if(ec.equals("DBOutBox")){
                        setDBOutselected(!isDBOutselected);
                        if(DBOutBox.isSelected()){
                            startFileDB.setEnabled(true);
                            otablenamefield.setEnabled(true);
                            otablenamefield.setBackground(Color.WHITE);	        
                            if(!isDBInUsed()){
                                rhostfield.setEnabled(true); 
                                ruserfield.setEnabled(true); 
                                rportfield.setEnabled(true);
                                rdatabasefield.setEnabled(true); 
                            	rpasswdfield.setEnabled(true);
				rhostfield.setBackground(Color.WHITE); 
                            	ruserfield.setBackground(Color.WHITE); 
				rportfield.setBackground(Color.WHITE);
				rdatabasefield.setBackground(Color.WHITE); 
				rpasswdfield.setBackground(Color.WHITE);
                            }
                        }
                 }
                
                
                if((!DBOutBox.isSelected())&&(!FileOutBox.isSelected())){
                    startFileDB.setEnabled(false);
                } else {
                    startFileDB.setEnabled(true);
                }
            }
             
     
	    if(isDBInUsed()||isDBOutselected()){
	        dbtake.setEnabled(true);
	        dbdefault.setEnabled(true);
	    }else{
	        dbtake.setEnabled(false);
	        dbdefault.setEnabled(false);
	    }
	    if(ec.equals("startFileDB")){
	        if(FileOutBox.isSelected()){
	            pl_LastFiles.setParam("nodelist-file",nodefileText.getText());
	        	pl_LastFiles.setParam("edgelist-file",edgeFileText.getText());
	        	Thread th1 = new Thread(new Runnable(){
	        	    public void run(){
	        	        startFileAndDB(1,this);
	        	    }
	        	});
	        	th1.start();
	        }
                
	        if(DBOutBox.isSelected()){
	        	pl_LastFiles.setParam("nodelist-file",nodefileText.getText());
	        	pl_LastFiles.setParam("edgelist-file",edgeFileText.getText());
	            //startDBWrite();
	            //DBOutBox.setSelected(false);
	        	Thread th2 = new Thread(new Runnable(){
	        	    public void run(){
	        	        startFileAndDB(2,this);
	        	    }
	        	});
	        	th2.start();
	        }
	    }
	}
	
	//indicates critical situations (file- and DBout at the same time)
	private boolean semaphore=true;
	/**
	 * Should handle and synchronize parallel file and db output.
	 * @param fileOrDB The number what to start with (file:1,db:2)
	 * @param r
	 */
	public synchronized void startFileAndDB(int fileOrDB,Runnable r){
	    dBInError=false;
            if(fileOrDB==1){
	        //System.out.println("startFileWrite()");
	        startFileWrite();
	        while(/*!dBInError&&*/!dBOutError&&(semaphore ||(!is_alg_started || (is_alg_started && ((is_already_read_from_DB && UseFile.isSelected())||(!is_already_read_from_DB && UseDB.isSelected())))))){
	            try{
	                Thread.sleep(200);
	                //System.out.println("Started startFileWrite(): Still waiting for finishing.");
	                //r.wait();
	            }
	            catch(Exception e){}
	        }
                
	    }
	    else if(fileOrDB==2){
	        //System.out.println("startDBWrite()");
	        startDBWrite();
	        while(/*!dBInError&&*/!dBOutError&&(semaphore || (!is_alg_started || (is_alg_started && ((is_already_read_from_DB && UseFile.isSelected())||(!is_already_read_from_DB && UseDB.isSelected())))))){
	            try{
	                //r.wait();
	                Thread.sleep(200);
	                //System.out.println("Started startDBWrite(): Still waiting for finishing.");
	            }
	            catch(Exception e){}
	        }
	    }
	} // end startFileAndDB
	/**
	 * Sets, if used, recent graph files into textfields. Otherwise defaults (examples).
	 *
	 */
	public void fillFilenames(){
	    if(pl_LastFiles.getParam("nodelist-file")!=null && pl_LastFiles.getParam("edgelist-file")!=null){
		    nodefileText.setText(pl_LastFiles.getParam("nodelist-file"));
		    edgeFileText.setText(pl_LastFiles.getParam("edgelist-file"));
	    }
	    else{
			nodefileText.setText("examples"+System.getProperty("file.separator")+"20nodes.txt");
			edgeFileText.setText("examples"+System.getProperty("file.separator")+"20edges.txt");
	    }
        } // end fillFilenames
	
	/**
	 * Set all Buttons either disabled during calculating chinesewhispers or enabled when calculation finished.
	 * @param status The status to set.
	 */
	public void setStatus(boolean status){
		AsGraph.setEnabled(status);
		AsDia.setEnabled(status);
		AsFile.setEnabled(status);
		AsDB.setEnabled(status);
		
		FileOutBox.setEnabled(status);
		DBOutBox.setEnabled(status);
		startFileDB.setEnabled(status);
		if(status&&(DBOutBox.isSelected()||FileOutBox.isSelected())){
		    startFileDB.setEnabled(true);
		}
		else{
		    startFileDB.setEnabled(false);
		}
	} // end setStatus

        
        ///* OPEN FILE OLD 
	public void openFile(ActionEvent e){
	    JFileChooser chooser = new JFileChooser();
               

	    int returnVal = chooser.showOpenDialog(null);
	    if(returnVal == JFileChooser.APPROVE_OPTION) {
	    	is_alg_started = false;
	    	is_already_renumbered=false;
	    	if(e.getActionCommand().equals("nodelist")){
	    		nodefileText.setText(chooser.getSelectedFile().getAbsolutePath());
	    	}
	    	if(e.getActionCommand().equals("edgelist")){
	    		edgeFileText.setText(chooser.getSelectedFile().getAbsolutePath());
	    	}
	    }
	} // end openFile
         
       
      
        
        /* NEW openFile using commonChooser
        public void openFile(ActionEvent e){
            String[] extension= new String[1];
            extension[0]="*";


            is_alg_started = false;
            is_already_renumbered=false;
            if(e.getActionCommand().equals("nodelist")){
                    CommonFileChooser chooser = new CommonFileChooser(extension, "node list");
                    String selected=chooser.showDialogAndReturnFilename(this.P2, "Choose");
                    if (selected!=null) {
                       nodefileText.setText(chooser.showDialogAndReturnFilename(this.P2, "Choose"));
                    }
            }
            if(e.getActionCommand().equals("edgelist")){
                    CommonFileChooser chooser = new CommonFileChooser(extension, "edge list");
                    String selected=chooser.showDialogAndReturnFilename(this.P2, "Choose");
                    if (selected!=null) {                        
                       edgeFileText.setText(chooser.showDialogAndReturnFilename(this.P2, "Choose"));
                    }
            }
        }
	
         
         //*/
        
        
	public boolean Alg_isFileOrDBOut;
	/**
	 * Says weither DBoutput Or Fileoutput is set.
	 * @return DB or File output set.
	 */
	public boolean isAlg_isFileOrDBOut(){
	    return this.Alg_isFileOrDBOut;
	}
	/**
	 * Set DBoutput Or Fileoutput.
	 * @return The DB or File output.
	 */
	public void setAlg_isFileOrDBOut(boolean Alg_isFileOrDBOut){
	    this.Alg_isFileOrDBOut=Alg_isFileOrDBOut;
	}

	/**
	 * Starts the chinesewhispers algorithm as thread
	 * @param Alg_isFileOrDBOut If filedialog expected (write to file) true, false otherwise.
	 */
	private void startALG(boolean Alg_isFileOrDBOut){
            
	    this.Alg_isFileOrDBOut=Alg_isFileOrDBOut;
	    //this.isDBReadInUse=true;
	    //System.out.println("startALG(boolean Alg_isFileOrDBOut)");
	    
	    Thread t = new Thread(new Runnable(){
	        public void run(){
                        dBOutError = false;
                         //dBInError = false;
                        String nodeListString;
                        String edgeListString;

                        //get pparameters
                        display_degree_temp    = Integer.parseInt((String)nodeDegreeSpinner.getValue());	
                        display_edges_temp= 1000;

                        if(displayEdgesSpinner.getValue().equals("all")){
                                display_edges_temp= -2;
                        }else{
                                display_edges_temp  = Integer.parseInt((String)displayEdgesSpinner.getValue());	
                        }

                        scale_temp = 1000; 
                        if(scaleSpinner.getValue().equals("600 x 600"))   scale_temp = 600;
                        if(scaleSpinner.getValue().equals("1000 x 1000")) scale_temp = 1000;
                        if(scaleSpinner.getValue().equals("2000 x 2000")) scale_temp = 2000;
                        if(scaleSpinner.getValue().equals("3000 x 3000")) scale_temp = 3000;
                        if(scaleSpinner.getValue().equals("4000 x 4000")) scale_temp = 4000;
                        if(scaleSpinner.getValue().equals("5000 x 5000")) scale_temp = 5000;

                        int minWeight_temp  = Integer.parseInt((String)minweightSpinner.getValue());		
                        int iterations_temp   = Integer.parseInt((String)iterationsSpinner.getValue());	

                        display_sub_current = only_sub.isSelected();

                        String Alg_param_temp="";
                        String Alg_param_value_temp="";
                        String Alg_param_keep="";
                        String Alg_param_mut1="";
                        String Alg_param_mut2="";
                        String Alg_param_update="";


                        String ALGOPT = Alg_param.getSelection().getActionCommand();
                        if(ALGOPT.equals("top")) {Alg_param_temp="top";}
                        if(ALGOPT.equals("dist log")) {Alg_param_temp="dist"; Alg_param_value_temp="log";}
                        if(ALGOPT.equals("dist nolog")) {Alg_param_temp="dist"; Alg_param_value_temp="nolog";}
                        if(ALGOPT.equals("vote")){
                                Alg_param_temp="vote"; 
                                Alg_param_value_temp=((String)vote_value.getValue()).trim();
                        } // fi vote

                        Alg_param_keep=((String)keep_value.getValue()).trim();
                        Alg_param_mut1=mutationParameter.getSelection().getActionCommand();
                        Alg_param_mut2=((String)mut_value.getValue()).trim();
                        Alg_param_update=Update_param.getSelection().getActionCommand();            

                        alg_param_current = Alg_param_temp;
                        vote_value_current= Alg_param_value_temp;
                        keepclass_value_current= Alg_param_keep;
                        mut_option_current = Alg_param_mut1;
                        mut_value_current = Alg_param_mut2;


                        cw.isActive=true;
                        if(!is_already_renumbered||((is_already_read_from_DB && UseFile.isSelected())||(!is_already_read_from_DB && UseDB.isSelected()))){
                            cw.isNumbered=false;
                            is_already_renumbered=true;
                        }
				
		        if(isDBInUsed()){
		            nodeListString = NODES_TMP_FILENAME;
                            edgeListString  = EDGES_TMP_FILENAME;
		            isDBReadInUse=true;

		            //if data is not yet read from DB
		            if(!is_already_read_from_DB){
 
                                // delete old files
           /*                     
                                System.out.println("Deleting files with "+nodeListString+" and "+edgeListString);
                                
                                new File(nodeListString).delete();
                                new File(nodeListString+".renumbered").delete();
                                new File(nodeListString+".renumbered.bin").delete();
                                new File(nodeListString+".renumbered.idx").delete();
                                new File(nodeListString+".renumbered.meta").delete();
                                new File(nodeListString+".renumbered.tmp").delete();                                
                                new File(nodeListString+".bin").delete();
                                new File(nodeListString+".idx").delete();                            
                                
                                new File(edgeListString).delete();
                                new File(edgeListString+".renumbered").delete();                                
                                new File(edgeListString+".renumbered.bin").delete();                               
                                new File(edgeListString+".renumbered.idx").delete();
                                new File(edgeListString+".renumbered.meta").delete();
                                new File(edgeListString+".renumbered.tmp").delete();                                  
                                new File(edgeListString+".bin").delete();                               
                                new File(edgeListString+".idx").delete();       
             */                   
                                
                                String [] nodeColumns={colnodeIdfield.getText(),colnodeLabelfield.getText()}; 
                                String [] edgeColumns={coledge1field.getText(),coledge2field.getText(),colweightfield.getText()};
                                String pw="";

                                for(int i=0; i<rpasswdfield.getPassword().length;i++)pw+=rpasswdfield.getPassword()[i];

                                dbc_out = new DBConnect(
                                    rhostfield.getText(), 
                                    rdatabasefield.getText(),
                                    ruserfield.getText(), 
                                    pw,
                                    (int)Integer.parseInt(rportfield.getText()),
                                    rtablename1efield.getText(), 
                                    nodeColumns,
                                    rtablename2efield.getText(),
                                    edgeColumns
                                );
                                                      
                                
                                try{
                                    //System.out.println("Deleting files with "+nodeListString+" and "+edgeListString);

                                    new File(nodeListString).delete();
                                    new File(nodeListString+".renumbered").delete();
                                    new File(nodeListString+".renumbered.bin").delete();
                                    new File(nodeListString+".renumbered.idx").delete();
                                    new File(nodeListString+".renumbered.meta").delete();
                                    new File(nodeListString+".renumbered.tmp").delete();                                
                                    new File(nodeListString+".bin").delete();
                                    new File(nodeListString+".idx").delete();                            

                                    new File(edgeListString).delete();
                                    new File(edgeListString+".renumbered").delete();                                
                                    new File(edgeListString+".renumbered.bin").delete();                               
                                    new File(edgeListString+".renumbered.idx").delete();
                                    new File(edgeListString+".renumbered.meta").delete();
                                    new File(edgeListString+".renumbered.tmp").delete();                                  
                                    new File(edgeListString+".bin").delete();                               
                                    new File(edgeListString+".idx").delete();      
                                    
                                    
                                    dbc_out.stillWorks=true;
                                    dbc_out.getAllFromDbAndWriteIntoTempFiles();

                                    is_already_read_from_DB=true;
                                }
                                catch(IOWrapperException iow_e){
                                    System.err.println("Error while loading from DB!\nConnection failed!");
                                    JOptionPane.showMessageDialog(null,"Error while loading from database!\nConnection failed!", "Error", JOptionPane.ERROR_MESSAGE); 
                                    is_alg_started = false;
                                    dBOutError=true;
                                    dBInError=true;
                                    dbc_out.stillWorks=false;
                                    semaphore=true;
                                    isFileOutStarted=false;
                                    isDBOutStarted=false;
                                    return;

                                }
                                catch(IOIteratorException ioi_e){
                                    System.err.println("Error while loading from DB!\nCould not iterate over results!");
                                    JOptionPane.showMessageDialog(null,"Error while loading from database!\nCould not iterate over results!", "Error", JOptionPane.ERROR_MESSAGE); 
                                    is_alg_started = false;
                                    dBOutError=true;
                                    dBInError=true;
                                    dbc_out.stillWorks=false;
                                    semaphore=true;
                                    isFileOutStarted=false;
                                    isDBOutStarted=false;
                                    return;
                                }

		            }//fi (!is_already_read_from_DB)
		        }// fi (isDBInUsed())
				
                        else if(isFileInUsed() && (edgeFileText.getText().length()!=0 && nodefileText.getText().length()!=0 ))	{
                                nodeListString = nodefileText.getText();
                                edgeListString  = edgeFileText.getText();
                                is_already_read_from_DB=false;
                                
                            
                        }
                        else{
                            nodeListString = "examples"+System.getProperty("file.separator")+"allews.txt";
                                edgeListString  = "examples"+System.getProperty("file.separator")+"skoll.txt";
                                is_already_read_from_DB=false;
                        }
                        
                        
		        //initialize                                
                        double call_mut_value;
                        double call_keep_value;
                        
                        call_keep_value=(new Double(Alg_param_keep)).doubleValue();
                        call_mut_value=(new Double(Alg_param_mut2)).doubleValue();

                        cw.isNumbered=FilesNumbered.isSelected();
                        cw.graphInMemory=GraphInMemory.isSelected();
                        cw.setCWGraph(nodeListString,edgeListString);
		        cw.setCWParameters(minWeight_temp,Alg_param_temp,Alg_param_value_temp, call_keep_value, Alg_param_mut1, call_mut_value, Alg_param_update, iterations_temp,isAlg_isFileOrDBOut());
                        
		        cw.run();
                     
		        is_alg_started = true;
		        semaphore=false;
		        //setAlg_isFileOrDBOut(false);
	        }//run
            });//runnable, thread
            
	    t.start();
            Timer timer = new Timer();
	    timer.schedule(new ObserveProgress(timer,t), 200, 2000);  
	} // end startAlg
        
	/**
	 * Calculate the graph for output.
	 */
	public void startGraph(){
		 setStatus(false);
		 isGraphStarted=true;
		 
		 //if (Alg not started yet or Alg started, but not ready for graph) or (Alg started but (already read from DB but now fileSelected) or (DB selected but not read yet))
		 if((!is_alg_started || !isAlgStartedForGraph) || (is_alg_started && ((is_already_read_from_DB && UseFile.isSelected())||(!is_already_read_from_DB && UseDB.isSelected())))){
		 	dBInError=false; 
		 	startALG(false);
		 }
		 else{
		     try {
				Thread t1 = new Thread(new MyGraphGUI(display_sub_current,(ChineseWhispers)cw.clone(),display_edges_temp,scale_temp,30,display_degree_temp));
				t1.start();
		     }
		     catch (CloneNotSupportedException e) {e.printStackTrace();}
                     setStatus(true); 
                     isGraphStarted=false;
                     isAlgStartedForGraph=true;
		 }
	} // end startGraph
	/**
	 * Calculate diagrams for output.
	 */
	public void startDia(){
		 setStatus(false);
		 isDiagStarted=true;
		 if(!is_alg_started || (is_alg_started && ((is_already_read_from_DB && UseFile.isSelected())||(!is_already_read_from_DB && UseDB.isSelected())))){
		 	dBInError=false; 
		 	startALG(true);
		 }
		 else{
         	try{
        	    Thread t2 = new Thread(new Diagram((ChineseWhispers)cw.clone()));
        	    t2.start();
        	}
         	catch (CloneNotSupportedException e) {e.printStackTrace();}
		    setStatus(true);
		    isDiagStarted=false;
		 }
	}
	/**
	 * Write clustering to file.
	 *
	 */
	public void startFileWrite(){
	    setStatus(false);
 	    isFileOutStarted=true;
            if(!is_alg_started || (is_alg_started && ((is_already_read_from_DB && UseFile.isSelected())||(!is_already_read_from_DB && UseDB.isSelected())))){
                startALG(true);
            }
            else{
                    Thread t = new Thread(new Runnable(){
                        public void run(){
                            writeIntoFiles.setVisible(true);
                            isFileWriteInUse=true;
                            cw.setWritesOnlyFiles(true);
                            cw.writeFile(true,true,false);
                        }//run()
                    });//Thread
                    t.start();
                Timer timer = new Timer();
                timer.schedule(new ObserveProgress(timer,t), 200, 2000); 
                    isFileOutStarted=false;
            }      
	} // end startWriteFile

               
	/**
	 * Write clustering into DB.
	 *
	 */
	public void startDBWrite(){
	    setStatus(false);
 	    isDBOutStarted=true;
	    if(!is_alg_started || (is_alg_started && ((is_already_read_from_DB && UseFile.isSelected())||(!is_already_read_from_DB && UseDB.isSelected())))){
	        startALG(true);
	    }
	    else{
//        	setStatus(true);
        	
        	Thread t = new Thread(new Runnable(){
        	    public void run(){
        	        isDBWriteInUse=true;
        	        writeIntoDBProgress.setVisible(true);
        	        cw.setWritesOnlyFiles(true);
        	        cw.writeFile(false,false,false);
		        	String pw="";
		            for(int i=0; i<rpasswdfield.getPassword().length;i++)pw+=rpasswdfield.getPassword()[i];
		            
		            dbc_in=new DBConnect(
		                	rhostfield.getText(), 
		                	rdatabasefield.getText(),
		                	ruserfield.getText(), 
		                	pw,
		                	(int)Integer.parseInt(rportfield.getText()),
		                	otablenamefield.getText(), 
		                	COLUMN_NAMES_FOR_DB_OUT
		        	);
		            
		        	//File: Controller.CLASSES_TMP_FILENAME
		        	//Columns: Controller.COLUMN_NAMES_FOR_DB_OUT
		            try{
		                dbc_in.stillWorks=true;
		        	    dbc_in.maxProgerss=cw.countNodesWithClasses;
		                dbc_in.writeFromFileIntoDB();
		        	}
		        	catch(IOWrapperException iow_e){
				        System.err.println("Error while writing into DB!\nConnection failed!");
				    	JOptionPane.showMessageDialog(null,"Error while writing into DB!\nConnection failed!", "Error", JOptionPane.ERROR_MESSAGE); 
				    	dBInError=true;
				    	dbc_in.stillWorks=false;
				    	return;
		        	}
		        	catch(IOIteratorException ioi_e){
				        System.err.println("Error while writing into DB!\nCould not iterate over results!");
				    	JOptionPane.showMessageDialog(null,"Error while writing into DB!\nCould not iterate over results!", "Error", JOptionPane.ERROR_MESSAGE); 
				    	dBInError=true;
				    	dbc_in.stillWorks=false;
				    	return;
		        	}
        	    }//run()
        	});//Thread
        	
    		t.start();
            Timer timer = new Timer();
    	    timer.schedule(new ObserveProgress(timer,t), 200, 2000); 
        	isDBOutStarted=false;
	    }
	}
    /**
     * @return Returns the isDBInUsed.
     */
    public boolean isDBInUsed(){
        return isDBInUsed;
    }
    /**
     * @param isDBInUsed The isDBInUsed to set.
     */
    public void setDBInUsed(boolean isDBInUsed) {
        this.isDBInUsed = isDBInUsed;
    }
    /**
     * @return Returns the isDBOutselected.
     */
    public boolean isDBOutselected() {
        return isDBOutselected;
    }
    /**
     * @param isDBOutselected The isDBOutselected to set.
     */
    public void setDBOutselected(boolean isDBOutselected) {
        this.isDBOutselected = isDBOutselected;
    }
    /**
     * @return Returns the isDBValuesSet.
     */
    public boolean isDBValuesSet() {
        return isDBValuesSet;
    }
    /**
     * @param isDBValuesSet The isDBValuesSet to set.
     */
    public void setDBValuesSet(boolean isDBValuesSet) {
        this.isDBValuesSet = isDBValuesSet;
    }
    /**
     * 
     * 
     * @return Returns the isFileReadInUsed.
     */
    public boolean isFileInUsed() {
        return isFileReadInUsed;
    }
    /**
     * 
     * 
     * @param isFileReadInUsed The isFileReadInUsed to set.
     */
    public void setFileInUsed(boolean isFileInUsed) {
        this.isFileReadInUsed = isFileInUsed;
    }
    /**
     * @return Returns the isFileOutselected.
     */
    public boolean isFileOutselected() {
        return isFileOutselected;
    }
    
    
    /**
     * @param isFileOutselected The isFileOutselected to set.
     */
    public void setFileOutselected(boolean isFileOutselected) {
        this.isFileOutselected = isFileOutselected;
    }
    
    
    /**
     * 
     * @author seb
     *
     */
    class ObserveProgress extends TimerTask{
    	private Timer tImer;
    	private Thread tHread;
    	
    	/**
    	 * Handles threads for cw-algorithm and progess-bar.
    	 * @param tImer The Timer to cancel when ready.
    	 * @param tHread The Thread to watch.
    	 */
    	public ObserveProgress(Timer tImer, Thread tHread){
    		this.tImer = tImer;
    		this.tHread=tHread;
    	}
    	
    	/**
    	 * Checks if cw-algorithm is still running. If ready, creates new threads for graphs <br>
    	 * and/or diagrams and sets the progress-bar visible, valued if ready not visible.
    	 */
    	public void run() {
    	    
    	    //Thread: writes files
    	    if(isFileWriteInUse){
    	        //System.out.println("isFileWriteInUse");
    	        	
    	    	if(cw.getWritesOnlyFiles()){//as long as thread is running
                    int i=cw.writeFileProgress/2;
                    writeIntoFiles.setValue(i);
                    writeIntoFiles.repaint();
    	        }
    	        else{
    	            tImer.cancel();
    	            System.out.println("*\tWriting into files finished.");
    	            isFileWriteInUse=false;
    	            writeIntoFiles.setVisible(false);
    	            if(!isDBWriteInUse)setStatus(true);
    	        }
    	    } // fi isFileWriteInUse
    	    else
    	    //Thread: writes from file in DB
    	    if(isDBWriteInUse){//as long write process in DB runs
    	    	//System.out.println("isDBWriteInUse");
    	    	
    	        //critical, because dbc_in is not available as object in the beginning
    	        boolean dbc_inStillWork;
    	        try{
    	            dbc_inStillWork = dbc_in.stillWorks;
    	        }
    	        catch(Exception e){
    	        	dbc_inStillWork=false;
    	        }
    	        
    	        if(cw.getWritesOnlyFiles()||dbc_inStillWork){//as long as the thread is running
	                int i;
    	            if(dbc_inStillWork){
    	                i = (cw.writeFileProgress/2)+((dbc_in.singleProgress*50)/dbc_in.maxProgerss);
    	            }
	                else{
	                    i=cw.writeFileProgress/2;
	                }
	                writeIntoDBProgress.setValue(i);
	                writeIntoDBProgress.repaint();
    	        }
    	        else{
    	            tImer.cancel();
    	            System.out.println("*\tWriting into DB finished.");
    	            isDBWriteInUse=false;
    	            writeIntoDBProgress.setVisible(false);
    	            if(!isFileWriteInUse)setStatus(true);
		            
    	            if(dBInError){
    	            	tImer.cancel();
                        System.err.print("\t FAILED!\n");
                        setStatus(true);
                        dBInError=false;

                        //dBOutError=false;
                    }

    	        } // esle
    	    }
    	    else
    	    //Thread: lloads from DB and writes in nodelist.tmp and edgelist.tmp
    	    if(isDBReadInUse){//as lng as read process is active
	            //System.out.println("isDBReadInUse");
    	    	
    	    	loadFromDBProgress.setVisible(true);
	            if(dbc_out.stillWorks){
	                //System.out.println("Loading from DB active ...");
	                int i = (dbc_out.singleProgress*100)/dbc_out.maxProgerss;
	                if(i<1)i=1;
	                loadFromDBProgress.setValue(i);
	                loadFromDBProgress.repaint();
	            }
	            else{
	                //tImer.cancel();
	                System.out.println("*\tLoading from DB finished.");
	                isDBReadInUse=false;
		            loadFromDBProgress.setVisible(false);
		            
		            if(dBOutError){
		            	tImer.cancel();
		            	System.err.print("\t FAILED!\n");
						
		            	setStatus(true);
		            	
		            	isGraphStarted=false;
		            	isDiagStarted=false;
		            	isFileWriteInUse=false;
		            	dBOutError=false;
		            	
		            	//dBInError=true;
		            	
		            }
	            }
	        }
	        //Thread: runs Chinese Whispers
    	    else  if(!dBOutError && !dBInError) {
    	    	//System.out.println("!dBOutError&&!dBInError");
    	    	
    	    	calculateCWProgress.setVisible(true);
                CalculateCWLabel.setVisible(true);
	
                if(cw.isActive){
                    if (cw.getIteration()>0) calculateCWProgress.setValue((cw.getCurrentIteration()*100)/cw.getIteration());
                    calculateCWProgress.repaint();
                } else{ // cw not active
                    tImer.cancel();
                    System.out.println("*\tCalculating algorithm finished.");
                    calculateCWProgress.setVisible(false);
                    CalculateCWLabel.setVisible(false);
			        
                    if(isGraphStarted){
                        //System.out.println("isGraphStarted");
                        setStatus(true); 
                        isAlgStartedForGraph=true;
                        isGraphStarted=false;
                        try {
                            Thread t1 = new Thread(new MyGraphGUI(display_sub_current,(ChineseWhispers)cw.clone(),display_edges_temp,scale_temp,30,display_degree_temp));
                            t1.start();
                        } catch (CloneNotSupportedException e) {e.printStackTrace();}	    		    
                    } // fi isGraphStarted
                    
                    if(isDiagStarted){
                                //System.out.println("isDiagStarted");
                                setStatus(true); 
                                isAlgStartedForGraph=false;
                                isDiagStarted=false;
                        try {
                            Thread t2 = new Thread(new Diagram((ChineseWhispers)cw.clone()));
                            t2.start();
                        } 
                        catch (CloneNotSupportedException e) {e.printStackTrace();}
                                
                    } // fi isDiagStarted
                    if(isFileOutStarted){
                        //System.out.println("isFileOutStarted");
                        Thread t = new Thread(new Runnable(){
                                public void run(){
                                    isFileWriteInUse=true;
                                    writeIntoFiles.setVisible(true);
                                    isAlgStartedForGraph=false;
                                    cw.setWritesOnlyFiles(true);
                                    cw.writeFile(true,true,false);
                                    //isFileWriteInUse=false;
                                }//run()
                        });//Thread
                        
                        t.start();
                        Timer timer = new Timer();
                        timer.schedule(new ObserveProgress(timer,t), 200, 2000); 
                        isFileOutStarted=false;
                    } // fi isFileOutStarted
                    if(isDBOutStarted){
                        //System.out.println("isDBOutStarted");
                        Thread t = new Thread(new Runnable(){
                            public void run(){
                                isDBWriteInUse=true;
                                writeIntoDBProgress.setVisible(true);
                                isAlgStartedForGraph=false;
                                cw.setWritesOnlyFiles(true);
                                cw.writeFile(false,false,false);
                                String pw="";
                                for(int i=0; i<rpasswdfield.getPassword().length;i++)pw+=rpasswdfield.getPassword()[i];

                                dbc_in=new DBConnect(
                                    rhostfield.getText(), 
                                    rdatabasefield.getText(),
                                    ruserfield.getText(), 
                                    pw,
                                    (int)Integer.parseInt(rportfield.getText()),
                                    otablenamefield.getText(), 
                                    COLUMN_NAMES_FOR_DB_OUT
                                );

                                //File: Controller.CLASSES_TMP_FILENAME
                                //Columns: Controller.COLUMN_NAMES_FOR_DB_OUT
                                try{
                                    dbc_in.stillWorks=true;
                                    dbc_in.maxProgerss=cw.countNodesWithClasses;
                                    dbc_in.writeFromFileIntoDB();
                                } catch(IOWrapperException iow_e){
                                    System.err.println("Error while writing into DB!\nConnection failed!");
                                    JOptionPane.showMessageDialog(null,"Error while writing into DB!\nConnection failed!", "Error", JOptionPane.ERROR_MESSAGE); 
                                    dBInError=true;
                                    dbc_in.stillWorks=false;
                                    return;
                                } // end catch WrapperException
                                catch(IOIteratorException ioi_e){
                                    System.err.println("Error while writing into DB!\nCould not iterate over results!");
                                    JOptionPane.showMessageDialog(null,"Error while writing into DB!\nCould not iterate over results!", "Error", JOptionPane.ERROR_MESSAGE); 
                                    dBInError=true;
                                    dbc_in.stillWorks=false;
                                    return;
                                }
                                isDBOutStarted=false;
                            }//run()
                        });//Thread

                        t.start();
                        Timer timer = new Timer();
                        timer.schedule(new ObserveProgress(timer,t), 200, 2000); 
                        isDBOutStarted=false;
                    } // end isDBOutStarted
                    
	    		
                } // esle CW not active
    		
            } // fi (!dBOutError && !dBInError)
    	
        } // end void run()
    
    } // end class observeProgress

} // end Class Controller

