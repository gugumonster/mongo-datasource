Overview
================

Implementation for XAP mongodb persistency SpaceDataSource and SpaceSynchronizationEndpoint

Prerequisites
=============

	* 	download [XAP last version](http://www.gigaspaces.com/xap-download) and follow the [installation instructions](http://wiki.gigaspaces.com/wiki/display/XAP97/Installation)
	* 	download [MongoDB](http://www.mongodb.org/downloads) and follow the [installation instructions](http://docs.mongodb.org/manual/installation/)
	* 	download [Maven 3.0.5 or later](http://maven.apache.org/download.cgi) and follow the [installation instructions](http://maven.apache.org/download.cgi#Installation) at the end of document

Configure Environment Variable
==============================

	* 	create environment variable GS_HOME -> path/to/[XAP installation directory]
	* 	add GS_HOME\bin to PATH environment variable
	* 	create environment variable MONGO_HOME -> path/to/[mongo installtion directory]
	* 	add MONGO_HOME\bin to PATH environment variable
	* 	add M2_HOME\bin to PATH environment variable


Build
=====

	* 	clone the project git clone
	*	navigate to mongo-datasource project directory
	*	build project mvn clean surefire:test integration-test package