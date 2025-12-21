package com.dailywork.aicustomersupport.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChatEntry {
    private String role;//user,assistant,system
    private String content;


    @Override
    public String toString() {
        return "ChatEntry{" +
                "role='" + role + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
