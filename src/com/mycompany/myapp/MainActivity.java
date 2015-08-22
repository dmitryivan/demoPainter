package com.mycompany.myapp;
import java.io.*;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.app.Activity;
import android.view.Display;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.os.*;
import android.graphics.Rect;
import android.graphics.*;

public class MainActivity extends Activity { 		
	static View v;
    @Override
    public void onCreate(Bundle savedInstanceState)
        {super.onCreate(savedInstanceState); 
		v=new DV(this);
		setContentView(v);
		}

       	   
	class DV extends View {			
		Paint p;
		int maxHistory=100000;
		float x;
		float y;
		float oldX;
		float oldY;
		float evX;
		float evY;
		float maxRad;
		int figType=1; //default figure - line
		int cnt;     //current figure
		int col=-16711936;
		int [] figC = new int [maxHistory];  //figure color
		int [] figT = new int [maxHistory];  //figure type
		float [] figX1 = new float [maxHistory]; //coordinates: top x,y; down x,y
		float [] figY1 = new float [maxHistory];
		float [] figX2 = new float [maxHistory];
		float [] figY2 = new float [maxHistory];		
		final int maxX;
		final int maxY;
		String st;
		Bitmap bitmap;
		Bitmap b;
		Rect rect;
		FileOutputStream fos=null;
		boolean inMenu;
		Context con;
		
	DV (Context c){
		super(c);
		con=c;
		Display display = getWindowManager().getDefaultDisplay();
		maxX=display.getWidth();	
		maxY=display.getHeight();
        p=new Paint();
		p.setColor(Color.GREEN);
		p.setStrokeWidth(5);		
		bitmap=BitmapFactory.decodeResource(getResources(), R.drawable.i1);
		rect=new Rect(0,0,maxX,maxY-50);
		}
	
	void saveFile(){
     	 b=v.getDrawingCache();		
	     try{
    	     b.compress(Bitmap.CompressFormat.JPEG,95,
             new FileOutputStream( Environment.getExternalStorageDirectory()+"/aaaa.jpg"));			  
		 }catch (Exception e) { e.printStackTrace();}		
		 v.setDrawingCacheEnabled(false);
    }
	
		public void takeScreenshot(Activity activity) {  
			View view = activity.getWindow().getDecorView();  
			Bitmap bitmap = view.getDrawingCache();  			 
			try{
				bitmap.compress(Bitmap.CompressFormat.JPEG,95,
						   new FileOutputStream( Environment.getExternalStorageDirectory()+"/aaaa.jpg"));	
			}catch (Exception e) { e.printStackTrace();}
			}
			
	protected void onDraw (Canvas canvas)
	   {		      
	       canvas.drawBitmap(bitmap, null, rect,  p); //draw menu 

		   for (int i=0; i<cnt; i++){ //draw history
			   p.setColor(figC[i]);
			  
			   if (figT[i]==1) canvas.drawLine (figX1[i], figY1[i], figX2[i], figY2[i], p); 
			   if (figT[i]==2) canvas.drawRect (figX1[i], figY1[i], figX2[i], figY2[i], p);
			   if (figT[i]==3) canvas.drawLine (figX1[i], figY1[i], figX2[i], figY2[i], p);
		       if (figT[i]==4) canvas.drawCircle (figX1[i], figY1[i], figX2[i], p); 
			   }

		   p.setColor(col);   
		   if (figType==1) canvas.drawLine (oldX, oldY, x, y, p); 
		   if (figType==2) canvas.drawRect (oldX, oldY, x, y, p); //preview current figure
		   if (figType==3) canvas.drawLine (oldX, oldY, x, y, p);
		   if (figType==4) canvas.drawCircle (oldX, oldY, maxRadius(), p); 
		   if (figType==5) takeScreenshot((Activity)con);
		   }
	
	void toolMenu(){
		if (evY>1 & evY<50)    figType=1;				   
		if (evY>50 & evY<100)  figType=2;
		if (evY>100 & evY<150) figType=3;
		if (evY>150 & evY<200) figType=4;
		if (evY>200 & evY<250) figType=5;
		if (evY>250 & evY<300) figType=6; 
		if (evY>300)           figType=7;
    	inMenu=true;
		}	
		   
	void colorMenu(){
		if (evY>1 & evY<50)    col=-16711936;				   
		if (evY>50 & evY<100)  col=-16776961;
		if (evY>100 & evY<150) col=-16711681;
		if (evY>150 & evY<200) col=-16777216;
		if (evY>200 & evY<250) col=-65281;
		if (evY>250 & evY<300) col=-65536;
		if (evY>300)           col=-256;
		inMenu=true;
    	}	
	
	void saveMov(){
		figC[cnt]=col;
		figT[cnt]=figType;
		figX1[cnt]= oldX;
		figY1[cnt]= oldY;
		if (figType==4) {
			figX2[cnt]=maxRadius();
		} else	figX2[cnt]= x;
		figY2[cnt]= y;
		cnt++;
		}
	
	float maxRadius(){
		maxRad=(float)Math.hypot(x-oldX,y-oldY);
		if ((oldX+maxRad)>(maxX-50)) maxRad=maxX-50-oldX;
		if (oldX-maxRad<50) maxRad=oldX-50;
		return maxRad;
	}	
		
		
	boolean inBorders(){
		if (evX>50 && evX<maxX-50){return true;} 
		else {return false;}
	    }
	
	 @Override
	 public boolean onTouchEvent (MotionEvent event)
     {   
		 evX = event.getX();
		 evY = event.getY();
			
			 switch (event.getAction())
	    	 {
			     case MotionEvent.ACTION_DOWN:	
				   if (evX>maxX-50) colorMenu(); //select color
				   if (evX<50) toolMenu();  //select figure type
				   if (inBorders()) {
								 x=oldX=evX;
						         y=oldY=evY;
								 }
				       break;
					 
				 case MotionEvent.ACTION_MOVE:  
					 {	
					 if (inBorders())
						 {
					      if (figType==1){ //if figure is Line - save, else - resize
							 oldX=x;
							 oldY=y;
							 x = evX;
							 y = evY;
							 saveMov();
					      } else {
							 x = evX;
							 y = evY;
							 }
						  }
					 } break;
					 
				 case MotionEvent.ACTION_UP: 
		             if (inMenu) {inMenu=false; break;} 
					 if (!inBorders())
					{
					    if (evX<50) x=50;
					    if (evX>maxX-50) x=maxX-50;
					 }
				     saveMov();
					 break;			
		       }
			 
	    invalidate(); 
	    return true;
        }
    }
}
