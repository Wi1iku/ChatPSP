package com.company;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import java.security.*;
import java.util.Scanner;

public class Main{



    public static void main(String[] args) {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(512);
            KeyPair claves = keyPairGenerator.generateKeyPair();
            PrivateKey claveprivada= claves.getPrivate();
            PublicKey clavepublida = claves.getPublic();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }


        try {
            Cipher cipher = Cipher.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }
        boolean abierto=true;
        Scanner teclao = new Scanner(System.in);
        String commando;
        Principal principal = new Principal();
        principal.start();
        while (abierto){
            commando=teclao.nextLine();
            commando=commando.toLowerCase();
            switch (commando){
                case "/!cerrar":
                case "/!close":
                case "/!stop":
                    abierto=false;
                    principal.abierto=false;
                    principal.comandocerrar();
                    break;
            }
        }
    }




    }

