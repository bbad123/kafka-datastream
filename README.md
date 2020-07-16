# cep-controller

cep-controller
 - kafka-producer
 - rules select / insert / delete
 - generated producer (start/stop)
 - morules database 연동 (rule_info / trigger_items)


test examples: 
swagger url : 
http://localhost:5566/swagger-ui.html
{host-url}/swagger-ui.html

api sample:

{
  "aggregateFieldName": "value",
  "alertState": "CRITICAL",
  "controlType": "EXPORT_RULES_CURRENT",
  "descrion": "test CEP",
  "interval": 10,
  "keyNames": "eventKey",
  "ruleState": "ACTIVE",
  "triggerItems": [
    {
      "eventKey": "cpu usage",
      "expression": "{value} >= 85 && {value} < 100"
    }
  ]
}

kafka 구동관련 
linux 
1. zookeeper 서버시작
./bin/zookeeper-server-start.sh config/zookeeper.properties
2. kafka 서버시작
./bin/kafka-server-start.sh config/server.properties
3. topic 만들기
./bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic livetransactions
4. 만들어진 topic 확인
./bin/kafka-topics.sh --list --bootstrap-server localhost:9092
5. 메세지 보내기
./bin/kafka-console-producer.sh --bootstrap-server localhost:9092 --topic test
6. 소비자 시작
./bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic first-test --from-beginning

windows
1. zookeeper 서버시작
.\zookeeper-server-start.bat ..\..\config\zookeeper.properties
2. kafka 서버시작
.\kafka-server-start.bat ..\..\config\server.properties
3. topic 만들기
.\kafka-topics.bat --create --zookeeper localhost:2181 --replication-factor 1 --partitions 5 --topic first-test
4. 만들어진 topic 확인
.\kafka-topics.bat --list --bootstrap-server localhost:9092
5. 메세지 보내기
.\kafka-console-producer.bat --bootstrap-server localhost:9092  --topic livetransactions
6. 메세지 소비
.\kafka-console-consumer.bat --bootstrap-server localhost:9092  --topic rulesMo
7. topic 삭제
.\kafka-topics.bat --delete --zookeeper localhost:2181 --topic test

linux - confluent-kafka
1. topic 생성
kafka-topics --create  --zookeeper 172.16.11.194:2181 --topic livetransactions--replication-factor 1 --partitions 3
2. 메세지 보내기
kafka-console-producer --broker-list dev-flink:9092 --topic koomin1 
3. 메세지 소비
kafka-console-consumer --bootstrap-server dev-flink:9092 --topic alert


* 구동전 kafka의 topic 생성 필수 kafka와의 연결이 되지 않으면 구동되지 않음.
현재 생성되어 있는 topic
alerts
current-rules
latency
livetransactions
rulesMo
토픽은 변경될 수 있음.

