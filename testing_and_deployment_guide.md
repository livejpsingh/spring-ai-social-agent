# ============================================================================
# TESTING STRATEGY & QUALITY ASSURANCE
# ============================================================================

## Unit Testing with JUnit 5 & Mockito

### ContentGenerationServiceTest

```java
package com.socialmediaagent.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ContentGenerationServiceTest {
    
    @Mock
    private ChatClient chatClient;
    
    @Mock
    private BrandGuidelineService brandGuidelineService;
    
    @Mock
    private ContentPostRepository contentRepository;
    
    @InjectMocks
    private ContentGenerationService contentGenerationService;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    
    @Test
    void testGenerateContentWithValidRequest() {
        // Arrange
        ContentGenerationRequest request = new ContentGenerationRequest();
        request.setPrompt("Create a product announcement");
        request.setTone(Tone.PROFESSIONAL);
        request.setLanguage("en");
        request.setVariantsToGenerate(3);
        
        ChatResponse mockResponse = mock(ChatResponse.class);
        when(chatClient.call(any())).thenReturn(mockResponse);
        when(mockResponse.getResult().getOutput().getContent())
            .thenReturn("VARIANT_1: Product announcement\nVARIANT_2: Another variant");
        
        when(brandGuidelineService.validateContentAgainstBrandGuide(any(), any()))
            .thenReturn(0.9);
        
        // Act
        GeneratedContentResponse response = contentGenerationService.generateContent(request);
        
        // Assert
        assertNotNull(response);
        assertFalse(response.getVariants().isEmpty());
        assertEquals(2, response.getVariants().size());
        assertEquals(0.9, response.getBrandVoiceScores().get(0));
        
        verify(chatClient, times(1)).call(any());
    }
    
    @Test
    void testGenerateContentWithDifferentTones() {
        // Test that different tones are respected
        for (Tone tone : Tone.values()) {
            ContentGenerationRequest request = new ContentGenerationRequest();
            request.setTone(tone);
            
            // Verify tone is included in prompt
            // Implementation details...
        }
    }
    
    @Test
    void testGenerateHashtagsForTwitter() {
        // Test hashtag generation
        String content = "Excited to announce new feature release!";
        List<String> hashtags = contentGenerationService.generateHashtags(
            content, 
            Set.of(SocialPlatform.TWITTER)
        );
        
        assertNotNull(hashtags);
        assertFalse(hashtags.isEmpty());
        assertTrue(hashtags.size() <= 10);
    }
    
    @Test
    void testAdaptContentForPlatformConstraints() {
        // Test content adaptation for Twitter's 280 character limit
        String content = "This is a very long content that exceeds Twitter's character limit " +
                        "and should be truncated appropriately while maintaining meaning.";
        
        String adapted = contentGenerationService.adaptContentForPlatform(
            content, 
            SocialPlatform.TWITTER
        );
        
        assertNotNull(adapted);
        assertTrue(adapted.length() <= 280);
        assertTrue(adapted.endsWith("...") || adapted.length() == 280);
    }
}
```

### SchedulingServiceTest

