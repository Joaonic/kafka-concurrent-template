# Data Sync Kafka Library

A **Data Sync Kafka Library** é uma biblioteca para integração com o Apache Kafka em aplicações Spring Boot. Ela facilita a ingestão, processamento e tratamento de mensagens do Kafka, oferecendo recursos como:

- **Configuração automática de consumidores** com Spring Boot via auto-configuração.
- **Conversão de mensagens** do formato JSON para objetos de domínio, utilizando a interface `Converter` (com uma implementação padrão via Jackson).
- **Validação de eventos** com suporte a Bean Validation.
- **Execução de ações customizadas** antes e depois da inserção (via interfaces `HandlerBeforeInsert` e `HandlerAfterInsert`).
- **Tratamento de erros** com suporte a retries e envio para DLQ (Dead Letter Queue).
- **Monitoramento e correlação de logs** usando AOP e a anotação `@AddCorrelator`.
- **Processamento assíncrono e concorrente** com executores baseados em virtual threads.

Esta biblioteca foi desenvolvida para abstrair e padronizar o processamento de eventos via Kafka, permitindo que você foque na lógica de negócio sem se preocupar com detalhes de configuração e infraestrutura.

---

## Sumário

- [Funcionalidades](#funcionalidades)
- [Pré-requisitos](#pré-requisitos)
- [Instalação](#instalação)
- [Configuração](#configuração)
  - [Ativando a Biblioteca](#ativando-a-biblioteca)
  - [Configurando o Consumidor Kafka](#configurando-o-consumidor-kafka)
  - [Conversores Personalizados](#conversores-personalizados)
  - [Handlers Pré e Pós-Inserção](#handlers-pré-e-pós-inserção)
  - [Propriedades do Consumidor](#propriedades-do-consumidor)
- [Como Funciona](#como-funciona)
  - [Registro Automático dos Consumidores](#registro-automático-dos-consumidores)
  - [Processamento de Mensagens](#processamento-de-mensagens)
  - [Produtor Kafka](#produtor-kafka)
  - [Aspectos e Correlação de Logs](#aspectos-e-correlação-de-logs)
  - [Processamento Concorrente](#processamento-concorrente)
- [Exemplos de Uso](#exemplos-de-uso)
- [Contribuindo](#contribuindo)
- [Licença](#licença)

---

## Funcionalidades

- **Auto-configuração Kafka**: Detecta automaticamente os beans que implementam `IngestService` e que estejam anotados com `@KafkaIngestConsumer` para registrar os endpoints de consumo.
- **Conversão de Mensagens**: Utiliza a interface `Converter<T>` para converter as mensagens recebidas (em JSON) para objetos do domínio. Se nenhum conversor customizado for encontrado, o `DefaultConverter` (baseado no Jackson `ObjectMapper`) é utilizado.
- **Validação e Tratamento de Erros**: Valida os eventos (se configurado) e trata exceções com lógica de retry. Mensagens com falha podem ser reenviadas para um tópico de retry ou, em caso de erros não recuperáveis, para uma DLQ.
- **Handlers Personalizados**: Permite a execução de ações antes e depois da inserção do evento, através das interfaces `HandlerBeforeInsert` e `HandlerAfterInsert`.
- **Logging com Correlation ID**: Utiliza AOP para injetar identificadores de correlação nos logs, facilitando o rastreamento das mensagens.
- **Processamento Assíncrono**: Os consumidores processam mensagens de forma concorrente utilizando executores baseados em virtual threads, garantindo alta escalabilidade.

---

## Pré-requisitos

- **Java 17** ou superior (para suporte a virtual threads).
- **Spring Boot** com dependência do Spring Kafka.
- **Apache Kafka** instalado e configurado.
- Dependências do **Jackson** para manipulação de JSON.
- Configuração de **Bean Validation** (Jakarta Validation) se você deseja utilizar a validação de eventos.

---

## Instalação

Adicione a dependência da biblioteca ao seu projeto.  
_Exemplo para Maven:_

```xml
<dependency>
    <groupId>br.gft.template</groupId>
    <artifactId>demo-events-kafka</artifactId>
    <version>1.0.0</version>
</dependency>
```

---

## Configuração

### Ativando a Biblioteca

Para ativar a integração com Kafka e a auto-configuração da biblioteca, adicione a anotação `@EnableDataSyncKafka` na sua aplicação:

```java
import br.gft.template.demo.events.EnableDataSyncKafka;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableDataSyncKafka
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

### Configurando o Consumidor Kafka

Implemente a interface `IngestService<T>` e anote a classe com `@KafkaIngestConsumer` para configurar os tópicos e propriedades do consumidor.

_Exemplo:_

```java
import br.gft.template.demo.events.IngestService;
import br.gft.template.demo.events.kafka.concurrent.config.KafkaIngestConsumer;
import org.springframework.stereotype.Service;

@Service
@KafkaIngestConsumer(
    topics = {"${kafka.topic.input}"},
    groupId = "${kafka.consumer.groupId}",
    retryTopic = "${kafka.topic.retry}",
    dlqTopic = "${kafka.topic.dlq}",
    retryAttempts = 3,
    validateEvent = true
)
public class MyIngestService implements IngestService<MyDomainObject> {

    @Override
    public MyDomainObject ingest(MyDomainObject value) {
        // Lógica de processamento do objeto
        System.out.println("Processando objeto: " + value);
        return value;
    }
}
```

### Conversores Personalizados

Caso seja necessário converter a mensagem (em JSON) para um objeto do seu domínio de forma customizada, implemente a interface `Converter<T>`.

_Exemplo:_

```java
import br.gft.template.demo.events.Converter;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class MyDomainObjectConverter implements Converter<MyDomainObject> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public MyDomainObject convert(String value) {
        try {
            return objectMapper.readValue(value, MyDomainObject.class);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao converter a mensagem para MyDomainObject", e);
        }
    }
}
```

Caso nenhum conversor seja definido para o tipo, o `DefaultConverter` será utilizado.

### Handlers Pré e Pós-Inserção

Para executar ações personalizadas antes ou depois da inserção do evento, implemente as interfaces `HandlerBeforeInsert<T>` e `HandlerAfterInsert<T>`.

_Exemplo de handler antes da inserção:_

```java
import br.gft.template.demo.events.HandlerBeforeInsert;
import org.springframework.stereotype.Component;

@Component
public class MyBeforeInsertHandler implements HandlerBeforeInsert<MyDomainObject> {

    @Override
    public void handle(MyDomainObject document, Object... args) {
        // Lógica a ser executada antes da inserção
        System.out.println("Antes da inserção: " + document);
    }
}
```

_Exemplo de handler depois da inserção:_

```java
import br.gft.template.demo.events.HandlerAfterInsert;
import org.springframework.stereotype.Component;

@Component
public class MyAfterInsertHandler implements HandlerAfterInsert<MyDomainObject> {

    @Override
    public void handle(MyDomainObject document, Object... args) {
        // Lógica a ser executada após a inserção
        System.out.println("Após a inserção: " + document);
    }
}
```

### Propriedades do Consumidor

A classe `KafkaConsumerProperties` controla as seguintes propriedades:
- **validateEvent**: Indica se o evento deve ser validado com base nas constraints da classe.
- **retryAttempts**: Número de tentativas de retry caso ocorra um erro passível de nova tentativa.
- **topics**: Lista de tópicos a serem consumidos.
- **retryTopic**: Tópico para envio de mensagens em retry.
- **dlqTopic**: Tópico para envio de mensagens para a Dead Letter Queue (DLQ).

Essas propriedades são configuradas via a anotação `@KafkaIngestConsumer`, que aceita placeholders e expressões SpEL, resolvidas dinamicamente através do `Environment` do Spring.

---

## Como Funciona

### Registro Automático dos Consumidores

A classe `AutoKafkaConsumerConfiguration` é responsável por:
1. **Escanear o contexto** à procura de beans que implementem `IngestService` e estejam anotados com `@KafkaIngestConsumer`.
2. **Resolver as propriedades** (tópicos, groupId, retryTopic, dlqTopic, etc.) utilizando placeholders e expressões SpEL.
3. **Determinar o tipo de objeto** que será ingerido e buscar um `Converter` compatível; se não for encontrado, o `DefaultConverter` é utilizado.
4. **Filtrar e injetar handlers** compatíveis com o tipo do evento (tanto antes quanto depois da inserção).
5. **Criar e registrar endpoints** do Kafka dinamicamente, associando o método `receive` do consumidor (`DefaultKafkaConsumer`) para processamento das mensagens.

### Processamento de Mensagens

A classe `KafkaConsumerService` executa o fluxo de processamento para cada mensagem recebida:

1. **Extração de Cabeçalhos:** Recupera os atributos dos cabeçalhos da mensagem Kafka.
2. **Conversão:** Utiliza o conversor (customizado ou padrão) para transformar o JSON da mensagem em um objeto do domínio.
3. **Validação:** Se habilitada, valida o objeto de acordo com as constraints definidas.
4. **Execução de Handlers:** Invoca os handlers de pré-inserção antes de chamar o método de ingestão e, posteriormente, os handlers de pós-inserção.
5. **Ingestão:** Chama o método `ingest` do `IngestService` para processar o objeto.
6. **Tratamento de Erros:**
    - Se ocorrer uma exceção *retryable* (do tipo `RetryableException`), a mensagem é reenviada para o tópico de retry, respeitando o número máximo de tentativas.
    - Se o número de tentativas exceder ou ocorrer outro tipo de erro, a mensagem é encaminhada para o DLQ.
7. **Logging e Monitoramento:** Utiliza o `LogstashMarkerProvider` para adicionar metadados e identificadores de correlação aos logs.

### Produtor Kafka

A classe `KafkaProducer` encapsula o envio de mensagens ao Kafka, oferecendo:
- Métodos para enviar mensagens simples ou com headers.
- Conversão de documentos (objetos) para JSON via `MapperService`.
- Tratamento de respostas e exceções durante o envio, com tempo limite para evitar bloqueios.

### Aspectos e Correlação de Logs

Utilizando AOP, a biblioteca fornece:
- A anotação `@AddCorrelator` para métodos que precisam ter um identificador de correlação inserido nos logs.
- A classe `CorrelatorAop` intercepta a execução de métodos anotados e adiciona (ou propaga) um ID de correlação, facilitando o rastreamento da mensagem durante todo o fluxo.

### Processamento Concorrente

Os consumidores processam mensagens de forma assíncrona através de um `ExecutorService` que utiliza _virtual threads_ para:
- Aumentar a escalabilidade.
- Permitir o processamento paralelo de múltiplas mensagens sem bloquear o thread principal.

---

## Exemplos de Uso

### Exemplo Completo de um Serviço de Ingestão

```java
import br.gft.template.demo.events.IngestService;
import br.gft.template.demo.events.kafka.concurrent.config.KafkaIngestConsumer;
import org.springframework.stereotype.Service;

@Service
@KafkaIngestConsumer(
    topics = {"orders.topic"},
    groupId = "orders-group",
    retryTopic = "orders.retry.topic",
    dlqTopic = "orders.dlq.topic",
    retryAttempts = 3,
    validateEvent = true
)
public class OrderIngestService implements IngestService<Order> {

    @Override
    public Order ingest(Order order) {
        // Lógica de processamento da ordem
        System.out.println("Processando a ordem: " + order);
        // Possível persistência ou outra operação
        return order;
    }
}
```

### Exemplo de Objeto de Domínio

```java
public class Order {
    private String id;
    private String customer;
    private double amount;

    // Getters, setters e, opcionalmente, anotações de validação (por exemplo, @NotNull, @Size, etc.)
}
```

### Exemplo de Conversor Personalizado

```java
import br.gft.template.demo.events.Converter;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class OrderConverter implements Converter<Order> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Order convert(String value) {
        try {
            return objectMapper.readValue(value, Order.class);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao converter a mensagem para Order", e);
        }
    }
}
```

### Exemplo de Handler de Pré-Inserção

```java
import br.gft.template.demo.events.HandlerBeforeInsert;
import org.springframework.stereotype.Component;

@Component
public class OrderBeforeInsertHandler implements HandlerBeforeInsert<Order> {

    @Override
    public void handle(Order order, Object... args) {
        // Realize ações como auditoria, enriquecimento de dados, etc.
        System.out.println("Antes de inserir a ordem: " + order);
    }
}
```

---

## Contribuindo

Contribuições são bem-vindas! Se você encontrou um problema, tem sugestões ou deseja melhorar a biblioteca, por favor:

1. Abra uma _issue_ descrevendo o problema ou sugestão.
2. Envie um _pull request_ com suas melhorias.

---

## Licença

Este projeto está licenciado sob os termos da [MIT License](LICENSE).

---

## Considerações Finais

A **Data Sync Kafka Library** oferece uma solução robusta e extensível para a ingestão de eventos via Kafka, abstraindo a complexidade da configuração e do processamento. Com suporte a handlers customizados, conversores e tratamento automático de erros (com retry e DLQ), ela permite que você se concentre na lógica de negócio e garanta um fluxo de dados consistente e monitorado em sua aplicação.

Explore, adapte e contribua para tornar essa ferramenta ainda mais completa!
