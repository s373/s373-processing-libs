import s373.marchingcubes.*;
import processing.opengl.*;


MarchingCubes mc;
float isoval = 0.21;
PVector rot=new PVector();
boolean drawwireframe = false;
boolean drawnormals = true;


Part parts[];

void setup() {
  size(1000, 600, OPENGL); 
  int mcres = 64;
  mc = new MarchingCubes(this, 1000, 600, -1000, mcres, mcres, mcres);  
  mc.zeroData();
  mc.isolevel = 0.02;

  parts = new Part[10];
  for (int i=0; i<parts.length;i++) {
    parts[i] = new Part();
  }
}

void draw() {
  background(0);
  lights();

  XYZ lightloc = new XYZ (
  map(sin(frameCount*0.051), -1., 1., -500, 2*mc.worlddim.x ), 
  map(cos(frameCount*0.0321), -1., 1., -500, 2*mc.worlddim.y ), 
  map(sin(frameCount*0.031), -1., 1., 500, 2*mc.worlddim.z )
    );

  pointLight( 0, 255, 0, lightloc.x, lightloc.y, lightloc.z);
  pointLight( 0, 0, 255, lightloc.x*0.7, lightloc.y*0.7, lightloc.z*0.7);
  shininess(mouseX);
  pushMatrix();
  translate(mc.worlddim.x/2, mc.worlddim.y/2, mc.worlddim.z/2);
  rotateX(rot.y);
  rotateY(rot.x);
  translate(-mc.worlddim.x/2, -mc.worlddim.y/2, -mc.worlddim.z/2);

  translate(lightloc.x, lightloc.y, lightloc.z);
  box(20);
  popMatrix();
  //  ambientLight(0,255,0, width/2, height/2, -width/2);
  //  camera(0,0,-5000, 0, 0, 0, 0,1,0);

  XYZ loc = new XYZ(mouseX, mouseY, map(sin(frameCount*0.021), -1., 1., -1., mc.worlddim.z ));
  XYZ dim = new XYZ(10, 10, 10);
  if (mousePressed) {
    if (mouseButton==LEFT) {
      rot.x += -(mouseX-pmouseX)*0.05;
      rot.y += (mouseY-pmouseY)*0.05;
    } 
    else {
      // mc.addCube(isoval, loc, dim); 
      mc.addIsoPoint(isoval, loc);
      mc.polygoniseData();
    }
  }

  for (int i=0; i<parts.length;i++) {
    parts[i].update();
  }
  mc.multData(0.961);
  mc.polygoniseData();


  if (drawwireframe) {
    stroke(255, 200);
    noFill();
  } 
  else {
    fill(255, 255);
    noStroke();
  }



  pushMatrix();
  translate(mc.worlddim.x/2, mc.worlddim.y/2, mc.worlddim.z/2);
  rotateX(rot.y);
  rotateY(rot.x);
  translate(-mc.worlddim.x/2, -mc.worlddim.y/2, -mc.worlddim.z/2);
  stroke(255, 0, 0, 150);  
  fill(255,200);
  mc.draw();
  if (drawnormals)
    mc.drawnormals(0.01);
  fill(255, 128, 0, 250);
  for (int i=0; i<parts.length;i++) {
    parts[i].display();
  }
  popMatrix();

  camera();
  text("click rotate, fps: "+frameRate+"\n"+mc.getinfo(), 5, height-50);
}

void keyPressed() {

  if (key=='n') {     
    mc.invertnormals^=true;
  }

  if (key=='s') {
    mc.writeStl(""+frameCount+".stl"); 
    return;
  }
}

