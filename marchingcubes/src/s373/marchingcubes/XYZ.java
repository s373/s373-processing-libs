package s373.marchingcubes;


public class XYZ {
 public float x, y, z;
 public XYZ() {
  }
 public XYZ(float a, float b, float c) {
    x=a; 
    y=b; 
    z=c;
  }
 public XYZ(XYZ a) {
    x=a.x; 
    y=a.y; 
    z=a.z;
  }
}
