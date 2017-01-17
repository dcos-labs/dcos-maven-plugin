package io.dcos;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

import java.io.IOException;
import java.util.Map;

/**
 * Mojo to handle `mvn dcos:update`
 */
@Mojo(name = "restart", defaultPhase = LifecyclePhase.DEPLOY)
public class DcosRestartMojo extends AbstractDcosMojo {

  public void execute() throws MojoExecutionException {
    CloseableHttpClient client = null;
    try {
      getLog().info("About to execute DC/OS restart");
      logConfiguration();
      client = DcosPluginHelper.buildClient(ignoreSslCertificate);
      Map<String, Object> marathonConfigurationJson = DcosPluginHelper.readJsonFileToMap(appDefinitionFile);
      HttpPost post = new HttpPost(DcosPluginHelper.cleanUrl(dcosUrl + "/service/marathon/v2/apps/" + marathonConfigurationJson.get("id") + "/restart"));
      post.setHeader("Authorization", "token=" + DcosPluginHelper.readToken(dcosTokenFile));
      CloseableHttpResponse response = client.execute(post);
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
