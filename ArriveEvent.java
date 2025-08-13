import java.util.Optional;

class ArriveEvent extends Event {
    ArriveEvent(Customer customer, double eventTime) {
        super(customer, eventTime);
    }

    // Determines next event based on server availability
    @Override
    public Optional<Pair<Event, Shop>> next(Shop shop) {
        return shop.findServer(super.customer)
            .map(availableServer -> new Pair<Event, Shop>(
                new ServeEvent(super.customer, availableServer, super.occurrenceTime, false),
                shop
            ))
            .or(() -> shop.findServerQueue()
                .map(queueableServer -> {
                    Server queuedServer = queueableServer.addToQueue(super.customer);
                    Shop updatedShop = shop.addQueue(queuedServer);
                    return new Pair<Event, Shop>(
                        new WaitEvent(super.customer, super.occurrenceTime, queuedServer),
                        updatedShop
                    );
                })
            )
            .or(() -> Optional.of(
                new Pair<Event, Shop>(
                    new LeaveEvent(super.customer, super.occurrenceTime),
                    shop
                )
            ));
    }

    @Override
    public String toString() {
        return String.format("%.3f", super.occurrenceTime) + " " 
            + super.customer + " arrives";
    }
}