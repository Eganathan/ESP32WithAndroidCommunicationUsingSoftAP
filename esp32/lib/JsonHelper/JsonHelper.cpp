// JsonHelper.cpp
#include "JsonHelper.h"

// Function to create JSON response with code + data
String createJsonResponse(int code, const String& dataJson) {
  // Create a JSON document with enough capacity
  DynamicJsonDocument doc(256);

  // Add fields
  doc["code"] = code;

  // Parse the given data JSON string into a sub-object
  DynamicJsonDocument dataDoc(256);
  DeserializationError err = deserializeJson(dataDoc, dataJson);

  if (!err) {
    doc["data"] = dataDoc.as<JsonObject>();  // If valid JSON, insert it
  } else {
    doc["data"] = dataJson;  // Otherwise, store raw string
  }

  // Convert to String
  String output;
  serializeJson(doc, output);
  return output;
}

// Function to parse JSON and extract "code" and "data"
bool parseJsonRequest(const String& jsonStr, int& code, String& dataJson) {
  DynamicJsonDocument doc(512);

  // Deserialize
  DeserializationError err = deserializeJson(doc, jsonStr);
  if (err) {
    Serial.print("JSON parse error: ");
    Serial.println(err.c_str());
    return false;
  }

  // Extract values
  code = doc["code"] | -1;  // Default -1 if missing

  if (doc.containsKey("data")) {
    String tmp;
    serializeJson(doc["data"], tmp);  // Convert back to string
    dataJson = tmp;
  } else {
    dataJson = "{}";
  }

  return true;
}