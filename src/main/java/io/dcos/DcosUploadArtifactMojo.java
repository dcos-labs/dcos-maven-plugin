package io.dcos;

import net.iharder.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * Mojo to handle `mvn dcos:pushArtifact`
 */
@Mojo(name = "uploadArtifact", defaultPhase = LifecyclePhase.DEPLOY)
public class DcosUploadArtifactMojo extends AbstractDcosMojo {

  // curl -v -u admin:admin123 --upload-file artifactory-lb-options.json http://54.69.91.125:629/repository/test/

  public void execute() throws MojoExecutionException {
    CloseableHttpClient client = null;
    try {
      getLog().info("About to execute DC/OS pushArtifact.");

      CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
      credentialsProvider.setCredentials(AuthScope.ANY,
              new UsernamePasswordCredentials("admin", "admin123"));
      client = HttpClientBuilder.create().setDefaultCredentialsProvider(credentialsProvider).build();

      // this is the main artifact file
      File file = project.getArtifact().getFile();

      // check if artifact exists
      if (file == null) {
        throw new RuntimeException("Artifact does not exist. Did you run mvn package dcos:pushArtifact?");
      }

      getLog().info(project.getArtifact().getFile().toString());

      Process p = Runtime.getRuntime().exec("curl -u " + nexusUser + ":" + nexusPassword +"  --upload-file "+ project.getArtifact().getFile().toString()  + " http://" + nexusUrl + "/repository/" + nexusRepositoryName + "/");//file.toString());


//      MultipartEntityBuilder builder  = MultipartEntityBuilder.create();
//      builder.addBinaryBody("file", file, ContentType.create("application/octet-stream"), file.toString());

//      Map<String, Object> marathonConfigurationJson = DcosPluginHelper.readJsonFileToMap(appDefinitionFile, legacyAppDefinitionFile);
//      HttpPut put = new HttpPut("http://" + nexusUrl + "/repository/" + nexusRepositoryName + "/");
//      getLog().info(put.toString());
//      // put.setEntity(builder.build());
//      StringEntity jsonData = new StringEntity("{\"id\":\"123\", \"name\":\"Vicky Thakor\"}", "UTF-8");
//      put.setEntity(jsonData);
//      CloseableHttpResponse response = client.execute(put);
   //   getLog().info("Response from DC/OS [" + response.getStatusLine().getStatusCode() + "] " + IOUtils.toString(response.getEntity().getContent(), "UTF-8"));
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
