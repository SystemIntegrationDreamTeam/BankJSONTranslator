/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.bankjsontranslator;

import com.google.gson.Gson;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import java.io.IOException;

/**
 *
 * @author Buhrkall
 */
public class Translator {

    static final String SENDING_QUEUE_NAME = "cphbusiness.bankJSON";
    static final String LISTENING_QUEUE_NAME = "BankJSONTranslatorQueue";
    static final String EXCHANGE_NAME = "TranslatorExchange";
    static final String REPLY_TO_HEADER = "NormalizerQueue";

    static String message = "";
    static Gson gson = new Gson();

    public static void main(String[] args) throws IOException {

        ConnectionFactory listeningFactory = new ConnectionFactory();
        listeningFactory.setHost("datdb.cphbusiness.dk");
        listeningFactory.setUsername("Dreamteam");
        listeningFactory.setPassword("bastian");
        
        ConnectionFactory sendingFactory = new ConnectionFactory();
        sendingFactory.setHost("datdb.cphbusiness.dk");
        sendingFactory.setUsername("Dreamteam");
        sendingFactory.setPassword("bastian");
        
        
        Connection listeningConnection = listeningFactory.newConnection();
        Connection sendingConnection = sendingFactory.newConnection();
        
        final Channel listeningChannel = listeningConnection.createChannel();
        final Channel sendingChannel = sendingConnection.createChannel();

        final BasicProperties props = new BasicProperties.Builder()
                .replyTo(REPLY_TO_HEADER)
                .build();
        
         listeningChannel.queueDeclare(LISTENING_QUEUE_NAME, false, false, false, null);
         listeningChannel.queueBind(LISTENING_QUEUE_NAME, EXCHANGE_NAME, "CphBusinessJSON");
        

        listeningChannel.queueDeclare(LISTENING_QUEUE_NAME, false, false, false, null);
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        Consumer consumer = new DefaultConsumer(listeningChannel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
                    throws IOException {
                message = new String(body, "UTF-8");
                System.out.println(" [x] Received '" + message + "'");

                String[] arr = message.split(",");
                
                int dashRemoved = Integer.parseInt(arr[0].replace("-", ""));

                Result res = new Result(dashRemoved, Integer.parseInt(arr[1]), Double.parseDouble(arr[2]), Integer.parseInt(arr[3]));

                String result = gson.toJson(res);
                System.out.println(result);

                sendingChannel.exchangeDeclare(SENDING_QUEUE_NAME, "fanout");
                String test = sendingChannel.queueDeclare().getQueue();
                sendingChannel.queueBind(test, SENDING_QUEUE_NAME, "");
                sendingChannel.basicPublish(SENDING_QUEUE_NAME, "", props, result.getBytes());

            }
        };
        listeningChannel.basicConsume(LISTENING_QUEUE_NAME, true, consumer);

    }

}
