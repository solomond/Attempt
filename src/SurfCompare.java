import java.awt.*;

import java.awt.image.BufferedImage;

import java.io.*;

import java.util.List;

import java.util.Map;



import javax.imageio.ImageIO;

import javax.swing.*;

import com.stromberglabs.jopensurf.SURFInterestPoint;
import com.stromberglabs.jopensurf.Surf;





public class SurfCompare extends JPanel {

	private static final long serialVersionUID = 1L;



	private static final int BASE_CIRCLE_DIAMETER = 8;

	private static final int TARGET_CIRCLE_DIAMETER = 4;

	private static final int UNMATCHED_CIRCLE_DIAMETER = 4;



	private BufferedImage image;

	private BufferedImage imageB;

	private float mImageAXScale = 0;

	private float mImageAYScale = 0;

	private float mImageBXScale = 0;

	private float mImageBYScale = 0;

	private int mImageAWidth = 0;

	private int mImageAHeight = 0;

	private int mImageBWidth = 0;

	private int mImageBHeight = 0;

	private Surf mSurfA;

	private Surf mSurfB;



	private Map<SURFInterestPoint,SURFInterestPoint> mAMatchingPoints;

	private Map<SURFInterestPoint,SURFInterestPoint> mBMatchingPoints;



	private boolean mUpright = false;



	public SurfCompare(BufferedImage image,BufferedImage imageB){

		this.image = image;

		this.imageB = imageB;

		mSurfA = new Surf(image);

		mSurfB = new Surf(imageB);



		mImageAXScale = 1; //(float)Math.min(image.getWidth(),800)/(float)image.getWidth();

		mImageAYScale = 1; //(float)Math.min(image.getHeight(),800 * (float)image.getHeight()/(float)image.getWidth())/(float)image.getHeight();



		mImageBXScale = 1; //(float)Math.min(imageB.getWidth(),800)/(float)imageB.getWidth();

		mImageBYScale = 1; //(float)Math.min(imageB.getHeight(),800 * (float)imageB.getHeight()/(float)imageB.getWidth())/(float)imageB.getHeight();



		mImageAWidth = (int)(image.getWidth() * mImageAXScale);

		mImageAHeight = (int)(image.getHeight() * mImageAYScale);

		mImageBWidth = (int)(imageB.getWidth() * mImageBXScale);

		mImageBHeight = (int)(imageB.getHeight() * mImageBYScale);

		//System.out.println("AWidth: "  + mImageAWidth + "\n" +
				//"AHeight: "  + mImageAHeight + "\n" +
				//"BWidth: "  + mImageBWidth + "\n" +
			//	"BHeight: "  + mImageBHeight);



		mAMatchingPoints = mSurfA.getMatchingPoints(mSurfB,mUpright);

		mBMatchingPoints = mSurfB.getMatchingPoints(mSurfA,mUpright);

	}



	/**

	 * Drawing an image can allow for more

	 * flexibility in processing/editing.

	 */

	@Override
	protected void paintComponent(Graphics g) {

		// Center image in this component.

		g.drawImage(image,0,0,mImageAWidth,mImageAHeight,this);

		g.drawImage(imageB,mImageAWidth,0,mImageBWidth,mImageBHeight,Color.WHITE,this);



		//if there is a surf descriptor, go ahead and draw the points

		if ( mSurfA != null && mSurfB != null ){

			drawIpoints(g,mUpright ? mSurfA.getUprightInterestPoints() : mSurfA.getFreeOrientedInterestPoints(),0,mImageAXScale,mImageAYScale);

			drawIpoints(g,mUpright ? mSurfB.getUprightInterestPoints() : mSurfB.getFreeOrientedInterestPoints(),mImageAWidth,mImageBXScale,mImageBYScale);

			drawConnectingPoints(g);

		}

	}



	private void drawIpoints(Graphics g,List<SURFInterestPoint> points,int offset,float xScale,float yScale){

		Graphics2D g2d = (Graphics2D)g;

		g2d.setColor(Color.RED);

		for ( SURFInterestPoint point : points ){

			if ( mAMatchingPoints.containsKey(point) || mBMatchingPoints.containsKey(point) ) continue;

			int x = (int)(xScale * point.getX()) + offset;

			int y = (int)(yScale * point.getY());

			g2d.drawOval(x-UNMATCHED_CIRCLE_DIAMETER/2,y-UNMATCHED_CIRCLE_DIAMETER/2,UNMATCHED_CIRCLE_DIAMETER,UNMATCHED_CIRCLE_DIAMETER);

		}

		//g2d.setColor(Color.GREEN);

		//for ( SURFInterestPoint point : commonPoints ){

		//      int x = (int)(xScale * point.getX()) + offset;

		//      int y = (int)(yScale * point.getY());

		//      g2d.drawOval(x,y,8,8);

		//}

	}



