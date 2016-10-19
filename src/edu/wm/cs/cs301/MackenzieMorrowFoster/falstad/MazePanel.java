package edu.wm.cs.cs301.MackenzieMorrowFoster.falstad;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Bitmap.Config;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Handles maze graphics.
 */
public class MazePanel extends View  {
    // TODO: please check http://developer.android.com/guide/topics/graphics/2d-graphics.html
    // on how to implement your own View class
	
	private Bitmap myBit;
	private Canvas myCanvas;
	private Paint myPaint;
	private static int myColor;
	
	
    /**
     * Constructor with one context parameter.
     * @param context
     */
    public MazePanel(Context context) {
	// call super class constructor as necessary
	// TODO: initialize instance variables as necessary
    	super(context); //???????
    	myPaint = new Paint();
    	myPaint.setStyle(Style.FILL);
    	this.myBit = Bitmap.createBitmap(786, 1280, Config.ARGB_8888);
    	myCanvas = new Canvas(myBit);
    	myColor = 0;
    }
    
    
    /**
     * Constructor with two parameters: context and attributes.
     * @param context
     * @param app
     */
    public MazePanel(Context context, AttributeSet app) {
	// call super class constructor as necessary
	// TODO: initialize instance variables as necessary
    	super(context, app);
    	myPaint = new Paint();
    	myPaint.setStyle(Style.FILL);
    	this.myBit = Bitmap.createBitmap(786, 1280, Config.ARGB_8888);
    	myCanvas = new Canvas(myBit);
    	myColor = 0;
    }
    
    
    /**
     * Draws given canvas.
     * @param canvas
     */
    public void onDraw(Canvas c) {
    	//from Bitmap class; draw a bitmap
    	super.onDraw(c);
    	c.drawBitmap(myBit, 0, 0, myPaint);
    	Log.v("MazePanel", "Drawing on the canvas with onDraw()");
    }
    
    
    /**
     * Measures the view and its content to determine the measured width and the measured height.
     * @param width
     * @param height
     */
    @Override
    public void onMeasure(int width, int height) {
	// as described for superclass method
    	super.onMeasure(width, height);
    }
    
    
    /**
     * Updates maze graphics.
     */
    public void update() {
	//TODO: update maze graphics
    	invalidate();
    	Log.v("MazePanel", "Updating the maze graphics with update()");
    }

    
    
    /**
     * Sets paint object color attribute to given color.
     * @param color
     */
    public void setColor(int color) {
    	//from Paint class; sets the current color
    	myPaint.setColor(color);
    }
    
    
    /**
     * Takes in color integer values [0-255], returns corresponding color-int value. 
     * @param color values
     */
    public static int getColorEncoding(int red, int green, int blue) {
    	//given the integers for red, green, and blue
    	//it combines them into one color and returns the integer value of that color
    	//rgb is from the Color class
    	myColor = Color.rgb(red, green, blue);
    	return myColor;
    }
    
    
    /**
     * Returns the RGB value representing the current color. 
     * @return integer RGB value
     */
    public int getColor() {
    	//returns the integer value of the current color setting
    	return myColor;
    }
    
    
    /**
     * Takes in rectangle params, fills rectangle in canvas based on these. 
     * @param x
     * @param y
     * @param width
     * @param height
     */
    public void fillRect(int x, int y, int width, int height) {
    	// draw a filled rectangle on the canvas, requires decision on its color
    	//myCanvas.drawRect(left, top, right, bottom, type);
    	Rect rect = new Rect(x, y, x+width, y+height);
    	myCanvas.drawRect(rect, myPaint);
    }
    
    
    /**
     * Takes in polygon params, fills polygon in canvas based on these. 
     * Paint is always that for corn.
     * @param xPoints
     * @param yPoints
     * @param nPoints
     */
    public void fillPolygon(int[] xPoints, int[] yPoints, int nPoints){
	// translate the points into a path
	// draw a path on the canvas
    	//from the Path class
    	Path polygon = new Path();
    	polygon.reset();
		polygon.moveTo(xPoints[0], yPoints[0]);
		for(int i = 1; i < xPoints.length; i++){
			int x = xPoints[i];
			int y = yPoints[i];
			polygon.lineTo(xPoints[i], yPoints[i]);	
		}
		polygon.lineTo(xPoints[0], yPoints[0]);
    	myCanvas.drawPath(polygon, myPaint);
    }
    
    
    /**
     * Takes in line params, draws line in canvas based on these. 
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     */
    public void drawLine(int x1, int y1, int x2, int y2) {
    	//from Canvas class; draws a line on the canvas
    	myCanvas.drawLine(x1, y1, x2, y2, myPaint);
    }
    
    
    /**
     * Takes in oval params, fills oval in canvas based on these. 
     * @param x
     * @param y
     * @param width
     * @param height
     */
    public void fillOval(int x, int y, int width, int height) {
	// TODO: draw an oval on the canvas
    	//RectF(float left, float top, float right, float bottom)
    	RectF oval = new RectF(x, y+height, x+width, y);
    	myCanvas.drawOval(oval, myPaint);
    }
}