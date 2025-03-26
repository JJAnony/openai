package br.com.alura.openai;

import com.knuddels.jtokkit.api.ModelType;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;

import java.util.Arrays;
import java.util.Scanner;

public class ProductCategorizer {

    public static void main(String[] args) {

        var scanner = new Scanner(System.in);
        System.out.println("Digite as categorias válidas:");
        var categories = scanner.nextLine();

        while (true) {
            System.out.println("Digite o nome do produto:");
            var user = scanner.nextLine();

            var system = """
                    Você é um categorizador de produtos e deve responder apenas o nome da categoria do produto informado
                    
                    Escolha uma categoria dentre a lista abaixo:
                    
                    %s
                    
                    exemplo de uso:
                    
                    Pergunta: Bola de futebol
                    Resposta: Esportes
                    
                    regras a serem seguidas:
                    Caso o usuario pergunte algo que não seja categorização de produtos, voce deve responder:
                    resposta: Não posso ajudalo, pois meu papel é apenas categorizar produtos.
                    """.formatted(categories);

            chatRequest(user, system);
        }

    }


    public static void chatRequest(String user, String system){
        var key = System.getenv("OPENAI_API_KEY");

        var service = new OpenAiService(key);

        var request = ChatCompletionRequest.builder()
                .model(ModelType.GPT_3_5_TURBO.getName())
                .messages(Arrays.asList(
                        new ChatMessage(ChatMessageRole.USER.value(), user),
                        new ChatMessage(ChatMessageRole.SYSTEM.value(), system)
                ))
                .n(5)
                .build();

        service.createChatCompletion(request)
                .getChoices()
                .forEach(m -> System.out.println(m.getMessage().getContent()));
    }

}
