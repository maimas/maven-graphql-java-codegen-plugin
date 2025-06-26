# Type Mapping Changes

## Updated Code Generation for "ID" Type as String, Int as an Integer

### Changes Made
- Modified the `resolveType` function in `functions.ftl` to properly map GraphQL types to Java types:
  - Changed the handling of "ID" type to return "String" instead of "id"
  - Changed the handling of "Int" type to return "Integer" instead of "int"
  - Kept the handling of other primitive types (boolean, string, date, float) unchanged

### Rationale
- GraphQL "ID" type is typically represented as a String in Java
- GraphQL "Int" type is better represented as the Java wrapper class "Integer" rather than the primitive "int"
- These changes ensure proper type mapping between GraphQL and Java types

### Files Modified
- `W:\GitHub\maven-graphql-java-codegen-plugin\src\main\resources\java\utils\functions.ftl`

### Testing
- Ran existing tests to verify that the changes don't break existing functionality
- All enabled tests pass with the new type mapping implementation