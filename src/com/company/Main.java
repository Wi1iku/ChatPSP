package com.company;

import java.util.Scanner;

public class Main{



    public static void main(String[] args) {
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