```java
class SchedulingServiceTest {
    
    @Mock
    private ScheduledJobRepository jobRepository;
    
    @Mock
    private ContentPostRepository postRepository;
    
    @Mock
    private TaskScheduler taskScheduler;
    
    @InjectMocks
    private SchedulingService schedulingService;
    
    @Test
    void testScheduleContentWithOptimalTime() {
        // Arrange
        Long postId = 1L;
        ContentPost post = new ContentPost();
        post.setId(postId);
        
        when(postRepository.findById(postId)).thenReturn(java.util.Optional.of(post));
        when(postRepository.findTopPerformingLast90Days(any()))
            .thenReturn(createMockPosts());
        
        SchedulingRequest request = new SchedulingRequest();
        request.setPostId(postId);
        request.setUseAIOptimization(true);
        request.setPlatforms(Set.of(SocialPlatform.TWITTER, SocialPlatform.LINKEDIN));
        
        // Act
        schedulingService.scheduleContent(postId, request);
        
        // Assert
        assertEquals(PostStatus.SCHEDULED, post.getStatus());
        assertNotNull(post.getScheduledTime());
        verify(jobRepository, times(2)).save(any(ScheduledJob.class));
        verify(taskScheduler, times(2)).schedule(any(Runnable.class), any(Instant.class));
    }
    
    @Test
    void testFindOptimalPostingTime() {
        // Arrange
        List<ContentPost> posts = createMockPostsWithMetrics();
        when(postRepository.findTopPerformingLast90Days(1L)).thenReturn(posts);
        
        // Act
        LocalDateTime optimalTime = schedulingService.findOptimalPostingTime(
            1L, 
            Set.of(SocialPlatform.TWITTER)
        );
        
        // Assert
        assertNotNull(optimalTime);
        assertTrue(optimalTime.isAfter(LocalDateTime.now()));
    }
    
    @Test
    void testExecuteJobWithRetry() {
        // Arrange
        ScheduledJob job = createMockScheduledJob();
        
        // Simulate failure
        doThrow(new RuntimeException("API Error"))
            .when(publishingService).publishPost(any(), any());
        
        // Act
        schedulingService.executeJobWithRetry(job);
        
        // Assert
        assertEquals(JobStatus.RETRY, job.getStatus());
        assertEquals(1, job.getRetryCount());
        assertNotNull(job.getNextRetryTime());
    }
    
    private List<ContentPost> createMockPostsWithMetrics() {
        List<ContentPost> posts = new ArrayList<>();
        for (int hour = 0; hour < 24; hour++) {
            ContentPost post = new ContentPost();
            post.setPublishedTime(LocalDateTime.now().withHour(hour));
            
            Map<String, Double> metrics = new HashMap<>();
            // Peak engagement at 9 AM
            metrics.put("engagement_rate", hour == 9 ? 0.08 : 0.02);
            post.setPerformanceMetrics(metrics);
            posts.add(post);
        }
        return posts;
    }
}
```

### EngagementHandlingServiceTest

```java
class EngagementHandlingServiceTest {
    
    @Mock
    private CommentEngagementRepository commentRepository;
    
    @Mock
    private ChatClient chatClient;
    
    @InjectMocks
    private EngagementHandlingService engagementService;
    
    @Test
    void testProcessCommentWithPositiveSentiment() {
        // Arrange
        CommentEngagement comment = new CommentEngagement();
        comment.setCommentText("Love this feature! Great work!");
        comment.setPlatformUserId("user123");
        comment.setPlatform(SocialPlatform.TWITTER);
        
        when(commentRepository.findByPlatformUserIdAndPlatform(any(), any()))
            .thenReturn(new ArrayList<>());
        
        // Mock sentiment analysis
        when(chatClient.call(any()))
            .thenReturn(createMockSentimentResponse(0.9, "joy", "praise"));
        
        // Act
        engagementService.processComment(comment);
        
        // Assert
        assertEquals(EngagementStatus.AUTO_REPLIED, comment.getStatus());
        assertEquals(0.9, comment.getSentimentScore());
        assertEquals("joy", comment.getEmotion());
        assertEquals("praise", comment.getIntent());
        assertNotNull(comment.getResponseText());
    }
    
    @Test
    void testProcessCommentWithNegativeSentiment() {
        // Arrange
        CommentEngagement comment = new CommentEngagement();
        comment.setCommentText("This is broken and useless!");
        comment.setPlatformUserId("user456");
        comment.setPlatform(SocialPlatform.TWITTER);
        
        // Mock negative sentiment
        when(chatClient.call(any()))
            .thenReturn(createMockSentimentResponse(-0.9, "anger", "complaint"));
        
        // Act
        engagementService.processComment(comment);
        
        // Assert
        assertEquals(EngagementStatus.ESCALATED, comment.getStatus());
        assertEquals(-0.9, comment.getSentimentScore());
    }
    
    @Test
    void testGenerateContextualResponse() {
        // Test that responses use conversation history
        CommentEngagement comment = new CommentEngagement();
        comment.setCommentText("Follow up question");
        
        Map<String, Object> context = new HashMap<>();
        context.put("history", "User: Original question\nBrand: Answer\n");
        comment.setConversationContext(context);
        
        // Response should reference context
        String response = engagementService.generateContextualResponse(comment);
        assertNotNull(response);
        assertFalse(response.isEmpty());
    }
}
```

