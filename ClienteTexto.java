import java.io.*;
import java.net.*;
import java.util.*;

public class ClienteTexto {
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("200.145.43.166", 1234); 
            Scanner teclado = new Scanner(System.in);
            Scanner servidor = new Scanner(socket.getInputStream());
            PrintStream saida = new PrintStream(socket.getOutputStream());

            new Thread(() -> {
                while (servidor.hasNextLine()) {
                    System.out.println(servidor.nextLine());
                }
            }).start();

            while (teclado.hasNextLine()) {
                saida.println(teclado.nextLine());
            }

            socket.close();
            teclado.close();
        } catch (IOException e) {
            System.out.println("Erro na conexão: " + e.getMessage());
        }
    }
}
