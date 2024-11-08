package org.iesvdm;

import jakarta.persistence.EntityManager;
import org.antlr.v4.runtime.tree.pattern.ParseTreePattern;
import org.iesvdm.repository.DepartamentoRepository;
import org.iesvdm.modelo.Departamento;
import org.iesvdm.repository.EmpleadoRepository;
import org.iesvdm.modelo.Empleado;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static java.util.Comparator.*;
import java.util.*;
import java.util.function.Function;
import static java.util.stream.Collectors.*;

@SpringBootTest
class EmpresaTests {

    @Autowired
    DepartamentoRepository depRepo;
    @Autowired
    EmpleadoRepository empRepo;

    @Test
    void testAllDepartamento() {
        var listDeps = depRepo.findAll();

        listDeps.forEach(d -> {
            System.out.println(">>"+d+ ":");
            d.getEmpleados().forEach(System.out::println);
        });

    }

    @Test
    void testAllEmpleado() {
        var listEmps = empRepo.findAll();

        listEmps.forEach( e -> {
            System.out.println(">>"+e+":"+"\nEmpleados mismo departamento "+ e.getDepartamento());
            var dep = e.getDepartamento();
            if ( dep != null)
                dep.getEmpleados().forEach(eD -> System.out.println(">>>>"+eD));
        });

    }

    /**
     * 1. Lista el código de los departamentos de los empleados, 
     * eliminando los códigos que aparecen repetidos.
     */
    @Test
    void test1() {

        var listEmp = empRepo.findAll()
                .stream()
                .map(e -> e.getDepartamento().getCodigo());

        listEmp.forEach(System.out::println);

    }

    /**
     * 2. Lista el nombre y apellidos de los empleados en una única columna, convirtiendo todos los caracteres en mayúscula 
     *
     */
    @Test
    void test2() {

        var listEmp = empRepo.findAll()
                .stream()
                .map(e -> e.getNombre() + " " + e.getApellido1() + " " + e.getApellido2())
                .map(String::toUpperCase);

        listEmp.forEach(System.out::println);

    }

    /**
     * 3. Lista el código de los empleados junto al nif, pero el nif deberá aparecer en dos columnas, 
     * una mostrará únicamente los dígitos del nif y la otra la letra.
     */

    @Test
    void test3() {

        var listEmp = empRepo.findAll()
                .stream()
                .map(e -> e.getCodigo() + " " + e.getNif().substring(0, 8) + " " + e.getNif().substring(8));


                listEmp.forEach(System.out::println);

    }

    /**
     * 4. Lista el nombre de cada departamento y el valor del presupuesto actual del que dispone. 
     * Para calcular este dato tendrá que restar al valor del presupuesto inicial (columna presupuesto) los gastos que se han generado (columna gastos).
     *  Tenga en cuenta que en algunos casos pueden existir valores negativos.
     */
    void test4() {

        var listDep = depRepo.findAll()
                .stream()
                .map(d -> d.getNombre() + " " + (d.getPresupuesto() - d.getGastos()));

        listDep.forEach(System.out::println);

     }

    /**
     * 5. Lista el nombre de los departamentos y el valor del presupuesto actual ordenado de forma ascendente.
     */
    void test5() {

        var listDep = depRepo.findAll()
                .stream()
                .sorted(Comparator.comparingDouble(d -> d.getPresupuesto() - d.getGastos()))
                .map(d -> d.getNombre() + " " + (d.getPresupuesto() - d.getGastos()));

        listDep.forEach(System.out::println);

     }

    /**
     * 6. Devuelve una lista con el nombre y el presupuesto, de los 3 departamentos que tienen mayor presupuesto
     */
    void test6() {

        var listDep = depRepo.findAll();

        listDep.sort(comparing(Departamento::getPresupuesto).reversed());

        listDep.forEach(System.out::println);

     }

    /**
     * 7. Devuelve una lista con el nombre de los departamentos y el presupesto, de aquellos que tienen un presupuesto entre 100000 y 200000 euros
     */
    void test7() {

        var listDep = depRepo.findAll();

        listDep.stream()
                .filter(d -> d.getPresupuesto() >= 100000 && d.getPresupuesto() <= 200000)
                        .map(Departamento::getNombre);

        listDep.forEach(System.out::println);

     }

