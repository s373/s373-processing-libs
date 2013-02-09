
/*

 flob - A fast multi-blob detector and tracker using flood-fill algorithms
 http://s373.net/code/flob
 copyright (c) André Sier 2008-2010
 
 steps:
 	0. construct a flob object with video, width and height: 
 	   sets desired world coordinate return values for data
 	1. configure tracker (setOm, setTresh, setFade, setMirror, setBlur, setSrcImage, ...)
 	2. when new video frame arrives, pass it to binarize and the to one of the tracking 
 	   methods available, which returns an ArrayList with the blobs
 	3. access each blob individually and plug in the values from there to your program
 
 */

import processing.opengl.*;
import processing.video.*;
import s373.flob.*;


Capture video;    // processing video capture
Flob flob;        // flob tracker instance
ArrayList blobs;  // an ArrayList to hold the gathered blobs
PImage videoinput;
/// config params
int tresh = 20;   // adjust treshold value here or keys t/T
int fade = 25;
int om = 1;
int videores=128;//64//256
String info="";
PFont font;
float fps = 60;
int videotex = 3; //case 0: videotex = videoimg;//case 1: videotex = videotexbin; 
//case 2: videotex = videotexmotion//case 3: videotex = videoteximgmotion;



void setup() {


  size(700,500,OPENGL);
  frameRate(fps);
  rectMode(CENTER);
  // init video data and stream
  video = new Capture(this, 320, 240, (int)fps); 
  video.start();
  
  // init blob tracker

  // flob uses construtor to specify srcDimX, srcDimY, dstDimX, dstDimY
  // srcDim should be video input dimensions
  // dstDim should be desired output dimensions  
  videoinput = createImage(videores, videores, RGB);
  flob = new Flob(this, videoinput, width,height);

  flob.setTresh(tresh); //set the new threshold to the binarize engine
  flob.setThresh(tresh); //typo
  flob.setSrcImage(videotex);
  flob.setImage(videotex); //  pimage i = flob.get(Src)Image();

  flob.setBackground(videoinput); // zero background to contents of video
  flob.setBlur(0); //new : fastblur filter inside binarize
  flob.setMirror(true,false);
  flob.setOm(0); //flob.setOm(flob.STATIC_DIFFERENCE);
  flob.setOm(1); //flob.setOm(flob.CONTINUOUS_DIFFERENCE);
  flob.setFade(fade); //only in continuous difference

  /// or now just concatenate messages
  flob.setThresh(tresh).setSrcImage(videotex).setBackground(videoinput)
  .setBlur(0).setOm(1).setFade(fade).setMirror(true,false);;

  boolean feats[] = {true,true,true,true,true};
  flob.setTrackFeatures( feats );

  font = createFont("monaco",16);
  textFont(font);
  blobs = new ArrayList();
}



void draw() {

  // main image loop
  if(video.available()) {
     video.read();
     videoinput.copy(video, 0, 0, 320, 240, 0, 0, videores, videores);
     blobs = flob.calc(flob.binarize(videoinput));
  }

  background(0);
  image(flob.getSrcImage(), 0, 0, width, height);

  rectMode(CENTER);

  //get and use the data
  int numblobs = blobs.size();//flob.getNumBlobs();  

  for(int i = 0; i < numblobs; i++) {

    ABlob ab = (ABlob)flob.getABlob(i); 
    //     trackedBlob tb = (trackedBlob)flob.getTrackedBlob(i); 
    //now access all blobs fields.. float tb.cx, tb.cy, tb.dimx, tb.dimy...

    // test blob coords here    
    //b1.test(ab.cx,ab.cy, ab.dimx, ab.dimy);

    //box
    fill(0,0,255,100);
    rect(ab.cx,ab.cy,ab.dimx,ab.dimy);

    //extremes
    float xtreme[] = flob.getABlobExtreme(i);
    fill(255,0,0,255);
    rect(xtreme[0], xtreme[1], 10,10);
    println("blob "+i+" xtreme: "+xtreme[2]);
    //centroid
    fill(0,255,0,200);
    rect(ab.cx,ab.cy, 5, 5);
    info = ""+ab.id+" "+ab.cx+" "+ab.cy;
    text(info,ab.cx,ab.cy+20);
    
    // skeleton data
    line(ab.cx, ab.cy, ab.armleftx, ab.armlefty);
    line(ab.cx, ab.cy, ab.armrightx, ab.armrighty);
    line(ab.cx, ab.cy, ab.headx, ab.heady);
    line(ab.cx, ab.cy, ab.bottomx, ab.bottomy);
    line(ab.cx, ab.cy, ab.footleftx, ab.footlefty);
    line(ab.cx, ab.cy, ab.footrightx, ab.footrighty);
    
    
  }

  //report presence graphically
  fill(255,152,255);
  rectMode(CORNER);
  rect(5,5,flob.getPresencef()*width,10);
  String stats = ""+frameRate+"\nflob.numblobs: "+numblobs+"\nflob.thresh:"+tresh+
    " <t/T>"+"\nflob.fade:"+fade+"   <f/F>"+"\nflob.om:"+flob.getOm()+
    "\nflob.image:"+videotex+"\nflob.presence:"+flob.getPresencef();
  fill(0,255,0);
  text(stats,5,25);
}


void keyPressed() {

  if (key=='i') {  
    videotex = (videotex+1)%4;
    flob.setImage(videotex);
  }
  if(key=='t') {
    tresh--;
    flob.setTresh(tresh);
  }
  if(key=='T') {
    tresh++;
    flob.setTresh(tresh);
  }   
  if(key=='f') {
    fade--;
    flob.setFade(fade);
  }
  if(key=='F') {
    fade++;
    flob.setFade(fade);
  }   
  if(key=='o') {
    om=(om +1) % 3;
    flob.setOm(om);
  }   
  if(key==' ') //space clear flob.background
    flob.setBackground(videoinput);
}

