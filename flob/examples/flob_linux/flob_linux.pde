/*

 flob linux
 simple flob example using gsvideo by andres colubri
 tested and run on linux ubuntu 10.04, ubuntu 11.04 64-bits
 
 andre sier, 2010
 
 */



/*

 flob - A fast multi-blob detector and tracker using flood-fill algorithms
 http://s373.net/code/flob
 copyright © André Sier 2008-2010
 
 steps:
 
 	0. construct a flob object with video, width and height: 
 	   sets desired world coordinate return values for data
 	1. configure tracker (setOm, setTresh, setFade, setMirror, setBlur, setSrcImage, ...)
 	2. when new video frame arrives, pass it to binarize and the to one of the tracking 
 	   methods available, which returns an ArrayList with the blobs
 	3. access each blob individually and plug in the values from there to your program
 
 */

import processing.opengl.*;
import codeanticode.gsvideo.*;
import s373.flob.*;


GSCapture video;    // GSCapture video capture
Flob flob;        // flob tracker instance
ArrayList blobs=new ArrayList();  // an ArrayList to hold the gathered blobs

/// config params
int tresh = 20;   // adjust treshold value here or keys t/T
int fade = 25;
int om = 1;
int videores=64;//64//256
String info="";
PFont font;
float fps = 60;
int videotex = 0; //case 0: videotex = videoimg;//case 1: videotex = videotexbin; 
//case 2: videotex = videotexmotion//case 3: videotex = videoteximgmotion;
PImage vrImage;


void setup() {
  size(700, 500, OPENGL);
  frameRate(fps);
  rectMode(CENTER);
  video = new GSCapture(this, 320, 240);//, (int)fps);  
  video.play();

  // downscale image to ease flob processing load
  vrImage = createImage(videores, videores, RGB);
  // init blob tracker
  // flob uses construtor to specify srcDimX, srcDimY, dstDimX, dstDimY
  // srcDim should be video input dimensions
  // dstDim should be desired output dimensions  
  //  flob = new Flob(video, width,height);
  flob = new Flob(vrImage, this); 
  // flob = new Flob(videores, videores, width, height);

  flob.setThresh(tresh).setSrcImage(videotex).setBackground(vrImage)
    .setBlur(0).setOm(1).setFade(fade).setMirror(true, false);

  font = createFont("monaco", 10);
  textFont(font);
}



void draw() {
  background(0);
  // main image loop
  if (video.available() == true) {
    video.read();
    vrImage.copy(video, 0, 0, 320, 240, 0, 0, videores, videores);
    //image(video, 0, 0, width, height);
    blobs = flob.calc(flob.binarize(vrImage));
  }
  image(flob.getSrcImage(), 0, 0, width, height);


  rectMode(CENTER);
  //
  //  //get and use the data
  int numblobs = blobs.size();//flob.getNumBlobs();  
  //
  if (numblobs>0) {
    for (int i = 0; i < numblobs; i++) {

      ABlob ab = (ABlob)flob.getABlob(i); 
      //now access all blobs fields.. float tb.cx, tb.cy, tb.dimx, tb.dimy...

      // test blob coords here    
      //b1.test(ab.cx,ab.cy, ab.dimx, ab.dimy);

      //box
      fill(0, 0, 255, 100);
      rect(ab.cx, ab.cy, ab.dimx, ab.dimy);
      //centroid
      fill(0, 255, 0, 200);
      rect(ab.cx, ab.cy, 5, 5);
      info = ""+ab.id+" "+ab.cx+" "+ab.cy;
      text(info, ab.cx, ab.cy+20);
    }
  }

  //report presence graphically
  fill(255, 152, 255);
  rectMode(CORNER);
  rect(5, 5, flob.getPresencef()*width, 10);
  String stats = ""+frameRate+"\nflob.numblobs: "+numblobs+"\nflob.thresh:"+tresh+
    " <t/T>"+"\nflob.fade:"+fade+"   <f/F>"+"\nflob.om:"+flob.getOm()+
    "\nflob.image:"+videotex+"\nflob.presence:"+flob.getPresencef();
  fill(0, 255, 0);
  text(stats, 5, 25);
}


void keyPressed() {

  if (key=='i') {  
    videotex = (videotex+1)%4;
    flob.setImage(videotex);
  }
  if (key=='t') {
    tresh--;
    flob.setTresh(tresh);
  }
  if (key=='T') {
    tresh++;
    flob.setTresh(tresh);
  }   
  if (key=='f') {
    fade--;
    flob.setFade(fade);
  }
  if (key=='F') {
    fade++;
    flob.setFade(fade);
  }   
  if (key=='o') {
    om=(om +1) % 3;
    flob.setOm(om);
  }   
  if (key==' ') //space clear flob.background
    flob.setBackground(video);
}

