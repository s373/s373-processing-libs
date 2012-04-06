import s373.marchingcubes.*;
import processing.opengl.*;


MarchingCubes mc;
float isoval = 0.21;
PVector rot=new PVector();
boolean drawwireframe = false;
boolean drawnormals = true;

void setup(){
  size(1000, 600, OPENGL); 

  mc = new MarchingCubes(this, 1000, 600, -1000, 11, 11, 31);  
  mc.zeroData();
  mc.isolevel = 0.02;
  
}

void draw(){
  background(0);
  lights();
  
  XYZ lightloc = new XYZ (
                             map(sin(frameCount*0.051) , -1., 1., -500, 2*mc.worlddim.x ),
                             map(cos(frameCount*0.0321) , -1., 1., -500, 2*mc.worlddim.y ),
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
    if (mouseButton==RIGHT) {
      rot.x += -(mouseX-pmouseX)*0.005;
      rot.y += (mouseY-pmouseY)*0.005;
    } 
    else {
     // mc.addCube(isoval, loc, dim); 
      mc.addIsoPoint(isoval,loc);
      mc.polygoniseData();
    }
  }
  
  
   if (drawwireframe) {
    stroke(255, 200);
    noFill();
  } 
  else {
    fill(255, 255);
    noStroke();
  }


//  box(500);

  pushMatrix();
//  translate(width/2, height/2, -width/2);
  translate(mc.worlddim.x/2, mc.worlddim.y/2, mc.worlddim.z/2);
  rotateX(rot.y);
  rotateY(rot.x);
  translate(-mc.worlddim.x/2, -mc.worlddim.y/2, -mc.worlddim.z/2);
//  translate(-width/2, -height/2, width/2);
if(drawnormals)
  stroke(255,0,0,150);
  
    ArrayList <TRIANGLE> tris = mc.trilist;
    for(int i=0; i<tris.size(); i++){
       TRIANGLE tri = tris.get(i); 

      beginShape(TRIANGLES);
      normal((float)tri.n.x, (float)tri.n.y, (float)tri.n.z);
      vertex((float)tri.p[0].x, (float)tri.p[0].y, (float)tri.p[0].z);
      normal((float)tri.n.x, (float)tri.n.y, (float)tri.n.z);
      vertex((float)tri.p[1].x, (float)tri.p[1].y, (float)tri.p[1].z);
      normal((float)tri.n.x, (float)tri.n.y, (float)tri.n.z);
      vertex((float)tri.p[2].x, (float)tri.p[2].y, (float)tri.p[2].z);
      endShape();

      if( drawnormals ) {
      XYZ faceavg = new XYZ( 
                              (tri.p[0].x + tri.p[1].x + tri.p[2].x ) * 0.33333f , 
                              (tri.p[0].y + tri.p[1].y + tri.p[2].y ) * 0.33333f , 
                              (tri.p[0].z + tri.p[1].z + tri.p[2].z ) * 0.33333f 
                            );
                              
      XYZ facenormavg = new XYZ(faceavg); 
      float size = 0.02f;//:)//1.0f;
      facenormavg.x += tri.n.x*size;
      facenormavg.y += tri.n.y*size;
      facenormavg.z += tri.n.z*size;
                              
      beginShape(LINES);
//      vertex
      vertex(faceavg.x, faceavg.y, faceavg.z);
      vertex(facenormavg.x, facenormavg.y, facenormavg.z);
      endShape();
      }
    }


  popMatrix();

  fill(255,0,0,150);

 // mc.draw();
////  pushMatrix();
//////  translate(width/2, height/2, -width/2);
////  translate(mc.worlddim.x/2, mc.worlddim.y/2, mc.worlddim.z/2);
////  rotateX(rot.y);
////  rotateY(rot.x);
////  translate(-mc.worlddim.x/2, -mc.worlddim.y/2, -mc.worlddim.z/2);
//////  translate(-width/2, -height/2, width/2);
////
//
//  mc.draw();
//  stroke(255, 0, 0, 100);
//  mc.drawnormals(50.0);
////  popMatrix();



  camera();
  text(mc.getinfo(), 5, height-20);

//  mc.drawnormals(this, 25);

//  String debug = "";
//  for(int i=0; i<mc.trilist.size()/2; i++){
//     debug += " "+mc.trilist.get(i).p[0].x; 
//  }
//  println(debug);

}

void keyPressed(){
  
  if(key=='n'){
     
      mc.invertnormals^=true;
     
  }
  
  if (key=='s') {
    mc.writeStl("/testStl/"+frameCount+".stl"); 
    return;
  }

}

