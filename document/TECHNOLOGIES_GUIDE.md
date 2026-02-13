# Technologies & Dependencies Reference Guide

## Complete Technology Stack for databridge-config-service

---

## 1. Core Framework

### Java 21 LTS
**Purpose:** Latest long-term support Java release
**Usage in Project:**
- Language version for all source code
- Supports records, sealed classes, pattern matching
- VM options: `-Dspring.profiles.active=local`
- Maven compiler: `<release>${java.version}</release>`

**Key Features Used:**
- Records (future use for immutable DTOs)
- Pattern matching enhancements
- Virtual threads capability
- Modern stream APIs

**Dependency Reference:**
```xml
<property>
  <name>java.version</name>
  <value>21</value>
</property>
```

---

### Spring Boot 3.2.6
**Purpose:** Rapid development framework for production-grade applications
**Usage in Project:**
- Auto-configuration for all Spring components
- Embedded Tomcat server (runs on port 8080)
- Dependency management via Maven parent POM
- Application bootstrap in `DatabridgeConfigServiceApplication.java`

**Key Features Used:**
- Auto-configuration
- Embedded application server
- Actuator for operational monitoring
- Starter dependencies for rapid setup

**Configuration:**
```yaml
spring:
  application:
    name: databridge-config-service
  profiles:
    active: local
```

**Run Command:**
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=local
java -jar target/databridge-config-service-0.1.0-SNAPSHOT.jar --spring.profiles.active=local
```

---

### Spring Cloud 2023.0.5
**Purpose:** Distributed systems and microservices support
**Usage in Project:**
- Provides OpenFeign for inter-service communication
- BOM (Bill of Materials) for version management
- Ready for service discovery (future)
- Config server support (future)

**Key Features Available:**
- Spring Cloud OpenFeign (implemented)
- Service discovery (Eureka placeholder)
- Config management (placeholder)
- Circuit breakers (Resilience4j ready)

**Dependency Management:**
```xml
<dependencyManagement>
  <dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-dependencies</artifactId>
    <version>${spring-cloud.version}</version>
    <type>pom</type>
    <scope>import</scope>
  </dependency>
</dependencyManagement>
```

---

### Maven 3.8+
**Purpose:** Build automation and dependency management
**Usage in Project:**
- Defined in `pom.xml`
- Dependency management and version control
- Build lifecycle: clean → validate → compile → test → package → deploy
- Plugin management for code quality tools

**Key Plugins Used:**
- `maven-compiler-plugin` — Compiles Java code to Java 21 target
- `spring-boot-maven-plugin` — Builds executable JAR
- `checkstyle` — Code style validation
- `spotbugs` — Bug detection

**Common Commands:**
```bash
mvn clean package -DskipTests           # Build without tests
mvn clean package                       # Full build with tests
mvn dependency:resolve                  # Download all dependencies
mvn clean install                       # Install locally
mvn clean verify                        # Run full verification pipeline
mvn spring-boot:run -D...              # Run application
```

---

## 2. Web & API Layer

### Spring Web
**Purpose:** RESTful web application development
**Usage in Project:**
- `@RestController` on `ConfigController`
- Request/response mapping: `@GetMapping`, `@PostMapping`, `@RequestMapping`
- Content type: `MediaType.APPLICATION_JSON_VALUE`
- `ResponseEntity<T>` for HTTP responses

**Key Annotations Used:**
```java
@RestController
@RequestMapping(value = "/api/v1/configs", produces = MediaType.APPLICATION_JSON_VALUE)
public class ConfigController {
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse<JobConfigDto>> create(@Valid @RequestBody JobConfigDto dto)
}
```

**Configuration:**
```yaml
server:
  port: 8080
spring:
  mvc:
    view:
      prefix: /api
    content-negotiation:
      favor-parameter: true
