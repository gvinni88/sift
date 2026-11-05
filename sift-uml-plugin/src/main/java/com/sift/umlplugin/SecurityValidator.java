package com.sift.umlplugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

final class SecurityValidator {
    private static final String[] BLOCKED_DIRECTIVES = {
            "!include", "!theme", "!pragma", "!define", "@startuml", "@enduml", "newpage",
            "left to right direction", "skinparam", "hide circle"
    };

    private SecurityValidator() {
    }

    static Path resolveProjectPath(String configuredPath, String configName, Path projectRoot) throws IOException {
        if (configuredPath == null || configuredPath.isBlank()) {
            return projectRoot.toAbsolutePath().normalize();
        }

        if (configuredPath.contains("..")) {
            throw new IOException("The '" + configName + "' setting must not contain parent traversal segments: " + configuredPath);
        }

        Path candidate = Path.of(configuredPath);
        if (!candidate.isAbsolute()) {
            candidate = projectRoot.resolve(candidate).normalize();
        }

        Path absoluteCandidate = candidate.toAbsolutePath().normalize();
        Path realProjectRoot = projectRoot.toAbsolutePath().normalize();
        Path realCandidate = absoluteCandidate;
        if (Files.exists(realCandidate)) {
            realCandidate = realCandidate.toRealPath();
        }

        if (!realCandidate.startsWith(realProjectRoot)) {
            throw new IOException("The '" + configName + "' setting resolves outside the project directory: " + configuredPath);
        }

        return absoluteCandidate;
    }

    static String validateTitle(String value, String fallback) {
        if (value == null || value.isBlank()) {
            return fallback;
        }
        String sanitized = normalizeText(value, 200);
        if (containsBlockedDirective(sanitized)) {
            throw new IllegalArgumentException("Diagram titles must not contain PlantUML directives or control sequences.");
        }
        return sanitized;
    }

    static String validateIdentifier(String value, String fallback) {
        if (value == null || value.isBlank()) {
            return fallback;
        }
        String candidate = value.trim();
        if (containsBlockedDirective(candidate)) {
            throw new IllegalArgumentException("Diagram identifiers must not contain PlantUML directives or control sequences.");
        }

        StringBuilder builder = new StringBuilder(candidate.length());
        for (int i = 0; i < candidate.length(); i++) {
            char ch = candidate.charAt(i);
            if ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z')
                    || (ch >= '0' && ch <= '9') || ch == '_' || ch == '.') {
                builder.append(ch);
            } else {
                builder.append('_');
            }
        }

        String sanitized = builder.toString().replaceAll("_+", "_").replaceAll("(^\\.|\\.$)", "");
        if (sanitized.isBlank()) {
            return fallback;
        }
        return sanitized;
    }

    private static String normalizeText(String value, int maxLength) {
        String normalized = value
                .replace('\\', ' ')
                .replace('"', ' ')
                .replace('\r', ' ')
                .replace('\n', ' ')
                .replace('\t', ' ')
                .replace('\0', ' ');

        StringBuilder builder = new StringBuilder(normalized.length());
        for (int i = 0; i < normalized.length(); i++) {
            char ch = normalized.charAt(i);
            if (Character.isISOControl(ch)) {
                builder.append(' ');
            } else {
                builder.append(ch);
            }
        }

        String collapsed = builder.toString().replaceAll("\\s+", " ").trim();
        if (collapsed.length() > maxLength) {
            collapsed = collapsed.substring(0, maxLength).trim();
        }
        return collapsed;
    }

    private static boolean containsBlockedDirective(String value) {
        String lower = value == null ? "" : value.toLowerCase(Locale.ROOT);
        for (String token : BLOCKED_DIRECTIVES) {
            if (lower.contains(token.toLowerCase(Locale.ROOT))) {
                return true;
            }
        }
        return false;
    }
}
