package uniandes.dpoo.hamburguesas.tests;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import uniandes.dpoo.hamburguesas.mundo.Combo;
import uniandes.dpoo.hamburguesas.mundo.Ingrediente;
import uniandes.dpoo.hamburguesas.mundo.Pedido;
import uniandes.dpoo.hamburguesas.mundo.ProductoAjustado;
import uniandes.dpoo.hamburguesas.mundo.ProductoMenu;

public class PedidoTest{
    private Pedido pedido;

    @TempDir
    Path carpetaTemporal;

    @BeforeEach
    void setUp(){
        pedido = new Pedido("Ana", "Calle 10");
    }

    @Test
    void testNombreCliente(){
        assertEquals("Ana", pedido.getNombreCliente());
    }

    @Test
    void testIdentificadoresConsecutivos(){
        Pedido primero = new Pedido("Luis", "Calle 20");
        Pedido segundo = new Pedido("Maria", "Calle 30");

        assertEquals(primero.getIdPedido() + 1, segundo.getIdPedido());
    }

    @Test
    void testPedidoVacio(){
        assertEquals(0, pedido.getPrecioTotalPedido());
    }

    @Test
    void testTotalConUnProducto(){
        pedido.agregarProducto(new ProductoMenu("Hamburguesa", 10000));

        assertEquals(11900, pedido.getPrecioTotalPedido());
    }

    @Test
    void testTotalConVariosProductos(){
        pedido.agregarProducto(new ProductoMenu("Hamburguesa", 10000));
        pedido.agregarProducto(new ProductoMenu("Papas", 5000));

        assertEquals(17850, pedido.getPrecioTotalPedido());
    }

    @Test
    void testProductoRepetido(){
        ProductoMenu papas = new ProductoMenu("Papas", 5000);

        pedido.agregarProducto(papas);
        pedido.agregarProducto(papas);

        assertEquals(11900, pedido.getPrecioTotalPedido());
    }

    @Test
    void testPedidoConProductosDeDistintosTipos(){
        ProductoMenu hamburguesa = new ProductoMenu("Hamburguesa", 10000);
        ProductoMenu papas = new ProductoMenu("Papas", 5000);

        ArrayList<ProductoMenu> productosCombo = new ArrayList<>();
        productosCombo.add(hamburguesa);
        productosCombo.add(papas);

        Combo combo = new Combo("Especial", 0.10, productosCombo);

        ProductoAjustado ajustado = new ProductoAjustado(hamburguesa);
        ajustado.agregarIngrediente(new Ingrediente("queso", 2000));

        pedido.agregarProducto(papas);
        pedido.agregarProducto(combo);
        pedido.agregarProducto(ajustado);

        assertEquals(36295, pedido.getPrecioTotalPedido());
    }

    @Test
    void testGenerarTextoFactura(){
        pedido.agregarProducto(
                new ProductoMenu("Hamburguesa", 10000));
        pedido.agregarProducto(
                new ProductoMenu("Papas", 5000));

        String esperado = "Cliente: Ana\n" + "Dirección: Calle 10\n" + "----------------\n" + "Hamburguesa\n" + "            10000\n" + "Papas\n" + "            5000\n" + "----------------\n" + "Precio Neto:  15000\n" + "IVA:          2850\n" + "Precio Total: 17850\n";

        assertEquals(esperado, pedido.generarTextoFactura());
    }

    @Test
    void testGuardarFactura() throws Exception{
        pedido.agregarProducto(new ProductoMenu("Hamburguesa", 10000));

        File archivo = carpetaTemporal.resolve("factura.txt").toFile();

        pedido.guardarFactura(archivo);

        assertTrue(archivo.isFile(), "Debe crear el archivo de la factura.");

        String contenido = Files.readString( archivo.toPath(), Charset.defaultCharset());

        assertEquals(pedido.generarTextoFactura(), contenido, "El archivo debe contener la factura completa.");
    }

    @Test
    void testGuardarFacturaEnRutaInexistente(){
        File archivo = carpetaTemporal.resolve("carpetaInexistente").resolve("factura.txt").toFile();

        assertThrows(FileNotFoundException.class,() -> pedido.guardarFactura(archivo));
    }
}