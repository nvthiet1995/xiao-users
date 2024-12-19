package com.xiao.users.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xiao.users.entity.User;
import com.xiao.users.exception.UserSyncException;
import com.xiao.users.kafka.KafkaConfigProperties;
import com.xiao.users.mapper.UserMapper;
import com.xiao.users.service.KafkaService;
import com.xiao.users.service.UserSyncService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


@Service
public class UserSyncServiceImpl implements UserSyncService {

    private final UserMapper userMapper;
    private final KafkaConfigProperties kafkaConfigProperties;
    private final ObjectMapper objectMapper;
    private final KafkaService kafkaService;
    private final Logger logger = LoggerFactory.getLogger(UserSyncServiceImpl.class);

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

    public void syncUserToAuthServer(User syncUser, String actionType) {
        String userSyncTopic = kafkaConfigProperties.getUserSyncTopic();
        String userJsonData = null;
        try {
            userJsonData = objectMapper.writeValueAsString(userMapper.userToUserSyncDto(syncUser));
        } catch (JsonProcessingException e) {
            logger.error(String.format("Failed to serialize user data for synchronization - %s - userId: %s", actionType, syncUser.getId()));
            throw new UserSyncException("Failed to serialize user data for synchronization");
        }
        kafkaService.sendMessage(userSyncTopic, actionType, userJsonData);
    }

}