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
import java.net.Socket;
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

    public HiloLector(JTextArea jta, Socket socket, BufferedReader bufferedReader) {
        this.jTextArea1 = jta;
        this.socket = socket;
    }

    @Override
    public void run() {
        System.out.println("asd111111111" + socket.isConnected());
        System.out.println("asd222222222"+ socket.isClosed());
        while (!socket.isClosed()) {
System.out.println("as3333333"+ socket.isClosed());
            try {
              
                if (!socket.isClosed() && !"".equals(mensaje)) {
                    //System.out.println("conectado111111111111111");

                    jTextArea1.append("\n");
                    //jTextArea1.append("111111111111111111");
                    //asd
                    mensaje = bufferedReader.readLine();
                    //System.out.println("test1");
                    
                    if (mensaje != null) {
                        mensaje = mensajemayorque(mensaje);
                    
                    jTextArea1.append(mensaje);
                    System.out.println(mensaje);
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
            }
        }

        System.out.println(this.getName() + " hilo cerrado11111111111");
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
}
