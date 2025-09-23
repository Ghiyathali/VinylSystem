# Vinyl System - Java Implementation

A distributed vinyl server registration and lookup system implemented in Java with TCP/UDP protocols.

## Architecture

The system consists of three main components:

### 1. Directory Server (`directory/`)
- **TCP Server** (Port 8080): Handles vinyl server registrations and updates
- **UDP Server** (Port 8081): Handles client lookup requests
- **In-memory Registry**: Thread-safe storage with TTL management
- **Cleanup Mechanism**: Automatically removes expired server entries

### 2. Vinyl Server (`server/`)
- **Registration Client**: TCP client that registers with directory server
- **TTL Management**: Automatic refresh of registration before expiration
- **Client Handler**: Accepts and handles client connections
- **Name Validation**: Enforces `<string>.group#.pro2[x|y]` naming convention

### 3. Vinyl Client (`client/`)
- **Directory Lookup**: UDP client for server name resolution
- **Server Connection**: TCP client for connecting to vinyl servers
- **Interactive Mode**: Terminal-based communication with servers

### 4. Common Library (`common/`)
- **Message Classes**: JSON serializable protocol messages
- **Status Codes**: Standardized response codes (000001, 000002, etc.)
- **Validation Utils**: Input validation for names, IPs, ports, TTL
- **Protocol Constants**: Configuration constants and patterns

## Protocol Specification

### Status Codes
- `000001`: Success
- `000002`: Invalid Request
- `000003`: Server Error  
- `000100`: Not Found
- `000004`: Invalid Name Format
- `000005`: Invalid IP Format

### Message Types

#### Registration Message (TCP - Vinyl Server → Directory)
```json
{
  "messageType": "REGISTER|UPDATE",
  "serverName": "myserver.group1.pro2x",
  "serverIP": "192.168.1.100",
  "serverPort": 9090,
  "ttl": 300
}
```

#### Lookup Message (UDP - Client → Directory)
```json
{
  "messageType": "LOOKUP",
  "serverName": "myserver.group1.pro2x"
}
```

#### Response Message
```json
{
  "statusCode": "000001",
  "message": "Success",
  "serverIP": "192.168.1.100",
  "serverPort": 9090
}
```

### Name Format Validation
Server names must follow the pattern: `<string>.group#.pro2[x|y]`

Examples:
- ✅ `myserver.group1.pro2x`
- ✅ `server.group123.pro2y`
- ✅ `test-server.group1.pro2`
- ❌ `invalid.group.pro2x`
- ❌ `server.group1.pro3`

## Building and Running

### Prerequisites
- Java 11 or higher
- Apache Maven 3.6+

### Build System
```bash
# Build all modules
build.bat

# Run tests
test.bat
```

### Running Components

#### 1. Start Directory Server
```bash
start-directory.bat
```
The directory server will start on:
- TCP Port 8080 (vinyl server registrations)
- UDP Port 8081 (client lookups)

#### 2. Start Vinyl Server
```bash
start-server.bat <server-name> <server-ip> <server-port> [directory-ip] [directory-port] [ttl-seconds]
```

Example:
```bash
start-server.bat myserver.group1.pro2x 192.168.1.100 9090
start-server.bat testserver.group2.pro2y localhost 9091 localhost 8080 600
```

#### 3. Start Client
```bash
start-client.bat <server-name> [directory-ip] [directory-port] [mode]
```

Modes:
- `lookup`: Just resolve server name to IP:port
- `connect`: Test connection to server
- `message <text>`: Send a single message
- `interactive`: Start interactive chat session

Examples:
```bash
# Just lookup server
start-client.bat myserver.group1.pro2x

# Interactive session
start-client.bat myserver.group1.pro2x localhost 8081 interactive

# Send single message
start-client.bat myserver.group1.pro2x localhost 8081 message "Hello Server!"
```

## Manual Compilation (Alternative)

If you prefer manual compilation:

```bash
# 1. Build common library
cd common
mvn clean install

# 2. Build and run directory server
cd ../directory
mvn clean compile
mvn exec:java

# 3. Build and run vinyl server (in new terminal)
cd ../server
mvn clean compile
mvn exec:java -Dexec.args="myserver.group1.pro2x 192.168.1.100 9090"

# 4. Build and run client (in new terminal)
cd ../client
mvn clean compile
mvn exec:java -Dexec.args="myserver.group1.pro2x localhost 8081 interactive"
```

## Testing Scenarios

### Basic Integration Test
1. Start directory server
2. Start vinyl server with name `testserver.group1.pro2x`
3. Use client to lookup and connect to the server
4. Send messages and verify responses

### TTL Expiration Test
1. Start directory server
2. Start vinyl server with short TTL (e.g., 30 seconds)
3. Stop vinyl server
4. Wait for TTL expiration
5. Try client lookup - should fail with "not found"

### Multiple Servers Test
1. Start directory server
2. Start multiple vinyl servers with different names
3. Use client to lookup and connect to each server
4. Verify correct routing

### Error Handling Test
1. Try registering server with invalid name format
2. Try looking up non-existent server
3. Try connecting to stopped server
4. Verify appropriate error messages

## Configuration

### Default Ports
- Directory TCP: 8080
- Directory UDP: 8081
- Vinyl Server: 9090

### TTL Settings
- Default TTL: 300 seconds (5 minutes)
- Cleanup Interval: 30 seconds
- Refresh Interval: 60% of TTL

### Network Settings
- Socket Timeout: 5 seconds
- Max Message Size: 1024 bytes

## Troubleshooting

### Common Issues

1. **"Port already in use"**
   - Change ports in command line arguments
   - Kill existing processes using the ports

2. **"Connection refused"**
   - Ensure directory server is running first
   - Check firewall settings
   - Verify correct IP addresses and ports

3. **"Invalid name format"**
   - Ensure server name follows `<string>.group#.pro2[x|y]` pattern
   - Check for typos in group number or suffix

4. **"Server not found"**
   - Verify server is registered and running
   - Check if TTL has expired
   - Ensure client is connecting to correct directory

### Debug Mode
Add `-X` flag to Maven commands for verbose output:
```bash
mvn exec:java -X -Dexec.args="..."
```

## Project Structure
```
VinylSystem/
├── compile.bat               # Build script (javac-based)
├── run-directory.bat         # Start directory server
├── run-server.bat           # Start vinyl server  
├── run-client.bat           # Start client
├── README.md                # This documentation
├── build/                   # Compiled classes (generated)
├── common/                  # Shared libraries and message classes
│   └── src/main/java/com/vinylsystem/common/
├── directory/               # Directory server implementation
│   └── src/main/java/com/vinylsystem/directory/
├── server/                  # Vinyl server implementation
│   └── src/main/java/com/vinylsystem/server/
└── client/                  # Client implementation
    └── src/main/java/com/vinylsystem/client/
```

## Dependencies
- **Java 11+**: Minimum runtime version
- **No external libraries**: Pure Java implementation with custom JSON handling