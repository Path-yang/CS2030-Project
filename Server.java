import java.util.List;
import java.util.stream.Stream;

class Server {
    private final int serverId;
    private final double nextAvailableTime;
    private final int maxQueueCapacity;
    private final int currentQueueLength;
    private final List<Customer> queuedCustomers;

    Server(int serverId, int maxQueueCapacity) {
        this.serverId = serverId;
        this.nextAvailableTime = 0.0;
        this.maxQueueCapacity = maxQueueCapacity;
        this.currentQueueLength = 0;
        this.queuedCustomers = List.of();
    }
    
    private Server(int serverId, double nextAvailableTime, int maxQueueCapacity, 
        int currentQueueLength, List<Customer> queuedCustomers) {
        this.serverId = serverId;
        this.nextAvailableTime = nextAvailableTime;
        this.maxQueueCapacity = maxQueueCapacity;
        this.currentQueueLength = currentQueueLength;
        this.queuedCustomers = queuedCustomers;
    }
    
    // Returns server ID
    public int getServerId() {
        return this.serverId;
    }
    
    // Checks if servers are the same
    public boolean sameServer(Server other) {
        return this.serverId == other.serverId;
    }
    
    // Returns time server is next available
    public double getNextCustomerTime() {
        return this.nextAvailableTime;
    }
    
    // Returns current queue size
    public int getQueue() {
        return this.currentQueueLength;
    }
    
    // Updates server availability
    public Server serve(Customer customer, double endTime) {
        return new Server(this.serverId, endTime, this.maxQueueCapacity, 
            this.currentQueueLength, this.queuedCustomers);
    }
    
    // Checks if server can serve customer
    public boolean canServe(Customer customer) {
        return customer.canBeServed(this.nextAvailableTime);
    }
    
    // Checks if queue has space
    public boolean canQueue() {
        return this.maxQueueCapacity > this.currentQueueLength;
    }
    
    // Adds customer to queue if space is available
    public Server addToQueue(Customer customer) {
        if (this.canQueue()) {
            List<Customer> newQueue = Stream.concat(
                this.queuedCustomers.stream(),
                Stream.of(customer)
            ).toList();
            return new Server(this.serverId, this.nextAvailableTime, 
                this.maxQueueCapacity, this.currentQueueLength + 1, newQueue);
        }
        return this;
    }

    // Removes first customer from queue
    public Server removeFromQueue() {
        if (this.currentQueueLength > 0) {
            List<Customer> newQueue = this.queuedCustomers.subList(1, this.currentQueueLength);
            return new Server(this.serverId, this.nextAvailableTime,
                this.maxQueueCapacity, this.currentQueueLength - 1, newQueue);
        }
        return this;
    }
    
    // Returns next customer in queue
    public Customer peek() {
        if (this.currentQueueLength > 0) {
            return this.queuedCustomers.get(0);
        }
        return new Customer(0, 0);
    }
    
    // Checks if customer is next in queue
    public boolean isNextCustomer(Customer customer) {
        return this.peek().getId() == customer.getId();
    }

    @Override
    public String toString() {
        return "server " + this.serverId;
    }
}