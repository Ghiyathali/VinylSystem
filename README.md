# VinylSystem - Java 19 Music Discovery Platform 🎵

A sophisticated distributed vinyl server system with **real music database integration** using the Discogs API. Upgraded from Java 11 to Java 19 with live music search capabilities.

## 🎯 Key Features

- **🎶 Real Music Database**: Integration with Discogs API (14+ million vinyl records)  
- **☕ Java 19 Runtime**: Upgraded from Java 11 for modern performance
- **🌐 Distributed Architecture**: TCP/UDP client-server communication
- **🔍 Live Music Search**: Search across genres - Rock, Jazz, Classical, Hip-Hop
- **📀 Vinyl Record Details**: ID, artist, album, year, genre, format, country
- **🔐 Secure Authentication**: Personal Access Token for API access
- **⚡ Custom HTTP Client**: Lightweight, no external dependencies
- **🎨 Multi-Genre Support**: Beatles, Bob Dylan, Miles Davis, Nirvana, Queen

## 🏗️ Architecture

The system consists of four main components:

### 1. Directory Server (`directory/`)
- **TCP Server** (Port 8080): Handles vinyl server registrations and updates
- **UDP Server** (Port 8081): Handles client lookup requests  
- **In-memory Registry**: Thread-safe storage with TTL management
- **Cleanup Mechanism**: Automatically removes expired server entries

### 2. Vinyl Server (`server/`) ⭐ *Enhanced with Discogs API*
- **Registration Client**: TCP client that registers with directory server
- **TTL Management**: Automatic refresh of registration before expiration
- **Music Search Handler**: Processes real-time vinyl record searches
- **Discogs Integration**: Connects to live music database
- **Client Handler**: Accepts and handles client connections  
- **Name Validation**: Enforces `<string>.group#.pro2[x|y]` naming convention

### 3. Vinyl Client (`client/`) ⭐ *Enhanced with Music Search*
- **Directory Lookup**: UDP client for server name resolution
- **Server Connection**: TCP client for connecting to vinyl servers
- **Music Search Demo**: Interactive music database queries
- **Real-time Results**: Live vinyl record information display

### 4. Common Library (`common/`) ⭐ *New Music Components*
- **DiscogsApiService**: Complete Discogs API integration
- **HttpClient**: Custom HTTP client for API requests
- **MusicRelease**: Data model for vinyl records
- **ConfigManager**: API credentials and environment management
- **Message Classes**: JSON serializable protocol messages
- **Status Codes**: Standardized response codes (000001, 000002, etc.)
- **Validation Utils**: Input validation for names, IPs, ports, TTL
- **Protocol Constants**: Configuration constants and patterns

## 🎵 Music Integration

### Discogs API Integration
- **Database Size**: 14+ million vinyl releases
- **Authentication**: Personal Access Token
- **Search Capabilities**: Artist, album, genre, year filtering
- **Response Format**: Custom JSON parsing (no external dependencies)
- **Rate Limiting**: Built-in request management

### Sample Search Results
| Artist/Album | Found Releases |
|---|---|
| Beatles Abbey Road | 2,625 releases |
| Bob Dylan | 64,988 releases |
| Miles Davis | 36,338 jazz releases |
| Nirvana Nevermind | 1,320 releases |
| Queen Bohemian Rhapsody | 6,103 releases |

## 🚀 Protocol Specification

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

## 🎮 Quick Start - Music-Enabled System

