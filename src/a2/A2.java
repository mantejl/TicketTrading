package a2;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class A2 {
	static Semaphore buySem = new Semaphore(1);
	static Semaphore sellSem = new Semaphore(1);
	public static Map<String, Semaphore> semStr = new HashMap<String, Semaphore>();
	static int balance = 0;
	static long start = 0;
	static int rejectedTrades = 0;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Scanner scanner = null;
		List<Ticket> tickets = new ArrayList<Ticket>();
		String filepath = null;
		Data tours = null;
		boolean flag = true;
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		while (flag) {
			scanner = new Scanner(System.in);
			System.out.println("What is the name of the event file?");
			filepath = scanner.nextLine();
			File file = new File(filepath);
			try {
				Scanner sc = new Scanner(file);
				String l = "";
				while (sc.hasNext()) {
					l += sc.nextLine();
				}
				sc.close();
				tours = gson.fromJson(l, Data.class);
			} catch (JsonSyntaxException e) {
				System.out.println("The file " + filepath + " is badly formatted.");
				continue;
			} catch (IOException e) {
				System.out.println("The file " + filepath + " cannot be found.");
				continue;
			}
			boolean format = true;
			for (Event t : tours.data) {
				if (t.localDate == null || t.name == null || t.tour == null || t.venue == null
						|| !dateValid(t.localDate)) {
					System.out.println("The file " + filepath + " is not formatted properly.");
					format = false;
					break;
				}
			}
			if (format == true) {
				flag = false;
			}
		}

		System.out.println("The file has been properly read");

		flag = true;

		do {
			Scanner scan = new Scanner(System.in);
			System.out.println("What is the name of the schedule file?");
			String csvData = scan.nextLine();
			File file = new File(csvData);
			Scanner fileScanner = null;
			try {
				fileScanner = new Scanner(file);
				boolean invalidFormat = false;
				while (fileScanner.hasNextLine()) {
					String lineCheck = fileScanner.nextLine();
					String[] columns = lineCheck.split(",");
					if (columns.length != 4) {
						System.out.println("The file " + csvData + " is formatted incorrectly.");
						invalidFormat = true;
						break;
					}
				}
				if (invalidFormat) {
					continue;
				}
				fileScanner = new Scanner(file);
				while (fileScanner.hasNextLine()) {
					String line = fileScanner.nextLine();
					String[] fields = line.split(",");
					int sec = Integer.parseInt(fields[0].trim());
					String name = fields[1].trim();
					int sale = Integer.parseInt(fields[2].trim());
					int price = Integer.parseInt(fields[3].trim());
					Ticket t = new Ticket(sec, name, sale, price);
					tickets.add(t);
				}
				flag = false;
			} catch (NullPointerException e) {
				System.out.println("The file " + csvData + " is formatted incorrectly.");
				continue;
			} catch (ArrayIndexOutOfBoundsException e) {
				System.out.println("The file " + csvData + " is formatted incorrectly.");
				continue;
			} catch (FileNotFoundException e) {
				System.out.println("The file " + csvData + " cannot be found.");
				continue;
			}
		} while (flag);
		System.out.println("The file has been properly read");

		Scanner newScanner = new Scanner(System.in);
		boolean wrongBalance = true;
		while (wrongBalance) {
			System.out.println("What is the initial balance?");
			if (newScanner.hasNextInt()) {
				balance = newScanner.nextInt();
				wrongBalance = false;
			} else {
				System.out.println("Invalid input. Please enter another integer.");
				newScanner.next();
			}
		}

		for (int i = 0; i < tours.getData().size(); i++) {
			Semaphore sem = new Semaphore(tours.getData().get(i).getAgent());
			semStr.put(tours.getData().get(i).getName(), sem);
		}

		System.out.println("Starting execution of program...");
		System.out.println("Initial Balance: " + balance);

		ExecutorService executor = Executors.newCachedThreadPool();
		start = System.currentTimeMillis();

		boolean execution = true;
		for (Ticket t: tickets) {
			while (execution) {
				if ((t.getSeconds() < ((System.currentTimeMillis() - start) / 1000))) {
					Thread trade = new TradeThread(semStr.get(t.getName()), balance, t);
					executor.execute(trade);
					break;
				}
			}
		}
		executor.shutdown();
		while (!executor.isTerminated()) {
			Thread.yield();
		}

		if (rejectedTrades == 0) {
			System.out.println("All trades completed!");
		} else {
			System.out.println("All trades completed, except " + rejectedTrades + " trades.");
		}

	}

	public static String printFormattedTime() {
		DateFormat simple = new SimpleDateFormat("HH:mm:ss:SSS");
		Date result = new Date(System.currentTimeMillis() - start - 1000);
		simple.setTimeZone(TimeZone.getTimeZone("UTC"));
		return "[" + simple.format(result) + "]";
	}

	public static boolean dateValid(String date) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		format.setLenient(false);
		try {
			format.parse(date.trim());
		} catch (ParseException e) {
			return false;
		}
		return true;
	}

	public static void sale(Ticket t) {
		// TODO Auto-generated method stub
		try {
			sellSem.acquire();
			balance -= t.getSale() * t.getPrice();
			System.out.println(
					printFormattedTime() + " Finishing purchase of " + t.getSale() + " tickets of " + t.getName());
			System.out.println("Current Balance after trade: " + A2.balance);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			sellSem.release();
		}
	}

	public static void purchase(Ticket t) {
		// TODO Auto-generated method stub
		try {
			buySem.acquire();
			balance += (Math.abs(t.getSale()) * t.getPrice());
			System.out.println(printFormattedTime() + " Finishing sale of " + Math.abs(t.getSale()) + " tickets of "
					+ t.getName());
			System.out.println("Current Balance after trade: " + A2.balance);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			buySem.release();
		}

	}
}
