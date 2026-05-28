package com.globallive.globallive;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

@Controller
public class PedidoController {

    private final PedidoRepository pedidoRepository;
    private final EmailService emailService;
    private final MensajeRepository mensajeRepository;

    public PedidoController(PedidoRepository pedidoRepository, EmailService emailService, MensajeRepository mensajeRepository) {
        this.pedidoRepository = pedidoRepository;
        this.emailService = emailService;
        this.mensajeRepository = mensajeRepository;
    }

    @PostMapping("/api/pedido")
    @ResponseBody
    public Map<String, String> guardarPedido(@RequestBody Pedido pedido) {
        pedido.setEstado("Pendiente");
        pedido.setCodigo("GL-" + System.currentTimeMillis());
        pedidoRepository.save(pedido);

        if (pedido.getEmail() != null && !pedido.getEmail().isEmpty()) {
            try {
                emailService.enviarConfirmacion(
                        pedido.getEmail(), pedido.getNombreCliente(),
                        pedido.getProductos(), pedido.getTotal()
                );
            } catch (Exception e) {}
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
            response.put("telefono", pedido.getTelefono());
        }
        return response;
    }

    @PostMapping("/api/chat/enviar")
    @ResponseBody
    public Map<String, String> enviarMensaje(@RequestBody Map<String, String> body) {
        String codigo = body.get("codigo");
        String telefono = body.get("telefono");
        String mensaje = body.get("mensaje");

        Mensaje msg = new Mensaje(codigo, telefono, mensaje, true);
        mensajeRepository.save(msg);

        Map<String, String> response = new HashMap<>();
        response.put("status", "OK");
        return response;
    }

    @GetMapping("/api/chat/mensajes")
    @ResponseBody
    public List<Mensaje> obtenerMensajes(@RequestParam(required = false) String codigo,
                                         @RequestParam(required = false) String telefono) {
        if (codigo != null && !codigo.isEmpty()) {
            return mensajeRepository.findByCodigoPedidoOrderByFechaAsc(codigo);
        } else if (telefono != null && !telefono.isEmpty()) {
            return mensajeRepository.findByTelefonoOrderByFechaAsc(telefono);
        }
        return List.of();
    }
    @GetMapping("/api/chat/todos")
    @ResponseBody
    public List<Mensaje> obtenerTodosMensajes() {
        return mensajeRepository.findAll();
    }
    @GetMapping("/api/chat/fix-telefonos")
    @ResponseBody
    public String fixTelefonos() {
        List<Mensaje> mensajes = mensajeRepository.findAll();
        int count = 0;
        for (Mensaje m : mensajes) {
            if (m.getTelefono() == null || m.getTelefono().isEmpty()) {
                Pedido p = pedidoRepository.findAll().stream()
                        .filter(ped -> ped.getCodigo().equals(m.getCodigoPedido()))
                        .findFirst().orElse(null);
                if (p != null && p.getTelefono() != null && !p.getTelefono().isEmpty()) {
                    m.setTelefono(p.getTelefono());
                    mensajeRepository.save(m);
                    count++;
                }
            }
        }
        return "OK - " + count + " mensajes actualizados de " + mensajes.size();
    }
}