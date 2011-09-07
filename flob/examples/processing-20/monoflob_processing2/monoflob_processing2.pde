/*
  monoflob 
  (vs. 20090621)
  
  started out as buttons to trigger things, ended up
  with a nice fade engine in the buttons due to presence
  this works really sweet connected to soundfile banks gains  
  loosely inspired from the great and simple monome
  
  andré sier 
  http://s373.net
 
 
 updated for processing 2.0
 */

import processing.opengl.*;
import processing.video.*;
import s373.flob.*;

/// vars
Capture video;
PImage vrImage;
Flob flob; 
ArrayList blobs;

/// video params
int tresh = 10;
int fade = 25;//120;
int om = 1;
int videores=128;
boolean drawimg=true;//false;//true;
String info="";
PFont font;
int videotex = 0;//3;//0;
int colormode = flob.BLUE;
float fps = 60;

Monoflob mono;

void setup(){
  size(700,700,OPENGL);
  frameRate(fps);
  rectMode(CENTER);

  video = new Capture(this, 320, 240, 30);// (int)fps);  
  video.start();
  
  vrImage = createImage(videores, videores, RGB);
  
  flob = new Flob(this, vrImage); 

  flob.setTresh(tresh).setImage(videotex).setMirror(true,false);
  flob.setOm(1).setFade(fade).setMinNumPixels(20).setMaxNumPixels(500);
  flob.setColorMode(colormode);
  
  flob.setBackground(vrImage).setImage(videotex); /// must init internal video text's prior to requesting them

  font = createFont("monaco",9);
  textFont(font);

  mono = new Monoflob(4,4);
  //(20,20);//(10,10);//(3,3);//(5,4);
}



boolean videoWasAvailable = false;

void draw(){

  background(0);
  
  if(video.available()) {
    video.read();
    vrImage.copy(video, 0, 0, 320, 240, 0, 0, videores, videores);
    blobs = flob.calc(flob.binarize(vrImage)); 
 
     if(!videoWasAvailable){
      videoWasAvailable = true;
      println("video avail "+frameCount);
     }   

  }

  if(drawimg)
    image(flob.getImage(), 0, 0, width, height);

  rectMode(CENTER);
  int numblobs = flob.getNumBlobs();  
  for(int i = 0; i < numblobs; i++) {
    ABlob ab = (ABlob)flob.getABlob(i);     
    mono.touch(ab.cx,ab.cy, ab.dimx, ab.dimy);
    fill(0,0,255,100);
    rect(ab.cx,ab.cy,ab.dimx,ab.dimy);
    fill(0,255,0,200);
    rect(ab.cx,ab.cy, 5, 5);
    info = ""+ab.id+" "+ab.cx+" "+ab.cy;
    text(info,ab.cx,ab.cy+20);
  }

  mono.render();

  // stats
  fill(255,152,255);
  rectMode(CORNER);
  rect(5,5,flob.getPresencef()*width,10);
  String stats = ""+frameRate+"\nflob.numblobs: "+numblobs+"\nflob.thresh:" +tresh+
                 " <t/T>"+"\nflob.fade: "+fade+"   <f/F>"+"\nflob.om: "+flob.getOm()+
                 "\nflob.image: "+videotex+"\nflob.colormode: "+flob.getColorMode()+"\nflob.presence:"+flob.getPresencef();
  fill(0,255,0);
  text(stats,5,25);

}


void keyPressed(){
  if(key=='b')
    drawimg^=true;
//  if (key=='S')
//    video.settings();
  if (key=='s')
    saveFrame("monoflob-######.png");
  if (key=='i'){  
    videotex = (videotex+1)%4;
    flob.setImage(videotex);
  }
  if(key=='t'){
    tresh--;
    flob.setTresh(tresh);
  }
  if(key=='T'){
    tresh++;
    flob.setTresh(tresh);
  }   
  if(key=='f'){
    fade--;
    flob.setFade(fade);
  }
  if(key=='F'){
    fade++;
    flob.setFade(fade);
  }   
  if(key=='o'){
    om^=1;
    flob.setOm(om);
  }   
  if(key=='c'){
    colormode=(colormode+1)%5;
    flob.setColorMode(colormode);
  }   
 if(key==' ') //space clear flob.background
    flob.setBackground(video);
}



