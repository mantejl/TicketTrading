package a2;

public class Event {
	public String name;
	public String tour;
	public String venue;
	public String localDate;
	public int agents;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTour() {
		return tour;
	}

	public void setTour(String tour) {
		this.tour = tour;
	}

	public String getVenue() {
		return venue;
	}

	public void setVenue(String venue) {
		this.venue = venue;
	}

	public String getLocalDate() {
		return localDate;
	}

	public void setLocalDate(String localDate) {
		this.localDate = localDate;
	}

	public void setAgent(int agents) {
		this.agents = agents;
	}

	public int getAgent() {
		return agents;
	}
}


