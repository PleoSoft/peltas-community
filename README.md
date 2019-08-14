Peltas is an application that gives you BI insights on your existing Alfresco (audit applications or workspace repository data) data by extracting raw data from it and storing them in target system, like a relational database. 
It is built on open source technologies such as Spring Boot, Spring Batch and Spring Integrations and can easily be extended for custom requirements or configured with simple configuration files. As a ready to run application, 
Peltas provides immediate results, while enabling teams or companies to use any BI tool they are familiar with. It is simple and easy to setup and does not require a lot of specific tooling knowledge. 
 
Unlike ETL tools, where data transformation takes a long time, Peltas keeps traces of its records and knows exactly where it should re-start from. With its powerful evaluator mechanism, 
Peltas allows you to choose which data should be processed and stored in a target system or database. It supports the old audit rest APIs and the new V1 rest APIs, and is completely integrated with Alfresco, covering both community and enterprise installations.

# Supported Alfresco versions
	- should work with 5.2+
	- if a prior version to 5.2 is required it owuld not take much effort to change the way to get an Alfresco ticket

# Run Locally
	- git checkout	
	- setup up your DB connection in application.properties
		spring.datasource.driverClassName = org.postgresql.Driver
		spring.datasource.url = jdbc:postgresql://localhost:5432/peltas
		spring.datasource.username = peltas
		spring.datasource.password = peltas
		
		make sure the DB exists and the user has the rights to create tables
		
	- you will need Java 11 (although JDK 8 should work if [peltas-core](https://github.com/PleoSoft/peltas-core) is compiled with java 8)
	- start io.peltas.alfresco.workspace.AlfrescoWorkspaceApplication
	
	by default Peltas will connect to an Alfresco Repository (ACS) instance at localhost:8080. If you want to change it, update the data in the application.properties	
		peltas.host=http://localhost:8080
		peltas.authenticationtype=basicauth		
		peltas.auth.basic.username=admin
		peltas.auth.basic.password=admin
		
	if the root context of your Alfresco installation is not /alfresco make sure to update those properties too:
		peltas.loginUrl=alfresco/api/-default-/public/authentication/versions/1/tickets
		peltas.serviceUrl=alfresco/s/api/solr
		
	if your SOLR installation is protected by SSL, check these properties
		#x509 
		peltas.auth.x509.keyStore=/alfresco-community/solr4/workspace-SpacesStore/conf/ssl.repo.client.keystore
		peltas.auth.x509.keyStorePass=kT9X6oe68t
		peltas.auth.x509.keystoreType=JCEKS

		#ssl
		peltas.ssl.keystoreType=JCEKS
		peltas.ssl.trustStore=/alfresco-community/solr4/workspace-SpacesStore/conf/ssl.repo.client.truststore
		peltas.ssl.trustStorePass=kT9X6oe68t
		peltas.ssl.hostVerify=false

# Run with Docker
	- create docker image locally: mvn package -P docker
	- or pull from docker hub: docker pull docker pull pleosoft/peltas-community
	- start: docker run -p 8080:8080 pleosoft/peltas-community
	
# Change the DB schema
	- Peltas comes with a predefined DB schema and executions scripts (src/main/resources/io/peltas)
	- you can change them and adapt to your specific schema requirements
	
# Peltas Executions
	- TODO	
