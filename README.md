# Projeto de Sistemas Distribuídos 2016-2017 #

Grupo T50

Número | Nome | Email
 --- | --- | ---
77917	| Daniel José Nazaré Madruga | daniel.madruga@tecnico.ulisboa.pt
78013	| Bruno Miguel Silva Henriques | bruno.s.henriques@tecnico.ulisboa.pt
78042	| Bruno Miguel Nascimento Carola | bruno.carola@tecnico.ulisboa.pt
-------------------------------------------------------------------------------

## Instruções de instalação


0. Iniciar sistema operativo
* Linux

1. Configurar o Servidor de Nomes (UDDI)
   1. Criar um ficheiro [settings.xml](http://disciplinas.tecnico.ulisboa.pt/leic-sod/2016-2017/labs/06-ws2/settings.xml)
   2. Colocar o ficheiro na HOME do Maven: `mv settings.xml ~/.m2/`
   3. Substituir `user:pass` por `t50:WkyodoJT`
     
2. Obter código fonte do projeto (versão entregue)
```bash
git clone -b SD_P2 https://github.com/tecnico-distsys/T_50-project.git
``` 
 
3. Instalar módulos de bibliotecas auxiliares e alguns clientes com dependências
```
cd uddi-naming
mvn install
```

```
cd supplier-ws
mvn compile exec:java
```
 
```
cd supplier-ws-cli
mvn install
```

```
cd cc-ws-cli
mvn install
```

```
cd mediator-ws
mvn compile exec:java
``` 
 
```
cd mediator-ws-cli
mvn install -DskipTests
```


## Correr Aplicação

1. Abrir consola para Fornecedor 1
```
cd supplier-ws
mvn compile exec:java -Dws.i=1
```

2. Abrir consola para Fornecedor 2
```
cd supplier-ws
mvn compile exec:java -Dws.i=2
```

3. Abrir consola para Mediador
```
cd mediator-ws
mvn compile exec:java
```

4. Finalmente, na consola para o cliente do Mediador
```
cd mediator-ws-cli
mvn compile exec:java
```
O resultado final do ping deverá ser impresso nesta consola.

## Correr testes de integração
```
cd supplier-ws
mvn compile exec:java -Dws.i=1
```

```
cd supplier-ws
mvn compile exec:java -Dws.i=2
```

```
cd mediator-ws
mvn compile exec:java
```

```
cd mediator-ws-cli
mvn verify
```

**FIM**