```

---

### Spring Validation (Jakarta Bean Validation)
**Purpose:** Input validation framework
**Usage in Project:**
- Validates DTOs with `@Valid` annotation
- Prevents invalid data at API boundaries
- Prevents SQL injection and data corruption

**Validation Annotations Used:**
```java
@NotNull(message = "Name cannot be null")
@NotBlank(message = "Name cannot be blank")
@Size(min = 1, max = 255)
@Email
@Pattern(regexp = "...")
```

**Exception Handling:**
```java
@ExceptionHandler(MethodArgumentNotValidException.class)
public ResponseEntity<ApiError> handleValidationException(MethodArgumentNotValidException ex)
```

**Configuration:**
```yaml
spring:
  validation:
    enabled: true
```

---

### Jackson (JSON Processing)
**Purpose:** JSON serialization/deserialization
**Usage in Project:**
- Automatic via Spring Web dependency
- Serializes `JobConfigDto` to JSON
- Deserializes JSON to `JobConfigDto`
- ObjectMapper for custom conversions

**Key Features:**
- Handles `@JsonProperty` annotations
- Date/time serialization to ISO8601
- Null handling strategies
- Custom serializers (future)

**Included via:**
```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-web</artifactId>
  <!-- Includes jackson-databind, jackson-core -->
</dependency>
```

---

### Spring Actuator
**Purpose:** Application monitoring and management endpoints
**Usage in Project:**
- Health checks: `/actuator/health`
- Application info: `/actuator/info`
- Metrics: `/actuator/metrics`
- Used by load balancers for health checks

**Available Endpoints:**
```
GET /actuator/health              # Shows app status
GET /actuator/info                # Shows app metadata
GET /actuator/metrics             # Lists available metrics
GET /actuator/metrics/{metric}    # Get specific metric
GET /actuator/loggers             # View/modify log levels (prod)
```

**Configuration:**
```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,loggers
  endpoint:
    health:
      show-details: when-authorized
```

**Usage in Code:**
```java
curl http://localhost:8080/actuator/health
# Returns: {"status":"UP","components":{"db":{"status":"UP"}}}
```

---

## 3. Data Persistence Layer

### Spring Data JPA
**Purpose:** Object-Relational Mapping abstraction
**Usage in Project:**
- `JobConfigRepository` extends `JpaRepository<JobConfig, UUID>`
- Automatic CRUD operations
- Custom query methods with `@Query`
- Pagination/sorting support via `Pageable`

**Key Features Used:**
```java
public interface JobConfigRepository extends JpaRepository<JobConfig, UUID> {
    Optional<JobConfig> findByName(String name);
    List<JobConfig> findByEnabledTrue();
    // Custom queries possible with @Query
}
```

**Configuration:**
```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: update  # local/dev: update, prod: validate
    database-platform: org.hibernate.dialect.PostgreSQLDialect
    properties:
      hibernate:
        format_sql: true
```

**Dependency:**
```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
```

---

### Hibernate 6.x
**Purpose:** JPA persistence provider
**Usage in Project:**
- ORM framework for entity mapping
- Automatic SQL generation
- Transaction management
- Connection pooling (HikariCP)

**Entity Example:**
```java
@Entity
@Table(name = "job_configs")
@EntityListeners(AuditingEntityListener.class)
public class JobConfig extends AuditableBase {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(unique = true, nullable = false)
    private String name;
}
```

**Features Used:**
- `@Entity` — Entity mapping
- `@Table` — Custom table name
- `@Column` — Column constraints
- `@GeneratedValue` — Auto-generation strategy
- Cascade operations
- Relationships (future)

**Configuration:**
```yaml
spring:
  jpa:
    hibernate:
      dialect: org.hibernate.dialect.PostgreSQLDialect
```

---

### PostgreSQL Driver
**Purpose:** JDBC driver for PostgreSQL connectivity
**Dependency:**
```xml
<dependency>
  <groupId>org.postgresql</groupId>
  <artifactId>postgresql</artifactId>
  <scope>runtime</scope>
</dependency>
```

**Connection String:**
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/databridge_local
    username: dbuser
    password: dbpass
    driver-class-name: org.postgresql.Driver
```

**Docker Setup:**
```bash
docker run --name postgres-databridge \
  -e POSTGRES_DB=databridge_local \
  -e POSTGRES_USER=dbuser \
  -e POSTGRES_PASSWORD=dbpass \
  -p 5432:5432 \
  -d postgres:15-alpine
```

---

