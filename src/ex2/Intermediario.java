package ex2;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.io.IOException;

public class Intermediario {

    private static final String HOST = "localhost";
    private static final int PORTA = 9002;

    public static void main(String[] args) {

        try (
                Socket socket = new Socket(HOST, PORTA);

                BufferedReader teclado = new BufferedReader(
                        new InputStreamReader(System.in)
                );

                BufferedReader entrada = new BufferedReader(
                        new InputStreamReader(socket.getInputStream())
                );

                PrintWriter saida = new PrintWriter(
                        socket.getOutputStream(), true
                )
        ) {

            System.out.println("Conectado ao servidor!");

            // Thread responsável por receber mensagens
            Thread receptor = new Thread(() -> {

                try {
                    String mensagem;

                    while ((mensagem = entrada.readLine()) != null) {
                        System.out.println(mensagem);
                    }

                } catch (IOException e) {
                    System.out.println("Erro ao receber mensagens.");
                }

            });

            receptor.setDaemon(true);
            receptor.start();

            // Envio de mensagens
            String mensagem;

            while ((mensagem = teclado.readLine()) != null) {

                saida.println(mensagem);

                if (mensagem.equalsIgnoreCase("/sair")) {
                    System.out.println("A sair do chat...");
                    break;
                }
            }

        } catch (IOException e) {

            System.out.println(
                    "Não foi possível conectar ao servidor: "
                            + e.getMessage()
            );
        }
    }
}