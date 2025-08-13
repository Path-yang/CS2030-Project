# CS2030 Project — Discrete-Event Shop Simulator

A small Java program that simulates customers arriving at a shop with multiple servers. Customers either:
- are served immediately if a server is free,
- wait in a finite per-server queue if there is space,
- or leave if all queues are full.

Service time is deterministic (default 1.0 time unit per customer) via `DefaultServiceTime`.

## Requirements
- Java 17 or newer (uses records and `Stream.toList()`)

## Build
```bash
# From the project root
javac *.java
```

## Run
You can run interactively or via redirected input.

```bash
# Interactive
java Main

# Or with input file
java Main < input.txt
```

## Input format
The program reads from standard input.

- First line: three integers
  - `numServers` — number of servers
  - `qmax` — per-server maximum queue length
  - `numCustomers` — number of customers
- Following `numCustomers` lines: two values per line
  - `customerId` (int), `arrivalTime` (double)

Example:
```text
2 1 5
1 0.000
2 0.200
3 0.400
4 0.600
5 0.800
```

## Output
Two parts are printed:
- Event trace: one line per event (arrives, waits, serves, done, leaves), in chronological order
- Stats line: `[<avgWaitingTime> <servedCount> <leftCount>]`

Example (format illustration; exact trace depends on input):
```text
0.000 customer 1 arrives
0.000 customer 1 serves by server 1
1.000 customer 1 done
...
[0.000 1 0]
```

## Design overview
- `Main` — parses input, constructs `Simulator`, prints trace and stats.
- `Simulator` — sets up initial `ArriveEvent`s and runs the event loop, accumulating output and statistics.
- `State` — immutable container of the event priority queue, current `Shop`, last event text, and stats.
- `Shop` — immutable collection of `Server`s and a service-time generator (`Supplier<Double>`).
- `Server` — immutable server state (ID, next-available time, queue capacity/contents) with helpers to serve/queue.
- `Event` hierarchy — discrete events with `next(shop)` to compute the next event/shop state:
  - `ArriveEvent`, `WaitEvent`, `ServeEvent`, `DoneEvent`, `LeaveEvent`.
- `PQ<E>` — immutable wrapper over `PriorityQueue` with functional-style operations.
- `Customer` — customer ID and arrival time.
- `Pair<T,U>` — simple record pair.
- `DefaultServiceTime` — returns constant `1.0`; swap with your own `Supplier<Double>` for different distributions.

## Customizing service time
Replace the `Supplier<Double>` (currently `DefaultServiceTime`) with another implementation that returns a sampled service duration.

## Project structure
- Source files are all in the project root for simplicity.

## License
No license specified. 