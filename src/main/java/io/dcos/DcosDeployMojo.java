package io.dcos;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 */
@Mojo(name = "deploy", defaultPhase = LifecyclePhase.DEPLOY)
public class DcosDeployMojo extends AbstractMojo {
  /**
   * example target file
   */
  @Parameter(defaultValue = "${project.build.directory}", property = "outputDir", required = true)
  private File outputDirectory;

  public void execute() throws MojoExecutionException {
    // do nothing, yet
  }
}
