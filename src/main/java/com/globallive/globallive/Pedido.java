package com.globallive.globallive;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "pedidos")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombreCliente;
    private String telefono;
    private String email;
    private String departamento;
    private String ciudad;
    private String direccion;
    private String notas;
    private String tienda;

    @Column(length = 3000)
    private String productos;

    private Double total;
    private String metodoPago;
    private String estado;
    private LocalDateTime fecha;

    public Pedido() {}

    public Pedido(String nombreCliente, String telefono, String email, String departamento,
                  String ciudad, String direccion, String notas, String tienda,
                  String productos, Double total, String metodoPago, String estado) {
        this.nombreCliente = nombreCliente;
        this.telefono = telefono;
        this.email = email;
        this.departamento = departamento;
        this.ciudad = ciudad;
        this.direccion = direccion;
        this.notas = notas;
        this.tienda = tienda;
        this.productos = productos;
        this.total = total;
        this.metodoPago = metodoPago;
        this.estado = estado;
        this.fecha = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombreCliente() { return nombreCliente; }
    public void setNombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getDepartamento() { return departamento; }
    public void setDepartamento(String departamento) { this.departamento = departamento; }
    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }
    public String getTienda() { return tienda; }
    public void setTienda(String tienda) { this.tienda = tienda; }
    public String getProductos() { return productos; }
    public void setProductos(String productos) { this.productos = productos; }
    public Double getTotal() { return total; }
    public void setTotal(Double total) { this.total = total; }
    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
    private String codigo;
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    private String guia;
    private String estadoEnvio;
    public String getGuia() { return guia; }
    public void setGuia(String guia) { this.guia = guia; }
    public String getEstadoEnvio() { return estadoEnvio; }
    public void setEstadoEnvio(String estadoEnvio) { this.estadoEnvio = estadoEnvio; }
}