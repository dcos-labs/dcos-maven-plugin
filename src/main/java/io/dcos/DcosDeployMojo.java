package io.dcos;

import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * Mojo to handle `mvn dcos:deploy`
 */
@Mojo(name = "deploy", defaultPhase = LifecyclePhase.DEPLOY)
public class DcosDeployMojo extends AbstractMojo {

  @Parameter(defaultValue = "${project.basedir}/app-definition.json", property = "appDefinition", required = true)
  private File appDefinitionFile;

  @Parameter(defaultValue = "${project.basedir}/.dcos-token", property = "dcosTokenFile", required = true)
  private File dcosTokenFile;

  @Parameter(property = "dcosUrl", required = true)
  private String dcosUrl;

  @Parameter(defaultValue = "false", property = "ignoreSSL", required = true)
  private Boolean ignoreSSL;

  public void execute() throws MojoExecutionException {
    CloseableHttpClient client = null;
    try {
      System.out.println("lala");
      client = DcosPluginHelper.buildClient(ignoreSSL);
      Map<String, String> marathonConfigurationJson = DcosPluginHelper.readJsonFileToMap(appDefinitionFile);
      HttpPut post = new HttpPut(dcosUrl + "/service/marathon/v2/apps" + marathonConfigurationJson.get("id"));
      post.setHeader("Authorization", "token=" + DcosPluginHelper.readToken(dcosTokenFile));
      post.setEntity(new FileEntity(appDefinitionFile));
      client.execute(post);
    } catch (IOException e) {
      // TODO handle exception
      e.printStackTrace();
      throw new RuntimeException(e);
    } finally {
      if (client != null) {
        try {
          client.close();
        } catch (IOException e) {
          e.printStackTrace();
          // meh!
        }
      }
    }
  }
}
