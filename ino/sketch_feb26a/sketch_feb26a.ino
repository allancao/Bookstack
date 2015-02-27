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
  HISTORY = DoMeasurement();
  Serial.print(now());
  Serial.print(",");
  Serial.print(DoMeasurement());
  Serial.println();
}

boolean DoMeasurement()
{
// measure magnetic field
  int raw = analogRead(0);   // Range : 0..1024

//  Uncomment this to get a raw reading for calibration of no-field point
//    Serial.print("Raw reading: ");
//    Serial.println(raw);

  long compensated = raw - NOFIELD;                 // adjust relative to no applied field 
  long gauss = compensated * TOMILLIGAUSS / 1000;   // adjust scale to Gauss

  //Serial.print(now());
  //Serial.println(gauss);

  if (gauss == 9) return true;
  else return false;
  
  
}

void loop() 
{
    HISTORY = DoMeasurement();
    //Serial.println(HISTORY);
    delay(1000);
    if (HISTORY != DoMeasurement()){
      DATA = char(now())+","+char(DoMeasurement());
     Serial.print(now());
      Serial.print(",");
      Serial.print(DoMeasurement());
      Serial.println();
    }       
}
