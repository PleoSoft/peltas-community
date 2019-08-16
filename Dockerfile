FROM azul/zulu-openjdk-debian:11

ARG JAR_FILE
ADD target/${JAR_FILE} peltas-community.jar

ENV SPRING_APPLICATION_JSON {\"spring\":{\"datasource\":{\"url\":\"jdbc:postgresql://postgres:5432/alfresco\", \"username\":\"alfresco\", \"password\": \"alfresco\"}}, \"peltas\":{\"host\":\"http://alfresco:8080\", \"auth\":{\"basic\":{ \"username\":\"admin\", \"password\": \"admin\"}}}}
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/peltas-community.jar"]