### 🎵 Setup Discogs API Access
1. **Get your Discogs Personal Access Token**:
   - Visit [Discogs Developer Settings](https://www.discogs.com/settings/developers)
   - Generate a Personal Access Token
   - Copy the token (looks like: `OkTgLdhXlwBlOoTpnMuRAVqTnbtMXIOGkrwqPAYG`)

2. **Configure API Access** (choose one method):
   ```bash
   # Method 1: Environment Variable (Recommended)
   $env:DISCOGS_ACCESS_TOKEN="your_token_here"
   
   # Method 2: Config File
   # Edit config.properties and add:
   # discogs.access.token=your_token_here
   ```

### 🚀 Start the Complete System

#### 1. Start Directory Server
```bash
.\run-directory.bat
```
Starts the directory server on ports 8080 (TCP) and 8081 (UDP)

#### 2. Start Music-Enabled Vinyl Server  
```bash
$env:DISCOGS_ACCESS_TOKEN="your_token_here"
.\run-server.bat
```
Starts a vinyl server with **live Discogs music database access**

#### 3. Search for Music! 🎶
```bash
# Search for any artist or album
$env:DISCOGS_ACCESS_TOKEN="your_token_here"
java -cp "build" com.vinylsystem.common.VinylSystemDemo
```

#### 4. Interactive Music Search
```bash
# Direct API testing
$env:DISCOGS_ACCESS_TOKEN="your_token_here"
java -cp "build" com.vinylsystem.common.DiscogsApiTest
```

### 🎸 Sample Music Searches

The system can find real vinyl records for any artist:

```bash
# Classic Rock
java -cp "build" com.vinylsystem.common.MusicSearchDemo "Pink Floyd"
java -cp "build" com.vinylsystem.common.MusicSearchDemo "Led Zeppelin"

# Jazz Classics  
java -cp "build" com.vinylsystem.common.MusicSearchDemo "Miles Davis"
java -cp "build" com.vinylsystem.common.MusicSearchDemo "John Coltrane"

# Modern Music
java -cp "build" com.vinylsystem.common.MusicSearchDemo "Radiohead"
java -cp "build" com.vinylsystem.common.MusicSearchDemo "Kendrick Lamar"
```

### 🛑 Stop All Components
```bash
.\stop-all.bat
```

## 🎯 Live Demo Results

When you run the music search, you'll see real vinyl data like this:

```
🎵 VINYLSYSTEM COMPREHENSIVE DEMO 🎵

🔍 Searching for: The Beatles Abbey Road
Found 3 vinyl records:
  🎼 Abbey Road by The Beatles (2016) - LP vinyl [ID: 9269057]
  🎼 Abbey Road by The Beatles (2019) - LP vinyl [ID: 14186441]
  🎼 Abbey Road by The Beatles (2019) - LP vinyl [ID: 14192689]

🔍 Searching for: Bob Dylan  
Found 3 vinyl records:
  🎼 The Freewheelin' Bob Dylan by Bob Dylan (2018) - LP vinyl [ID: 11815836]
  🎼 Bob Dylan by Bob Dylan (1962) - LP vinyl [ID: 1189504]
  🎼 The Freewheelin' Bob Dylan by Bob Dylan (1963) - LP vinyl [ID: 1596438]
```

### Advanced Music Search Options

### Advanced Music Search Options

If you need custom configurations:

```bash
# Custom vinyl server with music search
$env:DISCOGS_ACCESS_TOKEN="your_token_here"
.\run-server.bat myserver.group1.pro2x

# Custom music search through client
.\run-client.bat myserver.group1.pro2x

# Direct music API search  
java -cp "build" com.vinylsystem.common.DiscogsApiService
```

## 🔧 Advanced Configuration

### Building the Project

#### Prerequisites
- **Java 19** (upgraded from Java 11)
- Apache Maven 3.6+ (optional, for IDE support)
- **Discogs API Access** (Personal Access Token)

#### Build System
```bash
# Build all modules with javac (Java 19)
.\compile.bat
```

### Running Components

#### Directory Server (Advanced)
```bash
# Uses fixed ports: TCP 8080, UDP 8081
.\run-directory.bat
```

#### Music-Enabled Vinyl Server (Advanced)
```bash
# Full parameter control + music database
$env:DISCOGS_ACCESS_TOKEN="your_token_here"
.\run-server.bat <server-name> <server-ip> <server-port> [directory-ip] [directory-port] [ttl-seconds]
```

Examples:
```bash
# Standard music server
$env:DISCOGS_ACCESS_TOKEN="your_token_here"
.\run-server.bat myserver.group1.pro2x 192.168.1.100 9090

# Advanced configuration
$env:DISCOGS_ACCESS_TOKEN="your_token_here"  
.\run-server.bat musicserver.group2.pro2y 127.0.0.1 9091 localhost 8080 600
```

#### Client with Music Search (Advanced)
```bash
.\run-client.bat <server-name> [directory-ip] [directory-port] [mode]
```

Modes:
- `lookup`: Just resolve server name to IP:port (default)
- `connect`: Test connection to server
- `music <artist>`: Search for vinyl records ⭐ *NEW*
- `message <text>`: Send a single message  
- `interactive`: Start interactive chat session

Examples:
```bash
# Music search through client
.\run-client.bat myserver.group1.pro2x localhost 8081 music "The Beatles"

# Interactive session with music server
.\run-client.bat myserver.group1.pro2x localhost 8081 interactive

# Single message to music server
.\run-client.bat myserver.group1.pro2x localhost 8081 message "search:Pink Floyd"
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

## 🧪 Testing Scenarios

### 🎵 Music Search Test (Recommended)
1. `$env:DISCOGS_ACCESS_TOKEN="your_token_here"; .\run-directory.bat` - Start directory server
2. `$env:DISCOGS_ACCESS_TOKEN="your_token_here"; .\run-server.bat` - Start music-enabled server  
3. `$env:DISCOGS_ACCESS_TOKEN="your_token_here"; java -cp "build" com.vinylsystem.common.VinylSystemDemo` - Demo searches

Expected result: Real vinyl records for Beatles, Bob Dylan, Miles Davis, Nirvana, and Queen

### 🎸 Direct API Test
```bash
$env:DISCOGS_ACCESS_TOKEN="your_token_here"
java -cp "build" com.vinylsystem.common.DiscogsApiTest
```
Expected result: Shows 2,416+ Pink Floyd and 657+ Led Zeppelin releases

### 🔌 Basic Integration Test  
1. Start directory server: `.\run-directory.bat`
2. Start vinyl server: `.\run-server.bat testserver.group1.pro2x`
3. Test lookup: `.\run-client.bat testserver.group1.pro2x`
4. Test connection: `.\run-client.bat testserver.group1.pro2x connect`

### ⏰ TTL Expiration Test
1. Start directory: `.\run-directory.bat`
2. Start server with short TTL: `.\run-server.bat testserver.group1.pro2x 127.0.0.1 9090 localhost 8080 30`
3. Stop vinyl server (Ctrl+C)
4. Wait 60 seconds for cleanup
5. Test lookup: `.\run-client.bat testserver.group1.pro2x` (should fail with "not found")

### 🎛️ Multiple Music Servers Test
1. Start directory: `.\run-directory.bat`
2. Start server 1: `$env:DISCOGS_ACCESS_TOKEN="token"; .\run-server.bat rock.group1.pro2x`
3. Start server 2: `$env:DISCOGS_ACCESS_TOKEN="token"; .\run-server.bat jazz.group1.pro2y` (in new terminal)
4. Test both: Search "Pink Floyd" via rock server, "Miles Davis" via jazz server

### 🚨 Error Handling Test
1. Try starting server without API token (should show error)
2. Try invalid API token (should show authentication error)
3. Try looking up non-existent server
4. Try searching with malformed query
5. Verify appropriate error messages

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

## ⚙️ Configuration

### 🌐 API Settings
- **Discogs API**: Personal Access Token required
- **Rate Limiting**: Built-in request throttling
- **Authentication**: Token-based (no OAuth complexity)
- **Database**: 14+ million vinyl records

### 🔌 Network Settings
- Directory TCP: 8080
- Directory UDP: 8081
- Vinyl Server: 9090 (default)
- Socket Timeout: 5 seconds
- Max Message Size: 1024 bytes

### ⏱️ TTL Settings
- Default TTL: 300 seconds (5 minutes)
- Cleanup Interval: 30 seconds
- Refresh Interval: 60% of TTL

### 📂 Config File Format
```properties
# config.properties
discogs.access.token=your_personal_access_token_here
discogs.consumer.key=optional_consumer_key
```

## 🔧 Troubleshooting

### 🎵 Music-Specific Issues

1. **"Discogs access token is required"**
   - Set `$env:DISCOGS_ACCESS_TOKEN="your_token"`  
   - Or add token to `config.properties` file
   - Get token from [Discogs Developer Settings](https://www.discogs.com/settings/developers)

2. **"API authentication failed"**
   - Verify token is correct and active
   - Check internet connection
   - Token format: long alphanumeric string

3. **"No results found"**
   - Try different search terms
   - Check artist/album spelling
   - Some obscure releases may not be in database

### 🔌 Network Issues

1. **"Port already in use"**
   - Run `.\stop-all.bat` to clear ports
   - Change ports in command line arguments
   - Kill existing Java processes

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

### 🐛 Debug Mode
Add environment variable for verbose output:
```bash
$env:DEBUG="true"
java -cp "build" com.vinylsystem.common.VinylSystemDemo
```

## 📁 Project Structure
```
VinylSystem/
├── 📄 compile.bat               # Build script (Java 19)
├── 🎵 run-directory.bat         # Start directory server
├── 🎸 run-server.bat           # Start music-enabled vinyl server  
├── 🎧 run-client.bat           # Start client with music search
├── 🛑 stop-all.bat             # Stop all components (quick)
├── 🔄 shutdown.bat             # Gentle shutdown guide
├── 📖 README.md                # This documentation
├── ⚙️ config.properties        # API configuration
├── 📁 build/                   # Compiled classes (generated)
├── 📚 common/                  # Music APIs and shared libraries
│   └── src/main/java/com/vinylsystem/common/
│       ├── 🎵 DiscogsApiService.java     # Music database API
│       ├── 🌐 HttpClient.java            # Custom HTTP client  
│       ├── 💿 MusicRelease.java          # Vinyl record data model
│       ├── ⚙️ ConfigManager.java         # API credentials
│       ├── 🎮 VinylSystemDemo.java       # Music search demo
│       └── 📋 [Protocol classes...]       # Message handling
├── 📁 directory/               # Directory server implementation
│   └── src/main/java/com/vinylsystem/directory/
├── 📁 server/                  # Music-enabled vinyl server
│   └── src/main/java/com/vinylsystem/server/
└── 📁 client/                  # Client with music search
    └── src/main/java/com/vinylsystem/client/
```

## 🔗 Dependencies
- **Java 19**: Latest LTS runtime (upgraded from Java 11)
- **Discogs API**: Personal Access Token (free registration)
- **No external libraries**: Pure Java implementation with custom JSON/HTTP handling
- **Zero Maven dependencies**: Lightweight, self-contained system

## 🎉 What's New in This Version

### ✨ Major Enhancements
- ⬆️ **Java 11 → Java 19 Upgrade**: Modern runtime performance
- 🎵 **Discogs API Integration**: 14+ million real vinyl records  
- 🌐 **Custom HTTP Client**: No external dependencies
- 💿 **Music Data Models**: Complete vinyl record information
- 🔍 **Live Music Search**: Real-time database queries
- 🎯 **Multi-Genre Support**: Rock, Jazz, Classical, Hip-Hop, Electronic

### 🛠️ Technical Improvements  
- 📊 **Custom JSON Parser**: Lightweight, no external libs
- 🔐 **Secure Authentication**: Personal Access Token
- ⚡ **Performance Optimized**: Java 19 runtime benefits
- 🎨 **Enhanced User Experience**: Rich search results display
- 🔄 **Backward Compatible**: All original features preserved

---

## 🎸 Ready to Rock!

Your VinylSystem is now a **complete music discovery platform** with:
- ✅ **Java 19 Runtime** 
- ✅ **Live Music Database** (14+ million records)
- ✅ **Real Vinyl Search**
- ✅ **Distributed Architecture** 
- ✅ **Zero External Dependencies**

**Start searching for music now!** 🎵