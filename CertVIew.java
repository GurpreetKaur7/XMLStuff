




import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

public class CertVIew {
	
	/* create master xml file
	 */
	public static void mergeXml(Document doc1, Document doc2) throws Exception 
	{
		DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document outputDoc = db.newDocument();
		Element rootElement = outputDoc.createElement("root");
		outputDoc.appendChild(rootElement);
		Element e1 = (Element) doc1.getFirstChild();
	    Element e2 = (Element) doc2.getFirstChild();
		
	    Node imported = outputDoc.importNode(e1, true);
	    rootElement.appendChild(imported);
	    
	     imported = outputDoc.importNode(e2, true);
	     rootElement.appendChild(imported);
	    
	    TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(outputDoc);
		StreamResult result = new StreamResult(new File(LoadProperties.getProp("masterfilepath")));
 
		transformer.transform(source, result);
	}
	
	/* https url hit code 
	 * before this you have to manually download certificate and add to key store
	 * using below two commands
	 *   			keytool -import -file E:\https\appvigil.co.crt -alias firstCA -keystore cacert.keystore
	 * 				keytool -import -file E:\https\appvigil.co.crt -alias firstCA -keystore myTrustStore
	 * 
	 * This could also be automated through java code : but could not implement it currently
	 * */
public static String readXml() throws Exception 
{
	 String inputLine = "";
	String u = LoadProperties.getProp("keystorepath");
	String t = LoadProperties.getProp("truststorepath");
	System.setProperty("javax.net.ssl.trustStore",t);
	System.setProperty( "sun.security.ssl.allowUnsafeRenegotiation", "true" );
	XTrustProvider.install();
	AuthSSLProtocolSocketFactory aspsf = new AuthSSLProtocolSocketFactory(u, "changeit", t, "changeit");
	SSLSocketFactory sf = aspsf.getSSLContext().getSocketFactory();
	
	HttpsURLConnection urlc=null;
	String chargingServiceURL=LoadProperties.getProp("url");
	URL url = new URL(chargingServiceURL);
	urlc = (HttpsURLConnection) url.openConnection();

	urlc.setSSLSocketFactory(sf);
	String encoding = null;
	
	urlc.setHostnameVerifier(new HostnameVerifier() {
		
		public boolean verify(String hostname, SSLSession session) {
			System.out.println("Warning: URL Host: "+hostname+" and  "+session.getPeerHost());
			return true;
		}
	});
	urlc.setRequestProperty( "Authorization", "Basic " + encoding );
	urlc.setRequestMethod("POST");
	urlc.setUseCaches(false);
	urlc.setRequestProperty("SOAPAction","");
	urlc.setRequestProperty("Content-Type","text/xml; charset=utf-8");
	urlc.setDoOutput(true);
    urlc.setDoInput(true);

   // System.out.println("reading response -------------");
	 try{
        InputStreamReader isr = new InputStreamReader(urlc.getInputStream());
        BufferedReader in = new BufferedReader(isr);
       
        String line;
        while ((line = in.readLine()) != null){
        	inputLine += line;
        }
   //     System.out.println(inputLine);
        in.close();
    }
    catch(Exception e){
    	e.printStackTrace();
    	System.out.println("Execption raised during parsing response :::" + e.getMessage());
    }
	 return inputLine;
}
	
	public static void init() throws Exception 
	{
		DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		String response = readXml();
		
		Document doc1 = (Document) db.parse(new InputSource(new StringReader(response)));
		Document doc2 = (Document) db.parse(new File(LoadProperties.getProp("localxmlpath")));
		mergeXml(doc1, doc2);
		
	}
	
	
}
