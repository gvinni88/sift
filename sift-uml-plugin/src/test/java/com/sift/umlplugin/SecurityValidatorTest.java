package com.sift.umlplugin;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SecurityValidatorTest {
    @Test
    void rejectsPlantUmlDirectivesInTitle() {
        assertThrows(IllegalArgumentException.class, () -> SecurityValidator.validateTitle("Demo !include /etc/passwd", "fallback"));
    }

    @Test
    void rejectsPathTraversalOutsideProjectRoot() throws IOException {
        Path projectRoot = Path.of("/tmp/project").toAbsolutePath().normalize();
        Path outside = projectRoot.resolve("../outside").toAbsolutePath().normalize();
        assertThrows(IOException.class, () -> SecurityValidator.resolveProjectPath(outside.toString(), "sift.sourceDirectory", projectRoot));
    }

    @Test
    void allowsSafeIdentifiers() {
        String safe = SecurityValidator.validateIdentifier("My.Safe_Class", "fallback");
        assertTrue(safe.contains("My"));
    }
}
