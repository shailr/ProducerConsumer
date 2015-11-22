import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/*
 *
 * 3 producer and 3 consumers producing/consuming 50 items
 *
 */
public class ProducerConsumerQueue {

        LinkedList<Integer> items = new LinkedList<Integer>();
        final static int NO_ITEMS = 50;

        public static void main(String args[]) {
                ProducerConsumerQueue pc = new ProducerConsumerQueue();
                Producer producer = pc.new Producer();
                Thread tp1 = new Thread(producer);
                Thread tp2 = new Thread(producer);
                Thread tp3 = new Thread(producer);
                Consumer consumer  = pc.new Consumer();
                Thread tc1 = new Thread(consumer);
                Thread tc2 = new Thread(consumer);
                Thread tc3 = new Thread(consumer);
                tp1.start();
                tp2.start();
                tp3.start();
                tc1.start();
                tc2.start();
                tc3.start();
                System.out.println("Main thread is terminating.");
        }

        class Producer implements Runnable {
                AtomicInteger produced = new AtomicInteger();

                public void produce() {
                        System.out.println(Thread.currentThread().getName() + " is Producing " + produced.get());
                        items.addFirst(new Integer(produced.getAndIncrement()));
                        try {
							items.wait(100);
						} catch (InterruptedException e) {
								Thread.interrupted();
						}
                }

                public void run() {
                        // produce NO_ITEMS items
						while (produced.get() <= NO_ITEMS) {
								synchronized (items) {
										produce();
										items.notifyAll();
								}
						}
						System.out.println("Producer thread is terminating.");
                }
        }

        class Consumer implements Runnable {
                //consumed counter to allow the thread to stop
                AtomicInteger consumed = new AtomicInteger();

                public void consume() {
                        if (!items.isEmpty()) {
                                System.out.println(Thread.currentThread().getName() + " is Consuming " + items.removeLast());
                                consumed.incrementAndGet();
								try {
										items.wait(100);
								} catch (InterruptedException e) {
										Thread.interrupted();
								}
                        }
                }

                private boolean theEnd() {
                        return consumed.get() > NO_ITEMS;
                }

                public void run() {
                        while (!theEnd()) {
                                synchronized (items) {
                                        while (items.isEmpty() && (!theEnd())) {
                                                try {
                                                        items.wait(10);
                                                } catch (InterruptedException e) {
                                                        Thread.interrupted();
                                                }
                                        }
                                        consume();
                                }
                        }
						System.out.println("Consumer thread is terminating.");
                }
        }
}