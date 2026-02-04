import java.io.*;
import java.net.*;
import java.util.*;

class Servidor{
    public static void main(String[] args) {
        ServerSocket serverSocket = null;

        try {
            serverSocket = new ServerSocket(1234);
        } catch (IOException e) {
            System.out.println("Could not listen on port: " + 1234 + ", " + e);
            System.exit(1);
        }

        for (int i = 0; i < 10000; i++) {
            try {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Cliente conectado!");
                new Servindo(clientSocket).start();
            } catch (IOException e) {
                System.out.println("Accept failed: " + 1234 + ", " + e);
                System.exit(1);
            }
        }

        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class Servindo extends Thread {
  private Socket clientSocket;
  private static List<PrintStream> outputStreams = new ArrayList<>();
  private static Map<PrintStream, String> clientNames = new HashMap<>();
  private static Map<PrintStream, String> clientColors = new HashMap<>();

  // Lista de cores ANSI para terminal
  private static final String[] COLORS = {
      "\u001B[31m", // vermelho
      "\u001B[32m", // verde
      "\u001B[33m", // amarelo
      "\u001B[34m", // azul
      "\u001B[35m", // magenta
      "\u001B[36m"  // ciano
  };
  private static final String RESET = "\u001B[0m";

  private static int colorIndex = 0;

  Servindo(Socket clientSocket) {
      this.clientSocket = clientSocket;
  }

  public void run() {
      try {
          Scanner is = new Scanner(clientSocket.getInputStream());
          PrintStream os = new PrintStream(clientSocket.getOutputStream(), true);

          os.println("Digite seu nome: ");
          String name = is.nextLine();

          String assignedColor;
          synchronized (outputStreams) {
              assignedColor = COLORS[colorIndex % COLORS.length];
              colorIndex++;
              outputStreams.add(os);
              clientNames.put(os, name);
              clientColors.put(os, assignedColor);
          }

          String inputLine;
          while (is.hasNextLine()) {
              inputLine = is.nextLine();
              synchronized (outputStreams) {
                  for (PrintStream out : outputStreams) {
                      String coloredName = clientColors.get(os) + name + RESET;
                      out.println(coloredName + ": " + inputLine);
                  }
              }
          }

          synchronized (outputStreams) {
              outputStreams.remove(os);
              clientNames.remove(os);
              clientColors.remove(os);
          }
          os.close();
          is.close();
          clientSocket.close();

      } catch (IOException | NoSuchElementException e) {
          System.out.println("Conexão encerrada com cliente.");
      }
  }
}