

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JRadioButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;




import jama.util.*;
import mpi.cbg.fly.*;

import com.sun.media.jai.widget.DisplayJAI;



public class MainGUI extends JFrame{


	SIFT fsd = new SIFT();
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String basePath =  
			"C:\\Users\\799943\\Pictures\\pics\\pics";
			//"C:\\Users\\795689\\Desktop\\Pics";

	private static final int GREATER_THAN = 0;

	private static final int LESS_THAN = 1;



	public static MainGUI mGUI = null;
	public static NaiveSimilarityFinder naive = null;




	public static int rsfd=3;
	public File file = null;
	private JPanel sotherPanel;
	private JScrollPane scrollPane2;
	public JFrame frame = new JFrame("Test");
	public  JPanel north = new JPanel();
	public JPanel east = new JPanel();
	public  JPanel west = new JPanel();
	public  JPanel south = new JPanel();

	JRadioButton surf = new JRadioButton("Surf");
	JRadioButton hist = new JRadioButton("Histogram");
	JRadioButton sift = new JRadioButton("Sift");   
	JRadioButton both = new JRadioButton("Both");      
	Container grid = getContentPane();



	public void set(int x){
		rsfd = x;
	}

	public int getss(){
		return rsfd;
	}

	public MainGUI() {
		north.setBackground(Color.cyan);
		//west.setBackground(Color.cyan);
		//east.setBackground(Color.cyan);
		//south.setBackground(Color.cyan);
		frame.setVisible(true);
		frame.setSize(1150,700);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.add(north, BorderLayout.NORTH);

		ButtonGroup group = new ButtonGroup();           
		group.add(surf);
		group.add(hist);
		group.add(sift);
		group.add(both);

		JButton button = new JButton("Browse Image");
		north.add(button);
		button.addActionListener (new Action1());

		JButton button2 = new JButton("Begin Matching");
		north.add(button2);
		button2.addActionListener (new Action2()); 

		north.add(surf);
		north.add(hist);
		north.add(sift);
		north.add(both);

		JButton button3 = new JButton("Refresh");
		north.add(button3);
		button3.addActionListener (new Action3()); 
	}

	class Action1 implements ActionListener {   
		public void actionPerformed (ActionEvent e) {     
			JFileChooser fc = new JFileChooser(basePath); 
			fc.addChoosableFileFilter(new ImageFilter());
			fc.setAcceptAllFileFilterUsed(false);
			fc.setFileView(new ImageFileView());
			fc.setAccessory(new ImagePreview(fc));
			fc.setSelectedFile(null);  
			int res = fc.showOpenDialog(null);

			if (res == JFileChooser.APPROVE_OPTION) { 
				file = fc.getSelectedFile(); 

				try {BufferedImage ref = ImageIO.read(file); 
				//JPanel west = new JPanel();
				//naive= new NaiveSimilarityFinder(file);
				west.setVisible(true);
				frame.add(west, BorderLayout.WEST);
				west.setBorder(BorderFactory.createEmptyBorder(10, 150, 100, 100));
				west.removeAll();

				Image image2 = ref.getScaledInstance(300, 300, Image.SCALE_SMOOTH);
				BufferedImage buffered = new BufferedImage(300, 300, ref.TYPE_3BYTE_BGR);
				buffered.getGraphics().drawImage(image2, 0, 0 , null);

				west.add(new DisplayJAI(buffered)); 
				frame.setVisible(true);
				setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 

				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} 
			} 

			else { 
				JOptionPane.showMessageDialog(null, 
						"You must select one image to be the reference.", "Aborting...", 
						JOptionPane.WARNING_MESSAGE); 
			}

		}
	} 

	class Action2 implements ActionListener  {        
		public void actionPerformed (ActionEvent e) {      
			try {

				if (surf.isSelected()){
					computeSurf(file);
				}
				else if (hist.isSelected()){
					naive= new NaiveSimilarityFinder(file);
					//ImageFileFilter();
				}
				else if(sift.isSelected()){
					siftAlgorithm(file);
				}
				else if (both.isSelected()){
					naive= new NaiveSimilarityFinder(file);
					if (naive.distances[1]>= 1000){
						computeSurf(file);	
					}
				}				
				else{
					JFrame frame2 = new JFrame("Invalid");
					frame2.setVisible(true);
					frame2.setSize(200,100);
					JLabel label1 = new JLabel("Select an Algorithm!");
					JPanel panel3 = new JPanel();
					frame2.add(panel3);
					panel3.add(label1);
				}
			} catch (IOException e1) {
				JFrame frame2 = new JFrame("Invalid");
				frame2.setVisible(true);
				frame2.setSize(200,100);
				JLabel label1 = new JLabel("Some Problem!");
				JPanel panel3 = new JPanel();
				frame2.add(panel3);
				panel3.add(label1);

				  e1.printStackTrace();
			}
			catch  (IllegalArgumentException e1) {
				JFrame frame2 = new JFrame("Invalid");
				frame2.setVisible(true);
				frame2.setSize(200,100);
				JLabel label1 = new JLabel("Browse for an image first!");
				JPanel panel3 = new JPanel();
				frame2.add(panel3);
				panel3.add(label1);

				// e1.printStackTrace();
			}

		}
	}   

