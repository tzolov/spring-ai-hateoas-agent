# Spring AI HATEOAS Agent

A Spring AI implementation of a [HATEOAS-aware autonomous agent](https://github.com/openknowledge/hateoas-agent) that navigates REST APIs through hypermedia discovery.

## Overview

This CLI application demonstrates an AI agent that interacts with HATEOAS APIs by dynamically discovering endpoints through `_links` and `_templates` rather than hardcoding API paths. The agent uses Spring AI's tool-calling capabilities to make HTTP requests based on hypermedia responses.

**Features**
- Dynamic API navigation via HATEOAS hypermedia links
- Supports GET, POST, PUT, DELETE operations as AI tools
- Works with Claude, OpenAI, or Google GenAI models
- Interactive CLI for conversational API exploration

**Requirements**
- Java 17+
- API key for your chosen AI provider
- A HATEOAS-compliant REST API to interact with (default: `http://localhost:8080/`)

**Project Structure**

```
src/main/java/com/example/spring/ai/hateoas/
├── AgentApplication.java   # Main CLI application
└── RestTools.java          # HTTP tools for AI agent

src/main/resources/
├── application.properties  # Configuration
└── system_prompt.md        # Agent instructions
```

## Configuration

Set your AI provider API key:

```bash
export ANTHROPIC_API_KEY=your-key  # For Claude (default)
# or
export OPENAI_API_KEY=your-key     # For OpenAI
# or
export GOOGLE_CLOUD_PROJECT=your-project  # For Gemini
```

Configure the API entry point in `application.properties`:

```properties
hateoas.agent.entrypoint=http://localhost:8080/
```

## How It Works

## Start Spring Restbucks

In order to test the agent, we need a HATEOAS compatible API to interact with. You can use i.e. [Spring Restbucks](https://github.com/odrotbohm/spring-restbucks).
```
git clone https://github.com/odrotbohm/spring-restbucks.git
cd spring-restbucks
cd server
mvn clean package
java -jar ./target/restbucks-1.0.0-SNAPSHOT.jar
```
After starting Spring Restbucks you can use the HAL-Explorer to add some data (i.e. drinks or orders) if you like. If url of the API is not `http://localhost:8080/`, please update the variable `ENTRY_POINT` in `prompts.py`.


### Start the Agent

```bash
./mvnw spring-boot:run
```

1. The agent starts from the configured entry point URL
2. Discovers available actions through `_links` in responses
3. Uses `_templates` to understand request body structure
4. Only performs actions explicitly allowed by the API
5. Never guesses or invents URLs, methods, or data structures

### Example Prompts
* "How many orders are there?"
* "What is the total amount to be paid?"
* "Which drinks do we have available?"
* "Create a new drink called MySpecialDrink! It should cost 50 cent."
* "Update the price of MySpecialDrink. It should cost 2 Euro now!" 
* "Delete the MySpecialDrink. We do not sell it anymore!"


#### Example Output

```
> USER: What is the total amount to be paid?

USER: 
 - SYSTEM: You are a HATEOAS ag...
 - TOOLS: ["post_request","put_request","delete_request","get_request"]
 - TEXT: What is the total amount to be paid?

ASSISTANT: 
 - TEXT: I'll help you find the total amount to be paid by exploring the API. Let me start from the entry point.
 - TOOL-CALL: get_request ({"url":"http://localhost:8080/"})

USER: 
 - SYSTEM: You are a HATEOAS ag...
 - TOOLS: ["post_request","put_request","delete_request","get_request"]
 - TOOL-RESPONSE: get_request: {"statusCode":200,"body":{"_links":{"restbucks:drinks":{"href":"http://localhost:8080/drinks"},"restbucks:statistics":{"href":"http://localhost:8080/statistics"},"restbucks:orders":{"href":"http://localhost:8080/orders{?page,size,sort*,projection}","templated":true,"title":"Access or create orders"}...

ASSISTANT: 
 - TEXT: Good! I can see the API entry point. There's a link to orders which might contain payment information. Let me explore the orders endpoint.
 - TOOL-CALL: get_request ({"url":"http://localhost:8080/orders"})

USER: 
 - SYSTEM: You are a HATEOAS ag...
 - TOOLS: ["post_request","put_request","delete_request","get_request"]
 - TOOL-RESPONSE: get_request: {"statusCode":200,"body":{"_embedded":{"restbucks:orders":[{"_links":{"self":{"href":"http://localhost:8080/orders/a4d953d2-ca53-49a5-a0bb-91a1f97fdc9e"},"restbucks:order":{"href":"http://localhost:8080/orders/a4d953d2-ca53-49a5-a0bb-91a1f97fdc9e{?projection}","templated":true,"title":"An order"}},"...

ASSISTANT: 
 - TEXT: I can see there are 2 orders with "Payment expected" status. Let me get the details of each order to see the amounts. I'll start with the first one:
 - TOOL-CALL: get_request ({"url":"http://localhost:8080/orders/a4d953d2-ca53-49a5-a0bb-91a1f97fdc9e"})
 - TOOL-CALL: get_request ({"url":"http://localhost:8080/orders/7d9b6074-495b-4101-bee6-4415981da73b"})

USER: 
 - SYSTEM: You are a HATEOAS ag...
 - TOOLS: ["post_request","put_request","delete_request","get_request"]
 - TOOL-RESPONSE: get_request: {"statusCode":200,"body":{"_links":{"restbucks:order":{"href":"http://localhost:8080/orders/a4d953d2-ca53-49a5-a0bb-91a1f97fdc9e{?projection}","templated":true,"title":"An order"},"restbucks:cancel":{"href":"http://localhost:8080/orders/a4d953d2-ca53-49a5-a0bb-91a1f97fdc9e","title":"Cancel an order"...
 - TOOL-RESPONSE: get_request: {"statusCode":200,"body":{"_links":{"restbucks:order":{"href":"http://localhost:8080/orders/7d9b6074-495b-4101-bee6-4415981da73b{?projection}","templated":true,"title":"An order"},"restbucks:cancel":{"href":"http://localhost:8080/orders/7d9b6074-495b-4101-bee6-4415981da73b","title":"Cancel an order"...

ASSISTANT: 
 - TEXT: Perfect! I found the order details with prices. Based on the API responses:

**Order 1:**
- Item: Java Chip (Large, Whole milk)
- Price: EUR 4.20
- Status: Payment expected

**Order 2:**
- Item: Cappu...

> ASSISTANT: Perfect! I found the order details with prices. Based on the API responses:

**Order 1:**
- Item: Java Chip (Large, Whole milk)
- Price: EUR 4.20
- Status: Payment expected

**Order 2:**
- Item: Cappuchino (Large, Whole milk)
- Price: EUR 3.20
- Status: Payment expected

**Total amount to be paid: EUR 7.40**

This is calculated from:
- Order 1: EUR 4.20
- Order 2: EUR 3.20
- **Total: EUR 7.40**

Both orders have the status "Payment expected" and have payment links available, indicating they are awaiting payment.
```