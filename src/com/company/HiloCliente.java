package com.company;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

import static com.company.Main.cerrartodo;

public class HiloCliente extends Thread{
    String nick;
    Scanner teclao = new Scanner(System.in);
    boolean bucleinfinito;
    private final Socket socket;
    //private ServerSocket serverSocket;
    BufferedWriter bufferedWriter;
    //BufferedReader bufferedReader;
    public HiloCliente(Socket socket, String nick){
        this.socket=socket;
        this.nick=nick;
        bucleinfinito=true;
        try {
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            //this.bufferedReader = new BufferedReader(new InputStreamReader())
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    void enviarmensaje(String mensaje){
        try {
            bufferedWriter.write(mensaje);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    String enviarmensaje(){
        String mensaje=teclao.next();
        mensaje=nick+": "+mensaje;
        try {
            bufferedWriter.write(mensaje);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        teclao.nextLine();
        return mensaje;
    }
    private void cerrar(){
        bucleinfinito=false;
        if (bufferedWriter!=null){
            try {
                bufferedWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        cerrartodo();

    }
    @Override
    public void run() {
        String mensajecerrar;
        enviarmensaje(nick);
        while (bucleinfinito){
            if (!socket.isConnected()){
                cerrartodo();
            }
            System.out.println("Introduce el siguiente mensaje...");
           
        }
    }
}
