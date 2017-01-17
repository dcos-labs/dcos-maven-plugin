package dcos;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

import java.io.IOException;
import java.util.Map;

/**
 * Mojo to handle `mvn dcos:deploy`
 */
@Mojo(name = "deploy", defaultPhase = LifecyclePhase.DEPLOY)
public class DcosDeployMojo extends AbstractDcosMojo {

  public void execute() throws MojoExecutionException {
    CloseableHttpClient client = null;
    try {
      getLog().info("About to execute DC/OS deploy");
      logConfiguration();
      client = DcosPluginHelper.buildClient(ignoreSslCertificate);
      Map<String, Object> marathonConfigurationJson = DcosPluginHelper.readJsonFileToMap(appDefinitionFile);
      HttpPut put = new HttpPut(DcosPluginHelper.cleanUrl(dcosUrl + "/service/marathon/v2/apps/" + marathonConfigurationJson.get("id")));
      put.setHeader("Authorization", "token=" + DcosPluginHelper.readToken(dcosTokenFile));
      put.setHeader("Content-Type", "application/json");
      put.setEntity(new FileEntity(appDefinitionFile));
      CloseableHttpResponse response = client.execute(put);
      getLog().info("Response from DC/OS [" + response.getStatusLine().getStatusCode() + "] " + IOUtils.toString(response.getEntity().getContent(), "UTF-8"));
    } catch (Exception e) {
      getLog().error("Unable to perform deployment", e);
      throw new RuntimeException(e);
    } finally { // no try-with-resources in this language level -> compatibility
      if (client != null) {
        try {
          client.close();
        } catch (IOException e) {
          getLog().warn("Unable to close the http client", e);
        }
      }
    }
  }
}
