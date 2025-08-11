package com.maimas.config;

import com.maimas.graphql.generator.UserConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

public class UserConfigTest {

    @Test
    void headers_parsing_valid_and_malformed() {
        UserConfig cfg = new UserConfig();
        cfg.setHeaders(new String[]{
                "Authorization: Bearer abc",
                "X-Empty-Value:",
                " NoName : value ",
                "MalformedNoColon",
                null
        });

        HashMap<String, String> headers = cfg.getHttpHeaders();
        // Should include valid ones, trimmed, including empty values
        Assertions.assertEquals("Bearer abc", headers.get("Authorization"));
        Assertions.assertTrue(headers.containsKey("X-Empty-Value"));
        Assertions.assertEquals("", headers.get("X-Empty-Value"));
        // " NoName : value " -> key trimmed to "NoName"
        Assertions.assertEquals("value", headers.get("NoName"));
        // Malformed should be skipped
        Assertions.assertFalse(headers.containsKey("MalformedNoColon"));
    }

    @Test
    void headers_array_immutability() {
        UserConfig cfg = new UserConfig();
        String[] arr = new String[]{"A: a"};
        cfg.setHeaders(arr);
        arr[0] = "B: b"; // mutate original
        // getHeaders returns a clone
        String[] clone = cfg.getHeaders();
        Assertions.assertNotNull(clone);
        Assertions.assertEquals(1, clone.length);
        Assertions.assertEquals("A: a", clone[0]);
    }

    @Test
    void validate_happy_path_and_failures() {
        UserConfig cfg = new UserConfig();
        cfg.setUrl("http://localhost:" + 1234 + "/graphql");
        cfg.setResultClassName("MyApi");
        cfg.setResultClassPackage("com.example.api");
        cfg.setDir("./target/tmp/");
        cfg.setConnectTimeoutMs(0);
        cfg.setSocketTimeoutMs(0);
        cfg.setMaxRetries(0);
        cfg.setRetryBackoffMs(0);

        // should not throw
        Assertions.assertDoesNotThrow(cfg::validate);

        // invalid URL
        UserConfig badUrl = new UserConfig();
        badUrl.setUrl("localhost");
        badUrl.setResultClassName("MyApi");
        badUrl.setResultClassPackage("com.example.api");
        Assertions.assertThrows(IllegalArgumentException.class, badUrl::validate);

        // invalid identifier
        UserConfig badName = new UserConfig();
        badName.setUrl("http://example.com");
        badName.setResultClassName("123Bad");
        badName.setResultClassPackage("com.example.api");
        Assertions.assertThrows(IllegalArgumentException.class, badName::validate);

        // invalid package
        UserConfig badPkg = new UserConfig();
        badPkg.setUrl("http://example.com");
        badPkg.setResultClassName("Ok");
        badPkg.setResultClassPackage("com..example");
        Assertions.assertThrows(IllegalArgumentException.class, badPkg::validate);

        // negative timeouts
        UserConfig badTimeout = new UserConfig();
        badTimeout.setUrl("http://example.com");
        badTimeout.setResultClassName("Ok");
        badTimeout.setResultClassPackage("com.example");
        badTimeout.setConnectTimeoutMs(-1);
        Assertions.assertThrows(IllegalArgumentException.class, badTimeout::validate);
    }
}
