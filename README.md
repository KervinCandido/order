# Order Service

## Descrição

Este serviço é responsável por receber pedidos, confirmados e encaminha o pedido para serviço de pagamento.

## Tecnologias Utilizadas

*   **Java 21**: Versão mais recente do Java, garantindo performance e acesso a features modernas da linguagem.
*   **Spring Boot 4.0.3**: Framework principal para a construção da aplicação, facilitando a configuração e o desenvolvimento de serviços RESTful.
*   **Spring Data JPA**: Para persistência de dados em banco de dados relacional.
*   **Spring Security**: Para autenticação e autorização.
*   **PostgreSQL**: Banco de dados relacional utilizado para persistir os dados da aplicação.
*   **H2 Database**: Banco de dados em memória para testes automatizados.
*   **Flyway**: Ferramenta para versionamento e migração de banco de dados.
*   **RabbitMQ**: Message broker para comunicação assíncrona entre serviços.
*   **MapStruct**: Para mapeamento de DTOs e entidades.
*   **SpringDoc (OpenAPI)**: Para documentação da API.
*   **Maven**: Gerenciador de dependências e build do projeto.
*   **JUnit 5 e Mockito**: Para testes unitários e de integração.
*   **Jacoco**: Para análise de cobertura de testes.

## Como Executar o Projeto

### Pré-requisitos

*   Java 21
*   Maven 3.9+
*   Docker e Docker Compose (para o banco de dados e RabbitMQ)

### Passos

1.  **Clone o repositório:**
    ```bash
    git clone https://github.com/KervinCandido/restaurant.git
    cd restaurant
    ```

2.  **Configure as variáveis de ambiente:**
    O serviço é configurado através do arquivo `src/main/resources/application.yaml` e seus perfis (`dev`, `prod`). As seguintes variáveis de ambiente são necessárias para o perfil `dev`:

    - **Banco de Dados:**
      - `DB_HOST`: Host do banco de dados (padrão: `localhost`)
      - `DB_PORT`: Porta do banco de dados (padrão: `5432`)
      - `DB_NAME`: Nome do banco de dados (padrão: `restaurant-db`)
      - `DB_USER`: Usuário do banco de dados (padrão: `restaurant-user`)
      - `DB_PASSWORD`: Senha do banco de dados (padrão: `restaurant-password`)

    - **RabbitMQ:**
      - `MQ_HOST`: Host do RabbitMQ (padrão: `localhost`)
      - `MQ_PORT`: Porta do RabbitMQ (padrão: `5672`)
      - `MQ_USER`: Usuário do RabbitMQ (padrão: `restaurant-mq`)
      - `MQ_PASSWORD`: Senha do RabbitMQ (padrão: `password-mq`)

    Você pode criar um arquivo `.env` na raiz do projeto ou configurar essas variáveis diretamente no seu ambiente de execução.

3.  **Inicie os serviços de dependência (usando Docker):**
    Se houver um arquivo `docker-compose.yml` na raiz do projeto, você pode iniciar o PostgreSQL e o RabbitMQ com:
    ```bash
    docker-compose up -d
    ```

### Execução

1.  **Compile e execute a aplicação com o Maven:**
    ```bash
    mvn spring-boot:run -Dspring-boot.run.profiles=dev
    ```

2.  A aplicação estará disponível em `http://localhost:8080`.

## Endpoints da API

A documentação completa da API está disponível via Swagger UI.

- **URL da Documentação (Swagger UI):** [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
- **URL da Definição OpenAPI:** [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)
- **URL do Actuator:** [http://localhost:8080/actuator/health](http://localhost:8080/actuator/health)

### Principais Endpoints

- **Pedidos:**
  - `POST /restaurants/{restaurant-id}/orders`: Cria pedido.
  - `POST /restaurants/{restaurant-id}/orders/confirm/{order-id}`: Confirma pedido.
  - `GET /restaurants/{restaurant-id}/orders/{id}`: Consulta pedido.
  - `GET /restaurants/{restaurant-id}/orders`: Consulta pedidos do usuário autenticado.


### Filas do RabbitMQ

- **Emite**
  - `payment.order.created`: Enviado quando uma pedido é criado e confirmado.

- **Consome**
 - `order.restaurant.created`: Evento para quando um restaurante é criado.
 - `order.restaurant.updated`: Evento para quando um restaurante é alterado.
 - `order.restaurant.deleted`: Evento para quando um restaurante é excluido.

 - `order.menuitem.created`: Evento para quando um item do menu é criado.
 - `order.menuitem.updated`: Evento para quando um item do menu é alterado.
 - `order.menuitem.deleted`: Evento para quando um item do menu é excluido.

 - `order.payment.approved`: Evento para quando um pagamento é aprovado.
 - `order.payment.pending`: Evento para quando um pagamento está pendente processamento.