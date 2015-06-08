

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ProducerConsumerService {
	
	public static void prod() throws Exception {
		try
		{
			
			
			// to get and merge xmls
		
		//Creating BlockingQueue of size 3
		BlockingQueue<Message> queue = new ArrayBlockingQueue<>(3);
		Producer producer = new Producer(queue,LoadProperties.getProp("masterfilepath"));
		Consumer consumer = new Consumer(queue);
		//starting producer to produce messages in queue
		new Thread(producer).start();
		//starting consumer to consume messages from queue
		new Thread(consumer).start();
		System.out.println("Producer and Consumer has been started");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}

