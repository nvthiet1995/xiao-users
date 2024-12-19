package com.xiao.users.service;

public interface KafkaService {

    void sendMessage(String topic, String key, String value);

}