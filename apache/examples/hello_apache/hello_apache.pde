// hello s373.apache

import s373.apache.*;

ApacheClient apachehttp;
String args[] = {"ping","1234567890","abc", "pairthis"};

void setup(){
  apachehttp = new ApacheClient("http://google.com", 1000);
  String result = apachehttp.POST(args);
  println("APACHE result: "+result);
}


void draw(){
  
  String replies[] = apachehttp.available();
//  println("APACHE frame "+frameCount+" replies: "+replies.length);
  
  if(replies.length>1){
//    println("APACHE reply: "+replies[0]);
    println("REPLY ARRIVED: "+frameCount);
    println(replies);
  }
  
}
