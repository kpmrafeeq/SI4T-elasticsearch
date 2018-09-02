package org.si4t.elastic;

/**
 * ElasticSearchClientRequest.
 * 
 * @author Velmurugan Arjunan
 */
public class ElasticSearchClientRequest {

	private String endpointUrl;

	private String indexName;

	private String indexType;

	public ElasticSearchClientRequest(String endpointUrl, String indexName, String indexType)
	{
		this.endpointUrl = endpointUrl;
		this.indexName = indexName;
		this.indexType = indexType;
	}

	public String getEndpointUrl() {
		return endpointUrl;
	}
	public String getIndexName() {
		return indexName;
	}
	public String getIndexType() {
		return indexType;
	}
}
