package de.thb.sparefood;

import io.quarkus.test.common.DevServicesContext;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.shaded.com.google.common.collect.ImmutableMap;

import java.util.Map;

public class PostgresResource
    implements QuarkusTestResourceLifecycleManager, DevServicesContext.ContextAware {

  private String containerNetworkId;
  private final JdbcDatabaseContainer<?> dbContainer =
      new PostgreSQLContainer<>("postgres:14")
          .withDatabaseName("sparefood")
          .withUsername("testEnv")
          .withPassword("testEnv");

  @Override
  public void setIntegrationTestContext(DevServicesContext context) {
    containerNetworkId = context.containerNetworkId().orElse(null);
  }

  @Override
  public Map<String, String> start() {
    dbContainer.start();

    String jdbcUrl = dbContainer.getJdbcUrl();
    if (containerNetworkId != null) {
      // Replace hostname + port in the provided JDBC URL with the hostname of the Docker container
      // running PostgreSQL and the listening port.
      jdbcUrl = fixJdbcUrl(jdbcUrl);
    }

    return ImmutableMap.of(
        "quarkus.datasource.jdbc.url", jdbcUrl,
        "quarkus.datasource.username", dbContainer.getUsername(),
        "quarkus.datasource.password", dbContainer.getPassword());
  }

  @Override
  public void stop() {
    dbContainer.stop();
  }

  private String fixJdbcUrl(String jdbcUrl) {
    // Part of the JDBC URL to replace
    String hostPort = dbContainer.getHost() + ':' + dbContainer.getMappedPort(PostgreSQLContainer.POSTGRESQL_PORT);

    // Host/IP on the container network plus the unmapped port
    String networkHostPort =
        dbContainer.getCurrentContainerInfo().getConfig().getHostName()
            + ':'
            + PostgreSQLContainer.POSTGRESQL_PORT;

    return jdbcUrl.replace(hostPort, networkHostPort);
  }
}
