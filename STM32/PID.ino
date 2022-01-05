#include <Servo.h>

Servo myservo;  

float k_p=3,k_i=5,k_d=0,err=0, xn=0,xn_1=0,I=0,pid,set_point=30;
unsigned long t=0;
int send_time=0;
float servo_0 =0;
float servo_180 =0;
  float z=0;

void setup() {
  Serial.begin(115200);
  Serial2.begin(115200);
  //PB0 for pwm pid
  pinMode(PB0,PWM);
  //PA8 for rc circuit (analog pid)
  pinMode(PA8,PWM);
  pinMode(PA7,INPUT);
  pinMode(PA5,INPUT);
   delay(1000);
  }

void compute_pid(float error,int rate){
  xn_1=xn;
  xn = error;
  float  D=(xn-xn_1)*rate;
  I+=xn/rate;
  pid=k_p*xn+k_i*I+k_d*D;
 
  if(pid<0){
    pid=0;
  }
  if(pid>65535){
      pid=65535;
  }
  String x="*p";
  Serial2.print(x+pid/65535*3.3+"e"+err/65535*3.3+"?");
    
}
  
void loop() {
  z=map((float)analogRead(PA5),0,4096,0,65535);
  err=set_point-z;
  if(micros()-t>=1000){
    t=micros();
    compute_pid(err,1000);    
  }
  if(Serial2.available()){
    String x= Serial2.readStringUntil('?');
    if(x[1]=='*'){
    k_p= x.substring(x.indexOf("p")+1,x.indexOf("i")).toFloat();
    //Serial.print("kp"); Serial.println(k_p);
    k_i= x.substring(x.indexOf("i")+1,x.indexOf("d")).toFloat();
    //Serial.print("ki"); Serial.println(k_i);
    k_d= x.substring(x.indexOf("d")+1,x.indexOf("s")).toFloat();
    //  Serial.print("kd");Serial.println( k_d);
    set_point= x.substring(x.indexOf("s")+1,x.indexOf("?")).toFloat();
    set_point=map(set_point,0,1000,0,65535);
    //    Serial.print("set_point");
    }
  }
  //max voltage(3.3)=65535
  Serial.println(pid);
  pwmWrite(PB0,pid);
  pwmWrite(PA8,pid);

  }
