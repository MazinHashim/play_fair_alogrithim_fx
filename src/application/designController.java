package application;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javax.swing.JOptionPane;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.FileChooser.ExtensionFilter;

public class designController implements Initializable{

    @FXML
    private JFXButton home,encrypt,decrypt,shutt,ideaButton;
    @FXML
    private AnchorPane homePane,encryPane,decryPane,shuttPane;
    @FXML
    private Pane desPane;
    @FXML
    private JFXTextArea desPaneArea,decryArea,encryArea,tableOutput;
    @FXML
    private JFXTextField decryKey,encryKey;
    
    private boolean desPaneFlag = false;
    private double x=0,y=0;
    private int length = 0;
    private String [][] table;

    @FXML
    private void close(ActionEvent event) {
    	Platform.exit();
    	System.exit(0);
    }
    
    @FXML
    private void decryptionButton(ActionEvent event) {
    	
    	encryArea.setText("");
		FileChooser fileChooser = new FileChooser();
		fileChooser.setInitialDirectory(new File("C:\\Users\\Mazin\\Desktop"));
		fileChooser.getExtensionFilters().addAll(new ExtensionFilter("TXT Files","*.txt"));
		File selected = fileChooser.showOpenDialog(null);
		
		String decodedOutput = decryption(selected);
		
		decryArea.setText(decodedOutput);
    }
    @FXML
    private void encryptionButton(ActionEvent event) {
    	decryArea.setText("");
		FileChooser fileChooser = new FileChooser();
		fileChooser.setInitialDirectory(new File("C:\\Users\\Mazin\\Desktop"));
		fileChooser.getExtensionFilters().addAll(new ExtensionFilter("TXT Files","*.txt"));
		File selected = fileChooser.showOpenDialog(null);
		
		encryArea.setText(encryption(selected));
    	
    }

	@FXML
    private  void menuHandler(ActionEvent event) {
    	if(event.getSource() == home) {
    		visibility(homePane,encryPane,decryPane,shuttPane);
    	}
    	if(event.getSource() == encrypt) {
    		visibility(encryPane,homePane,decryPane,shuttPane);
    	}
    	if(event.getSource() == decrypt) {
    		visibility(decryPane,homePane,encryPane,shuttPane);
    	}
    	if(event.getSource() == shutt) {
    		visibility(shuttPane,decryPane,homePane,encryPane);
    	}
    }
	private void visibility(AnchorPane needed, AnchorPane not1, AnchorPane not2, AnchorPane not3) {
		needed.setVisible(true);
		not1.setVisible(false);
		not2.setVisible(false);
		not3.setVisible(false);
	}
    @FXML
    private void minimize(ActionEvent event) {
    	Stage stage = (Stage) desPane.getScene().getWindow();
		stage.setIconified(true);
    }

    @FXML
    private void dragged(MouseEvent event) {
    	Node node = (Node) event.getSource();
		Stage stage = (Stage) node.getScene().getWindow();
		stage.setX(event.getScreenX()-x);
		stage.setY(event.getScreenY()-y);
    }
    @FXML
    private void pressed(MouseEvent event) {
    	x = event.getSceneX();
		y = event.getSceneY();
    }

    @FXML
    private void showHelp(ActionEvent event) {
    	if(!desPaneFlag) {
    		desPane.setVisible(true);
    		desPaneFlag = true;
    	}else {
    		desPane.setVisible(false);
    		desPaneFlag = false;
    	}
    }
    
    public String readFromFile(File name){
		String readed = "";
		try {
			BufferedReader reader = new BufferedReader(new FileReader(name));
			String line = null;
			StringBuilder strb = new StringBuilder();
			while((line = reader.readLine()) != null){
				strb.append(line);
			}
			readed = strb.toString();
			reader.close();
		} catch (IOException e1) {
			JOptionPane.showMessageDialog(null, "The file "+name.getName()+" could not be found","Open File",JOptionPane.ERROR_MESSAGE);
		}
		return readed;
	}

    private String parseString(String parse){
        parse = parse.toUpperCase();
        parse = parse.replaceAll("[^A-Z]", "");
        parse = parse.replace("J", "I");
        return parse;
    }
    
    private String[][] cipherTable(String key){
        String[][] playfairTable = new String[5][5];
        String keyString = key + "ABCDEFGHIKLMNOPQRSTUVWXYZ";
        
        // fill string array with empty string
        for(int i = 0; i < 5; i++)
          for(int j = 0; j < 5; j++)
            playfairTable[i][j] = "";
        
        for(int k = 0; k < keyString.length(); k++){
          boolean repeat = false;
          boolean used = false;
          for(int i = 0; i < 5; i++){
            for(int j = 0; j < 5; j++){
              if(playfairTable[i][j].equals("" + keyString.charAt(k))){
                repeat = true;
              }else if(playfairTable[i][j].equals("") && !repeat && !used){
                playfairTable[i][j] = "" + keyString.charAt(k);
                used = true;
              }
            }
          }
        }
        return playfairTable;
      }
    
    private Point getPoint(char c){
        Point pt = new Point(0,0);
        for(int i = 0; i < 5; i++)
          for(int j = 0; j < 5; j++)
            if(c == table[i][j].charAt(0))
              pt = new Point(i,j);
        return pt;
    }
    
