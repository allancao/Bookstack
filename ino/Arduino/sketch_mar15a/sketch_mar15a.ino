#include <SoftwareSerial.h>
#define RxD 2
#define TxD 3
#define NOFIELD 505L    // Analog output with no applied field, calibrate this
// Uncomment one of the lines below according to device in use A1301 or A1302
// This is used to convert the analog voltage reading to milliGauss
#define TOMILLIGAUSS 1953L  // For A1301: 2.5mV = 1Gauss, and 1024 analog steps = 5V, so 1 step = 1953mG
// #define TOMILLIGAUSS 3756L  // For A1302: 1.3mV = 1Gauss, and 1024 analog steps = 5V, so 1 step = 3756mG

 
SoftwareSerial blueToothSerial(RxD,TxD);
boolean HISTORY;
String DATA;

void setup()
{
  Serial.begin(9600);
  pinMode(RxD, INPUT);
  pinMode(TxD, OUTPUT);
  setupBlueToothConnection();
  
  if (DoMeasurement(A0)==9) HISTORY = 1;  
  blueToothSerial.println(HISTORY);
}
 
void loop()
{  
  delay(500);
  DoMeasurement(A0);       
}

long DoMeasurement(int pin)
{
// measure magnetic field
  int raw = analogRead(pin);   // Range : 0..1024

//  Uncomment this to get a raw reading for calibration of no-field point
//  Serial.print("Raw reading: ");
//  Serial.println(raw);

  long compensated = raw - NOFIELD;                 // adjust relative to no applied field 
  long gauss = compensated * TOMILLIGAUSS / 1000;   // adjust scale to Gauss

//  Serial.println(raw);
  Serial.println(gauss);
  blueToothSerial.println(gauss);
  
  return gauss;
}


 
void setupBlueToothConnection()
{
  blueToothSerial.begin(38400); //Set BluetoothBee BaudRate to default baud rate 38400
  blueToothSerial.print("\r\n+STWMOD=0\r\n"); //set the bluetooth work in slave mode
  blueToothSerial.print("\r\n+STNA=SeeedBTSlave\r\n"); //set the bluetooth name as "SeeedBTSlave"
  blueToothSerial.print("\r\n+STPIN=0000\r\n");//Set SLAVE pincode"0000"
  blueToothSerial.print("\r\n+STOAUT=1\r\n"); // Permit Paired device to connect me
  //blueToothSerial.print("\r\n+STAUTO=0\r\n"); // Auto-connection should be forbidden here
  delay(2000); // This delay is required.
  blueToothSerial.print("\r\n+INQ=1\r\n"); //make the slave bluetooth inquirable
  Serial.println("The slave bluetooth is inquirable!");
  delay(2000); // This delay is required.
  Serial.println("2 seconds passed");
  blueToothSerial.flush();
}
