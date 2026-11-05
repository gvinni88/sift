package com.sift.umlplugin;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlantUmlGeneratorTest {
    @Test
    void rejectsDangerousTitleAndClassNames() {
        ClassMetadata metadata = new ClassMetadata(
                "com.example\";!include /etc/passwd",
                "Bad\"Class",
                "com.example\";!include /etc/passwd.Bad\"Class",
                "class",
                "controller",
                "class Bad\"Class { }"
        );
        metadata.addDependency("Other\nClass");

        assertThrows(IllegalArgumentException.class, () -> PlantUmlGenerator.generate("Demo\n!include /etc/passwd", List.of(metadata)));
    }

    @Test
    void acceptsSafeTitleAndClasses() {
        ClassMetadata metadata = new ClassMetadata(
                "com.example.safe",
                "SafeClass",
                "com.example.safe.SafeClass",
                "class",
                "service",
                "class SafeClass { }"
        );
        metadata.addDependency("OtherClass");

        String uml = PlantUmlGenerator.generate("Safe Project Diagram", List.of(metadata));

        assertTrue(uml.contains("title Safe Project Diagram"));
        assertTrue(uml.contains("SafeClass"));
        assertTrue(uml.contains("OtherClass"));
    }
}
