package tracker;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

public class Messages
{
	/**
	 * The queue of messages stores the received packets in the order they are added here.
	 */
	private Queue<ReceivePacket> messages;
	
	/**
	 * These semaphores control the messages queue.  They implement essentially a 
	 * producer consumer patter where the producer is not limited.  I'm making the assumption
	 * that the producer will never fill all of memory.  Block is used to make access to the
	 * messages queue be atomic.  Empty is used to have the pop function wait if the messages
	 * list is empty.
	 */
	private Semaphore empty, block;
	
	
	public Messages()
	{
		messages = new LinkedList<ReceivePacket>();
		empty = new Semaphore(0);
		block = new Semaphore(1);
	}
	
	/** 
	 * Add a packet to the end of the queue of messages.
	 * @param packet - The packet to the end of the messages.
	 */
	public void push(ReceivePacket packet)
	{
		// Acquire the lockt that makes the queue atomic
		block.acquireUninterruptibly();
		
		messages.add(packet);
		
		// Increase the number in the empty sem
		empty.release();
		
		// Release the lock
		block.release();
	}
	
	/**
	 * Get a packet from the front of the queue of messages and return it.
	 * @return - The oldest element in the queue of messages.
	 */
	public ReceivePacket pop()
	{
		// Value to return.
		ReceivePacket toReturn;
		
		// Acquire a lock so that we don't try enter if the queue is empty. Released by push.
		empty.acquireUninterruptibly();
		
		
		// The queue is not empty.  Get a lock on the messages queue so we can add atomically.
		block.acquireUninterruptibly();		
		toReturn = messages.remove();		
		block.release();
		
		
		return toReturn;
	}
	
}
