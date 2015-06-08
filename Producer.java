

import java.io.File;
import java.util.concurrent.BlockingQueue;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Producer implements Runnable {

	private static BlockingQueue<Message> queue;
	private static String masterfilepath;
	public Producer(BlockingQueue<Message> q , String m){
		//System.out.println("const producer");
		queue=q;
		masterfilepath = m;
	}

	public static boolean sendmsg(Message msg) throws InterruptedException
	{
		try 
		{
			if(queue.size() < 3)
			{
				queue.put(msg);
				return true;
			}
		} 
		catch (InterruptedException e) 
		{
			e.printStackTrace();
		}
		return false;
	}
	public void run() 
	{
		try
		{

			//System.out.println("run produce at masterfilepath ="+masterfilepath);
			//produce messages
			File fXmlFile = new File(masterfilepath);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = null;
			Document doc = null;
			try {
				dBuilder = dbFactory.newDocumentBuilder();
			} 
			catch (ParserConfigurationException e1)
			{

				e1.printStackTrace();
			}
			try {
				doc = dBuilder.parse(fXmlFile);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			doc.getDocumentElement().normalize();

			if (doc.hasChildNodes()) {
				//System.out.println("inside if ");
				try {
					printNote(doc.getChildNodes());
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

			Message msg = new Message("exit");
			try {
				queue.put(msg);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
		catch(Exception e)
		{
			e.printStackTrace();	
		}
	}

	private static void printNote(NodeList nodeList) throws InterruptedException {
		try
		{
//System.out.println("inside printnode = " +nodeList.getLength());

			for (int count = 0; count < nodeList.getLength();) {

				Node tempNode = nodeList.item(count);

				// make sure it's element node.
				if (tempNode.getNodeType() == Node.ELEMENT_NODE) 
				{
					// get node name and value
					Message msg = new Message(tempNode.getTextContent());

					if(sendmsg(msg))
					{
						Thread.sleep(10);
						// check for child nodes , node attributes are not printed
						if (tempNode.hasChildNodes()) {
							printNote(tempNode.getChildNodes());

						}
						count++;
					}

				}
				else
					count++;
			}

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}


}
