package com.sift.umlplugin;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Mojo(name = "adr", defaultPhase = LifecyclePhase.PROCESS_CLASSES, threadSafe = true)
public class AdrMojo extends AbstractMojo {
    @Parameter(defaultValue = "${project.basedir}", readonly = true)
    private String projectBaseDirectory;

    @Parameter(property = "sift.sourceDirectory", defaultValue = "${project.basedir}/src/main/java")
    private String sourceDirectory;

    @Parameter(property = "sift.outputDirectory", defaultValue = "${project.build.directory}/sift")
    private String outputDirectory;

    @Parameter(property = "sift.includePackages")
    private String[] includePackages = new String[0];

    @Parameter(property = "sift.excludePackages")
    private String[] excludePackages = new String[0];

    @Override
    public void execute() throws MojoExecutionException {
        Path projectRoot = determineProjectRoot();
        try {
            Path sourceRoot = SecurityValidator.resolveProjectPath(sourceDirectory, "sift.sourceDirectory", projectRoot);
            if (!Files.isDirectory(sourceRoot)) {
                getLog().warn("No source directory found at " + sourceRoot + ". Nothing to render.");
                return;
            }

            Path output = SecurityValidator.resolveProjectPath(outputDirectory, "sift.outputDirectory", projectRoot);
            Files.createDirectories(output);
            AdrGenerator.generate(sourceRoot, output, includePackages, excludePackages);
            getLog().info("Generated ADR documents under " + output.resolve("adr").toAbsolutePath());
        } catch (IOException e) {
            throw new MojoExecutionException("Failed to generate ADR documents", e);
        }
    }

    private Path determineProjectRoot() {
        String configured = projectBaseDirectory == null || projectBaseDirectory.isBlank() ? "." : projectBaseDirectory;
        return Path.of(configured).toAbsolutePath().normalize();
    }
}
