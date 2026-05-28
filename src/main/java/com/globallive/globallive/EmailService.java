package com.globallive.globallive;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void enviarConfirmacion(String to, String nombre, String productos, double total) {
        String asunto = "✅ Pedido Confirmado - GlobalLive";
        String cuerpo = "¡Hola " + nombre + "!\n\n" +
                "Tu pedido ha sido registrado exitosamente en GlobalLive.\n\n" +
                "📦 Resumen de tu orden:\n" + productos + "\n" +
                "💰 Total: $" + String.format("%,.0f", total) + "\n" +
                "🚚 Envío: GRATIS\n" +
                "💚 Pago: Contra entrega\n\n" +
                "Te notificaremos cuando tu pedido esté en camino.\n\n" +
                "Gracias por confiar en GlobalLive · Salud & Bienestar 💚";
        enviarCorreo(to, asunto, cuerpo);
    }

    public void enviarActualizacion(String to, String asunto, String cuerpo) {
        enviarCorreo(to, asunto, cuerpo);
    }

    private void enviarCorreo(String to, String asunto, String cuerpo) {
        new Thread(() -> {
            try {
                SimpleMailMessage mensaje = new SimpleMailMessage();
                mensaje.setFrom(fromEmail);
                mensaje.setTo(to);
                mensaje.setSubject(asunto);
                mensaje.setText(cuerpo);
                mailSender.send(mensaje);
                System.out.println("✅ Correo enviado a: " + to);
            } catch (Exception e) {
                System.out.println("❌ Error enviando correo: " + e.getMessage());
            }
        }).start();
    }
}