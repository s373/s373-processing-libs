// hello s373.boids.*;

import processing.opengl.*;
import s373.boids.*;

Flock3d flock3d;
PFont font;

boolean varyForces=false;
float ali=random(1e-10,1e-3) , coh=random(1e-10,1e-3), sep =random(1e-10,1e-3);


void setup() {  

  size(1000,700,OPENGL);

  // num, x, y, z,  dev
  flock3d = new Flock3d(10, width/2,height/2, -300, 25);

  flock3d.setBoundmode(0).setMaxSpeed(5);//.setForceAlign(-0.01);
  //  flock2d.setBoundmode(1).setForceAlign(0.01).
  //  setForceCohesion(10).setForceSeparate(1000).
  //  setMaxForce(1000).setMaxSpeed(20);

  float b = 25;
  flock3d.setBounds(b,b, -700, width-b,height-b, 0);

  // make attrPts
  for(int i=0; i<10; i++) {   
     float x = random(width);
     float y = random(height);
     float z = random(-700,0);
     float force = -100;//random (-25,25);
     float dist = random(100,200); 
     flock3d.addAttractionPoint(x,y,z,force,dist);
  }


  fill(200);
  stroke(255);

  font = createFont("Arial",10);
  textFont(font);
  rectMode(CORNER);
}


void draw() {

  background(0);
  
  float speed = frameCount*0.005;
  float rad = 1500;
  float camx = width/2 + sin(speed) * rad;
  float camz = -350 + cos(speed) * rad;    
//  camera(width/2,height/2,400, width/2,height/2,0, 0, 1, 0);
  camera(camx,height/2,camz, width/2,height/2,0, 0, 1, 0);

  //view attrpoints
  color attrColor = color(100,255,0);
  color repelColor = color(255,128,0); 
  
  for(int i=0; i<flock3d.attractionPoints.size(); i++){   
      AttractionPoint3d ap = flock3d.attractionPoints.get(i);            
      /// rectdist, color force      
      fill( ap.force > 0 ? attrColor : repelColor, 100);

      pushMatrix();
      translate( ap.x, ap.y, ap.z);
      rect ( -ap.sensorDist/2, -ap.sensorDist/2, ap.sensorDist , ap.sensorDist );
      rotateX (radians(90));
      rect ( -ap.sensorDist/2, -ap.sensorDist/2, ap.sensorDist , ap.sensorDist );
      popMatrix();
      
//      rect(ap.x, ap.y, ap.sensorDist, ap.sensorDist);
//      fill( ap.force > 0 ? attrColor : repelColor, 200);
//      rect(ap.x, ap.y, 5, 5);    
  }

  if(frameCount%60==0) {
    flock3d.changeAttractionPoint( 
      (int) random(10), random (width), random (height), random(-700,0),
      -100, random(20,250)
      );
    flock3d.changeAttractionPoint( 
      (int) random(10), random (width), random (height), random(-700,0),
      20, random(20,250)
      );
  }

  if(frameCount>60) {
    flock3d.update(1);
    if(varyForces){
        float al = map (cos( ali * frameCount ), -1,1, 0, 150);
        float co = map (cos( coh * frameCount ), -1,1, 0, 150);
        float se = map (cos( sep * frameCount ), -1,1, 0, 150);
        flock3d.setAlign(al).setCohesion(co).setSeparate(se); 
    }
  }

  for(int i=0; i<flock3d.size(); i++) {     
    Boid3d b = flock3d.get(i);

    pushMatrix();
    translate(b.x, b.y, b.z);
    rect(-5,-5,10,10);
    rotateX(radians(90));
    rect(-5,-5,10,10);
    popMatrix();
    
//    rect(b.x, b.y, 5,5);
    float lm = 10.f;
    line(b.x, b.y, b.z, b.x + b.vx*lm, b.y + b.vy*lm, b.z + b.vz*lm);
  }

camera();
  text(
  ""+frameRate+"\nsize: "+flock3d.size()+
    "\nalign: "+flock3d.getAlign()+
    "\ncohesion: "+flock3d.getCohesion()+
    "\nseparate: "+flock3d.getSeparate(), 
  10, height-70  );
}

void addBoids() {
  flock3d.add(mouseX,mouseY, -300);
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

