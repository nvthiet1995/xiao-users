package com.xiao.users.kafka;

import com.xiao.users.config.AppConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.serialization.Serdes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration;
import org.springframework.kafka.config.KafkaStreamsConfiguration;
import org.springframework.kafka.config.TopicBuilder;

import java.util.Map;

import static org.apache.kafka.streams.StreamsConfig.*;

@Configuration
@EnableKafka
@EnableKafkaStreams
public class KafkaStreamsConfig {

    private AppConfig appConfig;

    public KafkaStreamsConfig(AppConfig appConfig) {
        this.appConfig = appConfig;
    }

    @Bean(name = KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
    KafkaStreamsConfiguration kStreamsConfigs() {
        return new KafkaStreamsConfiguration(Map.of(
                APPLICATION_ID_CONFIG, appConfig.getAppId(),
                BOOTSTRAP_SERVERS_CONFIG, appConfig.getBootstrapAddress(),
                DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName(),
                DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName()
        ));
    }

    @Bean
    NewTopic userSyncTopic() {
        return TopicBuilder.name(appConfig.getUserSyncTopic())
                .partitions(1)
                .replicas(1)
                .build();
    }

}