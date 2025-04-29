# BackBee
Automatic backup software developed in Java. Its purpose is to make backup copies of specific folders once a month, in a practical and efficient way, without manual intervention.
The application is designed to run locally, ideal for those who want to guarantee the preservation of important files in an automated way.

### Technologies and Concepts used:
- Java 17
- JDBC (to record backups in the PostgreSQL database)
- Launch4j (for generating executable `.exe`)
- Java NIO for manipulating files and directories.
- Layered architecture: clear separation between DAO, Service and Model.
- double-checked locking.
- Recursion to delete files.
- Logs in files.
- Manual resource management: try-with-resources was not used on purpose.
Instead, the release of resources (such as open files) is done manually via finally blocks.

Main features:
- Automatic backup based on the current date.
- Rotation of backups performed, keeping no more than three.
- Logging of all activity in a `.txt` file.

## Project Structure
```
backbee/
├── backups/
├── src/
    ├── BackbeeApplication.java
    ├── BackupService.java
    ├── Copy.java
    └── BackupDao.java
```

## How it works
1. **BackupService**:
   - Checks if the backup for the current month already exists.
   - Copies the configured folders to a destination directory.
   - Saves the backup date in the database.

## How to Run
1. Compile the project:
```bash
javac -d bin src/**/*.java
```

2. Run the program:
```bash
java -cp bin Main
```

Or, if you have already generated the `.exe` with Launch4j, just run the `.exe` normally.

## Notes
- The log is automatically saved in the `logs/` folder next to the executable or `.jar`.
- Make sure that the configured source folders exist on the system.

## Future Improvements
[] Perform 2 backups monthly.
[] Automatic compression of backups (ZIP).
[] Create a user-friendly interface for configuring source and destination folders.
---
Developed by Michael Ribeiro
---
pt-BR:
Um software de backup automático desenvolvido em Java. Seu objetivo é realizar cópias de segurança de pastas específicas uma vez por mês, de forma prática e eficiente, sem intervenção manual.
A aplicação foi projetada para executar localmente, ideal para quem deseja garantir a preservação de arquivos importantes de maneira automatizada.

### Tecnologias e conceitos utilizados:
- Java 17+
- JDBC (para registrar backups no banco de dados PostgreSQL)
- Launch4j (para geração de executável `.exe`)
- Java NIO para manipulação de arquivos e diretórios.
- Arquitetura em camadas: separação clara entre DAO, Service e Model.
- double-checked locking.
- Recursividade para deletar arquivos.
- Logs em arquivos.
- Gerenciamento manual de recursos: não foi utilizado try-with-resources de forma proposital.
Em vez disso, a liberação de recursos (como arquivos abertos) é feita manualmente através de blocos finally.

Principais características:
- Backup automático baseado na data atual.
- Rodízio de backups realizados, mantendo não mais do que três.
- Registro de toda a atividade em um arquivo `.txt`.

## Estrutura de Pastas
```
backbee/
├── backups/
├── src/
    ├── BackbeeApplication.java
    ├── BackupService.java
    ├── Copy.java
    └── BackupDao.java
```

## Como Funciona
1. **BackupService**:
   - Verifica se o backup do mês atual já existe.
   - Copia as pastas configuradas para um diretório de destino.
   - Salva a data do backup no banco.

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
[] Realizar 2 backups mensalmente.
[] Compressão automática dos backups (ZIP).
[] Criar interface amigável para configurar pastas de origem e destino.
---
Desenvolvido por Michael Ribeiro
