package br.com.dio.gerenciadortarefas;

import br.com.dio.gerenciadortarefas.repository.InMemoryBoardRepository;
import br.com.dio.gerenciadortarefas.service.BoardService;
import br.com.dio.gerenciadortarefas.web.ApiHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class App {
    public static void main(String[] args) throws IOException {
        int port = resolvePort(args);
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        // Monta as dependências manualmente (sem framework de injeção).
        BoardService boardService = new BoardService(new InMemoryBoardRepository());
        // Todo tráfego HTTP entra por este handler e é roteado por path/método.
        server.createContext("/", new ApiHandler(boardService));
        server.setExecutor(Executors.newFixedThreadPool(8));
        server.start();

        System.out.println("GerenciadorDeTarefas API rodando em http://localhost:" + port);
    }

    private static int resolvePort(String[] args) {
        if (args != null && args.length > 0) {
            return Integer.parseInt(args[0]);
        }
        String envPort = System.getenv("PORT");
        if (envPort != null && !envPort.isBlank()) {
            return Integer.parseInt(envPort);
        }
        return 8080;
    }
}
