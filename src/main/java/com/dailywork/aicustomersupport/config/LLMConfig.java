package com.dailywork.aicustomersupport.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LLMConfig {


    public ChatClient chatClient(ChatClient.Builder chatClientBuilder){
        return chatClientBuilder.build();
    }

   @Bean
    public ChatClient chatClient(){

        OpenAiApi openAiApi = OpenAiApi.builder()
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .build();

        OpenAiChatOptions chatOptions = OpenAiChatOptions.builder()
                .model("gpt-4o")
                .temperature(0.7)
                .maxTokens(500)
                .build();

        OpenAiChatModel chatModel= OpenAiChatModel.builder()
                .openAiApi(openAiApi)
                .defaultOptions(chatOptions)
                .build();

        return ChatClient.create(chatModel);
    }

}
