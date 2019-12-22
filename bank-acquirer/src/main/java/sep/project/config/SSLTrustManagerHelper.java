package sep.project.config;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SSLTrustManagerHelper {

    @Value("bank_acquirer_keyStore.jks")
    private String keyStore;

    @Value("password")
    private String keyStorePassword;

    @Value("bank_acquirer_trustedStore.jks")
    private String trustStore;

    @Value("password")
    private String trustStorePassword;

    @Bean
    @ConditionalOnProperty(name = "client.ssl.enabled")
    public SSLContext clientSSLContext() {
        try {
            TrustManagerFactory trustManagerFactory = getTrustManagerFactory(trustStore, trustStorePassword);

            KeyManagerFactory keyManagerFactory = getKeyManagerFactory(keyStore, keyStorePassword);

            return getSSLContext(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers());
        } catch (UnrecoverableKeyException | NoSuchAlgorithmException | CertificateException | KeyStoreException | IOException | KeyManagementException e) {
            throw new ClientException(e);
        }
    }


    private static SSLContext getSSLContext(KeyManager[] keyManagers, TrustManager[] trustManagers) throws NoSuchAlgorithmException, KeyManagementException {
        SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
        sslContext.init(keyManagers, trustManagers, null);
        return sslContext;
    }

    private static KeyManagerFactory getKeyManagerFactory(String keystorePath, String keystorePassword) throws NoSuchAlgorithmException, KeyStoreException, IOException, CertificateException, UnrecoverableKeyException {
        KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());

        keystore.load(SSLTrustManagerHelper.class.getClassLoader().getResourceAsStream(keystorePath), keystorePassword.toCharArray());

        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keystore, keystorePassword.toCharArray());
        return keyManagerFactory;
    }

    private static TrustManagerFactory getTrustManagerFactory(String truststorePath, String truststorePassword)
            throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException {
        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());

        trustStore.load(SSLTrustManagerHelper.class.getClassLoader().getResourceAsStream(truststorePath), truststorePassword.toCharArray());

        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(trustStore);
        return trustManagerFactory;
    }
}