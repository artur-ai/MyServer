package main;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server {
    private static final Logger logger = Logger.getLogger(Server.class.getName());
    private static final int PORT = 3000;

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            logger.info("Server started on port " + PORT + "," + "Waiting for users.....");

            while (true) {
                try (Socket clientSocket = serverSocket.accept()) {
                    logger.info("Client connected " + clientSocket.getInetAddress());

                    BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                    String requestLine = reader.readLine();
                    logger.info("Request: " + requestLine);

                    while (!(reader.readLine()).isEmpty()) {

                    }

                    String response;
                    byte[] fileContent;
                    String contentType;

                    if (requestLine == null || !requestLine.startsWith("GET")) {
                        response = "HTTP/1.1 405 Method Not Allowed\r\n\r\n";
                        clientSocket.getOutputStream().write(response.getBytes());
                        continue;
                    }

                    String uri = requestLine.split(" ")[1];
                    logger.info("RequestLine: " + requestLine);

                    if (uri.contains("index")) {
                        fileContent = Files.readAllBytes(Paths.get("src", "resources", "index.html"));
                        contentType = "text/html";
                    } else if (uri.contains("css")) {
                        fileContent = Files.readAllBytes(Paths.get("src","resources", "css", "style.css"));
                        contentType = "text/css";
                    } else {
                        String notFound = "<h1>404 Not Found</h1>";
                        fileContent = notFound.getBytes();
                        contentType = "text/html";
                    }

                    String headers = "HTTP/1.1 200 OK\r\n" + "Content-Type: " + contentType + "\r\n" +
                            "Content-Length: " + fileContent.length + "\r\n" + "\r\n";

                    OutputStream outputStream = clientSocket.getOutputStream();
                    outputStream.write(headers.getBytes());
                    outputStream.write(fileContent);
                    outputStream.flush();
                } catch (IOException exception) {
                    logger.log(Level.SEVERE, "Client connection error", exception);
                }
            }
        } catch (IOException exception) {
            logger.log(Level.SEVERE, "Error with server", exception);
        }
    }
}
