package com.globallive.globallive;

import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.net.Socket;
import java.util.Base64;

@Service
public class EmailService {

    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final int SMTP_PORT = 587;
    private static final String USERNAME = "vibecolombia26@gmail.com";
    private static final String PASSWORD = "nsipiazhpcwxptck";

    public void enviarConfirmacion(String to, String nombre, String productos, double total) {
        new Thread(() -> {
            try {
                Socket socket = new Socket(SMTP_HOST, SMTP_PORT);
                OutputStream out = socket.getOutputStream();

                String encodedUser = Base64.getEncoder().encodeToString(USERNAME.getBytes("UTF-8"));
                String encodedPass = Base64.getEncoder().encodeToString(PASSWORD.getBytes("UTF-8"));

                String mensaje = "Hola " + nombre + "!\n\n" +
                        "Tu pedido ha sido registrado exitosamente en GlobalLive.\n\n" +
                        "Resumen de tu orden:\n" + productos + "\n" +
                        "Total: $" + String.format("%,.0f", total) + "\n" +
                        "Envio: GRATIS\n" +
                        "Pago: Contra entrega\n\n" +
                        "Te notificaremos cuando tu pedido este en camino.\n\n" +
                        "Gracias por confiar en GlobalLive - Salud & Bienestar";

                sendCommand(out, "EHLO globallive.com");
                readResponse(socket);
                sendCommand(out, "AUTH LOGIN");
                readResponse(socket);
                sendCommand(out, encodedUser);
                readResponse(socket);
                sendCommand(out, encodedPass);
                readResponse(socket);
                sendCommand(out, "MAIL FROM:<" + USERNAME + ">");
                readResponse(socket);
                sendCommand(out, "RCPT TO:<" + to + ">");
                readResponse(socket);
                sendCommand(out, "DATA");
                readResponse(socket);

                String subject = Base64.getEncoder().encodeToString("Pedido Confirmado - GlobalLive".getBytes("UTF-8"));

                sendCommand(out, "From: GlobalLive <" + USERNAME + ">");
                sendCommand(out, "To: " + to);
                sendCommand(out, "Subject: =?UTF-8?B?" + subject + "?=");
                sendCommand(out, "Content-Type: text/plain; charset=UTF-8");
                sendCommand(out, "");
                sendCommand(out, mensaje);
                sendCommand(out, ".");
                readResponse(socket);
                sendCommand(out, "QUIT");
                socket.close();

                System.out.println("Correo enviado a: " + to);
            } catch (Exception e) {
                System.out.println("Error enviando correo: " + e.getMessage());
            }
        }).start();
    }

    private void sendCommand(OutputStream out, String command) throws Exception {
        out.write((command + "\r\n").getBytes("UTF-8"));
        out.flush();
    }

    private void readResponse(Socket socket) throws Exception {
        byte[] buffer = new byte[4096];
        int read = socket.getInputStream().read(buffer);
        String response = new String(buffer, 0, read, "UTF-8");
        if (response.startsWith("5")) {
            throw new Exception("SMTP Error: " + response);
        }
    }
}