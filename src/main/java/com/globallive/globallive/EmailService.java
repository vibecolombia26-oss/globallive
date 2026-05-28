package com.globallive.globallive;

import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class EmailService {

    private static final String API_KEY = "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";
    private static final String FROM_EMAIL = "contacto@globalive.shop";
    private static final String FROM_NAME = "GlobalLive";

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
            Email from = new Email(FROM_EMAIL, FROM_NAME);
            Email toEmail = new Email(to);
            Content content = new Content("text/plain", cuerpo);
            Mail mail = new Mail(from, asunto, toEmail, content);

            SendGrid sg = new SendGrid(API_KEY);
            Request request = new Request();
            try {
                request.setMethod(Method.POST);
                request.setEndpoint("mail/send");
                request.setBody(mail.build());
                Response response = sg.api(request);
                System.out.println("✅ Correo enviado a: " + to + " | Status: " + response.getStatusCode());
            } catch (IOException e) {
                System.out.println("❌ Error enviando correo: " + e.getMessage());
            }
        }).start();
    }
}