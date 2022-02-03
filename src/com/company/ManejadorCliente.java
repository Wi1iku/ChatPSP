package com.company;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ManejadorCliente extends Thread {
    public static ArrayList<ManejadorCliente> manejadorClientes= new ArrayList<>();
    int identificador=0;
    private Socket socket;
    private String nombre;
    private BufferedReader bufferLeer;
    private BufferedWriter bufferEscribir;
    boolean salido=false;
    public ManejadorCliente(Socket socket) {
        this.socket=socket;
        try {
            this.bufferLeer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferEscribir = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            cerrartodo();
        }
        try {
            this.nombre= bufferLeer.readLine();
        } catch (IOException e) {
            cerrartodo();
        }
        manejadorClientes.add(identificador,this);
        System.out.println("usuario añadido");
        broadcast("Servidor: "+nombre+" se ha unido al chat de grupo");
        identificador++;
    }

    @Override
    public void run() {
        String muchotexto =" ";
        while (socket.isConnected()){
            try {
                if(!salido) {
                    muchotexto = bufferLeer.readLine();
                }
                while (!salido){
                broadcast(muchotexto);}
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("error en conexion");
                cerrartodo();
            }
        }
    }
    private void broadcast(String mensaje){
        for (ManejadorCliente manejador:
             manejadorClientes) {
            if (!manejador.nombre.equals(this.nombre)){
                try {
                    manejador.bufferEscribir.write(mensaje);
                    manejador.bufferEscribir.newLine();
                    manejador.bufferEscribir.flush();
                } catch (IOException e) {
                    cerrartodo();
                }

            }
        }
    }
    private void cerrartodo(){
        try {
            salido=true;
            if(socket!=null){
                socket.close();
            }
            if (bufferEscribir != null) {
                bufferEscribir.close();
            }
            if (bufferLeer!=null){
                bufferLeer.close();
            }
        }catch (Exception e){
            System.out.println("Error servidor");
            System.out.println(e);
        }
    }
}
