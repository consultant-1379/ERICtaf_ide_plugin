package com.ericsson;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Pattern;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

@Mojo(name = "update-version")
public class VersionUpdateMojo extends AbstractMojo {
    @Parameter(defaultValue = "${project}")
    MavenProject project;

    public void execute() throws MojoExecutionException {
        try {
            String version = project.getVersion();
            // get rid of -SNAPSHOT because OSGi doesn't work well with it.
            if (version.endsWith("-SNAPSHOT")) {
                version = version.substring(0, version.length() - "-SNAPSHOT".length());
            }
            //
            File featureFile = new File(project.getModel().getProjectDirectory().getAbsolutePath() + "/feature.xml");
            getLog().info("BASEDIR:"+project.getModel().getProjectDirectory().getAbsolutePath());
            if (featureFile.exists()) {
                getLog().info("Updating version of feature.xml to:" + version);
                String feature = new String(Files.readAllBytes(Paths.get(featureFile.getAbsolutePath())));
                Pattern regex = Pattern.compile("(<feature.+?version=[\"])([0-9]+[.][0-9]+[.][0-9]+)(.*)",
                        Pattern.DOTALL);
                String replacedFeature = regex.matcher(feature).replaceFirst("$1" + version + "$3");
                Files.write(Paths.get(featureFile.getAbsolutePath()), replacedFeature.getBytes());
            }
            //
            File manifestFile = new File(project.getModel().getProjectDirectory() + "/META-INF/MANIFEST.MF");
            if (manifestFile.exists()) {
                getLog().info("Updating version of MANIFEST.MF to:" + version);
                String manifest = new String(Files.readAllBytes(Paths.get(manifestFile.getAbsolutePath())));
                String replacedManifest = manifest.replaceFirst(
                        "(Bundle[-]Version[:][ \t]?)([0-9]+[.][0-9]+[.][0-9]+)", "$1" + version);
                Files.write(Paths.get(manifestFile.getAbsolutePath()), replacedManifest.getBytes());
            }
            //
        } catch (IOException e) {
            throw new MojoExecutionException("IOError", e);
        }
    }
}