## Integration Testing

### ContentGenerationIntegrationTest

```java
@SpringBootTest
@AutoConfigureMockMvc
class ContentGenerationIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ContentPostRepository contentRepository;
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Test
    void testGenerateContentEndToEnd() throws Exception {
        // Arrange
        ContentGenerationRequest request = new ContentGenerationRequest();
        request.setPrompt("Announce Q4 results");
        request.setTone(Tone.PROFESSIONAL);
        request.setTargetPlatforms(Set.of(SocialPlatform.LINKEDIN));
        
        // Act & Assert
        mockMvc.perform(post("/api/v1/content/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.variants").isArray())
            .andExpect(jsonPath("$.variants[0]").isNotEmpty());
    }
    
    @Test
    void testScheduleAndPublishContent() throws Exception {
        // Arrange
        ContentPost post = new ContentPost();
        post.setUserId(1L);
        post.setContentText("Test announcement");
        post.setStatus(PostStatus.DRAFT);
        contentRepository.save(post);
        
        SchedulingRequest request = new SchedulingRequest();
        request.setPostId(post.getId());
        request.setUseAIOptimization(true);
        request.setPlatforms(Set.of(SocialPlatform.TWITTER));
        
        // Act
        mockMvc.perform(post("/api/v1/content/" + post.getId() + "/schedule")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk());
        
        // Assert
        ContentPost updated = contentRepository.findById(post.getId()).orElseThrow();
        assertEquals(PostStatus.SCHEDULED, updated.getStatus());
    }
}
```

## Performance Testing

### Load Testing with JMeter

```jmeter
Test Plan
├── ThreadGroup (100 users, ramp-up 10s, loop 5 times)
│   ├── HTTP Request Sampler
│   │   ├── POST /api/v1/content/generate
│   │   ├── HTTP Request Sampler POST /api/v1/content/{id}/schedule
│   │   └── HTTP Request Sampler GET /api/v1/analytics/dashboard
│   └── Assertion - Response Time < 500ms
└── Results Tree / Summary Report
```

### Benchmark Metrics

```
Performance Targets:
- Content Generation: < 3 seconds (with AI API call)
- Scheduling: < 200ms
- Comment Processing: < 500ms
- Analytics Query: < 1 second
- Dashboard Load: < 2 seconds
- Concurrent Users: 500+
- Database Query Optimization: 95% < 100ms
```

---

# ============================================================================
# DEPLOYMENT GUIDE
# ============================================================================

## Local Development Setup

### Prerequisites
```bash
# Required
- Java 17 or higher
- Docker & Docker Compose
- Maven 3.8+
- Git

# Recommended
- PostgreSQL client tools
- Redis client (redis-cli)
- Postman or REST client
```

### Setup Steps

```bash
# 1. Clone repository
git clone https://github.com/yourusername/social-media-agent.git
cd social-media-agent

# 2. Copy environment file
cp .env.example .env
# Edit .env with your API keys

# 3. Start infrastructure (PostgreSQL, Redis, Kafka)
docker-compose up -d

# 4. Build application
mvn clean install

# 5. Run application
mvn spring-boot:run

# 6. Verify health
curl http://localhost:8080/api/actuator/health
```

### IDE Configuration (IntelliJ IDEA)