### Spring Data Auditing
**Purpose:** Automatic audit field tracking
**Usage in Project:**
- Implemented in `AuditableBase` class
- Tracks: `createdBy`, `createdAt`, `updatedBy`, `updatedAt`
- Configured in `JpaAuditingConfig.java`

**Entity Auditing Fields:**
```java
@CreatedBy
@Column(name = "created_by", updatable = false)
private String createdBy;

@CreatedDate
@Column(name = "created_at", updatable = false)
private Instant createdAt;

@LastModifiedBy
@Column(name = "updated_by")
private String updatedBy;

@LastModifiedDate
@Column(name = "updated_at")
private Instant updatedAt;
```

**Configuration:**
```java
@Configuration
@EnableJpaAuditing
public class JpaAuditingConfig {
    @Bean
    public AuditorAware<String> auditorAware() {
        return () -> Optional.of("system"); // Get from SecurityContext in production
    }
}
```

---

## 4. Cloud & AWS Integration

### AWS SDK v2 (2.20.0)
**Purpose:** AWS service integration
**Features:**
- Modern async-first design
- Non-blocking operations
- Better performance than v1
- Includes S3, SQS, SNS, Secrets Manager

**BOM Import:**
```xml
<dependency>
  <groupId>software.amazon.awssdk</groupId>
  <artifactId>bom</artifactId>
  <version>2.20.0</version>
  <type>pom</type>
  <scope>import</scope>
</dependency>
```

---

### AWS S3 (Simple Storage Service)
**Purpose:** Object storage
**Dependency:**
```xml
<dependency>
  <groupId>software.amazon.awssdk</groupId>
  <artifactId>s3</artifactId>
</dependency>
```

**Usage Example (future implementation):**
```java
S3Client s3Client = S3Client.builder()
    .region(Region.US_EAST_1)
    .endpointOverride(URI.create("http://localhost:4566"))
    .build();

s3Client.putObject(PutObjectRequest.builder()
    .bucket("my-bucket")
    .key("my-file")
    .build(), RequestBody.fromString("content"));
```

**Configuration:**
```yaml
aws:
  s3:
    bucket: databridge-configs
    region: us-east-1
```

---

### AWS SQS (Simple Queue Service)
**Purpose:** Message queue for async processing
**Dependency:**
```xml
<dependency>
  <groupId>software.amazon.awssdk</groupId>
  <artifactId>sqs</artifactId>
</dependency>
```

**Usage Example (future implementation):**
```java
SqsClient sqsClient = SqsClient.builder()
    .region(Region.US_EAST_1)
    .build();

SendMessageResponse result = sqsClient.sendMessage(SendMessageRequest.builder()
    .queueUrl(queueUrl)
    .messageBody(jsonMessage)
    .build());
```

**Configuration:**
```yaml
aws:
  sqs:
    queue-url: https://sqs.us-east-1.amazonaws.com/123456789/my-queue
```

---

### AWS SNS (Simple Notification Service)
**Purpose:** Event publishing and notifications
**Dependency:**
```xml
<dependency>
  <groupId>software.amazon.awssdk</groupId>
  <artifactId>sns</artifactId>
</dependency>
```

**Usage Example (future implementation):**
```java
SnsClient snsClient = SnsClient.builder()
    .region(Region.US_EAST_1)
    .build();

PublishResponse result = snsClient.publish(PublishRequest.builder()
    .topicArn(topicArn)
    .message(jsonMessage)
    .subject("Job Config Updated")
    .build());
```

**Configuration:**
```yaml
aws:
  sns:
    topic-arn: arn:aws:sns:us-east-1:123456789:config-updates
```

---

### AWS Secrets Manager
**Purpose:** Secure secrets retrieval
**Dependency:**
```xml
<dependency>
  <groupId>software.amazon.awssdk</groupId>
  <artifactId>secretsmanager</artifactId>
</dependency>
```

**Usage in Production:**
```java
SecretsManagerClient client = SecretsManagerClient.builder()
    .region(Region.US_EAST_1)
    .build();

GetSecretValueResponse response = client.getSecretValue(
    GetSecretValueRequest.builder()
        .secretId("databridge/db-password")
        .build());

String secret = response.secretString();
```

