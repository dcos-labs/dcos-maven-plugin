package io.dcos;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

import java.io.File;
import java.io.IOException;

/**
 * Mojo to handle `mvn dcos:uploadArtifact`
 */
@Mojo(name = "uploadArtifact", defaultPhase = LifecyclePhase.DEPLOY)
public class DcosUploadArtifactMojo extends AbstractDcosMojo {

  // curl -v -u admin:admin123 --upload-file artifactory-lb-options.json http://54.69.91.125:629/repository/test/

  public void execute() throws MojoExecutionException {
    CloseableHttpClient client = null;
    try {
      getLog().info("About to execute DC/OS pushArtifact.");

      client = HttpClientBuilder.create().build();

      // this is the main artifact file
      File file = project.getArtifact().getFile();

      // check if artifact exists
      if (file == null) {
        throw new RuntimeException("Artifact does not exist. You need to package first. Did you run `mvn package dcos:uploadArtifact`?");
      }

      getLog().info("Uploading this file: " + file.toString());

      HttpPut put = new HttpPut("http://" + nexusUrl + "/repository/" + nexusRepositoryName + "/" + file.getName());

      put.addHeader("Authorization", "Basic " + Base64.encodeBase64String((nexusUser + ":" + nexusPassword).getBytes()));

      put.setEntity(new FileEntity(file, ContentType.DEFAULT_BINARY));

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
