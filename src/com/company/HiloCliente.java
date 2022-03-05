package com.company;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

import static com.company.Main.cerrartodo;
import java.awt.Rectangle;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import javax.swing.JTextField;

public class HiloCliente extends Thread{
    Boolean newmesage=false;
    String nick;
    Scanner teclao = new Scanner(System.in);
    static boolean bucleinfinito;
    private final Socket socket;
    //private ServerSocket serverSocket;
    BufferedWriter bufferedWriter;
    static BufferedWriter bufferedWriterstatic;
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
    void enviar1ermensaje(String mensaje){
        try {
            bufferedWriter.write(mensaje);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    static void enviarmensajecerrar(){
        String mensaje = "%\"Ju6A9jI2js\"%";
        try {
            bufferedWriterstatic.write(mensaje);
            bufferedWriterstatic.newLine();
            bufferedWriterstatic.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
     void enviarmensaje(String mensaje){
          mensaje=nick+": "+mensaje;
        try {
            bufferedWriter.write(mensaje);
            bufferedWriter.newLine();
            bufferedWriter.flush();
            mensaje=mensajemayorque(mensaje);
             Main.jTextArea1.append(mensaje+"\n");
               newmesage=true;
                            
             Main.jScrollPane1.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {  
public void adjustmentValueChanged(AdjustmentEvent e) {  
    if(newmesage){
 e.getAdjustable().setValue(e.getAdjustable().getMaximum());
    newmesage=false;
            }
}});
                             
                             

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
     String mensajemayorque(String mensaje) {
        String mensaje1;
        String mensaje2;
        try {

            if (mensaje.length() > 65) {
                mensaje1 = mensaje.substring(0, 65);
                mensaje2 = mensaje.substring(65);
                mensaje2 = mensajemayorque(mensaje2);
                mensaje = mensaje1 + "\n" + mensaje2;
                //System.out.println(pasos);
                return mensaje;
            } else {
                return mensaje;
            }
        } catch (NullPointerException e) {
            System.out.println("");
            return "";
        }

    }
    //Sin uso
     String enviarmensaje(JTextField jTextField){
        
        String mensaje=jTextField.getText();
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
        if (bufferedWriterstatic!=null){
            try {
                bufferedWriterstatic.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        cerrartodo();

    }
    @Override
    public void run() {
        String mensajecerrar;
        enviar1ermensaje(nick);
        /*  No hace falta tener un bucle aqui.
        while (bucleinfinito){
            if (socket.isClosed()){
                cerrartodo();
            }
           
        }*/
        //System.out.println(this.getName()+" hilo cerrado");
    }
}
