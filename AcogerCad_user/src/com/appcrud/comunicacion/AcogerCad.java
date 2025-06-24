/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.appcrud.comunicacion;


import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import org.mindrot.jbcrypt.BCrypt;

/**
 * Clase que implenta todos los métodos necesarios para gestionar la base de datos ACOGER
 * @author DAM214
 */
public class AcogerCad {

    /**
     * Atributo cadenaConexionBD, cadena de conexion con la base de datos 
     */
    private String cadenaConexionBD = "jdbc:oracle:thin:@192.168.1.214:1521:test";
    
    /**
     * Atributo cadenaConexionBD, usuario de la base de datos 
     */
    private String usuario = "ACOGER";
    
     /**
     * Atributo cadenaConexionBD, usuario de la base de datos 
     */
    private String contrasena = "kk";

    /**
     * Constructor vacio de AcogeCad en el que manejamos la ClassNotFoundException para todos los métodos
     * @throws ExcepcionesAcoger Esta excepción se lanzará si ocurre  un error cuando se inntenta cargar el controlador de la BD
     */
    public AcogerCad() throws ExcepcionesAcoger {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
        } catch (ClassNotFoundException ex) {
            ExcepcionesAcoger e = new ExcepcionesAcoger();
            e.setMensajeErrorUsuario("Error general del sistema. Consulte con el administrador");
            e.setMensajeErrorAdministrador(ex.getMessage());
            throw e;

        }
    }

    public Usuario loginUsuario(String email, String passwordPlano) {
        String sql = "SELECT * FROM USUARIO WHERE EMAIL = ?";

        try (Connection conexion = DriverManager.getConnection(cadenaConexionBD, usuario, contrasena);
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, email);
            ResultSet resultado = ps.executeQuery();

            if (resultado.next()) {
                String hash = resultado.getString("CONTRASENA");
                if (BCrypt.checkpw(passwordPlano, hash)) {
                    Usuario u = new Usuario();
                    u.setUsuarioId(resultado.getInt("USUARIO_ID"));
                    u.setDni(resultado.getString("DNI"));
                    u.setAlias(resultado.getString("ALIAS"));
                    u.setTelefono(resultado.getInt("TELEFONO"));
                    u.setEmail(resultado.getString("EMAIL"));
                    u.setNombre(resultado.getString("NOMBRE"));
                    u.setApellido1(resultado.getString("APELLIDO1"));
                    u.setApellido2(resultado.getString("APELLIDO2"));
                    u.setRol(resultado.getString("ROL"));
                    u.setContrasena(hash);
                    return u;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

        
    public Protectora loginProtectora(String email, String passwordPlano) {
        String sql = "SELECT * FROM PROTECTORA WHERE EMAIL = ?";

        try (Connection conexion = DriverManager.getConnection(cadenaConexionBD, usuario, contrasena);
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, email);
            ResultSet resultado = ps.executeQuery();

            if (resultado.next()) {
                String hash = resultado.getString("CONTRASENA");
                if (BCrypt.checkpw(passwordPlano, hash)) {
                    Protectora protectora = new Protectora();
                    protectora.setProtectoraId(resultado.getInt("PROTECTORA_ID"));
                    protectora.setCapacidad(resultado.getInt("CAPACIDAD"));
                    protectora.setNombre(resultado.getString("NOMBRE"));
                    protectora.setCalle(resultado.getString("CALLE"));
                    protectora.setNumeroCalle(resultado.getInt("NUMERO_CALLE"));
                    protectora.setCodPostal(resultado.getInt("COD_POSTAL"));
                    protectora.setEmail(resultado.getString("EMAIL"));
                    protectora.setTelefono(resultado.getInt("TELEFONO"));
                    protectora.setHistoria(resultado.getString("HISTORIA"));
                    protectora.setContrasena(hash);
                    return protectora;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }



    /**
     * Método que implementa la insercción de un animal
     * @param animal Objeto animal que contiene los valores que se van a insertar
     * @return Retorna el numero de registros afectados. Valore posibles: 0
     * ningun animal ha sido insertado; o 1 un animal ha sido insertado
     * @throws ExcepcionesAcoger Se lanzara esta excepcion cuando se produzca una violación de las 
     * constraint de la BD o por fallos internos en la ejecución del metodo o de la BD
     */
    public Integer insertarAnimal(Animal animal) throws ExcepcionesAcoger {

    	String dml = "INSERT INTO ANIMAL (ANIMAL_ID, PROTECTORA_ID, IDENTIFICADOR, FECHA_NACIMIENTO, NOMBRE, VACUNA, CASTRADO, PASAPORTE, SEXO, ANIMAL_TIPO, RAZA, HISTORIA, FOTO) VALUES (SeqAnimal.nextval, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        int registrosAfectados = 0;

        try {
            Connection conexion = DriverManager.getConnection(cadenaConexionBD, usuario, contrasena);
            PreparedStatement sentenciaPreparada = conexion.prepareStatement(dml);

            sentenciaPreparada.setObject(1, animal.getProtectora().getProtectoraId(), Types.INTEGER);
            sentenciaPreparada.setString(2, animal.getIdentificador());
            java.util.Date fechaUtil = animal.getFechaNacimiento();
            java.sql.Date fechaSql = new java.sql.Date(fechaUtil.getTime());
            sentenciaPreparada.setDate(3, fechaSql);
            sentenciaPreparada.setString(4, animal.getNombreAnimal());
            sentenciaPreparada.setString(5, animal.getVacuna());
            sentenciaPreparada.setString(6, animal.getCastrado());
            sentenciaPreparada.setString(7, animal.getPasaporte());
            sentenciaPreparada.setString(8, animal.getSexo());
            sentenciaPreparada.setString(9, animal.getAnimalTipo());
            sentenciaPreparada.setString(10, animal.getRaza());
            sentenciaPreparada.setString(11, animal.getHistoria());
            sentenciaPreparada.setString(12, animal.getFoto());
            
            registrosAfectados = sentenciaPreparada.executeUpdate();

        } catch (SQLException ex) {
            ExcepcionesAcoger e = new ExcepcionesAcoger();
            e.setMensajeErrorAdministrador(ex.getMessage());
            e.setSentenciaSQL(dml);
            e.setCodigoErrorBD(ex.getErrorCode());
            switch (ex.getErrorCode()) {
                case 1:
                    e.setMensajeErrorUsuario("El identificador ya existe. Por favor, introduzca uno diferente");
                    break;

                case 1861:
                    e.setMensajeErrorUsuario("Algun dato ha sido introducido de manera incorrecta");
                    break;

                case 1840:
                    e.setMensajeErrorUsuario("La fecha introducida no es lo bastante largo para el formato de fecha");
                    break;
                
                case 17361:
                    e.setMensajeErrorUsuario("El año esta fuera de rango");
                    break;

                case 1847:
                    e.setMensajeErrorUsuario("El día del mes debe estar entre 1 y el último día del mes");
                    break;

                case 1843:
                    e.setMensajeErrorUsuario("Has introducido un mes que no es válido");
                    break;

                case 1400:
                    e.setMensajeErrorUsuario("Debes insertar todos los datos obligatorios: ID de animal, ID de usuario, ID de protectora, fecha de nacimiento, nombre, vacuna, castrado, pasaporte, sexo y tipo de animal.");
                    break;

                case 0000:
                    e.setMensajeErrorUsuario("Faltan datos, asegurate que hac rellenado todos los campos");
                    break;

                case 2291:
                    e.setMensajeErrorUsuario("Estas ingresando un usuario o protectora que no existen. Asegúrate de que la protectora y el usuario existen.");
                    break;

                case 12899:
                    e.setMensajeErrorUsuario("El valor introducido es demasiado grande para el tamaño permitido de la columna.");
                    break;

                default:
                    System.out.println("MENSAJE DE ERROR TÉCNICO → " + ex.getMessage());
                    System.out.println("CÓDIGO DE ERROR BD → " + ex.getErrorCode());
                    e.setMensajeErrorUsuario("Error general del sistema. Consulte con el administrador.");
            }
            throw e;
        }
        return registrosAfectados;
    }

    /**
     * Método que implementa la eliminacion de un animal
     * @param animalId Identificador del animal que queremos eliminar
     * @return Retorna el número de animale afectados. Valores posibles: 0 ningun animal ha sido eliminado; o 1 un animal ha sido eliminado
     * @throws ExcepcionesAcoger Se lanzara esta excepcion cuando se produzca una violación de las constraint de la BD o por fallos internos en la ejecución del metodo o de la BD
     */
    public Integer eliminarAnimal(Integer animalId) throws ExcepcionesAcoger {
        Integer registrosAfectados = null;
        String dml = "delete from ANIMAL where ANIMAL_ID =" + animalId;

        try {
            Connection conexion = DriverManager.getConnection(cadenaConexionBD, usuario, contrasena);
            Statement sentencia = conexion.createStatement();
            registrosAfectados = sentencia.executeUpdate(dml);
            sentencia.close();
            conexion.close();

        } catch (SQLException ex) {
            ExcepcionesAcoger e = new ExcepcionesAcoger();
            e.setMensajeErrorAdministrador(ex.getMessage());
            e.setSentenciaSQL(dml);
            e.setCodigoErrorBD(ex.getErrorCode());

            switch (ex.getErrorCode()) {
                case 904:
                    e.setMensajeErrorUsuario("El identificador no es valido");
                    break;

                case 1722:
                    e.setMensajeErrorUsuario("El identificador no es valido");
                    break;

                case 936:
                    e.setMensajeErrorUsuario("El identificador no es valido");
                    break;

                default:
                    System.out.println("MENSAJE DE ERROR TÉCNICO → " + ex.getMessage());
                    System.out.println("CÓDIGO DE ERROR BD → " + ex.getErrorCode());
                    e.setMensajeErrorUsuario("Error general del sistema. Consulte con el administrador.");
                    break;
            }
            throw e;
        }
        return registrosAfectados;
    }

    /**
     * Método que implementa la actualización de un animal
     * @param animalId Identificador del animal que se va ha actualizar
     * @param animal Objeto Animal que contiene los nuevos valores
     * @return Retorna el número de animale afectados. Valores posibles: 0
     * ningun animal ha sido actualizado; o 1 un animal ha sido actualizado
     * @throws ExcepcionesAcoger Se lanzara esta excepcion cuando se produzca una violación de las constraint de la BD o por fallos internos en la ejecución del metodo o de la B
     */
    public Integer actualizarAnimal(Integer animalId, Animal animal) throws ExcepcionesAcoger {
    	String dml = "UPDATE ANIMAL SET IDENTIFICADOR = ?, FECHA_NACIMIENTO = ?, NOMBRE = ?, VACUNA = ?, CASTRADO = ?, PASAPORTE = ?, SEXO = ?, ANIMAL_TIPO = ?, RAZA = ?, HISTORIA = ?, FOTO = ? WHERE ANIMAL_ID = ?";
        int registrosAfectados = 0;
        try {
            Connection conexion = DriverManager.getConnection(cadenaConexionBD, usuario, contrasena);

            PreparedStatement sentenciaPreparada = conexion.prepareStatement(dml);     
            sentenciaPreparada.setString(1, animal.getIdentificador());
            sentenciaPreparada.setDate(2, new java.sql.Date(animal.getFechaNacimiento().getTime()));
            sentenciaPreparada.setString(3, animal.getNombreAnimal());
            sentenciaPreparada.setString(4, animal.getVacuna());
            sentenciaPreparada.setString(5, animal.getCastrado());
            sentenciaPreparada.setString(6, animal.getPasaporte());
            sentenciaPreparada.setString(7, animal.getSexo());
            sentenciaPreparada.setString(8, animal.getAnimalTipo());
            sentenciaPreparada.setString(9, animal.getRaza());
            sentenciaPreparada.setString(10, animal.getHistoria());
            sentenciaPreparada.setString(11, animal.getFoto());
            sentenciaPreparada.setObject(12, animalId, Types.INTEGER);


            registrosAfectados = sentenciaPreparada.executeUpdate();

            sentenciaPreparada.close();
            conexion.close();

        } catch (SQLException ex) {
            ExcepcionesAcoger e = new ExcepcionesAcoger();
            e.setMensajeErrorAdministrador(ex.getMessage());
            e.setSentenciaSQL(dml);
            e.setCodigoErrorBD(ex.getErrorCode());
            switch (ex.getErrorCode()) {
                case 1:
                    e.setMensajeErrorUsuario("El identificador ya existe. Por favor, introduzca uno diferente");
                    break;

                case 1861:
                    e.setMensajeErrorUsuario("Algun dato ha sido introducido de manera incorrecta");
                    break;

                case 1840:
                    e.setMensajeErrorUsuario("La fecha introducida no es lo bastante largo para el formato de fecha");
                    break;

                case 17361:
                    e.setMensajeErrorUsuario("El año esta fuera de rango");
                    break;
                    
                case 1847:
                    e.setMensajeErrorUsuario("El día del mes debe estar entre 1 y el último día del mes");
                    break;

                case 1843:
                    e.setMensajeErrorUsuario("Has introducido un mes que no es válido");
                    break;

                case 1400:
                    e.setMensajeErrorUsuario("Debes insertar todos los datos obligatorios: ID de animal, ID de usuario, ID de protectora, fecha de nacimiento, nombre, vacuna, castrado, pasaporte, sexo y tipo de animal.");
                    break;

                case 0000:
                    e.setMensajeErrorUsuario("Faltan datos, asegurate que hac rellenado todos los campos");
                    break;

                case 2291:
                    e.setMensajeErrorUsuario("Estas ingresando un usuario o protectora que no existen. Asegúrate de que la protectora y el usuario existen.");
                    break;

                case 12899:
                    e.setMensajeErrorUsuario("El valor introducido es demasiado grande para el tamaño permitido de la columna.");
                    break;

                default:
                    System.out.println("MENSAJE DE ERROR TÉCNICO → " + ex.getMessage());
                    System.out.println("CÓDIGO DE ERROR BD → " + ex.getErrorCode());
                    e.setMensajeErrorUsuario("Error general del sistema. Consulte con el administrador.");
                    break;
            }
            throw e;
        }
        return registrosAfectados;
    }

    /**
     * Método que impplementa la lectura de un animal
     * @param animalId Indetificador del animal que queremos leer
     * @return Deveulve los datos del animal
     * @exception ExcepcionesAcoger Se lanzara esta excepcion cuando se produzca una violación de las constraint de la BD o por fallos internos en la ejecución del metodo o de la BD
     */
    public Animal leerAnimal(Integer animalId) throws ExcepcionesAcoger {
        Animal animal = null;
        Usuario usuarioBd;
        Protectora protectora;

        String dql = "SELECT a.ANIMAL_ID, a.IDENTIFICADOR, a.FECHA_NACIMIENTO, a.NOMBRE AS NOMBRE_ANIMAL, " +
        	    "a.VACUNA, a.CASTRADO, a.PASAPORTE, a.SEXO, a.ANIMAL_TIPO, a.RAZA, a.HISTORIA, a.FOTO, " +
        	    "p.PROTECTORA_ID, p.NOMBRE AS NOMBRE_PROTECTORA, p.EMAIL, p.TELEFONO, p.HISTORIA AS HISTORIA_PROTECTORA, " +
        	    "p.CALLE, p.NUMERO_CALLE, p.COD_POSTAL, p.FOTO AS FOTO_PROTECTORA " + 
        	    "FROM ANIMAL a " +
        	    "JOIN PROTECTORA p ON a.PROTECTORA_ID = p.PROTECTORA_ID " +
        	    "WHERE a.ANIMAL_ID = " + animalId;



        try {
            Connection conexion = DriverManager.getConnection(cadenaConexionBD, usuario, contrasena);
            Statement sentencia = conexion.createStatement();
            ResultSet resultado = sentencia.executeQuery(dql);

            while (resultado.next()) {
                protectora = new Protectora();
                protectora.setProtectoraId(resultado.getInt("PROTECTORA_ID"));
                protectora.setNombre(resultado.getString("NOMBRE_PROTECTORA"));
                protectora.setEmail(resultado.getString("EMAIL"));
                protectora.setTelefono(resultado.getInt("TELEFONO"));
                protectora.setHistoria(resultado.getString("HISTORIA_PROTECTORA"));
                protectora.setCalle(resultado.getString("CALLE"));
                protectora.setNumeroCalle(resultado.getInt("NUMERO_CALLE"));
                protectora.setCodPostal(resultado.getInt("COD_POSTAL"));
                protectora.setFoto(resultado.getString("FOTO_PROTECTORA"));

                animal = new Animal();
                animal.setAnimalId(resultado.getInt("ANIMAL_ID"));
                animal.setIdentificador(resultado.getString("IDENTIFICADOR"));
                animal.setFechaNacimiento(resultado.getDate("FECHA_NACIMIENTO"));
                animal.setNombreAnimal(resultado.getString("NOMBRE_ANIMAL"));
                animal.setVacuna(resultado.getString("VACUNA"));
                animal.setCastrado(resultado.getString("CASTRADO"));
                animal.setPasaporte(resultado.getString("PASAPORTE"));
                animal.setSexo(resultado.getString("SEXO"));
                animal.setAnimalTipo(resultado.getString("ANIMAL_TIPO"));
                animal.setRaza(resultado.getString("RAZA"));
                animal.setHistoria(resultado.getString("HISTORIA"));
                animal.setFoto(resultado.getString("FOTO"));

                animal.setProtectora(protectora);
            }
            resultado.close();
            sentencia.close();
            conexion.close();

        } catch (SQLException ex) {
            ExcepcionesAcoger e = new ExcepcionesAcoger();
            e.setMensajeErrorAdministrador(ex.getMessage());
            e.setSentenciaSQL(dql);
            e.setCodigoErrorBD(ex.getErrorCode());
            e.setMensajeErrorUsuario("Error general del sistema. Consulte con el administrador.");

            switch (ex.getErrorCode()) {
                case 2292:
                    e.setMensajeErrorUsuario("El animal no existe");
                    break;

                case 904:
                    e.setMensajeErrorUsuario("El identificador no es valido");
                    break;

                case 1722:
                    e.setMensajeErrorUsuario("El identificador no es valido");
                    break;

                case 936:
                    e.setMensajeErrorUsuario("El identificador no es valido");
                    break;

                default:
                    System.out.println("MENSAJE DE ERROR TÉCNICO → " + ex.getMessage());
                    System.out.println("CÓDIGO DE ERROR BD → " + ex.getErrorCode());
                    e.setMensajeErrorUsuario("Error general del sistema. Consulte con el administrador.");
                    break;
            }
            throw e;
        }
        return animal;
    }

    /**
     * Método que implementa la lectura de todos los Animales
     * @return Retorna una ArrayList de todos los objetos animal
     * @exception ExcepcionesAcoger Se lanzara esta excepcion cuando se produzca una violación de las constraint de la BD o por fallos internos en la ejecución del metodo o de la BD
     */
    public ArrayList<Animal> leerAnimales() throws ExcepcionesAcoger {
        ArrayList<Animal> listaAnimales = new ArrayList<>();
        Animal animal;
        Protectora protectora;

        String dql = "SELECT a.ANIMAL_ID, a.IDENTIFICADOR, a.FECHA_NACIMIENTO, a.NOMBRE AS NOMBRE_ANIMAL, " +
        	    "a.VACUNA, a.CASTRADO, a.PASAPORTE, a.SEXO, a.ANIMAL_TIPO, a.RAZA, a.HISTORIA, a.FOTO, " + 
        	    "p.PROTECTORA_ID, p.NOMBRE AS NOMBRE_PROTECTORA, p.EMAIL, p.TELEFONO, p.HISTORIA AS HISTORIA_PROTECTORA, " +
        	    "p.CALLE, p.NUMERO_CALLE, p.COD_POSTAL " +
        	    "FROM ANIMAL a " +
        	    "JOIN PROTECTORA p ON a.PROTECTORA_ID = p.PROTECTORA_ID";

        System.out.println("→ Ejecutando consulta leerAnimales...");
        
        try {
            Connection conexion = DriverManager.getConnection(cadenaConexionBD, usuario, contrasena);
            Statement sentencia = conexion.createStatement();
            ResultSet resultado = sentencia.executeQuery(dql);

            int contador = 0;
            while (resultado.next()) {
                contador++;
                protectora = new Protectora();
                protectora.setProtectoraId(resultado.getInt("PROTECTORA_ID"));
                protectora.setNombre(resultado.getString("NOMBRE_PROTECTORA"));

                animal = new Animal();
                animal.setAnimalId(resultado.getInt("ANIMAL_ID"));
                animal.setIdentificador(resultado.getString("IDENTIFICADOR"));
                animal.setFechaNacimiento(resultado.getDate("FECHA_NACIMIENTO"));
                animal.setNombreAnimal(resultado.getString("NOMBRE_ANIMAL"));
                animal.setVacuna(resultado.getString("VACUNA"));
                animal.setCastrado(resultado.getString("CASTRADO"));
                animal.setPasaporte(resultado.getString("PASAPORTE"));
                animal.setSexo(resultado.getString("SEXO"));
                animal.setAnimalTipo(resultado.getString("ANIMAL_TIPO"));
                animal.setRaza(resultado.getString("RAZA"));
                animal.setHistoria(resultado.getString("HISTORIA"));
                animal.setFoto(resultado.getString("FOTO"));
                animal.setProtectora(protectora);

                listaAnimales.add(animal);
            }

            resultado.close();
            sentencia.close();
            conexion.close();

        } catch (SQLException ex) {
        	ExcepcionesAcoger e = new ExcepcionesAcoger();
            e.setMensajeErrorAdministrador(ex.getMessage());
            e.setSentenciaSQL(dql);
            e.setCodigoErrorBD(ex.getErrorCode());
            e.setMensajeErrorUsuario("Error general del sistema. Consulte con el administrador.");

            throw e;
        }

        return listaAnimales;
    }


    /**
     * Método que implementa la insercción de una protectora
     * @param protectora Objeto protectora que recoge los valores que se van a insertar
     * @return Retorna el número de registros afectados. Valores posibles: 0 no
     * se ha insertado ninguna protectora; o 1 se ha insertado una protectora.
     * @exception ExcepcionesAcoger Se lanzara esta excepcion cuando se produzca una violación de las constraint de la BD o por fallos internos en la ejecución del metodo o de la BD
     */
    public Integer insertarProtectora(Protectora protectora) throws ExcepcionesAcoger {

        String dml = "INSERT INTO PROTECTORA (PROTECTORA_ID, CAPACIDAD, NOMBRE, CALLE, NUMERO_CALLE, COD_POSTAL, EMAIL, TELEFONO, HISTORIA, CONTRASENA) VALUES (SeqProtectora.nextval, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        int registrosAfectados = 0;

        try {
            Connection conexion = DriverManager.getConnection(cadenaConexionBD, usuario, contrasena);
            PreparedStatement sentenciaPreparada = conexion.prepareStatement(dml);

            String hashedPassword = BCrypt.hashpw(protectora.getContrasena(), BCrypt.gensalt());

            sentenciaPreparada.setInt(1, protectora.getCapacidad());
            sentenciaPreparada.setString(2, protectora.getNombre());
            sentenciaPreparada.setString(3, protectora.getCalle());
            sentenciaPreparada.setInt(4, protectora.getNumeroCalle());
            sentenciaPreparada.setInt(5, protectora.getCodPostal());
            sentenciaPreparada.setString(6, protectora.getEmail());
            sentenciaPreparada.setInt(7, protectora.getTelefono());
            sentenciaPreparada.setString(8, protectora.getHistoria());
            sentenciaPreparada.setString(9, hashedPassword);


            registrosAfectados = sentenciaPreparada.executeUpdate();

        } catch (SQLException ex) {
            ExcepcionesAcoger e = new ExcepcionesAcoger();
            e.setMensajeErrorAdministrador(ex.getMessage());
            e.setSentenciaSQL(dml);
            e.setCodigoErrorBD(ex.getErrorCode());
            switch (ex.getErrorCode()) {
                case 1:
                    e.setMensajeErrorUsuario("Parece que la combinación de calle y número que ingresas ya existe. Intenta con otra.");
                    break;

                case 1861:
                    e.setMensajeErrorUsuario("Algun dato ha sido introducido de manera incorrecta");
                    break;

                case 2290:
                    e.setMensajeErrorUsuario("El formato del email ingresado no es válido. Por favor, verifica que esté correctamente escrito");
                    break;

                case 1400:
                    e.setMensajeErrorUsuario("Asegúrate de completar todos los campos. Todos son obligatorios");
                    break;

                case 12899:
                    e.setMensajeErrorUsuario("El valor introducido es demasiado grande para el tamaño permitido de la columna.");
                    break;

                case 1438:
                    e.setMensajeErrorUsuario("La cantidad de datos que intentas almacenar en 'capacidad' excede permitida.");
                    break;

                default:
                    e.setMensajeErrorUsuario("Error general del sistema. Consulte con el administrador.");
                    break;
            }
            throw e;
        }
        return registrosAfectados;
    }

    /**
     * Método que implementa la eliminación de una protectora
     * @param protectoraId Identificador de la protectora a eliminar
     * @return Retorna la cantidad de registros de protectora eliminados.
     * Valores posibles: 0 no se ha eliminado ninguna protectora; 1 se ha
     * eliminado una protectora
     * @exception ExcepcionesAcoger Se lanzara esta excepcion cuando se produzca una violación de las constraint de la BD o por fallos internos en la ejecución del metodo o de la BD
     */
    public Integer eliminarProtectora(Integer protectoraId) throws ExcepcionesAcoger {
        Integer registrosAfectados = null;
        String dml = "delete from PROTECTORA where PROTECTORA_ID =" + protectoraId;

        try {
            Connection conexion = DriverManager.getConnection(cadenaConexionBD, usuario, contrasena);
            Statement sentencia = conexion.createStatement();
            registrosAfectados = sentencia.executeUpdate(dml);
            sentencia.close();
            conexion.close();

        } catch (SQLException ex) {
            ExcepcionesAcoger e = new ExcepcionesAcoger();
            e.setMensajeErrorAdministrador(ex.getMessage());
            e.setSentenciaSQL(dml);
            e.setCodigoErrorBD(ex.getErrorCode());
            switch (ex.getErrorCode()) {
                case 2292:
                    e.setMensajeErrorUsuario("La protectora no se puede eliminar porque tiene animales asociados.");
                    break;

                default:
                    System.out.println("MENSAJE DE ERROR TÉCNICO → " + ex.getMessage());
                    System.out.println("CÓDIGO DE ERROR BD → " + ex.getErrorCode());
                    e.setMensajeErrorUsuario("Error general del sistema. Consulte con el administrador.");
            }
            throw e;
        }
        return registrosAfectados;
    }

    /**
     * Método que implementa la acutalización de una protectora
     *
     * @param protectoraId Identificador de la protectora que se quiere actualizar
     * @param protectora Objeto de la clase Protectora que almaena los nuevos
     * valores a asignar a protectora
     * @return Retorna la cantidad de registros de protectora actualizados.
     * Valores posibles: 0 no se actualizado ninguna protectora; o 1 se ha
     * actualizado una protectora
     * @exception ExcepcionesAcoger Se lanzara esta excepcion cuando se produzca una violación de las constraint de la BD o por fallos internos en la ejecución del metodo o de la BD
     */
    public Integer actualizarProtectora(Integer protectoraId, Protectora protectora) throws ExcepcionesAcoger {
        String dml = "UPDATE protectora SET CAPACIDAD = ?, NOMBRE = ?, CALLE = ?, NUMERO_CALLE = ?, COD_POSTAL = ?, EMAIL = ?, TELEFONO = ?, HISTORIA = ? WHERE protectora_id = ?";
        int registrosAfectados = 0;
        try {
            Connection conexion = DriverManager.getConnection(cadenaConexionBD, usuario, contrasena);

            PreparedStatement sentenciaPreparada = conexion.prepareStatement(dml);
            sentenciaPreparada.setInt(1, protectora.getCapacidad());
            sentenciaPreparada.setString(2, protectora.getNombre());
            sentenciaPreparada.setString(3, protectora.getCalle());
            sentenciaPreparada.setInt(4, protectora.getNumeroCalle());
            sentenciaPreparada.setInt(5, protectora.getCodPostal());
            sentenciaPreparada.setString(6, protectora.getEmail());
            sentenciaPreparada.setInt(7, protectora.getTelefono());
            sentenciaPreparada.setString(8, protectora.getHistoria());
            sentenciaPreparada.setInt(9, protectoraId);
            
            System.out.println("→ Ejecutando UPDATE para protectora con ID: " + protectoraId);
            System.out.println("→ Datos: " + protectora.toString());

            registrosAfectados = sentenciaPreparada.executeUpdate();

            System.out.println("→ Filas actualizadas: " + registrosAfectados);
            
            sentenciaPreparada.close();
            conexion.close();

        } catch (SQLException ex) {
            ExcepcionesAcoger e = new ExcepcionesAcoger();
            e.setMensajeErrorAdministrador(ex.getMessage());
            e.setSentenciaSQL(dml);
            e.setCodigoErrorBD(ex.getErrorCode());
            switch (ex.getErrorCode()) {
                case 1:
                    e.setMensajeErrorUsuario("Parece que la combinación de calle y número que ingresas ya existe. Intenta con otra.");
                    break;
                    
                case 1861:
                    e.setMensajeErrorUsuario("Algun dato ha sido introducido de manera incorrecta");
                    break; 

                case 2290:
                    e.setMensajeErrorUsuario("El formato del email ingresado no es válido. Por favor, verifica que esté correctamente escrito");
                    break;

                case 1407:
                    e.setMensajeErrorUsuario("Asegúrate de completar todos los campos. Todos son obligatorios.");
                    break;

                case 12899:
                    e.setMensajeErrorUsuario("El valor introducido es demasiado grande para el tamaño permitido de la columna.");
                    break;

                case 1438:
                    e.setMensajeErrorUsuario("La cantidad de datos que intentas almacenar en 'capacidad' excede permitida.");
                    break;
                             
                default:
                    System.out.println("MENSAJE DE ERROR TÉCNICO → " + ex.getMessage());
                    System.out.println("CÓDIGO DE ERROR BD → " + ex.getErrorCode());
                    e.setMensajeErrorUsuario("Error general del sistema. Consulte con el administrador.");
                    break;
            }
            throw e;
        }
        return registrosAfectados;
    }

    /**
     * Método que implementa la lectura de una protectora
     *
     * @param protectoraId Identificador de la protectora que se quiere
     * actualizar
     * @return Retorna los datos de la protectora
     * @throws ExcepcionesAcoger se lanzará esta excepción cuando se produzca una violación de las constraint de la base de datos
     */
    public Protectora leerProtectora(Integer protectoraId) throws ExcepcionesAcoger {
        Protectora protectora = null;
        String dql = "select * from PROTECTORA where PROTECTORA_ID = " + protectoraId;

        try {
            Connection conexion = DriverManager.getConnection(cadenaConexionBD, usuario, contrasena);
            Statement sentencia = conexion.createStatement();
            ResultSet resultado = sentencia.executeQuery(dql);
            while (resultado.next()) {

                protectora = new Protectora();
                protectora.setNombre(resultado.getString("NOMBRE"));
                protectora.setCapacidad(resultado.getInt("CAPACIDAD"));
                protectora.setCalle(resultado.getString("CALLE"));
                protectora.setNumeroCalle(resultado.getInt("NUMERO_CALLE"));
                protectora.setCodPostal(resultado.getInt("COD_POSTAL"));
                protectora.setEmail(resultado.getString("EMAIL"));
                protectora.setTelefono(resultado.getInt("TELEFONO"));
                protectora.setHistoria(resultado.getString("HISTORIA"));
                protectora.setProtectoraId(resultado.getInt("PROTECTORA_ID")); 
                protectora.setFoto(resultado.getString("FOTO"));

            }
            resultado.close();
            sentencia.close();
            conexion.close();

        } catch (SQLException ex) {
            ExcepcionesAcoger e = new ExcepcionesAcoger();
            e.setMensajeErrorAdministrador(ex.getMessage());
            e.setSentenciaSQL(dql);
            e.setCodigoErrorBD(ex.getErrorCode());
            e.setMensajeErrorUsuario("Error general del sistema. Consulte con el administrador.");

            switch (ex.getErrorCode()) {
                case 904:
                    e.setMensajeErrorUsuario("El identificador no es valido");
                    break;

                case 1722:
                    e.setMensajeErrorUsuario("El identificador no es valido");
                    break;

                case 936:
                    e.setMensajeErrorUsuario("El identificador no es valido");
                    break;

                default:
                    e.setMensajeErrorUsuario("Error general del sistema. Consulte con el administrador.");
                    break;
            }
            throw e;
        }
        return protectora;
    }

    /**
     * Método que implementa la lectura de todas las protectoras
     * @return Retorna un ArrayList de todos los objetos con la información de
     * cada protectora
     * @throws ExcepcionesAcoger se lanzará esta excepción cuando se produzca una violación de las constraint de la base de datos
     */
    public ArrayList<Protectora> leerProtectoras() throws ExcepcionesAcoger {
        ArrayList<Protectora> listaProtectoras = new ArrayList();
        Protectora protectora;

        String dql = "select * from PROTECTORA";

        try {
            Connection conexion = DriverManager.getConnection(cadenaConexionBD, usuario, contrasena);
            Statement sentencia = conexion.createStatement();
            ResultSet resultado = sentencia.executeQuery(dql);
            while (resultado.next()) {
                protectora = new Protectora();
                protectora.setProtectoraId(resultado.getInt("PROTECTORA_ID"));
                protectora.setCapacidad(resultado.getInt("CAPACIDAD"));
                protectora.setNombre(resultado.getString("NOMBRE"));
                protectora.setCalle(resultado.getString("CALLE"));
                protectora.setNumeroCalle(Integer.parseInt(resultado.getString("NUMERO_CALLE").trim()));
                protectora.setCodPostal(Integer.parseInt(resultado.getString("COD_POSTAL").trim()));
                protectora.setEmail(resultado.getString("EMAIL"));
                protectora.setTelefono(resultado.getInt("TELEFONO"));
                protectora.setHistoria(resultado.getString("HISTORIA"));
                protectora.setFoto(resultado.getString("FOTO"));


                listaProtectoras.add(protectora);

            }
            resultado.close();
            sentencia.close();
            conexion.close();

        } catch (SQLException ex) {
            ExcepcionesAcoger e = new ExcepcionesAcoger();
            e.setMensajeErrorAdministrador(ex.getMessage());
            e.setSentenciaSQL(dql);
            e.setCodigoErrorBD(ex.getErrorCode());
            e.setMensajeErrorUsuario("Error general del sistema. Consulte con el administrador.");

            switch (ex.getErrorCode()) {
                case 904:
                    e.setMensajeErrorUsuario("El identificador no es valido");
                    break;

                case 1722:
                    e.setMensajeErrorUsuario("El identificador no es valido");
                    break;

                case 936:
                    e.setMensajeErrorUsuario("El identificador no es valido");
                    break;

                default:
                    e.setMensajeErrorUsuario("Error general del sistema. Consulte con el administrador.");
                    break;
            }
            throw e;
        }
        return listaProtectoras;
    }
    
    /**
     * Método para ver los animales registrados por protectora
     * @param protectoraId
     * @return
     * @throws ExcepcionesAcoger 
     */
    
    public ArrayList<Animal> obtenerAnimalesPorProtectora(Integer protectoraId) throws ExcepcionesAcoger {
        ArrayList<Animal> lista = new ArrayList<>();
        Animal animal;
        Protectora protectora;

        String sql = "SELECT * FROM ANIMAL WHERE PROTECTORA_ID = ?";

        try (Connection con = DriverManager.getConnection(cadenaConexionBD, usuario, contrasena);
            PreparedStatement sentencia = con.prepareStatement(sql)) {

            sentencia.setInt(1, protectoraId);
            ResultSet resultado = sentencia.executeQuery();

            while (resultado.next()) {
                protectora = new Protectora();
                protectora.setProtectoraId(resultado.getInt("PROTECTORA_ID"));
                protectora.setNombre(resultado.getString("NOMBRE"));
            
                animal = new Animal();
                animal.setAnimalId(resultado.getInt("ANIMAL_ID"));
                animal.setIdentificador(resultado.getString("IDENTIFICADOR"));
                animal.setFechaNacimiento(resultado.getDate("FECHA_NACIMIENTO"));
                animal.setNombreAnimal(resultado.getString("NOMBRE"));
                animal.setVacuna(resultado.getString("VACUNA"));
                animal.setCastrado(resultado.getString("CASTRADO"));
                animal.setPasaporte(resultado.getString("PASAPORTE"));
                animal.setSexo(resultado.getString("SEXO"));
                animal.setAnimalTipo(resultado.getString("ANIMAL_TIPO"));
                animal.setRaza(resultado.getString("RAZA"));
                animal.setHistoria(resultado.getString("HISTORIA"));
                animal.setFoto(resultado.getString("FOTO"));

                animal.setProtectora(protectora);

                lista.add(animal);
            }

            resultado.close();
        } catch (SQLException ex) {
            ExcepcionesAcoger e = new ExcepcionesAcoger();
            e.setMensajeErrorAdministrador(ex.getMessage());
            e.setSentenciaSQL(sql);
            e.setCodigoErrorBD(ex.getErrorCode());
            e.setMensajeErrorUsuario("No se han podido recuperar los animales de la protectora. Consulte con el administrador.");
            throw e;
        }

        return lista;
    }

   
     /**
     * Método que implementa la insercción de un usuario
     * @param user Objeto protectora que recoge los valores que se van a insertar
     * @return Retorna el número de registros afectados. Valores posibles: 0 no
     * se ha insertado ninguna protectora; o 1 se ha insertado una protectora.
     * @exception ExcepcionesAcoger Se lanzara esta excepcion cuando se produzca una violación de las constraint de la BD o por fallos internos en la ejecución del metodo o de la BD
     */
    public Integer insertarUsuario(Usuario user) throws ExcepcionesAcoger {

        String dml = "INSERT INTO USUARIO (USUARIO_ID, DNI, ALIAS, TELEFONO, EMAIL, NOMBRE, APELLIDO1, APELLIDO2, CONTRASENA) VALUES (sequsuario.nextval, ?, ?, ?, ?, ?, ?, ?, ?)";       
        int registrosAfectados = 0;

        try {
            Connection conexion = DriverManager.getConnection(cadenaConexionBD, usuario, contrasena);
            PreparedStatement sentenciaPreparada = conexion.prepareStatement(dml);

            String hashedPassword = BCrypt.hashpw(user.getContrasena(), BCrypt.gensalt());

            
            sentenciaPreparada.setString(1, user.getDni());
            sentenciaPreparada.setString(2, user.getAlias());
            sentenciaPreparada.setObject(3, user.getTelefono(),  Types.INTEGER);
            sentenciaPreparada.setString(4, user.getEmail());
            sentenciaPreparada.setString(5, user.getNombre());
            sentenciaPreparada.setString(6, user.getApellido1());
            sentenciaPreparada.setString(7, user.getApellido2());
            sentenciaPreparada.setString(8, hashedPassword);

            registrosAfectados = sentenciaPreparada.executeUpdate();

        } catch (SQLException ex) {
            ExcepcionesAcoger e = new ExcepcionesAcoger();
            e.setMensajeErrorAdministrador(ex.getMessage());
            e.setSentenciaSQL(dml);
            e.setCodigoErrorBD(ex.getErrorCode());
            switch (ex.getErrorCode()) {
                case 1861:
                    e.setMensajeErrorUsuario("Algun dato ha sido introducido de manera incorrecta");
                    break;

                case 2290:
                    e.setMensajeErrorUsuario("El formato del email ingresado no es válido. Por favor, verifica que esté correctamente escrito");
                    break;

                case 1400:
                    e.setMensajeErrorUsuario("Asegúrate de completar todos los campos. Todos son obligatorios");
                    break;

                case 12899:
                    e.setMensajeErrorUsuario("El valor introducido es demasiado grande para el tamaño permitido de la columna.");
                    break;

                case 1438:
                    e.setMensajeErrorUsuario("La cantidad de datos que intentas almacenar en 'capacidad' excede permitida.");
                    break;

                default:
                    e.setMensajeErrorUsuario("Error general del sistema. Consulte con el administrador.");                    
                    break;
            }
            throw e;
        }
        return registrosAfectados;
    }
    
    /**
     * Método que implementa la eliminación de una protectora
     * @param usuarioId Identificador de la protectora a eliminar
     * @return Retorna la cantidad de registros de protectora eliminados.
     * Valores posibles: 0 no se ha eliminado ninguna protectora; 1 se ha
     * eliminado una protectora
     * @exception ExcepcionesAcoger Se lanzara esta excepcion cuando se produzca una violación de las constraint de la BD o por fallos internos en la ejecución del metodo o de la BD
     */
    public Integer eliminarUsuario(Integer usuarioId) throws ExcepcionesAcoger {
        Integer registrosAfectados = null;
        String dml = "DELETE FROM usuario WHERE USUARIO_ID = ?";

        try {
        	 Connection conexion = DriverManager.getConnection(cadenaConexionBD, usuario, contrasena);
             conexion.setAutoCommit(true);

             PreparedStatement ps = conexion.prepareStatement(dml);
             ps.setInt(1, usuarioId);
             registrosAfectados = ps.executeUpdate();

             ps.close();
             conexion.close();
            
        } catch (SQLException ex) {
            ExcepcionesAcoger e = new ExcepcionesAcoger();
            e.setMensajeErrorAdministrador(ex.getMessage());
            e.setSentenciaSQL(dml);
            e.setCodigoErrorBD(ex.getErrorCode());
            switch (ex.getErrorCode()) {
                default:
                    e.setMensajeErrorUsuario("Error general del sistema. Consulte con el administrador.");
            }
            throw e;
        }
        return registrosAfectados;
    }

    /**
     * Método que implementa la acutalización de una protectora
     *
     * @param protectoraId Identificador de la protectora que se quiere actualizar
     * @param protectora Objeto de la clase Protectora que almaena los nuevos
     * valores a asignar a protectora
     * @return Retorna la cantidad de registros de protectora actualizados.
     * Valores posibles: 0 no se actualizado ninguna protectora; o 1 se ha actualizado un usuario
     * @exception ExcepcionesAcoger Se lanzara esta excepcion cuando se produzca una violación de las constraint de la BD o por fallos internos en la ejecución del metodo o de la BD
     */
    public Integer actualizarUsuario(Integer usuarioId, Usuario user) throws ExcepcionesAcoger {
        String dml = "update USUARIO SET DNI = ?, ALIAS = ?, TELEFONO = ?, EMAIL = ?, NOMBRE = ?, APELLIDO1 = ?, APELLIDO2 = ?  where USUARIO_ID = ?";

        int registrosAfectados = 0;
        try {
            Connection conexion = DriverManager.getConnection(cadenaConexionBD, usuario, contrasena);

            PreparedStatement sentenciaPreparada = conexion.prepareStatement(dml);
            sentenciaPreparada.setString(1, user.getDni());
            sentenciaPreparada.setString(2, user.getAlias());
            sentenciaPreparada.setObject(3, user.getTelefono(), Types.INTEGER);
            sentenciaPreparada.setString(4, user.getEmail());
            sentenciaPreparada.setString(5, user.getNombre());
            sentenciaPreparada.setString(6, user.getApellido1());
            sentenciaPreparada.setString(7, user.getApellido2());
            sentenciaPreparada.setInt(8, usuarioId);

            registrosAfectados = sentenciaPreparada.executeUpdate();

            sentenciaPreparada.close();
            conexion.close();
            

        } catch (SQLException ex) {
            ExcepcionesAcoger e = new ExcepcionesAcoger();
            e.setMensajeErrorAdministrador(ex.getMessage());
            e.setSentenciaSQL(dml);
            e.setCodigoErrorBD(ex.getErrorCode());
            switch (ex.getErrorCode()) {
                case 1861:
                    e.setMensajeErrorUsuario("Algun dato ha sido introducido de manera incorrecta");
                    break; 

                case 2290:
                    e.setMensajeErrorUsuario("El formato del email ingresado no es válido. Por favor, verifica que esté correctamente escrito");
                    break;

                case 1407:
                    e.setMensajeErrorUsuario("Asegúrate de completar todos los campos. Todos son obligatorios.");
                    break;

                case 12899:
                    e.setMensajeErrorUsuario("El valor introducido es demasiado grande para el tamaño permitido de la columna.");
                    break;

                case 1438:
                    e.setMensajeErrorUsuario("La cantidad de datos que intentas almacenar en 'capacidad' excede permitida.");
                    break;
                             
                default:
                    e.setMensajeErrorUsuario("Error general del sistema. Consulte con el administrador.");
                    break;
            }
            throw e;
        }
        return registrosAfectados;
    }
    
    /**
     * Método que implementa la lectura de todas los usuarios
     * @return Retorna un ArrayList de todos los objetos con la información de cada usuario
     * @throws ExcepcionesAcoger se lanzará esta excepción cuando se produzca una violación de las constraint de la base de datos
     */
    public ArrayList<Usuario> leerUsuarios() throws ExcepcionesAcoger {
        ArrayList<Usuario> listaUsuarios = new ArrayList();
        Usuario user;

        String dql = "select * from USUARIO";

        try {
            Connection conexion = DriverManager.getConnection(cadenaConexionBD, usuario, contrasena);
            Statement sentencia = conexion.createStatement();
            ResultSet resultado = sentencia.executeQuery(dql);
            while (resultado.next()) {
                user = new Usuario();
                user.setUsuarioId(resultado.getInt("USUARIO_ID"));
                user.setAlias(resultado.getString("ALIAS"));
                user.setNombre(resultado.getString("NOMBRE"));
                user.setApellido1(resultado.getString("APELLIDO1"));
                user.setApellido2(resultado.getString("APELLIDO2"));
                user.setTelefono(resultado.getInt("TELEFONO"));
                user.setDni(resultado.getString("DNI"));
                user.setEmail(resultado.getString("EMAIL"));

                listaUsuarios.add(user);

            }
            resultado.close();
            sentencia.close();
            conexion.close();

        } catch (SQLException ex) {
            ExcepcionesAcoger e = new ExcepcionesAcoger();
            e.setMensajeErrorAdministrador(ex.getMessage());
            e.setSentenciaSQL(dql);
            e.setCodigoErrorBD(ex.getErrorCode());
            e.setMensajeErrorUsuario("Error general del sistema. Consulte con el administrador.");

            switch (ex.getErrorCode()) {
                default:
                    e.setMensajeErrorUsuario("Error general del sistema. Consulte con el administrador.");
                    break;
            }
            throw e;
        }
        return listaUsuarios;
    }
    
    /**
     * Método que implementa la lectura de una protectora
     *
     * @param protectoraId Identificador de la protectora que se quiere
     * actualizar
     * @return Retorna los datos de la protectora
     * @throws ExcepcionesAcoger se lanzará esta excepción cuando se produzca una violación de las constraint de la base de datos
     */
    public Usuario leerUsuario(Integer usuarioId) throws ExcepcionesAcoger {
        Usuario user = null;
        String dql = "select * from USUARIO where USUARIO_ID = " + usuarioId;

        try {
            Connection conexion = DriverManager.getConnection(cadenaConexionBD, usuario, contrasena);
            Statement sentencia = conexion.createStatement();
            ResultSet resultado = sentencia.executeQuery(dql);
            while (resultado.next()) {
                user = new Usuario();
                user.setUsuarioId(resultado.getInt("USUARIO_ID"));
                user.setAlias(resultado.getString("ALIAS"));
                user.setNombre(resultado.getString("NOMBRE"));
                user.setApellido1(resultado.getString("APELLIDO1"));
                user.setApellido2(resultado.getString("APELLIDO2"));
                user.setTelefono(resultado.getInt("TELEFONO"));
                user.setEmail(resultado.getString("EMAIL"));
                user.setDni(resultado.getString("DNI"));
                user.setContrasena(resultado.getString("CONTRASENA"));
            }
            resultado.close();
            sentencia.close();
            conexion.close();

        } catch (SQLException ex) {
            ExcepcionesAcoger e = new ExcepcionesAcoger();
            e.setMensajeErrorAdministrador(ex.getMessage());
            e.setSentenciaSQL(dql);
            e.setCodigoErrorBD(ex.getErrorCode());
            e.setMensajeErrorUsuario("Error general del sistema. Consulte con el administrador.");

            switch (ex.getErrorCode()) {
                case 904:
                    e.setMensajeErrorUsuario("El identificador no es valido");
                    break;

                case 1722:
                    e.setMensajeErrorUsuario("El identificador no es valido");
                    break;

                case 936:
                    e.setMensajeErrorUsuario("El identificador no es valido");
                    break;

                default:
                    e.setMensajeErrorUsuario("Error general del sistema. Consulte con el administrador.");
                    break;
            }
            throw e;
        }
        return user;
    }
    
    
    public Integer insertarFavorito(Integer usuarioId, Integer animalId) throws ExcepcionesAcoger {
        String dml = "INSERT INTO favoritos (usuario_id, animal_id) VALUES (?, ?)";
        int registrosAfectados = 0;

        try (Connection conexion = DriverManager.getConnection(cadenaConexionBD, usuario, contrasena);
             PreparedStatement sentencia = conexion.prepareStatement(dml)) {

            sentencia.setInt(1, usuarioId);
            sentencia.setInt(2, animalId);

            registrosAfectados = sentencia.executeUpdate();

            System.out.println(">>> insertando favorito: usuario=" + usuarioId + ", animal=" + animalId);

        } catch (SQLException ex) {
            ExcepcionesAcoger e = new ExcepcionesAcoger();
            e.setMensajeErrorAdministrador(ex.getMessage());
            e.setSentenciaSQL(dml);
            e.setCodigoErrorBD(ex.getErrorCode());

            switch (ex.getErrorCode()) {
                case 1: 
                    e.setMensajeErrorUsuario("Este animal ya está guardado como favorito por este usuario.");
                    break;
                case 2291:
                    e.setMensajeErrorUsuario("El usuario o el animal no existen.");
                    break;
                default:
                    e.setMensajeErrorUsuario("Error al guardar el animal como favorito. Consulte con el administrador.");
            }

            throw e;
        }

        return registrosAfectados;
    }

    
    public Integer eliminarAnimalDeUsuario(Integer usuarioId, Integer animalId) throws ExcepcionesAcoger {
        String dml = "DELETE FROM favoritos WHERE usuario_id = ? AND animal_id = ?";
        int registrosAfectados = 0;

        try (Connection conexion = DriverManager.getConnection(cadenaConexionBD, usuario, contrasena);
             PreparedStatement sentencia = conexion.prepareStatement(dml)) {

            sentencia.setInt(1, usuarioId);
            sentencia.setInt(2, animalId);

            registrosAfectados = sentencia.executeUpdate();

        } catch (SQLException ex) {
            ExcepcionesAcoger e = new ExcepcionesAcoger();
            e.setMensajeErrorAdministrador(ex.getMessage());
            e.setSentenciaSQL(dml);
            e.setCodigoErrorBD(ex.getErrorCode());
            e.setMensajeErrorUsuario("Error al eliminar el animal de favoritos. Consulte con el administrador.");
            throw e;
        }

        return registrosAfectados;
    }

    public ArrayList<Animal> obtenerFavoritosDeUsuario(Integer usuarioId) throws ExcepcionesAcoger {
    	ArrayList<Animal> lista = new ArrayList<>();
    	String dql = "SELECT a.ANIMAL_ID, a.IDENTIFICADOR, a.FECHA_NACIMIENTO, a.NOMBRE AS NOMBRE_ANIMAL, " +
    	        "a.VACUNA, a.CASTRADO, a.PASAPORTE, a.SEXO, a.ANIMAL_TIPO, a.RAZA, a.HISTORIA, a.FOTO," +
    	        "p.PROTECTORA_ID, p.NOMBRE AS NOMBRE_PROTECTORA, p.EMAIL, p.TELEFONO, p.HISTORIA AS HISTORIA_PROTECTORA, " +
    	        "p.CALLE, p.NUMERO_CALLE, p.COD_POSTAL " +
    	        "FROM ANIMAL a " +
    	        "JOIN FAVORITOS f ON a.ANIMAL_ID = f.ANIMAL_ID " +   
    	        "JOIN PROTECTORA p ON a.PROTECTORA_ID = p.PROTECTORA_ID " +
    	        "WHERE f.USUARIO_ID = ?";

        try (Connection conexion = DriverManager.getConnection(cadenaConexionBD, usuario, contrasena);
             PreparedStatement ps = conexion.prepareStatement(dql)) {

            ps.setInt(1, usuarioId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Protectora protectora = new Protectora();
                protectora.setProtectoraId(rs.getInt("PROTECTORA_ID"));
                protectora.setNombre(rs.getString("NOMBRE_PROTECTORA"));
                protectora.setEmail(rs.getString("EMAIL"));
                protectora.setTelefono(rs.getInt("TELEFONO"));
                protectora.setHistoria(rs.getString("HISTORIA_PROTECTORA"));
                protectora.setCalle(rs.getString("CALLE"));
                protectora.setNumeroCalle(rs.getInt("NUMERO_CALLE"));
                protectora.setCodPostal(rs.getInt("COD_POSTAL"));
                protectora.setFoto(rs.getString("FOTO"));


                Animal animal = new Animal();
                animal.setAnimalId(rs.getInt("ANIMAL_ID"));
                animal.setIdentificador(rs.getString("IDENTIFICADOR"));
                animal.setFechaNacimiento(rs.getDate("FECHA_NACIMIENTO"));
                animal.setNombreAnimal(rs.getString("NOMBRE_ANIMAL"));
                animal.setVacuna(rs.getString("VACUNA"));
                animal.setCastrado(rs.getString("CASTRADO"));
                animal.setPasaporte(rs.getString("PASAPORTE"));
                animal.setSexo(rs.getString("SEXO"));
                animal.setAnimalTipo(rs.getString("ANIMAL_TIPO"));
                animal.setRaza(rs.getString("RAZA"));
                animal.setHistoria(rs.getString("HISTORIA"));
                animal.setFoto(rs.getString("FOTO"));

                animal.setProtectora(protectora);
                animal.setEsFavorito(true);

                lista.add(animal);
            }

        } catch (SQLException ex) {
            ExcepcionesAcoger e = new ExcepcionesAcoger();
            e.setMensajeErrorAdministrador(ex.getMessage());
            e.setSentenciaSQL(dql);
            e.setCodigoErrorBD(ex.getErrorCode());
            e.setMensajeErrorUsuario("Error al obtener los favoritos del usuario.");
            throw e;
        }

        return lista;
    }
  
    public void marcarSiEsFavorito(Animal animal, int usuarioId) throws SQLException {
        String sql = "SELECT 1 FROM favoritos WHERE usuario_id = ? AND animal_id = ?";
        try (Connection conn = DriverManager.getConnection(cadenaConexionBD, usuario, contrasena);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, usuarioId);
            ps.setInt(2, animal.getAnimalId());

            ResultSet rs = ps.executeQuery();
            animal.setEsFavorito(rs.next()); 
        }
    }

    public void cargarFotoProtectoraDesdeArchivo(int protectoraId, String rutaImagen) throws Exception {
        File archivo = new File(rutaImagen);
        if (!archivo.exists()) {
            System.out.println("❌ Archivo no encontrado: " + rutaImagen);
            return;
        }

        // Solo el nombre del archivo
        String nombreArchivo = archivo.getName();

        String sql = "UPDATE PROTECTORA SET FOTO = ? WHERE PROTECTORA_ID = ?";

        try (Connection conn = DriverManager.getConnection(cadenaConexionBD, usuario, contrasena);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nombreArchivo);  // ← se guarda el nombre
            ps.setInt(2, protectoraId);

            int filas = ps.executeUpdate();
            System.out.println("✅ Ruta de foto registrada para PROTECTORA_ID=" + protectoraId + " → " + nombreArchivo);
        }
    }

    
    public void cargarFotoDesdeArchivo(int animalId, String rutaImagen) throws Exception {
        File archivo = new File(rutaImagen);
        if (!archivo.exists()) {
            System.out.println("❌ Archivo no encontrado: " + rutaImagen);
            return;
        }

        // Solo extraemos el nombre del archivo
        String nombreArchivo = archivo.getName();

        String sql = "UPDATE ANIMAL SET FOTO = ? WHERE ANIMAL_ID = ?";

        try (Connection conn = DriverManager.getConnection(cadenaConexionBD, usuario, contrasena);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nombreArchivo);  // ← se guarda el nombre
            ps.setInt(2, animalId);

            int filas = ps.executeUpdate();
            System.out.println("✅ Ruta de foto registrada para ANIMAL_ID=" + animalId + " → " + nombreArchivo);
        }
    }




}



       