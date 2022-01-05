#include <ESP8266WiFi.h>
#include <DNSServer.h>
#include <ESP8266WebServer.h>

const byte DNS_PORT = 53;
IPAddress apIP(192, 168, 1, 1);
DNSServer dnsServer;
ESP8266WebServer webServer(80);


float p,i,d,error,pid;   
String message = "";


void setup() {
  Serial.begin(115200);
  WiFi.mode(WIFI_AP);
  WiFi.softAPConfig(apIP, apIP, IPAddress(255, 255, 255, 0));
  WiFi.softAP("EMG PID","987654321");

  // modify TTL associated  with the domain name (in seconds)
  // default is 60 seconds
  dnsServer.setTTL(300);
  // set which return code will be used for all other domains (e.g. sending
  // ServerFailure instead of NonExistentDomain will reduce number of queries
  // sent by clients)
  // default is DNSReplyCode::NonExistentDomain
  dnsServer.setErrorReplyCode(DNSReplyCode::ServerFailure);

  // start DNS server for a specific domain name
  dnsServer.start(DNS_PORT, "www.eslam-gad.com", apIP);

  // simple HTTP server to see that DNS server is working
  webServer.onNotFound([]() {
    String ur_i=webServer.uri();
    if(ur_i[1]=='*'){
      Serial.println(ur_i);
      }
      else if(ur_i[1]=='#'){
          
        }
    webServer.send(200, "text/plain", message);
  });
  webServer.begin();
}

void loop() {
  if(Serial.available()){
   String x=Serial.readStringUntil('?');
    if(x.indexOf("*")>=0){
    message=x;
    }
  }
  dnsServer.processNextRequest();
  webServer.handleClient();
  
}
