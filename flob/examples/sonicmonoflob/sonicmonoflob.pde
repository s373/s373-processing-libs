/*
  sonicmonoflob
  (vs. 20101220)
  
  sonic version of monoflob, here a polyphonic 49 voice synth (7x7 monoflob)
  where each button has a sine wave you can fade or turn on/off (b)
  
  andré sier 
  http://s373.net
 
 */
float wavemaxamp = 0.11;

import processing.opengl.*;
import processing.video.*;
import s373.flob.*;
import ddf.minim.*;
import ddf.minim.signals.*;
/// vars
Capture video;
Flob flob; 
ArrayList blobs=new ArrayList();
PImage videoinput;
boolean buttonOm=false;
/// video params
int tresh = 10;
int fade = 25;//120;
int om = 1;
int videores=128;
boolean drawimg=true;
String info="";
PFont font;
int videotex = 3;//0;
int colormode = flob.BLUE;
float fps = 60;

Monoflob mono;
Minim minim;
AudioOutput out;

void setup(){
//  //bug 882 processing 1.0.1
//  try { quicktime.QTSession.open(); } 
//  catch (quicktime.QTException qte) { qte.printStackTrace(); }

  size(700,700,OPENGL);
  frameRate(fps);
  rectMode(CENTER);

  video = new Capture(this, 320, 240, (int)fps); 
  video.start();
  
  videoinput = createImage(videores, videores, RGB);
  flob = new Flob(this, videoinput); 

  flob.setTresh(tresh).setImage(videotex).setMirror(true,false);
  flob.setOm(1).setFade(fade).setMinNumPixels(20).setMaxNumPixels(2500);
  flob.setColorMode(colormode);

  font = createFont("monaco",16);
  textFont(font);

  minim = new Minim(this);  
  out = minim.getLineOut(Minim.STEREO);

  mono = new Monoflob(7,7);
  //(20,20);//(10,10);//(3,3);//(5,4);
}



void draw(){

  if(video.available()) {
    video.read();
    videoinput.copy(video, 0, 0, 320, 240, 0, 0, videores, videores);
    blobs = flob.calc(flob.binarize(videoinput));    
  }

  if(drawimg)
    image(flob.getImage(), 0, 0, width, height);

  rectMode(CENTER);
  int numblobs = blobs.size();//flob.getNumBlobs();  
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
                 "\nflob.image: "+videotex+"\nflob.colormode: "+flob.getColorMode()+"\nflob.presence:"+flob.getPresencef()
                 +"\nbuttonOm: "+buttonOm +" (b)";
  fill(0,255,0);
  text(stats,5,25);
  
  int ns = out.bufferSize();
  float x = (float) width / ns;
  for(int i=0; i<ns-1;i++){
    line(i * x, height/2 + out.mix.get(i)*height/2, 
          x * (i+1),  height/2 + out.mix.get(i+1)*height/2);
  }

}


void keyPressed(){
  if(key=='b')
    buttonOm^=true;
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
    flob.setBackground(videoinput);
}


float mtof(float midi){  
  return  (440.0f * pow(2, ((midi-69.0) / 12.0)) );  
}
