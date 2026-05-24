package com.globallive.globallive;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void enviarConfirmacion(String to, String nombre, String productos, double total) {
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setTo(to);
        mensaje.setSubject("✅ Pedido Confirmado - GlobalLive");
        mensaje.setText("¡Hola " + nombre + "!\n\n" +
                "Tu pedido ha sido registrado exitosamente en GlobalLive.\n\n" +
                "📦 Resumen de tu orden:\n" + productos + "\n" +
                "💰 Total: $" + String.format("%,.0f", total) + "\n" +
                "🚚 Envío: GRATIS\n" +
                "💚 Pago: Contra entrega\n\n" +
                "Te notificaremos cuando tu pedido esté en camino.\n\n" +
                "Gracias por confiar en GlobalLive · Salud & Bienestar 💚");
        mailSender.send(mensaje);
    }
}