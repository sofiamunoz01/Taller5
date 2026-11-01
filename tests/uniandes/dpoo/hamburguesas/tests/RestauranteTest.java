package uniandes.dpoo.hamburguesas.tests;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import uniandes.dpoo.hamburguesas.excepciones.*;
import uniandes.dpoo.hamburguesas.mundo.*;

public class RestauranteTest{
    private Restaurante restaurante;
    private File ingredientes;
    private File menu;
    private File combos;

    @TempDir
    Path carpetaTemporal;

    @BeforeEach
    void setUp() throws IOException{
        restaurante = new Restaurante();

        ingredientes = crearArchivo("ingredientes.txt", "queso;2000\n\ntomate;1000\n");

        menu = crearArchivo("menu.txt", "Hamburguesa;10000\n\nPapas;5000\n");

        combos = crearArchivo("combos.txt", "Especial;10%;Hamburguesa;Papas\n\n" + "Individual;0%;Hamburguesa\n");
    }

    private File crearArchivo(String nombre, String contenido) throws IOException{
        Path ruta = carpetaTemporal.resolve(nombre);
        Files.writeString(ruta, contenido, Charset.defaultCharset());
        return ruta.toFile();
    }

    private void cargarDatos() throws Exception{
        restaurante.cargarInformacionRestaurante(ingredientes, menu, combos);
    }

    @Test
    void testRestauranteVacio(){
        assertNull(restaurante.getPedidoEnCurso());
        assertTrue(restaurante.getPedidos().isEmpty());
        assertTrue(restaurante.getIngredientes().isEmpty());
        assertTrue(restaurante.getMenuBase().isEmpty());
        assertTrue(restaurante.getMenuCombos().isEmpty());
    }

    @Test
    void testCargarInformacion() throws Exception{
        cargarDatos();

        assertEquals(2, restaurante.getIngredientes().size());
        assertEquals("queso", restaurante.getIngredientes().get(0).getNombre());
        assertEquals(2000, restaurante.getIngredientes().get(0).getCostoAdicional());
        assertEquals("tomate", restaurante.getIngredientes().get(1).getNombre());
        assertEquals(1000, restaurante.getIngredientes().get(1).getCostoAdicional());

        assertEquals(2, restaurante.getMenuBase().size());
        assertEquals("Hamburguesa", restaurante.getMenuBase().get(0).getNombre());
        assertEquals(10000, restaurante.getMenuBase().get(0).getPrecio());
        assertEquals("Papas", restaurante.getMenuBase().get(1).getNombre());
        assertEquals(5000, restaurante.getMenuBase().get(1).getPrecio());

        assertEquals(2, restaurante.getMenuCombos().size());
        assertEquals("Especial", restaurante.getMenuCombos().get(0).getNombre());
        assertEquals(13500, restaurante.getMenuCombos().get(0).getPrecio());
        assertEquals("Individual", restaurante.getMenuCombos().get(1).getNombre());
        assertEquals(10000, restaurante.getMenuCombos().get(1).getPrecio());
    }

    @Test
    void testArchivosVacios() throws Exception{
        File vacio = crearArchivo("vacio.txt", "");

        restaurante.cargarInformacionRestaurante(vacio, vacio, vacio);

        assertTrue(restaurante.getIngredientes().isEmpty());
        assertTrue(restaurante.getMenuBase().isEmpty());
        assertTrue(restaurante.getMenuCombos().isEmpty());
    }

    @Test
    void testIngredienteRepetido() throws Exception{
        ingredientes = crearArchivo("ingredientes.txt","queso;2000\nqueso;3000\n");

        assertThrows(IngredienteRepetidoException.class, () -> cargarDatos());
    }

    @Test
    void testProductoRepetido() throws Exception{
        menu = crearArchivo("menu.txt", "Hamburguesa;10000\nHamburguesa;12000\n");

        assertThrows(ProductoRepetidoException.class, () -> cargarDatos());
    }

    @Test
    void testComboRepetido() throws Exception
    {
        combos = crearArchivo("combos.txt", "Especial;10%;Hamburguesa\n" + "Especial;20%;Papas\n");

        assertThrows(ProductoRepetidoException.class, () -> cargarDatos());
    }

    @Test
    void testProductoFaltanteEnCombo() throws Exception{
        combos = crearArchivo("combos.txt", "Especial;10%;Pizza\n");

        assertThrows(ProductoFaltanteException.class, () -> cargarDatos());
    }

    @Test
    void testCostoIngredienteInvalido() throws Exception{
        ingredientes = crearArchivo("ingredientes.txt", "queso;abc\n");

        assertThrows(NumberFormatException.class, () -> cargarDatos());
    }

    @Test
    void testPrecioMenuInvalido() throws Exception{
        menu = crearArchivo("menu.txt", "Hamburguesa;abc\n");

        assertThrows(NumberFormatException.class,
                () -> cargarDatos());
    }

    @Test
    void testDescuentoInvalido() throws Exception{
        combos = crearArchivo("combos.txt", "Especial;abc%;Hamburguesa\n");

        assertThrows(NumberFormatException.class, () -> cargarDatos());
    }

    @Test
    void testArchivoInexistente(){
        ingredientes = carpetaTemporal.resolve("noExiste.txt").toFile();

        assertThrows(IOException.class, () -> cargarDatos());
    }

    @Test
    void testIniciarPedido() throws Exception{
        restaurante.iniciarPedido("Ana", "Calle 10");

        Pedido pedido = restaurante.getPedidoEnCurso();

        assertNotNull(pedido);
        assertEquals("Ana", pedido.getNombreCliente());
        assertTrue(pedido.generarTextoFactura().contains("Dirección: Calle 10\n"));
        assertTrue(restaurante.getPedidos().isEmpty());
    }

    @Test
    void testIniciarPedidoConOtroActivo() throws Exception{
        restaurante.iniciarPedido("Ana", "Calle 10");
        Pedido primero = restaurante.getPedidoEnCurso();

        assertThrows(YaHayUnPedidoEnCursoException.class, () -> restaurante.iniciarPedido("Luis", "Calle 20"));

        assertSame(primero, restaurante.getPedidoEnCurso(), "Debe conservar el pedido que ya estaba activo.");
    }

    @Test
    void testCerrarSinPedido(){
        assertThrows(NoHayPedidoEnCursoException.class, () -> restaurante.cerrarYGuardarPedido());
    }

    @Test
    void testCerrarYGuardarPedido() throws Exception{
        cargarDatos();
        restaurante.iniciarPedido("Ana", "Calle 10");

        Pedido pedido = restaurante.getPedidoEnCurso();
        pedido.agregarProducto(restaurante.getMenuBase().get(0));

        Path archivo = Path.of("facturas", "factura_" + pedido.getIdPedido() + ".txt");

        assertFalse(Files.exists(archivo), "Ya existe esta factura. Respalda el archivo antes de probar.");

        try{
            restaurante.cerrarYGuardarPedido();

            assertTrue(Files.isRegularFile(archivo), "Debe guardar el archivo de factura.");

            String contenido = Files.readString(archivo, Charset.defaultCharset());

            assertEquals(pedido.generarTextoFactura(), contenido);
            assertNull(restaurante.getPedidoEnCurso());

            assertEquals(1, restaurante.getPedidos().size());
            assertSame(pedido, restaurante.getPedidos().get(0));

            restaurante.iniciarPedido("Luis", "Calle 20");
            assertEquals("Luis", restaurante.getPedidoEnCurso().getNombreCliente());
        }
        finally{
            Files.deleteIfExists(archivo);
        }
    }
}