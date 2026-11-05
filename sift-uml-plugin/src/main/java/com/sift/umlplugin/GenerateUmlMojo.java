package com.sift.umlplugin;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;

@Mojo(name = "generate-uml", defaultPhase = LifecyclePhase.PROCESS_CLASSES, threadSafe = true)
public class GenerateUmlMojo extends AbstractMojo {
    @Parameter(defaultValue = "${project.basedir}", readonly = true)
    private String projectBaseDirectory;

    @Parameter(property = "sift.sourceDirectory", defaultValue = "${project.basedir}/src/main/java")
    private String sourceDirectory;

    @Parameter(property = "sift.outputDirectory", defaultValue = "${project.build.directory}/sift")
    private String outputDirectory;

    @Parameter(property = "sift.title", defaultValue = "Project Diagram")
    private String title;

    @Parameter(property = "sift.includePackages")
    private String[] includePackages = new String[0];

    @Parameter(property = "sift.excludePackages")
    private String[] excludePackages = new String[0];

    @Parameter(property = "sift.format", defaultValue = "puml")
    private String format;

    @Override
    public void execute() throws MojoExecutionException {
        Path projectRoot = determineProjectRoot();
        try {
            Path sourceRoot = SecurityValidator.resolveProjectPath(sourceDirectory, "sift.sourceDirectory", projectRoot);
            if (!Files.isDirectory(sourceRoot)) {
                getLog().warn("No source directory found at " + sourceRoot + ". Nothing to render.");
                return;
            }

            List<ClassMetadata> classes = JavaProjectAnalyzer.analyze(sourceRoot, includePackages, excludePackages);
            if (classes.isEmpty()) {
                getLog().warn("No Java classes detected under " + sourceRoot + ".");
                return;
            }

            String uml = PlantUmlGenerator.generate(title, classes);

            Path output = SecurityValidator.resolveProjectPath(outputDirectory, "sift.outputDirectory", projectRoot);
            Files.createDirectories(output);

            String extension = normalizeFormat(format);
            if (!"puml".equals(extension) && !"uml".equals(extension)) {
                getLog().warn("Unsupported format '" + format + "'. Defaulting to .puml.");
                extension = "puml";
            }

            Path diagramFile = output.resolve("project-diagram." + extension);
            Files.writeString(diagramFile, uml, StandardCharsets.UTF_8);

            getLog().info("Generated UML diagram at " + diagramFile.toAbsolutePath());
        } catch (IOException e) {
            throw new MojoExecutionException("Failed to generate UML diagram", e);
        } catch (IllegalArgumentException e) {
            throw new MojoExecutionException("Invalid diagram input detected", e);
        }
    }

    private Path determineProjectRoot() {
        String configured = projectBaseDirectory == null || projectBaseDirectory.isBlank() ? "." : projectBaseDirectory;
        return Path.of(configured).toAbsolutePath().normalize();
    }

    private static String normalizeFormat(String requestedFormat) {
        if (requestedFormat == null || requestedFormat.isBlank()) {
            return "puml";
        }
        String normalized = requestedFormat.trim().toLowerCase(Locale.ROOT);
        return switch (normalized) {
            case "uml", "puml" -> normalized;
            default -> "puml";
        };
    }
}

