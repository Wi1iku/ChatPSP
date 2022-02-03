/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.company;

import static com.company.Main.bufferedReader;
import static com.company.Main.cerrartodo;
import static com.company.Main.socket;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.Socket;
import javax.swing.JTextArea;

/**
 *
 * @author Usuario
 */
public class HiloLector extends Thread{
    JTextArea jTextArea1;
    Socket socket;
    BufferedReader buffer;
    public HiloLector(JTextArea jta,Socket socket, BufferedReader bufferedReader){
    this.jTextArea1=jta;
    this.socket=socket;
    }

    @Override
    public void run() {
        System.out.println("asd"+socket.isConnected());
    while (socket.isConnected()){
           try {
               jTextArea1.append("\n");
                //jTextArea1.append("111111111111111111");
                jTextArea1.append(bufferedReader.readLine());
                System.out.println(bufferedReader.readLine());
                
               


            } catch (IOException e) {
                e.printStackTrace();
                cerrartodo();
            }
        }
    }
    
}
