import java.util.Optional;

class LeaveEvent extends Event {
    LeaveEvent(Customer customer, double eventTime) {
        super(customer, eventTime);
    }

    @Override
    public String toString() {
        return String.format("%.3f", super.occurrenceTime) + " " + super.customer 
            + " leaves";
    }

    // Counts customers who left
    @Override
    public int getCustomersLeft() {
        return 1;
    }
    
    // No further event
    @Override
    public Optional<Pair<Event,Shop>> next(Shop shop) {
        return Optional.of(new Pair<Event,Shop>(this, shop));
    }
}
