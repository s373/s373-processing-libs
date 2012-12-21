/*

 flob linux
 simple flob linux testing processing.video sketch
 
 sketch should actually run on linux, osx + windows
 courtesy by the great work done under the hood at processing.video.*; 
 
 andré sier, 2012
 
 */




import processing.opengl.*;
import processing.video.*;
import s373.flob.*;


Capture video;    // Capture video capture
Flob flob;        // flob tracker instance
ArrayList blobs=new ArrayList();  // an ArrayList to hold the gathered blobs
PImage videoinput;// a downgraded image to flob as input

/// config params
int tresh = 20;   // adjust treshold value here or keys t/T
int fade = 25;
int om = 1;
int videores=64;//64//256
String info="";
PFont font;
float fps = 60;
int videotex = 0;


void setup() {
  size(700, 500, OPENGL);
  frameRate(fps);
  rectMode(CENTER);
  video = new Capture(this, 320, 240);//, (int)fps);  
  video.start();

  // create one image with the dimensions you want flob to run at
  videoinput = createImage(videores, videores, RGB);

  // init blob tracker
  // flob uses construtor to specify srcDimX, srcDimY, dstDimX, dstDimY
  // srcDim should be video input dimensions
  // dstDim should be desired output dimensions  
  flob = new Flob(this, videoinput); 

  flob.setThresh(tresh).setSrcImage(videotex).setBackground(videoinput)
   .setMinNumPixels(10).setBlur(0).setOm(1).setFade(fade).setMirror(true, false);

  font = createFont("monaco", 16);
  textFont(font);
}



void draw() {
  background(0);
  // main image loop
  if (video.available() == true) {
    video.read();
    videoinput.copy(video, 0, 0, 320, 240, 0, 0, videores, videores);
    //image(video, 0, 0, width, height);
    blobs = flob.calc(flob.binarize(videoinput));
  }
  image(flob.getSrcImage(), 0, 0, width, height);


  rectMode(CENTER);
  //
  //  //get and use the data
    for (int i = 0; i < flob.getNumBlobs(); i++) {

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
  

  //report presence graphically
  fill(255, 152, 255);
  rectMode(CORNER);
  rect(5, 5, flob.getPresencef()*width, 10);
  String stats = ""+frameRate+"\nflob.numblobs: "+flob.getNumBlobs()+"\nflob.thresh:"+tresh+
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

