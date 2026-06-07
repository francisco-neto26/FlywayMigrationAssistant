# Flyway Migration Assistant

O **Flyway Migration Assistant** é uma aplicação desktop projetado para auxiliar desenvolvedores na criação, organização e validação de migrações SQL do Flyway para o banco de dados **PostgreSQL**. A ferramenta atua como um *linter* e guardião de qualidade de banco de dados, assegurando a segurança, nomenclatura e padronização dos scripts antes de serem executados.
---
## Como Funciona

- **Geração de Arquivos**: Cria migrações versionadas ou repetíveis com nomes padronizados com base em timestamps e no tipo de ação do banco.
- **Validação de Sintaxe e Semântica**: Detecta erros de digitação de comandos SQL e previne a criação de scripts malformados.
- **Segurança de Dados**: Bloqueia comandos potencialmente perigosos (como `DROP DATABASE`, `TRUNCATE`, ou comandos de transações como `BEGIN` e `COMMIT` que quebram o ciclo do Flyway).
- **Prevenção de Alterações Acidentais**: Impede a execução de `UPDATE` e `DELETE` sem cláusula `WHERE` ou contendo expressões genéricas como `WHERE 1=1`.
- **Dicionário de Dados Obrigatório**:
  - Exige definição de Chave Primária (`PRIMARY KEY`) em todas as tabelas novas.
  - Obriga a documentação de novos objetos (tabelas, colunas, views, etc.) exigindo comandos `COMMENT ON`.
- **Regras de Nomenclatura**: Valida se nomes de tabelas, colunas e chaves usam o padrão `snake_case` e respeitam o limite de 63 caracteres do PostgreSQL.
---
## Tecnologias Utilizadas

- **Java 21**: Linguagem base do projeto.
- **JavaFX & AtlantaFX**: Interface gráfica desktop com design moderno e temas dinâmicos (como *Darcula*).
- **ANTLR 4**: Mecanismo de análise sintática e geração de AST (Abstract Syntax Tree) baseado na gramática do PostgreSQL.
- **Google Guice**: Injeção de dependências para acoplamento fraco entre as camadas.
- **SnakeYAML**: Leitura de configurações flexíveis em formato `.yml`.
- **SLF4J + Logback**: Gerenciamento de logs estruturados.
- **JUnit 5 + Mockito**: Suíte de testes automatizados das regras de negócio.
- **Maven**: Ferramenta de automação de compilação e gerenciamento de dependências.
---
## Arquitetura

O sistema foi estruturado seguindo conceitos de **Clean Architecture** e **DDD (Domain-Driven Design)** dividido em módulos com responsabilidades muito bem definidas:
1. **Domínio (`dominio`)**: Contém a lógica central e os modelos (`Arquivo`, `Modulo`, `Funcao`), além das regras de negócio e validação de SQL.
2. **Aplicação (`aplicacao`)**: Coordena os fluxos de trabalho e a geração de templates SQL.
3. **Persistência (`persistencia`)**: Responsável por ler e escrever os arquivos de migração no disco.
4. **UI (`ui`)**: Interface rica estruturada com o padrão Model-View-Controller (MVC) em FXML.
---
### Pré-requisitos

	Eclipse Adoptium jdk-21.0.9.10-hotspot
