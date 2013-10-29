@ECHO OFF
@mvn -f itest-model\pom.xml install


@mvn -f mongodb-qa-mirror\pom.xml install

@mvn -f mongodb-qa-load\pom.xml package