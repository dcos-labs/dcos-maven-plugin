package io.dcos;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

/**
 * Mojo to handle `mvn dcos:deployUCR`
 */
@Mojo(name = "deployUCR", defaultPhase = LifecyclePhase.DEPLOY)
public class DcosDeployUCRMojo extends AbstractDcosMojo {

  public void execute() throws MojoExecutionException {
    CloseableHttpClient client = null;
    File tmpFile = null;
    try {
      getLog().info("About to execute DC/OS deployUCR");
      logConfiguration();
      client = DcosPluginHelper.buildClient(ignoreSslCertificate);
      Map<String, Object> marathonConfigurationJson = DcosPluginHelper.readJsonFileToMap(appDefinitionFile, legacyAppDefinitionFile);

      // this is the main artifact file
      File file = project.getArtifact().getFile();

      // check if artifact exists
      if (file == null) {
        throw new RuntimeException("Artifact does not exist. You need to package first. Did you run `mvn package dcos:deployUCR`?");
      }

      tmpFile = File.createTempFile("ucr-deploy", "json");
      //Files.copy(appDefinitionFile.toPath(), tmpFile.toPath(), REPLACE_EXISTING);

      Path path = Paths.get(appDefinitionFile.getAbsolutePath());
      Charset charset = StandardCharsets.UTF_8;

      String content = new String(Files.readAllBytes(path), charset);
      content = content.replaceAll("<FILENAME>", file.getName());
      content = content.replaceAll("<NEXUS_URL>", nexusUrl);

      FileOutputStream fooStream = new FileOutputStream(tmpFile, false);
      fooStream.write(content.getBytes());
      fooStream.close();

      getLog().info("TempFile: " + tmpFile.toPath().toString());

      HttpPut put = new HttpPut(buildDcosUrl(marathonConfigurationJson.get("id"), marathonConfigurationJson) + "?force=true'");
      put.setHeader("Authorization", "token=" + DcosPluginHelper.readToken(dcosTokenFile));
      put.setHeader("Content-Type", "application/json");

      put.setEntity(new FileEntity(tmpFile));

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
      // clean up after deployment
      if (tmpFile != null && tmpFile.exists()) {
        try {
          Files.delete(tmpFile.toPath());
        } catch (IOException e) {
          getLog().warn("Unable to delete temporary app definition", e);
        }
      }
    }
  }
}
