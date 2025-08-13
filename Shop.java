import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.IntStream;

class Shop {
    private final List<Server> serverPool;
    private final Supplier<Double> serviceTimeGenerator;
    private final double lastServiceDuration;

    Shop(int serverCount, Supplier<Double> serviceTimeGenerator, int maxQueueLength) {
        this.serverPool = IntStream
            .rangeClosed(1, serverCount)
            .mapToObj(idx -> new Server(idx, maxQueueLength))
            .toList();
        this.serviceTimeGenerator = serviceTimeGenerator;
        this.lastServiceDuration = 0.0;
    }

    private Shop(List<Server> serverPool, Supplier<Double> serviceTimeGenerator, 
                double lastServiceDuration) {
        this.serverPool = serverPool;
        this.serviceTimeGenerator = serviceTimeGenerator;
        this.lastServiceDuration = lastServiceDuration;
    }
    
    // Returns a service time
    public Double getServiceTime() {
        return this.serviceTimeGenerator.get();
    }
    
    // Gets server by ID
    public Server getServer(int serverId) {
        return this.serverPool.get(serverId - 1);
    }

    // Finds server matching target
    public Optional<Server> getServer(Server targetServer) {
        return IntStream
            .rangeClosed(1, this.serverPool.size())
            .mapToObj(idx -> this.serverPool.get(idx - 1))
            .filter(srv -> srv.sameServer(targetServer))
            .findFirst();
    }
    
    // Finds first available server
    public Optional<Server> findServer(Customer customer) {
        return this.serverPool
            .stream()
            .filter(srv -> srv.canServe(customer))
            .findFirst();
    }

    // Finds server with queue space
    public Optional<Server> findServerQueue() {
        return this.serverPool
            .stream()
            .filter(srv -> srv.canQueue())
            .findFirst();
    }  
    
    // Updates shop with new server state
    public Shop update(Customer customer, 
                       Server server, double serviceEndTime, double serviceDuration) {
        Server updatedServer = server.serve(customer, serviceEndTime);
        List<Server> newServerPool = IntStream
            .rangeClosed(1, this.serverPool.size())
            .mapToObj(idx -> {
                Server current = this.serverPool.get(idx - 1);
                return current.sameServer(updatedServer) ? updatedServer : current;
            })
            .toList();
        return new Shop(newServerPool, this.serviceTimeGenerator, serviceDuration);
    }

    // Adds server to queue in shop
    public Shop addQueue(Server server) {
        List<Server> newServerPool = IntStream
            .rangeClosed(1, this.serverPool.size())
            .mapToObj(idx -> {
                Server current = this.serverPool.get(idx - 1);
                return current.sameServer(server) ? server : current;
            })
            .toList();
        return new Shop(newServerPool, this.serviceTimeGenerator, this.lastServiceDuration);
    }

    // Removes server from queue in shop
    public Shop removeQueue(Server server) {
        List<Server> newServerPool = IntStream
            .rangeClosed(1, this.serverPool.size())
            .mapToObj(idx -> {
                Server current = this.serverPool.get(idx - 1);
                return current.sameServer(server) ? server : current;
            })
            .toList();
        return new Shop(newServerPool, this.serviceTimeGenerator, this.lastServiceDuration);
    }

    @Override
    public String toString() {
        return this.serverPool.toString();
    }
}