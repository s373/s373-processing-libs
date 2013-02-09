/*
  
  flob part sys example
  atypical particle system influenced by blobs positions
  each particle has its own force towards all blobs
  André Sier 2010
  
*/

import processing.opengl.*;
import processing.video.*;
import s373.flob.*;


Capture video;   
Flob flob;       
ArrayList blobs = new ArrayList(); 
PImage videoinput;

PSys psys;

int tresh = 5;//12;   // adjust treshold value here or keys t/T
int fade = 225;
int om = 1;
int videores=128;
String info="";
PFont font;
float fps = 60;
int videotex = 0; //case 0: videotex = videoimg;//case 1: videotex = videotexbin; 
//case 2: videotex = videotexmotion//case 3: videotex = videoteximgmotion;



void setup() {

  size(1024,512,OPENGL);
  frameRate(fps);
  rectMode(CENTER);
  // init video data and stream
  video = new Capture(this, 320,240, (int)fps);  
  video.start();
  
  videoinput = createImage(videores, videores, RGB);

  flob = new Flob(this,videores, videores, width, height);

  flob.setThresh(tresh).setSrcImage(videotex)
  .setBackground(videoinput).setBlur(0).setOm(1).
  setFade(fade).setMirror(true,false);

  font = createFont("monaco",16);
  textFont(font);

  psys = new PSys(10000);
  stroke(255,200);
  strokeWeight(2);
}



void draw() {

  if(video.available()) {
     video.read();
     videoinput.copy(video, 0, 0, 320, 240, 0, 0, videores, videores);
     blobs = flob.calc(flob.binarize(videoinput));
  }
  background(0);
  image(flob.getSrcImage(), 0, 0, width, height);

  rectMode(CENTER);

  int numblobs = blobs.size();
  stroke(255,25);
  for(int i = 0; i < numblobs; i++) {
    ABlob ab = (ABlob)flob.getABlob(i); 
    psys.touch(ab);

    //box
    fill(0,0,255,20);
    rect(ab.cx,ab.cy,ab.dimx,ab.dimy);
    //centroid
    fill(0,255,0,70);
    rect(ab.cx,ab.cy, 5, 5);
    info = ""+ab.id+" "+ab.cx+" "+ab.cy;
    text(info,ab.cx,ab.cy+20);
  }
  stroke(255);
  psys.go();
  psys.draw();

  //report presence graphically
  fill(255,152,255, 100);
  rectMode(CORNER);
  rect(5,5,flob.getPresencef()*width,10);
  String stats = ""+frameRate+"\nflob.numblobs: "+numblobs+"\nflob.thresh:"+tresh+
    " <t/T>"+"\nflob.fade:"+fade+"   <f/F>"+"\nflob.om:"+flob.getOm()+
    "\nflob.image:"+videotex+"\nflob.presence:"+flob.getPresencef()
    +"\nparts: "+psys.p.length;
  fill(0,255,0, 100);
  text(stats,5,25);
}


void keyPressed() {

  if (key=='i') {  
    videotex = (videotex+1)%4;
    flob.setImage(videotex);
  }
  if(key=='t') {
    flob.setTresh(tresh--);
  }
  if(key=='T') {
    flob.setTresh(tresh++);
  }   
  if(key=='f') {
    flob.setFade(fade--);
  }
  if(key=='F') {
    flob.setFade(fade++);
  }   
  if(key=='o') {
    om=(om +1) % 3;
    flob.setOm(om);
  }   
  if(key==' ') //space clear flob.background
    flob.setBackground(videoinput);
}



float drag = 0.9547;
class Part {
  float x, y, vx, vy, ax, ay;
  float px,py,force;

  Part() {
    x = random(width);
    y = random(height);
    vx = random(-2,2);
    vy = random(-2,2);
    force = random(1)<0.1?random(-0.72,0.2): random(-2,2);
    px = x;
    py = y;
  }
  void go() {
    vx += ax;
    vy += ay;
    vx *= drag;
    vy *= drag;
    px = x;
    py = y;
    x+=vx;
    y+=vy;
    ax = 0;
    ay = 0;
    bounds();
  }
  
  void bounds(){
    boolean c = false;
    if(x>width){
      x-=width;
      c = true;
    }
    if(x<0){
      c = true;
      x+=width;
    }
    if(y>height){
      c = true;
      y-=height;
    }
    if(y<0){
      y+=height;
      c = true;
    }
    if(c){
      px = x;
      py = y;
    }
  }

  void draw() {
    line(px,py,x,y);
  }
  void touch(ABlob ab) {
    float dx = ab.cx - x;
    float dy = ab.cy - y;
    float d = sqrt(dx*dx+dy*dy);
    if(d > 0 && d < 150) {
      d = 1.0f/d * force;
      dx *= d;
      dy *= d;
      ax += dx;
      ay += dy;
    }
  }
}


class PSys {
  Part p[];

  PSys (int num) {
    p = new Part[num];
    for(int i=0;i<p.length;i++)
      p[i] = new Part();
  }

  void go() {    
    for(int i=0; i<p.length;i++)
      p[i].go();
  }
  void draw() {
    for(int i=0; i<p.length;i++)
      p[i].draw();
  }
  void touch(ABlob ab) {
    for(int i=0; i<p.length;i++)
      p[i].touch(ab);
  }
}

