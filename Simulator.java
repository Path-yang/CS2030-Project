import java.util.stream.Stream;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

class Simulator {
    private final PQ<Event> initialEvents;
    private final Shop startingShop;
    private final int totalCustomerCount;

    Simulator(int numOfServers, int qmax, Supplier<Double> serviceTime,
            int totalCustomerCount, List<Pair<Integer, Double>> arrivals) {
        this.startingShop = new Shop(numOfServers, serviceTime, qmax);
        
        PQ<Event> eventSetup = new PQ<Event>();
        for (Pair<Integer, Double> arrival : arrivals) {
            eventSetup = eventSetup.add(
                new ArriveEvent(
                    new Customer(arrival.t(), arrival.u()),
                    arrival.u()
                )
            );
        }
        
        this.initialEvents = eventSetup;
        this.totalCustomerCount = totalCustomerCount;
    }

    // Runs the simulation and returns trace and stats
    Pair<String, String> run() {
        Pair<String, Optional<State>> simulationResults = Stream
            .iterate(
                Optional.of(new State(this.initialEvents, this.startingShop)),
                state -> state.isPresent(),
                state -> state.flatMap(s -> s.next())
            )
            .reduce(
                new Pair<>("", Optional.empty()),
                (acc, stateOpt) -> {
                    String accumulatedText = acc.t();
                    Optional<State> finalState = acc.u();
                    
                    if (stateOpt.isPresent()) {
                        State currentState = stateOpt.get();
                        String eventText = currentState.toString();
                        
                        if (!eventText.isEmpty()) {
                            accumulatedText += eventText + "\n";
                        }
                        
                        finalState = Optional.of(currentState);
                    }
                    
                    return new Pair<>(accumulatedText, finalState);
                },
                (a, b) -> b
            );

        String simulationText = simulationResults.t();
        Optional<State> endState = simulationResults.u();

        String statsText = endState.map(state -> {
            int unservedCustomers = state.getCustomersLeft();
            double totalWaitingTime = state.getTotalWaitingTime();
            int servedCustomers = this.totalCustomerCount - unservedCustomers;
            double avgWaitTime = servedCustomers == 0 ? 0.0 : totalWaitingTime / servedCustomers;
            return String.format("[%.3f %d %d]", avgWaitTime, servedCustomers, unservedCustomers);
        }).orElse("[0.000 0 0]");

        return new Pair<>(simulationText, statsText);
    }
}