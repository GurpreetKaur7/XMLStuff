
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyStore;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

//import org.apache.commons.httpclient.protocol.SecureProtocolSocketFactory;

//import org.apache.commons.httpclient.protocol.SecureProtocolSocketFactory;

/*import org.apache.commons.httpclient.ConnectTimeoutException;
import org.apache.commons.httpclient.params.HttpConnectionParams;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.apache.commons.httpclient.protocol.SecureProtocolSocketFactory;*/

public class AuthSSLProtocolSocketFactory /*implements SecureProtocolSocketFactory, ProtocolSocketFactory*/ {

	private String keystoreUrl = null;
	private String keystorePassword = null;
	private String truststoreUrl = null;
	private String truststorePassword = null;
	private SSLContext sslcontext = null;

	public AuthSSLProtocolSocketFactory(String keystoreUrl, String keystorePassword, String truststoreUrl, String truststorePassword) {
		this.keystoreUrl = keystoreUrl;
		this.keystorePassword = keystorePassword;
		this.truststoreUrl = truststoreUrl;
		this.truststorePassword = truststorePassword;
	}


	/**
	 * @see org.apache.commons.httpclient.protocol.ProtocolSocketFactory#createSocket(java.lang.String, int, java.net.InetAddress, int, org.apache.commons.httpclient.params.HttpConnectionParams)
	 *//*
    public Socket createSocket(String host, int port, InetAddress localAddress, int localPort, HttpConnectionParams params) throws IOException, UnknownHostException, ConnectTimeoutException {
        int timeout = params.getConnectionTimeout();
        SocketFactory socketfactory;
        try {
            socketfactory = getSSLContext().getSocketFactory();
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
        if (timeout == 0) {
            return socketfactory.createSocket(host, port, localAddress, localPort);
        } else {
            Socket socket = socketfactory.createSocket();
            SocketAddress localaddr = new InetSocketAddress(localAddress, localPort);
            SocketAddress remoteaddr = new InetSocketAddress(host, port);
            socket.bind(localaddr);
            socket.connect(remoteaddr, timeout);
            return socket;
        }
    }*/

	/**
	 * @see SecureProtocolSocketFactory#createSocket(java.lang.String,int,java.net.InetAddress,int)
	 */
	public Socket createSocket(String host, int port, InetAddress clientHost, int clientPort) throws IOException, UnknownHostException {
		try {
			return getSSLContext().getSocketFactory().createSocket(host, port, clientHost, clientPort);
		} catch (Exception e) {
			throw new IOException(e.getMessage());
		}
	}

	/**
	 * @see SecureProtocolSocketFactory#createSocket(java.lang.String,int)
	 */
	public Socket createSocket(String host, int port) throws IOException, UnknownHostException {
		try {
			return getSSLContext().getSocketFactory().createSocket(host, port);
		} catch (Exception e) {
			throw new IOException(e.getMessage());
		}
	}

	/**
	 * @see SecureProtocolSocketFactory#createSocket(java.net.Socket,java.lang.String,int,boolean)
	 */
	public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException, UnknownHostException {
		try {
			return getSSLContext().getSocketFactory().createSocket(socket, host, port, autoClose);
		} catch (Exception e) {
			throw new IOException(e.getMessage());
		}
	}

	private static KeyStore createKeyStore(String url, String password) throws Exception {
		KeyStore keystore = KeyStore.getInstance("jks");
		InputStream is = null;
		try {
			is = new FileInputStream(new File(url));
			keystore.load(is, password != null ? password.toCharArray() : null);
			/*Enumeration enumObj = keystore.aliases();
			while(enumObj.hasMoreElements())
			{
				System.out.println("alias = "+enumObj.nextElement());
			}*/
			Logger.sysLog(LogValues.info, AuthSSLProtocolSocketFactory.class.getName(),"certificate "+keystore.getCertificate("bng"));
		} finally {
			if (is != null)
				is.close();
		}
		return keystore;
	}

	private static KeyManager[] createKeyManagers(KeyStore keystore, String password) throws Exception {
		KeyManagerFactory kmfactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
		kmfactory.init(keystore, password != null ? password.toCharArray() : null);
		return kmfactory.getKeyManagers();
	}

	private static TrustManager[] createTrustManagers(KeyStore keystore) throws Exception {
		TrustManagerFactory tmfactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		tmfactory.init(keystore);
		TrustManager[] trustmanagers = tmfactory.getTrustManagers();
		for (int i = 0; i < trustmanagers.length; i++) {
			if (trustmanagers[i] instanceof X509TrustManager) {
				trustmanagers[i] = new AuthSSLX509TrustManager((X509TrustManager) trustmanagers[i]);
			}
		}
		return trustmanagers;
	}

	private SSLContext createSSLContext() throws Exception {
		KeyManager[] keymanagers = null;
		TrustManager[] trustmanagers = null;
		if (this.keystoreUrl != null) {
			KeyStore keystore = createKeyStore(this.keystoreUrl, this.keystorePassword);
			keymanagers = createKeyManagers(keystore, this.keystorePassword);
		}
		if (this.truststoreUrl != null) {
			KeyStore keystore = createKeyStore(this.truststoreUrl, this.truststorePassword);
			trustmanagers = createTrustManagers(keystore);
		}
		SSLContext sslcontext = SSLContext.getInstance("SSL");
		sslcontext.init(keymanagers, trustmanagers, null);
		return sslcontext;
	}

	public SSLContext getSSLContext() throws Exception {
		if (this.sslcontext == null) {
			this.sslcontext = createSSLContext();
		}
		return this.sslcontext;
	}

}
