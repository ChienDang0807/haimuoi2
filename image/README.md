## Image Service (Go, Gin) - Hướng dẫn từng bước

Tài liệu này hướng dẫn triển khai một Image Service bằng Go, phù hợp tích hợp trong kiến trúc microservices hiện có (API Gateway, Auth, Kafka, Prometheus, Docker).

### 1) Yêu cầu môi trường
- Go >= 1.21
- Docker & Docker Compose
- (Tùy chọn) MinIO hoặc S3 nếu lưu trữ object storage; mặc định hỗ trợ lưu local disk
- (Tùy chọn) Kafka nếu cần bắn event khi upload/xóa ảnh

### 2) Khởi tạo service
```bash
cd image
go mod init github.com/your-org/image-service
go mod tidy
```

### 3) Thư viện đề xuất
```bash
go get github.com/gin-gonic/gin \
       github.com/gin-contrib/zap \
       go.uber.org/zap \
       github.com/caarlos0/env/v9 \
       github.com/google/uuid \
       github.com/h2non/filetype \
       github.com/minio/minio-go/v7 \
       github.com/redis/go-redis/v9 \
       github.com/prometheus/client_golang/prometheus/promhttp \
       go.opentelemetry.io/contrib/instrumentation/github.com/gin-gonic/gin/otelgin \
       go.opentelemetry.io/otel \
       github.com/segmentio/kafka-go
```

Giải thích ngắn:
- Gin: HTTP framework phổ biến, hiệu năng tốt, hệ sinh thái middleware phong phú
- gin-contrib/zap + zap: logging có cấu trúc
- env: load cấu hình từ biến môi trường
- filetype: xác định mime/định dạng ảnh an toàn
- minio-go: client S3/MinIO (nếu dùng object storage)
- redis: cache metadata hoặc URL tạm
- prometheus/otel: metrics + tracing
- kafka-go: publish event (tùy chọn)

### 4) Cấu trúc thư mục gợi ý
```
image/
  cmd/
    server/
      main.go
  internal/
    config/
      config.go
    http/
      handler.go
      middleware.go
      routes.go
    storage/
      storage.go        # interface chung
      local/
        local.go
      s3/
        s3.go
    service/
      image_service.go
    kafka/
      producer.go       # tùy chọn
    metrics/
      prometheus.go
  Dockerfile
  .env.example
  README.md (file này)
```

### 5) Biến môi trường (.env.example)
```env
APP_NAME=image-service
APP_PORT=8080
ENV=dev

STORAGE_DRIVER=local            # local | s3
LOCAL_STORAGE_DIR=./data        # thư mục lưu file khi dùng local

S3_ENDPOINT=http://localhost:9000
S3_BUCKET=images
S3_ACCESS_KEY=minioadmin
S3_SECRET_KEY=minioadmin
S3_USE_SSL=false

MAX_UPLOAD_MB=10
ALLOWED_MIME=image/jpeg,image/png,image/webp

KAFKA_BROKERS=localhost:9092
KAFKA_TOPIC_IMAGE=image.events

PROMETHEUS_ENABLED=true
OTEL_EXPORTER_OTLP_ENDPOINT=
```

### 6) Giao diện Storage
```go
// internal/storage/storage.go
package storage

import "context"

type PutResult struct {
    Key      string
    MimeType string
    Size     int64
    URL      string // pre-signed hoặc path tương đối
}

type Storage interface {
    Put(ctx context.Context, objectName string, data []byte, mime string) (PutResult, error)
    Get(ctx context.Context, objectName string) ([]byte, string, error)
    Delete(ctx context.Context, objectName string) error
    BuildURL(objectName string) string
}
```

### 7) Local storage (ví dụ tối giản)
```go
// internal/storage/local/local.go
package local

import (
    "context"
    "io/fs"
    "os"
    "path/filepath"
    "strings"

    "github.com/your-org/image-service/internal/storage"
)

type LocalStorage struct {
    baseDir string
}

func New(baseDir string) (*LocalStorage, error) {
    if err := os.MkdirAll(baseDir, 0o755); err != nil {
        return nil, err
    }
    return &LocalStorage{baseDir: baseDir}, nil
}

func (s *LocalStorage) Put(_ context.Context, name string, data []byte, mime string) (storage.PutResult, error) {
    path := filepath.Join(s.baseDir, filepath.Clean(name))
    if err := os.MkdirAll(filepath.Dir(path), 0o755); err != nil {
        return storage.PutResult{}, err
    }
    if err := os.WriteFile(path, data, fs.FileMode(0o644)); err != nil {
        return storage.PutResult{}, err
    }
    return storage.PutResult{Key: name, MimeType: mime, Size: int64(len(data)), URL: "/images/" + strings.TrimPrefix(name, "/")}, nil
}

func (s *LocalStorage) Get(_ context.Context, name string) ([]byte, string, error) {
    path := filepath.Join(s.baseDir, filepath.Clean(name))
    b, err := os.ReadFile(path)
    return b, "", err
}

func (s *LocalStorage) Delete(_ context.Context, name string) error {
    path := filepath.Join(s.baseDir, filepath.Clean(name))
    return os.Remove(path)
}

func (s *LocalStorage) BuildURL(name string) string { return "/images/" + strings.TrimPrefix(name, "/") }
```

### 8) HTTP endpoints (Gin)
- POST `/v1/images` nhận multipart form `file` để upload
- GET `/v1/images/:key` trả về file (nếu dùng local) hoặc redirect sang URL S3
- DELETE `/v1/images/:key` xóa file

