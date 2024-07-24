package com.xiao.users.kafka;

import com.xiao.users.config.AppConfig;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class KafkaStreamProcessor {

    private AppConfig appConfig;

    public KafkaStreamProcessor(AppConfig appConfig) {
        this.appConfig = appConfig;
    }

    @Bean
    public KStream<String, String> kStreamJson(StreamsBuilder builder) {
        return builder.stream(appConfig.getUserSyncTopic());
    }
}