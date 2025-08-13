import java.util.Optional;

class State {
    private final PQ<Event> eventPriorityQueue;
    private final Optional<Shop> shopInstance;
    private final Optional<Event> priorEvent;
    private final String outputText;
    private final double accumulatedWaitTime;
    private final int departedCustomers;

    State(PQ<Event> eventPriorityQueue, Shop shop) {
        this.eventPriorityQueue = eventPriorityQueue;
        this.shopInstance = Optional.of(shop);
        this.priorEvent = Optional.empty();
        this.outputText = "";
        this.accumulatedWaitTime = 0.0;
        this.departedCustomers = 0;
    }

    private State(PQ<Event> eventPriorityQueue, Optional<Shop> shopInstance,
            Optional<Event> priorEvent, String outputText, 
            int departedCustomers, double accumulatedWaitTime) {
        this.eventPriorityQueue = eventPriorityQueue;
        this.shopInstance = shopInstance;
        this.priorEvent = priorEvent;
        this.outputText = outputText;
        this.accumulatedWaitTime = accumulatedWaitTime;
        this.departedCustomers = departedCustomers;
    }

    // Returns current state output
    @Override
    public String toString() {
        return this.outputText;
    }

    // Returns number of customers who left
    public int getCustomersLeft() {
        return this.departedCustomers;
    }

    // Returns total wait time
    public double getTotalWaitingTime() {
        return this.accumulatedWaitTime;
    }
    
    // Creates next state
    private Optional<State> makeNextState(Optional<PQ<Event>> newQueue,
            Optional<Shop> newShop, Optional<Event> newPriorEvent,
            String newOutput, int newDepartedCount, double newWaitTime) {
        return newQueue.map(queue -> new State(queue, newShop, newPriorEvent,
                newOutput, newDepartedCount, newWaitTime));
    }
    
    // Processes next event
    public Optional<State> next() {
        Pair<Optional<Event>, PQ<Event>> pollResult = this.eventPriorityQueue.poll();
        Optional<Event> currentEvent = pollResult.t();
        PQ<Event> remainingEvents = pollResult.u();
        
        return currentEvent.flatMap(event -> {
            int newDepartedCount = this.departedCustomers + event.getCustomersLeft();
            double newWaitTime = this.accumulatedWaitTime + event.getWaitingTime();
            String newOutput = event.toString();
            Optional<Pair<Event, Shop>> nextResult = this.shopInstance
                .flatMap(shop -> event.next(shop));
            Optional<Shop> shopAfter = nextResult.map(pair -> pair.u());
            Optional<Event> nextEvent = nextResult.map(pair -> pair.t());
            Optional<PQ<Event>> updatedQueue = nextEvent
                .filter(newEvent -> event != newEvent)
                .map(newEvent -> remainingEvents.add(newEvent))
                .or(() -> Optional.of(remainingEvents));
            Optional<Event> newPriorEvent = Optional.of(event);
            return makeNextState(updatedQueue, shopAfter, newPriorEvent,
                    newOutput, newDepartedCount, newWaitTime);
        });
    }
}