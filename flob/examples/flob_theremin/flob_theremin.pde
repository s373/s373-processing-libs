
/* 
 hello_flob_theremin
 André Sier, 2009
 
 10 voices simple theremin emulator
 
 */

import processing.opengl.*;
import processing.video.*;
import s373.flob.*;
import ddf.minim.*;
import ddf.minim.signals.*;

Minim minim;
AudioOutput out;
SineWave sine[];

Capture video;    // processing video capture
Flob flob;        // flob tracker instance
ArrayList blobs;  // an ArrayList to hold the gathered blobs

/// config params
int tresh = 10;   // adjust treshold value here or keys t/T
int fade = 45;
int om = 1;
int videores=128;//64//256
String info="";
PFont font;
float fps = 60;
int videotex = 3; 
PImage vrImage;

void setup() {
//  // if on mac, processing 1.5, must open qt session: osx quicktime bug 882 processing 1.0.1
//  try { quicktime.QTSession.open(); } 
//  catch (quicktime.QTException qte) { qte.printStackTrace(); }

  size(800, 600, OPENGL);
  frameRate(fps);
  rectMode(CENTER);
  // init video data and stream
  video = new Capture(this, 320, 240, (int)fps); 
  video.start(); // if on processing 151, comment this line 

  // init blob tracker
  flob = new Flob(this, videores, videores, width, height);
  vrImage = createImage(videores, videores, RGB);
  flob.setThresh(tresh).setSrcImage(videotex).setBackground(vrImage)
    .setBlur(0).setOm(1).setFade(fade).setMirror(true, false);


  font = createFont("monaco", 16);
  textFont(font);
  blobs = new ArrayList();

  minim = new Minim(this);
  // get a line out from Minim, default bufferSize is 1024, default sample rate is 44100, bit depth is 16
  out = minim.getLineOut(Minim.STEREO);
  // create a sine wave Oscillator, set to 440 Hz, at 0.5 amplitude, sample rate from line out
  sine = new SineWave[10];
  for (int i=0; i<10; i++) {
//    sine[i] = new SineWave(440 + 121*i, 0.11, out.sampleRate());
    sine[i] = new SineWave(mtof(i*7+20), 0.21, out.sampleRate());
    sine[i].portamento(1000);
    out.addSignal(sine[i]);
  }
}



void draw() {

  // main image loop
  if (video.available()) {
    video.read();
    vrImage.copy(video, 0, 0, 320, 240, 0, 0, videores, videores);
    blobs = flob.calc(flob.binarize(vrImage));
  }
  background(0);
  image(flob.getSrcImage(), 0, 0, width, height);

  rectMode(CENTER);

  //get and use the data
  int numblobs = blobs.size();//flob.getNumBlobs();  

  for (int i = 0; i < numblobs; i++) {

    ABlob ab = (ABlob)flob.getABlob(i); 

    // test blob coords here    
    //b1.test(ab.cx,ab.cy, ab.dimx, ab.dimy);

    if (i<10) {
//      sine[i].setFreq( map(ab.cy, 0, height, 2000, 200) );
      sine[i].setFreq( mtof(map(ab.cy, 0, height, 120, 40) ));
    }


    //box
    fill(0, 0, 255, 100);
    rect(ab.cx, ab.cy, ab.dimx, ab.dimy);
    //centroid
    fill(0, 255, 0, 200);
    rect(ab.cx, ab.cy, 5, 5);
    info = ""+ab.id+" "+ab.cx+" "+ab.cy;
    text(info, ab.cx, ab.cy+20);
  }

  int activesounds = min(10, numblobs);
  for (int i=0; i<10;i++) {
    if (i<activesounds) {
      sine[i].setAmp( 0.9f / activesounds);
    } 
    else {
      sine[i].setAmp( 0.0001f);
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

  if (key=='s')
    saveFrame("flobtheremin-######.png");
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
    flob.setBackground(vrImage);
}



float mtof(float midi){  
  return  (440.0f * pow(2, ((midi-69.0) / 12.0)) );  
}
