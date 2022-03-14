package com.company;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

import static com.company.Main.cerrartodo;
import java.awt.Color;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;

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
    Style stylemd;
    static BufferedWriter bufferedWriterstatic;
    ObjectOutputStream enviarobjeto;
    ObjectInputStream recibirobjeto;
    JTextPane jTextPane;
    //BufferedReader bufferedReader;
    public HiloCliente(Socket socket, String nick, PublicKey clavepublica, ObjectInputStream recibirobjeto, ObjectOutputStream enviarobjeto, JTextPane jTextPane){
        try {
            
            this.cipher = Cipher.getInstance("RSA");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException ex) {
            Logger.getLogger(HiloCliente.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.jTextPane=jTextPane;
        this.socket=socket;
        this.nick=nick;
        this.clavepublica = clavepublica;
        this.recibirobjeto=recibirobjeto;
        this.enviarobjeto=enviarobjeto;
        //StyleConstants.setForeground(stylenegro, Color.black);
        bucleinfinito=true;
         stylemd= jTextPane.addStyle("", null);
         StyleConstants.setForeground(stylemd, Color.pink);
       /* try {
            
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }*/
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
    void enviarpaquetemensaje(String mensaje){
        String mensaje2 = mensaje;
        try {
            byte[] mensajeCifrado = cipher.doFinal(mensaje.getBytes());
            enviarobjeto.writeObject(mensajeCifrado);
            enviarobjeto.flush();
            if(mensaje2.startsWith("!!md")){
                mensaje2=mensaje2.substring(4);
                mensaje2 = "Tu a"+mensaje2+"\n";
                           jTextPane.getStyledDocument().insertString(jTextPane.getStyledDocument().getLength(), mensaje2,stylemd);

            }else if (mensaje2.startsWith("!!")){
                mensaje2=mensaje2+"\n";
                jTextPane.getStyledDocument().insertString(jTextPane.getStyledDocument().getLength(), mensaje2, null);
            }else{
            mensaje2= nick+": "+mensaje2+"\n";
           jTextPane.getStyledDocument().insertString(jTextPane.getStyledDocument().getLength(), mensaje2, null);
            }
           
           newmesage = true;

                    Main.jScrollPane2.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
                        @Override
                        public void adjustmentValueChanged(AdjustmentEvent e) {
                            if (newmesage) {
                                e.getAdjustable().setValue(e.getAdjustable().getMaximum());
                                newmesage = false;
                            }
                        }
                    });
        } catch (IllegalBlockSizeException | BadPaddingException | IOException | BadLocationException  ex) {
            Logger.getLogger(HiloCliente.class.getName()).log(Level.SEVERE, null, ex);
        }
        
       
    }
    void enviar1ermensaje(String mensaje){
        try {
            sleep(800);
            enviarobjeto.writeObject(clavepublica);
            System.out.println("objetoenviado");
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(HiloCliente.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
           // System.out.println(mensaje+"ovjetoooooo");
            Object object = recibirobjeto.readObject();
            //System.out.println(object+"objeto666");
           Main.clavepublicaservidor=(PublicKey)object;
            //System.out.println("ClavePublicaRecibida");
                    } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(HiloCliente.class.getName()).log(Level.SEVERE, null, ex);
        }
        //mensaje=mensaje+"\n";
        try {
            cipher.init(Cipher.ENCRYPT_MODE, Main.clavepublicaservidor);
            System.out.println("cifer init");
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
    void enviarmensajecerrar(){
        try {
            String mensaje = "%\"Ju6A9jI2js\"%";
            byte[] mensajeCifrado = cipher.doFinal((mensaje).getBytes());
                    
            enviarobjeto.writeObject(mensajeCifrado);
            enviarobjeto.flush();
            cerrartodo();
            
            
            
        }catch (IOException | IllegalBlockSizeException | BadPaddingException e) {
                e.printStackTrace();
            }
    }
    //Sin uso
     /*void enviarmensaje(String mensaje){
          mensaje=nick+": "+mensaje;
        try {
            bufferedWriter.write(mensaje);
            bufferedWriter.newLine();
            bufferedWriter.flush();
            mensaje=mensajemayorque(mensaje);
            
            // Main.jTextArea1.append(mensaje+"\n");
               newmesage=true;
                            
             Main.jScrollPane2.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {  
@Override
public void adjustmentValueChanged(AdjustmentEvent e) {  
    if(newmesage){
 e.getAdjustable().setValue(e.getAdjustable().getMaximum());
    newmesage=false;
            }
}});
                             
                             

        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/
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
    /*Sin uso
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
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        cerrartodo();

    }*/
    @Override
    public void run() {
        
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
