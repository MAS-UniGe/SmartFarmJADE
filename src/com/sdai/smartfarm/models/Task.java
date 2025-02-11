package com.sdai.smartfarm.models;

import java.util.List;

import jade.core.AID;

public record Task(
    AssistanceRequest request,
    List<Position> path,
    AID requester,
    String replyCode
) {
    
    @Override
    public boolean equals(Object o) {
        return (o instanceof Task task && request.equals(task.request()));
    }

    @Override
    public int hashCode() {
        return request.hashCode();
    }

}
