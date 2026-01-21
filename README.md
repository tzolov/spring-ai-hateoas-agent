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
