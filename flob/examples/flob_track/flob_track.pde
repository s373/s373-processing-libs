/*
  flob tracking example with method track

  thanks to Mahesh Viswanathan for adding city names
  
 */
import processing.opengl.*;
import processing.video.*;
import s373.flob.*;

Capture video;
Flob flob; 
PImage videoinput;
/// video params
int tresh = 10;
int fade = 25;
int om = 0;
int videores=128;
int videotex=0;//3
boolean drawimg=true;
String info="";
float fps = 30;
PFont font = createFont("monaspace",16);
ArrayList blobs = new ArrayList();

void setup(){
//  try { quicktime.QTSession.open(); } 
//  catch (quicktime.QTException qte) { qte.printStackTrace(); }

  size(700,500); //,OPENGL);
  frameRate(fps);
  rectMode(CENTER);
  
  video = new Capture(this, 320, 240, (int)fps);
  video.start();  
  
  videoinput = createImage(videores, videores, RGB);
  flob = new Flob(this, videores, videores, width, height);
  flob.setOm(om);  
  flob.setThresh(tresh);
  flob.setSrcImage(videotex);
  flob.setTBlobLifeTime(5);  
  textFont(font);
}

    // use these names to label objects -- lot easier than cryptic names.
    String cityNames[] = {
      "Albania", "Algeria", "Andorra", "Angola", "Armenia", "Aruba", "Austria", "Bahamas", "Bahrain", "Belarus", "Belgium", "Belize", "Benin", "Bermuda", "Bhutan", "Bolivia", "Brazil", "Brunei", "Burundi", "Canada", "Chad", "Chile", "Comoros", "Croatia", "Cuba", "Curaçao", "Cyprus", "Denmark", "Ecuador", "Egypt", "England", "Eritrea", "Estonia", "Fiji", "Finland", "France", "Gabon", "Gambia", "Georgia", "Germany", "Ghana", "Greece", "Grenada", "Guam", "Guinea", "Guyana", "Haiti", "Hungary", "Iceland", "India", "Iran", "Iraq", "Ireland", "Israel", "Italy", "Jamaica", "Japan", "Jersey", "Jordan", "Kenya", "Kuwait", "Laos", "Latvia", "Lebanon", "Lesotho", "Liberia", "Libya", "Malawi", "Mali", "Malta", "Mexico", "Moldova", "Monaco", "Morocco", "Myanmar", "Namibia", "Nauru", "Nepal", "Niger", "Nigeria", "Niue", "Norway", "Oman", "Palau", "Panama", "Peru", "Poland", "Qatar", "Romania", "Russia", "Rwanda", "Samoa", "Senegal", "Serbia", "Somalia", "Spain", "Sudan", "Sweden", "Syria", "Taiwan", "Togo", "Tonga", "Tunisia", "Turkey", "Tuvalu", "Uganda", "Ukraine", "Uruguay", "Vanuatu", "Vietnam", "Wales", "Yemen", "Zambia", "Abuja", "Accra", "Algiers", "Alofi", "Amman", "Ankara", "Apia", "Ashgabat", "Asmara", "Astana", "Asunción", "Athens", "Avarua", "Baghdad", "Baku", "Bamako", "Bangkok", "Bangui", "Banjul", "Beijing", "Beirut", "Belfast", "Belgrade", "Belmopan", "Berlin", "Bern", "Bishkek", "Bissau", "Bogotá", "Brasília", "Brussels", "Budapest", "Cairo", "Canberra", "Caracas", "Cardiff", "Castries", "Cayenne", "Chisinau", "Conakry", "Dakar", "Damascus", "Dhaka", "Dili", "Djibouti", "Doha", "Douglas", "Dublin", "Dushanbe", "Freetown", "Funafuti", "Gaborone", "Gustavia", "Hagåtña", "Hamilton", "Hanoi", "Harare", "Hargeisa", "Havana", "Helsinki", "Honiara", "Jakarta", "Juba", "Kabul", "Kampala", "Khartoum", "Kiev", "Kigali", "Kingston", "Kingston", "Kinshasa", "Lilongwe", "Lima", "Lisbon", "Lomé", "London", "Luanda", "Lusaka", "Madrid", "Majuro", "Malabo", "Malé", "Managua", "Manama", "Manila", "Maputo", "Marigot", "Maseru", "Mata-Utu", "Melekeok", "Minsk", "Monaco", "Monrovia", "Moroni", "Moscow", "Muscat", "Nairobi", "Nassau", "Niamey", "Nicosia", "Nicosia", "Nouméa", "Nuuk", "Oslo", "Ottawa", "Palikir", "Papeete", "Paris", "Prague", "Praia", "Pristina", "Quito", "Rabat", "Riga", "Riyadh", "Rome", "Roseau", "Saipan", "San José", "San Juan", "Sanaá", "Santiago", "São Tomé", "Sarajevo", "Seoul", "Skopje", "Sofia", "Stanley", "Sukhumi", "Suva", "Taipei", "Tallinn", "Tarawa", "Tashkent", "Tbilisi", "Tehran", "Thimphu", "Tirana", "Tiraspol", "Tokyo", "Tórshavn", "Tripoli", "Tunis", "Vaduz", "Valletta", "Victoria", "Vienna", "Vilnius", "Warsaw", "Windhoek", "Yaoundé", "Yerevan", "Zagreb"
    };
    // int namePos = 0;
    final int nameMax = cityNames.length;


