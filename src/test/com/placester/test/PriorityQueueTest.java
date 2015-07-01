package com.placester.test;


import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.junit.Test;

public class PriorityQueueTest
{

    @Test
    public void test() throws InterruptedException
    {
        final ThreadSafePriorityQueue<QueueTestTask> q = new ThreadSafePriorityQueue<>();
        final Random rand = new Random();
        final ExecutorService threadPool = Executors.newFixedThreadPool(20);

        final int max = 1000;
        for(int i = 0; i < max; i++)
        {
            final int idx = i;
            threadPool.submit(new Runnable(){
                @Override
                public void run()
                {
                    try
                    {
                        //make sure we actually hit multiple threads;
                        Thread.sleep(rand.nextInt(50));
                        q.add(new Priority<QueueTestTask>(rand.nextInt(100), new QueueTestTask(idx)));
                    } catch (final InterruptedException e)
                    {
                        throw new RuntimeException(e);
                    }
                }});
        }

        threadPool.awaitTermination(10000, TimeUnit.MILLISECONDS);

        Assert.assertFalse(q.contains(new Priority<QueueTestTask>(101, new QueueTestTask(1111))));

        int lastPriority = 0, count=0;
        //Test that threadsafe insertion worked and things return in right order
        while(!q.isEmpty())
        {
            final Priority<QueueTestTask> item = q.poll();
            Assert.assertTrue("Items are out of order: " + item.priority + "<" + lastPriority, item.priority >= lastPriority);
            lastPriority = item.priority;
            count++;
        }
        // ensure we dequeued as many items as we inserted
        Assert.assertEquals("Didn't dequeue all of the items", count, max);

        final Priority<QueueTestTask> item1 = new Priority<QueueTestTask>(666, new QueueTestTask(0));
        final Priority<QueueTestTask> item2 = new Priority<QueueTestTask>(0, new QueueTestTask(0));
        final Priority<QueueTestTask> item3 = new Priority<QueueTestTask>(42, new QueueTestTask(0));


        q.add(item1);
        q.add(item2);
        q.add(item3);

        Assert.assertTrue(q.contains(item1));
        Priority<QueueTestTask> item = q.poll();
        Assert.assertEquals(item, item2);
        item = q.poll();
        Assert.assertEquals(item, item3);
        item = q.poll();
        Assert.assertEquals(item, item1);


        q.add(item1);
        Assert.assertFalse(q.isEmpty());
        item = q.peek();
        Assert.assertEquals(item, item1);
        Assert.assertFalse(q.isEmpty());

        item = q.poll();
        Assert.assertEquals(item, item1);
        Assert.assertTrue(q.isEmpty());
    }

}
