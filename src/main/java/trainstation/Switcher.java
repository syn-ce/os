package main.java.trainstation;

import java.util.*;
import main.java.log.Log;

/**
 * The Switcher will move all wagons from the parking-Rail to the train-Rail with as little movements as possible.
 * It will document each of its movements in a log. In order to achieve this, it will need an optimal path in a very
 * specific format which will be provided by the TrainStation. When the Switcher has to make a non-trivial decision, it
 * will consult this optimal path and then be able to decide optimally.
 */
public class Switcher {
    private Log log = new Log(new String[]{"Wagon", "From", "To", "Main", "Siding", "Parking"}, new boolean[]{true, false, false, true, true, true});
    private Rail parkingRail;
    private Rail sidingRail;
    private Rail mainRail;
    private int[] wagonValues;
    private int[] uniqueWagonValuesAscending;

    /**
     * Creates a new Switcher with the specified Rails. All wagons should initially be placed on the parking-Rail.
     *
     * @param parkingRail   Rail on which the wagons are initially positioned.
     * @param sidingRail Rail used for switching the wagons.
     * @param mainRail     Rail on which the wagons shall all be placed in correct order.
     * @param uniqueWagonValuesAscending All unique values which occur in the wagon values of the parkingRail, in
     *                                   ascending order.
     */
    public Switcher(Rail parkingRail, Rail sidingRail, Rail mainRail, int[] uniqueWagonValuesAscending) {
        this.parkingRail = parkingRail;
        this.sidingRail = sidingRail;
        this.mainRail = mainRail;
        wagonValues = parkingRail.getWagonValues();
        Arrays.sort(wagonValues);
        this.uniqueWagonValuesAscending = uniqueWagonValuesAscending;
    }

    /**
     * Moves the last wagon of the Rail from to the Rail to.
     *
     * @param from Rail from which to move the wagon from.
     * @param to   Rail to which to move the wagon to.
     */
    private void moveWagon(Rail from, Rail to) {
        int waggon = from.removeWagon();
        to.addWagon(waggon);
        log.addAction(String.valueOf(waggon), from.getName(), to.getName(), mainRail.getReverseWagonString(), sidingRail.getReverseWagonString(), parkingRail.getReverseWagonString());
    }

    /**
     * Moves all wagons onto the train-Rail. The Switcher is able to make its own decisions, meaning that it will figure
     * out the Rail on which the wagons with the next number are currently standing. If all of these wagons are parked
     * on the same Rail, the Switcher will move all of them onto the train-Rail with as few moves as possible. If wagons
     * with the next value happen to be located on both rails, the Switcher will consult the Array of Strings
     * representing the names of the nodes of the optimal path, and make its decision based on that information.
     *
     * @param optimalPathNodeNames Array of Strings containing the names of the nodes of the optimal path in correct
     *                             order. For more information, refer to the documentation of `OptimalPathCalculator`.
     */
    public void shuntNew(String[] optimalPathNodeNames, boolean print) {
        for (int i = 0; i < uniqueWagonValuesAscending.length; i++) {
            int wagonNr = uniqueWagonValuesAscending[i];
            // get positions of that wagonNr on both rails
            int paPos = parkingRail.getSmallestPosOfValue(wagonNr);
            int swPos = sidingRail.getSmallestPosOfValue(wagonNr);

            if (swPos == -1 || paPos == -1) {  // the number is only present on one of the rails: there is no decision to be made
                if (swPos == -1) {  // value is on parkingRail -> remove all value from parkingRail
                    moveAllValueFromTo(wagonNr, parkingRail, sidingRail);
                } else {  // value is on sidingRail -> remove all value from sidingRail
                    moveAllValueFromTo(wagonNr, sidingRail, parkingRail);
                }
            } else {    // wagonNr is present on both rails -> need to consult the list generated by the graph
                String nodeName = "L";      // if there is only one wagon-value left, we can arbitrarily choose to start at the left since it does not matter
                if (i < optimalPathNodeNames.length) {      // if there are at least two different wagon-values left, look at what optimal path says
                    nodeName = optimalPathNodeNames[i];
                }
                if (nodeName.charAt(0) == 'L') {    // look to left (sidingRail) first, remove rightmost value last
                    moveAllValueFromTo(wagonNr, sidingRail, parkingRail);
                    moveAllValueFromTo(wagonNr, parkingRail, sidingRail);
                } else {  // nodeName.charAt(0) == 'R', look to right (parkingRail) first, remove leftmost value last
                    moveAllValueFromTo(wagonNr, parkingRail, sidingRail);
                    moveAllValueFromTo(wagonNr, sidingRail, parkingRail);
                }
            }
        }
        if (print) log.print();
    }

    /**
     * Will move all wagons from the from-Rail to the to-Rail until there are no more wagons with the
     * specified value in the from-Rail. All wagons with value == nr will be moved from the from-Rail onto the
     * train-Rail instead.
     *
     * @param wagonValue Value of wagons to be moved to mainRail.
     */
    private void moveAllValueFromTo(int wagonValue, Rail from, Rail to) {
        while (from.getSmallestPosOfValue(wagonValue) != -1) {
            while (from.getNextWagonValue() != wagonValue) {
                moveWagon(from, to);
            }
            moveWagon(from, mainRail);
        }
    }

    public int getLogSize() {
        return log.getSize();
    }

}
