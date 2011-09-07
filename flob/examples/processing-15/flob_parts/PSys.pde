float drag = 0.957;
class Part {
  float x, y, vx, vy, ax, ay;
  float px,py,force;

  Part() {
    x = random(width);
    y = random(height);
    vx = random(-2,2);
    vy = random(-2,2);
    force = random(-2,2);
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
    if(d > 0 && d < 200) {
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

