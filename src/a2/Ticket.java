package a2;

public class Ticket {

	public int seconds;
	public String name;
	public int sale;
	public int price;

	public Ticket(int seconds, String name, int sale, int price) {
		this.seconds = seconds;
		this.name = name;
		this.sale = sale;
		this.price = price;
	}

	public int getSeconds() {
		return seconds;
	}

	public void setSeconds(int seconds) {
		this.seconds = seconds;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getSale() {
		return sale;
	}

	public void setSale(int sale) {
		this.sale = sale;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

}