```
1. File > Open Project > select project directory
2. Configure SDK: File > Project Structure > SDK > Java 17
3. Enable Lombok: File > Settings > Plugins > Install Lombok
4. Enable Annotation Processing: 
   Settings > Build, Execution, Deployment > 
   Compiler > Annotation Processors > Enable annotation processing
5. Configure Run Configuration:
   - Main class: com.socialmediaagent.SocialMediaAgentApplication
   - VM options: -Dspring.profiles.active=dev
   - Environment: Load from .env file
```

---

## Docker Deployment

### Build Docker Image

```bash
# 1. Build JAR
mvn clean package

# 2. Build Docker image
docker build -t social-media-agent:latest .

# 3. Run container
docker run -d \
  --name social-media-agent \
  -p 8080:8080 \
  --env-file .env \
  --network app-network \
  social-media-agent:latest

# 4. View logs
docker logs -f social-media-agent
```

### Multi-Stage Build Optimization

```dockerfile
# Dockerfile.prod - Production optimized

# Stage 1: Build
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /build
COPY . .
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=builder /build/target/social-media-agent-1.0.0.jar app.jar

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
  CMD curl -f http://localhost:8080/api/actuator/health || exit 1

EXPOSE 8080
ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-jar", "app.jar"]
```

---

## Kubernetes Deployment

### K8s Manifests

```yaml
# deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: social-media-agent
  namespace: default
spec:
  replicas: 3
  strategy:
    type: RollingUpdate
    rollingUpdate:
      maxSurge: 1
      maxUnavailable: 0
  selector:
    matchLabels:
      app: social-media-agent
  template:
    metadata:
      labels:
        app: social-media-agent
    spec:
      containers:
      - name: app
        image: social-media-agent:1.0.0
        imagePullPolicy: IfNotPresent
        ports:
        - containerPort: 8080
          name: http
        env:
        - name: SPRING_DATASOURCE_URL
          valueFrom:
            configMapKeyRef:
              name: app-config
              key: database.url
        - name: OPENAI_API_KEY
          valueFrom:
            secretKeyRef:
              name: api-secrets
              key: openai-key
        resources:
          requests:
            memory: "512Mi"
            cpu: "250m"
          limits:
            memory: "1Gi"
            cpu: "1000m"
        livenessProbe:
          httpGet:
            path: /api/actuator/health
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 10
        readinessProbe:
          httpGet:
            path: /api/actuator/health/readiness
            port: 8080
          initialDelaySeconds: 20
          periodSeconds: 5

---
apiVersion: v1
kind: Service
metadata:
  name: social-media-agent-service
spec:
  type: LoadBalancer
  ports:
  - port: 80
    targetPort: 8080
  selector:
    app: social-media-agent

---
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: social-media-agent-hpa
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: social-media-agent
  minReplicas: 3
  maxReplicas: 10
  metrics:
  - type: Resource
    resource:
      name: cpu
      target:
        type: Utilization
        averageUtilization: 70
  - type: Resource
    resource:
      name: memory
      target:
        type: Utilization
        averageUtilization: 80
```

### Deployment Command

```bash
# Create namespace
kubectl create namespace social-media-agent

# Create secrets
kubectl create secret generic api-secrets \
  --from-literal=openai-key=$OPENAI_API_KEY \
  --from-literal=twitter-key=$TWITTER_API_KEY \
  -n social-media-agent

# Apply manifests
kubectl apply -f deployment.yaml -n social-media-agent

# Monitor rollout
kubectl rollout status deployment/social-media-agent -n social-media-agent

# Check pod status
kubectl get pods -n social-media-agent

# View logs
kubectl logs -f deployment/social-media-agent -n social-media-agent
```

---

## AWS ECS Deployment

