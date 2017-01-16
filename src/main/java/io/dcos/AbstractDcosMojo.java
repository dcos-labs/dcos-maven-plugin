package io.dcos;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;

/**
 * Abstract Mojo for handle `mvn dcos:*` commands
 */
public abstract class AbstractDcosMojo extends AbstractMojo {

  @Parameter(defaultValue = "${project.basedir}/app-definition.json", property = "appDefinition", required = true)
  File appDefinitionFile;

  @Parameter(defaultValue = "${project.basedir}/.dcos-token", property = "dcosTokenFile", required = true)
  File dcosTokenFile;

  @Parameter(property = "dcosUrl", required = true)
  String dcosUrl;

  @Parameter(defaultValue = "false", property = "ignoreSSL", required = true)
  Boolean ignoreSSL;

  void logConfiguration() {
    Log log = getLog();
    log.info("app definition: " + appDefinitionFile);
    log.info("dcos token: " + dcosTokenFile);
    log.info("dcos url: " + dcosUrl);
    log.info("ignore ssl certificate: " + ignoreSSL);
  }
}
