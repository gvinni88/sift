package com.sift.umlplugin;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public final class JavaProjectAnalyzer {
    private static final Pattern PACKAGE_PATTERN =
            Pattern.compile("package\\s+([A-Za-z0-9_.]+)\\s*;");

    private static final Pattern CLASS_PATTERN =
            Pattern.compile("(?m)(?:^|\\s)(?:public\\s+)?(?:final\\s+)?(?:abstract\\s+)?(class|interface|enum)\\s+(\\w+)");

    private static final Pattern TYPE_PATTERN =
            Pattern.compile("\\b([A-Z][A-Za-z0-9_]*)\\b");

    private static final Set<String> IGNORED_TYPES = Set.of(
            "String", "Object", "Class", "Boolean", "Integer", "Long", "Short",
            "Byte", "Float", "Double", "Character", "Void", "Optional", "List",
            "Set", "Map", "Collection", "Iterable", "Iterator", "ArrayList",
            "LinkedHashSet", "HashMap", "ConcurrentHashMap", "Date", "UUID",
            "Instant", "LocalDate", "LocalDateTime", "RuntimeException", "Exception",
            "IOException", "File", "Path", "Comparator", "Predicate", "Supplier",
            "Consumer", "Function", "Runnable", "Stream", "System", "Enum"
    );

    private JavaProjectAnalyzer() {
    }

    public static List<ClassMetadata> analyze(Path sourceRoot,
                                              String[] includePackages,
                                              String[] excludePackages) throws IOException {
        if (!Files.isDirectory(sourceRoot)) {
            return List.of();
        }

        Map<String, ClassMetadata> bySimpleName = new LinkedHashMap<>();

        try (Stream<Path> files = Files.walk(sourceRoot)) {
            List<Path> javaFiles = files
                    .filter(path -> path.toString().endsWith(".java"))
                    .sorted()
                    .toList();

            for (Path javaFile : javaFiles) {
                String content = Files.readString(javaFile, StandardCharsets.UTF_8);
                String packageName = extractPackageName(content);

                if (!matchesPackage(packageName, includePackages, excludePackages)) {
                    continue;
                }

                List<ClassInfo> declarations = extractClasses(content, packageName);
                for (ClassInfo declaration : declarations) {
                    String simpleName = declaration.simpleName();
                    String fullyQualifiedName = packageName.isBlank()
                            ? simpleName
                            : packageName + "." + simpleName;

                    String stereotype = detectStereotype(content);

                    ClassMetadata metadata = new ClassMetadata(
                            packageName,
                            simpleName,
                            fullyQualifiedName,
                            declaration.kind(),
                            stereotype,
                            content
                    );

                    ClassMetadata existing = bySimpleName.get(simpleName);
                    if (existing == null) {
                        bySimpleName.put(simpleName, metadata);
                    } else {
                        existing.getDependencies().addAll(metadata.getDependencies());
                    }
                }
            }
        }

        for (ClassMetadata metadata : bySimpleName.values()) {
            resolveDependencies(metadata, bySimpleName);
        }

        return bySimpleName.values().stream()
                .sorted(Comparator.comparing(ClassMetadata::getSimpleName))
                .toList();
    }

    private static String detectStereotype(String source) {
        if (source.contains("@SpringBootApplication")) return "application";
        if (source.contains("@RestController")) return "restController";
        if (source.contains("@Controller")) return "controller";
        if (source.contains("@Service")) return "service";
        if (source.contains("@Repository")) return "repository";
        if (source.contains("@Configuration")) return "configuration";
        if (source.contains("@Component")) return "component";
        if (source.contains("@Entity")) return "entity";
        return "class";
    }

    private static List<ClassInfo> extractClasses(String source, String packageName) {
        List<ClassInfo> result = new ArrayList<>();
        Matcher matcher = CLASS_PATTERN.matcher(source);
        while (matcher.find()) {
            String kind = matcher.group(1);
            String simpleName = matcher.group(2);
            result.add(new ClassInfo(kind, simpleName));
        }
        return result;
    }

    private static String extractPackageName(String source) {
        Matcher matcher = PACKAGE_PATTERN.matcher(source);
        return matcher.find() ? matcher.group(1) : "";
    }

    private static void resolveDependencies(ClassMetadata metadata,
                                            Map<String, ClassMetadata> bySimpleName) {
        Set<String> names = extractTypeNames(metadata.getSource());
        for (String name : names) {
            ClassMetadata match = bySimpleName.get(name);
            if (match != null && !metadata.getSimpleName().equals(name)) {
                metadata.addDependency(match.getSimpleName());
            }
        }
    }

    private static Set<String> extractTypeNames(String source) {
        String cleaned = source
                .replaceAll("/\\*.*?\\*/", " ")
                .replaceAll("//.*", " ")
                .replaceAll("<[^>]+>", " ");

        Set<String> names = new LinkedHashSet<>();
        Matcher matcher = TYPE_PATTERN.matcher(cleaned);
        while (matcher.find()) {
            String candidate = matcher.group(1);
            if (candidate.equals("package") || candidate.equals("class") || candidate.equals("interface") || candidate.equals("enum")) {
                continue;
            }
            if (IGNORED_TYPES.contains(candidate)) {
                continue;
            }
            names.add(candidate);
        }

        return names;
    }

    private static boolean matchesPackage(String packageName,
                                          String[] includePackages,
                                          String[] excludePackages) {
        if (packageName == null || packageName.isBlank()) {
            return true;
        }

        if (includePackages != null && includePackages.length > 0) {
            boolean included = false;
            for (String pattern : includePackages) {
                if (pattern == null || pattern.isBlank()) continue;
                if (matchPattern(packageName, pattern)) {
                    included = true;
                    break;
                }
            }
            if (!included) {
                return false;
            }
        }

        if (excludePackages != null) {
            for (String pattern : excludePackages) {
                if (pattern == null || pattern.isBlank()) continue;
                if (matchPattern(packageName, pattern)) {
                    return false;
                }
            }
        }

        return true;
    }

    private static boolean matchPattern(String value, String pattern) {
        if (pattern.endsWith(".*")) {
            String prefix = pattern.substring(0, pattern.length() - 2);
            return value.equals(prefix) || value.startsWith(prefix + ".");
        }
        return value.equals(pattern);
    }

    private record ClassInfo(String kind, String simpleName) {
    }
}
