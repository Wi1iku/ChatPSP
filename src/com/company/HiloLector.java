/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.company;

import static com.company.Main.bufferedReader;
import static com.company.Main.cerrartodo;
import static com.company.Main.socket;
import java.awt.Color;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.swing.JButton;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;

/**
 *
 * @author Usuario
 */
public class HiloLector extends Thread {

    //JTextArea jTextArea1;
    JTextPane jTextPane;
    Socket socket;
    Boolean newmesage = false;
    //BufferedReader buffer;
    String mensaje;
    Cipher descifrador;
    PrivateKey claveprivadauser;
    Style stylenegro;
    Style stylemd;
    Style stylecmd;
    ObjectInputStream recibirobjeto;
    JButton jButton2;
    public HiloLector(JTextPane jTextPane, Socket socket, ObjectInputStream recibirobjeto, PrivateKey claveprivadauser, JButton jButton2) {
        //this.jTextArea1 = jta;
        this.jTextPane = jTextPane;
        this.socket = socket;
        this.claveprivadauser= claveprivadauser;
        this.recibirobjeto=recibirobjeto;
        this.jButton2 = jButton2;
        stylenegro= jTextPane.addStyle("", null);
        stylemd= jTextPane.addStyle("", null);
        stylecmd=jTextPane.addStyle("", null);
        StyleConstants.setForeground(stylenegro, Color.black);
        StyleConstants.setForeground(stylemd, Color.pink);
        StyleConstants.setBackground(stylemd, Color.BLACK);
        StyleConstants.setForeground(stylecmd, Color.GREEN);
        try {
            //inicializa desfricador
            descifrador=Cipher.getInstance("RSA");
            descifrador.init(Cipher.DECRYPT_MODE, claveprivadauser);
            
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException  ex) {
            Logger.getLogger(HiloLector.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public void run() {
        try {
            //Este hilo si o si se tiene que inicializar mas tarde que el hilo cliente
            sleep(1200);
                        jTextPane.getStyledDocument().insertString(jTextPane.getStyledDocument().getLength(), "Conectado a la sala de chat\n/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\", stylecmd);
                        jButton2.setEnabled(true);
            //Inicializa el buffer de recibir objetos, tiene que ir despues de un sleep.
           // recibirobjeto= new ObjectInputStream(socket.getInputStream());
        } catch (InterruptedException ex) {
            Logger.getLogger(HiloLector.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BadLocationException ex) {
            Logger.getLogger(HiloLector.class.getName()).log(Level.SEVERE, null, ex);
        }
        //System.out.println("asd111111111" + socket.isConnected());
        //System.out.println("asd222222222"+ socket.isClosed());
        while (!socket.isClosed()) {
//System.out.println("as3333333"+ socket.isClosed());
            try {
                if (!socket.isClosed()) {
                    jTextPane.getStyledDocument().insertString(jTextPane.getStyledDocument().getLength(), "\n", stylenegro);
                    //jTextArea1.append("\n");                    
                  //Esta línea recibe el paquete, la pasa desencripta y luego la pasa a bytes
                  // para luego crear un string nuevo
                    System.out.println("intento leer objeto");
                    Object object = recibirobjeto.readObject();
                    System.out.println("objeto leido");
                    byte[] paquetenuevo = (byte[])object;
                    paquetenuevo=descifrador.doFinal(paquetenuevo);
                    mensaje= new String(paquetenuevo);
                    System.out.println(mensaje);
                    
                    if (mensaje != null) {
                        if(mensaje.startsWith("%br%")){
                            mensaje=mensaje.substring(4);
                        mensaje = mensajemayorque(mensaje);
                        System.out.println(mensaje);
                        jTextPane.getStyledDocument().insertString(jTextPane.getStyledDocument().getLength(), mensaje, stylenegro);
                        System.out.println(mensaje+" mensaje broadcast");
                        }
                        else if (mensaje.startsWith("!!")) {
                            mensaje= mensaje.substring(2);
                            mensaje="[Comando] "+mensaje;
                            //mensaje=mensajemayorque(mensaje); no es necesario.
                                                jTextPane.getStyledDocument().insertString(jTextPane.getStyledDocument().getLength(), mensaje, stylecmd);
                            System.out.println(mensaje+" mensaje comando");
                        }else if(mensaje.startsWith("%md%")){
                            mensaje = mensaje.substring(4);
                            System.out.println("han mandado md");
                           jTextPane.getStyledDocument().insertString(jTextPane.getStyledDocument().getLength(), mensaje, stylemd);
                        }
                        else{
                        
                        }
                    }

                    newmesage = true;

                    Main.jScrollPane2.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
                        public void adjustmentValueChanged(AdjustmentEvent e) {
                            if (newmesage) {
                                e.getAdjustable().setValue(e.getAdjustable().getMaximum());
                                newmesage = false;
                            }
                        }
                    });

                }
            } catch (IOException e) {              
                cerrartodo();
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(HiloLector.class.getName()).log(Level.SEVERE, null, ex);
                                System.out.println("14");

            } catch (IllegalBlockSizeException ex) {
                Logger.getLogger(HiloLector.class.getName()).log(Level.SEVERE, null, ex);
                                System.out.println("13");

            } catch (BadPaddingException ex) {
                Logger.getLogger(HiloLector.class.getName()).log(Level.SEVERE, null, ex);
                                System.out.println("12");

            } catch (BadLocationException ex) {
                Logger.getLogger(HiloLector.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println("1");
            }
        }
        jButton2.setEnabled(false);
        System.out.println(this.getName() + " hilo cerrado");
    }

    String mensajemayorque(String mensaje) {
          //Metodo que se encarga de recortar el texto para que entre en el cuadro
          // de texto
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
}
