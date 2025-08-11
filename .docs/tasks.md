# Maven GraphQL Java Codegen Plugin - Improvement Tasks

This document outlines tasks for improving the Maven GraphQL Java Codegen Plugin project.

The tasks are grouped by category and include suggested priority (P0 critical, P1 high, P2 normal), effort estimate (S/M/L), and acceptance criteria (AC) to make them actionable.

# Task execution workflow
1. Tasks have to be implemented iteratively based on the list below
2. After completing each task, it needs to be marked as done by changing the checkbox from [ ] to [x]


## 1. Code Generation Templates (FreeMarker)
- [x] Fix Java LIST type mapping (P0, S)
  - Problem: functions.ftl resolveType returns innerType for LIST, not List<innerType>, which likely breaks generics in generated code.
  - Task: Update resolveType to return "List<...>" for LIST when generating Java and align getFragmentType/createFragmentInstance accordingly.
  - AC: Generated Java uses List<T> consistently for list fields; compilation succeeds in sample project; unit snapshot tests updated.

- [ ] Nullability and Optional handling (P1, M)
  - Task: Map NON_NULL to non-Optional types and optional args to Optional<T> consistently; ensure buildArgumentMethods and buildMethodArguments align.
  - AC: For NON_NULL args, no Optional wrapper; for nullable args, Optional<T> is used; tests cover both cases.

- [ ] Scalar mappings completeness (P1, S)
  - Task: Extend scalar mapping in resolveType to include Long, BigDecimal, OffsetDateTime, etc., and allow overrides via templateConfig.
  - AC: Configurable mapping works; default mapping documented.

- [ ] Fragment generation for collections (P1, M)
  - Task: Revisit getFragmentType/createFragmentInstance for LIST fields to avoid returning ArrayList where Fragment expected; design consistent fragment model.
  - AC: API remains ergonomic and types compile; examples updated.

- [ ] Template header and imports hygiene (P2, S)
  - Task: Ensure unused imports are minimized; add necessary imports for collections when lists are used.
  - AC: Generated files have no unused import warnings in default templates.

## 2. TemplateProcessor and Pipeline
- [x] Enforce/Improve validation (P1, S)
  - Task: Re-enable validation failure throw in TemplateProcessor when CodeValidator fails; make it configurable via UserConfig (failOnValidationError boolean).
  - AC: By default, invalid code fails the build; flag can disable for migration.

- [x] Resource handling and IO (P1, S)
  - Task: Use try-with-resources for ByteArrayOutputStream/Writer; ensure UTF-8 explicitly; avoid FileUtils if not needed; handle mkdirs result.
  - AC: No resource leaks; error paths covered.

- [ ] Logging (P1, S)
  - Task: Replace System.err/printStackTrace with Maven logger (getLog()) where available or slf4j for non-Mojo classes; propagate meaningful exceptions.
  - AC: Uniform logging strategy; no direct System.out/err.

- [x] Builders content loading (P2, S)
  - Task: Validate resource presence for <Language>_GraphQL_Builders.txt; add clear error if missing.
  - AC: Meaningful error when resource not found.

## 3. SchemaFetcher robustness
- [x] Timeouts and retries (P0, M)
  - Task: Configure request/connect timeouts; add limited retry with backoff for transient 5xx/IO errors.
  - AC: Network hiccups handled; configurable via UserConfig.

- [x] Proper resource closing (P0, S)
  - Task: Use try-with-resources for CloseableHttpClient/Response; ensure InputStream body is repeatable or buffered.
  - AC: No resource leaks; static analysis clean.

- [ ] Error reporting (P1, S)
  - Task: Include status code and body snippet; parse GraphQL errors if present; attach headers info redacted.
  - AC: Exceptions include actionable diagnostics.

## 4. UserConfig improvements
- [x] Fix enum typo (P0, S)
  - Task: Selector.Typescript currently labeled "Typescrypt"; correct to "TypeScript"; consider backward compatibility.
  - AC: toString and getName return correct spelling; tests updated.

- [ ] Validation helpers (P1, S)
  - Task: Add validate() to check url non-empty, class/package identifiers valid, dir path normalized.
  - AC: Mojo fails early on invalid config with clear message.

- [ ] Headers parsing robustness (P1, S)
  - Task: Parse on first ':' only; trim; skip invalid; log warnings; support empty values.
  - AC: No IndexOutOfBounds on malformed header; tests added.

- [ ] Immutability and null-safety (P2, S)
  - Task: Copy arrays to lists or defensive copies; avoid exposing internal arrays.
  - AC: Public API safer; tests cover copies.

- [ ] Extensibility for additional languages (P2, M)
  - Task: Allow template selection via TemplateRegistry; document how to add languages.
  - AC: New language can be plugged by adding config and resources.

## 5. Mojo (GQLCodeGeneratorMojo)
- [ ] Configuration validation and helpful logs (P1, S)
  - Task: Validate servers list non-empty; log each server index; catch and wrap exceptions with context.
  - AC: Clear, structured logs; build fails on invalid config before generation.

## 7. Testing
- [ ] Add unit tests for core components (P0, M)
  - Task: Tests for SchemaFetcher (with mock server), TemplateProcessor (with sample schema), UserConfig (headers parsing), CodeValidator.
  - AC: CI green; coverage for critical paths; remove skipTests in surefire.

- [ ] Snapshot/golden-file tests for templates (P1, M)
  - Task: Given a fixed schema JSON, assert generated code matches expected outputs.
  - AC: Deterministic outputs with reviewable snapshots.

## 8. Build, Quality, and CI
- [ ] Maven Enforcer and Java level (P1, S)
  - Task: Add maven-enforcer-plugin to enforce minimum Maven and Java; consider lowering source/target to 11 or 17 for wider compatibility.
  - AC: Clear failure if environment unsupported; documented in README.

- [ ] Static analysis and formatting (P1, S)
  - Task: Add Spotless (or fmt) and Checkstyle; configure basic rules; add to CI.
  - AC: Consistent formatting; PRs fail on violations.

- [ ] Dependency updates (P2, S)
  - Task: Bump dependencies to latest stable where safe (jackson, freemarker, maven plugin tools); consider Dependabot.
  - AC: Build remains green; no security alerts.

## 9. Documentation and DX
- [ ] README improvements (P1, S)
  - Task: Add configuration examples (servers list, headers), minimal/advanced usage, troubleshooting, and template customization guide.
  - AC: New users can run within minutes; examples verified.
