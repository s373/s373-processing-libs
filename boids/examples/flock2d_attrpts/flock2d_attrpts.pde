// hello s373.boids.*;

import processing.opengl.*;
import s373.boids.*;

Flock2d flock2d;
PFont font;

boolean varyForces=false;
float ali=random(1e-10,1e-3) , coh=random(1e-10,1e-3), sep =random(1e-10,1e-3);


void setup() {  

  size(1024,600,OPENGL);
  // num, centerx, centery, deviation    
  //  flock2d = new Flock2d(100, width/2,height/2, 100);

  // num, x, y, dev
  flock2d = new Flock2d(10, width/2,height/2, 25);

  flock2d.setBoundmode(1).setMaxSpeed(2);//.setForceAlign(-0.01);
  //  flock2d.setBoundmode(1).setForceAlign(0.01).
  //  setForceCohesion(10).setForceSeparate(1000).
  //  setMaxForce(1000).setMaxSpeed(20);

  float b = 25;
  flock2d.setBounds(b,b,width-b,height-b);

  // make attrPts
  for(int i=0; i<10; i++) {   
     float x = random(width);
     float y = random(height);
     float force = -100;//random (-25,25);
     float dist = random(100,200); 
     flock2d.addAttractionPoint(x,y,force,dist);
  }


  fill(200);
  stroke(10);

  font = createFont("Arial",10);
  textFont(font);
  rectMode(CENTER);
}


void draw() {

  background(255);

  //view attrpoints
  color attrColor = color(100,255,0);
  color repelColor = color(255,128,0); 
  
  for(int i=0; i<flock2d.attractionPoints.size(); i++){   
      AttractionPoint2d ap = flock2d.attractionPoints.get(i);            
      /// rectdist, color force      
      fill( ap.force > 0 ? attrColor : repelColor, 100);
//      rect(ap.x, ap.y, ap.sensorDist/2, ap.sensorDist/2);
      rect(ap.x, ap.y, ap.sensorDist, ap.sensorDist);
      fill( ap.force > 0 ? attrColor : repelColor, 200);
      rect(ap.x, ap.y, 5, 5);    
  }

  if(frameCount%60==0) {
    flock2d.changeAttractionPoint( 
      (int) random(10), random (width), random (height), -100, random(20,250)
      );
    flock2d.changeAttractionPoint( 
      (int) random(10), random (width), random (height), 20, random(20,250)
      );
  }

  if(frameCount>60) {
    flock2d.update(1);
    if(varyForces){
        float al = map (cos( ali * frameCount ), -1,1, 0, 150);
        float co = map (cos( coh * frameCount ), -1,1, 0, 150);
        float se = map (cos( sep * frameCount ), -1,1, 0, 150);
        flock2d.setAlign(al).setCohesion(co).setSeparate(se); 
    }
  }

  for(int i=0; i<flock2d.size(); i++) {     
    Boid2d b = flock2d.get(i);
    rect(b.x, b.y, 5,5);
    float lm = 10.f;
    line(b.x, b.y, b.x + b.vx*lm, b.y + b.vy*lm);
  }

  text(
  ""+frameRate+"\nsize: "+flock2d.size()+
    "\nalign: "+flock2d.getAlign()+
    "\ncohesion: "+flock2d.getCohesion()+
    "\nseparate: "+flock2d.getSeparate(), 
  10, height-70  );
}

void addBoids() {
  flock2d.add(mouseX,mouseY);
}

void mousePressed() {
  addBoids();
}
void mouseDragged() {
  addBoids();
}

void keyPressed(){
   if(key=='f')
    varyForces^=true; 
}

