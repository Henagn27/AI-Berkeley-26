import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.responses.Response;
import com.openai.models.responses.ResponseCreateParams;
import com.openai.models.responses.ResponseOutputItem;
import com.openai.models.responses.ResponseOutputMessage;

public class OpenAITest {
    public static void main(String[] args) {
        String model = "gpt-5.5";

        System.out.println("Testing OpenAI model: " + model);

        OpenAIClient client = OpenAIOkHttpClient.fromEnv();

        ResponseCreateParams params = ResponseCreateParams.builder()
                .input("A basket has 3 apples and 2 oranges. If I add 4 apples, how many pieces of fruit are in the basket total? Answer in one short sentence.")
                .model(model)
                .build();

        Response response = client.responses().create(params);

        System.out.println("OpenAI API key works and the model processed the test question.");
        System.out.println("OpenAI said:");
        System.out.println(getOutputText(response));
    }

    private static String getOutputText(Response response) {
        StringBuilder outputText = new StringBuilder();

        for (ResponseOutputItem item : response.output()) {
            if (!item.isMessage()) {
                continue;
            }

            ResponseOutputMessage message = item.asMessage();

            message.content().forEach(content -> {
                if (content.isOutputText()) {
                    outputText.append(content.asOutputText().text());
                }
            });
        }

        if (outputText.length() == 0) {
            return "No text output was returned.";
        }

        return outputText.toString();
    }
}
