public class ParkingLot implements Comparable<ParkingLot> {
    private int capacityConstraint;
    private int truckLimit;
    private int numberOfTrucks;
    private MyQueue<Truck> waitingTrucks;
    private MyQueue<Truck> readyTrucks;
    public ParkingLot(int capacityConstraint, int truckLimit) {
        this.capacityConstraint = capacityConstraint;
        this.truckLimit = truckLimit;
        this.waitingTrucks = new MyQueue<>();
        this.readyTrucks = new MyQueue<>();
        this.numberOfTrucks = 0;
    }
    public int getCapacityConstraint() {
        return capacityConstraint;
    }

    // Comparison between two parking lots is done according to their capacity constraints.
    public int compareTo(ParkingLot parkingLot) {
        if (this.capacityConstraint < parkingLot.capacityConstraint)
            return -1;
        else if (this.capacityConstraint > parkingLot.capacityConstraint)
            return 1;
        else
            return 0;
    }
    public boolean isFull() {
        if (numberOfTrucks < truckLimit)
            return false;
        else
            return true;
    }
    public void addTruck(Truck truck) {
        this.waitingTrucks.enqueue(truck);
        this.numberOfTrucks = this.numberOfTrucks + 1;
    }

    // This method moves the first truck in the waiting section to ready section.
    public Truck moveFirstWaitingTruckToReady() {
        Truck truck = this.waitingTrucks.dequeue();
        this.readyTrucks.enqueue(truck);
        return truck;
    }

    // Returns true if there exists any trucks in the waiting section.
    public boolean waitingTrucksDoExist() {
        return !this.waitingTrucks.isEmpty();
    }

    // Returns true if there exists any trucks in the waiting section.
    public boolean readyTrucksDoExist() {
        return !this.readyTrucks.isEmpty();
    }

    // Removes and returns the first truck in the ready section.
    public Truck readyTrucksDequeue() {
        this.numberOfTrucks = this.numberOfTrucks - 1;
        return this.readyTrucks.dequeue();
    }

    public int getNumberOfTrucks() { return this.numberOfTrucks; }
}
