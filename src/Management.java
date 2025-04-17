import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Management {

    private static ParkingLot[] parkingLotsHashMap = new ParkingLot[500001];
    // Parking lots are stored in a hash map and the key is the capacity constraint for each parking lot.
    private static AvlTree<Integer> parkingLots = new AvlTree<>();
    // All parking lots are also stored in an AVL Tree. The AVL Tree nodes contain the capacity constraint of that parking lot.
    private static AvlTree<Integer> addingSuitableParkingLots = new AvlTree<>();
    // Parking lots which have fewer trucks than its truck limit are added to another AVL Tree.
    // The purpose of this act is to reduce the time to search the suitable parking lot when adding a truck.
    private static AvlTree<Integer> readySuitableParkingLots = new AvlTree<>();
    // Parking lots which contain truck in its waiting section is stored in another AVL Tree,
    // in order to reduce the time to search for the suitable parking lot when ready command is issued.
    private static AvlTree<Integer> loadSuitableParkingLots = new AvlTree<>();
    // Parking lots which contain truck in its ready section is stored in another AVL Tree,
    // in order to reduce the time to search for the suitable parking lot when load command is issued.

    private static PrintWriter writer;
    public static void scanFile(String fileName, String outputFileName) throws FileNotFoundException {
        File file = new File(fileName);
        Scanner input = new Scanner(file);
        writer = initialiseWriter(outputFileName); // A PrintWriter object is created in order to write to the output file.
        String action;
        int line = 0;
        while (input.hasNextLine()) {
            line += 1; // The loop is iterating through the input file line by line.
            try {
                action = input.next();
            }
            catch (NoSuchElementException e) { break; }
            // Different methods are called in response to the different inputs.
            if (action.equals("create_parking_lot")) {
                int capacityConstraint = Integer.parseInt(input.next());
                int truckLimit = Integer.parseInt(input.next());
                createParkingLot(capacityConstraint, truckLimit);
            }
            else if (action.equals("add_truck")) {
                int truckId = Integer.parseInt(input.next());
                int capacity = Integer.parseInt(input.next());
                addTruck(truckId, capacity);
            }
            else if (action.equals("ready")) {
                int capacity = Integer.parseInt(input.next());
                ready(capacity);
            }
            else if (action.equals("load")) {
                int capacity = Integer.parseInt(input.next());
                int loadAmount = Integer.parseInt(input.next());
                load(capacity, loadAmount);
            }
            else if (action.equals("delete_parking_lot")) {
                int capacity = Integer.parseInt(input.next());
                deleteParkingLot(capacity);
            }
            else if (action.equals("count")) {
                int capacity = Integer.parseInt(input.next());
                count(capacity);
            }
        }
        writer.close(); // PrintWriter object is closed.
    }
    private static void createParkingLot(int capacityConstraint, int truckLimit) {
        // A parking lot is created, and it is added to the hashmap, the general AVL Tree and the AVL Tree which contains the suitable parking lots for adding.
        parkingLotsHashMap[capacityConstraint] = new ParkingLot(capacityConstraint, truckLimit);
        parkingLots.insert(capacityConstraint);
        addingSuitableParkingLots.insert(capacityConstraint);
    }
    private static void deleteParkingLot(int capacity) {
        // Parking lot is deleted from the hashmap and the AVL trees.
        parkingLotsHashMap[capacity] = null;
        addingSuitableParkingLots.remove(capacity);
        readySuitableParkingLots.remove(capacity);
        loadSuitableParkingLots.remove(capacity);
    }
    private static void addTruck(int truckId, int capacity) {
        Truck truck = new Truck(truckId, capacity); // A truck is created with the given specifications.
        Integer parkingLotCapacity = addingSuitableParkingLots.findSmallerLargest(capacity);
        // The parking lot with the given capacity is searched in the AVL Tree containing the parking lots suitable for adding operation.
        // If no parking lot is found with the given capacity, the largest parking lot with a smaller capacity is searched.
        if (parkingLotCapacity == null) {
            try {
                int[] outputData = {-1}; // If no parking lot can be found, truck is not added.
                writeFile("add_truck", outputData); // This method is called in order to write the outputs.
            } catch (FileNotFoundException e) { return; }
            return;
        }
        ParkingLot parkingLot = parkingLotsHashMap[parkingLotCapacity];
        parkingLot.addTruck(truck); // Truck is added to the corresponding parking lot.
        truck.setParkingLot(parkingLot);
        if (parkingLot.isFull()) {
            addingSuitableParkingLots.remove(parkingLotCapacity);
            // If the parking lot becomes full after adding the truck, it is removed from the AVL Tree containing suitable parking lots for adding operation.
        }
        if (parkingLot.waitingTrucksDoExist()) {
            readySuitableParkingLots.insert(parkingLotCapacity);
            // After adding the truck, there is at least one truck in the waiting section,
            // therefore the parking lot is added to the AVL Tree containing suitable parking lots for ready operation.
        }
        int[] outputData = {parkingLotCapacity};
        try {
            writeFile("add_truck", outputData);
        }
        catch (FileNotFoundException e) {}
    }
    private static void ready(int capacity) {
        Integer targetParkingLotCapacity = readySuitableParkingLots.findLargerSmallest(capacity);
        // The parking lot with the given capacity is searched in the AVL Tree containing the parking lots suitable for adding operation.
        // If no parking lot is found with the given capacity, the smallest parking lot with a larger capacity is searched.
        if (targetParkingLotCapacity == null) {
            try {
                int[] outputData = {-1};
                writeFile("ready", outputData);
            } catch (FileNotFoundException e) {}
            return;
        }
        ParkingLot parkingLot = parkingLotsHashMap[targetParkingLotCapacity];
        Truck movedTruck = parkingLot.moveFirstWaitingTruckToReady(); // Truck is moved from waiting section to ready section.

        if (!parkingLot.waitingTrucksDoExist())
            readySuitableParkingLots.remove(targetParkingLotCapacity);
        // After moving the truck to ready section, the waiting section of that parking lot might have become empty,
        // in that case, it is removed from the corresponding AVL Tree.
        if (parkingLot.readyTrucksDoExist())
            loadSuitableParkingLots.insert(targetParkingLotCapacity);
        // After the ready command, there is at least one truck in the ready section. Therefore, the parking lot is added to the corresponding AVL Tree.
        int truckId = movedTruck.getId();
        int[] outputData = {truckId, targetParkingLotCapacity};
        try {
            writeFile("ready", outputData);
        }
        catch (FileNotFoundException e) {}
    }
    private static void load(int capacity, int load) {
        ArrayList<int[]> outputData = new ArrayList<>();
        int parkingLotCapacity = capacity;
        int loadAmount = load;
        ArrayList<Truck> trucksNeedingRelocation = new ArrayList<>();
        while (loadAmount > 0) {
            Integer targetParkingLotCapacity = loadSuitableParkingLots.findLargerSmallest(parkingLotCapacity);
            // The parking lot with the equal or smallest larger capacity is searched.
            if (targetParkingLotCapacity == null) {
                try {
                    relocateTrucks(outputData, trucksNeedingRelocation); // The trucks that needs relocation are relocated.
                    if (outputData.size() == 0) {
                        int[] data = {-1};
                        writeFile("load", data);
                    }
                    else {
                        int[] data = dataTypeConversion(outputData);
                        writeFile("load", data);
                    }
                } catch (FileNotFoundException e) {}
                return;
            }
            ParkingLot parkingLot = parkingLotsHashMap[targetParkingLotCapacity];

            // While there exists some load, the loop continues and the load is distributed.
            while (loadAmount > 0 && parkingLot.readyTrucksDoExist()) {
                Truck truck = parkingLot.readyTrucksDequeue();
                if (!parkingLot.readyTrucksDoExist())
                    loadSuitableParkingLots.remove(targetParkingLotCapacity);
                // If there are no trucks in ready section, that parking lot is removed from the corresponding tree.
                int remainingCapacity = targetParkingLotCapacity;

                if (loadAmount < remainingCapacity) {
                    truck.setLoad(truck.getLoad() + loadAmount);
                    loadAmount = 0; // Truck gets the last amount of load.
                    trucksNeedingRelocation.add(truck);
                }
                else {
                    loadAmount -= remainingCapacity; // Truck gets as much load as it possibly can get.
                    truck.setLoad(truck.getLoad() + remainingCapacity);
                    trucksNeedingRelocation.add(truck);
                }
            }
        }
        relocateTrucks(outputData, trucksNeedingRelocation);
        int[] data = dataTypeConversion(outputData);
        try {
            writeFile("load", data);
        } catch (FileNotFoundException e) {}
    }

    private static int[] dataTypeConversion(ArrayList<int[]> outputData) {
        int[] dataReshaped = new int[outputData.size() * 2]; // Data in the array list is converted to data in an integer array.
        for (int i = 0; i < outputData.size() * 2 - 1; i = i + 2) {
            dataReshaped[i] = outputData.get(i / 2)[0];
            dataReshaped[i + 1] = outputData.get(i / 2)[1];
        }
        return dataReshaped;
    }

    private static void relocateTrucks(ArrayList<int[]> outputData, ArrayList<Truck> trucksNeedingRelocation) {
        int idx = 0;
        while (idx <= trucksNeedingRelocation.size()-1) {
            Truck truck = trucksNeedingRelocation.get(idx);
            int relocationResult = relocate(truck); // Trucks are being relocated.
            int[] data = new int[2];
            data[0] = truck.getId();
            if (relocationResult != -1) {
                data[1] = relocationResult; // The new parking lot of that truck is put into the data.
            }
            else {
                data[1] = -1; // If the truck is not placed anywhere, the output is -1 for that truck.
            }
            outputData.add(data);
            idx += 1;
        }
    }

    // Returns the capacity constraint of the new parking lot
    private static int relocate(Truck truck) {
        ParkingLot originalParkingLot = truck.getParkingLot();
        int originalParkingLotCapacity = originalParkingLot.getCapacityConstraint();
        if (!originalParkingLot.isFull())
            addingSuitableParkingLots.insert(originalParkingLotCapacity);
        // If the parking lot from which the truck is moved is not full after the truck is relocated,
        // that parking lot is added to the parking lots which are suitable for adding operation.
        int remainingCapacity = truck.getCapacity() - truck.getLoad();
        if (remainingCapacity == 0)
            truck.unload();
        // Truck unloads all of its load and becomes empty again, goes to the parking lot with the capacity constraint equal to its original capacity.
        remainingCapacity = truck.getCapacity() - truck.getLoad(); // Remaining capacity is calculated again, it is changed if there is an unload.
        int newParkingLotCapacity;
        if (addingSuitableParkingLots.findSmallerLargest(remainingCapacity) == null)
            return -1; // If there is no suitable parking lot for relocating, the truck is not placed anywhere.
        else
             newParkingLotCapacity = addingSuitableParkingLots.findSmallerLargest(remainingCapacity);
        ParkingLot newParkingLot = parkingLotsHashMap[newParkingLotCapacity];
        boolean waitingTrucksExist = newParkingLot.waitingTrucksDoExist();
        newParkingLot.addTruck(truck);
        truck.setParkingLot(newParkingLot);
        if (newParkingLot.isFull()) {
            addingSuitableParkingLots.remove(newParkingLotCapacity);
        }
        if (!waitingTrucksExist)
            readySuitableParkingLots.insert(newParkingLotCapacity);
        return newParkingLotCapacity;
    }
    private static void count(int capacity) {
        int parkingLotCapacity = capacity+1;
        int count = 0;
        while (parkingLotCapacity < 101 ) {
            if (parkingLotsHashMap[parkingLotCapacity] != null) {
                count += parkingLotsHashMap[parkingLotCapacity].getNumberOfTrucks();
                // If there exists a parking lot with the corresponding capacity, the number of trucks in that parking lot is added to the sum.
            }
            parkingLotCapacity += 1; // Each parking lot is traversed from the initial capacity to greater capacities.
        }
        int[] data = {count};
        try {
            writeFile("count", data);
        } catch (FileNotFoundException e ) {}
    }

    // The output data from the inputs is written to the output file.
    private static void writeFile(String type, int[] outputData) throws FileNotFoundException {
        if (type.equals("add_truck")) {
            int capacity = outputData[0];
            writer.println(capacity);
        }
        else if (type.equals("ready")) {
            if (outputData[0] != -1) {
                int truckId = outputData[0];
                int capacity = outputData[1];
            }
            if (outputData[0] == -1) {
                writer.println(-1);
            }
            else {
                int truckId = outputData[0];
                int capacity = outputData[1];
                writer.println(truckId + " " + capacity);
            }
        }
        else if (type.equals("load")) {
            if (outputData[0] == -1) {
                writer.println(-1);
            }
            else {
                for (int i = 0 ; i < outputData.length - 3 ; i = i+2) {
                    int truckId = outputData[i];
                    int capacityConstraint = outputData[i+1];
                    writer.print(truckId + " " + capacityConstraint + " - ");
                }
                writer.println(outputData[outputData.length-2] + " " + outputData[outputData.length-1]);
            }
        }
        else if (type.equals("count")) {
            int count = outputData[0];
            writer.println(count);
        }
    }
    private static PrintWriter initialiseWriter(String outputFilename) throws FileNotFoundException {
        PrintWriter writer = new PrintWriter(outputFilename);
        // PrintWriter object is initialised in order to write to the output file.
        return writer;
    }
}

