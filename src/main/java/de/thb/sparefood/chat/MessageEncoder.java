package de.thb.sparefood.chat;

import com.google.gson.Gson;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

public class MessageEncoder implements Encoder.Text<Message> {

    private static final Gson gson = new Gson();

    @Override
    public String encode(Message message) throws EncodeException {
        return gson.toJson(message);
    }

    @Override
    public void init(EndpointConfig config) {}

    @Override
    public void destroy() {}
}