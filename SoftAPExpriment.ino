#include <ESP8266WiFi.h>

// SSID & Password for SoftAP
const char* ssid     = "ESP32_AP";
const char* password = "12345678"; // Minimum 8 characters

void setup() {
  Serial.begin(115200);
  Serial.println("Configuring access point...");

  // Start the WiFi in AP mode
  WiFi.softAP(ssid, password);

  IPAddress IP = WiFi.softAPIP();  // Get the IP address of the ESP32 AP
  Serial.print("AP IP address: ");
  Serial.println(IP);

  Serial.print("SSID: ");
  Serial.println(ssid);
  Serial.print("Password: ");
  Serial.println(password);
}

void loop() {
  // Just keeping it alive
}