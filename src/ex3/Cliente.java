package ex3;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Cliente {

    private static final String HOST = "localhost";
    private static final int PORTA = 9003;

    public static void main(String[] args) {

        if (args.length != 1) {
            System.out.println("Uso: java ClienteImagem caminho-da-imagem");
            return;
        }

        Path imagem = Paths.get(args[0]);

        if (!Files.isRegularFile(imagem)) {
            System.err.println("Ficheiro não encontrado: " + imagem);
            return;
        }

        try (Socket socket = new Socket(HOST, PORTA);
             BufferedInputStream arquivo = new BufferedInputStream(
                     new FileInputStream(imagem.toFile()));
             DataOutputStream saida = new DataOutputStream(
                     new BufferedOutputStream(socket.getOutputStream()));
             DataInputStream resposta = new DataInputStream(
                     socket.getInputStream())) {

            long tamanho = Files.size(imagem);

            // Envia cabeçalho
            saida.writeUTF(imagem.getFileName().toString());
            saida.writeLong(tamanho);

            // Envia os bytes da imagem sem alterações
            byte[] buffer = new byte[8192];
            int quantidade;

            while ((quantidade = arquivo.read(buffer)) != -1) {
                saida.write(buffer, 0, quantidade);
            }

            saida.flush();

            // Aguarda confirmação do servidor
            System.out.println(resposta.readUTF());

        } catch (IOException e) {
            System.err.println("Erro ao enviar imagem: " + e.getMessage());
        }
    }
}