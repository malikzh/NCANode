package kz.ncanode.interaction.interactors;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import kz.ncanode.api.ApiServiceProvider;
import kz.ncanode.api.core.ApiStatus;
import kz.ncanode.info.InfoServiceProvider;
import kz.ncanode.interaction.InteractionServiceProvider;
import kz.ncanode.interaction.Interactor;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class HttpInteractor implements Interactor {
    InteractionServiceProvider provider = null;

    public HttpInteractor(InteractionServiceProvider provider) {
        this.provider = provider;
    }

    @Override
    public void interact() {
        String ip = provider.config.get("http", "ip");
        int port = Integer.valueOf(provider.config.get("http", "port"));

        provider.out.write("Starting HTTP server on " + ip + ":" + port + "...");

        InetSocketAddress inetSocketAddress = new InetSocketAddress(ip, port);

        try {
            HttpServer http = HttpServer.create(inetSocketAddress, 0);
            http.createContext("/", new HttpApiHandler());
            http.start();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    protected class HttpApiHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            ApiServiceProvider api = HttpInteractor.this.provider.api;
            InfoServiceProvider info = HttpInteractor.this.provider.info;

            int responseCode = HttpURLConnection.HTTP_OK;

            // headers
            Headers headers = exchange.getResponseHeaders();
            headers.add("X-Powered-By", info.getFullName());

            // streams
            OutputStream resp = exchange.getResponseBody();
            InputStream req = exchange.getRequestBody();

            // Принимаем только POST запросы
            if (!exchange.getRequestMethod().equals("POST")) {
                headers.add("Content-Type", "text/plain; charset=UTF-8");
                String respString = info.getFullName() + " is started. Please use POST request method for work with API.";

                exchange.sendResponseHeaders(responseCode, respString.length());
                resp.write(respString.getBytes());
                resp.close();
            } else {
                // handle request
                String response = "";
                headers.add("Content-Type", "application/json; charset=UTF-8");

                String contentTypeHeader = exchange.getRequestHeaders().getFirst("Content-Type");

                if (contentTypeHeader == null || !exchange.getRequestHeaders().getFirst("Content-Type").equals("application/json")) {
                    response = "Invalid header Content-Type. Please set Content-Type to application/json";
                } else {
                    // read request body
                    StringBuilder stringBuilder = new StringBuilder();
                    String line = null;

                    try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(req, "UTF-8"))) {
                        while ((line = bufferedReader.readLine()) != null) {
                            stringBuilder.append(line);
                        }
                    }

                    String requestBody = stringBuilder.toString();

                    // Process request
                    try {
                        JSONObject requestJson = (JSONObject) (new JSONParser()).parse(requestBody);
                        JSONObject responseJson = api.process(requestJson);
                        responseCode = (int) responseJson.getOrDefault("httpCode", responseCode);
                        response = responseJson.toJSONString();
                    } catch (ParseException e) {
                        JSONObject re = new JSONObject();
                        re.put("status", ApiStatus.STATUS_REQUEST_PARSE_ERROR);
                        re.put("message", "JSON parsing error");
                        response = re.toJSONString();
                        responseCode = HttpURLConnection.HTTP_BAD_REQUEST;
                    } catch (Exception e) {
                        JSONObject re = new JSONObject();
                        re.put("status", ApiStatus.STATUS_API_ERROR);
                        re.put("message", e.getMessage());
                        response = re.toJSONString();
                        responseCode = HttpURLConnection.HTTP_INTERNAL_ERROR;
                    }
                }

                byte[] respBytes = response.getBytes(StandardCharsets.UTF_8);

                exchange.sendResponseHeaders(responseCode, respBytes.length);
                resp.write(respBytes);
                resp.close();
                req.close();
                System.gc();
            }

        }
    }
}
