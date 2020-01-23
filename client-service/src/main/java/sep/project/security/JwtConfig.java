package sep.project.security;

import org.springframework.beans.factory.annotation.Value;

public class JwtConfig {

	// Spring doesn't inject/autowire to "static" fields. 
	// Link: https://stackoverflow.com/a/6897406
	@Value("${security.jwt.uri:/cilent/**}")
    private String Uri;

    @Value("${security.jwt.header:Authorization}")
    private String header;

    @Value("${security.jwt.prefix:Bearer }")
    private String prefix;

    @Value("${security.jwt.expiration:#{24*60*60}}")
    private int expiration;

    @Value("${security.jwt.secret:JwtSecretKey}")
    private String secret;
    
    // In case you want to use plain getters instead of lombok.
	public String getUri() {
		return Uri;
	}

	public String getHeader() {
		return header;
	}

	public String getPrefix() {
		return prefix;
	}

	public int getExpiration() {
		return expiration;
	}

	public String getSecret() {
		return secret;
	}

	public void setUri(String uri) {
		Uri = uri;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public void setExpiration(int expiration) {
		this.expiration = expiration;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}
    
}