	class Action3 implements ActionListener  {        
		public void actionPerformed (ActionEvent e) {      
			west.setVisible(false);
			east.setVisible(false);
			south.setVisible(false);
		}
	}   

	public void siftAlgorithm(File reference) throws IOException{
		BufferedImage referenceImage = ImageIO.read(reference);
		double percentage = 0;
		double topval = 0;

		Vector<Feature> list = SIFT.getFeatures(referenceImage);
		BufferedImage topImage = null;

		//BufferedImage image = ImageIO.read(reference);
		File[] surfOthers = getOtherImageFiles(reference); 
		//BufferedImage[] sothers = new BufferedImage[surfOthers.length];

		for(int o = 0; o < surfOthers.length; o++){
			BufferedImage image4 = ImageIO.read(surfOthers[o]);

			Vector<Feature> list2 = SIFT.getFeatures(image4);

			Collections.sort(list2);

			Vector<PointMatch> vector = SIFT.createMatches(list, list2, 2, new TRModel2D(), 1);

			int val = vector.size();
			percentage = (double) val/(double)list2.size()*100;
			System.out.println(val);
			System.out.println(percentage);
			if( percentage > topval){

				topval = percentage;

				topImage = image4;		
			}
		}
		
		east.setVisible(true); 
		frame.add(east, BorderLayout.EAST);
		east.setBorder(BorderFactory.createEmptyBorder(10, 100, 100, 150));
		east.removeAll();
		Image image2 = topImage.getScaledInstance(300, 300, Image.SCALE_SMOOTH);
		BufferedImage buffered = new BufferedImage(300, 300, topImage.TYPE_3BYTE_BGR);
		buffered.getGraphics().drawImage(image2, 0, 0 , null);
	

		east.add(new DisplayJAI(buffered)); 
		frame.setVisible(true); 

	}



	public void computeSurf(File reference) throws IOException {
		BufferedImage image = ImageIO.read(reference);
		File[] surfOthers = getOtherImageFiles(reference); 
		BufferedImage[] sothers = new BufferedImage[surfOthers.length]; 
		//double[] vals = new double[surfOthers.length];
		//double[] numOfMatchingPoints = new double[surfOthers.length]; 
		for (int o = 0; o < surfOthers.length; o++) { 
			sothers[o] = (BufferedImage)ImageIO.read(surfOthers[o]); 
		}
		BufferedImage[] firstHalf;
		BufferedImage[] secondHalf;
		if(sothers.length % 2 == 0){
			firstHalf = new BufferedImage[sothers.length/2];
			secondHalf = new BufferedImage[sothers.length/2];
		}
		else{
			firstHalf = new BufferedImage[sothers.length/2];
			secondHalf = new BufferedImage[sothers.length/2 + 1];
		}
		int secondHalfIndex = 0;
		for(int i = 0; i < sothers.length; i++){
			if(i < sothers.length/2)
				firstHalf[i] = sothers[i];
			else{
				secondHalf[secondHalfIndex] = sothers[i];
				secondHalfIndex++;
			}
		}

		ExecuteComparisons ec1 = new ExecuteComparisons(image, firstHalf);
		ExecuteComparisons ec2 = new ExecuteComparisons(image, secondHalf);

		ec1.start();
		ec2.start();

		BufferedImage topMatch = null;
		double val;

		while(true){
			if(!ec1.isAlive() && !ec2.isAlive()){
				if(ec1.topVal > ec2.topVal){
					topMatch = ec1.getTopImage();
					val = ec1.topVal;
				}
				else{
					topMatch = ec2.getTopImage();
					val = ec2.topVal;
				}
				break;
			}
		}

		//JPanel east = new JPanel();
		east.setVisible(true); 
		frame.add(east, BorderLayout.EAST);
		east.setBorder(BorderFactory.createEmptyBorder(10, 100, 100, 150));
		east.removeAll();
		Image image2 = topMatch.getScaledInstance(300, 300, Image.SCALE_SMOOTH);
		BufferedImage buffered = new BufferedImage(300, 300, topMatch.TYPE_3BYTE_BGR);
		buffered.getGraphics().drawImage(image2, 0, 0 , null);
	

		east.add(new DisplayJAI(buffered)); 
		frame.setVisible(true); 
	}

