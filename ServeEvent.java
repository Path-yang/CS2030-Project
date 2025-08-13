import java.util.Optional;

class ServeEvent extends Event {
    private final Server servingServer;
    private final boolean fromWaiting;

    ServeEvent(Customer customer, Server servingServer, double eventTime, boolean fromWaiting) {
        super(customer, eventTime);
        this.servingServer = servingServer;
        this.fromWaiting = fromWaiting;
    }

    @Override
    public String toString() {
        return String.format("%.3f", super.occurrenceTime) 
            + " " + super.customer + " serves by " + this.servingServer;
    }

    // Processes service to schedule done event
    @Override
    public Optional<Pair<Event, Shop>> next(Shop shop) {
        Optional<Server> targetServer = shop.getServer(this.servingServer);
        Optional<Shop> adjustedShop = Optional.of(shop);
        
        if (this.fromWaiting) {
            targetServer = targetServer.map(s -> s.removeFromQueue());
            adjustedShop = targetServer.map(s -> shop.removeQueue(s));
        }
        
        double serviceLength = shop.getServiceTime();
        double completionTime = super.occurrenceTime + serviceLength;
        
        Optional<Shop> finalShop = targetServer.map(
            srv -> shop.update(super.customer, srv, completionTime, serviceLength)
        );
        
        return finalShop
            .map(updatedShop -> new Pair<Event, Shop>(
                new DoneEvent(super.customer, completionTime),
                updatedShop
            ))
            .or(() -> Optional.of(
                new Pair<Event, Shop>(
                    new DoneEvent(super.customer, completionTime),
                    shop
                )
            ));
    }
    
    // Waiting time: serve time - arrival time
    @Override
    public double getWaitingTime() {
        return this.occurrenceTime - customer.getArrivalTime();
    }
}
