function completarOrden() {
    const nombre = document.getElementById('nombreCliente').value;
    const telefono = document.getElementById('telefono').value;
    const email = document.getElementById('email').value;
    const departamento = document.getElementById('departamento').value;
    const ciudad = document.getElementById('ciudad').value;
    const direccion = document.getElementById('direccion').value;
    const notas = document.getElementById('notas').value;

    if (!nombre || !telefono || !departamento || !ciudad || !direccion) {
        alert('Por favor completa todos los campos requeridos (*)');
        return;
    }

    let productosTexto = '';
    let total = 0;
    carrito.forEach(item => {
        const precio = Number(item.precio) || 0;
        const cantidad = Number(item.cantidad) || 1;
        const subtotal = precio * cantidad;
    total += subtotal;
    productosTexto += '• ' + item.nombre + ' x' + cantidad;
    if (item.color) productosTexto += ' · ' + item.color;
    if (item.talla) productosTexto += ' · Talla ' + item.talla;
    productosTexto += ' · $' + subtotal.toLocaleString('es-CO') + '\n';
    });

    const pedido = {
            nombreCliente: nombre,
            telefono: telefono,
            email: email,
            departamento: departamento,
            ciudad: ciudad,
            direccion: direccion,
            notas: notas,
            tienda: 'GlobalLive',
            productos: productosTexto,
            total: total,
            metodoPago: 'Contra entrega'
    };

    fetch('/api/pedido', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(pedido)
    }).then(response => {
            localStorage.removeItem('carritoVibeColombia');
    carrito = [];
    window.location.href = '/pedido-confirmado';
    }).catch(error => {
            alert('Error al guardar el pedido. Intenta de nuevo.');
    });
}