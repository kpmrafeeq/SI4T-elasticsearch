Elastic
=======

Elastic Search Indexer implementation for use with <a href="http://si4t.github.io">SI4T</a>.


Checkout the <a href="https://www.elastic.co/guide/en/elasticsearch/reference/current/getting-started.html">Elastic website</a> for more information.  

#Tridion 2013sp1+ Notes
- Tridion 2013sp1+ needs following Tridion dependencies:

- cd_model
- cd_core

#Web 8 Notes
 
- Web 8 needs to add two additional Tridion dependencies in the deployer config:

- cd_common_config_legacy
- cd_common_util

- The Elastic Version used 6.2.2 Read: https://www.elastic.co/guide/en/elasticsearch/reference/6.2/release-notes-6.2.2.html

- elastic dependencies are:

The High Level Java REST Client depends on the following artifacts and their transitive dependencies:

- org.elasticsearch.client:elasticsearch-rest-client
- org.elasticsearch:elasticsearch
