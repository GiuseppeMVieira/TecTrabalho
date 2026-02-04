import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.Scanner;
import javax.swing.*;

public class ClienteGUI {
    private static PrintStream saida;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Chat Cliente");
        JTextArea areaTexto = new JTextArea();
        JTextField campoEntrada = new JTextField();
        areaTexto.setEditable(false);

        frame.setLayout(new BorderLayout());
        frame.add(new JScrollPane(areaTexto), BorderLayout.CENTER);
        frame.add(campoEntrada, BorderLayout.SOUTH);

        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        try {
            Socket socket = new Socket("200.145.43.166", 1234);
            Scanner servidor = new Scanner(socket.getInputStream());
            saida = new PrintStream(socket.getOutputStream());

            // Thread para receber mensagens
            new Thread(() -> {
                while (servidor.hasNextLine()) {
                    areaTexto.append(servidor.nextLine() + "\n");
                }
            }).start();

            campoEntrada.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    String msg = campoEntrada.getText();
                    if (!msg.trim().isEmpty()) {
                        saida.println(msg);
                        campoEntrada.setText("");
                    }
                }
            });

        } catch (IOException e) {
            areaTexto.append("Erro ao conectar: " + e.getMessage());
        }
    }
}
