package co.com.crediya.autenticacion.model.usuario.gateways;

public interface PasswordEncryptionGateway {
    String encriptar(String passwordPlano);
}
