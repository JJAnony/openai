package br.com.alura.openai;

import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;

import java.util.Arrays;

public class IntegrationTest {

    public static void main(String[] args) {
        var user = "Gere 5 produtos";
        var system = "Você é um gerador de produtos ficticios para um ecommerce e deve gerar apenas o nome dos produtos";

        var key = System.getenv("OPENAI_API_KEY");

        var service = new OpenAiService(key);

        var request = ChatCompletionRequest.builder()
                .model("gpt-4")
                .messages(Arrays.asList(
                        new ChatMessage(ChatMessageRole.USER.value(), user),
                        new ChatMessage(ChatMessageRole.SYSTEM.value(), system)
                ))
                .build();

        service.createChatCompletion(request)
                .getChoices()
                .forEach(m -> System.out.println(m.getMessage().getContent()));

    }

}
