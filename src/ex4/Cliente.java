package ex4;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Cliente {

    public static void main(String[] args) {

        String servidor = "www.aied.com.br";
        int porta = 80;

        try (Socket socket = new Socket(servidor, porta)) {

            System.out.println("Conectado ao servidor: " + servidor);

            // Envia a requisição HTTP pelo socket TCP
            OutputStream saida = socket.getOutputStream();

            String requisicao =
                    "GET / HTTP/1.1\r\n" +
                            "Host: " + servidor + "\r\n" +
                            "Connection: close\r\n" +
                            "\r\n";

            saida.write(requisicao.getBytes(StandardCharsets.US_ASCII));
            saida.flush();

            // Recebe a resposta HTTP
            BufferedReader entrada = new BufferedReader(
                    new InputStreamReader(
                            socket.getInputStream(),
                            StandardCharsets.UTF_8));

            String linha;

            System.out.println("\n--- Resposta HTTP ---");

            while ((linha = entrada.readLine()) != null) {
                System.out.println(linha);
            }

            System.out.println("\nFim da resposta.");

        } catch (Exception e) {
            System.err.println("Erro: " + e.getMessage());
        }
    }
}