**Configuration (Production):**
```yaml
aws:
  region: ${AWS_REGION:us-east-1}
  secretsManager:
    secretName: ${AWS_SECRET_NAME:databridge/config}
```

**Usage:**
```bash
# Set environment variables
export AWS_REGION=us-east-1
export AWS_SECRET_NAME=databridge/config
java -jar app.jar --spring.profiles.active=prod
```

---

### Spring Cloud OpenFeign
**Purpose:** Declarative HTTP client for microservice communication
**Dependency:**
```xml
<dependency>
  <groupId>org.springframework.cloud</groupId>
  <artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>
```

**Usage Example (future implementation):**
```java
@FeignClient(name = "user-service", url = "http://user-service:8080")
public interface UserServiceClient {
    @GetMapping("/api/v1/users/{id}")
    UserDto getUserById(@PathVariable Long id);
}

// In service layer:
@Service
public class ConfigService {
    @Autowired
    private UserServiceClient userClient;
    
    public void createConfig(JobConfigDto dto) {
        UserDto user = userClient.getUserById(1L);
        // Process...
    }
}
```

**Configuration:**
```yaml
feign:
  client:
    config:
      default:
        connectTimeout: 5000
        readTimeout: 5000
```

---

## 5. Utilities & Code Generation

### Lombok
**Purpose:** Boilerplate code reduction
**Dependency:**
```xml
<dependency>
  <groupId>org.projectlombok</groupId>
  <artifactId>lombok</artifactId>
  <optional>true</optional>
</dependency>
```

**Annotations Used:**
```java
@Data                    // @Getter, @Setter, @ToString, @EqualsAndHashCode, @RequiredArgsConstructor
@Getter                  // Generate getters
@Setter                  // Generate setters
@NoArgsConstructor       // Generate no-arg constructor
@AllArgsConstructor      // Generate all-arg constructor
@RequiredArgsConstructor // Generate constructor for final fields
@Builder                 // Builder pattern
@Slf4j                   // SLF4J logger: private static final Logger log = LoggerFactory.getLogger(...)
@ToString                // toString() method
@EqualsAndHashCode       // equals() and hashCode() methods
```

**Example Usage in Project:**
```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobConfigDto {
    private UUID id;
    private String name;
    private boolean enabled;
    private Instant createdAt;
    private String createdBy;
}
```

---

### SLF4J + Logback
**Purpose:** Structured logging framework
**Dependency:**
```xml
<!-- Included via spring-boot-starter-web -->
<!-- No explicit dependency needed -->
```

**Logger Declaration:**
```java
// Without Lombok:
private static final Logger log = LoggerFactory.getLogger(ConfigController.class);

// With Lombok:
@Slf4j
public class ConfigController {
    // Use: log.info("message"), log.error("error", exception)
}
```

**Logging Levels:**
```
TRACE < DEBUG < INFO < WARN < ERROR < FATAL
```

**Usage in Code:**
```java
@Slf4j
@RestController
public class ConfigController {
    
    @PostMapping
    public ResponseEntity<BaseResponse<JobConfigDto>> create(@Valid @RequestBody JobConfigDto dto) {
        log.info("Creating new config: {}", dto.getName());
        JobConfigDto created = configService.create(dto);
        log.debug("Config created with ID: {}", created.getId());
        return ResponseEntity.ok(new BaseResponse<>(true, created, null));
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleError(Exception ex) {
        log.error("Error processing request", ex);
        return ResponseEntity.status(500).body(new ApiError(...));
    }
}
```

**Configuration:**
```yaml
logging:
  level:
    root: INFO
    com.databridge: DEBUG
    org.springframework: WARN
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"
    file: "%d{ISO8601} [%X{X-Correlation-Id}] %-5level %logger{36} - %msg%n"
  file:
    name: logs/application.log
```

**LogBack Configuration (logback-spring.xml):**
```xml
<configuration>
  <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
    <pattern>${LOG_PATTERN:-${LOG_CONSOLE_PATTERN}}</pattern>
  </appender>
  <root level="INFO">
    <appender-ref ref="CONSOLE" />
  </root>
</configuration>
```

---

## 6. Code Quality & Static Analysis

