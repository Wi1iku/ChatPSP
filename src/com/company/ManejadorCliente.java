package com.company;

import java.io.*;
import java.net.Inet4Address;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class ManejadorCliente extends Thread {
    public static ArrayList<ManejadorCliente> manejadorClientes = new ArrayList<>();
    int identificador = 0;
    private final Socket socket;
    private String nombre;
    String salida;
    String entrada;
    PrintWriter logger;
    String ip;
    String logg;

    long totalmensajessesion = -1;


    private BufferedReader bufferLeer;
    private BufferedWriter bufferEscribir;
    boolean salido = false;

    public ManejadorCliente(Socket socket, PrintWriter logger) {
        this.socket = socket;
        this.logger = logger;
        try {
            this.bufferLeer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferEscribir = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            //System.out.println("Error en 1 ooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo");

            cerrartodo();

            System.out.println(e);
        }
        try {
            this.nombre = bufferLeer.readLine();
        } catch (IOException e) {
            //System.out.println("Error en 2 ÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑ");

            cerrartodo();

            System.out.println(e);
        }
        manejadorClientes.add(identificador, this);
        System.out.println("svsided:" + nombre + " conectado al servidor");
        //System.out.println("AAAA2222222222222222222");
        broadcast("Servidor: " + nombre + " se ha unido al chat de grupo");
        //System.out.println("Aaaaaa333333333333333333");
        identificador++;
    }



    @Override
    public void run() {
        try {
            Inet4Address inet4 = (Inet4Address) socket.getInetAddress();
            ip = inet4.toString();
        } catch (Exception e) {
            ip = "Error al guardar ip";
        }
        entrada = LocalDateTime.now().format(DateTimeFormatter.ofPattern("'[Dia]' dd/MM/yyyy '[Hora del dia]' HH:mm:ss.SSS"));
        String muchotexto = "";
        while (!socket.isClosed() && bufferLeer != null) {
            try {
                if (!salido) {

                    muchotexto = bufferLeer.readLine();
                    totalmensajessesion++;
                }
                if (!salido) {
                    if (muchotexto.equals("%\"Ju6A9jI2js\"%")) {
                        muchotexto = null;
                        //salida = LocalDateTime.now().format(DateTimeFormatter.ofPattern("'Dia ' dd/MM/yyyy 'Hora del dia' hh:mm:ss.SSS"));
                        this.cerrartodo();
                        salido = true;


                    } else if (muchotexto.startsWith("!!")){
                        muchotexto=muchotexto.substring(2);
                        System.out.println("cl: #cmd#: " + muchotexto);
                        comandocliente(muchotexto);

                    }
                    else {
                        System.out.println("cl: " + muchotexto);
                        broadcast(muchotexto);
                    }
                }
            } catch (IOException e) {
                cerrartodo();
                System.out.println("error en conexion de " + nombre);

            }
        }
    }

    private void broadcast(String mensaje) {

        for (ManejadorCliente manejador :
                manejadorClientes) {
            if (!manejador.nombre.equals(this.nombre)) {
                try {
                    manejador.bufferEscribir.write(mensaje);
                    manejador.bufferEscribir.newLine();
                    manejador.bufferEscribir.flush();
                } catch (IOException e) {
                    if (e.equals("java.io.IOException: Stream closed")) {
                        System.out.println("ce me ha cerrao bufferWritter");


                    } else {
                        e.printStackTrace();
                        cerrartodo();
                    }

                }

            }
        }
    }

    private void cerrartodo() {
        try {

            salida = LocalDateTime.now().format(DateTimeFormatter.ofPattern("'[Dia]' dd/MM/yyyy '[Hora del dia]' HH:mm:ss.SSS"));
            logg = "Usuario: " + nombre + " /conexion: " + entrada + " /desconexion: " + salida + " /total mensajes: " + totalmensajessesion + "/IP usuario: " + ip + " /";
            salido = true;
            logger.println(logg);
            logger.flush();

            //System.out.println("////////////////////////////////////////////////");
            if (bufferEscribir != null) {
                bufferEscribir.close();
                //  System.out.println("BufferEscribir de socket "+this.socket+" cerrado\n");
            }
            if (bufferLeer != null) {
                //System.out.println("BufferLeer de socket "+this.socket+" cerrado\n");
                bufferLeer.close();
            }
            //System.out.println("////////////////////////////////////////////////");
            socket.close();
            //System.out.println(this.socket+"     "+this.nombre+" nombre socket cerrado\n");
            //System.out.println("////////////////////////////////////////////////");
            manejadorClientes.remove(this);
            //PUEDE QUE ESTO SEA NECESARIO, ME VOY A CURRAR! EL CLIENTE DE MOMENTO VA BIEN

        } catch (Exception e) {
            System.out.println("Error servidor");
            System.out.println(e);
        }
    }
    private void comandocliente(String comadno){
        String[] aux;
        aux=comadno.split(" ");
        switch (aux[0]){
            case "md":
            case "mensajedirecto":
            case"mensaje_directo":
                mensajedirecto(aux);
                break;
            case "help":
            case "ayuda":
            case "man":
                help();
                break;
            case "cerrar":
            case "close":
            case "stop":

                break;
            default:
                comandonoreconocido();
                break;
        }
    }
    private void comandonoreconocido(){
        try {
            this.bufferEscribir.write("Comando no reconocido, para más ayuda, usa !!help");
            this.bufferEscribir.newLine();
            this.bufferEscribir.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void help(){
        try {
            this.bufferEscribir.write("Comandos disponibles:\n" +
                    "!!md + nombreusuario o !!mensajedirecto + nombreusuario manda un mensaje privado al usuario indicado\n" +
                    "!!close o !!cerrar le desconecta del servidor"
                    +"!!help despliega la ayuda de comandos\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void cerrar(){
        try {
            this.bufferEscribir.write("%\"Ju6A9jI2js\"%");
            this.bufferEscribir.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        cerrartodo();
    }
    private void mensajedirecto(String[] datos){
        if (datos.length!=2){
            try {
                this.bufferEscribir.write("Comando mal introducido, por favor introduce en comando con [!!md +(espacio)+nombredeusuario]");
                this.bufferEscribir.newLine();
                this.bufferEscribir.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {
            ManejadorCliente userpriv = null;
            for (ManejadorCliente mane:
                 manejadorClientes) {
                if (mane.equals(datos[1])){
                    userpriv=mane;
                }
            }
            if (userpriv!=null){
                ManejadorPrivado textoprivado = new ManejadorPrivado(socket,logger,bufferEscribir,userpriv);
            }else {

                try {
                    this.bufferEscribir.write("Error al abrir chat privado");
                    this.bufferEscribir.newLine();
                    this.bufferEscribir.flush();
                } catch (IOException e) {


                }
            }

        }
    }
}
