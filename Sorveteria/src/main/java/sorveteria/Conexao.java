package sorveteria;

import java.sql.*;
import javax.swing.JOptionPane;

public class Conexao {
    // Altere a senha para a senha do seu PostgreSQL local.
    private static final String URL = "jdbc:postgresql://localhost:5432/sorveteria";
    private static final String USUARIO = "postgres";
    private static final String SENHA = "2212";

    public static Connection abrir() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, SENHA);
    }

    public static void erro(Exception e) {
        JOptionPane.showMessageDialog(null, "Erro: " + e.getMessage());
    }
}
