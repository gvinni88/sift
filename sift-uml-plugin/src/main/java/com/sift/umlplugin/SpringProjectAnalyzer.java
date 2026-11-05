package com.sift.umlplugin;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

final class SpringProjectAnalyzer {
    private SpringProjectAnalyzer() {
    }

    public static List<ClassMetadata> analyze(Path sourceRoot,
                                             String[] includePackages,
                                             String[] excludePackages) throws IOException {
        return JavaProjectAnalyzer.analyze(sourceRoot, includePackages, excludePackages);
    }
}
