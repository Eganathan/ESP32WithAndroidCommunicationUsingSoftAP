#include <ESP8266WiFi.h>
#include <ESP8266WebServer.h>
#include <ArduinoJson.h>

// SoftAP SSID & Password
const char* apSSID = "Eknath_SoftAPExperiment";
const char* apPassword = "12345678";

ESP8266WebServer server(80);

// Data storage for CRUD operations
struct InputData {
  int id;
  String message;
  String timestamp;
};

InputData inputs[10]; // Store up to 10 inputs
int inputCount = 0;
int nextId = 1;

// JsonHelper function
String createJsonResponse(int code, const String& dataJson) {
  DynamicJsonDocument doc(256);
  doc["code"] = code;
  
  DynamicJsonDocument dataDoc(256);
  DeserializationError err = deserializeJson(dataDoc, dataJson);
  
  if (!err) {
    doc["data"] = dataDoc.as<JsonObject>();
  } else {
    doc["data"] = dataJson;
  }
  
  String output;
  serializeJson(doc, output);
  return output;
}

// Root page with CRUD interface
void handleRoot() {
  String html = "<!DOCTYPE html><html><head><title>ESP8266 CRUD API</title></head><body>";
  html += "<h2>ESP8266 CRUD API</h2>";
  html += "<h3>Add Input</h3>";
  html += "<form action=\"/input\" method=\"POST\">";
  html += "Message: <input type=\"text\" name=\"message\" required><br><br>";
  html += "<input type=\"submit\" value=\"Add Input\">";
  html += "</form>";
  html += "<hr>";
  html += "<h3>API Endpoints</h3>";
  html += "<ul>";
  html += "<li><b>POST /input</b> - Create new input</li>";
  html += "<li><b>GET /input</b> - Get all inputs</li>";
  html += "<li><b>GET /input/{id}</b> - Get specific input</li>";
  html += "<li><b>PUT /input/{id}</b> - Update input</li>";
  html += "<li><b>DELETE /input/{id}</b> - Delete input</li>";
  html += "</ul>";
  html += "</body></html>";
  server.send(200, "text/html", html);
}

// CREATE - Add new input
void handleCreateInput() {
  if (inputCount >= 10) {
    String response = createJsonResponse(400, "{\"error\":\"Storage full\"}");
    server.send(400, "application/json", response);
    return;
  }
  
  String message = server.arg("message");
  if (message.length() == 0) {
    String response = createJsonResponse(400, "{\"error\":\"Message required\"}");
    server.send(400, "application/json", response);
    return;
  }
  
  inputs[inputCount].id = nextId++;
  inputs[inputCount].message = message;
  inputs[inputCount].timestamp = String(millis());
  inputCount++;
  
  String dataJson = "{\"id\":" + String(inputs[inputCount-1].id) + 
                   ",\"message\":\"" + inputs[inputCount-1].message + 
                   "\",\"timestamp\":\"" + inputs[inputCount-1].timestamp + "\"}";
  String response = createJsonResponse(201, dataJson);
  server.send(201, "application/json", response);
}

// READ - Get all inputs
void handleGetAllInputs() {
  String dataJson = "{\"inputs\":[";
  for (int i = 0; i < inputCount; i++) {
    if (i > 0) dataJson += ",";
    dataJson += "{\"id\":" + String(inputs[i].id) + 
               ",\"message\":\"" + inputs[i].message + 
               "\",\"timestamp\":\"" + inputs[i].timestamp + "\"}";
  }
  dataJson += "],\"count\":" + String(inputCount) + "}";
  
  String response = createJsonResponse(200, dataJson);
  server.send(200, "application/json", response);
}

// Handle individual input routes manually
void handleNotFound() {
  String uri = server.uri();
  
  // Check if it's an input/{id} pattern
  if (uri.startsWith("/input/")) {
    String idStr = uri.substring(7); // Remove "/input/"
    
    if (server.method() == HTTP_GET) {
      handleGetInputById(idStr);
    } else if (server.method() == HTTP_PUT) {
      handleUpdateInputById(idStr);
    } else if (server.method() == HTTP_DELETE) {
      handleDeleteInputById(idStr);
    } else {
      server.send(405, "text/plain", "Method Not Allowed");
    }
  } else {
    server.send(404, "text/plain", "Not Found");
  }
}

// READ - Get specific input by ID
void handleGetInputById(String idStr) {
  int id = idStr.toInt();
  
  for (int i = 0; i < inputCount; i++) {
    if (inputs[i].id == id) {
      String dataJson = "{\"id\":" + String(inputs[i].id) + 
                       ",\"message\":\"" + inputs[i].message + 
                       "\",\"timestamp\":\"" + inputs[i].timestamp + "\"}";
      String response = createJsonResponse(200, dataJson);
      server.send(200, "application/json", response);
      return;
    }
  }
  
  String response = createJsonResponse(404, "{\"error\":\"Input not found\"}");
  server.send(404, "application/json", response);
}

// UPDATE - Update input by ID  
void handleUpdateInputById(String idStr) {
  int id = idStr.toInt();
  String newMessage = server.arg("message");
  
  if (newMessage.length() == 0) {
    String response = createJsonResponse(400, "{\"error\":\"Message required\"}");
    server.send(400, "application/json", response);
    return;
  }
  
  for (int i = 0; i < inputCount; i++) {
    if (inputs[i].id == id) {
      inputs[i].message = newMessage;
      inputs[i].timestamp = String(millis());
      
      String dataJson = "{\"id\":" + String(inputs[i].id) + 
                       ",\"message\":\"" + inputs[i].message + 
                       "\",\"timestamp\":\"" + inputs[i].timestamp + "\"}";
      String response = createJsonResponse(200, dataJson);
      server.send(200, "application/json", response);
      return;
    }
  }
  
  String response = createJsonResponse(404, "{\"error\":\"Input not found\"}");
  server.send(404, "application/json", response);
}

// DELETE - Delete input by ID
void handleDeleteInputById(String idStr) {
  int id = idStr.toInt();
  
  for (int i = 0; i < inputCount; i++) {
    if (inputs[i].id == id) {
      for (int j = i; j < inputCount - 1; j++) {
        inputs[j] = inputs[j + 1];
      }
      inputCount--;
      
      String response = createJsonResponse(200, "{\"message\":\"Input deleted\"}");
      server.send(200, "application/json", response);
      return;
    }
  }
  
  String response = createJsonResponse(404, "{\"error\":\"Input not found\"}");
  server.send(404, "application/json", response);
}



void setup() {
  Serial.begin(115200);
  delay(1000);
  
  // Set static IP for SoftAP (gateway, subnet)
  IPAddress local_ip(192, 168, 4, 1);
  IPAddress gateway(192, 168, 4, 1);
  IPAddress subnet(255, 255, 255, 0);
  
  WiFi.softAPConfig(local_ip, gateway, subnet);
  
  // Start ESP8266 as SoftAP
  WiFi.softAP(apSSID, apPassword);
  Serial.print("SoftAP started. Connect to SSID: ");
  Serial.println(apSSID);
  Serial.print("IP Address: ");
  Serial.println(WiFi.softAPIP());

  // Setup server routes
  server.on("/", HTTP_GET, handleRoot);
  server.on("/input", HTTP_POST, handleCreateInput);
  server.on("/input", HTTP_GET, handleGetAllInputs);
  server.onNotFound(handleNotFound);

  server.begin();
  Serial.println("WebServer started.");
}

void loop() {
  server.handleClient();
}