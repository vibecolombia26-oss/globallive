package com.globallive.globallive;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Base64;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final ProductoRepository productoRepository;
    private final PedidoRepository pedidoRepository;
    private final MensajeRepository mensajeRepository;
    private final EmailService emailService;
    private String adminPassword = "GlobalLive2026*";

    public AdminController(ProductoRepository productoRepository, PedidoRepository pedidoRepository,
                           MensajeRepository mensajeRepository, EmailService emailService) {
        this.productoRepository = productoRepository;
        this.pedidoRepository = pedidoRepository;
        this.mensajeRepository = mensajeRepository;
        this.emailService = emailService;
    }

    @GetMapping("/login")
    public String login() { return "admin-login"; }

    @PostMapping("/login")
    public String procesarLogin(@RequestParam String password, Model model) {
        if (adminPassword.equals(password)) return "redirect:/admin/panel?key=" + password;
        model.addAttribute("error", "Contraseña incorrecta");
        return "admin-login";
    }

    @GetMapping("/panel")
    public String panel(@RequestParam String key, Model model) {
        if (!adminPassword.equals(key)) return "redirect:/admin/login";
        model.addAttribute("productos", productoRepository.findAll());
        model.addAttribute("key", key);
        return "admin-panel";
    }

    @GetMapping("/pedidos")
    public String pedidos(@RequestParam String key, Model model) {
        if (!adminPassword.equals(key)) return "redirect:/admin/login";
        model.addAttribute("pedidos", pedidoRepository.findAll());
        model.addAttribute("key", key);
        return "admin-pedidos";
    }

    @GetMapping("/chats")
    public String chats(@RequestParam String key, Model model) {
        if (!adminPassword.equals(key)) return "redirect:/admin/login";
        model.addAttribute("mensajes", mensajeRepository.findAll());
        model.addAttribute("key", key);
        return "admin-chats";
    }

    @PostMapping("/responder/{id}")
    public String responder(@PathVariable Long id, @RequestParam String key, @RequestParam String respuesta) {
        if (!adminPassword.equals(key)) return "redirect:/admin/login";
        Mensaje msg = mensajeRepository.findById(id).orElse(null);
        if (msg != null) {
            msg.setRespuesta(respuesta);
            mensajeRepository.save(msg);
        }
        return "redirect:/admin/chats?key=" + key;
    }

    @PostMapping("/cambiar-estado/{id}")
    public String cambiarEstado(@PathVariable Long id, @RequestParam String key, @RequestParam String estado,
                                @RequestParam(required = false) String transportadora,
                                @RequestParam(required = false) String numeroGuia) {
        if (!adminPassword.equals(key)) return "redirect:/admin/login";
        Pedido pedido = pedidoRepository.findById(id).orElse(null);
        if (pedido != null) {
            pedido.setEstado(estado);
            if (transportadora != null) pedido.setTransportadora(transportadora);
            if (numeroGuia != null) pedido.setNumeroGuia(numeroGuia);
            pedidoRepository.save(pedido);

            String telefono = pedido.getTelefono();
            if (telefono != null && !telefono.isEmpty()) {
                String mensaje = "";
                if (estado.equals("Procesando")) {
                    mensaje = "Hola " + pedido.getNombreCliente() + "! Tu pedido #" + pedido.getCodigo() +
                            " ha sido confirmado y lo estamos preparando. Gracias por confiar en GlobalLive! %F0%9F%92%9A";
                } else if (estado.equals("Enviado") && transportadora != null && numeroGuia != null) {
                    mensaje = "Buenas noticias " + pedido.getNombreCliente() + "! Tu pedido #" + pedido.getCodigo() +
                            " ya va en camino! %F0%9F%9A%9A%0A%0ATransportadora: " + transportadora +
                            "%0ANumero de guia: " + numeroGuia + "%0A%0AGracias por confiar en GlobalLive! %F0%9F%92%9A";
                } else if (estado.equals("Entregado")) {
                    mensaje = "Hola " + pedido.getNombreCliente() + "! Tu pedido #" + pedido.getCodigo() +
                            " ha sido entregado. Gracias por confiar en GlobalLive! %F0%9F%92%9A";
                }
                if (!mensaje.isEmpty()) {
                    enviarWhatsApp(telefono, mensaje);
                }
            }

            String email = pedido.getEmail();
            if (email != null && !email.isEmpty()) {
                try {
                    String asunto = "Actualizacion de tu pedido #" + pedido.getCodigo() + " - GlobalLive";
                    String cuerpo = "Hola " + pedido.getNombreCliente() + "!\n\nTu pedido ha cambiado a: " + estado + ".\n\nGracias por confiar en GlobalLive %F0%9F%92%9A";
                    emailService.enviarActualizacion(email, asunto, cuerpo);
                } catch (Exception e) {}
            }
        }
        return "redirect:/admin/pedidos?key=" + key;
    }

    @GetMapping("/nuevo")
    public String nuevoProducto(@RequestParam String key, Model model) {
        if (!adminPassword.equals(key)) return "redirect:/admin/login";
        model.addAttribute("producto", new Producto());
        model.addAttribute("key", key);
        return "admin-form";
    }

    @GetMapping("/editar/{id}")
    public String editarProducto(@PathVariable Long id, @RequestParam String key, Model model) {
        if (!adminPassword.equals(key)) return "redirect:/admin/login";
        model.addAttribute("producto", productoRepository.findById(id).orElse(null));
        model.addAttribute("key", key);
        return "admin-form";
    }

    @PostMapping("/guardar")
    public String guardarProducto(@ModelAttribute Producto producto, @RequestParam String key,
                                  @RequestParam(required = false) MultipartFile imagen1File,
                                  @RequestParam(required = false) MultipartFile imagen2File,
                                  @RequestParam(required = false) MultipartFile imagen3File,
                                  @RequestParam(required = false) MultipartFile imagen4File,
                                  @RequestParam(required = false) MultipartFile imagen5File,
                                  @RequestParam(required = false) MultipartFile imagen6File,
                                  @RequestParam(required = false) MultipartFile imagen7File,
                                  @RequestParam(required = false) MultipartFile imagen8File,
                                  @RequestParam(required = false) MultipartFile imagen9File,
                                  @RequestParam(required = false) MultipartFile imagen10File,
                                  RedirectAttributes redirect) {
        if (!adminPassword.equals(key)) return "redirect:/admin/login";
        try {
            MultipartFile[] nuevasImagenes = {imagen1File, imagen2File, imagen3File, imagen4File, imagen5File,
                    imagen6File, imagen7File, imagen8File, imagen9File, imagen10File};
            if (producto.getId() != null) {
                Producto existente = productoRepository.findById(producto.getId()).orElse(null);
                if (existente != null) {
                    producto.setImagen1(existente.getImagen1()); producto.setImagen2(existente.getImagen2());
                    producto.setImagen3(existente.getImagen3()); producto.setImagen4(existente.getImagen4());
                    producto.setImagen5(existente.getImagen5()); producto.setImagen6(existente.getImagen6());
                    producto.setImagen7(existente.getImagen7()); producto.setImagen8(existente.getImagen8());
                    producto.setImagen9(existente.getImagen9()); producto.setImagen10(existente.getImagen10());
                }
            }
            String[] setters = {"setImagen1","setImagen2","setImagen3","setImagen4","setImagen5",
                    "setImagen6","setImagen7","setImagen8","setImagen9","setImagen10"};
            for (int i = 0; i < 10; i++) {
                if (nuevasImagenes[i] != null && !nuevasImagenes[i].isEmpty()) {
                    String base64 = "data:" + nuevasImagenes[i].getContentType() + ";base64," +
                            Base64.getEncoder().encodeToString(nuevasImagenes[i].getBytes());
                    Producto.class.getMethod(setters[i], String.class).invoke(producto, base64);
                }
            }
            productoRepository.save(producto);
            redirect.addFlashAttribute("mensaje", "✅ Producto guardado!");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "❌ Error: " + e.getMessage());
        }
        return "redirect:/admin/panel?key=" + key;
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarProducto(@PathVariable Long id, @RequestParam String key, RedirectAttributes redirect) {
        if (!adminPassword.equals(key)) return "redirect:/admin/login";
        productoRepository.deleteById(id);
        redirect.addFlashAttribute("mensaje", "🗑️ Producto eliminado!");
        return "redirect:/admin/panel?key=" + key;
    }

    private void enviarWhatsApp(String telefono, String mensaje) {
        new Thread(() -> {
            try {
                String url = "https://wa.me/57" + telefono.replaceAll("[^0-9]", "") + "?text=" +
                        java.net.URLEncoder.encode(mensaje, "UTF-8");
                java.net.HttpURLConnection conn = (java.net.HttpURLConnection) new java.net.URL(url).openConnection();
                conn.setRequestMethod("GET");
                conn.getResponseCode();
                System.out.println("WhatsApp enviado a: " + telefono);
            } catch (Exception e) {
                System.out.println("Error enviando WhatsApp: " + e.getMessage());
            }
        }).start();
    }
}