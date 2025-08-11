package com.maimas.schema.validator;

import com.maimas.graphql.schema.processor.CodeValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CodeValidatorTest {

    @Test
    void validate_valid_code_passes() {
        String code = "package com.example;\n\npublic class Ok {\n    private int a = 1;\n    public String s = \"x\";\n}\n";
        Assertions.assertTrue(CodeValidator.validate(code));
    }

    @Test
    void validate_missing_package_fails() {
        String code = "public class Ok { }";
        Assertions.assertFalse(CodeValidator.validate(code));
    }

    @Test
    void validate_unbalanced_braces_fails() {
        String code = "package com.example;\npublic class Bad {"; // missing closing brace
        Assertions.assertFalse(CodeValidator.validate(code));
    }

    @Test
    void validate_missing_semicolon_fails() {
        String code = "package com.example;\npublic class Bad { int a = 5 }"; // missing semicolon
        Assertions.assertFalse(CodeValidator.validate(code));
    }
}
