# backbee
Automatic software backup in Java

# BackBee - Projeto de Backup com Log

## Descrição
Este é um projeto Java que realiza o **backup mensal** de pastas específicas do usuário e registra toda a execução em um **arquivo de log**.

Principais características:
- Backup automático baseado na data atual.
- Evita backups duplicados no mesmo mês.
- Cópia segura de arquivos e diretórios.
- Registro de toda a atividade em um arquivo `.log`.

## Tecnologias
- Java 17+
- JDBC (para registrar backups no banco de dados PostgreSQL)
- Launch4j (para geração de executável `.exe`)

## Estrutura de Pastas
```
backbee/
├── backups/
│   └── backup_YYYY_MM/
├── logs/
│   └── backbee-YYYY-MM-DD_HH-MM-SS.log
├── src/
    ├── BackupService.java
    ├── Logger.java
    ├── Copy.java
    └── BackupDao.java
```

## Como Funciona
1. **BackupService**:
   - Verifica se o backup do mês atual já existe.
   - Copia as pastas configuradas para um diretório de destino.
   - Salva a data do backup no banco.

2. **Logger**:
   - Cria um arquivo de log único por execução.
   - Registra logs de informações, erros e depuração.
   - Fecha o arquivo de log no final da execução (usando `shutdown hook`).

## Como Executar
1. Compile o projeto:
```bash
javac -d bin src/**/*.java
```

2. Execute o programa:
```bash
java -cp bin Main
```

Ou, se já tiver gerado o `.exe` com Launch4j, basta executar o `.exe` normalmente.

## Observações
- O log é salvo automaticamente na pasta `logs/` ao lado do executável ou `.jar`.
- Certifique-se de que as pastas de origem configuradas existam no sistema.

## Melhorias Futuras
- Configuração externa via `.properties`.
- Log para console + arquivo simultaneamente.
- Geração automática de backup incremental.
- Compressão automática dos backups (ZIP).

---

Desenvolvido com por Michael Ribeiro
