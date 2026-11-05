package com.sift.umlplugin;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class PlantUmlGenerator {
    private PlantUmlGenerator() {
    }

    public static String generate(String title, List<ClassMetadata> classes) {
        String safeTitle = SecurityValidator.validateTitle(title, "Project Diagram");
        Map<String, List<ClassMetadata>> packages = new LinkedHashMap<>();
        for (ClassMetadata clazz : classes) {
            packages.computeIfAbsent(clazz.getPackageName(), key -> new ArrayList<>())
                    .add(clazz);
        }

        StringBuilder builder = new StringBuilder();
        builder.append("@startuml\n");
        builder.append("title ").append(safeTitle).append("\n");
        builder.append("skinparam classAttributeIconSize 0\n");
        builder.append("hide circle\n\n");

        List<String> packageNames = new ArrayList<>(packages.keySet());
        packageNames.sort(String::compareTo);

        for (String packageName : packageNames) {
            String sanitizedPackage = SecurityValidator.validateIdentifier(packageName, "default");
            builder.append("package \"")
                    .append(sanitizedPackage)
                    .append("\" {\n");

            List<ClassMetadata> group = packages.get(packageName);
            group.sort(Comparator.comparing(ClassMetadata::getSimpleName));
            for (ClassMetadata clazz : group) {
                String safeSimpleName = SecurityValidator.validateIdentifier(clazz.getSimpleName(), "Class");
                builder.append("  class ")
                        .append(safeSimpleName)
                        .append(" <<")
                        .append(normalizeStereotype(clazz.getStereotype()))
                        .append(">>\n");
            }

            builder.append("}\n\n");
        }

        for (ClassMetadata clazz : classes) {
            String sourceName = SecurityValidator.validateIdentifier(clazz.getSimpleName(), "Class");
            for (String dependency : clazz.getDependencies().stream().sorted().toList()) {
                if (dependency == null || dependency.isBlank()) {
                    continue;
                }
                builder.append(sourceName)
                        .append(" --> ")
                        .append(SecurityValidator.validateIdentifier(dependency, "Dependency"))
                        .append("\n");
            }
        }

        builder.append("\n@enduml\n");
        return builder.toString();
    }

    private static String normalizeStereotype(String value) {
        if (value == null || value.isBlank()) return "class";
        return switch (value) {
            case "restController" -> "restController";
            case "controller" -> "controller";
            case "service" -> "service";
            case "repository" -> "repository";
            case "configuration" -> "configuration";
            case "component" -> "component";
            case "entity" -> "entity";
            case "application" -> "application";
            default -> "class";
        };
    }
}
