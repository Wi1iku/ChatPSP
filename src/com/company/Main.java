package com.company;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(1050);
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (true){
            assert serverSocket != null;
            if (!!serverSocket.isClosed()) break;
            Socket socket = null;
            try {
                System.out.println("test");
                socket = serverSocket.accept();
                if (socket!=null){
                    System.out.println(socket);
                    ManejadorCliente manejadorCliente = new ManejadorCliente(socket);
                    manejadorCliente.start();
                }
                System.out.println("test2");
            } catch (IOException e) {
                e.printStackTrace();

            }
        }
    }
}