Ví dụ khởi tạo server và handler upload (rút gọn):
```go
// cmd/server/main.go (ví dụ khởi tạo)
package main

import (
    "net/http"

    "github.com/gin-contrib/zap"
    "github.com/gin-gonic/gin"
    "go.uber.org/zap"
)

func main() {
    logger, _ := zap.NewProduction()
    defer logger.Sync()

    r := gin.New()
    r.Use(ginzap.Ginzap(logger, "2006-01-02T15:04:05Z07:00", true))
    r.Use(ginzap.RecoveryWithZap(logger, true))

    // Giới hạn kích thước upload 10MB (ví dụ)
    r.Use(func(c *gin.Context) {
        c.Request.Body = http.MaxBytesReader(c.Writer, c.Request.Body, 10<<20)
        c.Next()
    })

    v1 := r.Group("/v1")
    {
        v1.POST("/images", uploadHandler)
        v1.GET("/images/:key", getHandler)
        v1.DELETE("/images/:key", deleteHandler)
    }

    // Prometheus metrics
    r.GET("/metrics", gin.WrapH(promhttp.Handler()))

    _ = r.Run(":8080")
}

func uploadHandler(c *gin.Context) {
    fh, err := c.FormFile("file")
    if err != nil {
        c.JSON(http.StatusBadRequest, gin.H{"error": "missing file"})
        return
    }
    file, err := fh.Open()
    if err != nil {
        c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
        return
    }
    defer file.Close()

    buf := make([]byte, fh.Size)
    if _, err := file.Read(buf); err != nil {
        c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
        return
    }

    // result, err := imageService.Upload(c.Request.Context(), fh.Filename, buf)
    // if err != nil { c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()}); return }
    c.JSON(http.StatusCreated, gin.H{"ok": true})
}

func getHandler(c *gin.Context)    { /* TODO: triển khai */ }
func deleteHandler(c *gin.Context) { /* TODO: triển khai */ }
```

### 9) Service logic
- Validate mime/type bằng `filetype`
- Chuẩn hóa key: `yyyy/mm/dd/uuid-filename.ext`
- Lưu vào storage (local hoặc s3)
- (Tùy chọn) Publish Kafka event `image.uploaded`

### 10) Middleware & bảo mật
- Giới hạn kích thước upload: dựa trên `MAX_UPLOAD_MB`
- Xác thực: kiểm tra JWT từ API Gateway (forward header), role-based nếu cần
- Chỉ chấp nhận mime trong `ALLOWED_MIME`
- Tẩy metadata EXIF nếu có yêu cầu bảo mật (có thể dùng thêm lib xử lý ảnh)

### 11) Metrics & Tracing
- Bật `/metrics` cho Prometheus (dùng `promhttp.Handler()` + `gin.WrapH`)
- Thêm middleware OpenTelemetry với Gin: `otelgin.Middleware("image-service")`

### 12) Dockerfile (tham khảo)
```dockerfile
FROM golang:1.22 AS build
WORKDIR /app
COPY go.mod go.sum ./
RUN go mod download
COPY . .
RUN CGO_ENABLED=0 GOOS=linux GOARCH=amd64 go build -o /image-service ./cmd/server

FROM gcr.io/distroless/base-debian12
WORKDIR /
COPY --from=build /image-service /image-service
COPY --from=build /app/.env.example /.env.example
EXPOSE 8080
USER 65532:65532
ENTRYPOINT ["/image-service"]
```

### 13) docker-compose (local dev với MinIO)
```yaml
services:
  image-service:
    build: ./image
    environment:
      - APP_PORT=8080
      - STORAGE_DRIVER=s3
      - S3_ENDPOINT=http://minio:9000
      - S3_BUCKET=images
      - S3_ACCESS_KEY=minioadmin
      - S3_SECRET_KEY=minioadmin
      - S3_USE_SSL=false
      - PROMETHEUS_ENABLED=true
    ports:
      - "8080:8080"
    depends_on:
      - minio

  minio:
    image: minio/minio:latest
    environment:
      - MINIO_ROOT_USER=minioadmin
      - MINIO_ROOT_PASSWORD=minioadmin
    command: server /data --console-address ":9001"
    ports:
      - "9000:9000"
      - "9001:9001"
```

### 14) Tích hợp với hệ thống hiện có
- API Gateway: route `/image/**` tới `image-service:8080`
- Auth: forward `Authorization` header; service tự verify signature/claims hoặc tin cậy GW
- Kafka: dùng `KAFKA_BROKERS` và `KAFKA_TOPIC_IMAGE` để publish event sau upload/xóa
- Prometheus: thêm job scrape tới `image-service:8080/metrics` trong `prometheus.yml`

### 15) Kiểm thử nhanh
```bash
curl -F file=@/path/to/image.jpg http://localhost:8080/v1/images
curl -I http://localhost:8080/v1/images/{key}
curl -X DELETE http://localhost:8080/v1/images/{key}
```

### 16) Lưu ý triển khai production
- Dùng CDN (CloudFront/Cloudflare) cache ảnh
- Bật pre-signed URL thay vì streaming trực tiếp nếu dùng S3
- Thiết lập lifecycle policy (s3) và backup
- Rate limit + WAF tại API Gateway
- Scan virus file upload nếu yêu cầu compliance

---
Nếu bạn muốn, tôi có thể scaffold mã khởi đầu (theo cấu trúc ở trên) trực tiếp trong thư mục `image/` để bạn chạy ngay.


