package sorveteria;

import java.awt.*;
import javax.swing.*;

public class Menu extends JFrame {
    public Menu() {
        
        setTitle("Sorveteria - Ponto de Venda");
        setSize(420, 220);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        JPanel painel = new JPanel(new GridLayout(3, 1, 10, 10));
        
        painel.setBorder(BorderFactory.createEmptyBorder(15, 30, 15, 30));
        
        JButton funcionarios = new JButton("Funcionários");
        JButton produtos = new JButton("Produtos / Sorvetes");
        JButton vendas = new JButton("Vendas");
        
        painel.add(funcionarios); painel.add(produtos); painel.add(vendas);
        add(painel);
        
        funcionarios.addActionListener(e -> new Funcionarios().setVisible(true));
        produtos.addActionListener(e -> new Produtos().setVisible(true));
        vendas.addActionListener(e -> new Vendas().setVisible(true));
    }
}
