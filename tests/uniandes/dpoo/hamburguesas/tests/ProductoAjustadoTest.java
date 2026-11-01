package uniandes.dpoo.hamburguesas.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import uniandes.dpoo.hamburguesas.mundo.ProductoAjustado;
import uniandes.dpoo.hamburguesas.mundo.ProductoMenu;
import uniandes.dpoo.hamburguesas.mundo.Ingrediente;

public class ProductoAjustadoTest{
    private ProductoAjustado producto;

    @BeforeEach
    void setUp(){
        ProductoMenu base = new ProductoMenu("Hamburguesa", 10000); 
        producto = new ProductoAjustado(base);
    }

    @Test
    void testGetNombre(){
        assertEquals("Hamburguesa", producto.getNombre(), "Debe conservar el nombre del producto base.");
    }

    @Test
    void testPrecioSinAjustes(){
        assertEquals(10000, producto.getPrecio(), "Sin ajustes debe conservar el precio base.");
    }

    @Test
    void testFacturaSinAjustes(){
        String factura = producto.generarTextoFactura();

        assertTrue(factura.contains("Hamburguesa"), "La factura debe incluir el nombre del producto.");

        assertTrue(factura.contains("10000"), "La factura debe incluir el precio del producto.");
    }
    
    @Test
    void testPrecioConUnaAdicion(){
        producto.agregarIngrediente(new Ingrediente("queso", 2000));

        assertEquals(12000, producto.getPrecio(), "Debe sumar el costo del queso al precio base.");
    }

    @Test
    void testPrecioConVariasAdiciones(){
        producto.agregarIngrediente(new Ingrediente("queso", 2000));
        producto.agregarIngrediente(new Ingrediente("tocineta", 3000));

        assertEquals(15000, producto.getPrecio(), "Debe sumar todas las adiciones.");
    }

    @Test
    void testPrecioConAdicionRepetida(){
        Ingrediente queso = new Ingrediente("queso", 2000);

        producto.agregarIngrediente(queso);
        producto.agregarIngrediente(queso);

        assertEquals(14000, producto.getPrecio(), "Debe cobrar cada adicion, aunque se repita.");
    }

    @Test
    void testEliminarIngredienteNoReducePrecio(){
        producto.eliminarIngrediente(new Ingrediente("tomate", 1000));

        assertEquals(10000, producto.getPrecio(), "Quitar ingredientes no debe reducir el precio.");
    }

    @Test
    void testPrecioConAdicionYEliminacion(){
        producto.agregarIngrediente(new Ingrediente("queso", 2000));
        producto.eliminarIngrediente(new Ingrediente("tomate", 1000));

        assertEquals(12000, producto.getPrecio(), "Debe cobrar la adicion sin descontar lo eliminado.");
    }

    @Test
    void testFacturaConAjustes(){
        producto.agregarIngrediente(new Ingrediente("queso", 2000));
        producto.agregarIngrediente(new Ingrediente("tocineta", 3000));
        producto.eliminarIngrediente(new Ingrediente("tomate", 1000));
        producto.eliminarIngrediente(new Ingrediente("cebolla", 500));

        String esperado = "Hamburguesa\n" + "            10000\n" + "    +queso                2000\n" + "    +tocineta                3000\n" + "    -tomate\n" + "    -cebolla\n" + "            15000\n";

        assertEquals(esperado, producto.generarTextoFactura(), "La factura debe detallar los ajustes y el precio final.");
    }
}