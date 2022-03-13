package com.company;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.net.Inet4Address;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;

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
    private ObjectOutputStream bufferObjetoSalida;
    private ObjectInputStream bufferObjetoEntrada;
    private PublicKey clavepublicacliente;
    private PrivateKey claveprivadaservidor;
    private PublicKey clavepublicaservidor;
    private Cipher cipherencriptar;
    private Cipher cipherdesencriptar;
    boolean salido = false;
    PublicKey aux;
    public ManejadorCliente(Socket socket, PrintWriter logger, PrivateKey claveprivadaservidor, PublicKey clavepublicaservidor) {
        this.socket = socket;
        this.logger = logger;
        try {
            this.bufferLeer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferEscribir = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferObjetoEntrada= new ObjectInputStream(socket.getInputStream());
            this.bufferObjetoSalida = new ObjectOutputStream(socket.getOutputStream());
            this.cipherencriptar=Cipher.getInstance("RSA");
            this.cipherdesencriptar=Cipher.getInstance("RSA");
            this.claveprivadaservidor=claveprivadaservidor;
            this.clavepublicaservidor=clavepublicaservidor;
            System.out.println("test");
             aux= (PublicKey)bufferObjetoEntrada.readObject();
            System.out.println("test");
        } catch (IOException e) {
            //System.out.println("Error en 1 ooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo");

            cerrartodo();

            System.out.println(e+"error1");
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        if (aux==null){
            try {
                System.out.println(aux);
                aux= (PublicKey)bufferObjetoEntrada.readObject();
                System.out.println(aux+" null?");
            } catch (IOException | ClassNotFoundException exception) {
                exception.printStackTrace();
            }
        }
        else {

            try {
                try {
                    //Esta parte del codigo espera a que el cliente le mande su clave publica
                    //para luego mandarle su clave publica al servidor e inicializa 2 ciphers usados
                    //para encriptar y desencripta
                    clavepublicacliente = aux;
                    System.out.println("test0");
                    cipherencriptar.init(Cipher.ENCRYPT_MODE, clavepublicacliente);
                    System.out.println("test1");
                    cipherdesencriptar.init(Cipher.DECRYPT_MODE, claveprivadaservidor);
                    System.out.println("test2");
                    bufferObjetoSalida.writeObject(clavepublicaservidor);
                    System.out.println("test3");
                    byte[] bytesnombre = (byte[]) bufferObjetoEntrada.readObject();
                    bytesnombre = cipherdesencriptar.doFinal(bytesnombre);
                    this.nombre = new String(bytesnombre);
                } catch (ClassNotFoundException | InvalidKeyException e) {
                    System.out.println("error");
                    e.printStackTrace();
                } catch (BadPaddingException | IllegalBlockSizeException e) {
                    e.printStackTrace();
                }

            } catch (IOException e) {
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
                   /* byte[] bytesnuevos = new byte[0];
                    try {
                        bytesnuevos = (byte[]) bufferObjetoEntrada.readObject();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                   clavepublicacliente= new String(bytesnuevos);
                   */
                    //Esta línea recibe el paquete, la pasa desencripta y luego la pasa a bytes
                    muchotexto = new String(cipherdesencriptar.doFinal((byte[]) bufferObjetoEntrada.readObject()));
                    muchotexto=nombre+" "+muchotexto;
                    totalmensajessesion++;
                }
                if (!salido) {
                    //Esta linea espera el mensaje de salida
                    if (muchotexto.equals("%\"Ju6A9jI2js\"%")) {
                        muchotexto = null;
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

            } catch (ClassNotFoundException | BadPaddingException | IllegalBlockSizeException e) {
                e.printStackTrace();
            }
        }
    }

    private void broadcast(String mensaje) {

        for (ManejadorCliente manejador :
                manejadorClientes) {
            if (!manejador.nombre.equals(this.nombre)) {
                try {
                    mensaje="%br%"+mensaje;
                    //Esta linea encripta el codigo con la clave publica del usuario previamente
                    // pasado a bytes para luego mandarla a cada uno de los usuarios
                    bufferObjetoSalida.writeObject(cipherencriptar.doFinal(mensaje.getBytes()));
                    bufferObjetoSalida.flush();
                } catch (IOException | BadPaddingException | IllegalBlockSizeException e) {
                    e.printStackTrace();
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
                ManejadorPrivado textoprivado = new ManejadorPrivado(socket,logger,claveprivadaservidor,clavepublicaservidor,bufferEscribir,userpriv);
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
