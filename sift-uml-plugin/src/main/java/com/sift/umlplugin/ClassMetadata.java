package com.sift.umlplugin;

import java.util.LinkedHashSet;
import java.util.Set;

public final class ClassMetadata {
    private final String packageName;
    private final String simpleName;
    private final String fullyQualifiedName;
    private final String kind;
    private final String stereotype;
    private final String source;
    private final Set<String> dependencies = new LinkedHashSet<>();

    public ClassMetadata(String packageName,
                         String simpleName,
                         String fullyQualifiedName,
                         String kind,
                         String stereotype,
                         String source) {
        this.packageName = packageName;
        this.simpleName = simpleName;
        this.fullyQualifiedName = fullyQualifiedName;
        this.kind = kind;
        this.stereotype = stereotype;
        this.source = source;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getSimpleName() {
        return simpleName;
    }

    public String getFullyQualifiedName() {
        return fullyQualifiedName;
    }

    public String getKind() {
        return kind;
    }

    public String getStereotype() {
        return stereotype;
    }

    public String getSource() {
        return source;
    }

    public Set<String> getDependencies() {
        return dependencies;
    }

    public void addDependency(String name) {
        if (name == null || name.isBlank()) {
            return;
        }
        dependencies.add(name);
    }
}
