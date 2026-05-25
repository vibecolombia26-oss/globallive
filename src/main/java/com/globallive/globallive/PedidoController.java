package com.globallive.globallive;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

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
    public Map<String, String> guardarPedido(@RequestBody Pedido pedido) {
        pedido.setEstado("Pendiente");
        pedido.setCodigo("GL-" + System.currentTimeMillis());
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

        Map<String, String> response = new HashMap<>();
        response.put("codigo", pedido.getCodigo());
        response.put("status", "OK");
        return response;
    }

    @GetMapping("/api/pedido/{codigo}")
    @ResponseBody
    public Map<String, Object> buscarPedido(@PathVariable String codigo) {
        Pedido pedido = pedidoRepository.findAll().stream()
                .filter(p -> codigo.equals(p.getCodigo()))
                .findFirst().orElse(null);

        Map<String, Object> response = new HashMap<>();
        if (pedido == null) {
            response.put("error", "Pedido no encontrado");
        } else {
            response.put("codigo", pedido.getCodigo());
            response.put("estado", pedido.getEstado());
            response.put("productos", pedido.getProductos());
            response.put("total", pedido.getTotal());
            response.put("transportadora", pedido.getTransportadora());
            response.put("numeroGuia", pedido.getNumeroGuia());
        }
        return response;
    }
}