# Kafka

[TOC]



[Iniciando](#iniciando)

[Tópicos](#topics)

[Consumer Group](#consumer-group)

[Armazenamento de mensagens](#source-dir)

## Motivação



## <a name='iniciando'>Iniciando</a> 

Depois de instalar o Kafka vamos testar as funcionalidades para vermos se ele está funcionando tudo direitinho e já testarmos algumas funcionalidades.

Todos os comandos nesse documento são realizados no windows, existe uma diferença entre rodar no linux, aqui devemos adicionar o diretório windows após o diretório bin e ao invés de rodar arquivos .sh rodamos os arquivos com a extensão com .bat 

### Zookeeper

Em uma aba do terminal do windows rode o comando:

```bash
bin/windows/zookeeper-server-start.bat config/zookeeper.properties
```

Deixe essa aba aberta, o zookeeper ...

### Kafka

Agora vamos rodar o kafka, para isso abra uma nova aba do terminal e rode:

```bash
bin/windows/kafka-server-start.bat config/server.properties
```

O kafka vai utilizar o zookeeper ... 

No final do terminal deverá aparecer uma mensagem com o servidor e porta do kafka disponível.

### Criando um tópico

Agora em um novo terminal vamos criar um tópico:

```bash
bin/windows/kafka-topics.bat --create  --bootstrap-server localhost:9092 --replication-factor 1 partitions 1 --topic LOJA_NOVO_PEDIDO
```

### Listando tópicos criados

Para listar os tópicos em qualquer terminal podemos realizar o comando:

```bash
bin/windows/kafka-topics.bat --list --bootstrap-server localhost:9092
```

### Produtor de mensagem

Vamos criar um produtor de mensagem para isso vamos rodar:

```bash
bin/windows/Kafka-console-producer.bat --broker-list localhost:9092 --topic LOJA_NOVO_PEDIDO
```

Quando rodarmos o comando acima o terminal ira ficar esperando o input de mensagens, cada linha digitada e confirmada será uma nova mensagem.

```bash
>pedido0,11
>pedido1,13
>pedido2,17
```

### Consumidor de mensagem

Vamos consumir todas as mensagens criadas no tópico acima, para isso vamos utilizar o comando:

Caso queiramos olhar as mensagens a partir do momento que iniciarmos o consumidor podemos utilizar o seguinte comando:

```bash
bin/windows/kafka-console-consumer.bat --bootstrap-server localhost:9092 --topic LOJA_NOVO_PEDIDO
```

Note que nesse passo a passo, você não terá nada como resposta ao menos que crie uma nova mensagem.

Caso queiramos ver todas as mensagens já criadas e disponíveis no kafka basta rodarmos o comando:

```bash
bin/windows/kafka-console-consumer.bat --bootstrap-server localhost:9092 --topic LOJA_NOVO_PEDIDO --from-beginning
```

## <a name='topics'>Tópicos</a>

Segundo um artigo escrito por Ishwarya M para o site HEVO, ... **O Apache Kafka** possui uma unidade dedicada e fundamental para organização de Eventos ou Mensagens, chamada de **Tópicos** . Em outras palavras, Kafka Topics são Grupos Virtuais ou Logs que armazenam mensagens e eventos em uma **ordem lógica** , permitindo que os usuários enviem e recebam dados entre Kafka Servers com facilidade.

Quando um produtor envia mensagem ou um evento para um tópico específico, os tópicos irão armazenar os registros uma após a outra, criando assim um arquivo de log.

Para mais detalhes podemos dar uma olhadinha nesse link: https://hevodata.com/learn/kafka-topic/

## <a name='consumer-group'>Consumer Group</a> 

Um consumer group consegue ler dados em paralelo de um tópico Kafka, a vantagem de o usar é que digamos que estejamos trabalhando com uma organização de varejo, teremos um grande número de produtores, gerando um enorme quantidade de dados, um consumer group tem permissão de ler dados de apenas uma partição, resolvendo um possível problema de ler uma mensagem mais de uma vez caso utilizássemos mais de um consumer. 

Temos também algumas vantagens ao usar o Consumer Group, com vários consumidores lendo dados paralelamente conseguimos aumentar a taxa de consumo de dados. Com mais de um consumidor temos a garantia que por mais que tenhamos um consumidor apresentando falhas os outros conseguiram continuar consumindo os dados e o Kafka nos ajuda rebalanceando esses consumidores tanto se der erro em algum como se colocarmos um novo.

Para mais detalhes podemos dar uma olhadinha nesse link: https://www.educba.com/kafka-consumer-group/

### Analisando os grupos de consumo 

```bash
bin/windows/kafka-consumer-groups.bat --all-groups --bootstrap-server localhost:9092 --describe
```



### Partições

Caso desejarmos mais de um consumer, precisamos alterar o número de partitions configuradas para o topic, podemos realizar essa configuração de duas formas, diretamente no arquivo config/server.properties alterando seu valor padrão, ou alterando a partition diretamente do topic. Importante destacarmos que quando configuramos as alterações diretamente no arquivo default os tópicos já criados não são afetados.

O número de partições devem ser maior ou igual ao número de consumidores dentro de um grupo, se não um consumidor dentro do grupo fica parado por não ter uma partição atribuida.

#### Alterando partições diretamente no arquivo default:

```bash
nano config/server.properties
```

altere a linha que tem a propiedade 1 para 3:

```bash
#parallelism for cons...
#the brokers.
num.partitions = 3
```

#### Alterando as partições para um tópico específico:

```bash
bin/windows/kafka-topics.bat --alter --bootstrap-server localhost:9092 --topic <seu_topico> --partitions <numero_de_partitions_requeridas>
```

podemos verificar se deu tudo certo na alteração rodando:

```bash
bin/windows/kafka-topics.bat --bootstrap-server localhost:9092 --describe
```

### Chaves

Ela é peça fundamental para paralelizar o processamento de mensagens em um tópico dentro do mesmo consumer group.

A chave é usada para distribuir a mensagem entre as partições existentes e consequentemente entre as instâncias de um serviço dentro de um consumer group.

Quando criamos e enviamos um produtor de registro necessitamos que seja criado uma chave, essa chave consegue fazer o balanceamento entre quem vai ser a partição consumidora para a mensagem enviada.

### Rebalanceamento

Precisamos ajustar nosso tempo de commit, pois o Kafka pode se perder e ter que refazer todo o balanceamento caso não tenhamos bem configurado o tempo de realização de commits entre operações. O pool é um instante que acontece um commit, existem outros instantes que acontecem commits que veremos mais para a frente, mas no momento vamos utilizar o pool para podermos inserir commits em tempos menores e assim evitarmos que o rebalanceamento afete tanto o consumo de nossas mensagens.

## <a name='source-dir'>Armazenamento de mensagens</a>

Dentro do nosso SO as mensagens mantidas pelo zookeeper são armazenadas dentro de um diretório temporário, o que isso significa, em qualquer SO se armazenamos dados em diretórios temporários podemos perder esses dados. 

Existem duas maneiras para nos prevenirmos:

Dentro do diretório onde está instalado o Kafka podemos criar dois diretórios

```bash
mkdir data
mkdir data/zookeeper
mkdir data/kafka
```

agora devemos configurar o arquivo config/server.properties para utilizar os diretórios que criamos.

 ```bash
 vi config/server.properties
 /directory
 # A comma separated list of ...
 log.dirs=/nosso_caminho/data/kafka
 ```

```bash
vi confif/zookeeper.properties
/directory
# A comma separated list of ...
log.dirs=/nosso_caminho/data/zookeeper
```





