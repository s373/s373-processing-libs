/*
  flob calc simple collide // andré sier // 20090310
  http://s373.net
  
 puts the om to continuous difference and uses that image
 to calc the blobs. blobs are detected for collision,
 and if true, some nice ellastic collision is applied
 
 teclas/keys: espaço/space, o, i, v 
 */

import processing.opengl.*;
import processing.video.*;
import s373.flob.*;

Capture video;
Flob flob; 
PImage videoinput;


int videores=128;
int fps = 60;
PFont font = createFont("arial",10);

Bola bolas[];

boolean showcamera=true;
boolean om=true,omset=false;
float velmult = 10000.0f;
int vtex=0;


void setup(){

  size(640,480,OPENGL);
  frameRate(fps);

//  String[] devices = Capture.list();
//  println(devices);

  video = new Capture(this, 320, 240, fps);
  video.start();
  
    // create one image with the dimensions you want flob to run at
  videoinput = createImage(videores, videores, RGB);

  // construct flob
  flob = new Flob(this,videores,videores, width, height);
  
  flob.setMirror(true,false);
  flob.setThresh(10);
  flob.setFade(45);
  flob.setMinNumPixels(10);
  flob.setImage( vtex );

  bolas = new Bola[10];
  for(int i=0;i<bolas.length;i++){
    bolas[i] = new Bola(); 
  }

  textFont(font);
}


void draw(){
  background(0);
  if(video.available()) {   
    if(!omset){
      if(om)
        flob.setOm(flob.CONTINUOUS_DIFFERENCE);
      else
        flob.setOm(flob.STATIC_DIFFERENCE);
      omset=true;
    }
    video.read();
    
    //downscale video image to videoinput pimage
     videoinput.copy(video,0,0,320,240,0,0,videores,videores);
     
    // aqui é que se define o método calc, calcsimple, ou tracksimple
    // o tracksimple é mais preciso mas mais pesado que o calcsimple

    //    flob.tracksimple(  flob.binarize(video) ); 
    flob.calcsimple(  flob.binarize(videoinput) ); 
  }

  image(flob.getSrcImage(), 0, 0, width, height);

  //report presence graphically
  fill(255,152,255);
  rect(0,0,flob.getPresencef()*width,10);

  fill(255,100);
  stroke(255,200);
  //get and use the data
  // int numblobs = flob.getNumBlobs(); 
  int numtrackedblobs = flob.getNumTBlobs();

  text("numblobs> "+numtrackedblobs,5,height-10);

  fill(255,10);
  rectMode(CENTER);
  stroke(127,200);

  TBlob tb;

  for(int i = 0; i < numtrackedblobs; i++) {
    tb = flob.getTBlob(i);
    rect(tb.cx, tb.cy, tb.dimx, tb.dimy );
    line(tb.cx, tb.cy, tb.cx + tb.velx * velmult ,tb.cy + tb.vely * velmult );    
    String txt = ""+tb.id+" "+tb.cx+" "+tb.cy;
    text(txt,tb.cx, tb.cy);
  }

  // colisão

  float cdata[] = new float[5];
  for(int i=0;i<bolas.length;i++){
    float x = bolas[i].x / (float) width;
    float y = bolas[i].y / (float) height;
    cdata = flob.imageblobs.postcollidetrackedblobs(x,y,bolas[i].rad/(float)width); 
    if(cdata[0] > 0) {
      bolas[i].toca=true;
      bolas[i].vx +=cdata[1]*width*0.015;
      bolas[i].vy +=cdata[2]*height*0.015;
    } 
    else {
      bolas[i].toca=false; 
    }
    bolas[i].run(); 
  }

  if(showcamera){
    tint(255,150);
    image(flob.videoimg,width-videores,height-videores);
    image(flob.videotexbin,width-2*videores,height-videores);
    image(flob.videotexmotion,width-3*videores,height-videores);
  }

}

void keyPressed(){
  if(key==' ')
    flob.setBackground(videoinput); 
  if(key=='o'){
    om^=true; 
    omset=false;
  }
  if(key=='i')
    showcamera^=true;
  if(key=='v'){
    vtex = (vtex + 1) % 4;
    flob.setVideoTex(  vtex  );
  }
}


class Bola{
  float x,y,vx,vy;
  float g = 0.025, rad= random(5,25);
  boolean toca=false;

  Bola(){ 
    init();  
  }
  void init(){
    vx = random(-1.1,1.1);
    vy = random(1.5); 
    x = random(width);
    y = random(-100,-50);       
  }
  void update(){
    // vy+=g;
    // vx+=g;
    x+=vx;
    y+=vy;

    if(abs(vx)>3)
      vx*=0.9;
    if(abs(vy)>3)
      vy*=0.9;

    if(x<-rad){
      x=-rad;
      vx = -vx;
    }
    if(x>width+rad){
      x=width+rad;
      vx = -vx;
    }
    if(y<-100){
      y=-100;
      vy = -vy;
    }
    if(y>height-rad){
      y=height-rad;
      vy = -vy;
    }


  }
  void draw(){
    if(!toca)
      fill(0,255,0);
    else
      fill(255,0,0);
    ellipse(x,y,rad*2,rad*2); 
  }
  void run(){
    update(); 
    draw(); 
  }
}



