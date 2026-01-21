/*
* Copyright 2025 - 2025 the original author or authors.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* https://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.example.spring.ai.hateoas;

import java.util.Map;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;

/**
 * @author Christian Tzolov
 */
public class RestTools {

	private final RestClient restClient;

	public RestTools(RestClient restClient) {
		this.restClient = restClient;
	}

	public record HttpResponse(int statusCode, Object body) {
	}

	@Tool(description = "Sends a GET request to the specified URL and returns the response status code and body.",
			name = "get_request")
	public HttpResponse getRequest(@ToolParam(description = "The URL to send the GET request to") String url) {
		ResponseEntity<Object> response = this.restClient.get().uri(url).retrieve().toEntity(Object.class);
		return new HttpResponse(response.getStatusCode().value(), response.getBody());
	}

	@Tool(description = "Sends a POST request to the specified URL with the given body and returns the response status code and body.",
			name = "post_request")
	public HttpResponse postRequest(@ToolParam(description = "The URL to send the POST request to") String url,
			@ToolParam(description = "The request body as a map of key-value pairs") Map<String, Object> body) {

		ResponseEntity<Object> response = this.restClient.post()
			.uri(url)
			.contentType(MediaType.APPLICATION_JSON)
			.body(body)
			.retrieve()
			.toEntity(Object.class);

		return new HttpResponse(response.getStatusCode().value(), response.getBody());
	}

	@Tool(description = "Sends a PUT request to the specified URL with the given body and returns the response status code and body.",
			name = "put_request")
	public HttpResponse putRequest(@ToolParam(description = "The URL to send the PUT request to") String url,
			@ToolParam(description = "The request body as a map of key-value pairs") Map<String, Object> body) {

		ResponseEntity<Object> response = this.restClient.put()
			.uri(url)
			.contentType(MediaType.APPLICATION_JSON)
			.body(body)
			.retrieve()
			.toEntity(Object.class);
		return new HttpResponse(response.getStatusCode().value(), response.getBody());
	}

	@Tool(description = "Sends a DELETE request to the specified URL and returns the response status code and body.",
			name = "delete_request")
	public HttpResponse deleteRequest(@ToolParam(description = "The URL to send the DELETE request to") String url) {
		ResponseEntity<Object> response = this.restClient.delete().uri(url).retrieve().toEntity(Object.class);
		return new HttpResponse(response.getStatusCode().value(), response.getBody());
	}

}