### Checkstyle
**Purpose:** Code style enforcement
**Configuration File:** `config/checkstyle.xml`
**Maven Command:**
```bash
mvn checkstyle:check
```

**Key Rules:**
- Line length max: 120 characters
- Import ordering
- Naming conventions (camelCase, CONSTANT_CASE)
- JavaDoc requirements
- Whitespace rules

---

### SpotBugs
**Purpose:** Bug detection and analysis
**Configuration File:** `config/spotbugs-exclude.xml`
**Maven Command:**
```bash
mvn spotbugs:check
```

**Detects:**
- Null pointer dereferences
- Infinite loops
- Race conditions
- Incorrect exception handling
- SQL injection vulnerabilities

---

### SonarQube
**Purpose:** Comprehensive code quality metrics
**Configuration File:** `config/sonar-project.properties`
**Maven Command:**
```bash
mvn sonar:sonar -Dsonar.projectKey=databridge-config-service
```

**Metrics Tracked:**
- Code coverage
- Code duplicates
- Code smells
- Bugs
- Security vulnerabilities
- Maintainability index

---

## 7. Testing Framework

### Spring Boot Test
**Dependency:**
```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-test</artifactId>
  <scope>test</scope>
</dependency>
```

**Includes:**
- JUnit 5
- Mockito
- AssertJ
- Hamcrest

**Example Test:**
```java
@SpringBootTest
@AutoConfigureMockMvc
class ConfigControllerTest {
    
    @MockBean
    private ConfigService configService;
    
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    void testCreateConfig() throws Exception {
        JobConfigDto dto = new JobConfigDto();
        when(configService.create(any())).thenReturn(dto);
        
        mockMvc.perform(post("/api/v1/configs")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isOk());
    }
}
```

---

### Testcontainers (Future Implementation)
**Dependency:**
```xml
<dependency>
  <groupId>org.testcontainers</groupId>
  <artifactId>testcontainers</artifactId>
  <version>1.19.0</version>
  <scope>test</scope>
</dependency>
<dependency>
  <groupId>org.testcontainers</groupId>
  <artifactId>postgresql</artifactId>
  <version>1.19.0</version>
  <scope>test</scope>
</dependency>
```

**Usage Example:**
```java
@Testcontainers
@SpringBootTest
class ConfigRepositoryIntegrationTest {
    
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
        .withDatabaseName("testdb")
        .withUsername("test")
        .withPassword("test");
    
    @Test
    void testFindByName() {
        // Test with real PostgreSQL container
    }
}
```

---

## Complete Dependency Tree

```
databridge-config-service
├── Spring Boot 3.2.6 (Parent)
│   ├── Spring Framework 6.x
│   ├── Tomcat 10.x
│   ├── Jackson 2.x
│   └── Logback
├── Spring Web
├── Spring Data JPA
│   └── Hibernate 6.x
├── Spring Validation
├── Spring Actuator
├── Spring Cloud 2023.0.5
│   └── Spring Cloud OpenFeign
├── AWS SDK v2 (2.20.0)
│   ├── S3
│   ├── SQS
│   ├── SNS
│   └── Secrets Manager
├── PostgreSQL Driver
├── Lombok
├── SLF4J + Logback
├── Spring Boot Test
│   ├── JUnit 5
│   ├── Mockito
│   ├── AssertJ
│   └── Hamcrest
└── Code Quality Tools
    ├── Checkstyle
    ├── SpotBugs
    └── SonarQube
```

---

## Version Management Strategy

### BOM (Bill of Materials) Used
1. **Spring Boot Parent** — Manages all Spring and common library versions
2. **Spring Cloud** — Manages Spring Cloud dependencies
3. **AWS SDK BOM** — Manages AWS SDK module versions

### Locked Versions
```xml
<spring.boot.version>3.2.6</spring.boot.version>
<spring-cloud.version>2023.0.5</spring-cloud.version>
<java.version>21</java.version>
```

### Dependency Update Strategy
```bash
mvn dependency:update-properties  # Check for updates
mvn versions:display-dependency-updates  # Show available versions
```

---

**Last Updated:** February 14, 2026
**Status:** Complete
**Version:** 1.0

