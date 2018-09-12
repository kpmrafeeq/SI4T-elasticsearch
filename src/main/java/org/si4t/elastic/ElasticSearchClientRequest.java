package org.si4t.elastic;

/**
 * ElasticSearchClientRequest.
 * 
 * @author Velmurugan Arjunan
 */
public class ElasticSearchClientRequest {

	private String endpointUrl;

	private String user;

	private String password;

	private String indexName;

	private String indexType;

	public ElasticSearchClientRequest(String endpointUrl, String user, String password, String indexName, String indexType)
	{
		this.endpointUrl = endpointUrl;
		this.user = user;
		this.password = password;
		this.indexName = indexName;
		this.indexType = indexType;
	}

	public String getEndpointUrl() { return endpointUrl; }
	public String getUserName() { return user; }
	public String getPassword() { return password; }
	public String getIndexName() {
		return indexName;
	}
	public String getIndexType() {
		return indexType;
	}
}
