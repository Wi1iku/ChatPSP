/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.company;

import static com.company.Main.bufferedReader;
import static com.company.Main.cerrartodo;
import static com.company.Main.socket;
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
import javax.swing.JTextArea;

/**
 *
 * @author Usuario
 */
public class HiloLector extends Thread {

    JTextArea jTextArea1;
    Socket socket;
    Boolean newmesage = false;
    //BufferedReader buffer;
    String mensaje;
    Cipher descifrador;
    PrivateKey claveprivadauser;
    ObjectInputStream recibirobjeto;
    public HiloLector(JTextArea jta, Socket socket, ObjectInputStream recibirobjeto, PrivateKey claveprivadauser) {
        this.jTextArea1 = jta;
        this.socket = socket;
        this.claveprivadauser= claveprivadauser;
        this.recibirobjeto=recibirobjeto;
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
            //Inicializa el buffer de recibir objetos, tiene que ir despues de un sleep.
           // recibirobjeto= new ObjectInputStream(socket.getInputStream());
        } catch (InterruptedException ex) {
            Logger.getLogger(HiloLector.class.getName()).log(Level.SEVERE, null, ex);
        }
        //System.out.println("asd111111111" + socket.isConnected());
        //System.out.println("asd222222222"+ socket.isClosed());
        while (!socket.isClosed()) {
//System.out.println("as3333333"+ socket.isClosed());
            try {
                if (!socket.isClosed()) {
                    jTextArea1.append("\n");                    
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
                    jTextArea1.append(mensaje);
                    System.out.println(mensaje+" mensaje broadcast");
                        }
                        else if (mensaje.startsWith("!!")) {
                            mensaje= mensaje.substring(2);
                            mensaje="[Comando] "+mensaje;
                            //mensaje=mensajemayorque(mensaje); no es necesario.
                                                jTextArea1.append(mensaje);
                            System.out.println(mensaje+" mensaje comando");
                        }
                        else{
                        
                        }
                    }

                    newmesage = true;

                    Main.jScrollPane1.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
                        public void adjustmentValueChanged(AdjustmentEvent e) {
                            if (newmesage) {
                                e.getAdjustable().setValue(e.getAdjustable().getMaximum());
                                newmesage = false;
                            }
                        }
                    });

                }
            } catch (IOException e) {
                e.printStackTrace();
                cerrartodo();
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(HiloLector.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalBlockSizeException ex) {
                Logger.getLogger(HiloLector.class.getName()).log(Level.SEVERE, null, ex);
            } catch (BadPaddingException ex) {
                Logger.getLogger(HiloLector.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        System.out.println(this.getName() + " hilo cerrado11111111111");
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
