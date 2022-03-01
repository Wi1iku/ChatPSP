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
     void enviarmensaje(String mensaje){
          mensaje=nick+": "+mensaje;
        try {
            bufferedWriter.write(mensaje);
            bufferedWriter.newLine();
            bufferedWriter.flush();
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
        cerrartodo();

    }
    @Override
    public void run() {
        String mensajecerrar;
        enviar1ermensaje(nick);
        while (bucleinfinito){
            if (socket.isClosed()){
                cerrartodo();
            }
           
        }
        System.out.println(this.getName()+" hilo cerrado");
    }
}
