package sorveteria;

import java.awt.*;
import java.math.BigDecimal;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class Vendas extends JFrame {
    
    private final JComboBox<ProdutoOpcao> produtos = new JComboBox<>();
    private final JSpinner quantidade = new JSpinner(new SpinnerNumberModel(1, 1, 10000, 1));
    
    private final DefaultTableModel modelo = new DefaultTableModel
        (new String[]{"ID", "Sorvete", "Quantidade", "Preço unitário", "Subtotal"}, 0) {
        public boolean isCellEditable(int l, int c) { return false; }
    };
    
    private final JTable tabela = new JTable(modelo);
    private final JLabel totalLabel = new JLabel("Total: R$ 0,00");

    private static class ProdutoOpcao {
        
        final int id, estoque;
        final String nome;
        final BigDecimal preco;
        
        ProdutoOpcao(int id, String nome, BigDecimal preco, int estoque) {
            this.id = id; this.nome = nome; this.preco = preco; this.estoque = estoque;
        }
        
        @Override public String toString() { return nome + " - R$ " + preco + " (estoque: " + estoque + ")"; }
    }

    public Vendas() {
       
        setTitle("Venda de Sorvetes"); setSize(800, 450); setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
       
        JPanel topo = new JPanel();
        JButton adicionar = new JButton("Adicionar"), atualizar = new JButton("Atualizar produtos");
       
        topo.add(new JLabel("Produto:")); topo.add(produtos);
        topo.add(new JLabel("Quantidade:")); topo.add(quantidade);
        topo.add(adicionar); topo.add(atualizar);
        
        JPanel rodape = new JPanel();
        JButton remover = new JButton("Remover item"), finalizar = new JButton("Finalizar venda");
        
        rodape.add(remover); rodape.add(totalLabel); rodape.add(finalizar);
        
        add(topo, BorderLayout.NORTH); add(new JScrollPane(tabela), BorderLayout.CENTER); add(rodape, BorderLayout.SOUTH);
        
        adicionar.addActionListener(e -> adicionar()); atualizar.addActionListener(e -> carregarProdutos());
        remover.addActionListener(e -> { if (tabela.getSelectedRow() >= 0) { modelo.removeRow(tabela.getSelectedRow()); calcularTotal(); } });
        finalizar.addActionListener(e -> finalizar());
        
        carregarProdutos();
    }

    private void carregarProdutos() {
        produtos.removeAllItems();
        
        try (Connection c = Conexao.abrir(); Statement s = c.createStatement(); ResultSet r = s.executeQuery("SELECT * FROM produtos ORDER BY nome")) {
            while (r.next()) produtos.addItem(new ProdutoOpcao(r.getInt("id"), r.getString("nome"), r.getBigDecimal("preco"), r.getInt("estoque")));
        } 

        catch (SQLException e) { Conexao.erro(e); }

    }

    private void adicionar() {
        
        ProdutoOpcao p = (ProdutoOpcao) produtos.getSelectedItem();
        
        if (p == null) { JOptionPane.showMessageDialog(this, "Cadastre um produto primeiro."); 
            return; }
        
        int qtd = (Integer) quantidade.getValue();
        int noCarrinho = 0;
        
        for (int i = 0; i < modelo.getRowCount(); i++) 
            if ((Integer) modelo.getValueAt(i, 0) == p.id) noCarrinho += (Integer) modelo.getValueAt(i, 2);
        
        if (qtd + noCarrinho > p.estoque) { JOptionPane.showMessageDialog(this, "Estoque insuficiente."); 
            return; }
        
        modelo.addRow(new Object[]{p.id, p.nome, qtd, p.preco, p.preco.multiply(BigDecimal.valueOf(qtd))});
        
        calcularTotal();

    }

    private BigDecimal calcularTotal() {
        BigDecimal total = BigDecimal.ZERO;
        
        for (int i = 0; i < modelo.getRowCount(); i++) 
            total = total.add((BigDecimal) modelo.getValueAt(i, 4));

        totalLabel.setText("Total: R$ " + total);

        return total;
    }

    private void finalizar() {
        
        if (modelo.getRowCount() == 0) { JOptionPane.showMessageDialog(this, "Adicione um produto."); return; }
        
        try (Connection c = Conexao.abrir()) {
            c.setAutoCommit(false);
            
            try {
                // Reserva o estoque de cada item; se algo falhar, desfaz toda a venda.
                try (PreparedStatement baixa = c.prepareStatement("UPDATE produtos SET estoque=estoque-? WHERE id=? AND estoque>=?")) {
                    
                    for (int i = 0; i < modelo.getRowCount(); i++) {
                        int qtd = (Integer) modelo.getValueAt(i, 2);
                        baixa.setInt(1, qtd); baixa.setInt(2, (Integer) modelo.getValueAt(i, 0)); baixa.setInt(3, qtd);
                        if (baixa.executeUpdate() != 1) throw new SQLException("Estoque insuficiente para " + modelo.getValueAt(i, 1));
                    }

                }

                int vendaId;
                
                try (PreparedStatement p = c.prepareStatement("INSERT INTO vendas(total) VALUES(?)", Statement.RETURN_GENERATED_KEYS)) {
                    
                    p.setBigDecimal(1, calcularTotal()); p.executeUpdate();
                    try (ResultSet r = p.getGeneratedKeys()) { r.next(); vendaId = r.getInt(1); }

                }
                
                try (PreparedStatement p = c.prepareStatement("INSERT INTO itens_venda(venda_id,produto_id,quantidade,preco_unitario) VALUES(?,?,?,?)")) {
                    
                    for (int i = 0; i < modelo.getRowCount(); i++) {
                        p.setInt(1, vendaId); p.setInt(2, (Integer) modelo.getValueAt(i, 0));
                        p.setInt(3, (Integer) modelo.getValueAt(i, 2)); p.setBigDecimal(4, (BigDecimal) modelo.getValueAt(i, 3));
                        p.executeUpdate();
                    }

                }
                
                c.commit(); JOptionPane.showMessageDialog(this, "Venda nº " + vendaId + " registrada! Total: R$ " + calcularTotal());
                
                modelo.setRowCount(0); calcularTotal(); carregarProdutos();

            } 
            
            catch (SQLException e) { c.rollback(); throw e; }

        } 

        catch (SQLException e) { Conexao.erro(e); }

    }
}
