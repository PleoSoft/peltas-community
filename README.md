

Peltas is an application that gives you BI insights on your existing Alfresco (audit applications or workspace repository data) data by extracting raw data from it and storing them in target system, like a relational database. 
It is built on open source technologies such as Spring Boot, Spring Batch and Spring Integrations and can easily be extended for custom requirements or configured with simple configuration files. As a ready to run application, 
Peltas provides immediate results, while enabling teams or companies to use any BI tool they are familiar with. It is simple and easy to setup and does not require a lot of specific tooling knowledge. 
 
Unlike ETL tools, where data transformation takes a long time, Peltas keeps traces of its records and knows exactly where it should re-start from. With its powerful evaluator mechanism, 
Peltas allows you to choose which data should be processed and stored in a target system or database. It supports the old audit rest APIs and the new V1 rest APIs, and is completely integrated with Alfresco, covering both community and enterprise installations.

# Supported Alfresco versions
	- should work with 5.2+
	- if a prior version to 5.2 is required it owuld not take much effort to change the way to get an Alfresco ticket
	
# How does it work?
Peltas reads Alfresco nodes data via the Alfresco existing REST APIs and maps them to readable values into a database. Although the database writer can be swapped with a different storage implementation (i.e Elastic Search) the community version comes with a database storage only. 

No additional amps/jars or Alfresco customization is needed to run Peltas since it is an independant Spring Boot application.

Since Peltas was firstly built for Alfresco Audit logs data, the core engine is highly inspired by the data format of an audit log entry and therfore even the live workspace nodes are converted into a similar audit format while being processed.

Peltas also knows where to restart from, what was the last node processed is kept in the DB table named: peltas_timestamp.
It is important to understand that such data processing cannot be "parallelized" and therfore clustering is not os any help in speeding up the data processing. Peltas has a scheduler and it will run in a fixed delay way that can be configured by setting the property to the value that fits your setup
- peltas.scheduler.fixedDelay=5000
	
Workspace nodes data
--
Nodes data are read from the Alfresco SOLR API. Just like the Alfresco search services do the indexing part, exactly the same services are used by Peltas and therfore no data is missed and everything is transactionally written the to DB storage.

Alfresco Audit data
--
 As of today this is not supported in the opensource community version of Peltas, but we are planning to add that to the community version too.
	
Cherry picking Alfresco nodes
--
Peltas implements an evaluator engine, where each Alfresco node can be tested in order to be processed by Peltas or not taken into consideration, this is done by configuring an evaluator. An evaluator could be configured with node content type, action type or aspects/metadata and all of thoe could be combined

	peltas.handler.documentupdated.evaluator=/alfresco-workspace/transaction/action=NODE-UPDATED|/alfresco-workspace/transaction/type=cm:content 

The next step is to configure the node metadata mapping and do the data conversions if necessary

	peltas.handler.documentupdated.mapper.property.action.data=/alfresco-workspace/transaction/action

More information can be seen in the conifguration file at src/main/resources/io/peltas/peltas.properties and any of those could be overriden in Spring application.properties if required.

# Run Locally
	- git checkout	
	- setup up your DB connection in application.properties (PostgreSQL is supported) and each table is
	  prefixed with peltas_
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
		
	if your Alfresco SOLR APIs installation is protected by SSL, check these properties
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
	- create docker image locally: mvn clean package dockerfile:build -Pdocker
	- or pull from docker hub: docker pull docker pull pleosoft/peltas-community
	- since you need the Alfresco platform the easiest way is to checkout the acs-deplyoment and at the end of the file add
	  (take care of the allignment and do not use tabs)
		
        peltas:
            image: pleosoft/peltas-community:2.0.0-RELEASE
            mem_limit: 128m
		

# BI tools
Any kind of BI tools with Database connectors can be used. For demos Power BI is quite convenient however it does not support PostgreSQL out of the box and you need a connector such as [https://github.com/npgsql/Npgsql/releases](https://github.com/npgsql/Npgsql/releases "https://github.com/npgsql/npgsql/releases") or follow this simple turtorial [https://community.powerbi.com/t5/Community-Blog/Configuring-Power-BI-Connectivity-to-PostgreSQL-Database/ba-p/12567](https://community.powerbi.com/t5/Community-Blog/Configuring-Power-BI-Connectivity-to-PostgreSQL-Database/ba-p/12567)
	
# Change the DB schema
	- Peltas comes with a predefined DB schema and executions scripts (src/main/resources/io/peltas)
	- you can change them and adapt to your specific schema requirements

# Custom Namespaces
A custom Alfresco namespace is not automatically updated in Peltas Community and therfore you have to do an insert of your custom in the "peltas_model_dim" table:

* INSERT INTO peltas_model_dim(shortname, longname, modified) VALUES ('your_shortname','{your_localname}',NOW());

cm:content as example:
* INSERT INTO peltas_model_dim(shortname, longname, modified) VALUES ('cm','{http://www.alfresco.org/model/content/1.0}',NOW());
	
Peltas will not stop working if the namespace does not exist, but it will not go further on any nodes and it will continue retrying untill you setup your custom namespace.
	
# Peltas Executions
	- TODO	
