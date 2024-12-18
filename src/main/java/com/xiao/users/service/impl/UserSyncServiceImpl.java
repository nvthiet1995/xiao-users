package com.xiao.users.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xiao.users.entity.User;
import com.xiao.users.kafka.KafkaConfigProperties;
import com.xiao.users.mapper.UserMapper;
import com.xiao.users.service.KafkaService;
import com.xiao.users.service.UserSyncService;
import org.springframework.stereotype.Service;


@Service
public class UserSyncServiceImpl implements UserSyncService {

    private final UserMapper userMapper;

    private final KafkaConfigProperties kafkaConfigProperties;

    private final ObjectMapper objectMapper;

    private final KafkaService kafkaService;

    public UserSyncServiceImpl(
            UserMapper userMapper,
            KafkaConfigProperties kafkaConfigProperties,
            ObjectMapper objectMapper,
            KafkaService kafkaService
    ) {
        this.userMapper = userMapper;
        this.kafkaConfigProperties = kafkaConfigProperties;
        this.objectMapper = objectMapper;
        this.kafkaService = kafkaService;
    }

    public void syncUserToAuthServer(User syncUser, String actionType) throws JsonProcessingException {
        String userSyncTopic = kafkaConfigProperties.getUserSyncTopic();
        String userJsonData = objectMapper.writeValueAsString(userMapper.userToUserSyncDto(syncUser));
        kafkaService.sendMessage(userSyncTopic, actionType, userJsonData);
    }

}