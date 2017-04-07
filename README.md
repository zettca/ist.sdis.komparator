# Projeto de Sistemas Distribuídos 2016-2017 #

Grupo T50

Número | Nome | Email
 --- | --- | ---
77917	| Daniel José Nazaré Madruga | daniel.madruga@tecnico.ulisboa.pt
78013	| Bruno Miguel Silva Henriques | bruno.s.henriques@tecnico.ulisboa.pt
78042	| Bruno Miguel Nascimento Carola | bruno.carola@tecnico.ulisboa.pt

-------------------------------------------------------------------------------

## Instruções de instalação
 
 
### Ambiente
 
[0] Iniciar sistema operativo
 
Linux
 
 
[1] Servidor de nomes a utilizar é o jUDDI alojado na RNL
     
     criar um ficheiro settings.xml 
     (fazer download) disciplinas.tecnico.ulisboa.pt/leic-sod/2016-2017/labs/06-ws2/settings.xml
     na pasta .m2 da home do utilizador
     substituir user por t50
     substituir pass por WkyodoJT
     
[2] Criar pasta temporária
 
```
mkdir projeto
cd projeto
```
 
 
[3] Obter código fonte do projeto (versão entregue)
 
```
git clone -b SD_P2 https://github.com/tecnico-distsys/T_50-project.git
``` 
 
[4] Instalar módulos de bibliotecas auxiliares e alguns clientes com dependências
 
```
cd uddi-naming
mvn clean install
```
 
```
cd supplier-ws
mvn clean install
```
 
```
cd supplier-ws-cli
mvn clean install
```
```
cd cc-ws-cli
mvn clean install
```

```
cd mediator-ws
mvn clean install
``` 
 
```
cd mediator-ws-cli
mvn clean install -DskipTests
```


Correr Aplicação

   Abrir consola para Fornecedor 1:
       cd supplier-ws
       mvn exec:java
   Abrir consola para Fornecedor 2:
       cd supplier-ws
       mvn exec:java -Dws.i=2
   Abrir consola para Mediador:
       cd mediator-ws
       mvn exec:java
   Finalmente, na consola para o cliente do Mediador:
       cd mediator-ws-cli
       mvn exec:java
       O resultado final do ping deverá ser impresso nesta consola.

Correr testes de integração 
       cd mediator-ws-cli
       mvn verify

**FIM**
