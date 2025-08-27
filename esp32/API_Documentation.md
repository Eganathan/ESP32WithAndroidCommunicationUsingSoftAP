# ESP8266 CRUD API Documentation

## Overview
This ESP8266 device provides a RESTful CRUD API for managing user inputs. The device operates as a WiFi Access Point (SoftAP) and hosts a web server with JSON-based API endpoints.

## Connection Details
- **WiFi Network**: `Eknath_SoftAPExperiment`
- **WiFi Password**: `12345678`
- **Base URL**: `http://192.168.4.1`
- **Content-Type**: `application/json` (responses), `application/x-www-form-urlencoded` (requests)

## API Endpoints

### 1. Create Input
**POST** `/input`

Creates a new input entry.

**Request Parameters:**
- `message` (string, required): The input message text

**Example Request:**
```bash
curl -X POST http://192.168.4.1/input \
  -d "message=Hello World"
```

**Success Response (201):**
```json
{
  "code": 201,
  "data": {
    "id": 1,
    "message": "Hello World",
    "timestamp": "12345"
  }
}
```

**Error Responses:**
- **400**: Missing message parameter
- **400**: Storage full (max 10 inputs)

---

### 2. Get All Inputs
**GET** `/input`

Retrieves all stored inputs.

**Example Request:**
```bash
curl http://192.168.4.1/input
```

**Success Response (200):**
```json
{
  "code": 200,
  "data": {
    "inputs": [
      {
        "id": 1,
        "message": "Hello World",
        "timestamp": "12345"
      },
      {
        "id": 2,
        "message": "Another message",
        "timestamp": "23456"
      }
    ],
    "count": 2
  }
}
```

---

### 3. Get Specific Input
**GET** `/input/{id}`

Retrieves a specific input by ID.

**Path Parameters:**
- `id` (integer): The input ID

**Example Request:**
```bash
curl http://192.168.4.1/input/1
```

**Success Response (200):**
```json
{
  "code": 200,
  "data": {
    "id": 1,
    "message": "Hello World",
    "timestamp": "12345"
  }
}
```

**Error Response:**
- **404**: Input not found

---

### 4. Update Input
**PUT** `/input/{id}`

Updates an existing input message.

**Path Parameters:**
- `id` (integer): The input ID

**Request Parameters:**
- `message` (string, required): The new message text

**Example Request:**
```bash
curl -X PUT http://192.168.4.1/input/1 \
  -d "message=Updated message"
```

**Success Response (200):**
```json
{
  "code": 200,
  "data": {
    "id": 1,
    "message": "Updated message",
    "timestamp": "34567"
  }
}
```

**Error Responses:**
- **400**: Missing message parameter
- **404**: Input not found

---

### 5. Delete Input
**DELETE** `/input/{id}`

Deletes a specific input by ID.

**Path Parameters:**
- `id` (integer): The input ID

**Example Request:**
```bash
curl -X DELETE http://192.168.4.1/input/1
```

**Success Response (200):**
```json
{
  "code": 200,
  "data": {
    "message": "Input deleted"
  }
}
```

**Error Response:**
- **404**: Input not found

---

## Response Format
All API responses follow a consistent JSON structure:

```json
{
  "code": <HTTP_STATUS_CODE>,
  "data": <RESPONSE_DATA>
}
```

## Data Model
Each input has the following structure:

```json
{
  "id": 1,                    // Auto-incrementing unique identifier
  "message": "User message",   // The input text
  "timestamp": "12345"        // Milliseconds since device boot
}
```

## Storage Limitations
- Maximum 10 inputs can be stored simultaneously
- Data is stored in volatile memory (lost on device restart)
- IDs auto-increment and are never reused within a session

## Testing with Mobile Apps
For mobile testing, use REST client apps like:
- **iOS**: REST Client, HTTP Request Shortcut
- **Android**: HTTP Request, REST Client for Android

## Error Handling
- **400 Bad Request**: Invalid or missing parameters
- **404 Not Found**: Resource not found
- **405 Method Not Allowed**: HTTP method not supported for endpoint
- **500 Internal Server Error**: Device error

## Web Interface
Visit `http://192.168.4.1` in a browser for a simple web form to test the CREATE endpoint.

## Development Notes
- The device must be connected to via WiFi before API calls can be made
- All timestamps are in milliseconds since device boot
- The device IP is static at `192.168.4.1`
- CORS is not implemented - requests must be made directly to the device