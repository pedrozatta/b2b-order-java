# B2B Order — Sistema de Gestão de Pedidos

Microserviço REST para recebimento, processamento e gerenciamento de pedidos B2B de parceiros comerciais, com catálogo de produtos, controle de crédito, machine de status, notificações simuladas e suporte a alta concorrência.

> API only — não há interface de usuário.

---

## Sumário

- [Stack](#stack)
- [Arquitetura](#arquitetura)
- [Requisitos](#requisitos)
- [Como executar](#como-executar)
- [Credenciais e seed](#credenciais-e-seed)
- [Documentação da API](#documentação-da-api)
- [Contrato REST](#contrato-rest)
- [Regras de negócio](#regras-de-negócio)
- [Exemplos com curl](#exemplos-com-curl)
- [Erros e rastreabilidade](#erros-e-rastreabilidade)
- [Testes](#testes)
- [Estrutura do projeto](#estrutura-do-projeto)
- [Fora de escopo](#fora-de-escopo)

---

## Stack

| Tecnologia | Versão / uso |
|---|---|
| Java | 17 |
| Spring Boot | 3.4.x |
| Build | Gradle (Kotlin DSL) |
| Persistência | Spring Data JPA + Flyway |
| Banco local / testes | H2 (in-memory) |
| Banco Docker / stage-like | PostgreSQL |
| Segurança | HTTP Basic Authentication |
| API docs | springdoc-openapi (Swagger UI) |
| Container | Docker + docker-compose |

Código-fonte, identificadores, logs e campos JSON em **inglês**. Documentação do projeto (este README) e textos do Swagger voltados ao usuário em **português (PT-BR)**.

---

## Arquitetura

O projeto segue **Clean Architecture**, com dependências apontando para dentro:

| Camada | Responsabilidade |
|---|---|
| `domain` | Modelos, enums, invariantes, ports (repositórios / notificação), exceções de domínio |
| `application` | Use cases (`@Transactional`), orquestração via ports |
| `presentation` | Controllers REST finos, DTOs, mappers REST, OpenAPI |
| `infrastructure` | Entidades JPA, Spring Data, adapters, segurança, simulação de mensageria |
| `common` | Exceções base, Problem Details, trace, auditoria, paginação |

Pacote raiz: `br.com.zattaz`  
Módulos principais: `common`, `order`, `partner`, `product`

Padrões adotados:

- Controllers sem regra de negócio
- Use cases não acessam Spring Data diretamente
- Mappers explícitos (`@Component`) nas fronteiras REST e JPA
- Soft delete (`is_deleted`) + auditoria (`updated_at`, `updated_by`)
- Optimistic locking com `@Version` (concorrência de crédito/pedido)
- Sem comentários/Javadoc no código-fonte

---

## Requisitos

### Funcionais

- CRUD de produtos (catálogo)
- Cadastro / consulta de parceiros com créditos (`PUT` / `GET`)
- Cadastro de pedidos informando lista de produtos e quantidades (idempotente)
- Consulta de pedidos por ID, período e/ou status (paginada)
- Transições explícitas de status
- Cancelamento de pedidos
- Sistema de créditos (validação na criação, débito na aprovação, estorno no cancelamento)
- Notificação simulada a cada mudança de status

### Técnicos

- Alta concorrência tratada com `@Version` (conflito → HTTP 409)
- Documentação OpenAPI
- Execução via `docker compose up` (aplicação + PostgreSQL)
- Testes unitários e de integração REST com contexto Spring real

---

## Como executar

### Pré-requisitos

- JDK 17+
- Docker e Docker Compose (para execução containerizada)
- Gradle Wrapper (`./gradlew`) incluso no repositório

### Opção 1 — Docker Compose (recomendado)

Sobe a API e o PostgreSQL com um único comando:

```bash
docker compose up --build
```

- API: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`
- Health: `http://localhost:8080/actuator/health`

Para derrubar:

```bash
docker compose down
```

### Opção 2 — Local com H2 (profile `local`)

```bash
./gradlew bootRun --args='--spring.profiles.active=local'
```

O profile `local` usa H2 em memória (não exige PostgreSQL).

### Opção 3 — Local apontando para PostgreSQL

```bash
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/b2b_order
export SPRING_DATASOURCE_USERNAME=b2b
export SPRING_DATASOURCE_PASSWORD=b2b
./gradlew bootRun
```

### Build

```bash
./gradlew build
```

---

## Credenciais e seed

### Autenticação (HTTP Basic)

| Usuário | Senha | Uso |
|---|---|---|
| `admin` | `admin123` | Operações da API |

> Em ambiente real, troque essas credenciais. Aqui existem apenas para facilitar o desafio e os exemplos.

### Parceiros seed

| Partner ID | Nome | Créditos disponíveis |
|---|---|---|
| `11111111-1111-1111-1111-111111111111` | Partner Alpha | `10000.00` |
| `22222222-2222-2222-2222-222222222222` | Partner Beta | `500.00` |

### Produtos seed

| Product ID | Nome / SKU | Preço unitário |
|---|---|---|
| `aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa` | Notebook 14" | `3500.00` |
| `bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb` | Mouse | `120.00` |
| `cccccccc-cccc-cccc-cccc-cccccccccccc` | Teclado | `250.00` |

---

## Documentação da API

Com a aplicação no ar:

- **Swagger UI:** [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **OpenAPI:** [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

No Swagger, use **Authorize** com as credenciais Basic Auth.

---

## Contrato REST

Convenções:

- URIs em `kebab-case`, recursos no plural
- JSON em `camelCase`
- Datas em ISO 8601 (UTC)
- Criação / upsert via `PUT` (idempotente)
- Transições de estado / ações via `POST`
- Remoção lógica via `DELETE` (soft delete)
- Header de correlação: `X-Trace-Id` em todas as respostas

### Produtos

| Método | Endpoint | Descrição |
|---|---|---|
| `PUT` | `/products/{productId}` | Cria ou atualiza produto (idempotente) |
| `GET` | `/products/{productId}` | Consulta produto por ID |
| `GET` | `/products?page=&size=` | Lista produtos (paginado) |
| `DELETE` | `/products/{productId}` | Remove produto (soft delete) |

Payload de cadastro/atualização:

```json
{
  "name": "Notebook 14\"",
  "sku": "SKU-NOTEBOOK-14",
  "unitPrice": 3500.00
}
```

### Parceiros e créditos

Um único recurso concentra cadastro do parceiro e definição dos créditos:

| Método | Endpoint | Descrição |
|---|---|---|
| `PUT` | `/partners/{partnerId}` | Cria ou atualiza parceiro informando os créditos |
| `GET` | `/partners/{partnerId}` | Consulta parceiro com seus créditos |
| `GET` | `/partners?page=&size=` | Lista parceiros (paginado) |

Payload de cadastro/atualização:

```json
{
  "name": "Partner Alpha",
  "availableCredit": 10000.00
}
```

Resposta de consulta:

```json
{
  "id": "11111111-1111-1111-1111-111111111111",
  "name": "Partner Alpha",
  "availableCredit": 10000.00,
  "createdAt": "2026-09-09T12:00:00Z",
  "updatedAt": "2026-09-09T12:00:00Z"
}
```

> `PUT /partners/{partnerId}` define o saldo de créditos informado no body (cadastro e reposição/ajuste no mesmo endpoint). Não há endpoint separado de “add credits”.

### Pedidos

| Método | Endpoint | Descrição |
|---|---|---|
| `PUT` | `/orders/{orderId}` | Cria pedido com lista de produtos e quantidades |
| `GET` | `/orders/{orderId}` | Consulta por ID |
| `GET` | `/orders?status=&from=&to=&page=&size=` | Lista / filtra (paginado) |
| `POST` | `/orders/{orderId}/approve` | Aprova e debita crédito |
| `POST` | `/orders/{orderId}/start-processing` | Inicia processamento |
| `POST` | `/orders/{orderId}/ship` | Marca como enviado |
| `POST` | `/orders/{orderId}/deliver` | Marca como entregue |
| `POST` | `/orders/{orderId}/cancel` | Cancela (estorna crédito se já debitado) |

Payload de criação — informa **produtos existentes** e **quantidades**. O preço unitário é obtido do catálogo e gravado (snapshot) no item do pedido:

```json
{
  "partnerId": "11111111-1111-1111-1111-111111111111",
  "items": [
    { "productId": "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa", "quantity": 2 },
    { "productId": "bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb", "quantity": 2 }
  ]
}
```

### Paginação

```json
{
  "data": [],
  "page": {
    "size": 10,
    "totalElements": 130,
    "totalPages": 13,
    "number": 0
  }
}
```

---

## Regras de negócio

### Produtos

- Todo item de pedido referencia um `productId` existente e não removido.
- `unitPrice` e `name`/`sku` do produto devem ser válidos (`unitPrice > 0`).
- Na criação do pedido, o sistema resolve o produto, valida a existência e calcula `totalAmount = Σ (unitPrice × quantity)`.
- O preço vigente no momento da criação é **snapshot** no item do pedido (alterações posteriores no catálogo não recalculam pedidos já criados).

### Status do pedido

Valores: `PENDING`, `APPROVED`, `PROCESSING`, `SHIPPED`, `DELIVERED`, `CANCELLED`

Transições permitidas:

```text
PENDING      → APPROVED | CANCELLED
APPROVED     → PROCESSING | CANCELLED
PROCESSING   → SHIPPED | CANCELLED
SHIPPED      → DELIVERED
DELIVERED    → (terminal)
CANCELLED    → (terminal)
```

Transição inválida retorna erro de negócio (tipicamente **400** ou **409**).

### Campos do pedido

- `id` (UUID)
- `partnerId`
- `items[]`: `productId`, `productName` (snapshot), `quantity`, `unitPrice` (snapshot)
- `totalAmount`
- `status`
- `createdAt`, `updatedAt`
- versionamento otimista (`@Version`)

### Crédito e parceiro

1. **Cadastro/atualização do parceiro (`PUT`)**: informa `name` e `availableCredit` (≥ 0).
2. **Criação do pedido**: valida se o parceiro tem crédito disponível ≥ `totalAmount` (não debita).
3. **Aprovação**: debita o valor do crédito disponível.
4. **Cancelamento**:
   - Se ainda `PENDING`: não há estorno (nada foi debitado).
   - Se já houve débito (`APPROVED` ou posterior cancelável): estorna o valor.
5. Conflito de versão (concorrência) → **409 Conflict**.

### Notificações

A cada transição de status bem-sucedida, o sistema publica uma notificação via port de domínio.  
A implementação de infraestrutura **simula** mensageria (log / recorder em memória nos testes). Não há broker real (Kafka/Rabbit) neste desafio.

---

## Exemplos com curl

Variáveis úteis:

```bash
export BASE_URL=http://localhost:8080
export USER=admin
export PASS=admin123
export PARTNER_ID=11111111-1111-1111-1111-111111111111
export PRODUCT_NOTEBOOK=aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa
export PRODUCT_MOUSE=bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb
export ORDER_ID=$(uuidgen | tr '[:upper:]' '[:lower:]')
```

> No macOS, `uuidgen` já existe. Em Linux, use `uuidgen` do pacote `uuid-runtime` ou outro gerador de UUID.

### 1. Cadastrar / atualizar produto

```bash
curl -s -u "$USER:$PASS" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -X PUT "$BASE_URL/products/$PRODUCT_NOTEBOOK" \
  -d '{
    "name": "Notebook 14\"",
    "sku": "SKU-NOTEBOOK-14",
    "unitPrice": 3500.00
  }' | jq
```

```bash
curl -s -u "$USER:$PASS" \
  -H "Content-Type: application/json" \
  -X PUT "$BASE_URL/products/$PRODUCT_MOUSE" \
  -d '{
    "name": "Mouse",
    "sku": "SKU-MOUSE",
    "unitPrice": 120.00
  }' | jq
```

### 2. Consultar produto

```bash
curl -s -u "$USER:$PASS" \
  -H "Accept: application/json" \
  "$BASE_URL/products/$PRODUCT_NOTEBOOK" | jq
```

### 3. Listar produtos

```bash
curl -s -u "$USER:$PASS" \
  -H "Accept: application/json" \
  -G "$BASE_URL/products" \
  --data-urlencode "page=0" \
  --data-urlencode "size=10" | jq
```

### 4. Remover produto (soft delete)

```bash
curl -s -i -u "$USER:$PASS" \
  -X DELETE "$BASE_URL/products/cccccccc-cccc-cccc-cccc-cccccccccccc"
```

### 5. Cadastrar / atualizar parceiro com créditos

```bash
curl -s -u "$USER:$PASS" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -X PUT "$BASE_URL/partners/$PARTNER_ID" \
  -d '{
    "name": "Partner Alpha",
    "availableCredit": 10000.00
  }' | jq
```

Para **adicionar/repor créditos**, use o mesmo endpoint informando o novo saldo desejado (ou um valor maior):

```bash
curl -s -u "$USER:$PASS" \
  -H "Content-Type: application/json" \
  -X PUT "$BASE_URL/partners/$PARTNER_ID" \
  -d '{
    "name": "Partner Alpha",
    "availableCredit": 11500.00
  }' | jq
```

### 6. Consultar parceiro com créditos

```bash
curl -s -u "$USER:$PASS" \
  -H "Accept: application/json" \
  "$BASE_URL/partners/$PARTNER_ID" | jq
```

### 7. Listar parceiros (paginado)

```bash
curl -s -u "$USER:$PASS" \
  -H "Accept: application/json" \
  -G "$BASE_URL/partners" \
  --data-urlencode "page=0" \
  --data-urlencode "size=10" | jq
```

### 8. Criar pedido com lista de produtos e quantidades

```bash
curl -s -u "$USER:$PASS" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -X PUT "$BASE_URL/orders/$ORDER_ID" \
  -d "{
    \"partnerId\": \"$PARTNER_ID\",
    \"items\": [
      { \"productId\": \"$PRODUCT_NOTEBOOK\", \"quantity\": 2 },
      { \"productId\": \"$PRODUCT_MOUSE\", \"quantity\": 2 }
    ]
  }" | jq
```

`totalAmount` esperado neste exemplo: `(3500 × 2) + (120 × 2) = 7240.00`.

Repetir o mesmo `PUT` com o mesmo `orderId` deve ser idempotente.

### 9. Consultar pedido por ID

```bash
curl -s -u "$USER:$PASS" \
  -H "Accept: application/json" \
  "$BASE_URL/orders/$ORDER_ID" | jq
```

### 10. Listar pedidos (filtro por status e período)

```bash
curl -s -u "$USER:$PASS" \
  -H "Accept: application/json" \
  -G "$BASE_URL/orders" \
  --data-urlencode "status=PENDING" \
  --data-urlencode "from=2026-01-01T00:00:00Z" \
  --data-urlencode "to=2026-12-31T23:59:59Z" \
  --data-urlencode "page=0" \
  --data-urlencode "size=10" | jq
```

### 11. Aprovar pedido (debita crédito)

```bash
curl -s -u "$USER:$PASS" \
  -H "Accept: application/json" \
  -X POST "$BASE_URL/orders/$ORDER_ID/approve" | jq
```

### 12. Iniciar processamento

```bash
curl -s -u "$USER:$PASS" \
  -H "Accept: application/json" \
  -X POST "$BASE_URL/orders/$ORDER_ID/start-processing" | jq
```

### 13. Marcar como enviado

```bash
curl -s -u "$USER:$PASS" \
  -H "Accept: application/json" \
  -X POST "$BASE_URL/orders/$ORDER_ID/ship" | jq
```

### 14. Marcar como entregue

```bash
curl -s -u "$USER:$PASS" \
  -H "Accept: application/json" \
  -X POST "$BASE_URL/orders/$ORDER_ID/deliver" | jq
```

### 15. Cancelar pedido (estorna crédito quando aplicável)

```bash
curl -s -u "$USER:$PASS" \
  -H "Accept: application/json" \
  -X POST "$BASE_URL/orders/$ORDER_ID/cancel" | jq
```

### 16. Fluxo completo

```bash
PARTNER_ID=$(uuidgen | tr '[:upper:]' '[:lower:]')
PRODUCT_A=$(uuidgen | tr '[:upper:]' '[:lower:]')
PRODUCT_B=$(uuidgen | tr '[:upper:]' '[:lower:]')
ORDER_ID=$(uuidgen | tr '[:upper:]' '[:lower:]')

# produtos
curl -s -u "$USER:$PASS" -H "Content-Type: application/json" -X PUT \
  "$BASE_URL/products/$PRODUCT_A" \
  -d '{"name":"SKU-A","sku":"SKU-A","unitPrice":100.00}' | jq

curl -s -u "$USER:$PASS" -H "Content-Type: application/json" -X PUT \
  "$BASE_URL/products/$PRODUCT_B" \
  -d '{"name":"SKU-B","sku":"SKU-B","unitPrice":50.00}' | jq

# parceiro com créditos
curl -s -u "$USER:$PASS" -H "Content-Type: application/json" -X PUT \
  "$BASE_URL/partners/$PARTNER_ID" \
  -d '{"name":"Partner Demo","availableCredit":1000.00}' | jq

# pedido com produtos + quantidades
curl -s -u "$USER:$PASS" -H "Content-Type: application/json" -X PUT \
  "$BASE_URL/orders/$ORDER_ID" \
  -d "{\"partnerId\":\"$PARTNER_ID\",\"items\":[{\"productId\":\"$PRODUCT_A\",\"quantity\":2},{\"productId\":\"$PRODUCT_B\",\"quantity\":1}]}" | jq

curl -s -u "$USER:$PASS" -X POST "$BASE_URL/orders/$ORDER_ID/approve" | jq
curl -s -u "$USER:$PASS" -X POST "$BASE_URL/orders/$ORDER_ID/start-processing" | jq
curl -s -u "$USER:$PASS" -X POST "$BASE_URL/orders/$ORDER_ID/ship" | jq
curl -s -u "$USER:$PASS" -X POST "$BASE_URL/orders/$ORDER_ID/deliver" | jq

curl -s -u "$USER:$PASS" "$BASE_URL/orders/$ORDER_ID" | jq
curl -s -u "$USER:$PASS" "$BASE_URL/partners/$PARTNER_ID" | jq
```

### 17. Exemplo de erro de validação (422)

```bash
curl -s -i -u "$USER:$PASS" \
  -H "Content-Type: application/json" \
  -X PUT "$BASE_URL/orders/$(uuidgen | tr '[:upper:]' '[:lower:]')" \
  -d "{
    \"partnerId\": \"$PARTNER_ID\",
    \"items\": []
  }"
```

Resposta esperada (RFC 9457 / Problem Details), incluindo header `X-Trace-Id`:

```http
HTTP/1.1 422 Unprocessable Entity
X-Trace-Id: ...
Content-Type: application/problem+json

{
  "type": "about:blank",
  "title": "Unprocessable Entity",
  "status": 422,
  "detail": "items: must not be empty",
  "traceId": "...",
  "date": "2026-09-09T15:00:00Z",
  "details": [
    { "field": "items", "message": "must not be empty" }
  ]
}
```

### 18. Sem autenticação (401)

```bash
curl -s -i -H "Accept: application/json" \
  "$BASE_URL/orders/$ORDER_ID"
```

---

## Erros e rastreabilidade

Erros seguem **RFC 9457** (`ProblemDetail`):

| Situação | HTTP |
|---|---|
| JSON inválido / request malformado | 400 |
| Não autenticado | 401 |
| Recurso não encontrado (produto, parceiro, pedido) | 404 |
| Conflito de estado / optimistic lock | 409 |
| Validação de campos / invariantes | 422 |
| Erro inesperado | 500 |

Todas as respostas (sucesso ou erro) incluem o header **`X-Trace-Id`**. Em erros, o mesmo valor aparece no corpo como `traceId`.

Propriedades JSON desconhecidas no request são **ignoradas** (evolução aditiva do contrato).

---

## Testes

```bash
./gradlew test
```

Relatório de cobertura (JaCoCo):

```bash
./gradlew jacocoTestReport
```

- HTML: `build/reports/jacoco/test/html/index.html`
- XML: `build/reports/jacoco/test/jacocoTestReport.xml`

Cobertura esperada:

- **Unitários** em `domain`, `application` e `infrastructure` (Mockito + AssertJ)
- **Integração REST** (`*IT`) com contexto Spring real, H2 e Basic Auth:
  - `ProductIT`
  - `PartnerIT`
  - `OrderIT`

Cenários relevantes: CRUD de produto, cadastro/consulta de parceiro com créditos, pedido com produtos inexistentes, crédito insuficiente, transição inválida, cancelamento com estorno, conflito de versão, validação 422 e header `X-Trace-Id`.

### CI (GitHub Actions)

| Evento | Workflow | O que faz |
|---|---|---|
| Push/merge em `main` | [`.github/workflows/on_push_main.yml`](.github/workflows/on_push_main.yml) | Build, testes, cobertura e artifact |
| Pull request para `main` | [`.github/workflows/on_pull_request_main.yml`](.github/workflows/on_pull_request_main.yml) | Build, testes, comentário de cobertura no PR e **gate ≥ 70%** de linhas |

O gate de cobertura é enforced pelo JaCoCo (`jacocoTestCoverageVerification`). Se a cobertura de linhas for inferior a **70%**, o job falha e o merge fica bloqueado quando o check for obrigatório.

Para exigir o sucesso do check antes do merge, configure no GitHub:

**Settings → Branches → Branch protection rule** (branch `main`):

- Require a pull request before merging
- Require status checks to pass before merging
- Status check obrigatório: `Build, Tests and Coverage Gate`

---

## Estrutura do projeto

```text
b2b-order-java/
├── AGENTS.md
├── Dockerfile
├── docker-compose.yml
├── build.gradle.kts
├── settings.gradle.kts
├── README.md
└── src/
    ├── main/
    │   ├── java/br/com/zattaz/
    │   │   ├── common/
    │   │   ├── product/
    │   │   ├── partner/
    │   │   └── order/
    │   │       ├── domain/
    │   │       ├── application/usecase/
    │   │       ├── presentation/
    │   │       └── infrastructure/
    │   └── resources/
    │       ├── application.yaml
    │       ├── application-local.yaml
    │       └── db/migration/
    └── test/java/br/com/zattaz/
```

---

## Fora de escopo

- Interface gráfica (UI)
- Broker real de mensageria (Kafka, RabbitMQ, etc.) — apenas simulação
- Autenticação JWT / OAuth2 (o desafio usa HTTP Basic)

---

## Licença

Projeto de desafio técnico — uso educacional / avaliação.
