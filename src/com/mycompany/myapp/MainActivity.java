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

public class MainActivity extends Activity { 		
       
    @Override
    public void onCreate(Bundle savedInstanceState)
        {super.onCreate(savedInstanceState); setContentView(new DV(this));}
       	   
	class DV extends View {			
		Paint p;
		int dflag=2;
		float x = 0;
		float y = 0;
		float evX;
		float evY;
		int cnt=0;
		int col=-16711936;
		int lc=-16711936;
		int mc=-16711936;
		int lcnt=0;
		String st;
		Bitmap bitmap;
		Rect rect;
		float [] mtx = new float[2000];
		float [] mty = new float[2000];
	    float [] dtx = new float[2000];
		float [] dty = new float[2000];
		float [] mcol= new float[2000];
		float [] ml =  new float[100000];
	    float [] ml2 = new float[25000];
		float oldX;
		float oldY;
		final int maxX;
		final int maxY;
		FileOutputStream fos=null;
		
	DV (Context c){
		super(c);
		Display display = getWindowManager().getDefaultDisplay();
		maxX=display.getWidth();	
		maxY=display.getHeight();
        p=new Paint();
		p.setColor(Color.GREEN);
		p.setStrokeWidth(5);
		
		//st=Environment.getExternalStorageDirectory().toString()+"appproject/My App/res/1.jpg";
		//bitmap=BitmapFactory.decodeFile(st);
		bitmap=BitmapFactory.decodeResource(getResources(), R.drawable.i1);
		rect=new Rect(0,0,maxX,maxY-50);
	    //bitmap = Bitmap.createBitmap(bitmapS,2,2,100,100);
		//Canvas canvas=new Canvas(bitmap);
		//bitmap.compress(Bitmap,CompressFormat.JPEG,100,fos);
		}
		
				
	protected void onDraw (Canvas canvas)
	   {		      
		   canvas.drawBitmap(bitmap, null, rect,  p);
		 
		   for (int i=0; i<cnt; i++){
			   p.setColor((int)mcol[i]);
			   canvas.drawRect (mtx[i], mty[i], dtx[i], dty[i], p);}
		   for (int i=0; i<lcnt; i=i+4){
			   p.setColor((int)ml2[i/4]);
			   canvas.drawLine((int)ml[i],(int)ml[i+1],(int)ml[i+2],(int)ml[i+3], p);}
		       
		   p.setColor(col);   
		   if (dflag==1) canvas.drawRect (oldX, oldY, x, y, p);
	   }
		
	public void colorMenu(){
		if (evY>2 & evY<50)  col=-16711936;				   
		if (evY>50&evY<100)  col=-16776961;
		if (evY>100&evY<150) col=-16711681;
		if (evY>150&evY<200) col=-16777216;
		if (evY>200&evY<250) col=-65281;
		if (evY>250&evY<300) col=-65536;
		if (evY>300) col=-256;
	}	
	
	void drawMove(){
		oldX=x;
		oldY=y;
		x = evX;
		y = evY;	 
		ml[lcnt]=oldX;
		ml[lcnt+1]=oldY;
		ml[lcnt+2]=x;
		ml[lcnt+3]=y;
		ml2[lcnt/4]=col;
		lcnt=lcnt+4;
	}
		
		boolean inBorders(float x, float y){
			if (x>50 && x<maxX-50){return true;} 
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
				   if (evX>maxX-50) {colorMenu();} else { 
		     		   if (evX<50){
							 if (evY<50){dflag=2;}else{ dflag=1; }
							 
							 }}
							 if (evX>50 && evX<975){
								 oldX=evX;
						         oldY=evY;
				                 x=evX+1;
						         y=evY+1;
							 }
						 break;
					 
				 case MotionEvent.ACTION_MOVE:  
					 {	
					 if (inBorders(evX,evY)){if (dflag==1){			
						 x = evX;
						 y = evY;	
						 }	else {
							 if (evX-x>1 | x-evX>1)	{ drawMove(); }
					         }
					 }} break;
					 
				 case MotionEvent.ACTION_UP: 
			     if (inBorders(evX,evY)){    
				 if (dflag==1){
				     mtx[cnt]=oldX;
					 mty[cnt]=oldY;
					 dtx[cnt]=x;
					 dty[cnt]=y;
					 mcol[cnt]=col;
					 cnt++;
					 break;		
					 }	}
		       }
			 
	    invalidate(); 
	    return true;
        }
    }
}
