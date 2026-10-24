package co.edu.notas;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/*
 * ENTREGA DEL TALLER
 * Nombre completo: (completar)
 * 1. Bug encontrado (método y línea de CarritoCompras.java): agregarProducto, línea 24.
 *    Validaba cantidad < 0 y aceptaba cantidad 0; R1 exige cantidad mayor a 0.
 * 2. Prueba que lo detectó (nombre del método): agregarProducto_cantidadCero_lanzaExcepcion.
 * 3. Sugerencia de la IA que rechacé o corregí, y por qué: ninguna rechazada; verifiqué el
 *    caso límite cantidad = 0 contra R1 (cantidad mayor a 0) y la prueba expuso el bug.
 */
class CarritoComprasTest {

    private CarritoCompras carrito;

    @BeforeEach
    void setUp() {
        carrito = new CarritoCompras();
    }

    @Test
    @DisplayName("Carrito vacío: 0 items, subtotal 0 y total 0")
    void carritoVacio_retornaCantidadSubtotalYTotalCero() {
        assertEquals(0, carrito.cantidadDeItems());
        assertEquals(0.0, carrito.calcularSubtotal(), 0.001);
        assertEquals(0.0, carrito.calcularTotal(), 0.001);
    }

    @Test
    @DisplayName("Subtotal = precio × cantidad de cada producto")
    void calcularSubtotal_variosProductos_sumaCorrecta() {
        // Arrange
        carrito.agregarProducto("Cuaderno", 8_000, 3);
        carrito.agregarProducto("Esfero", 2_000, 5);
        // Act
        double subtotal = carrito.calcularSubtotal();
        // Assert
        assertEquals(34_000, subtotal, 0.001);
    }

    @Test
    @DisplayName("Nombre nulo es rechazado")
    void agregarProducto_nombreNulo_lanzaExcepcion() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> carrito.agregarProducto(null, 8_000, 1));

        assertTrue(ex.getMessage().contains("El nombre es obligatorio"));
    }

    @Test
    @DisplayName("Nombre vacío es rechazado")
    void agregarProducto_nombreVacio_lanzaExcepcion() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> carrito.agregarProducto("", 8_000, 1));

        assertTrue(ex.getMessage().contains("El nombre es obligatorio"));
    }

    @Test
    @DisplayName("Nombre en blanco es rechazado")
    void agregarProducto_nombreEnBlanco_lanzaExcepcion() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> carrito.agregarProducto("   ", 8_000, 1));

        assertTrue(ex.getMessage().contains("El nombre es obligatorio"));
    }

    @Test
    @DisplayName("Precio cero es rechazado")
    void agregarProducto_precioCero_lanzaExcepcion() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> carrito.agregarProducto("Cuaderno", 0, 1));

        assertTrue(ex.getMessage().contains("El precio debe ser positivo"));
    }

    @Test
    @DisplayName("Precio negativo es rechazado")
    void agregarProducto_precioNegativo_lanzaExcepcion() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> carrito.agregarProducto("Cuaderno", -1, 1));

        assertTrue(ex.getMessage().contains("El precio debe ser positivo"));
    }

    @Test
    @DisplayName("Caso límite: cantidad 0 es rechazada (R1)")
    void agregarProducto_cantidadCero_lanzaExcepcion() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> carrito.agregarProducto("Cuaderno", 8_000, 0));

        assertTrue(ex.getMessage().contains("La cantidad debe ser mayor a cero"));
    }

    @Test
    @DisplayName("Cantidad negativa es rechazada")
    void agregarProducto_cantidadNegativa_lanzaExcepcion() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> carrito.agregarProducto("Cuaderno", 8_000, -1));

        assertTrue(ex.getMessage().contains("La cantidad debe ser mayor a cero"));
    }

    @Test
    @DisplayName("SENA10 aplica 10 % de descuento sobre el subtotal")
    void calcularDescuento_cuponSena10_retornaDiezPorCiento() {
        carrito.agregarProducto("Cuaderno", 20_000, 5);
        carrito.aplicarCupon("SENA10");

        assertEquals(10_000, carrito.calcularDescuento(), 0.001);
    }

    @Test
    @DisplayName("BIENVENIDO aplica $5.000 con subtotal exactamente de $50.000")
    void calcularDescuento_bienvenidoSubtotalExacto_retornaCincoMil() {
        carrito.agregarProducto("Cuaderno", 25_000, 2);
        carrito.aplicarCupon("BIENVENIDO");

        assertEquals(5_000, carrito.calcularDescuento(), 0.001);
    }

    @Test
    @DisplayName("Caso límite: BIENVENIDO no aplica con subtotal de $49.999")
    void calcularDescuento_bienvenidoSubtotalInsuficiente_retornaCero() {
        carrito.agregarProducto("Cuaderno", 49_999, 1);
        carrito.aplicarCupon("BIENVENIDO");

        assertEquals(0, carrito.calcularDescuento(), 0.001);
    }

    @Test
    @DisplayName("Cupón inválido es rechazado")
    void aplicarCupon_codigoInvalido_lanzaExcepcion() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> carrito.aplicarCupon("NAVIDAD"));

        assertTrue(ex.getMessage().contains("Cupón inválido"));
    }

    @Test
    @DisplayName("Total con envío pagado: libro de $100.000 da $131.000")
    void calcularTotal_envioPagado_sumaDoceMil() {
        carrito.agregarProducto("Libro", 100_000, 1);

        assertEquals(131_000, carrito.calcularTotal(), 0.001);
    }

    @Test
    @DisplayName("Total con envío gratis y SENA10: monitor de $500.000 da $535.500")
    void calcularTotal_envioGratisConCupon_noSumaEnvio() {
        carrito.agregarProducto("Monitor", 500_000, 1);
        carrito.aplicarCupon("SENA10");

        assertEquals(535_500, carrito.calcularTotal(), 0.001);
    }

    @Test
    @DisplayName("Total con BIENVENIDO: descuenta $5.000 y cobra envío")
    void calcularTotal_cuponBienvenido_descuentaYCobraEnvio() {
        carrito.agregarProducto("Cuaderno", 25_000, 2);
        carrito.aplicarCupon("BIENVENIDO");

        assertEquals(65_550, carrito.calcularTotal(), 0.001);
    }

    @Test
    @DisplayName("El total se redondea al peso")
    void calcularTotal_conDecimales_redondeaAlPeso() {
        carrito.agregarProducto("Marcador", 49_999, 1);

        assertEquals(71_499, carrito.calcularTotal(), 0.001);
    }
}
