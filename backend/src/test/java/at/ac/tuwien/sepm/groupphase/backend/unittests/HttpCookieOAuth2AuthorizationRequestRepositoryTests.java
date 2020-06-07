package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.config.security.HttpCookieOAuth2AuthorizationRequestRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;

import javax.servlet.http.Cookie;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests for {@link HttpCookieOAuth2AuthorizationRequestRepository}.
 */
@RunWith(MockitoJUnitRunner.class)
public class HttpCookieOAuth2AuthorizationRequestRepositoryTests {
    private HttpCookieOAuth2AuthorizationRequestRepository authorizationRequestRepository =
        new HttpCookieOAuth2AuthorizationRequestRepository();

    @Test(expected = IllegalArgumentException.class)
    public void loadAuthorizationRequestWhenHttpServletRequestIsNullThenThrowIllegalArgumentException() {
        this.authorizationRequestRepository.loadAuthorizationRequest(null);
    }

    @Test
    public void loadAuthorizationRequestWhenNotSavedThenReturnNull() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter(OAuth2ParameterNames.STATE, "state-1234");
        OAuth2AuthorizationRequest authorizationRequest =
            this.authorizationRequestRepository.loadAuthorizationRequest(request);

        assertThat(authorizationRequest).isNull();
    }

    @Test
    public void loadAuthorizationRequestWhenSavedAndStateParameterNullThenReturnNull() {
        MockHttpServletRequest request = new MockHttpServletRequest();

        OAuth2AuthorizationRequest authorizationRequest = createAuthorizationRequest().build();
        this.authorizationRequestRepository.saveAuthorizationRequest(
            authorizationRequest, request, new MockHttpServletResponse());

        assertThat(this.authorizationRequestRepository.loadAuthorizationRequest(request)).isNull();
    }

    @Test
    public void saveAuthorizationRequestWhenHttpServletRequestIsNullThenThrowIllegalArgumentException() {
        OAuth2AuthorizationRequest authorizationRequest = createAuthorizationRequest().build();

        assertThatThrownBy(() -> this.authorizationRequestRepository.saveAuthorizationRequest(
            authorizationRequest, null, new MockHttpServletResponse()))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void saveAuthorizationRequestWhenHttpServletResponseIsNullThenThrowIllegalArgumentException() {
        OAuth2AuthorizationRequest authorizationRequest = createAuthorizationRequest().build();

        assertThatThrownBy(() -> this.authorizationRequestRepository.saveAuthorizationRequest(
            authorizationRequest, new MockHttpServletRequest(), null))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void saveAuthorizationRequestWhenNotNullThenSaved() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        OAuth2AuthorizationRequest authorizationRequest = createAuthorizationRequest().build();
        this.authorizationRequestRepository.saveAuthorizationRequest(
            authorizationRequest, request, response);

        request.setCookies(response.getCookies());

        OAuth2AuthorizationRequest loadedAuthorizationRequest =
            this.authorizationRequestRepository.loadAuthorizationRequest(request);

        assertThat(loadedAuthorizationRequest.getAttributes()).isEqualTo(authorizationRequest.getAttributes());
        assertThat(loadedAuthorizationRequest.getAdditionalParameters()).isEqualTo(authorizationRequest.getAdditionalParameters());
        assertThat(loadedAuthorizationRequest.getAuthorizationRequestUri()).isEqualTo(authorizationRequest.getAuthorizationRequestUri());
        assertThat(loadedAuthorizationRequest.getAuthorizationUri()).isEqualTo(authorizationRequest.getAuthorizationUri());
        assertThat(loadedAuthorizationRequest.getGrantType()).isEqualTo(authorizationRequest.getGrantType());
        assertThat(loadedAuthorizationRequest.getRedirectUri()).isEqualTo(authorizationRequest.getRedirectUri());
        assertThat(loadedAuthorizationRequest.getScopes()).isEqualTo(authorizationRequest.getScopes());
        assertThat(loadedAuthorizationRequest.getState()).isEqualTo(authorizationRequest.getState());
        assertThat(loadedAuthorizationRequest.getClientId()).isEqualTo(authorizationRequest.getClientId());
    }

    @Test
    public void saveAuthorizationRequestWhenRequestInsecureThenCookiesInsecure(){
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.setSecure(false);

        OAuth2AuthorizationRequest authorizationRequest = createAuthorizationRequest().build();
        this.authorizationRequestRepository.saveAuthorizationRequest(
            authorizationRequest, request, response);
        for (Cookie cookie: response.getCookies()){
            assertThat(!cookie.getSecure());
        }
    }

    @Test
    public void saveAuthorizationRequestWhenRequestSecureThenCookiesSecure(){
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.setSecure(true);

        OAuth2AuthorizationRequest authorizationRequest = createAuthorizationRequest().build();
        this.authorizationRequestRepository.saveAuthorizationRequest(
            authorizationRequest, request, response);

        assertThat(response.getCookies().length).isEqualTo(1);
        assertThat(response.getCookies()[0].getSecure());
    }

    @Test
    public void saveAuthorizationRequestWhenNullThenCookiesExpired() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        this.authorizationRequestRepository.saveAuthorizationRequest(
            null, request, response);

        assertThat(response.getCookies().length).isEqualTo(1);
        assertThat(response.getCookies()[0].getMaxAge()).isEqualTo(0);
    }

    @Test
    public void removeAuthorizationRequestWhenNotNullThenCookiesExpired() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        this.authorizationRequestRepository.removeAuthorizationRequest(request, response);

        assertThat(response.getCookies().length).isEqualTo(1);
        assertThat(response.getCookies()[0].getMaxAge()).isEqualTo(0);
    }

    @Test
    public void removeAuthorizationRequestWhenHttpServletRequestIsNullThenThrowIllegalArgumentException() {
        assertThatThrownBy(() -> this.authorizationRequestRepository.removeAuthorizationRequest(
            null, new MockHttpServletResponse())).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void removeAuthorizationRequestWhenHttpServletResponseIsNullThenThrowIllegalArgumentException() {
        assertThatThrownBy(() -> this.authorizationRequestRepository.removeAuthorizationRequest(
            new MockHttpServletRequest(), null)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void removeAuthorizationRequestWhenNotSavedThenNotRemoved() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter(OAuth2ParameterNames.STATE, "state-1234");

        MockHttpServletResponse response = new MockHttpServletResponse();

        OAuth2AuthorizationRequest removedAuthorizationRequest =
            this.authorizationRequestRepository.removeAuthorizationRequest(request, response);

        assertThat(removedAuthorizationRequest).isNull();
    }

    private OAuth2AuthorizationRequest.Builder createAuthorizationRequest() {
        return OAuth2AuthorizationRequest.authorizationCode()
            .authorizationUri("https://example.com/oauth2/authorize")
            .authorizationRequestUri("http://example.com/example")
            .scope("one", "two", "three")
            .clientId("client-id-1234")
            .additionalParameters(Map.of("param1", "parameter", "param2", "bar"))
            .attributes(Map.of("param1", "test", "param2", "foo"))
            .state("state-1234");
    }
}
