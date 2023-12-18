package a2;

import java.util.concurrent.Semaphore;

public class TradeThread extends Thread {
	Semaphore agentSemaphore;
	Ticket t;
	int balance;

	public TradeThread(Semaphore agentSemaphore, int balance, Ticket t) {
		this.agentSemaphore = agentSemaphore;
		this.t = t;
		this.balance = balance;
	}

	public void run() {
		try {
			agentSemaphore.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		if (t.getSale() > 0) {
			System.out.println(A2.printFormattedTime() + " Starting purchase of " + t.getSale() + " tickets of " + t.getName());
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} else if (t.getSale() < 0) {
			System.out.println(A2.printFormattedTime() + " Starting sale of " + Math.abs(t.getSale()) + " tickets of " + t.getName());
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		if (t.getSale() > 0) {
			if (A2.balance < (t.getPrice() * t.getSale())) {
				System.out.println("Transaction failed due to insufficient balance. Unsuccessful purchase of "
						+ Math.abs(t.getSale()) + " tickets of " + t.getName());
				A2.rejectedTrades++; 
				return; 
			} else {
				A2.sale(t); 
			}
		}
		else {
			A2.purchase(t); 
		}
		
		agentSemaphore.release();
	}
}
