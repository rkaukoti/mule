/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.functional.classloading.isolation.maven;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;

import static java.nio.file.Files.readAllLines;

/**
 * Uses maven <a href="https://github.com/ferstl/depgraph-maven-plugin">https://github.com/ferstl/depgraph-maven-plugin</a> to resolve the
 * dependencies for the test class. It relies on the {@link DependencyGraphMavenDependenciesResolver#DEPENDENCIES_GRAPH_FILE_NAME} generated
 * by the depgraph-maven-plugin and the artifact should have set this plugin in its maven build section and run it before the test is
 * executed.
 * <p/>
 * An example of how the plugin has to be defined:
 * <pre>
 * {@code
 * <plugin>
 *     <groupId>com.github.ferstl</groupId>
 *     <artifactId>depgraph-maven-plugin</artifactId>
 *     <version>1.0.4</version>
 *     <configuration>
 *         <showDuplicates>true</showDuplicates>
 *         <showConflicts>true</showConflicts>
 *         <outputFile>${project.build.testOutputDirectory}/dependency-graph.dot</outputFile>
 *     </configuration>
 *     <executions>
 *         <execution>
 *             <id>depgraph-dependencies-graph</id>
 *             <goals>
 *                 <goal>graph</goal>
 *             </goals>
 *             <phase>process-test-resources</phase>
 *         </execution>
 *     </executions>
 * </plugin>
 * }
 * </pre>
 * If the file doesn't exists it will thrown a {@link IllegalStateException} in all of its methods.
 *
 * @since 4.0
 */
public class DependencyGraphMavenDependenciesResolver implements MavenDependenciesResolver
{

    private static final String DEPENDENCIES_GRAPH_ARROW = "->";
    private static final String DEPENDENCIES_GRAPH_FILE_NAME = "dependency-graph.dot";
    protected final transient Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * Creates a dependency graph where with all the transitive dependencies, including duplicates. Uses depgraph-maven-plugin
     * in order to generate the dot graph and with the relationships (edges) it generates the {@link DependenciesGraph}.
     *
     * @return a {@link DependenciesGraph} that holds the rootArtifact, dependencies and transitive dependencies for each dependency. The
     * rootArtifact represents the current maven artifact that the test belongs to.
     * @throws IllegalStateException if the dependencies are empty
     */
    @Override
    public DependenciesGraph buildDependencies() throws IllegalStateException
    {
        try
        {
            final File dependenciesGraphFile = getDependenciesGraphFile();

            Path dependenciesPath = Paths.get(dependenciesGraphFile.toURI());
            BasicFileAttributes view = Files.getFileAttributeView(dependenciesPath, BasicFileAttributeView.class).readAttributes();
            logger.debug("Building maven dependencies graph using depgraph-maven-plugin output file: '{}', created: {}, last modified: {}",
                    dependenciesGraphFile, view.creationTime(), view.lastModifiedTime());


            LinkedHashMap<MavenArtifact, Set<MavenArtifact>> mavenArtifactsDependencies = new LinkedHashMap<>();
            readAllLines(dependenciesGraphFile.toPath(),
                    Charset.defaultCharset()).stream()
                                             .filter(line -> line.contains(DEPENDENCIES_GRAPH_ARROW)).forEach(line ->
                    {
                        MavenArtifact from = parseDotDependencyArtifactFrom(line);
                        MavenArtifact to = parseDotDependencyArtifactTo(line);
                        if (!mavenArtifactsDependencies.containsKey(from))
                        {
                            mavenArtifactsDependencies.put(from, new HashSet<>());
                        }
                        mavenArtifactsDependencies.get(from).add(to);
                    }
            );
            if (mavenArtifactsDependencies.isEmpty())
            {
                throw new IllegalStateException(
                        "depgraph-maven-plugin output file read but no dependencies found, something may be wrong please check the dot file");
            }
            MavenArtifact rootArtifact = mavenArtifactsDependencies.keySet().stream().findFirst().get();
            Set<MavenArtifact> dependencies = mavenArtifactsDependencies.get(rootArtifact);
            mavenArtifactsDependencies.remove(rootArtifact);
            return new DependenciesGraph(rootArtifact, dependencies, mavenArtifactsDependencies);
        }
        catch (IOException e)
        {
            throw new RuntimeException("Error while processing dependencies from depgraph-maven-plugin output file", e);
        }
    }

    private File getDependenciesGraphFile()
    {
        URL dependenciesListFileURL =
                DependencyGraphMavenDependenciesResolver.class.getClassLoader().getResource(DEPENDENCIES_GRAPH_FILE_NAME);
        if (dependenciesListFileURL == null)
        {
            throw new IllegalStateException(
                    DEPENDENCIES_GRAPH_FILE_NAME + " not found, the maven plugin 'depgraph-maven-plugin ' should be executed first.");
        }
        File file = new File(dependenciesListFileURL.getFile());
        if (!file.exists())
        {
            throw new IllegalStateException(
                    String.format("Unable to resolve dependencies for test due to file '%s' was not found", DEPENDENCIES_GRAPH_FILE_NAME));
        }

        return file;
    }

    private MavenArtifact parseDotDependencyArtifactTo(final String line)
    {
        String artifactLine = line.split(DEPENDENCIES_GRAPH_ARROW)[1];
        if (artifactLine.contains("["))
        {
            artifactLine = artifactLine.substring(0, artifactLine.indexOf("["));
        }
        if (artifactLine.contains("\""))
        {
            artifactLine = artifactLine.substring(artifactLine.indexOf("\"") + 1, artifactLine.lastIndexOf("\""));
        }
        return parseMavenArtifact(artifactLine.trim());
    }

    private MavenArtifact parseDotDependencyArtifactFrom(final String line)
    {
        String artifactLine = line.split(DEPENDENCIES_GRAPH_ARROW)[0];
        if (artifactLine.contains("\""))
        {
            artifactLine = artifactLine.substring(artifactLine.indexOf("\"") + 1, artifactLine.lastIndexOf("\""));
        }
        return parseMavenArtifact(artifactLine.trim());
    }

    private MavenArtifact parseMavenArtifact(final String mavenDependencyString)
    {
        String[] tokens = mavenDependencyString.split(MavenArtifact.MAVEN_DEPENDENCIES_DELIMITER);
        String groupId = tokens[0];
        String artifactId = tokens[1];
        String type = tokens[2];
        String version = tokens[3];
        String scope = tokens[4];
        return MavenArtifact.builder()
                            .withGroupId(groupId)
                            .withArtifactId(artifactId)
                            .withType(type)
                            .withVersion(version)
                            .withScope(scope)
                            .build();
    }

}
