# urlShortner
url shortner project using java,springboot and thymleaf,with jwt based authentication

LOGIN-API
Features
Anonymous Users

Create Short URLs: Convert long URLs into short, shareable links
Automatic Code Generation: 4-character random codes using Base62 encoding
URL Validation: Comprehensive validation for HTTP/HTTPS URLs
Instant Redirection: Fast redirection to original URLs

Authenticated Users (JWT-based)

Custom Short Codes: Create personalized short codes (3-20 characters)
Code Validation: Alphanumeric characters, hyphens, and underscores allowed
Reserved Word Protection: Prevents conflicts with system paths
Token-based Authentication: Secure JWT implementation



Build the application
bashmvn clean install

Run the application
bashmvn spring-boot:run

Access the application

Open your browser and navigate to
API's
for  use login
http://localhost:4444/auth/login
for annonymous generation http://localhost:4444/


Testing
Run All Tests
bash mvn test
Test Coverage

Controller Tests: 10 comprehensive test cases covering all endpoints
Service Layer: Business logic validation
Exception Handling: Error scenarios and edge cases
Security: Authentication and authorization flows

Test Scenarios Covered

 Anonymous URL shortening
 Authenticated user custom codes
 Invalid URL handling
 Duplicate URL management
 Short code validation
 Redirection functionality
 Error responses
Token-based authentication
Custom code conflicts
Reserved word protection

originalUrl (required): The URL to shorten
token (optional): JWT token for authenticated users
customCode (optional): Custom short code (requires authentication)


📊 Technical Specifications
URL Validation

Supported Protocols: HTTP, HTTPS
Maximum Length: 2048 characters
Hostname Validation: Valid domain format required
Malformed URL Detection: Comprehensive URL parsing

Short Code Generation

Default Length: 4 characters
Custom Code Length: 3-20 characters

Storage

In-Memory: ConcurrentHashMap for thread-safe operations
Bidirectional Mapping: Original URL ↔ Short Code

Application Properties

server.port=4444

























