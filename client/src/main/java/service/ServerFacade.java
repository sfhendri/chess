package service;

import chess.ChessGame;
import com.google.gson.Gson;
import model.AuthData;
import model.GameData;

import java.net.URI;
import java.net.http.*;
import java.util.HashMap;
import java.util.Map;

public class ServerFacade {

    private final String serverUrl;
    private final HttpClient httpClient;

    public ServerFacade(String url) {
        serverUrl = url;
        httpClient = HttpClient.newHttpClient();
    }

    private <T> T makeRequest(String method, String path, Object requestBody, String authToken, Class<T> clazz) throws Exception {
        try {
            URI uri = new URI(serverUrl + path);
            var requestBuilder = HttpRequest.newBuilder(uri);

            if (authToken != null) {
                requestBuilder.header("Authorization", authToken);
            }

            if (requestBody != null) {
                String json = new Gson().toJson(requestBody);
                requestBuilder.header("Content-Type", "application/json");
                requestBuilder.method(method, HttpRequest.BodyPublishers.ofString(json));
            } else {
                requestBuilder.method(method, HttpRequest.BodyPublishers.noBody());
            }

            var request = requestBuilder.build();
            HttpResponse<String> httpResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (httpResponse.statusCode() >= 200 && httpResponse.statusCode() < 300) {
                if (clazz != null) {
                    return new Gson().fromJson(httpResponse.body(), clazz);
                }
                return null;
            }

            var message = (String) (new Gson().fromJson(httpResponse.body(), HashMap.class)).get("message");
            throw new Exception(message);
        } catch (Exception ex) {
            throw new Exception(ex.getMessage());
        }
    }
}