    private String[] encodeDigraph(String di[]){
        String[] enc = new String[length];
        for(int i = 0; i < length; i++){
          char a = di[i].charAt(0);
          char b = di[i].charAt(1);
          int r1 = (int) getPoint(a).getX();
          int r2 = (int) getPoint(b).getX();
          int c1 = (int) getPoint(a).getY();
          int c2 = (int) getPoint(b).getY();
          
          // case 1: letters in digraph are of same row, shift columns to right
          if(r1 == r2){
            c1 = (c1 + 1) % 5;
            c2 = (c2 + 1) % 5;
            
          // case 2: letters in digraph are of same column, shift rows down
          }else if(c1 == c2){
            r1 = (r1 + 1) % 5;
            r2 = (r2 + 1) % 5;
          
          // case 3: letters in digraph form rectangle, swap first column # with second column #
          }else{
            int temp = c1;
            c1 = c2;
            c2 = temp;
          }
          
          //performs the table look-up and puts those values into the encoded array
          enc[i] = table[r1][c1] + "" + table[r2][c2];
        }
        return enc;
    }
    
    private String cipher(String in){
        length = (int) in.length() / 2 + in.length() % 2;
        
        // insert x between double-letter digraphs & redefines "length"
        for(int i = 0; i < (length - 1); i++){
          if(in.charAt(2 * i) == in.charAt(2 * i + 1)){
            in = new StringBuffer(in).insert(2 * i + 1, 'X').toString();
            length = (int) in.length() / 2 + in.length() % 2;
          }
        }
        
        // adds an x to the last digraph, if necessary
        String[] digraph = new String[length];
        for(int j = 0; j < length ; j++){
          if(j == (length - 1) && in.length() / 2 == (length - 1))
            in = in + "X";
          digraph[j] = in.charAt(2 * j) +""+ in.charAt(2 * j + 1);
        }
        
        // encodes the digraphs and returns the output
        String out = "";
        String[] encDigraphs = new String[length];
        encDigraphs = encodeDigraph(digraph);
        for(int k = 0; k < length; k++)
          out = out + encDigraphs[k];
        return out;
    }
    
    private String decryption(File selected){
    	String out = readFromFile(selected);
        String decoded = "";
        for(int i = 0; i < out.length() / 2; i++){
          char a = out.charAt(2*i);
          char b = out.charAt(2*i+1);
          int r1 = (int) getPoint(a).getX();
          int r2 = (int) getPoint(b).getX();
          int c1 = (int) getPoint(a).getY();
          int c2 = (int) getPoint(b).getY();
          if(r1 == r2){
            c1 = (c1 + 4) % 5;
            c2 = (c2 + 4) % 5;
          }else if(c1 == c2){
            r1 = (r1 + 4) % 5;
            r2 = (r2 + 4) % 5;
          }else{
            int temp = c1;
            c1 = c2;
            c2 = temp;
          }
          decoded = decoded + table[r1][c1] + table[r2][c2];
        }
        writeInToFile(decoded, selected);
        return decoded;
    }
        
    private String encryption(File selected) {
    	
    	String parse = encryKey.getText();
    	String keyword = parseString(parse);
        while(keyword.equals(""))
        	keyword = parseString(parse);
        table = this.cipherTable(keyword);
        
        String input = readFromFile(selected);
        input = parseString(input);
        while(input.equals(""))
          input = parseString(input);
        
        String output = cipher(input);
        tableOutput.setText(this.printTable(table));
        writeInToFile(output, selected);
        return output;
	}
    
	public void writeInToFile(String text,File name){
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(name));
			writer.write(text);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
    
	// prints the cipher table out for the user
    private String printTable(String[][] printedTable){
    	String tbOut = "";
    	System.out.println("This is the cipher table from the given keyword.");
    	System.out.println();
      
    	for(int i = 0; i < 5; i++){
    		for(int j = 0; j < 5; j++){
    			tbOut+=printedTable[i][j]+"\t"
    					+ "";
    		}
    		tbOut+="\n";
    	}
    	tbOut+="\n";
    	return tbOut;
    }
    
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		ideaButton.setTooltip(new Tooltip("algorithm idea's"));
		desPaneArea.setText("Play Fair Cipher :-\r\n" + 
				"E.x : Encryption key : \"Keyword\"\r\n" + 
				"createTable(\"keyword\");\r\n" + 
				"table =   K	E	Y	W	O\r\n" + 
				"	        R	D	A	B	C\r\n" + 
				"	        F	G	H	I/J	L\r\n" + 
				"		M	N	P	Q	S\r\n" + 
				"		T	U	V	X	Z\r\n" + 
				"The plaintext : \"playfair algorithm\"\r\n" + 
				"cipher(\"playfair algorithm\");\r\n" + 
				"PL : SH\r\n" + 
				"AY : HA\r\n" + 
				"FA : HR\r\n" + 
				"IR  : FB\r\n" + 
				"AL : CH\r\n" + 
				"GO : LE\r\n" + 
				"RI  : BF\r\n" + 
				"TH : VF\r\n" + 
				"MX :  QT\r\n" + 
				"the ciphertext : \"SHHAHRFBCHLEBFXFQT\"");
	}
	
}
