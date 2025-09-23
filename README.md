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

## Quick Start - 3 Simple Commands

The system is designed to be simple to run with sensible defaults:

### 1. Start Directory Server
```bash
.\run-directory.bat
```
Starts the directory server on ports 8080 (TCP) and 8081 (UDP)

### 2. Start Vinyl Server  
```bash
.\run-server.bat
```
Starts a vinyl server with default name `defaultserver.group1.pro2x` on port 9090

### 3. Run Client
```bash
.\run-client.bat
```
Looks up and displays the default server location

### 4. Stop All Components
```bash
.\stop-all.bat
```
Stops all running Java processes and clears ports for a fresh restart

**That's it!** The system is now running with all components communicating.

### Optional Parameters

If you need custom configurations:

```bash
# Custom server name
.\run-server.bat myserver.group1.pro2x

# Custom client lookup
.\run-client.bat myserver.group1.pro2x

# Client connection mode
.\run-client.bat myserver.group1.pro2x connect
```

## Advanced Configuration

### Running Components

#### Directory Server (Advanced)
```bash
# Uses fixed ports: TCP 8080, UDP 8081
.\run-directory.bat
```

#### Vinyl Server (Advanced)
```bash
# Full parameter control
.\run-server.bat <server-name> <server-ip> <server-port> [directory-ip] [directory-port] [ttl-seconds]
```

Examples:
```bash
.\run-server.bat myserver.group1.pro2x 192.168.1.100 9090
.\run-server.bat testserver.group2.pro2y 127.0.0.1 9091 localhost 8080 600
```

#### Client (Advanced)
```bash
.\run-client.bat <server-name> [directory-ip] [directory-port] [mode]
```

Modes:
- `lookup`: Just resolve server name to IP:port (default)
- `connect`: Test connection to server
- `message <text>`: Send a single message  
- `interactive`: Start interactive chat session

Examples:
```bash
# Interactive session
.\run-client.bat myserver.group1.pro2x localhost 8081 interactive

# Send single message
.\run-client.bat myserver.group1.pro2x localhost 8081 message "Hello Server!"
```

## Building the Project

### Prerequisites
- Java 11 or higher
- Apache Maven 3.6+ (optional, for IDE support)

### Build System
```bash
# Build all modules with javac
.\compile.bat
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

### Quick Test (3 Commands)
1. `.\run-directory.bat` - Start directory server
2. `.\run-server.bat` - Start default vinyl server  
3. `.\run-client.bat` - Lookup default server (should show: `Server found: defaultserver.group1.pro2x at 127.0.0.1:9090`)

### Basic Integration Test
1. Start directory server: `.\run-directory.bat`
2. Start vinyl server: `.\run-server.bat testserver.group1.pro2x`
3. Test lookup: `.\run-client.bat testserver.group1.pro2x`
4. Test connection: `.\run-client.bat testserver.group1.pro2x connect`

### TTL Expiration Test
1. Start directory: `.\run-directory.bat`
2. Start server with short TTL: `.\run-server.bat testserver.group1.pro2x 127.0.0.1 9090 localhost 8080 30`
3. Stop vinyl server (Ctrl+C)
4. Wait 60 seconds for cleanup
5. Test lookup: `.\run-client.bat testserver.group1.pro2x` (should fail with "not found")

### Multiple Servers Test
1. Start directory: `.\run-directory.bat`
2. Start server 1: `.\run-server.bat server1.group1.pro2x`
3. Start server 2: `.\run-server.bat server2.group1.pro2y` (in new terminal)
4. Test both: `.\run-client.bat server1.group1.pro2x` and `.\run-client.bat server2.group1.pro2y`

### Error Handling Test
1. Try registering server with invalid name format
2. Try looking up non-existent server
3. Try connecting to stopped server
4. Verify appropriate error messages

## Shutdown Options

### Quick Stop (Recommended)
```bash
.\stop-all.bat
```
- Immediately stops all Java processes
- Clears all ports (8080, 8081, 9090)
- Shows status and restart instructions

### Gentle Shutdown
```bash
.\shutdown.bat
```
- Provides instructions for manual shutdown
- Shows current Java processes
- Allows graceful component shutdown with Ctrl+C

### Manual Shutdown
If you prefer manual control:
1. Go to each terminal running a component
2. Press `Ctrl+C` to stop the process
3. If prompted "Terminate batch job (Y/N)?", press `Y`

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
├── stop-all.bat             # Stop all components (quick)
├── shutdown.bat             # Gentle shutdown guide
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