package ex2;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Servidor {

    private static final int PORTA = 9002;

    private static final Set<ClienteHandler> clientes =
            ConcurrentHashMap.newKeySet();

    public static void main(String[] args) {

        System.out.println("A iniciar servidor de chat...");

        try (ServerSocket servidor = new ServerSocket(PORTA)) {

            System.out.println(
                    "Servidor iniciado na porta " + PORTA
            );

            while (true) {

                // Aguarda uma nova conexão
                Socket socket = servidor.accept();

                ClienteHandler cliente =
                        new ClienteHandler(socket);

                clientes.add(cliente);

                cliente.start();

                System.out.println(
                        "Novo cliente conectado: "
                                + socket.getRemoteSocketAddress()
                );
            }

        } catch (IOException e) {

            System.out.println(
                    "Erro no servidor: " + e.getMessage()
            );
        }
    }

    // Envia mensagens para todos os clientes
    private static void enviarTodos(
            String mensagem,
            ClienteHandler remetente
    ) {

        for (ClienteHandler cliente : clientes) {

            if (cliente != remetente) {
                cliente.enviar(mensagem);
            }
        }
    }

    // Classe responsável por cada cliente
    private static class ClienteHandler extends Thread {

        private final Socket socket;

        private BufferedReader entrada;
        private PrintWriter saida;

        private String nome = "Cliente";

        public ClienteHandler(Socket socket) {
            this.socket = socket;
        }

        // Envia uma mensagem ao cliente
        public synchronized void enviar(String mensagem) {

            if (saida != null) {
                saida.println(mensagem);
            }
        }

        @Override
        public void run() {

            try {

                entrada = new BufferedReader(
                        new InputStreamReader(
                                socket.getInputStream()
                        )
                );

                saida = new PrintWriter(
                        socket.getOutputStream(), true
                );

                // Solicita o nome do utilizador
                saida.println("Digite seu nome:");

                String nomeRecebido = entrada.readLine();

                if (nomeRecebido == null ||
                        nomeRecebido.trim().isEmpty()) {
                    return;
                }

                nome = nomeRecebido.trim();

                System.out.println(
                        nome + " entrou no chat."
                );

                enviarTodos(
                        "Servidor: " + nome + " entrou no chat.",
                        this
                );

                String mensagem;

                // Recebe mensagens continuamente
                while ((mensagem = entrada.readLine()) != null) {

                    if (mensagem.equalsIgnoreCase("/sair")) {
                        break;
                    }

                    if (mensagem.trim().isEmpty()) {
                        continue;
                    }

                    String texto = nome + ": " + mensagem;

                    System.out.println(texto);

                    enviarTodos(texto, this);
                }

            } catch (IOException e) {

                System.out.println(
                        "Conexão perdida com " + nome
                );

            } finally {

                // Remove o cliente da lista
                clientes.remove(this);

                // Informa os restantes clientes
                enviarTodos(
                        "Servidor: " + nome + " saiu do chat.",
                        this
                );

                try {
                    socket.close();
                } catch (IOException e) {
                    System.out.println(
                            "Erro ao fechar conexão."
                    );
                }

                System.out.println(
                        nome + " desconectado."
                );
            }
        }
    }
}