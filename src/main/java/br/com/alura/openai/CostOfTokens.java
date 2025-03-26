package br.com.alura.openai;

import com.knuddels.jtokkit.Encodings;
import com.knuddels.jtokkit.api.ModelType;

import java.math.BigDecimal;

public class CostOfTokens {


    public static void main(String[] args) {
        var registry = Encodings.newDefaultEncodingRegistry();
        var enc = registry.getEncodingForModel(ModelType.GPT_3_5_TURBO);
        var count = enc.countTokens("Identifique o perfil de compra de cada cliente");
        var cost = new BigDecimal(count).divide(new BigDecimal(1000)).multiply(new BigDecimal(0.0010));
        System.out.println("Count Token: " + count);
        System.out.println("Cost: " + cost);

    }
}
