import java.util.Optional;

abstract class Event implements Comparable<Event> {
    protected final Customer customer;
    protected final double occurrenceTime;

    Event(Customer customer, double occurrenceTime) {
        this.customer = customer;
        this.occurrenceTime = occurrenceTime;
    }

    // Compares events by time
    @Override
    public int compareTo(Event otherEvent) {
        int timeDiff = Double.compare(this.occurrenceTime, otherEvent.occurrenceTime);
        return (timeDiff != 0) ? timeDiff : 
            this.customer.compareArrivalTime(otherEvent.customer);
    }

    // Default number of customers who left
    public int getCustomersLeft() {
        return 0;
    }
    
    // Default waiting time
    public double getWaitingTime() {
        return 0.0;
    }

    // Defines next event
    public abstract Optional<Pair<Event,Shop>> next(Shop shop);
}


