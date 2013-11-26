Overview
================

Implementation for XAP mongodb persistency SpaceDataSource and SpaceSynchronizationEndpoint

Prerequisites
=============

* download [XAP last version](http://www.gigaspaces.com/xap-download) and follow the [installation instructions](http://wiki.gigaspaces.com/wiki/display/XAP97/Installation)
* download [MongoDB](http://www.mongodb.org/downloads) and follow the [installation instructions](http://docs.mongodb.org/manual/installation/)
* download [Maven 3.0.5 or later](http://maven.apache.org/download.cgi) and follow the [installation instructions](http://maven.apache.org/download.cgi#Installation) at the end of document

Configure Environment Variable
==============================

* create environment variable GS_HOME -> path/to/[XAP installation directory]
* add GS_HOME\bin to PATH environment variable
* create environment variable MONGO_HOME -> path/to/[mongo installtion directory]
* add MONGO_HOME\bin to PATH environment variable
* add M2_HOME\bin to PATH environment variable


Build
=====

* clone the project git clone
* navigate to mongo-datasource project directory
* build project without testing `mvn clean install`
* build project with running unit tests and system test 
	`mvn clean surefire:test install`
* run integration and system tests `mvn integration-test` or `mvn verify` 

> ##### Notes #####
> * its recommended that you run `mvn clean install` and then running the testing phases separately `mvn surefire:test integration-test` 
  because integration and system test can take long time
> * eclipse users uses m2e plugin sometimes miss synchronization its recommended to right click on 
  mongodb-datasource project and from the menu [Maven]-> [Update project]


Repositories
============
	<repositories>
		<repository>
			<id>org.openspaces</id>
			<name>OpenSpaces</name>
			<url>http://maven-repository.openspaces.org</url>
		</repository>

		<repository>
			<releases>
				<enabled>true</enabled>
				<updatePolicy>always</updatePolicy>
				<checksumPolicy>warn</checksumPolicy>
			</releases>
			<id>allanbank</id>
			<name>Allanbank Releases</name>
			<url>http://www.allanbank.com/repo/</url>
			<layout>default</layout>
		</repository>
	</repositories>

Dependencies
============
    	<dependency>
			<groupId>com.gigaspaces</groupId>
			<artifactId>gs-openspaces</artifactId>
			<version>9.7.0-SNAPSHOT</version>
		</dependency>

		<!-- mongodb java driver -->
		<dependency>
			<groupId>org.mongodb</groupId>
			<artifactId>mongo-java-driver</artifactId>
			<version>2.11.2</version>
		</dependency>
		<dependency>
			<groupId>com.allanbank</groupId>
			<artifactId>mongodb-async-driver</artifactId>
			<version>1.2.3</version>
		</dependency>

		<dependency>
			<groupId>org.antlr</groupId>
			<artifactId>antlr4-runtime</artifactId>
			<version>4.0</version>
		</dependency>

