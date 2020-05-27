# OpenAPI Spec for Stock API

* [ReDoc](https://redocly.github.io/redoc/?url=https://raw.githubusercontent.com/making/spring-store-2020/master/stock-spec/openapi/doc.yml)
* [Swagger UI](https://petstore.swagger.io/?url=https://raw.githubusercontent.com/making/spring-store-2020/master/stock-spec/openapi/doc.yml)

## Include the generated spec

```xml
<dependency>
    <groupId>lol.maki.dev.store</groupId>
    <artifactId>stock-spec</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <exclusions>
        <exclusion>
            <groupId>io.springfox</groupId>
            <artifactId>springfox-swagger2</artifactId>
        </exclusion>
        <exclusion>
            <groupId>io.springfox</groupId>
            <artifactId>springfox-swagger-ui</artifactId>
        </exclusion>
    </exclusions>
</dependency>
<dependency>
    <groupId>org.webjars</groupId>
    <artifactId>swagger-ui</artifactId>
    <version>3.25.4</version>
</dependency>
<dependency>
    <groupId>org.webjars</groupId>
    <artifactId>webjars-locator-core</artifactId>
</dependency>
```

and

```xml
<repositories>
    <repository>
        <id>sonatype-snapshots</id>
        <name>Sonatype Snapshots</name>
        <url>https://oss.sonatype.org/content/repositories/snapshots</url>
        <snapshots>
            <enabled>true</enabled>
        </snapshots>
    </repository>
</repositories>
```

## How to generate source code from the spec


```
./openapi/generate-sources.sh
```