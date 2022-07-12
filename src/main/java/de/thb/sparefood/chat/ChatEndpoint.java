package de.thb.sparefood.chat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;

@ServerEndpoint(value = "/chat", decoders = MessageDecoder.class, encoders = MessageEncoder.class)
public class ChatEndpoint {

  private static final HashMap<String, Session> userSessions = new HashMap<>();
  private static final Logger logger = LoggerFactory.getLogger(ChatEndpoint.class);

  @OnOpen
  public void onOpen(Session session) throws IOException {
    String userEmail = session.getUserPrincipal().getName();
    if (userEmail == null) {
      logger.error("A user tried to connect to the chat without a valid Bearer Token");
      session.close();
    }

    logger.info("User {} connected.", userEmail);
    userSessions.put(userEmail, session);
  }

  @OnMessage
  public void onMessage(Message message) throws IOException, EncodeException {
    String recipient = message.getRecipient();
    Session sessionOfRecipient = userSessions.get(recipient);

    if (sessionOfRecipient == null) {
      // user is not available. What to do?
      // maybe save it to the database and send the user all messages on connection
      return;
    }

    forwardMessageToRecipient(message, sessionOfRecipient);
  }

  private void forwardMessageToRecipient(Message message, Session sessionOfRecipient) {
    new Thread(
            () -> {
              try {
                sessionOfRecipient.getBasicRemote().sendObject(message);
              } catch (IOException | EncodeException e) {
                logger.error("Failed to forward message to user.", e);
              }
            })
        .start();
  }

  @OnClose
  public void onClose(Session session) throws IOException {
    String disconnectingUser = session.getUserPrincipal().getName();
    logger.error("User {} disconnected.", disconnectingUser);
    userSessions.values().remove(session);
    session.close();
  }

  @OnError
  public void onError(Session session, Throwable throwable) {
    logger.error("An error occurred at the chat! If we would only handle those...", throwable);
  }
}
