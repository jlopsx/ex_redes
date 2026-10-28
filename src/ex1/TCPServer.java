package ex1;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer {
    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(9001);
            Socket socket = serverSocket.accept();
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

            System.out.println("Cliente conectado de " + socket.getInetAddress().getHostName());

            String clienteMsg;
            String servMsg;

            while ((clienteMsg  = in.readLine()) != null) {
                System.out.println("cliente diz: " + clienteMsg);

                if (clienteMsg.equalsIgnoreCase("exit")) {
                    System.out.println("Servidor finalizado");
                    break;
                }

                System.out.println("Servidor: ");
                servMsg = input.readLine();
                out.println(servMsg);
            }
        }catch (Exception e){
            System.out.println("Erro ao tentar abrir o servidor: " + e.getMessage());
        }
        System.out.println("Servidor finalizado");
    }
}
