package com.company;

import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ManejadorPrivado extends ManejadorCliente{
    BufferedWriter bufferedWriter;
    ManejadorCliente usuarioaescribir;
    public ManejadorPrivado(Socket socket, PrintWriter logger, BufferedWriter bufferedWriter, ManejadorCliente usuarioaescribir) {
        super(socket, logger);
        this.bufferedWriter=bufferedWriter;
        this.usuarioaescribir=usuarioaescribir;
        String entradaprivado= LocalDateTime.now().format(DateTimeFormatter.ofPattern("'[Dia]' dd/MM/yyyy '[Hora del dia]' HH:mm:ss.SSS"));
    }

    @Override
    public void run() {
       //TODO encontrar manera de mandar una diferenciacion entre md y broadcast normal.
        //
    }
}
