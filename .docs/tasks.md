# Maven GraphQL Java Codegen Plugin - Improvement Tasks

This document outlines tasks for improving the Maven GraphQL Java Codegen Plugin project.

## Code Improvements

- [ ] **Refactor FTL fragments generation**
  - [ ] Split monolithic template into smaller, reusable fragments
  - [ ] Create separate template files for different types (enums, objects, queries, etc.)
  - [ ] Implement template inheritance for common patterns
  - [ ] Improve handling of complex nested types in fragments
  - [ ] Implement better error handling in template processing
  - [ ] Add validation for generated code
  - [ ] Create a template registry for managing multiple template versions
  - [ ] Support for different programming languages through template configuration, limit to java only for now
