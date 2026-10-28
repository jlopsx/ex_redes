package ex1;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class CliServer {
    public static void main(String[] args) {
        String endereco = "localhost";
        int port = 9001;

        try{
            Socket socket = new Socket(endereco, port);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

            String msgCliente;
            String msgServidor;

            while(true){
                System.out.print("Cliente: ");
                msgCliente = input.readLine();

                out.println(msgCliente);

                if (msgCliente.equalsIgnoreCase("EXIT")){
                    break;
                }

                msgServidor = in.readLine();
                if (msgServidor == null) {
                    System.out.println("Servidor encerrou conexa");
                    break;
                }
                System.out.println("Servidor: " + msgServidor);
            }
        }catch(Exception e){
            System.out.println("Erro ao tentar abrir o servidor: " + e.getMessage());
        }

        System.out.println("Servidor finalizado");
    }
}
