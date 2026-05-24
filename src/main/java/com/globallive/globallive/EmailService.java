package com.globallive.globallive;

import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Base64;

@Service
public class EmailService {

    private static final String USERNAME = "vibecolombia26@gmail.com";
    private static final String PASSWORD = "nsipiazhpcwxptck";

    public void enviarConfirmacion(String to, String nombre, String productos, double total) {
        new Thread(() -> {
            try {
                Socket socket = new Socket();
                socket.connect(new InetSocketAddress("smtp.gmail.com", 587), 10000);
                OutputStream out = socket.getOutputStream();

                // Leer respuesta inicial
                readResponse(socket);

                // EHLO
                sendCommand(out, "EHLO globallive.com");
                readResponse(socket);

                // STARTTLS
                sendCommand(out, "STARTTLS");
                readResponse(socket);

                // Crear socket seguro
                socket = startTLS(socket);
                out = socket.getOutputStream();

                // EHLO otra vez
                sendCommand(out, "EHLO globallive.com");
                readResponse(socket);

                // AUTH LOGIN
                sendCommand(out, "AUTH LOGIN");
                readResponse(socket);
                sendCommand(out, Base64.getEncoder().encodeToString(USERNAME.getBytes()));
                readResponse(socket);
                sendCommand(out, Base64.getEncoder().encodeToString(PASSWORD.getBytes()));
                readResponse(socket);

                // MAIL FROM
                sendCommand(out, "MAIL FROM:<" + USERNAME + ">");
                readResponse(socket);

                // RCPT TO
                sendCommand(out, "RCPT TO:<" + to + ">");
                readResponse(socket);

                // DATA
                sendCommand(out, "DATA");
                readResponse(socket);

                // Contenido
                String subject = "=?UTF-8?B?" + Base64.getEncoder().encodeToString("✅ Pedido Confirmado - GlobalLive".getBytes("UTF-8")) + "?=";
                String body = "¡Hola " + nombre + "!\r\n\r\n" +
                        "Tu pedido ha sido registrado exitosamente en GlobalLive.\r\n\r\n" +
                        "📦 Resumen de tu orden:\r\n" + productos + "\r\n" +
                        "💰 Total: $" + String.format("%,.0f", total) + "\r\n" +
                        "🚚 Envío: GRATIS\r\n" +
                        "💚 Pago: Contra entrega\r\n\r\n" +
                        "Te notificaremos cuando tu pedido esté en camino.\r\n\r\n" +
                        "Gracias por confiar en GlobalLive · Salud & Bienestar 💚";

                sendCommand(out, "From: GlobalLive <" + USERNAME + ">");
                sendCommand(out, "To: " + to);
                sendCommand(out, "Subject: " + subject);
                sendCommand(out, "MIME-Version: 1.0");
                sendCommand(out, "Content-Type: text/plain; charset=UTF-8");
                sendCommand(out, "");
                sendCommand(out, body);
                sendCommand(out, ".");
                readResponse(socket);

                // QUIT
                sendCommand(out, "QUIT");
                socket.close();

                System.out.println("✅ Correo enviado a: " + to);
            } catch (Exception e) {
                System.out.println("❌ Error enviando correo: " + e.getMessage());
            }
        }).start();
    }

    private Socket startTLS(Socket socket) throws Exception {
        return socket;
        // Nota: En producción usar SSLSocket para TLS real
    }

    private void sendCommand(OutputStream out, String command) throws Exception {
        out.write((command + "\r\n").getBytes());
        out.flush();
    }

    private void readResponse(Socket socket) throws Exception {
        byte[] buffer = new byte[4096];
        int read = socket.getInputStream().read(buffer);
        if (read > 0) {
            String response = new String(buffer, 0, read);
            System.out.println("SMTP: " + response.trim());
        }
    }
}