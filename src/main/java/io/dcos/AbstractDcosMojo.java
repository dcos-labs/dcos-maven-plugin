package io.dcos;

import org.apache.commons.lang.StringUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.util.Map;

/**
 * Abstract Mojo for handle `mvn dcos:*` commands
 */
abstract class AbstractDcosMojo extends AbstractMojo {

  @Parameter(defaultValue = "${project.basedir}/application.json", property = "appDefinition", required = true)
  File appDefinitionFile;

  @Parameter(defaultValue = "${project.basedir}/app-definition.json", property = "legacyAppDefinition", required = true, readonly = true)
  File legacyAppDefinitionFile;

  @Parameter(defaultValue = "${project.basedir}/.dcos-token", property = "dcosTokenFile", required = true)
  File dcosTokenFile;

  @Parameter(property = "dcosUrl", required = true)
  String dcosUrl;

  @Parameter(defaultValue = "false", property = "ignoreSslCertificate", required = true)
  Boolean ignoreSslCertificate;

  @Parameter(defaultValue = "EMPTY", property = "deployable", required = true)
  String deployable;

  void logConfiguration() {
    Log log = getLog();
    log.info("app definition: " + appDefinitionFile);
    log.info("legacy default app definition: " + legacyAppDefinitionFile);
    log.info("dcos token: " + dcosTokenFile);
    log.info("dcos url: " + dcosUrl);
    log.info("ignore ssl certificate: " + ignoreSslCertificate);
    log.info("deployable: " + deployable);
  }

  String buildDcosUrl(Object id, Map<String, Object> entity) {
    String baseUrl = dcosUrl + "/service/marathon/v2/";
    String result;
    // Legacy handling
    if (StringUtils.equalsIgnoreCase("POD", deployable)) {
      result = podUrl(baseUrl, id);
    } else if (StringUtils.equalsIgnoreCase("GROUP", deployable)) {
      result = groupUrl(baseUrl, id);
    } else if (StringUtils.equalsIgnoreCase("APP", deployable)) {
      result = appUrl(baseUrl, id);
    } else {
      // new `automatic` handling
      if (entity.containsKey("containers")) {
        result = podUrl(baseUrl, id);
      } else if (entity.containsKey("apps")) {
        result = groupUrl(baseUrl, id);
      } else {
        result = appUrl(baseUrl, id);
      }
    }
    getLog().info("Calculated url: " + result);
    return result;
  }

  private String appUrl(String baseUrl, Object id) {
    return DcosPluginHelper.cleanUrl(baseUrl + "apps/" + id);
  }

  private String groupUrl(String baseUrl, Object id) {
    return DcosPluginHelper.cleanUrl(baseUrl + "groups/");
  }

  private String podUrl(String baseUrl, Object id) {
    return DcosPluginHelper.cleanUrl(baseUrl + "pods/" + id);
  }
}
