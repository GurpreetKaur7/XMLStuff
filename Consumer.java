


import java.util.concurrent.BlockingQueue;

public class Consumer implements Runnable{

	private static BlockingQueue<Message> queue;

	public Consumer(BlockingQueue<Message> q){
		queue=q;
	}
	
	public static Message getmsg() throws InterruptedException
	{
		if(queue.size() > 0)
			return queue.take();
		else 
			return null;
	}

	public static String reverse(String s)
	{
		String out = null;
		try{
			out = new StringBuilder(s).reverse().toString();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return out;
	}
	@Override
	public void run() {
		try{
			Message msg;
			//consuming messages until exit message is received
			
			while(true)
			{
				msg = getmsg();
				if(msg == null)
					Thread.sleep(10);
				else
				{
					if(msg.getMsg() =="exit")
						break;
					else
					{
						System.out.println(reverse(msg.getMsg()));
					}
				}
			}
		}
		catch(InterruptedException e) {
			e.printStackTrace();
		}
	}
}
