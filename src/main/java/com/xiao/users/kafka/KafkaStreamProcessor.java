package com.xiao.users.kafka;

import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class KafkaStreamProcessor {

    private KafkaConfigProperties kafkaConfigProperties;

    public KafkaStreamProcessor(KafkaConfigProperties kafkaConfigProperties) {
        this.kafkaConfigProperties = kafkaConfigProperties;
    }

    @Bean
    public KStream<String, String> kStreamJson(StreamsBuilder builder) {
        return builder.stream(kafkaConfigProperties.getUserSyncTopic());
    }
}