	public void addImagesToScrollPaneHistogram(JPanel otherPanel, JScrollPane scrollPane, double[] distances, 
			RenderedImage[] rothers, File[] others){
		Toolkit t = Toolkit.getDefaultToolkit();
		//int numThroughLoop = 0;
		for (int o = 1; o < others.length; o++) {
			//if(distances[o] > 1050.0) break;
			JPanel selection = new JPanel();
			selection.setLayout(new BoxLayout(selection, BoxLayout.X_AXIS));
			selection.add(new DisplayJAI(rothers[o])); 
			selection.add(Box.createHorizontalStrut(5));
			JLabel ldist = new JLabel("<html>" + others[o].getName() + "<br>" 
					+ String.format("% 13.3f", distances[o]) + "<br>" +
					String.format("% 13.3f", (distances[o]/11041.824)*100) +"%" + "</html>"); 
			ldist.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 36)); 
			//System.out.printf("<td class=\"simpletable legend\"> "+ 
			//	"<img src=\"MiscResources/ImageSimilarity/icons/miniicon_%s\" "+ 
			//	"alt=\"Similarity result\"><br>% 13.3f</td>\n", others[o].getName(),distances[o]); 
			selection.add(ldist);
			otherPanel.add(selection);
			//numThroughLoop++;
		} 
		int max = Math.max(others.length * 300, 310);
		scrollPane.setPreferredSize(new Dimension(550, Math.min(t.getScreenSize().height - 80, max)));

	}

	public void addImagesToScrollPaneSurf(double[] vals, RenderedImage[] sothers, File[] others){
		Toolkit t = Toolkit.getDefaultToolkit();
		for (int o = 1; o < others.length; o++) {
			//if(distances[o] > 1050.0) break;
			JPanel selection = new JPanel();
			selection.setLayout(new BoxLayout(selection, BoxLayout.X_AXIS));
			selection.add(new DisplayJAI(sothers[o])); 
			selection.add(Box.createHorizontalStrut(5));
			JLabel ldist = new JLabel("Val: " + vals[o]);
			//ldist.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 36)); 
			//System.out.printf("<td class=\"simpletable legend\"> "+ 
			//	"<img src=\"MiscResources/ImageSimilarity/icons/miniicon_%s\" "+ 
			//	"alt=\"Similarity result\"><br>% 13.3f</td>\n", others[o].getName(),distances[o]); 
			selection.add(ldist);
			sotherPanel.add(selection);
		}
		int max = Math.max(others.length * 300, 310);
		scrollPane2.setPreferredSize(new Dimension(550, Math.min(t.getScreenSize().height - 80, max)));
	}

	public void sort(File[] others, double[] distances, Object[] rothers, int sort){
		for (int p1 = 0; p1 < others.length - 1; p1++) 
			for (int p2 = p1 + 1; p2 < others.length; p2++) { 
				if ((sort == GREATER_THAN && distances[p1] > distances[p2]) || 
						(sort == LESS_THAN && distances[p1] < distances[p2])) { 
					double tempDist = distances[p1]; 
					distances[p1] = distances[p2]; 
					distances[p2] = tempDist; 

					Object tempR = rothers[p1]; 
					rothers[p1] = rothers[p2]; 
					rothers[p2] = tempR; 

					File tempF = others[p1]; 
					others[p1] = others[p2]; 
					others[p2] = tempF; 
				} 
			} 
	}

	private File[] getOtherImageFiles(File reference) { 
		File dir = new File(reference.getParent()); 
		// List all the image files in that directory. 
		File[] others = dir.listFiles(new JPEGImageFileFilter(reference.getName())); 
		return others; 
	} 

	public static void main(String[] args) throws IOException {
		mGUI = new MainGUI(); 
	}

}

class ExecuteComparisons extends Thread{

	BufferedImage mainImage;
	BufferedImage[] images;
	double[] vals;
	double topVal;
	int topValIndex;

	public ExecuteComparisons(BufferedImage mainImage, BufferedImage[] images){
		this.mainImage = mainImage;
		this.images = images;
		vals = new double[images.length];
		topVal = 0.0;
		topValIndex = 0;
	}

	public void run() {
		for(int i = 0; i < images.length; i++){
			vals[i] = new SurfCompare(mainImage, images[i]).matchesInfo();
			if(vals[i] > topVal){
				topVal = vals[i];
				topValIndex = i;
			}
		}

	}



	public double[] getVals(){
		return vals;
	}

	public double getTopVal(){
		return topVal;
	}

	public BufferedImage getTopImage(){
		return images[topValIndex];
	}

	public static void main(String args[]) {

	}


}
