# Financial Transaction Service

Um microserviço robusto e escalável para o gerenciamento de Contas e Transações Financeiras. Construído seguindo princípios de **Arquitetura Hexagonal (Ports and Adapters)**, **Domain-Driven Design (DDD)** e **API First**.

## 🚀 Funcionalidades Principais

- **Gestão de Contas**:
  - Criação de novas contas com validação rígida de formato algorítmico para documentos (CPF/CNPJ).
  - Consulta de informações da conta.
  - Bloqueio de contas (Contas inativas/bloqueadas não podem realizar transações).
  
- **Gestão de Transações Financeiras**:
  - Processamento de transações.
  - Controle e inserção de débitos e créditos de acordo com os requisitos (valores são persistidos de acordo com a sua contrapartida natural contábil, baseada no tipo de operação).

- **Mecanismo de Idempotência**:
  - Proteção robusta contra concorrência (Race Conditions) e _double-spending_ usando bloqueios de banco de dados (`locked_at`).
  - Cache de respostas processadas com sucesso para requisições repetidas de mesma chave.
  - Liberação de locks expirados, permitindo que falhas de rede (timeout) ou _crashes_ não travem permanentemente a tentativa do cliente.

## 🏗️ Arquitetura

O projeto implementa rigorosamente a **Arquitetura Hexagonal**:

- **Domain Layer**: O núcleo do sistema, contendo regras de negócio, _Value Objects_ e exceções customizadas (ex: validador algorítmico do formato do documento, verificação de conta ativa, etc). Nenhuma dependência ou anotação de _framework_ de infraestrutura reside aqui.
- **Application Layer (Use Cases)**: Coordena os fluxos de aplicação chamando regras do domínio e delegando interações com o meio externo através das _Ports_ (interfaces).
- **Infrastructure Layer**: Os _Adapters_ de entrada (Controladores Web REST derivados do contrato) e os adaptadores de saída (Repositórios Spring Data JPA com banco de dados PostgreSQL).

A comunicação se dá estritamente das camadas mais externas para o núcleo (Domain). Dependências externas são injetadas nas "Portas" de saída, facilitando testes e isolando o modelo de negócio de detalhes de banco de dados ou _framework_.

## 💻 Detalhes Técnicos e Tecnologias

- **Java 21**: Utilização da versão LTS focando em estabilidade e recursos mais recentes.
- **Spring Boot 3.2.x**: Ecossistema core, lidando com injeção de dependência e servidor embutido.
- **OpenAPI Generator**: Definição **"API-First"**. Os endpoints HTTP e objetos de requisição derivam automaticamente durante o _build_ através de um contrato `openapi.yaml`. 
- **PostgreSQL**: Banco relacional para gerenciar estado, garantir consistência (ACID) e aplicar _Unique Constraints_ para as chaves de idempotência e documentos unificados das contas.
- **Flyway**: Gerenciamento de versionamento das estruturas de tabelas e esquemas de dados.
- **Testcontainers**: Abordagem primária em testes de integração, subindo containers Docker efêmeros do PostgreSQL para garantir uma simulação real sem o uso exagerado de _mocks_ e H2 Databases irreais.
- **Maven**: Ferramenta de _build_ e empacotamento.
- **Lombok**: Gerador de _boilerplate_ simplificado para redução de código verboso (getters, builder pattern, construtores).

## 🧪 Estratégia de Testes

A arquitetura de testes do projeto foi construída para garantir alto nível de confiança, operando em duas frentes complementares:

1. **Testes Unitários (Domain & Use Cases)**:
   - Validam exaustivamente o coração da aplicação isoladamente, assegurando o comportamento de _Value Objects_ (como `DocumentNumber` para regras matemáticas de CPF/CNPJ).
   - Testes nos Use Cases com _Mocks_ (via `Mockito`) garantem os fluxos de negócios (ex: proibir a execução de transações em contas inativas ou bloqueadas).

2. **Testes de Integração (Web & Persistence Layers)**:
   - Utilizam o **Testcontainers** para instanciar um banco de dados PostgreSQL efêmero via Docker.
   - Os testes de Idempotência e Concorrência disparam requisições via `MockMvc` de ponta-a-ponta, testando o banco real, bloqueios otimistas, violações de constraint e respostas _cached_.
   - Evita o uso de _in-memory databases_ (como H2), proporcionando 100% de paridade com o ambiente produtivo.

## 🔧 Como Executar

### Pré-requisitos
- **JDK 21** configurado no `PATH`.
- **Docker** ou **Docker Desktop** (Obrigatório rodando para rodar a suíte de testes de integração).
- **Maven 3.8+**

### Executando a Suíte de Testes
O projeto tem excelente cobertura englobando cenários transacionais em ambientes controlados. Basta rodar:

```bash
mvn clean test
```
*O Testcontainers irá efetuar o download/start do PostgreSQL automaticamente.*

### Iniciando a Aplicação (Docker)
Para iniciar rapidamente a aplicação e o banco de dados usando os _containers_ Docker, utilize o nosso script simplificado de inicialização:

```bash
./run.sh
```

A API estará servindo em `http://localhost:8080/api` e seguindo fielmente a topologia ditada pelo contrato OpenAPI.
Para conferir o Swagger UI, acesse: `http://localhost:8080/api/swagger-ui.html`
