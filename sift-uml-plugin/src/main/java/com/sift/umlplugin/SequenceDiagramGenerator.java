package com.sift.umlplugin;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class SequenceDiagramGenerator {
    private SequenceDiagramGenerator() {
    }

    public static String generate(String title, List<ClassMetadata> classes) {
        List<ClassMetadata> sorted = new ArrayList<>(classes);
        sorted.sort(Comparator.comparing(ClassMetadata::getSimpleName));

        StringBuilder builder = new StringBuilder();
        builder.append("@startuml\n");
        builder.append("title ").append(sanitizeTitle(title)).append("\n");
        builder.append("actor User\n");

        Set<String> participants = new LinkedHashSet<>();
        for (ClassMetadata clazz : sorted) {
            String simpleName = sanitizeIdentifier(clazz.getSimpleName(), "Class");
            participants.add(simpleName);
            builder.append("participant ")
                    .append(simpleName)
                    .append(" as ")
                    .append(simpleName)
                    .append("\n");
        }

        if (participants.isEmpty()) {
            builder.append("@enduml\n");
            return builder.toString();
        }

        for (ClassMetadata clazz : sorted) {
            String sourceName = sanitizeIdentifier(clazz.getSimpleName(), "Class");
            for (String dependency : clazz.getDependencies().stream().sorted().toList()) {
                if (dependency == null || dependency.isBlank()) {
                    continue;
                }
                String targetName = sanitizeIdentifier(dependency, "Dependency");
                builder.append(sourceName)
                        .append("->")
                        .append(targetName)
                        .append(": invoke\n");
                builder.append(targetName)
                        .append("-->")
                        .append(sourceName)
                        .append(": response\n");
            }
        }

        builder.append("@enduml\n");
        return builder.toString();
    }

    private static String sanitizeTitle(String value) {
        if (value == null || value.isBlank()) {
            return "Project Sequence Diagram";
        }

        StringBuilder builder = new StringBuilder(value.length());
        for (int i = 0; i < value.length(); i++) {
            char ch = value.charAt(i);
            if (Character.isISOControl(ch) || ch == '\n' || ch == '\r' || ch == '\t') {
                builder.append(' ');
            } else if (Character.isLetterOrDigit(ch) || ch == ' ' || ch == '-' || ch == '_' || ch == '.' || ch == ':' || ch == '/' || ch == '(' || ch == ')' || ch == '[' || ch == ']' || ch == '{' || ch == '}') {
                builder.append(ch);
            } else {
                builder.append(' ');
            }
        }

        String sanitized = builder.toString().replaceAll("\\s+", " ").trim();
        return sanitized.isBlank() ? "Project Sequence Diagram" : sanitized;
    }

    private static String sanitizeIdentifier(String value, String fallback) {
        if (value == null || value.isBlank()) {
            return fallback;
        }

        StringBuilder builder = new StringBuilder(value.length());
        for (int i = 0; i < value.length(); i++) {
            char ch = value.charAt(i);
            if ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') || (ch >= '0' && ch <= '9') || ch == '_' || ch == '.') {
                builder.append(ch);
            } else {
                builder.append('_');
            }
        }

        String sanitized = builder.toString();
        sanitized = sanitized.replaceAll("_+", "_");
        sanitized = sanitized.replaceAll("(^\\.|\\.$)", "");
        sanitized = sanitized.replaceAll("\\.+", ".");
        return sanitized.isBlank() ? fallback : sanitized;
    }
}
