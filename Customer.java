class Customer {
    private final int customerId;
    private final double timeOfArrival;
    
    Customer(int customerId, double timeOfArrival) {
        this.customerId = customerId;
        this.timeOfArrival = timeOfArrival;
    }

    // Returns the customer's ID
    public int getId() {
        return customerId;
    }
    
    // Returns the customer's arrival time
    public double getArrivalTime() {
        return timeOfArrival;
    }

    // Checks if customer arrives on or after the given time
    public boolean canBeServed(double timeStamp) {
        return this.timeOfArrival >= timeStamp;
    }
    
    // Compares arrival times
    public int compareArrivalTime(Customer otherCustomer) {
        return Double.compare(this.timeOfArrival, otherCustomer.timeOfArrival);
    }

    @Override
    public String toString() {
        return "customer " + this.customerId;
    }
    
}