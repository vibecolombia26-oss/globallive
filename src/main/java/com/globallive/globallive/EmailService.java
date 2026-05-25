package com.globallive.globallive;

import org.springframework.stereotype.Service;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Base64;

@Service
public class EmailService {

    private static final String USERNAME = "vibecolombia26@gmail.com";
    private static final String PASSWORD = "nsipiazhpcwxptck";

    public void enviarConfirmacion(String to, String nombre, String productos, double total) {
        enviarCorreo(to, "✅ Pedido Confirmado - GlobalLive",
                "¡Hola " + nombre + "!\r\n\r\n" +
                        "Tu pedido ha sido registrado exitosamente en GlobalLive.\r\n\r\n" +
                        "📦 Resumen de tu orden:\r\n" + productos + "\r\n" +
                        "💰 Total: $" + String.format("%,.0f", total) + "\r\n" +
                        "🚚 Envío: GRATIS\r\n" +
                        "💚 Pago: Contra entrega\r\n\r\n" +
                        "Te notificaremos cuando tu pedido esté en camino.\r\n\r\n" +
                        "Gracias por confiar en GlobalLive · Salud & Bienestar 💚");
    }

    public void enviarActualizacion(String to, String asunto, String cuerpo) {
        enviarCorreo(to, asunto, cuerpo);
    }

    private void enviarCorreo(String to, String asunto, String cuerpo) {
        new Thread(() -> {
            try {
                // Conectar al servidor SMTP
                Socket socket = new Socket();
                socket.connect(new InetSocketAddress("smtp.gmail.com", 587), 10000);
                OutputStream out = socket.getOutputStream();

                readResponse(socket);
                sendCommand(out, "EHLO globallive.com");
                readResponse(socket);
                sendCommand(out, "STARTTLS");
                readResponse(socket);

                // Actualizar a conexión TLS
                SSLSocketFactory sslFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
                SSLSocket sslSocket = (SSLSocket) sslFactory.createSocket(
                        socket, socket.getInetAddress().getHostAddress(), socket.getPort(), true);
                out = sslSocket.getOutputStream();

                sendCommand(out, "EHLO globallive.com");
                readResponse(sslSocket);
                sendCommand(out, "AUTH LOGIN");
                readResponse(sslSocket);
                sendCommand(out, Base64.getEncoder().encodeToString(USERNAME.getBytes()));
                readResponse(sslSocket);
                sendCommand(out, Base64.getEncoder().encodeToString(PASSWORD.getBytes()));
                readResponse(sslSocket);
                sendCommand(out, "MAIL FROM:<" + USERNAME + ">");
                readResponse(sslSocket);
                sendCommand(out, "RCPT TO:<" + to + ">");
                readResponse(sslSocket);
                sendCommand(out, "DATA");
                readResponse(sslSocket);

                String subject = "=?UTF-8?B?" + Base64.getEncoder().encodeToString(asunto.getBytes("UTF-8")) + "?=";
                sendCommand(out, "From: GlobalLive <" + USERNAME + ">");
                sendCommand(out, "To: " + to);
                sendCommand(out, "Subject: " + subject);
                sendCommand(out, "MIME-Version: 1.0");
                sendCommand(out, "Content-Type: text/plain; charset=UTF-8");
                sendCommand(out, "");
                sendCommand(out, cuerpo);
                sendCommand(out, ".");
                readResponse(sslSocket);
                sendCommand(out, "QUIT");
                sslSocket.close();

                System.out.println("✅ Correo enviado a: " + to);
            } catch (Exception e) {
                System.out.println("❌ Error enviando correo: " + e.getMessage());
                e.printStackTrace();
            }
        }).start();
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