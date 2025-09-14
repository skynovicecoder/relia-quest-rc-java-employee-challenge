package com.reliaquest.api.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

class RestClientConfigTest {

    private RestClientConfig config;
    private RestClient.Builder builderMock;
    private RestClient restClientMock;

    @BeforeEach
    void setUp() {
        config = new RestClientConfig();
        config.empServerURL = "http://dummy-server";

        // Mock RestClient.Builder and RestClient
        builderMock = mock(RestClient.Builder.class);
        restClientMock = mock(RestClient.class);

        when(builderMock.baseUrl(anyString())).thenReturn(builderMock);
        when(builderMock.build()).thenReturn(restClientMock);
    }

    @Test
    void restClient_ShouldReturnNonNullInstance() {
        RestClient client = config.restClient(builderMock);

        assertThat(client).isNotNull();
        assertThat(client).isEqualTo(restClientMock);
    }

    @Test
    void restClient_ShouldCallBaseUrlWithConfiguredValue() {
        config.restClient(builderMock);

        verify(builderMock).baseUrl("http://dummy-server");
        verify(builderMock).build();
    }

    @Test
    void restClient_ShouldUseUpdatedUrl() {
        config.empServerURL = "http://another-server/api";

        config.restClient(builderMock);

        verify(builderMock).baseUrl("http://another-server/api");
        verify(builderMock).build();
    }

    @Test
    void restClient_ShouldBuildNewInstanceEachTime() {
        RestClient client1 = config.restClient(builderMock);
        RestClient client2 = config.restClient(builderMock);

        assertThat(client1).isEqualTo(restClientMock);
        assertThat(client2).isEqualTo(restClientMock);
        verify(builderMock, times(2)).build();
    }
}
