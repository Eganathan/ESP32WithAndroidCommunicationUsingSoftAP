// JsonHelper.h
// Utility functions for creating and parsing JSON on ESP8266
// Requires: ArduinoJson library (https://arduinojson.org/)

#ifndef JSON_HELPER_H
#define JSON_HELPER_H

#include <ArduinoJson.h>
#include <Arduino.h>

/// Creates a JSON string with a "code" and "data" object.
/// Example:
///   createJsonResponse(200, "{\"temp\":25}");
/// Produces:
///   {"code":200,"data":{"temp":25}}
String createJsonResponse(int code, const String& dataJson);

/// Parses a JSON string and extracts the "code" and "data".
/// Example Input:
///   {"code":200,"data":{"temp":25}}
/// Outputs:
///   code -> 200
///   data -> {"temp":25}
/// Returns true if parsing succeeded.
bool parseJsonRequest(const String& jsonStr, int& code, String& dataJson);

#endif