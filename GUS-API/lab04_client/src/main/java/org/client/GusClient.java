package org.client;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class GusClient {
    private static final String BASE_URL = "https://bdl.stat.gov.pl/api/v1/data/by-variable/";
    private final HttpClient httpClient;
    public GusClient() {
        this.httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }
    public List<GusData> fetchData(int variableId, int year) throws IOException, InterruptedException {
        String url = String.format("%s%d?unit-level=2&year=%d&page-size=16", BASE_URL, variableId, year);
        System.out.println("Wysyłam zapytanie: " + url);
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(url))
                .header("Accept", "application/json")
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new IOException("Błąd pobierania danych: HTTP " + response.statusCode());
        }
        return parseJson(response.body());
    }

    private List<GusData> parseJson(String jsonBody) {
        List<GusData> results = new ArrayList<>();
        JSONObject root = new JSONObject(jsonBody);
        JSONArray jsonResults = root.getJSONArray("results");
        for (int i = 0; i < jsonResults.length(); i++) {
            JSONObject unit = jsonResults.getJSONObject(i);
            String name = unit.getString("name");
            JSONArray values = unit.getJSONArray("values");
            if (!values.isEmpty()) {
                JSONObject valObj = values.getJSONObject(0);
                double val = valObj.optDouble("val", 0.0);
                int year = valObj.getInt("year");
                results.add(new GusData(name, val, year));
            } else {
                System.out.println("Ostrzeżenie: Brak danych dla województwa: " + name);
            }
        }
        return results;
    }
}