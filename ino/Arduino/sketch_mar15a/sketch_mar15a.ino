#include <SoftwareSerial.h>
#define RxD 2
#define TxD 3
 
SoftwareSerial blueToothSerial(RxD,TxD);
 
void setup()
{
  Serial.begin(9600);
  pinMode(RxD, INPUT);
  pinMode(TxD, OUTPUT);
  setupBlueToothConnection();
}
 
void loop()
{
  char recvChar;
  while(1){
    if(blueToothSerial.available()){
      recvChar = blueToothSerial.read();
      Serial.print(recvChar);
    }
    if(Serial.available()){
      recvChar  = Serial.read();
      blueToothSerial.print(recvChar);
    }
  }
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
