package kz.ncanode.interaction.interactors;

import com.rabbitmq.client.*;
import kz.ncanode.interaction.InteractionServiceProvider;
import kz.ncanode.interaction.Interactor;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

public class RabbitMqInteractor implements Interactor {
    InteractionServiceProvider provider = null;

    public RabbitMqInteractor(InteractionServiceProvider provider) {
        this.provider = provider;
    }

    @Override
    public void interact() {

        // getting config
        String host      = provider.config.get("rabbitmq", "host");
        String queueName = provider.config.get("rabbitmq", "queue_name");
        int port = Integer.valueOf(provider.config.get("rabbitmq", "port"));

        // Creating connection
        ConnectionFactory cf = new ConnectionFactory();
        cf.setHost(host);
        cf.setPort(port);

        provider.out.write("Trying to connect AMQP server " + host + ":" + port + "...");

        // Creating queue
        try {
            Connection conn = cf.newConnection();
            Channel channel = conn.createChannel();

            provider.out.write("Listening queue:" + queueName);
            channel.queueDeclare(queueName, false, false, true, null);

            // Create consumer
            Consumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    super.handleDelivery(consumerTag, envelope, properties, body);

                    String message = new String(body, StandardCharsets.UTF_8);

                    try {
                        JSONObject requestJson = (JSONObject)(new JSONParser()).parse(message);

                        long requestId  = (Long)requestJson.get("request_id");
                        String replyTo = (String)requestJson.get("reply_to");

                        if (requestId < 1) {
                            provider.err.write("request_id not specified");
                        }

                        if (replyTo == null) {
                            provider.err.write("reply_to not specified");
                        }

                        JSONObject response = provider.api.process(requestJson);
                        response.put("request_id", requestId);
                        response.put("reply_to", replyTo);

                        // Send response
                        channel.queueDeclare(replyTo, false, false, true, null);
                        channel.basicPublish("", replyTo, null, response.toJSONString().getBytes(StandardCharsets.UTF_8));

                    } catch (ParseException e) {
                        provider.err.write("Invalid request given. JSON parsing failed.");
                    } catch (ClassCastException e) {
                        provider.err.write("Invalid parameter given. request_id or reply_to");
                    } finally {
                        System.gc();
                    }

                }
            };

            channel.basicConsume(queueName, consumer);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }


    }
}
