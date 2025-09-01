package co.com.crediya.autenticacion.model.usuario.util;

import java.util.regex.Pattern;

public final class Constants {

    private Constants() {
        // Constructor privado para evitar instanciación.
    }

    // Mensajes de error de validación
    public static final String ERROR_CAMPOS_OBLIGATORIOS = "Los campos nombres, apellidos, email y salario_base son obligatorios.";
    public static final String ERROR_FORMATO_EMAIL_INVALIDO = "El formato del email no es valido.";
    public static final String ERROR_SALARIO_FUERA_DE_RANGO = "El salario_base debe estar entre 0 y 15,000,000.";
    public static final String ERROR_EMAIL_YA_REGISTRADO = "El correo electronico ya esta registrado.";
    public static final String ERROR_DOCUMENTO_YA_REGISTRADO = "El documento de identidad ya esta registrado.";
    public static final String ERROR_PASSWORD_INVALIDO = "La contraseña no puede ser nula y debe tener al menos 8 caracteres.";
    public static final String ERROR_PASSWORD_COMPLEJIDAD = "La contraseña debe tener al menos 8 caracteres, e incluir al menos una mayúscula, una minúscula y un número.";

    // Valores de validación
    public static final double SALARIO_MINIMO = 0.0;
    public static final double SALARIO_MAXIMO = 15_000_000.0;
    public static final Pattern PASSWORD_COMPLEXITY_PATTERN =
            Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}$");
    public static final int PASSWORD_MIN_LENGTH = 8;}
