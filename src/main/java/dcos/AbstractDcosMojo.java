package dcos;

import org.apache.commons.lang.StringUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;

/**
 * Abstract Mojo for handle `mvn dcos:*` commands
 */
abstract class AbstractDcosMojo extends AbstractMojo {

  @Parameter(defaultValue = "${project.basedir}/app-definition.json", property = "appDefinition", required = true)
  File appDefinitionFile;

  @Parameter(defaultValue = "${project.basedir}/.dcos-token", property = "dcosTokenFile", required = true)
  File dcosTokenFile;

  @Parameter(property = "dcosUrl", required = true)
  String dcosUrl;

  @Parameter(defaultValue = "false", property = "ignoreSslCertificate", required = true)
  Boolean ignoreSslCertificate;

  @Parameter(defaultValue = "APP", property = "deployable", required = true)
  String deployable;

  void logConfiguration() {
    Log log = getLog();
    log.info("app definition: " + appDefinitionFile);
    log.info("dcos token: " + dcosTokenFile);
    log.info("dcos url: " + dcosUrl);
    log.info("ignore ssl certificate: " + ignoreSslCertificate);
    log.info("deployable: " + deployable);
  }

  String buildDcosUrl() {
    String result = dcosUrl + "/service/marathon/v2/";
    if (StringUtils.equalsIgnoreCase("POD", deployable)) {
      return result + "pods/";
    } else if (StringUtils.equalsIgnoreCase("GROUP", deployable)) {
      return result + "groups/";
    } else {
      return result + "apps/";
    }
  }
}
