# Maven GraphQL Java Codegen Plugin - Improvement Tasks

This document outlines tasks for improving the Maven GraphQL Java Codegen Plugin project.

The tasks are grouped by category and include suggested priority (P0 critical, P1 high, P2 normal), effort estimate (S/M/L), and acceptance criteria (AC) to make them actionable.

# Task execution workflow
1. Tasks have to be implemented iteratively based on the list below
2. After completing each task, it needs to be marked as done by changing the checkbox from [ ] to [x]

# Documentation Tasks

## README Updates

### [x] Fix artifact ID in README
- **Priority**: P0
- **Effort**: S
- **AC**: 
  - Update the artifact ID in the README from "graphql-codegen" to "graphql-codegen-maven-plugin"
  - Ensure all code examples in the README use the correct artifact ID

### [x] Enhance code validation documentation
- **Priority**: P1
- **Effort**: M
- **AC**:
  - Add a dedicated section about the CodeValidator in the README
  - Document the types of validation performed (basic syntax, balanced braces, semicolons)
  - Explain how validation errors are reported and how to troubleshoot them
  - Update the troubleshooting section with common validation errors and solutions

### [x] Improve template customization documentation
- **Priority**: P1
- **Effort**: M
- **AC**:
  - Expand the "Extending to additional languages" section with more details about TemplateRegistry
  - Add examples of how to register custom templates programmatically
  - Document all available template properties and their meanings
  - Include a complete example of creating a custom template for a new language

### [ ] Update network configuration documentation
- **Priority**: P2
- **Effort**: S
- **AC**:
  - Clarify the retry behavior for network errors
  - Document all network configuration options in detail
  - Add examples of common network configuration scenarios

### [ ] Add examples for TypeScript generation
- **Priority**: P2
- **Effort**: M
- **AC**:
  - Add examples of using the plugin with TypeScript
  - Include sample TypeScript output
  - Document TypeScript-specific configuration options

### [x] Update Java version requirements
- **Priority**: P1
- **Effort**: S
- **AC**:
  - Clearly state Java 17 requirement in the README prerequisites section
  - Update any outdated Java version references throughout the document

### [ ] Improve README structure and organization
- **Priority**: P2
- **Effort**: M
- **AC**:
  - Add a table of contents at the beginning of the README
  - Organize sections in a logical flow (introduction, installation, configuration, usage, advanced topics, troubleshooting)
  - Add section headers and improve formatting for better readability
  - Ensure consistent formatting throughout the document

### [ ] Add examples of common use cases
- **Priority**: P2
- **Effort**: M
- **AC**:
  - Add examples for common GraphQL operations (queries, mutations, subscriptions)
  - Include examples with complex types and nested objects
  - Show how to handle GraphQL variables and arguments
  - Demonstrate integration with popular Java frameworks (Spring Boot, etc.)

# Code Improvement Tasks

### [x] Enhance error handling and reporting
- **Priority**: P1
- **Effort**: M
- **AC**:
  - Improve error messages to be more descriptive and actionable
  - Add more detailed logging throughout the code generation process
  - Implement better handling of GraphQL schema errors
  - Add option to output validation errors to a file

### [ ] Add support for additional languages
- **Priority**: P2
- **Effort**: L
- **AC**:
  - Implement Kotlin template support
  - Add Scala template support
  - Create a mechanism for community-contributed language templates
  - Document the process for adding new language support

### [ ] Improve code validation
- **Priority**: P2
- **Effort**: M
- **AC**:
  - Enhance the CodeValidator to detect more types of errors
  - Add support for language-specific validation rules
  - Implement warning levels (error, warning, info) for validation issues
  - Add option to ignore specific validation rules

### [ ] Add integration tests
- **Priority**: P1
- **Effort**: L
- **AC**:
  - Create integration tests with real GraphQL schemas
  - Test against different GraphQL server implementations
  - Add tests for error conditions and edge cases
