package main.java.log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/**
 * This class is used by the Switcher for documenting its actions when moving the wagons from one Rail to another.
 */
public class Log {
    private List<String[]> actions = new ArrayList<>();
    private int actionId = 1;
    /**
     * Stores the length of the longest entry of each column. Stored as Integers as it will be converted to Object[] for String-formatting.
     */
    private Integer[] columnLengths;
    /**
     * Stores for each column whether that column should be aligned to the right or not (default is left). Text will be aligned left, numeric columns right.
     */
    private boolean[] alignRight;

    /**
     * Constructs a new log with the specified column-headers (plus an extra column for the IDs of the actions) and
     * aligns all columns i for which alignRight[i] == true (as well as the ID-column) to the right, all others
     * to the left.
     *
     * @param columns    Column-headers of the columns excluding the action-id-column.
     * @param alignRight Array of booleans where alignRight[i] specifies whether columns[i] should be aligned to the
     *                   right (true) or the left (false). Has to be of the same length as columns.
     */
    public Log(String[] columns, boolean[] alignRight) {
        String[] headers = Stream.concat(Arrays.stream((new String[]{"Action"})), Arrays.stream(columns)).toArray(String[]::new);
        actions.add(headers);
        columnLengths = new Integer[columns.length + 1];
        Arrays.fill(columnLengths, 1);
        updateColumnLengths(headers);
        this.alignRight = new boolean[columns.length + 1];
        this.alignRight[0] = true;
        System.arraycopy(alignRight, 0, this.alignRight, 1, columns.length);
    }

    /**
     * Adds an action to the log using the specified entries. The number of entries should equal the number of columns
     * -1, with the first column being reserved for the ID of the action.
     *
     * @param entries Entries for the protocol line, one entry for every column except the first (actionID)
     */
    public void addAction(String... entries) {
        String[] action = new String[entries.length + 1];
        action[0] = Integer.toString(actionId++);
        for (int i = 0; i < entries.length; i++) {
            String entry = entries[i];
            action[i + 1] = entry;
        }
        updateColumnLengths(action);
        actions.add(action);
    }

    /**
     * Checks if the length of any entry of the action exceeds the length of its
     * column. If so, updates the length of the column accordingly.
     *
     * @param action Array of Strings containing an entry for every column of the protokoll.
     */
    private void updateColumnLengths(String[] action) {
        for (int i = 0; i < columnLengths.length; i++) {
            if (action[i].length() > columnLengths[i]) {
                columnLengths[i] = action[i].length();
            }
        }
    }

    /**
     * Multiplies the specified String the specified number of times and returns the new String.
     *
     * @param s           String to be multiplied.
     * @param repetitions Number of repetitions of the String.
     * @return Multiplied String.
     */
    private String multiplyString(String s, int repetitions) {
        return new String(new char[repetitions]).replace("\0", s);
    }

    /**
     * Constructs a new protocol line which can then be formatted for any action by simply calling
     * String.format(line, action) and will have correct spacing.
     *
     * @return String template protocol line which can be String-formatted with any action out of the actions-List.
     */
    private String constructProtocolLine() {
        StringBuilder s = new StringBuilder();
        int spaceBetweenColumns = 3;
        String spaceBetween = multiplyString(" ", spaceBetweenColumns);
        for (int i = 0; i < columnLengths.length; i++) {
            s.append("%%").append(!alignRight[i] ? "-" : "").append("%ds").append(spaceBetween);
        }
        return String.format(s.append("\n").toString(), (Object[]) columnLengths);
    }

    /**
     * Converts the log into readable format and prints it into the console.
     */
    public void print() {
        StringBuilder printableProtokoll = new StringBuilder();
        for (String[] action : actions) {
            String templateLine = constructProtocolLine();
            String line = String.format(templateLine, (Object[]) action);
            printableProtokoll.append(line);
        }
        System.out.println(printableProtokoll);
    }

    /**
     * Getter for the size of the protocol, i.e. the number of actions added to the protocol so far.
     *
     * @return The number of actions recorded so far.
     */
    public int getSize() {
        return actionId - 1;
    }
}