    /**
     * 8. Devuelve una lista con 5 filas a partir de la tercera fila de empleado ordenado por código de empleado. La tercera fila se debe incluir en la respuesta.
     */
    @Test
    void test8() {

        var listEmp = empRepo.findAll();

        listEmp.stream()
                .skip(2)
                .sorted(comparing(Empleado::getCodigo))
                .limit(5)
                .map(Empleado::getNombre);

        listEmp.forEach(System.out::println);


    }

    /**
     * 9. Devuelve una lista con el nombre de los departamentos y el gasto, de aquellos que tienen menos de 5000 euros de gastos.
     * Ordenada de mayor a menor gasto.
     */
    void test9() {

        var listDep = depRepo.findAll();

        listDep.stream()
                .filter(d -> d.getGastos() < 5000)
                .sorted(comparingDouble(Departamento::getGastos).reversed())
                .map(Departamento::getNombre);

        listDep.forEach(System.out::println);

     }

    /**
     * 10. Lista todos los datos de los empleados cuyo segundo apellido sea Díaz o Moreno
     */
    @Test
    void test10() {

        var listEmp = empRepo.findAll();

        listEmp.stream()
                .filter(e -> e.getApellido2().equals("Díaz") || e.getApellido2().equals("Moreno"))
                .map(Empleado::toString);

        listEmp.forEach(System.out::println);


    }

    /**
     * 11. Lista los nombres, apellidos y nif de los empleados que trabajan en los departamentos 2, 4 o 5
     */
    @Test
    void test11() {

        var listEmp = empRepo.findAll();

        listEmp.stream()
                .filter(e -> e.getDepartamento().getCodigo() == 2 || e.getDepartamento().getCodigo() == 4 || e.getDepartamento().getCodigo() == 5)
                .map(Empleado::toString);

        listEmp.forEach(System.out::println);


    }


    /**
     * 12. Devuelve el nombre del departamento donde trabaja el empleado que tiene el nif 38382980M
     */
    @Test
    void test12() {

        var listEmp = empRepo.findAll();

        listEmp.stream().
                filter(e -> e.getNif().equals("38382980M"))
                .map(Empleado::getDepartamento);

        listEmp.forEach(System.out::println);

    }

    /**
     * 13. Devuelve una lista con el nombre de los empleados que tienen los departamentos 
     * que no tienen un presupuesto entre 100000 y 200000 euros.
     */
    @Test
    void test13() {

        var listEmp = empRepo.findAll();

        listEmp.stream()
                .filter(e -> e.getDepartamento().getPresupuesto() < 100000 || e.getDepartamento().getPresupuesto() > 200000)
                .map(Empleado::toString);

        listEmp.forEach(System.out::println);

    }

    /**
     * 14. Calcula el valor mínimo del presupuesto de todos los departamentos.
     */
    void test14() {

        var listDep = depRepo.findAll();

        listDep.stream()
                .min(comparingDouble(Departamento::getPresupuesto));


        listDep.forEach(System.out::println);

     }

    /**
     * 15. Calcula el número de empleados que hay en cada departamento. 
     * Tienes que devolver dos columnas, una con el nombre del departamento 
     * y otra con el número de empleados que tiene asignados.
     */
    void test15() {

        var listDep = depRepo.findAll();



        listDep.forEach(System.out::println);

     }

    /**
     * 16. Calcula el número total de empleados que trabajan en cada unos de los departamentos que tienen un presupuesto mayor a 200000 euros.
     */
    void test16() {

        var listDep = depRepo.findAll();



        listDep.forEach(System.out::println);

     }

    /**
     * 17. Calcula el nombre de los departamentos que tienen más de 2 empleados. 
     * El resultado debe tener dos columnas, una con el nombre del departamento y
     *  otra con el número de empleados que tiene asignados
     */
    void test17() {

        var listDep = depRepo.findAll();


        listDep.forEach(System.out::println);

     }

    /** 18. Lista todos los nombres de departamentos junto con los nombres y apellidos de los empleados. 
     *
     */
    void test18() {

        var listDep = depRepo.findAll();


        listDep.forEach(System.out::println);

     }

    /**
     * 19. Devuelve la media de empleados que trabajan en los departamentos
     */
    void test19() {

        var listDep = depRepo.findAll();


        listDep.forEach(System.out::println);

   }

    /**
     * 20. Calcula el presupuesto máximo, mínimo  y número total de departamentos con un solo stream.
     */
    void test20() {
        
        var listDep = depRepo.findAll();



        listDep.forEach(System.out::println);

    }

}
