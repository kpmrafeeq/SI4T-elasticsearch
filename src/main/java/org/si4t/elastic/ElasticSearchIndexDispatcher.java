package org.si4t.elastic;

import com.tridion.storage.si4t.BinaryIndexData;
import com.tridion.storage.si4t.Utils;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;

import com.google.gson.Gson;
import org.apache.http.HttpHost;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.net.*;


/**
 * ElasticSearchIndexDispatcher.
 * 
 * Singleton. Dispatches updates to the Elastic Index.
 * 
 * @author Velmurugan Arjunan
 */
public enum ElasticSearchIndexDispatcher
{
	INSTANCE;

	private static final Logger log = LoggerFactory.getLogger(ElasticSearchIndexDispatcher.class);
	private static ConcurrentHashMap<String, RestHighLevelClient> _httpClients = new ConcurrentHashMap<>();

	private RestHighLevelClient getElasticSearchRestClient(ElasticSearchClientRequest clientRequest)
	{
		if (_httpClients.get(clientRequest.getEndpointUrl()) == null)
		{
			log.info("Obtaining Elastic Search Client [" + clientRequest.getEndpointUrl() + ": " + clientRequest.getEndpointUrl());
				this.createElasticSearchRestClient(clientRequest.getEndpointUrl(),clientRequest.getUserName(),clientRequest.getPassword());
		}
		return _httpClients.get(clientRequest.getEndpointUrl());
	}

	private void createElasticSearchRestClient(String endpointUrl, String user, String password)
	{
		RestHighLevelClient restClient = null;
		try {
			URL url = new URL(endpointUrl);

			RestClientBuilder builder = RestClient.builder(new HttpHost(url.getHost(), url.getPort(),url.getProtocol()))
					.setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
						@Override
						public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
							if (!Utils.StringIsNullOrEmpty(user) && !Utils.StringIsNullOrEmpty(password)) {
								CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
								credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(user, password));
								return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
							}
							else {
								return httpClientBuilder;
							}
						}
					});

			restClient = new RestHighLevelClient(builder);

		} catch (Exception e) {
			throw new ElasticsearchException("Could not create RestService instance",e);
		}

		_httpClients.put(endpointUrl, restClient);
	}

	public String addDocuments(DispatcherPackage dispatcherPackage) throws ParserConfigurationException, IOException, SAXException, ElasticsearchException
	{
		ElasticSearchClientRequest clientRequest = dispatcherPackage.getClientRequest();
		RestHighLevelClient client = this.getElasticSearchRestClient(clientRequest);
		if (client == null)
		{
			throw new ElasticsearchException("Elastic Client not Instantiated");
		}

		DocumentBatch documentBatch = dispatcherPackage.getDocumentBatch();
		if (documentBatch == null)
		{
			throw new NullPointerException("Document batch is null");
		}

		BulkRequest request = new BulkRequest();

		ArrayList<DocumentData> documents = documentBatch.getItems();

		for (DocumentData documentData : documents)
		{
			log.info("Adding " + documentData.getId() + " document to the elastic search Indexer");

			request.add(new IndexRequest(clientRequest.getIndexName(), clientRequest.getIndexType(), documentData.getId())
					.source(new Gson().toJson(documentData.getFields()),XContentType.JSON));
		}

		BulkResponse bulkResponse = client.bulk(request);
		RestStatus status = bulkResponse.status();

		return ("Adding " + documents.size() + " document(s) had the following response: " + status.name());
	}

	public String addBinaries(Map<String, BinaryIndexData> binaryAdds, ElasticSearchClientRequest clientRequest) throws IOException, ParserConfigurationException, SAXException
	{
		//TODO:NOT IMPLEMENTED
		return "";
	}

	public String removeFromElasticSearch(DispatcherPackage dispatcherPackage) throws ParserConfigurationException, IOException, SAXException
	{
		ElasticSearchClientRequest clientRequest = dispatcherPackage.getClientRequest();
		RestHighLevelClient client = this.getElasticSearchRestClient(clientRequest);
		if (client == null)
		{
			throw new ElasticsearchException("Elastic Client not Instantiated");
		}

		DocumentBatch documentBatch = dispatcherPackage.getDocumentBatch();
		if (documentBatch == null)
		{
			throw new NullPointerException("Document batch is null");
		}

		BulkRequest request = new BulkRequest();

		ArrayList<DocumentData> documents = documentBatch.getItems();

		for (DocumentData documentData : documents)
		{
			log.info("Removing " + documentData.getId() + " document from the elastic search Indexer");

			request.add(new DeleteRequest(clientRequest.getIndexName(), clientRequest.getIndexType(), documentData.getId()));
		}

		BulkResponse bulkResponse = client.bulk(request);

		RestStatus status = bulkResponse.status();

		return ("Deleting " + documents.size() + " document(s) had the following response: " + status.name());
	}


	public void destroyServers()
	{
		try
		{
			for (Entry<String, RestHighLevelClient> clients : _httpClients.entrySet())
			{
				RestHighLevelClient client = clients.getValue();
				if (client != null)
				{
					log.info("Closing down RestHighLevelClient for url: " + clients.getKey());
					client.close();
				}
			}
		}
		catch (IOException e)
		{
			logException(e);
		}

	}

	private void logException(Exception e)
	{
		log.error(e.getMessage());
		log.error(Utils.stacktraceToString(e.getStackTrace()));
	}
}
