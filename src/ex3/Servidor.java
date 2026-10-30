package ex3;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.atomic.AtomicInteger;

public class Servidor {

    private static final int PORTA = 9003;
    private static final String PASTA = "imagens_recebidas";
    private static final AtomicInteger CONTADOR = new AtomicInteger(0);
    private static final long TAMANHO_MAXIMO = 50L * 1024 * 1024;

    public static void main(String[] args) {

        try {
            Files.createDirectories(Paths.get(PASTA));

            try (ServerSocket servidor = new ServerSocket(PORTA)) {
                System.out.println("Servidor de imagens ativo na porta " + PORTA);

                while (true) {
                    Socket socket = servidor.accept();

                    Thread thread = new Thread(() -> receberImagem(socket));
                    thread.start();
                }
            }

        } catch (IOException e) {
            System.err.println("Erro no servidor: " + e.getMessage());
        }
    }

    private static void receberImagem(Socket socket) {
        try (Socket conexao = socket;
             DataInputStream entrada = new DataInputStream(
                     new BufferedInputStream(conexao.getInputStream()));
             DataOutputStream resposta = new DataOutputStream(
                     conexao.getOutputStream())) {

            // Recebe nome e tamanho do ficheiro
            String nomeOriginal = entrada.readUTF();
            long tamanho = entrada.readLong();

            if (tamanho < 0 || tamanho > TAMANHO_MAXIMO) {
                resposta.writeUTF("ERRO: tamanho inválido.");
                return;
            }

            // Impede que o nome recebido determine diretórios
            String nome = Paths.get(nomeOriginal).getFileName().toString();

            if (nome.isBlank() || nome.equals(".") || nome.equals("..")) {
                resposta.writeUTF("ERRO: nome inválido.");
                return;
            }

            // Evita sobrescrever ficheiros já existentes
            String nomeFinal = CONTADOR.incrementAndGet() + "_" + nome;

            Path destino = Paths.get(PASTA, nomeFinal);

            try (BufferedOutputStream arquivo =
                         new BufferedOutputStream(
                                 Files.newOutputStream(destino,
                                         StandardOpenOption.CREATE_NEW,
                                         StandardOpenOption.WRITE))) {

                byte[] buffer = new byte[8192];
                long restantes = tamanho;

                while (restantes > 0) {
                    int quantidade = entrada.read(
                            buffer, 0,
                            (int) Math.min(buffer.length, restantes));

                    if (quantidade == -1) {
                        throw new IOException("Conexão interrompida durante a transferência.");
                    }

                    arquivo.write(buffer, 0, quantidade);
                    restantes -= quantidade;
                }
            } catch (IOException e) {
                Files.deleteIfExists(destino);
                throw e;
            }

            System.out.println("Imagem recebida: " + destino + " (" + tamanho + " bytes)");

            resposta.writeUTF("OK: imagem salva como " + nomeFinal);

        } catch (IOException e) {
            System.err.println("Erro ao receber imagem: " + e.getMessage());
        }
    }
}