```json
// task-definition.json
{
  "family": "social-media-agent",
  "networkMode": "awsvpc",
  "requiresCompatibilities": ["FARGATE"],
  "cpu": "512",
  "memory": "1024",
  "containerDefinitions": [
    {
      "name": "app",
      "image": "YOUR_ECR_REGISTRY/social-media-agent:latest",
      "portMappings": [
        {
          "containerPort": 8080,
          "protocol": "tcp"
        }
      ],
      "logConfiguration": {
        "logDriver": "awslogs",
        "options": {
          "awslogs-group": "/ecs/social-media-agent",
          "awslogs-region": "us-east-1",
          "awslogs-stream-prefix": "ecs"
        }
      },
      "environment": [
        {
          "name": "SPRING_PROFILES_ACTIVE",
          "value": "prod"
        }
      ],
      "secrets": [
        {
          "name": "OPENAI_API_KEY",
          "valueFrom": "arn:aws:secretsmanager:us-east-1:ACCOUNT:secret:openai-key"
        }
      ]
    }
  ]
}
```

---

## CI/CD Pipeline (GitHub Actions)

```yaml
# .github/workflows/deploy.yml
name: Deploy to Production

on:
  push:
    branches: [main]
  pull_request:
    branches: [main]

jobs:
  build-and-test:
    runs-on: ubuntu-latest
    
    services:
      postgres:
        image: postgres:16-alpine
        env:
          POSTGRES_DB: test
          POSTGRES_PASSWORD: password
        options: >-
          --health-cmd pg_isready
          --health-interval 10s
          --health-timeout 5s
          --health-retries 5
        ports:
          - 5432:5432
      
      redis:
        image: redis:7-alpine
        options: >-
          --health-cmd "redis-cli ping"
          --health-interval 10s
          --health-timeout 5s
          --health-retries 5
        ports:
          - 6379:6379
    
    steps:
    - uses: actions/checkout@v3
    
    - name: Set up JDK 17
      uses: actions/setup-java@v3
      with:
        java-version: '17'
        distribution: 'temurin'
        cache: maven
    
    - name: Build with Maven
      run: mvn clean install
      env:
        OPENAI_API_KEY: ${{ secrets.OPENAI_API_KEY }}
    
    - name: Run Tests
      run: mvn test
      env:
        SPRING_DATASOURCE_URL: jdbc:postgresql://localhost:5432/test
        SPRING_REDIS_HOST: localhost
    
    - name: SonarQube Scan
      run: mvn sonar:sonar -Dsonar.projectKey=social-media-agent
      env:
        SONAR_HOST_URL: ${{ secrets.SONAR_HOST_URL }}
        SONAR_LOGIN: ${{ secrets.SONAR_LOGIN }}
    
    - name: Build Docker image
      run: docker build -t social-media-agent:${{ github.sha }} .
    
    - name: Push to ECR
      run: |
        aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin $ECR_REGISTRY
        docker tag social-media-agent:${{ github.sha }} $ECR_REGISTRY/social-media-agent:latest
        docker push $ECR_REGISTRY/social-media-agent:latest
      env:
        ECR_REGISTRY: ${{ secrets.ECR_REGISTRY }}
        AWS_ACCESS_KEY_ID: ${{ secrets.AWS_ACCESS_KEY_ID }}
        AWS_SECRET_ACCESS_KEY: ${{ secrets.AWS_SECRET_ACCESS_KEY }}

  deploy:
    needs: build-and-test
    runs-on: ubuntu-latest
    if: github.ref == 'refs/heads/main'
    
    steps:
    - uses: actions/checkout@v3
    
    - name: Deploy to Kubernetes
      run: |
        kubectl set image deployment/social-media-agent \
          app=${{ secrets.ECR_REGISTRY }}/social-media-agent:${{ github.sha }} \
          -n social-media-agent
        kubectl rollout status deployment/social-media-agent -n social-media-agent
      env:
        KUBECONFIG: ${{ secrets.KUBECONFIG }}
```

---

## Monitoring & Observability

### Prometheus Metrics

```yaml
# prometheus.yml
global:
  scrape_interval: 15s

scrape_configs:
  - job_name: 'social-media-agent'
    static_configs:
      - targets: ['localhost:8080']
    metrics_path: '/api/actuator/prometheus'
```

### Grafana Dashboard

