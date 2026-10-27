# message-intel

报文分析与检索微服务。接入原始报文后自动识别 JSON / XML / HTTP / HEX / 键值文本，提取字段并写入 Elasticsearch，再按关键字、协议、源、目标和时间范围检索。

## 模块

| 模块 | 端口 | 职责 |
| --- | --- | --- |
| `intel-registry` | 8761 | Eureka 注册中心 |
| `intel-gateway` | 8080 | API 网关 |
| `intel-ingest` | 8081 | 报文接入与解析 |
| `intel-search` | 8082 | 索引与检索 |
| `intel-common` | - | 共享 DTO |

## 启动

需要 JDK 21+。本机当前可用 Maven Wrapper。

```bash
docker compose up -d
./mvnw -pl intel-registry,intel-gateway,intel-ingest,intel-search -am spring-boot:run
```

四个应用需要分别启动：

```bash
./mvnw -pl intel-registry -am spring-boot:run
./mvnw -pl intel-search -am spring-boot:run
./mvnw -pl intel-ingest -am spring-boot:run
./mvnw -pl intel-gateway -am spring-boot:run
```

## 接口（经网关 `http://localhost:8080`）

分析但不落库：

```bash
curl -s http://localhost:8080/api/ingest/analyze \
  -H 'Content-Type: application/json' \
  -d '{"source":"core","destination":"risk","rawPayload":"{\"txnId\":\"T-1001\",\"amount\":99.5}"}'
```

分析并检索入库：

```bash
curl -s http://localhost:8080/api/ingest/messages \
  -H 'Content-Type: application/json' \
  -d '{"source":"gw","destination":"pay","rawPayload":"POST /pay HTTP/1.1\nHost: bank.local\n\namount=100"}'
```

检索：

```bash
curl -s 'http://localhost:8080/api/search/messages?q=T-1001&protocol=JSON'
curl -s http://localhost:8080/api/search/stats
```

## 请求字段

`IngestMessageRequest`

- `rawPayload`：必填，原始报文
- `source` / `destination`：可选，发送方与接收方
- `protocol`：可选，强制指定 JSON、XML、HTTP、HEX、KV
- `encoding`：可选，默认 UTF-8；十六进制串会按二进制解析
- `capturedAt`：可选采集时间
- `tags` / `extraFields`：可选附加信息
