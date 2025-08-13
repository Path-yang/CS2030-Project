import java.util.Optional;

class WaitEvent extends Event {
    private final Server targetServer;
    private final boolean isPending;

    WaitEvent(Customer customer, double eventTime, Server targetServer) {
        super(customer, eventTime);
        this.targetServer = targetServer;
        this.isPending = false;
    }
    
    WaitEvent(Customer customer, double eventTime, Server targetServer, boolean isPending) {
        super(customer, eventTime);
        this.targetServer = targetServer;
        this.isPending = isPending;
    }
    
    @Override
    public String toString() {
        return this.isPending ? "" : String.format("%.3f", super.occurrenceTime) + " " 
            + super.customer + " waits at " + this.targetServer;
    }
    
    // Schedules serve event or reschedules wait
    @Override
    public Optional<Pair<Event, Shop>> next(Shop shop) {
        Server server = shop.getServer(this.targetServer.getServerId());
        double readyTime = Math.max(super.occurrenceTime, server.getNextCustomerTime());
        if (server.isNextCustomer(super.customer)) {
            return Optional.of(
                new Pair<Event, Shop>(
                    new ServeEvent(super.customer, server, readyTime, true),
                    shop
                )
            );
        }
        return Optional.of(
            new Pair<Event, Shop>(
                new WaitEvent(super.customer, readyTime, server, true),
                shop
            )
        );
    }
}




