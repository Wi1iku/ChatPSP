package com.company;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Principal extends Thread{
    boolean abierto=true;
    PrintWriter logger;
    ServerSocket serverSocket = null;
    BufferedWriter bufferEscritura = null;
    PublicKey clavepublicaservidor;
    PrivateKey claveprivadaservidor;
    public Principal() {

    }

    @Override
    public void run() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(512);
            KeyPair claves = keyPairGenerator.generateKeyPair();
             claveprivadaservidor= claves.getPrivate();
             clavepublicaservidor = claves.getPublic();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }


        try {
            Cipher cipher = Cipher.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }


        {
            try {
                bufferEscritura = new BufferedWriter(new FileWriter("archivolog",true));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        assert bufferEscritura != null;
        logger = new PrintWriter(bufferEscritura);

        try {
            serverSocket = new ServerSocket(1050);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Servidor inicializado");

        logger.println("Servidor inicializado "+LocalDateTime.now().format(DateTimeFormatter.ofPattern("'[Dia]' dd/MM/yyyy '[Hora del dia]' HH:mm:ss.SSS")));
        logger.flush();

        BufferedWriter finalBufferEscritura = bufferEscritura;
        Runtime.getRuntime().addShutdownHook(new Thread()
                                             {
        @Override
                                                 public void run(){
            comandocerrar();
            System.out.println("Servidor cerrado");
            try {
                sleep(1200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                //Este close no tiene mucho sentido, lo dejo para que cierre el bufferedWriter pero no se si hace algo en realidad
                finalBufferEscritura.close();
            } catch (IOException exception) {
                exception.printStackTrace();
            }


        }
                                             }


        );
        while(abierto){
            assert serverSocket != null;
            if (serverSocket.isClosed()) break;
            Socket socket;

            try {
                socket = serverSocket.accept();
                if (socket!=null){
                    System.out.println(socket);

                    ManejadorCliente manejadorCliente = new ManejadorCliente(socket,logger,claveprivadaservidor, clavepublicaservidor);
                    manejadorCliente.start();
                }

            } catch (IOException e) {
                if (abierto){
                e.printStackTrace();
                }else {
                    System.out.println("Socket cerrado");
                }

            }
        }



    }
    void comandocerrar(){
        System.out.println("Cerrando servidor...");
        logger.println("Servidor cerrado "+LocalDateTime.now().format(DateTimeFormatter.ofPattern("'[Dia]' dd/MM/yyyy '[Hora del dia]' HH:mm:ss.SSS")));
        logger.flush();
        logger.close();
        try {

            serverSocket.close();
            sleep(750);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException exception) {
            exception.printStackTrace();
        }

    }
}