	private void drawConnectingPoints(Graphics g){

		Graphics2D g2d = (Graphics2D)g;

		g2d.setColor(Color.GREEN);

		int offset = mImageAWidth;

		for ( SURFInterestPoint point : mAMatchingPoints.keySet() ){

			int x = (int)(mImageAXScale * point.getX());

			int y = (int)(mImageAYScale * point.getY());

			g2d.drawOval(x-BASE_CIRCLE_DIAMETER/2,y-BASE_CIRCLE_DIAMETER/2,BASE_CIRCLE_DIAMETER,BASE_CIRCLE_DIAMETER);

			SURFInterestPoint target = mAMatchingPoints.get(point);

			int tx = (int)(mImageBXScale * target.getX()) + offset;

			int ty = (int)(mImageBYScale * target.getY());

			g2d.drawOval(tx-TARGET_CIRCLE_DIAMETER/2,ty-TARGET_CIRCLE_DIAMETER/2,TARGET_CIRCLE_DIAMETER,TARGET_CIRCLE_DIAMETER);

			g2d.drawLine(x,y,tx,ty);

		}

		g2d.setColor(Color.BLUE);

		for ( SURFInterestPoint point : mBMatchingPoints.keySet() ){

			int x = (int)(mImageBXScale * point.getX()) + offset;

			int y = (int)(mImageBYScale * point.getY());

			g2d.drawOval(x-BASE_CIRCLE_DIAMETER/2,y-BASE_CIRCLE_DIAMETER/2,BASE_CIRCLE_DIAMETER,BASE_CIRCLE_DIAMETER);

			SURFInterestPoint target = mBMatchingPoints.get(point);

			int tx = (int)(mImageAXScale * target.getX());

			int ty = (int)(mImageAYScale * target.getY());

			g2d.drawOval(tx-TARGET_CIRCLE_DIAMETER/2,ty-TARGET_CIRCLE_DIAMETER/2,TARGET_CIRCLE_DIAMETER,TARGET_CIRCLE_DIAMETER);

			g2d.drawLine(x,y,tx,ty);

		}

	}



	public void display(){

		JFrame f = new JFrame();

		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JScrollPane pane = new JScrollPane(this);
		pane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		pane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		f.getContentPane().add(pane, BorderLayout.CENTER);

		//f.setSize(mImageAWidth+mImageBWidth,Math.max(mImageAHeight,mImageBHeight));
		//f.setPreferredSize(new Dimension(mImageAWidth+mImageBWidth,Math.max(mImageAHeight,mImageBHeight)));
		//f.setMinimumSize(new Dimension(mImageAWidth+mImageBWidth,Math.max(mImageAHeight,mImageBHeight)));
		//f.setMaximumSize(new Dimension(mImageAWidth+mImageBWidth,Math.max(mImageAHeight,mImageBHeight)));
		
		pane.setSize(new Dimension(1400, 800));
		pane.setPreferredSize(new Dimension(1400, 800));
		pane.setMinimumSize(new Dimension(1400, 800));
		pane.setMaximumSize(new Dimension(1400, 800));
		
		//f.setSize(new Dimension(1400, 850));
		//f.setPreferredSize(new Dimension(1400, 850));
		//f.setMinimumSize(new Dimension(1400, 850));
		//f.setMaximumSize(new Dimension(1400, 850));
		
		f.setLocation(0,0);
		f.pack();
		//pane.validate();
		f.setVisible(true);
		pane.revalidate();
	}



	public double matchesInfo(){

		Map<SURFInterestPoint,SURFInterestPoint> pointsA = mSurfA.getMatchingPoints(mSurfB,true);

		Map<SURFInterestPoint,SURFInterestPoint> pointsB = mSurfB.getMatchingPoints(mSurfA,true);

	//	System.out.println("There are: " + pointsA.size() + " matching points of " + mSurfA.getUprightInterestPoints().size());

		//System.out.println("There are: " + pointsB.size() + " matching points of " + mSurfB.getUprightInterestPoints().size());
		
		double x = ((double)pointsA.size()+(double)pointsB.size()) / 2.0;
		
		double y = ((double)mSurfA.getUprightInterestPoints().size() + (double)mSurfB.getUprightInterestPoints().size()) / 2.0;
		
		//System.out.println((double)(x));
		return x;
	}



	public static void main(String[] args) throws IOException {

		BufferedImage imageA = ImageIO.read(new File(args[0]));

		BufferedImage imageB = ImageIO.read(new File(args[1]));

		//        System.out.println(imageA);

		//        System.out.println(imageB);

		SurfCompare show = new SurfCompare(imageA,imageB);

		show.display();

		//show.matchesInfo();

	}

}

