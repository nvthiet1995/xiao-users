package com.xiao.users.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.xiao.users.entity.User;

public interface KafkaService {

    void sendMessage(String topic, String key, String value);

}