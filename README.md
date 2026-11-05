# SIFT

SIFT is an architecture intelligence tool for Java projects. It reads your source code and turns it into clear architecture artifacts that leaders, engineers, and reviewers can understand without having to inspect every class manually.

In plain English: SIFT acts like a digital architect's assistant. It looks at your codebase, maps the moving parts, shows how they interact, and records the key design decisions in a way that is easy to review and govern.

This repository contains:
- the Maven plugin that performs the analysis
- a demo project showing the plugin in action
- enterprise-ready security and release controls

Side note: this is built on plain Java and does not require any AI plugin or model integration to operate. It is a standard Maven-based architecture-analysis tool that works in normal Java build pipelines.

## Why this matters

Most organizations do not struggle because they lack code. They struggle because the architecture is hidden inside implementation details.

SIFT helps teams answer questions such as:
- What are the main application components?
- Which classes depend on which other classes?
- What does a typical business flow look like?
- What architectural decisions are being made as the system evolves?

That makes it useful for:
- architecture reviews
- onboarding new engineers
- technical due diligence
- modernization planning
- release readiness and governance

## What the plugin generates

The plugin inspects code under `src/main/java` and generates PlantUML files and ADR documents.

- A UML diagram shows the system landscape
- A class diagram shows relationships between classes
- A sequence diagram shows the flow of actions over time
- ADRs explain the rationale behind major design choices

The generated output is placed under `target/sift` by default.

## The four plugin commands

Each command solves a different communication problem. Think of them as four lenses on the same system.

### 1) `generate-uml`

Business meaning: "Give me the big-picture system map."

This command creates a high-level UML view of the application, highlighting major Java components such as controllers, services, repositories, entities, and configuration classes when they are present.

Use it when:
- you want an executive-friendly overview
- you need to understand the overall shape of the application
- you want a quick visual summary before drilling into code

Typical output:
- `target/sift/project-diagram.puml`

### 2) `classdiagram`

Business meaning: "Show me how the pieces connect."

This command creates a class-level dependency diagram. It is more granular than the high-level UML view and is best for understanding how classes collaborate and where responsibilities are split.

Use it when:
- reviewing architecture for coupling and complexity
- identifying central classes or bottlenecks
- planning refactors or module boundaries

Typical output:
- `target/sift/project-class-diagram.puml`

### 3) `sequencediagram`

Business meaning: "Walk me through the customer journey or internal workflow."

This command creates a sequence diagram to show how a flow moves across objects over time. It is ideal for understanding step-by-step interactions inside the application.

Use it when:
- explaining a critical business workflow
- debugging a multi-step process
- presenting how a feature works to product or leadership teams

Typical output:
- `target/sift/project-sequence-diagram.puml`

### 4) `adr`

Business meaning: "Document the decisions behind the design."

This command generates Architecture Decision Records (ADRs). ADRs capture the context, responsibilities, and trade-offs around a component or feature area. They are especially valuable when architecture decisions need to be understood later, especially by new team members or stakeholders.

Use it when:
- documenting important design decisions
- clarifying responsibilities within a module
- creating durable knowledge for future teams

Typical output:
- `target/sift/adr/ADR-0001-root.md`

## Quick-start example

Add the plugin to a Maven project:

```xml
<build>
  <plugins>
    <plugin>
      <groupId>com.scrupulouslysifted.sift</groupId>
      <artifactId>sift-uml-plugin</artifactId>
      <version>1.0.0-SNAPSHOT</version>
      <executions>
        <execution>
          <id>generate-uml</id>
          <phase>process-classes</phase>
          <goals>
            <goal>generate-uml</goal>
          </goals>
          <configuration>
            <title>My Application Architecture</title>
            <outputDirectory>${project.build.directory}/sift</outputDirectory>
          </configuration>
        </execution>
      </executions>
    </plugin>
  </plugins>
</build>
```

Then run:

```bash
mvn process-classes
```

Or invoke a goal directly:

```bash
mvn com.scrupulouslysifted.sift:sift-uml-plugin:1.0.0-SNAPSHOT:generate-uml
mvn com.scrupulouslysifted.sift:sift-uml-plugin:1.0.0-SNAPSHOT:classdiagram
mvn com.scrupulouslysifted.sift:sift-uml-plugin:1.0.0-SNAPSHOT:sequencediagram
mvn com.scrupulouslysifted.sift:sift-uml-plugin:1.0.0-SNAPSHOT:adr
```

## Common configuration options

The plugin supports a few useful configuration fields:

- `sift.sourceDirectory` — the Java source root to inspect. Default: `${project.basedir}/src/main/java`
- `sift.outputDirectory` — where generated files are written. Default: `${project.build.directory}/sift`
- `sift.title` — title displayed in diagrams
- `sift.includePackages` — limit analysis to selected packages
- `sift.excludePackages` — ignore specific packages
- `sift.format` — output format for diagrams; `puml` is the default

Example:

```xml
<configuration>
  <title>Fleet Operations Platform</title>
  <sourceDirectory>${project.basedir}/src/main/java</sourceDirectory>
  <outputDirectory>${project.build.directory}/sift</outputDirectory>
  <includePackages>
    <includePackage>com.company.ordering</includePackage>
    <includePackage>com.company.billing</includePackage>
  </includePackages>
</configuration>
```

## What makes this project valuable

This tool helps teams move from code-as-implementation to code-as-communication.

It turns technical details into:
- architecture narratives
- dependency visibility
- easier onboarding
- fewer misunderstandings between teams
- better governance of the software landscape

For a CTO, the value is straightforward: faster understanding, lower risk, and better decision-making. For an engineer, it means less time reverse-engineering the system and more time improving it.

## Repository layout

```text
sift/
├── sift-uml-plugin/      # Maven plugin implementation
├── sift-demo/            # Example usage and generated outputs
├── docs/                 # Supporting design and security documents
├── pom.xml               # Parent Maven build
├── LICENSE               # Project license
├── SECURITY.md           # Security policies
└── README.md             # This guide
```

## Security and release posture

This project includes enterprise-grade controls such as:
- Java and Maven enforcement
- dependency vulnerability scanning
- SBOM generation
- release signing support

For details, see `docs/enterprise-security.md`.

## In one sentence

SIFT converts a codebase into architecture clarity: diagrams for understanding, ADRs for decision capture, and governance-ready artifacts for engineering organizations.
