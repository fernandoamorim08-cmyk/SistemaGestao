# 🗂 Sistema de Gestão de Projetos e Equipes
**Projeto Acadêmico — Análise e Desenvolvimento de Sistemas**
**Disciplina: Programação Orientada a Objetos com Java**

---

## 📋 Índice
1. [Visão Geral](#visão-geral)
2. [Tecnologias](#tecnologias)
3. [Arquitetura MVC](#arquitetura-mvc)
4. [Diagrama de Classes UML](#diagrama-uml)
5. [Conceitos POO Demonstrados](#conceitos-poo)
6. [Estrutura de Pastas](#estrutura)
7. [Passo a Passo para Execução](#execução)
8. [Exemplos de Uso](#exemplos)
9. [Melhorias Futuras](#melhorias)

---

## 1. Visão Geral

Sistema desktop desenvolvido em Java com interface gráfica (Swing) e banco de dados MySQL,
permitindo o gerenciamento completo de projetos, equipes, tarefas e usuários de uma organização.

### Funcionalidades
| Módulo | O que faz |
|--------|-----------|
| Usuários | Cadastro, edição, desativação e busca de usuários |
| Projetos | CRUD completo de projetos com status e gerente responsável |
| Equipes | Criação de equipes e gerenciamento de membros |
| Tarefas | Cadastro de tarefas com prioridade, prazo e responsável |
| Relatórios | 4 relatórios: projetos em andamento, tarefas pendentes, usuários por perfil, tarefas atrasadas |

---

## 2. Tecnologias

| Tecnologia | Versão | Finalidade |
|------------|--------|-----------|
| Java JDK | 17+ | Linguagem principal |
| Java Swing | (incluso no JDK) | Interface gráfica |
| JDBC | (incluso no JDK) | Conexão com banco de dados |
| MySQL | 8.0+ | Banco de dados relacional |
| MySQL Connector/J | 8.0+ | Driver JDBC para MySQL |

### Dependência externa necessária
Baixe o **MySQL Connector/J** em: https://dev.mysql.com/downloads/connector/j/
Adicione o arquivo `.jar` ao classpath do projeto na sua IDE.

---

## 3. Arquitetura MVC

```
┌──────────────────────────────────────────────────────────┐
│                    USUÁRIO                                │
└───────────────────────┬──────────────────────────────────┘
                        │ interage
                        ▼
┌──────────────────────────────────────────────────────────┐
│  VIEW (view/)   — O que o usuário VÊ                      │
│  TelaLogin, TelaPrincipal, TelaUsuarios, TelaProjetos...  │
│  Responsabilidade: exibir dados e capturar entradas       │
└───────────────────────┬──────────────────────────────────┘
                        │ chama
                        ▼
┌──────────────────────────────────────────────────────────┐
│  CONTROLLER (controller/)  — O que o sistema FAZ          │
│  UsuarioController, ProjetoController, TarefaController.. │
│  Responsabilidade: validar dados, aplicar regras negócio  │
└───────────────────────┬──────────────────────────────────┘
                        │ usa
                        ▼
┌──────────────────────────────────────────────────────────┐
│  DAO (dao/)   — Como persiste os dados                    │
│  UsuarioDAO, ProjetoDAO, EquipeDAO, TarefaDAO             │
│  Responsabilidade: executar SQL e mapear resultados       │
└───────────────────────┬──────────────────────────────────┘
                        │ conecta
                        ▼
┌──────────────────────────────────────────────────────────┐
│  BANCO DE DADOS (MySQL)                                   │
│  sistema_gestao: usuarios, projetos, equipes, tarefas...  │
└──────────────────────────────────────────────────────────┘

  MODEL (model/) atravessa TODAS as camadas como objetos de dados:
  Usuario, Projeto, Equipe, Tarefa
```

---

## 4. Diagrama de Classes UML

```
┌──────────────────┐       ┌──────────────────────┐
│    <<enum>>      │       │       Usuario          │
│  PerfilUsuario   │──────▶│ ─ id: int             │
│──────────────────│       │ ─ nome: String        │
│ ADMINISTRADOR    │       │ ─ cpf: String         │
│ GERENTE          │       │ ─ email: String       │
│ COLABORADOR      │       │ ─ cargo: String       │
└──────────────────┘       │ ─ login: String       │
                           │ ─ senha: String       │
┌──────────────────┐       │ ─ perfil: PerfilUsr   │
│    <<enum>>      │       │ ─ ativo: boolean      │
│  StatusProjeto   │       │──────────────────────│
│──────────────────│       │ + getters/setters     │
│ PLANEJADO        │       │ + toString()          │
│ EM_ANDAMENTO     │       └──────────────────────┘
│ CONCLUIDO        │              ▲            ▲
│ CANCELADO        │              │            │
└──────────────────┘         ASSOCIAÇÃO   AGREGAÇÃO
                              (gerente)   (membros)
┌──────────────────┐              │            │
│    <<enum>>      │              │            │
│  StatusTarefa    │       ┌──────┴───┐  ┌────┴─────┐
│──────────────────│       │ Projeto  │  │  Equipe  │
│ PENDENTE         │       │──────────│  │──────────│
│ EM_ANDAMENTO     │──────▶│ id       │  │ id       │
│ CONCLUIDA        │       │ nome     │  │ nome     │
│ CANCELADA        │       │ descricao│  │ descricao│
└──────────────────┘       │ dataInic │  │ membros  │
                           │ dataFim  │  │  List<>  │
┌──────────────────┐       │ status   │  └──────────┘
│    <<enum>>      │       │ gerente  │       │
│   Prioridade     │       │ tarefas◀─┘  COMPOSIÇÃO
│──────────────────│       │ equipes  │       │
│ BAIXA            │──────▶│──────────│  ┌────┴─────┐
│ MEDIA            │       │ métodos  │  │  Tarefa  │
│ ALTA             │       └──────────┘  │──────────│
│ CRITICA          │                     │ id       │
└──────────────────┘                     │ titulo   │
                                         │ descricao│
                                         │ projeto  │
                                         │ respons. │
                                         │ dataLim  │
                                         │ priorid. │
                                         │ status   │
                                         │──────────│
                                         │ isAtras. │
                                         └──────────┘

RELACIONAMENTOS:
──────────────────────────────────────────────────────
Associação  → Projeto TEM UM Gerente (Usuario)
              Tarefa  TEM UM Responsável (Usuario)

Composição  → Projeto COMPÕE Tarefas
              (se Projeto excluído, Tarefas também)

Agregação   → Equipe AGREGA Membros (Usuarios)
              Projeto AGREGA Equipes
              (partes existem independentemente do todo)

Herança     → DAOException → SistemaException → RuntimeException

N:N DB      → equipe_membros  (Equipe ↔ Usuario)
              projeto_equipes (Projeto ↔ Equipe)
```

---

## 5. Conceitos POO Demonstrados

### Classes e Objetos
```java
// Classe: molde/planta baixa
public class Usuario { ... }

// Objeto: instância concreta criada a partir do molde
Usuario joao = new Usuario("João", "123.456.789-00", ...);
```

### Encapsulamento
```java
private String nome;          // Atributo privado (protegido)
public String getNome() { return nome; }   // Getter público
public void setNome(String nome) {         // Setter com validação
    if (nome.isEmpty()) throw new IllegalArgumentException("...");
    this.nome = nome;
}
```

### Herança
```java
// DAOException herda tudo de SistemaException
public class DAOException extends SistemaException {
    // Adiciona ou sobrescreve comportamentos
}
```

### Polimorfismo
```java
// Todos os enums podem chamar toString() de forma diferente
StatusProjeto.EM_ANDAMENTO.toString(); // → "Em Andamento"
Prioridade.ALTA.toString();            // → "Alta"
// Mesmo método, comportamento diferente por classe
```

### Enum
```java
public enum Prioridade { BAIXA, MEDIA, ALTA, CRITICA }
// Uso: tarefa.setPrioridade(Prioridade.ALTA);
```

### ArrayList (Lista dinâmica)
```java
List<Usuario> membros = new ArrayList<>();
membros.add(novoMembro);    // adiciona
membros.remove(membro);     // remove
membros.size();             // conta
```

### Stack (Pilha — LIFO)
```java
Stack<String> historico = new Stack<>();
historico.push("Login: Ana");   // empilha
historico.push("Cadastrou: X"); // empilha
historico.pop();  // retira "Cadastrou: X" (último que entrou)
```

### Queue (Fila — FIFO)
```java
Queue<String> notificacoes = new LinkedList<>();
notificacoes.offer("Mensagem 1");  // enfileira
notificacoes.offer("Mensagem 2");  // enfileira
notificacoes.poll();  // retira "Mensagem 1" (primeiro que entrou)
```

### Tratamento de Exceções
```java
try {
    usuario = dao.autenticar(login, senha);
} catch (DAOException e) {
    // erro de banco
    JOptionPane.showMessageDialog(..., e.getMessage());
} catch (SistemaException e) {
    // regra de negócio violada
    labelMensagem.setText(e.getMessage());
}
```

### Algoritmo de Busca
```java
// Busca linear na lista em memória
public List<Usuario> buscarPorNome(String nome) {
    List<Usuario> resultado = new ArrayList<>();
    for (Usuario u : listaUsuarios) {
        if (u.getNome().toLowerCase().contains(nome.toLowerCase())) {
            resultado.add(u);
        }
    }
    return resultado;
}
// No banco usamos: WHERE nome LIKE '%texto%'
```

### Algoritmo de Ordenação
```java
// Ordenação com Comparator (TimSort internamente)
Collections.sort(lista, (u1, u2) ->
    u1.getNome().compareToIgnoreCase(u2.getNome())
);

// Ordenação encadeada: por prioridade DESC, depois data ASC
lista.sort(
    Comparator.comparing(Tarefa::getPrioridade).reversed()
              .thenComparing(t -> t.getDataLimite().toString())
);
```

---

## 6. Estrutura de Pastas

```
SistemaGestao/
├── banco_de_dados.sql          ← Script completo do banco
├── README.md                   ← Esta documentação
└── src/
    ├── enums/
    │   ├── PerfilUsuario.java  ← ADMINISTRADOR, GERENTE, COLABORADOR
    │   ├── StatusProjeto.java  ← PLANEJADO, EM_ANDAMENTO, CONCLUIDO, CANCELADO
    │   ├── StatusTarefa.java   ← PENDENTE, EM_ANDAMENTO, CONCLUIDA, CANCELADA
    │   └── Prioridade.java     ← BAIXA, MEDIA, ALTA, CRITICA
    │
    ├── model/                  ← Entidades de dados
    │   ├── Usuario.java
    │   ├── Projeto.java
    │   ├── Equipe.java
    │   └── Tarefa.java
    │
    ├── exception/              ← Exceções personalizadas
    │   ├── SistemaException.java
    │   └── DAOException.java
    │
    ├── util/
    │   └── Conexao.java        ← Gerencia conexão JDBC (Singleton)
    │
    ├── dao/                    ← Acesso ao banco de dados (Padrão DAO)
    │   ├── UsuarioDAO.java
    │   ├── ProjetoDAO.java
    │   ├── EquipeDAO.java
    │   └── TarefaDAO.java
    │
    ├── controller/             ← Lógica de negócio (MVC Controller)
    │   ├── UsuarioController.java
    │   ├── ProjetoController.java
    │   ├── EquipeController.java
    │   └── TarefaController.java
    │
    ├── view/                   ← Interface gráfica Swing (MVC View)
    │   ├── TelaLogin.java
    │   ├── TelaPrincipal.java
    │   ├── TelaUsuarios.java
    │   ├── FormularioUsuario.java
    │   ├── TelaProjetos.java
    │   ├── TelaEquipes.java
    │   ├── TelaTarefas.java
    │   └── TelaRelatorios.java
    │
    └── main/
        └── Main.java           ← Ponto de entrada (método main)
```

---

## 7. Passo a Passo para Execução

### Pré-requisitos
- Java JDK 17 ou superior instalado
- MySQL 8.0 instalado e em execução
- IDE: IntelliJ IDEA Community ou Apache NetBeans
- MySQL Connector/J (driver JDBC)

### Passo 1 — Configurar o Banco de Dados
```sql
-- No MySQL Workbench ou terminal MySQL:
SOURCE caminho/para/banco_de_dados.sql;
-- Ou abra o arquivo e execute linha por linha
```

### Passo 2 — Configurar as Credenciais
Abra `src/util/Conexao.java` e ajuste:
```java
private static final String URL     = "jdbc:mysql://localhost:3306/sistema_gestao?...";
private static final String USUARIO = "root";         // seu usuário
private static final String SENHA   = "sua_senha";    // sua senha
```

### Passo 3 — Adicionar o Driver MySQL à IDE

**IntelliJ IDEA:**
1. File → Project Structure → Modules → Dependencies
2. Clique em "+" → JARs or Directories
3. Selecione o arquivo `mysql-connector-j-X.X.X.jar`

**NetBeans:**
1. Clique direito no projeto → Properties → Libraries
2. Add JAR/Folder → selecione o arquivo `.jar`

### Passo 4 — Executar o Projeto
- Execute a classe `main/Main.java`
- A janela de login será exibida

### Passo 5 — Fazer Login
Use as credenciais de exemplo inseridas pelo script SQL:
| Login | Senha | Perfil |
|-------|-------|--------|
| ana | admin123 | Administrador |
| carlos | gerente123 | Gerente |
| maria | colab123 | Colaborador |

---

## 8. Exemplos de Uso

### Cadastrar um novo usuário
1. Faça login como `ana` (Administrador)
2. No menu principal, clique em **Usuários**
3. Clique em **➕ Novo**
4. Preencha os campos e clique em **Cadastrar**

### Criar um projeto e adicionar tarefa
1. No menu, clique em **Projetos**
2. Clique em **➕ Novo Projeto** e preencha os dados
3. Vá em **Tarefas**, selecione o projeto no dropdown
4. Clique em **➕ Nova Tarefa**

### Gerar relatório de projetos em andamento
1. No menu, clique em **Relatórios**
2. A aba "Projetos em Andamento" é exibida automaticamente
3. Clique em **🔄 Atualizar Relatórios** para recarregar

---

## 9. Sugestões de Melhorias Futuras

| Melhoria | Dificuldade | Descrição |
|----------|-------------|-----------|
| Hash de senha | Fácil | Usar BCrypt para não salvar senha em texto puro |
| Exportar PDF | Médio | Gerar relatórios em PDF com a biblioteca iText |
| Gráficos | Médio | Adicionar gráficos de pizza/barra nos relatórios (JFreeChart) |
| Notificações | Médio | E-mail automático para tarefas atrasadas |
| Log de auditoria | Fácil | Registrar em banco todas as ações dos usuários |
| Testes automatizados | Médio | JUnit 5 para testar Controllers e DAOs |
| REST API | Difícil | Expor os dados via API REST (Spring Boot) |
| Docker | Difícil | Containerizar o banco de dados com Docker |
| Internacionalização | Fácil | Suporte a múltiplos idiomas (i18n) |
| Tema escuro | Fácil | Alternar entre tema claro e escuro na interface |

---

## Créditos e Referências

- Oracle Java Documentation: https://docs.oracle.com/en/java/
- MySQL Documentation: https://dev.mysql.com/doc/
- Java Swing Tutorial: https://docs.oracle.com/javase/tutorial/uiswing/
- Padrão DAO: https://www.oracle.com/java/technologies/dataaccessobject.html

**Projeto desenvolvido para fins acadêmicos — ADS 1º Semestre**
