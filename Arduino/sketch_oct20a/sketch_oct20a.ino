#include <SoftwareSerial.h>

#define bluetouthReceivePin A0 // The input pin for receiving bluetouth messages.
#define bluetouthTransmitPin A1 // The output pin for transmitting bluetouth messages.

SoftwareSerial bt( bluetouthReceivePin, bluetouthTransmitPin );


void setup()
{
  Serial.begin(38400);
  pinMode(13, OUTPUT);


  bt.begin(38400);

}

void loop()
{
//  bt.writeln(speed + "&" + distance + "&" + totalDrivingTime); // 30&150&3000

  bt.write("300&150&3000\n");
  delay(1000);
  if ( bt.available())
  {
    //bt.write(bt.read());
    
  }
}
