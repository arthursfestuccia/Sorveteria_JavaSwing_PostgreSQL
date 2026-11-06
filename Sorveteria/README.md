# Sorveteria — Java Swing + PostgreSQL

Projeto simples de ponto de venda para abrir no Apache NetBeans (projeto Maven).

## Requisitos
- JDK 17 ou superior
- Apache NetBeans com Maven
- PostgreSQL local na porta 5432

## Como configurar
1. Abra o PostgreSQL (pgAdmin ou psql). Execute `banco/01_criar_banco.sql` conectado ao banco `postgres`.
2. Conecte-se ao banco recém-criado `sorveteria` e execute `banco/02_tabelas.sql`.
3. Abra `src/main/java/sorveteria/Conexao.java` e altere a constante `SENHA` para a senha do seu usuário `postgres` (e `USUARIO`, se necessário).
4. No NetBeans, vá em **Arquivo > Abrir Projeto**, escolha a pasta `Sorveteria` e aguarde o Maven baixar a dependência JDBC do PostgreSQL (requer internet na primeira vez).
5. Execute a classe `sorveteria.Main` (ou pelo Maven: `mvn compile exec:java`).
6. Entre com usuário **admin** e senha **1234**.

## Arquivos
- `Main.java`: inicializa o programa.
- `Conexao.java`: configura e abre conexão JDBC.
- `Login.java`: valida usuário e senha cadastrados no banco.
- `Menu.java`: navegação pelas telas.
- `Funcionarios.java`: CRUD de funcionários.
- `Produtos.java`: CRUD de sorvetes.
- `Vendas.java`: carrinho, cálculo total, registro da venda e baixa de estoque.
- `banco/*.sql`: criação do banco e tabelas, com dados de exemplo.

## Demonstração sugerida
1. Fazer login com `admin / 1234`.
2. Cadastrar, editar, listar e excluir um funcionário de teste.
3. Cadastrar, editar, listar e excluir um sorvete de teste (ou usar o sorvete de chocolate já cadastrado).
4. Abrir Vendas, selecionar sorvete, informar quantidade, adicionar e finalizar.
5. Conferir o registro em `vendas` e `itens_venda` no PostgreSQL e a redução do estoque em `produtos`.

## Limitações (para comentar na apresentação)
- Projeto didático: senhas são armazenadas em texto simples e aparecem na tela de funcionários. Não use assim em produção.
- Não há emissão de nota fiscal, pagamentos ou relatórios.
- Produtos vendidos não podem ser excluídos por causa da referência no histórico de vendas.
- Não há histórico de vendas na interface; os registros ficam no PostgreSQL.

## Requisitos do PDF atendidos
Tema Sorveteria; Java, Swing, NetBeans, PostgreSQL local e JDBC; login validado no banco; cadastro/edição/exclusão/listagem de funcionários e produtos; venda com produtos cadastrados, quantidades, total e persistência. Os requisitos sobre divisão de tarefas, apresentação e conclusão devem ser preparados pelo(s) integrante(s) do grupo.
