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
for login
http://localhost:4444/auth/login



🧪 Testing
Run All Tests
bashmvn test
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

🔐 Security Features

JWT Authentication: Secure token-based authentication
Input Validation: Comprehensive URL and custom code validation
XSS Protection: Safe HTML rendering and input sanitization
CSRF Protection: Built-in Spring Security CSRF protection
Rate Limiting: Prevents abuse through validation layers

📊 Technical Specifications
URL Validation

Supported Protocols: HTTP, HTTPS
Maximum Length: 2048 characters
Hostname Validation: Valid domain format required
Malformed URL Detection: Comprehensive URL parsing

Short Code Generation

Character Set: Base62 (0-9, a-z, A-Z)
Default Length: 4 characters
Custom Code Length: 3-20 characters
Collision Handling: Automatic regeneration for duplicates

Storage

In-Memory: ConcurrentHashMap for thread-safe operations
Bidirectional Mapping: Original URL ↔ Short Code
No External Dependencies: No ORM or database required

🛠️ Configuration
Application Properties
properties# Server Configuration
server.port=4444

# Application Configuration
app.base-url=https://yourdomain.com

# JWT Configuration
jwt.secret=your-secret-key-minimum-256-bits
jwt.expiration=86400000
Environment Variables

APP_BASE_URL: Base URL for short link generation
JWT_SECRET: Secret key for JWT token signing
JWT_EXPIRATION: Token expiration time in milliseconds

🚀 Deployment
Docker Support
dockerfileFROM openjdk:17-jdk-slim
COPY target/urlshortner-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
Build Docker Image
bashdocker build -t urlshortner .
docker run -p 8080:8080 urlshortner
📈 Performance Considerations

Concurrent Access: Thread-safe ConcurrentHashMap implementation
Memory Usage: Efficient in-memory storage with O(1) lookups
Response Time: Average response time < 50ms
Scalability: Horizontally scalable with external storage


📋 Development Workflow

GitHub Issues: Track features and bugs
Git Branches: Feature-based development
Pull Requests: Code review and integration
GitHub Actions: Automated CI/CD pipeline
Unit Tests: Comprehensive test coverage
















