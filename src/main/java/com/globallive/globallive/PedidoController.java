package com.globallive.globallive;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class PedidoController {

    private final PedidoRepository pedidoRepository;
    private final EmailService emailService;

    public PedidoController(PedidoRepository pedidoRepository, EmailService emailService) {
        this.pedidoRepository = pedidoRepository;
        this.emailService = emailService;
    }

    @PostMapping("/api/pedido")
    @ResponseBody
    public String guardarPedido(@RequestBody Pedido pedido) {
        pedido.setEstado("Pendiente");
        pedidoRepository.save(pedido);

        // Enviar correo de confirmación
        if (pedido.getEmail() != null && !pedido.getEmail().isEmpty()) {
            try {
                emailService.enviarConfirmacion(
                        pedido.getEmail(),
                        pedido.getNombreCliente(),
                        pedido.getProductos(),
                        pedido.getTotal()
                );
            } catch (Exception e) {
                // Si falla el correo, no afecta el pedido
            }
        }

        return "OK";
    }
}