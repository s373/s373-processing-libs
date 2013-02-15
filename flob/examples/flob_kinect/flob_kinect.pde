// flob + kinect testing
// André Sier, 2010
// flob at s373.net/code/flob

import s373.flob.*;

// Daniel Shiffman
// Basic Library functionality example
// http://www.shiffman.net
// https://github.com/shiffman/libfreenect/tree/master/wrappers/java/processing

import org.openkinect.*;
import org.openkinect.processing.*;

Kinect kinect;
boolean depth = true;
boolean rgb = false;
boolean ir = false;

float deg = 15; // Start at 15 degrees


PImage vrImage;
int videores=128;//64//256
Flob flob;

void setup() {
  size(1280,520);
  kinect = new Kinect(this);
  kinect.start();
  kinect.enableDepth(depth);
  kinect.enableRGB(rgb);
  kinect.enableIR(ir);
  kinect.tilt(deg);
  
    // downscale image to ease flob processing load
  vrImage = createImage(videores, videores, RGB);
  //flob = new Flob(vrImage, this); 
   flob = new Flob(this, videores, videores, 640, 480);

  flob.setThresh(20).setSrcImage(3).setBackground(vrImage)
    .setBlur(0).setOm(1).setFade(25).setMirror(false, false);
    
    // new kinect specific code can clamp kinect image (or other images)
    // between values of nearGray and farGray (works on 8bit 0-255 limits)

  flob.setClampGray(true).setNearGray(10).setFarGray(80);

  rectMode(CENTER);
  textFont(createFont("monospace",16));
  
}


void draw() {
  background(0);

  {
    vrImage.copy(kinect.getDepthImage(), 0, 0, 640, 480, 0, 0, videores, videores);
    flob.calc(flob.binarize(vrImage));    
  }
  
  image(flob.getSrcImage(), 0, 0, 640, 480);
  int numblobs = flob.getNumBlobs();  
  if (numblobs>0) {
    for (int i = 0; i < numblobs; i++) {
      ABlob ab = (ABlob)flob.getABlob(i); 
      //box
      fill(0, 0, 255, 100);
      rect(ab.cx, ab.cy, ab.dimx, ab.dimy);
      //centroid
      fill(0, 255, 0, 200);
      rect(ab.cx, ab.cy, 5, 5);
//      info = ""+ab.id+" "+ab.cx+" "+ab.cy;
//      text(info, ab.cx, ab.cy+20);
    }
  }

  //report presence graphically
  fill(255, 152, 255);
  rectMode(CORNER);
  rect(5, 5, flob.getPresencef()*width, 10);
  String stats = ""+frameRate+"\nflob.numblobs: "+numblobs+"\nflob.thresh:"+flob.getThresh()+
    " <t/T>"+"\nflob.fade:"+flob.getFade()+"   <f/F>"+"\nflob.om:"+flob.getOm()+
    "\nflob.image:"+flob.videotexmode+"\nflob.presence:"+flob.getPresencef();
  fill(0, 255, 0);
  text(stats, 5, 25);

  // kinect stats
  image(kinect.getDepthImage(),640,0);
  fill(255);
  text("RGB/IR FPS: " + (int) kinect.getVideoFPS() + "        Camera tilt: " + (int)deg + " degrees",10,495);
  text("DEPTH FPS: " + (int) kinect.getDepthFPS(),640,495);
  text("Press 'd' to enable/disable depth    Press 'r' to enable/disable rgb image   Press 'i' to enable/disable IR image  UP and DOWN to tilt camera   Framerate: " + frameRate,10,515);
}

void keyPressed() {    
  if(key=='t'){
     flob.setThresh(flob.getThresh() - 1 );
  }
  if(key=='T'){
     flob.setThresh(flob.getThresh() + 1 );
  }
  
  if (key == 'd') {
    depth = !depth;
    kinect.enableDepth(depth);
  } 
  else if (key == 'r') {
    rgb = !rgb;
    if (rgb) ir = false;
    kinect.enableRGB(rgb);
  }
  else if (key == 'i') {
    ir = !ir;
    if (ir) rgb = false;
    kinect.enableIR(ir);
  } 
  else if (key == CODED) {
    if (keyCode == UP) {
      deg++;
    } 
    else if (keyCode == DOWN) {
      deg--;
    }
    deg = constrain(deg,0,30);
    kinect.tilt(deg);
  }
}
void stop() {
  kinect.quit();
  super.stop();
}