void draw(){
  if(video.available()) {
    video.read();
    videoinput.copy(video, 0, 0, 320, 240, 0, 0, videores, videores);
    blobs = flob.track(  flob.binarize(videoinput) );    
  }
  image(flob.getSrcImage(), 0, 0, width, height);

  fill(255,100);
  stroke(255,200);
  rectMode(CENTER);

  for(int i = 0; i < blobs.size(); i++) {
    TBlob tb = flob.getTBlob(i);
   
    // String txt = "id: "+tb.id+" time: "+tb.presencetime+" ";
    //String txt = cityNames[tb.id%nameMax] +" presence: "+(tb.presencetime)+" ";
    String txt = "id: "+tb.id+" "+ cityNames[tb.id%nameMax] +" presence: "+(tb.presencetime)+" ";
    float velmult = 100.0f;
    fill(220,220,255,100);
    rect(tb.cx,tb.cy,tb.dimx,tb.dimy);
    fill(0,255,0,200);
    rect(tb.cx,tb.cy, 5, 5); 
    fill(0);
    line(tb.cx, tb.cy, tb.cx + tb.velx * velmult ,tb.cy + tb.vely * velmult ); 
    text(txt,tb.cx -tb.dimx*0.10f, tb.cy + 5f);   
  }



  // stats
  fill(255,152,255);
  rectMode(CORNER);
  rect(5,5,flob.getPresencef()*width,10);
  String stats = ""+frameRate+"\nflob.numblobs: "+blobs.size()+"\nflob.thresh:"+tresh+
                 " <t/T>"+"\nflob.fade:"+fade+"   <f/F>"+"\nflob.om:"+flob.getOm()+
                 "\nflob.image:"+videotex+"\nflob.presence:"+flob.getPresencef()
                 +"\nease:"+flob.continuous_ease
                 +"\npress space to clear background";
  fill(0,255,0);
  text(stats,5,25);

    
}

void keyPressed(){
  if(key=='b')
    drawimg^=true;
  if (key=='s')
    saveFrame("flobtrack-######.png");
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
  if(key==' ') //space clear flob.background
    flob.setBackground(videoinput);
  

  if(key=='e'){
    flob.continuous_ease-=0.05;
  }
  if(key=='E'){
    flob.continuous_ease+=0.05;
  }   

  
 
}
