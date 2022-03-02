package com.company;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Main {



    public static void main(String[] args) {
        ServerSocket serverSocket = null;
        BufferedWriter bufferEscritura = null;
        {
            try {
                bufferEscritura = new BufferedWriter(new FileWriter("archivolog",true));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        assert bufferEscritura != null;
        PrintWriter logger = new PrintWriter(bufferEscritura);

        try {
            serverSocket = new ServerSocket(1050);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Servidor inicializado");

        logger.println("Servidor inicializado "+LocalDateTime.now().format(DateTimeFormatter.ofPattern("'[Dia]' dd/MM/yyyy '[Hora del dia]' hh:mm:ss.SSS")));
        logger.flush();


        BufferedWriter finalBufferEscritura = bufferEscritura;
        Runtime.getRuntime().addShutdownHook(new Thread()
                                             {
        @Override
                                                 public void run(){
            logger.println("Servidor cerrado "+LocalDateTime.now().format(DateTimeFormatter.ofPattern("'[Dia]' dd/MM/yyyy '[Hora del dia]' hh:mm:ss.SSS")));
            logger.flush();
            logger.close();
            //Este close no tiene mucho sentido, no se puede hacer close de otra manera
            try {
                finalBufferEscritura.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


                                             }


        );
        while (true){
            assert serverSocket != null;
            if (serverSocket.isClosed()) break;
            Socket socket;
            try {
                socket = serverSocket.accept();
                if (socket!=null){
                    System.out.println(socket);
                    ManejadorCliente manejadorCliente = new ManejadorCliente(socket,logger);
                    manejadorCliente.start();
                }

            } catch (IOException e) {
                e.printStackTrace();

            }
        }
        //logger.println(manejadorCliente.getLogg());


    }

}
