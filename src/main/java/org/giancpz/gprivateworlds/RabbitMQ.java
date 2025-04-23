package org.giancpz.gprivateworlds;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import org.bukkit.GameRule;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Random;

public class RabbitMQ implements IMessage
{
    private static final String EXCHANGE_NAME = "direct";
    private static final String MAIN = "main";

    ConnectionFactory factory;
    Connection connection;
    Channel channel;

    public Node node;

    @Override
    public void init(Node node)
    {
        this.node = node;
        switch (PluginConfig.Options().nodemode)
        {
            case MAIN:
                initMaster();
                break;
            case CLIENT:
                initClient();
                break;
            default:
                Main.Singleton().getLogger().severe("Undefined node method!");
                break;
        }
    }

    public void initMaster()
    {
        try {
            factory = new ConnectionFactory();
            connection = factory.newConnection();
            channel = connection.createChannel();
            channel.exchangeDeclare("centralExchange", EXCHANGE_NAME);
            channel.queueDeclare("centralQueue", false, false, false, null);
            channel.queueBind("centralQueue", "centralExchange", "central");

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String msg = new String(delivery.getBody(), StandardCharsets.UTF_8);
                node.Received(msg);
            };
            channel.basicConsume("centralQueue", true, deliverCallback, consumerTag -> {
            });

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void initClient()
    {
        try {
            factory = new ConnectionFactory();
            factory.setHost("localhost");
            factory.setPort(5672);
            factory.setUsername("guest");
            factory.setPassword("guest");
            factory.setVirtualHost("/");
            connection = factory.newConnection();
            channel = connection.createChannel();

            channel.exchangeDeclare("centralExchange", "direct");

            String clientId = node.nodename;
            String queueName = channel.queueDeclare().getQueue();
            channel.queueBind(queueName, "centralExchange", clientId);

            String mensaje = clientId + "=request";
            channel.basicPublish("centralExchange", "central", null, mensaje.getBytes(StandardCharsets.UTF_8));

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String msg = new String(delivery.getBody(), StandardCharsets.UTF_8);
                node.Received(msg);
            };
            channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public void Send(String to,String message)
    {
        try {
            channel.basicPublish("centralExchange", to, null, message.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}
