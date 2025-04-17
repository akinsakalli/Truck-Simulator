public class Truck {
    private int id;
    private int capacity;
    private int load;
    private ParkingLot parkingLot;

    public Truck(int id, int capacity) {
        this.id = id;
        this.capacity = capacity;
        this.load = 0;
    }
    public void setParkingLot(ParkingLot parkingLot) { this.parkingLot = parkingLot; }
    public ParkingLot getParkingLot() { return this.parkingLot; }
    public int getId() { return this.id; }
    public int getCapacity() { return this.capacity; }
    public int getLoad() { return this.load; }
    public void setLoad( int load ) { this.load = load; }
    public void unload() { this.load = 0; }

}
