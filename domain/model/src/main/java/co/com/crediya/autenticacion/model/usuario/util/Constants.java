package co.com.crediya.autenticacion.model.usuario.util;

import java.util.regex.Pattern;

public final class Constants {

    private Constants() { }

    public static final String ERROR_NAME_REQUIRED_FIELD = "El nombre es obligatorio.";
    public static final String ERROR_LASTNAME_REQUIRED_FIELD = "El apellido es obligatorio.";
    public static final String ERROR_EMAIL_REQUIRED_FIELD = "El email es obligatorio.";
    public static final String ERROR_MESSAGE_REQUIRED_FIELDS = "Los campos nombres, apellidos, email y salario_base son obligatorios.";
    public static final String EMAIL_FORMAT_ERROR_MESSAGE = "El formato del email no es valido.";
    public static final String INVALID_SALARY_BASE_MESSAGE = "El salario_base debe estar entre 0 y 15,000,000.";
    public static final String ERROR_EMAIL_ALREADY_REGISTERED = "El correo electronico ya esta registrado.";
    public static final String ERROR_DOCUMENT_ALREADY_REGISTERED = "El documento de identidad ya esta registrado.";
    public static final String PASSWORD_REQUIREMENTS_ERROR = "La contraseña no puede ser nula y debe tener al menos 8 caracteres.";
    public static final String PASSWORD_VALIDATION_ERROR_MESSAGE = "La contraseña debe tener al menos 8 caracteres, e incluir al menos una mayúscula, una minúscula y un número.";

    public static final double MINIMUM_SALARY = 0.0;
    public static final double MAXIMUM_SALARY = 15_000_000.0;
    public static final Pattern PASSWORD_COMPLEXITY_PATTERN =
            Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}$");
    public static final int PASSWORD_MIN_LENGTH = 8;
    public static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$"
    );

}
