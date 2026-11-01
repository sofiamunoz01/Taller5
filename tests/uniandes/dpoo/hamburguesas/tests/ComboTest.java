package uniandes.dpoo.hamburguesas.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import uniandes.dpoo.hamburguesas.mundo.Combo;
import uniandes.dpoo.hamburguesas.mundo.ProductoMenu;

public class ComboTest{
    private ArrayList<ProductoMenu> productos;
    private Combo combo;

    @BeforeEach
    void setUp(){
        productos = new ArrayList<>();
        productos.add(new ProductoMenu("Hamburguesa", 10000));
        productos.add(new ProductoMenu("Papas", 5000));

        combo = new Combo("Especial", 0.10, productos);
    }

    @Test
    void testGetNombre(){
        assertEquals("Especial", combo.getNombre());
    }

    @Test
    void testPrecioConDescuento(){
        assertEquals(13500, combo.getPrecio(), "Debe cobrar el 90% del precio original.");
    }

    @Test
    void testPrecioSinDescuento(){
        Combo sinDescuento = new Combo("Normal", 0.0, productos);

        assertEquals(15000, sinDescuento.getPrecio(), "Sin descuento debe cobrar la suma completa.");
    }

    @Test
    void testDescuentoTotal(){
        Combo gratis = new Combo("Gratis", 1.0, productos);

        assertEquals(0, gratis.getPrecio(), "Con descuento del 100% el precio debe ser cero.");
    }

    @Test
    void testComboVacio(){
        Combo vacio = new Combo("Vacio", 0.10, new ArrayList<>());

        assertEquals(0, vacio.getPrecio());
    }

    @Test
    void testCopiaDeProductos(){
        Combo copia = new Combo("Copia", 0.0, productos);

        productos.clear();

        assertEquals(15000, copia.getPrecio(), "Modificar la lista original no debe cambiar el combo.");
    }

    @Test
    void testGenerarTextoFactura(){
        String esperado = "Combo Especial\n" + " Descuento: 0.1\n" + "            13500\n";

        assertEquals(esperado, combo.generarTextoFactura(), "La factura debe mostrar el precio con descuento.");
    }
}