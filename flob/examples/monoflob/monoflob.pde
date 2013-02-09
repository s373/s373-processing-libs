/*
  s373Monoflob 
  (vs. 20090621)
  
  started out as buttons to trigger things, ended up
  with a nice fade engine in the buttons due to presence
  this works really sweet connected to soundfile banks gains  
  loosely inspired from the great and simple monome
  
  andré sier 
  http://s373.net
 
 */

import processing.opengl.*;
import processing.video.*;
import s373.flob.*;

/// vars
Capture video;
Flob flob; 
ArrayList blobs=new ArrayList();
PImage videoinput;
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

s373Monoflob mono;

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
  flob.setOm(1).setFade(fade).setMinNumPixels(20).setMaxNumPixels(500);
  flob.setColorMode(colormode);

  font = createFont("monaco",16);
  textFont(font);

  mono = new s373Monoflob(4,4);
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
                 "\nflob.image: "+videotex+"\nflob.colormode: "+flob.getColorMode()+"\nflob.presence:"+flob.getPresencef();
  fill(0,255,0);
  text(stats,5,25);

}


void keyPressed(){
  if(key=='b')
    drawimg^=true;
  if (key=='s')
    saveFrame("s373Monoflob-######.png");
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




class Botao {
  int id;
  float x, y, w, h,w2,h2;
  int coroff,coron;
  int gain; 
  boolean on = false;
  boolean touch = false;

  Botao(int i,  float _x, float _y, float _w , float _h  ) {
    id = i;
    x = _x;
    y = _y;
    w = _w;
    h = _h;
    w2 = w*0.5f;
    h2 = h*0.5f;    
    coroff = color(50);
    coron = color(0,250,0);
  } 


  void test(float _x, float _y, float dimx, float dimy) {
    float dx = x - _x;
    float dy = y - _y;
    if(abs(dx) <= (w2+dimx*0.5) && abs(dy) <= (h2+dimy*0.5)){
      gain++;  
      touch = true;
    }
  }

  void state(){    
    if(touch)
      touch=false;
    else
      gain--;
      
    if(gain>100){
      gain = 100;
      on = true; 
    }
    if(gain<50)
      on = false;
    if(gain<0)
      gain=0;
  }

  void render(){
    state();
    fill(on ? coron : coroff,map(gain,0,100,10,255));    
    rect(x,y,w,h);
    fill(255);
    text(""+gain,x,y);
    text(""+id,x,y+h2-2);
  }

}





class s373Monoflob{
  Botao b[];
  int gx,gy,num;
  float dimx,dimy;
  float sizex,sizey;//1.0 max
  
  s373Monoflob(int _gx, int _gy){
    gx = _gx;
    gy = _gy;
    sizex = 0.75;
    sizey = 0.55;
    float stridex = (float)width / (float)gx;
    float stridey = (float)height / (float)gy;
    dimx = stridex * sizex ;
    dimy = stridey * sizey ;
    
    num = gx * gy;    
    b = new Botao[num];

    for(int i=0; i<num;i++){
       float x =  ((float)(i % gx) +0.5) * stridex  ;
       float y = ((float)(i / gx) +0.5) *stridey ;
       b[i] = new Botao(i, x,y,dimx,dimy);      
    }        
        
  }
  
  void touch(float x, float y,float w, float h){    
    for(int i=0; i<num; i++)
      b[i].test(x,y,w,h);    
  }

  void render(){
    for(int i=0; i<num; i++)
      b[i].render();   
  }


}

