package dcos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController("/")
public class MvnPluginSpringSampleApplication {

  @RequestMapping("/")
  public String hi() {
    return "Hi!";
  }

  public static void main(String[] args) {
    SpringApplication.run(MvnPluginSpringSampleApplication.class, args);
  }
}
