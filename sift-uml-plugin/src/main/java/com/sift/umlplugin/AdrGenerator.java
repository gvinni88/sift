package com.sift.umlplugin;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public final class AdrGenerator {
    private static final Pattern TYPE_DECLARATION_PATTERN =
            Pattern.compile("(?:public\\s+)?(?:final\\s+)?(?:abstract\\s+)?(?:class|interface|enum)\\s+(\\w+)");
    private static final Pattern METHOD_SIGNATURE_PATTERN =
            Pattern.compile("(?:public|protected|private)\\s+(?:static\\s+)?(?:[A-Za-z0-9_<>,\\[\\]\\?]+\\s+)+(\\w+)\\s*\\((.*?)\\)");

    private AdrGenerator() {
    }

    public static void generate(Path sourceRoot,
                                Path outputRoot,
                                String[] includePackages,
                                String[] excludePackages) throws IOException {
        if (sourceRoot == null || !Files.isDirectory(sourceRoot)) {
            return;
        }

        Path adrRoot = outputRoot.resolve("adr");
        Files.createDirectories(adrRoot);

        List<Path> directories = new ArrayList<>();
        try (Stream<Path> walk = Files.walk(sourceRoot)) {
            walk.filter(Files::isDirectory)
                    .sorted(Comparator.comparing(Path::toString))
                    .forEach(directories::add);
        }

        int index = 1;
        for (Path directory : directories) {
            List<ClassMetadata> classes = JavaProjectAnalyzer.analyze(directory, includePackages, excludePackages);
            if (classes.isEmpty() && !containsJavaFiles(directory)) {
                continue;
            }

            String directoryLabel = sourceRoot.relativize(directory).toString();
            String fileStem = toFileStem(directoryLabel.isBlank() ? "root" : directoryLabel);
            Path file = adrRoot.resolve(String.format("ADR-%04d-%s.md", index, fileStem));
            Files.writeString(file, buildAdr(directory, sourceRoot, directoryLabel, classes), StandardCharsets.UTF_8);
            index++;
        }

        if (index == 1) {
            Path file = adrRoot.resolve("ADR-0001-root.md");
            Files.writeString(file, buildAdr(sourceRoot, sourceRoot, "root", List.of()), StandardCharsets.UTF_8);
        }
    }

    private static boolean containsJavaFiles(Path directory) throws IOException {
        try (Stream<Path> files = Files.walk(directory, 1)) {
            return files.filter(path -> path.toString().endsWith(".java")).findAny().isPresent();
        }
    }

    private static String buildAdr(Path directoryPath,
                                  Path sourceRoot,
                                  String directoryLabel,
                                  List<ClassMetadata> classes) {
        String target = directoryLabel == null || directoryLabel.isBlank() ? "root" : directoryLabel.replace('\\', '/');
        String title = target.equals("root") ? "Root ADR" : "ADR for " + target;
        List<ClassDoc> classDocs = extractClassDocs(directoryPath);
        StringBuilder builder = new StringBuilder();
        builder.append("# ").append(title).append("\n\n");
        builder.append("- Status: proposed\n");
        builder.append("- Date: ").append(LocalDate.now()).append("\n\n");
        builder.append("## Context\n");
        builder.append("This ADR documents the architecture for the directory `").append(target).append("`.\n");
        builder.append("The folder represents a bounded feature area with responsibilities, data contracts and dependencies that should remain coherent as the application evolves.\n\n");

        if (!classes.isEmpty()) {
            builder.append("## Class responsibilities\n");
            for (ClassMetadata clazz : classes.stream().sorted(Comparator.comparing(ClassMetadata::getSimpleName)).toList()) {
                ClassDoc classDoc = findClassDoc(classDocs, clazz.getSimpleName());
                builder.append("### `").append(clazz.getSimpleName()).append("`\n");
                builder.append(classDoc != null && !classDoc.summary.isBlank()
                        ? classDoc.summary + "\n"
                        : "This class is part of the parking feature area and manages a specific capability within the bounded module.\n");
                if (classDoc != null && !classDoc.methods.isEmpty()) {
                    builder.append("**Functions in scope:**\n");
                    for (MethodDoc method : classDoc.methods) {
                        builder.append("- `").append(method.signature).append("` - ")
                                .append(method.summary.isBlank() ? "Executes the relevant module behavior." : method.summary)
                                .append("\n");
                    }
                }
                builder.append("\n");
            }
        } else {
            builder.append("This directory currently has no detected Java classes, but it is included for architectural review.\n\n");
        }

        builder.append("## In scope\n");
        builder.append("- Clearly define the responsibilities that live inside this folder and the contracts exposed to callers.\n");
        builder.append("- Describe how the classes collaborate with sibling modules or repositories for the feature being implemented.\n");
        builder.append("- Maintain consistent naming and boundary rules so that the feature stays easy to reason about.\n\n");

        builder.append("## Out of scope\n");
        builder.append("- Cross-feature orchestration that belongs in a shared application layer or platform boundary.\n");
        builder.append("- Authentication, billing, or unrelated business domains outside the current folder.\n");
        builder.append("- Storage or infrastructure decisions that are not explicitly part of the feature contract.\n\n");

        builder.append("## Implementation details\n");
        if (!classDocs.isEmpty()) {
            for (ClassDoc classDoc : classDocs.stream().sorted(Comparator.comparing(d -> d.name)).toList()) {
                if (classDoc.methods.isEmpty()) {
                    continue;
                }
                builder.append("### `").append(classDoc.name).append("`\n");
                for (MethodDoc method : classDoc.methods) {
                    builder.append("- `").append(method.signature).append("`\n");
                    if (!method.summary.isBlank()) {
                        builder.append("  - Summary: ").append(method.summary).append("\n");
                    }
                    if (!method.params.isBlank()) {
                        builder.append("  - Parameters: ").append(method.params).append("\n");
                    }
                    if (!method.returns.isBlank()) {
                        builder.append("  - Returns: ").append(method.returns).append("\n");
                    }
                    if (!method.throwsText.isBlank()) {
                        builder.append("  - Throws: ").append(method.throwsText).append("\n");
                    }
                    if (!method.implSpec.isBlank()) {
                        builder.append("  - Implementation detail: ").append(method.implSpec).append("\n");
                    }
                }
                builder.append("\n");
            }
        } else {
            builder.append("No direct JavaDoc was found in this directory. The generated class model still indicates the feature boundaries, and implementation details should be added when the classes are expanded.\n");
        }

        builder.append("## Decision\n");
        builder.append("The project treats this directory as a bounded architectural unit with clear responsibilities and explicit dependencies.\n\n");
        builder.append("## Consequences\n");
        builder.append("- A clear ownership boundary for the code in `").append(target).append("`.\n");
        builder.append("- Dependencies are visible in the generated class model and follow project conventions.\n");
        builder.append("- Future changes should preserve the established package boundaries, JavaDoc intent, and naming conventions.\n");
        return builder.toString();
    }

    private static List<ClassDoc> extractClassDocs(Path directory) {
        List<ClassDoc> docs = new ArrayList<>();
        if (directory == null || !Files.isDirectory(directory)) {
            return docs;
        }
        try (Stream<Path> files = Files.walk(directory)) {
            List<Path> javaFiles = files.filter(path -> path.toString().endsWith(".java"))
                    .sorted()
                    .toList();
            for (Path javaFile : javaFiles) {
                try {
                    String source = Files.readString(javaFile, StandardCharsets.UTF_8);
                    Matcher typeMatcher = TYPE_DECLARATION_PATTERN.matcher(source);
                    while (typeMatcher.find()) {
                        String name = typeMatcher.group(1);
                        String summary = extractJavadocSummary(source, typeMatcher.start());
                        docs.add(new ClassDoc(name, summary, extractMethodDocs(source, typeMatcher.start())));
                    }
                } catch (IOException ignored) {
                    // Ignore unreadable Java source files.
                }
            }
        } catch (IOException ignored) {
            // Ignore unreadable directories.
        }
        return docs;
    }

    private static List<MethodDoc> extractMethodDocs(String source, int classStart) {
        List<MethodDoc> methods = new ArrayList<>();
        Matcher matcher = METHOD_SIGNATURE_PATTERN.matcher(source);
        while (matcher.find()) {
            int methodStart = matcher.start();
            if (methodStart <= classStart) {
                continue;
            }
            String methodName = matcher.group(1);
            String params = sanitizeParamText(matcher.group(2));
            String summary = extractJavadocSummary(source, methodStart);
            String throwsText = "";
            String signature = methodName + "(" + params + ")";
            methods.add(new MethodDoc(signature, summary, params, "", throwsText, extractImplSpecFromSource(source, methodStart)));
        }
        return methods;
    }

    private static String extractJavadocSummary(String source, int anchorIndex) {
        int docStart = source.lastIndexOf("/**", anchorIndex);
        if (docStart < 0) {
            return "";
        }
        int docEnd = source.indexOf("*/", docStart);
        if (docEnd < 0) {
            return "";
        }
        String raw = source.substring(docStart + 3, docEnd);
        String summary = sanitizeDocText(raw);
        if (summary.isBlank()) {
            return "";
        }
        return summary;
    }

    private static String extractImplSpecFromSource(String source, int anchorIndex) {
        int docStart = source.lastIndexOf("/**", anchorIndex);
        if (docStart < 0) {
            return "";
        }
        int docEnd = source.indexOf("*/", docStart);
        if (docEnd < 0) {
            return "";
        }
        String raw = source.substring(docStart + 3, docEnd);
        Matcher matcher = Pattern.compile("@implSpec\\s+(.*)", Pattern.DOTALL).matcher(raw);
        if (matcher.find()) {
            return matcher.group(1).replaceAll("\\s+", " ").trim();
        }
        return "";
    }

    private static String sanitizeDocText(String raw) {
        if (raw == null) {
            return "";
        }
        String text = raw
                .replace("*", " ")
                .replace("/", " ")
                .replaceAll("(?s)@param\\s+\\w+\\s+.*?(?=(?:@\\w+|$))", " ")
                .replaceAll("(?s)@return\\s+.*?(?=(?:@\\w+|$))", " ")
                .replaceAll("(?s)@throws\\s+\\w+\\s+.*?(?=(?:@\\w+|$))", " ")
                .replaceAll("(?s)@implSpec\\s+.*?(?=(?:@\\w+|$))", " ")
                .replaceAll("(?s)@since\\s+.*?(?=(?:@\\w+|$))", " ")
                .replaceAll("(?s)@see\\s+.*?(?=(?:@\\w+|$))", " ")
                .replaceAll("<p>", " ")
                .replaceAll("</p>", " ")
                .replaceAll("<[^>]+>", " ")
                .replaceAll("@\\w+", " ")
                .replaceAll("\\s+", " ")
                .trim();
        return text.isBlank() ? "" : text;
    }

    private static String sanitizeParamText(String raw) {
        if (raw == null || raw.isBlank()) {
            return "";
        }
        String cleaned = raw.replaceAll("\\s+", " ").trim();
        return cleaned.isBlank() ? "" : cleaned;
    }

    private static String sanitizeThrowsText(String raw) {
        if (raw == null || raw.isBlank()) {
            return "";
        }
        return raw.replaceAll("\\s+", " ").trim();
    }

    private static ClassDoc findClassDoc(List<ClassDoc> docs, String simpleName) {
        for (ClassDoc doc : docs) {
            if (doc.name.equals(simpleName)) {
                return doc;
            }
        }
        return null;
    }

    private static String toFileStem(String value) {
        String normalized = value == null || value.isBlank() ? "root" : value
                .replace('\\', '-')
                .replace('/', '-')
                .replace(' ', '-')
                .replaceAll("[\\W_]+", "-");
        normalized = normalized.replaceAll("-+", "-");
        normalized = normalized.replaceAll("^-|-$", "");
        return normalized.isBlank() ? "root" : normalized.toLowerCase(Locale.ROOT);
    }

    private record ClassDoc(String name, String summary, List<MethodDoc> methods) {
    }

    private record MethodDoc(String signature,
                             String summary,
                             String params,
                             String returns,
                             String throwsText,
                             String implSpec) {
    }
}
