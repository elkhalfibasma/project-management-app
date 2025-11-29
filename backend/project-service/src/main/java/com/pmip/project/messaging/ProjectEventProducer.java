package com.pmip.project.messaging;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class ProjectEventProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${app.topics.project}")
    private String topicProject;

    public ProjectEventProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(String key, String payload) {
        kafkaTemplate.send(topicProject, key, payload);
    }
}
