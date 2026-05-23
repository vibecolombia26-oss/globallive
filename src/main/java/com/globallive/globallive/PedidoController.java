package com.globallive.globallive;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class PedidoController {

    private final PedidoRepository pedidoRepository;

    public PedidoController(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    @PostMapping("/api/pedido")
    @ResponseBody
    public String guardarPedido(@RequestBody Pedido pedido) {
        pedido.setEstado("Pendiente");
        pedidoRepository.save(pedido);
        return "OK";
    }
}