package com.dailywork.aicustomersupport.dtos;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ChatEntry {
    private String role;//user,assistant,system
    private String content;
}
