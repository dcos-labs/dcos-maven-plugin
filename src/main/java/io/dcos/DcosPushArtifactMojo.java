package io.dcos;

import net.iharder.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * Mojo to handle `mvn dcos:pushArtifact`
 */
@Mojo(name = "pushArtifact", defaultPhase = LifecyclePhase.DEPLOY)
public class DcosPushArtifactMojo extends AbstractDcosMojo {

  // curl -v -u admin:admin123 --upload-file artifactory-lb-options.json http://54.69.91.125:629/repository/test/

  public void execute() throws MojoExecutionException {
    CloseableHttpClient client = null;
    try {
      getLog().info("About to execute DC/OS pushArtifact1");
      //logConfiguration();

      // this is the main artifact file
      File file = project.getArtifact().getFile();

      // check if artifact exists
      if (file == null) {
        throw new RuntimeException("Artifact does not exist. Did you run mvn package dcos:pushArtifact?");
      }

      getLog().info(project.getArtifact().getFile().toString());     //file.toString());

      MultipartEntityBuilder builder  = MultipartEntityBuilder.create();
      builder.addBinaryBody("file", file, ContentType.create("application/octet-stream"), file.toURI().toString());

      client = DcosPluginHelper.buildClient(ignoreSslCertificate);
      Map<String, Object> marathonConfigurationJson = DcosPluginHelper.readJsonFileToMap(appDefinitionFile, legacyAppDefinitionFile);
      HttpPut put = new HttpPut(nexusUrl + "/" + nexusRepositoryName + "/");
      //put.setHeader("Authorization", "token=" + DcosPluginHelper.readToken(dcosTokenFile));
      String encoding = Base64.encodeBytes (("admin" + ":" + "admin123").getBytes());
      put.setHeader("Authorization", "Basic " + encoding );
      put.setEntity(builder.build());
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
