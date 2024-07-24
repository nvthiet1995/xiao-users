package com.xiao.users.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class AppConfig {

  @Value(value = "${info.app.name}")
  private String appId;

  @Value(value = "${spring.kafka.bootstrap-servers}")
  private String bootstrapAddress;

  @Value(value = "${kafka-topic.user-sync}")
  private String userSyncTopic;
}
