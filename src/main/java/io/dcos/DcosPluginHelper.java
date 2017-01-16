package io.dcos;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.FileUtils;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;

class DcosPluginHelper {

  static Map<String, String> readJsonFileToMap(File file) {
    Type stringStringMap = new TypeToken<Map<String, String>>() {
    }.getType();
    try {
      return new Gson().fromJson(FileUtils.readFileToString(file), stringStringMap);
    } catch (IOException e) {
      // TODO handle exception
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }

  static String readToken(File tokenFile) {
    try {
      return FileUtils.readFileToString(tokenFile);
    } catch (IOException e) {
      // TODO handle exception
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }

  static CloseableHttpClient buildClient(boolean ignoreSSL) {
    try {
      SSLSocketFactory sslsf = new SSLSocketFactory(new TrustStrategy() {

        public boolean isTrusted(
            final X509Certificate[] chain, String authType) throws CertificateException {
          // Oh, I am easy...
          return true;
        }

      });
      if (ignoreSSL) {
        return HttpClients.custom().setSSLSocketFactory(sslsf).build();
      } else {
        return HttpClients.createDefault();
      }
    } catch (Exception e) {
      // TODO handle exception
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }

}
