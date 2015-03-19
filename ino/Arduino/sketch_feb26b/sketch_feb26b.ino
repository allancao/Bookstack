/*
 *    FILE: MM01
 *  AUTHOR: Rob van den Tillaart; modified Ralph Martin
 *    DATE: 2012 06 10 
 *     ORIGINAL URL: http://playground.arduino.cc/Code/HallEffect
 *
 * PURPOSE: use an A1301 or A1302 as magnetometer   
 *
 * Pin Layout LH Package
 * =====================
 *  1     VCC 5V
 *  2     signal    connected to Analog 0    
 *  3     GND
 *
 * Pin Layout UA Package
 * =====================
 *  1     VCC 5V
 *  2     GND
 *  3     signal    connected to Analog 0    
 *
 */
#include <Time.h> 
#define NOFIELD 505L    // Analog output with no applied field, calibrate this
// Uncomment one of the lines below according to device in use A1301 or A1302
// This is used to convert the analog voltage reading to milliGauss
#define TOMILLIGAUSS 1953L  // For A1301: 2.5mV = 1Gauss, and 1024 analog steps = 5V, so 1 step = 1953mG
// #define TOMILLIGAUSS 3756L  // For A1302: 1.3mV = 1Gauss, and 1024 analog steps = 5V, so 1 step = 3756mG

boolean HISTORY;
String DATA;

void setup() 
{
  Serial.begin(9600);
  setTime(1425168000);
  if (DoMeasurement()==9) HISTORY = 1;  
  Serial.print(now());
  Serial.print(",");
  Serial.print(HISTORY);
  Serial.println();
}

long DoMeasurement()
{
// measure magnetic field
  int raw = analogRead(A0);   // Range : 0..1024

//  Uncomment this to get a raw reading for calibration of no-field point
//    Serial.print("Raw reading: ");
//    Serial.println(raw);

  long compensated = raw - NOFIELD;                 // adjust relative to no applied field 
  long gauss = compensated * TOMILLIGAUSS / 1000;   // adjust scale to Gauss

  //Serial.println(raw);
  
  return gauss;
  
  
}

void loop() 
{
  long sum = 0;
  bool reading;
  for (int i = 0; i<10; i++)
{
  delay(500);
  //Serial.println(DoMeasurement());
  sum = sum + abs(DoMeasurement());
}     
  if ((7 <= (sum/10)) && (sum/10 <11)) 
  {
  //Serial.println(sum/10);
  reading = 1;
}
  else {
    //Serial.println(sum/10);
    reading = 0;
}
  
  if (reading != HISTORY){
   Serial.print(now()); 
   Serial.print(",");
   Serial.print(reading);
   Serial.println();
   
  } 
  HISTORY = reading;
}
