package com.company;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

import static com.company.Main.cerrartodo;
import java.awt.Rectangle;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.swing.JTextField;

public class HiloCliente extends Thread{
    Boolean newmesage=false;
    String nick;
    Scanner teclao = new Scanner(System.in);
    PublicKey clavepublica;
    static boolean bucleinfinito;
    private final Socket socket;
    Cipher cipher;
    //private ServerSocket serverSocket;
    BufferedWriter bufferedWriter;
    static BufferedWriter bufferedWriterstatic;
    ObjectOutputStream enviarobjeto;
    ObjectInputStream recibirobjeto;
    //BufferedReader bufferedReader;
    public HiloCliente(Socket socket, String nick, PublicKey clavepublica){
        try {
            
            this.cipher = Cipher.getInstance("RSA");
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(HiloCliente.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchPaddingException ex) {
            Logger.getLogger(HiloCliente.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.socket=socket;
        this.nick=nick;
        this.clavepublica = clavepublica;
        bucleinfinito=true;
        try {
            
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    void enviar1ermensajestring(String mensaje){
        try {
            bufferedWriter.write(mensaje);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    void enviar1ermensaje(String mensaje){
        try {
            sleep(20);
            enviarobjeto.writeObject(clavepublica);
            System.out.println("objetoenviado");
        } catch (IOException ex) {
            Logger.getLogger(HiloCliente.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(HiloCliente.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
           Main.clavepublicaservidor=(PublicKey)recibirobjeto.readObject();
            System.out.println("ClavePublicaRecibida");
                    } catch (IOException ex) {
            Logger.getLogger(HiloCliente.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(HiloCliente.class.getName()).log(Level.SEVERE, null, ex);
        }
        //mensaje=mensaje+"\n";
        try {
            cipher.init(Cipher.ENCRYPT_MODE, Main.clavepublicaservidor);
        } catch (InvalidKeyException ex) {
            Logger.getLogger(HiloCliente.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            byte[] mensajeCifrado = cipher.doFinal((mensaje).getBytes());
            enviarobjeto.writeObject(mensajeCifrado);
            enviarobjeto.flush();
        } catch (IOException | IllegalBlockSizeException | BadPaddingException ex) {
            Logger.getLogger(HiloCliente.class.getName()).log(Level.SEVERE, null, ex);
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
        try {
            enviarobjeto = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException ex) {
            Logger.getLogger(HiloCliente.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            //this.bufferedReader = new BufferedReader(new InputStreamReader())
            recibirobjeto= new ObjectInputStream(socket.getInputStream());
        } catch (IOException ex) {
            Logger.getLogger(HiloCliente.class.getName()).log(Level.SEVERE, null, ex);
        }
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
