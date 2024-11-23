package ru.otus.chat.server;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private final int port;
    private List<ClientHandler> clients;

    public Server(int port) {
        this.port = port;
        clients = new ArrayList<>();
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Сервер запущен на порту: " + port);
            while (true) {
                Socket socket = serverSocket.accept();
                subscribe(new ClientHandler(this, socket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void subscribe(ClientHandler clientHandler) {
        clients.add(clientHandler);
    }

    public synchronized void unsubscribe(ClientHandler clientHandler) {
        clients.remove(clientHandler);
    }

    public synchronized void broadcastMessage(String message) {
        for (ClientHandler client : clients) {
            client.sendMessage(message);
        }
    }


    public synchronized boolean userExistance (String username){
        for (ClientHandler c : clients) {
            if (c.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }
    public synchronized void privateMessage(ClientHandler sender, String message) {
        String[] messageArray = message.split(" ", 3);
        if (messageArray.length != 3) {
            sender.sendMessage("Неверная команда");
            return;
        }
        String recipient = messageArray[1];
        String privateMessage = messageArray[2];
        if (!this.isUserExists(recipient)) {
            sender.sendMessage("Данного пользователя '" + recipient + "' не существует.");
            return;
        }


    }

}