```json
// Grafana dashboard for social-media-agent showing:
// - Request rate & latency
// - Error rate
// - Database connection pool
// - Cache hit rate
// - Job execution status
// - AI API latency
```

### Alerting Rules

```yaml
groups:
  - name: social-media-agent
    rules:
    - alert: HighErrorRate
      expr: rate(http_requests_total{job="social-media-agent",status=~"5.."}[5m]) > 0.05
      for: 5m
      annotations:
        summary: "High error rate detected"
    
    - alert: DatabaseConnectionPoolExhausted
      expr: hikaricp_connections_active{job="social-media-agent"} / hikaricp_connections_max > 0.9
      for: 2m
      annotations:
        summary: "Database connection pool nearly exhausted"
    
    - alert: ScheduledJobFailures
      expr: increase(scheduled_jobs_failed_total[5m]) > 10
      for: 5m
      annotations:
        summary: "High number of scheduled job failures"
```

---

## Rollback Procedures

### Kubernetes Rollback

```bash
# Check rollout history
kubectl rollout history deployment/social-media-agent

# Rollback to previous version
kubectl rollout undo deployment/social-media-agent

# Rollback to specific revision
kubectl rollout undo deployment/social-media-agent --to-revision=2

# Monitor rollback
kubectl rollout status deployment/social-media-agent
```

### Database Migration Rollback

```bash
# Using Flyway
mvn flyway:undo

# Using Spring Data JDBC
java -jar social-media-agent.jar migrate:undo
```

---

## Runbook: Common Operations

### Scale Application

```bash
# Kubernetes
kubectl scale deployment social-media-agent --replicas=5

# Docker Compose
docker-compose up -d --scale app=5
```

### Clear Cache

```bash
# Redis
redis-cli FLUSHALL

# Application cache endpoint
curl -X POST http://localhost:8080/api/admin/cache/clear \
  -H "Authorization: Bearer $TOKEN"
```

### Reprocess Failed Jobs

```bash
# SQL Query
UPDATE scheduled_jobs 
SET status = 'PENDING', retry_count = 0 
WHERE status = 'FAILED' AND created_at > now() - interval '24 hours';
```

---

## Performance Tuning Checklist

- [ ] Enable query result caching (Redis)
- [ ] Configure connection pooling (HikariCP max: CPU*2+spare)
- [ ] Set appropriate thread pool sizes
- [ ] Enable database query optimization (EXPLAIN ANALYZE)
- [ ] Configure pagination limits
- [ ] Implement request rate limiting
- [ ] Enable compression for API responses
- [ ] Use CDN for static assets
- [ ] Monitor slow queries (log queries > 1s)
- [ ] Implement circuit breakers for external APIs

---

## Security Checklist

- [ ] Rotate API keys quarterly
- [ ] Enable HTTPS everywhere
- [ ] Implement API rate limiting per user
- [ ] Encrypt sensitive data at rest & in transit
- [ ] Regular security scanning (OWASP Top 10)
- [ ] Implement audit logging
- [ ] Use secrets management (AWS Secrets Manager, Vault)
- [ ] Regular penetration testing
- [ ] Keep dependencies updated
- [ ] Implement WAF (Web Application Firewall)

---

## Cost Optimization

### AWS Cost Reduction Strategies
- Use Reserved Instances (30-40% savings)
- Implement auto-scaling (scale down during off-peak)
- Use spot instances for batch jobs
- Optimize data transfer costs
- Implement log retention policies

### AI API Cost Management
- Cache LLM responses for common queries
- Batch API calls where possible
- Use model selection based on complexity
- Implement cost monitoring & alerting
- Regular model cost analysis

---

## SLA & Uptime Targets

| Service | Target | Measurement |
|---------|--------|-------------|
| API Availability | 99.9% | Monthly |
| Response Time (p95) | 500ms | Continuous |
| Post Publishing | 99% | Per post |
| Comment Processing | 95% | Per comment |
| Analytics Refresh | < 5 min | Real-time |
| Backup | Daily | 24-hour retention min |

