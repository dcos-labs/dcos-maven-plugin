# DC/OS Maven Plugin
Maven plugin to deploy containerized java applications to DC/OS

Containerized applications deployed on DC/OS are realized through Marathon. You can find  more information about Marathon and container deployment with DC/OS here:

- [Marathon documentation](http://mesosphere.github.io/marathon/docs/application-basics.html)
- [DC/OS 101 tutorials](https://dcos.io/docs/1.8/usage/tutorials/dcos-101/)


## Configuration
Example plugin configuration:

```
    <build>
        <plugins>
            <plugin>
                <groupId>io.dcos</groupId>
                <artifactId>dcos-maven-plugin</artifactId>
                <version>1.0-SNAPSHOT</version>
                <configuration>
                    <dcosUrl>http://junterste-elasticl-1j6yqdjx1qa8f-2107866714.us-west-2.elb.amazonaws.com/</dcosUrl>
                    <ignoreSslCertificate>true</ignoreSslCertificate>
                </configuration>
            </plugin>
        </plugins>
    </build>
```

### appDefinition
By default, you need to place your application definition in the `app-definition.json`. You can change this filename with the `appDefinition` parameter.

### dcosTokenFile
By default, you need to place your DC/OS authentication token in the `.dcos-token` file. You can can change this filename with the `dcosTokenFile` parameter.

### dcosUrl
You can configure the URL of your DC/OS with the `dcosUrl` configuration node in your plugin configuration within your `pom.xml`.

### ignoreSslCertificate
If you want to skip ssl certificate verification, e.g. you have a self-signed certificate, you can set configure `<ignoreSslCertificate>true</ignoreSslCertificate>` to disable this verification.

## How do I get my DC/OS auth token?!
If you are using the DC/OS CLI, just do the following:

```
dcos config show core.dcos_acs_token
```


## Commands
### dcos:deploy
If you want to create or update your application, you want to use `mvn dcos:deploy`. This will send the file configured as `appDefinition` to DC/OS.

### dcos:restart
If you just want to restart your application, for example you updated the docker image in your registry, you want to use `mvn dcos:restart`. No application definition will be sent to DC/OS, only the restart trigger for the defined application ID will be sent.


## Docker Maven Plugin
This plugin plays well with the [docker-maven-plugin](https://github.com/spotify/docker-maven-plugin) of spotify. You might want to use this plugin to build and push your docker images. After using 

```
mvn docker:build docker:push
```

to update your docker image in your registry, you might want to use 

```
mvn dcos:restart
```

to restart your application on DC/OS. Combined: `mvn docker:build docker:push dcos:restart`

**Attention** When using `mvn dcos:restart` please make sure you you are using the `forcePullImage` flag in your marathon configuration to disable image caching, for example:

```
{
  "type": "DOCKER",
  "docker": {
    "image": "your/image",
    "forcePullImage": true
  }
}
```


## Limitations
### Only available for apps
This plugin is currently only able to deploy apps. PODs and Groups are currently not supported


## Run example
You can find a complete example using a Spring Boot web application in the `sample/` folder. 

```
TODO plugins configuration from pom.xml
```

You only need to adapt the configuration (`.dcos-token` & `pom.xml`) and run

```
mvn docker:build docker:push dcos:deploy
```