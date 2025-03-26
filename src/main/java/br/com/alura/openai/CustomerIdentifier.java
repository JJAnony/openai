package br.com.alura.openai;

import com.knuddels.jtokkit.Encodings;
import com.knuddels.jtokkit.api.ModelType;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

public class CustomerIdentifier {

    public static void main(String[] args) {
        var prompt = """
                Identifique o perfil de compra de cada cliente.
                
                A resposta deve ser:
                
                Cliente - descreva o perfil do cliente em três palavras
                """;

        var clients = loadClientFile();

        var model = ModelType.GPT_3_5_TURBO.getName();
        var maxResponseLength = 2048;
        var tokens = countTokens(clients);

        if (tokens > 4096 - maxResponseLength) {
            model = ModelType.GPT_3_5_TURBO_16K.getName();
        }

        System.out.println("Count Tokens: " + tokens);
        System.out.println("Model :" + model);

        var request = ChatCompletionRequest.builder()
                .model(model)
                .maxTokens(maxResponseLength)
                .messages(Arrays.asList(
                        new ChatMessage(ChatMessageRole.SYSTEM.value(), prompt),
                        new ChatMessage(ChatMessageRole.USER.value(), clients)
                )).build();

        var key = System.getenv("OPENAI_API_KEY");

        var service = new OpenAiService(key);

        service.createChatCompletion(request)
                .getChoices()
                .forEach(m -> System.out.println(m.getMessage().getContent()));


    }

    private static int countTokens(String prompt) {
        var registry = Encodings.newDefaultEncodingRegistry();
        var enc = registry.getEncodingForModel(ModelType.GPT_3_5_TURBO);

        return enc.countTokens(prompt);
    }


    private static String loadClientFile() {
        try {
            var path = Path.of(ClassLoader.getSystemResource("list_clients_10.csv").toURI());
            return Files.readAllLines(path).toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
