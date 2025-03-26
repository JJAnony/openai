package br.com.alura.openai;

import com.knuddels.jtokkit.api.ModelType;
import com.theokanning.openai.OpenAiHttpException;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.util.Arrays;
import java.util.stream.Collectors;

public class FeelingsAnalysis {

    public static void main(String[] args) {
        try {

            var dirReviews = Path.of("src/main/resources/reviews");
            var reviewsFiles = Files.walk(dirReviews, 1)
                    .filter(path -> path.toString().endsWith(".txt"))
                    .collect(Collectors.toList());

            for (Path file : reviewsFiles) {
                System.out.println("Iniciando analise do produto: " + file.getFileName());

                var response = sendRequest(file);
                saveAnalysis(file.getFileName().toString(), response);

                System.out.println("Analise Finalizada!");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String sendRequest(Path file) throws InterruptedException {
        var key = System.getenv("OPENAI_API_KEY");
        var service = new OpenAiService(key, Duration.ofSeconds(60));

        var systemPrompt = """
                Você é um analisador de sentimentos de avaliações de produtos.
                Escreva um parágrafo com até 50 palavras resumindo as avaliações e depois atribua qual o sentimento geral para o produto.
                Identifique também 3 pontos fortes e 3 pontos fracos identificados a partir das avaliações.
                
                #### Formato de saída
                Nome do produto:
                Resumo das avaliações: [resuma em até 50 palavras]
                Sentimento geral: [deve ser: POSITIVO, NEUTRO ou NEGATIVO]
                Pontos fortes: [3 bullets points]
                Pontos fracos: [3 bullets points]
                """;

        var userPrompt = loadFile(file);

        var request = ChatCompletionRequest
                .builder()
                .model(ModelType.GPT_4.getName())
                .messages(Arrays.asList(
                        new ChatMessage(ChatMessageRole.SYSTEM.value(), systemPrompt),
                        new ChatMessage(ChatMessageRole.USER.value(), userPrompt)))
                .build();

        var attemptTime = 1000 * 5;
        var attempt = 0;
        while (attempt++ != 3) {
            try {
                return service
                        .createChatCompletion(request)
                        .getChoices().get(0).getMessage().getContent();
            } catch (OpenAiHttpException ex) {
                switch (ex.statusCode) {
                    case 401 -> throw new RuntimeException("Erro com a chave da API!", ex);
                    case 429 -> {
                        System.out.println("RateLimit atingido! nova tentativa em instantes");
                        Thread.sleep(attemptTime);
                    }
                    case 500, 503 -> {
                        System.out.println("API fora do ar! nova tentativa em instantes");
                        Thread.sleep(attemptTime);
                    }

                }

            }
        }

        throw new RuntimeException("API fora do ar! Tentativas finalizadas sem sucesso!");
    }


    private static String loadFile(Path path) {
        try {
            return Files.readAllLines(path).toString();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao carregar o arquivo!", e);
        }
    }

    private static void saveAnalysis(String file, String analise) {
        try {
            var path = Path.of("src/main/resources/analysis/analysis_" + file);
            Files.writeString(path, analise, StandardOpenOption.CREATE_NEW);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao salvar o arquivo!", e);
        }
    }
}
