package uniandes.dpoo.hamburguesas.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uniandes.dpoo.hamburguesas.mundo.ProductoMenu;

public class ProductoMenuTest{
    private ProductoMenu producto;

    @BeforeEach
    void setUp(){
        producto = new ProductoMenu("Hamburguesa", 10000);
    }

    @Test
    void testGetNombre(){
        assertEquals("Hamburguesa", producto.getNombre(), "El nombre del producto no es el esperado.");
    }

    @Test
    void testGetPrecio(){
        assertEquals(10000, producto.getPrecio(), "El precio del producto no es el esperado.");
    }

    @Test
    void testGenerarTextoFactura(){
        String esperado = "Hamburguesa\n" + "            10000\n";

        assertEquals(esperado, producto.generarTextoFactura(), "El texto de la factura no es el esperado.");
    }
}