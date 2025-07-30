# Web Crawler API

A Java-based web crawling service built with the Spark Java framework that enables developers to programmatically crawl websites and search for specific keywords. This application provides a simple RESTful API to initiate crawl jobs and retrieve results, making it ideal for lightweight crawling use cases or prototyping search capabilities.

## ✨ Key Features

- **Asynchronous Web Crawling**: Start crawl jobs that run in the background
- **Keyword Search**: Search for specific keywords within crawled pages
- **RESTful API**: Simple HTTP endpoints for managing crawl operations
- **Docker Support**: Containerized deployment with Docker
- **Thread Pool Management**: Efficient handling of multiple concurrent crawl jobs
- **Comprehensive Error Handling**: Proper exception handling and validation
- **Logging**: Detailed logging for monitoring and debugging

## 🚀 Technology Stack

- **Java 14**: Core programming language
- **Spark Java**: Lightweight web framework
- **Maven**: Build tool and dependency management
- **JUnit 5**: Unit testing framework
- **Mockito**: Mocking framework for tests
- **Docker**: Containerization

## 📂 Project Structure

```
src/
├── main/java/com/webcrawler/backend/
│   ├── config/
│   │   └── GlobalExceptionHandler.java
│   ├── controller/crawl/
│   │   └── CrawlController.java
│   ├── model/crawl/
│   │   ├── Crawl.java
│   │   ├── CrawlStatus.java
│   │   ├── request/
│   │   │   └── CrawlRequest.java
│   │   └── response/
│   │       ├── CrawlResponse.java
│   │       └── SimplifiedCrawlResponse.java
│   ├── service/crawl/
│   │   ├── CrawlRunner.java
│   │   └── CrawlService.java
│   ├── utils/
│   │   ├── exceptions/
│   │   ├── IdGenerator.java
│   │   ├── LinkExtractor.java
│   │   └── PageFetcher.java
│   └── Main.java
└── test/java/com/webcrawler/backend/
    ├── controller/crawl/
    ├── service/crawl/
    └── utils/
```

## 🌐 API Endpoints

### Start a Crawl Job

**POST** `/crawl`

Initiates a new web crawling job with the specified keyword.

**Request Body:**
```json
{
  "keyword": "your-search-keyword"
}
```

**Response:**
```json
{
  "id": "generated-crawl-id"
}
```

**Constraints:**
- Keyword must be between 4 and 32 characters
- Keyword cannot be null or empty

### Get Crawl Results

**GET** `/crawl/{id}`

Retrieves the results of a specific crawl job.

**Response:**
```json
{
  "id": "crawl-id",
  "status": "active|done",
  "urls": [
    "http://example.com/page1",
    "http://example.com/page2"
  ]
}
```

## 🔑 Environment Variables

- `BASE_URL`: The base URL to start crawling from (required)

> 💡 You can add a environment variable by running:
> ```bash
> export BASE_URL="https://example.com"
> ```

## 🛠️ Getting Started

### Prerequisites

- Java 14 or higher
- Maven 3.6+
- Docker (optional)

### Running Locally

1. **Clone the repository**
   ```bash
   git clone https://github.com/jhonatademuner/spark-webcrawler.git
   cd spark-webcrawler
   ```

2. **Build the project**
   ```bash
   mvn clean compile
   ```

3. **Run the application (assuming that you already have the environment variable)**
   ```bash
   mvn exec:java
   ```

The application will start on `http://localhost:4567` by default.

### Running with Docker

1. **Build the Docker image**
   ```bash
   docker build -t spark-webcrawler .
   ```

2. **Run the container**
   ```bash
   docker run -p 4567:4567 -e BASE_URL="https://example.com" spark-webcrawler
   ```

## Testing

Run the test suite:

```bash
mvn test
```

The project includes comprehensive unit tests for:
- Controllers
- Services
- Utility classes

## Error Handling

The application provides proper error handling for various scenarios:

- **400 Bad Request**: Invalid input parameters
- **404 Not Found**: Crawl job not found
- **500 Internal Server Error**: System overload or unexpected errors

## Performance Considerations

- **Thread Pool**: Configurable thread pool for managing concurrent crawl jobs
- **Bounded Queue**: Prevents memory overflow with large numbers of pending jobs
- **Timeout Handling**: Proper timeout management for web requests
- **Resource Management**: Efficient cleanup of completed jobs

## Contributing

Contributions are welcome to improve the Web Crawler API. Here's how you can contribute:

### Development Setup

1. **Fork the repository**
2. **Clone your fork**
   ```bash
   git clone https://github.com/jhonatademuner/spark-webcrawler.git
   cd webcrawler
   ```
3. **Create a feature branch**
   ```bash
   git checkout -b feature/your-feature-name
   ```
4. **Make your changes**
5. **Run tests**
   ```bash
   mvn test
   ```
6. **Commit your changes**
   ```bash
   git commit -m "Add your feature description"
   ```
7. **Push to your fork**
   ```bash
   git push origin feature/your-feature-name
   ```
8. **Open a Pull Request**

### Contribution Guidelines

- **Code Style**: Follow Java conventions and maintain consistent formatting
- **Testing**: Add unit tests for new features and ensure all tests pass
- **Documentation**: Update README.md and add inline comments for complex logic
- **Commit Messages**: Use clear, descriptive commit messages
- **Pull Requests**: Provide a clear description of changes and their purpose

### Areas for Contribution

- Performance optimizations
- Additional API endpoints
- Enhanced error handling
- Improved documentation
- Bug fixes
- New features

## 📜 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
