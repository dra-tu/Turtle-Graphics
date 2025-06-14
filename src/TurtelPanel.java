import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

public class TurtelPanel extends JPanel {
    private final ArrayList<Line> lines;
    private final ArrayList<String> errors;
    private final Point turtelPos;
    private double angel;
    private int maxX;
    private int maxY;

    public TurtelPanel(int width, int height) {
        maxX = width;
        maxY = height;

        setBorder(BorderFactory.createLineBorder(Color.BLACK));
        setPreferredSize(new Dimension(maxX, maxY));

        lines = new ArrayList<>();
        errors = new ArrayList<>();
        turtelPos = new Point();
        reset();
    }

    public void reset() {
        lines.clear();
        errors.clear();
        turtelPos.x = maxX / 2;
        turtelPos.y = maxY / 2;
        angel = 0;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // draw Lines
        for (Line line : lines) {
            line.draw(g);
        }

        // draw "turtel"
        Graphics2D g2 = (Graphics2D) g;
        g2.translate(turtelPos.x, turtelPos.y);
        g2.rotate(angel);
        g2.drawChars(new char[]{'>'}, 0, 1, 0, 0);
        g2.translate(-turtelPos.x, -turtelPos.y);
        g2.rotate(-angel);

        // print errors
        if (errors.isEmpty()) return;
        g.setColor(Color.RED);
        for (int i = 0; i < errors.size(); i++) {
            g.drawString(errors.get(i), 0, (i+1) * 20);
        }
    }

    public void move(int length) {
        int newX = (int) (turtelPos.x + length * Math.cos(angel));
        int newY = (int) (turtelPos.y + length * Math.sin(angel));

        lines.add(new Line(turtelPos.x, turtelPos.y, newX, newY));

        turtelPos.x = newX;
        turtelPos.y = newY;
    }

    public void rotate(String direction, int angelDeg) {
        switch (direction) {
            case "R":
                angel = (angel + Math.toRadians(angelDeg)) % (2 * Math.PI);
                break;
            case "L":
                angel = (angel - Math.toRadians(angelDeg)) % (2 * Math.PI);
                break;
        }
    }

    private enum ErrorType {
        ARG_NUM, NOT_A_NUM, FUN_IN_FUN, END_OUT_OF_FUN, UNKNOWN_FUN,
    }
    private void addError(ErrorType e, int line) {
        switch (e) {
            case ARG_NUM:
                errors.add("wrong number of args at line " + line);
                break;
            case NOT_A_NUM:
                errors.add("Not a number at line " + line);
                break;
            case FUN_IN_FUN:
                errors.add("FUN in Fun at line " + line);
                break;
            case END_OUT_OF_FUN:
                errors.add("END before FUN at line " + line);
                break;
            case UNKNOWN_FUN:
                errors.add("UNKNOWN FUN at line " + line);
                break;
        }
    }

    private int parse(HashMap<String, Integer> values, HashMap<String, Integer> funVals, String value) throws NumberFormatException {
        if (value.startsWith("CALC ")) {
            value = value.replace("CALC ", "");
            String[] input = value.split(" ");

            if (input.length != 3) throw new NumberFormatException();
            return switch (input[1]) {
                case "+" -> parse(values, funVals, input[0]) + parse(values, funVals, input[2]);
                case "-" -> parse(values, funVals, input[0]) - parse(values, funVals, input[2]);
                case "*" -> parse(values, funVals, input[0]) * parse(values, funVals, input[2]);
                case "/" -> parse(values, funVals, input[0]) / parse(values, funVals, input[2]);
                default -> throw new NumberFormatException();
            };

        } else if (value.contains(" ")) throw new NumberFormatException();

        Integer funStored = funVals == null
                ? null
                : funVals.get(value);

        if (funStored == null) {
            Integer stored = values.get(value);
            return stored == null
                    ? Integer.parseInt(value)
                    : stored;
        } else {
            return funStored;
        }
    }

    public void executeCommands(String commandList) {
        HashMap<String, Integer> values = new HashMap<>();
        HashMap<String, Function> funMap = new HashMap<>();
        HashMap<String, Integer> funValues = new HashMap<>();

        executeCommands(commandList, true, values, funMap, funValues);
    }

    public void executeCommands(
            String commandList,
            boolean reset,
            HashMap<String, Integer> values,
            HashMap<String, Function> funMap,
            HashMap<String, Integer> funValues
    ) {
        String[] commands = commandList.split("\n");

        if (reset) reset();

        boolean inFun = false;
        String funName = null;
        StringBuilder funBody = null;
        String[] funArgsNames = null;

        for (int i = 0; i < commands.length; i++) {
            if (commands[i].isBlank() || commands[i].startsWith("#")) continue;

            commands[i] = commands[i].trim();
            String[] command = commands[i].split(" ", 2);

            String[] args = (command.length > 1)
                    ? command[1].split("\\|")
                    : new String[0];

            for (int j = 0; j < args.length; j++) {
                args[j] = args[j].trim();
            }

            if (inFun && !command[0].equals("END") && !command[0].equals("FUN")) {
                funBody.append(commands[i]).append("\n");
                continue;
            }

            try {
                switch (command[0]) {
                    case "MOVE":
                        if (args.length != 1) {
                            addError(ErrorType.ARG_NUM, i);
                            continue;
                        }
                        int length = parse(values, funValues, args[0]);
                        move(length);
                        break;
                    case "ROTATE":
                        if (args.length != 2) {
                            addError(ErrorType.ARG_NUM, i);
                            continue;
                        }
                        int angel = parse(values, funValues, args[1]);
                        rotate(args[0], angel);
                        break;

                    case "VAL":
                        if (args.length != 2) {
                            addError(ErrorType.ARG_NUM, i);
                            continue;
                        }
                        values.put(args[0], parse(values, funValues, args[1]));
                        break;

                    case "FUN":
                        if (inFun) {
                            addError(ErrorType.FUN_IN_FUN, i);
                            continue;
                        }
                        inFun = true;
                        funName = args[0];
                        funBody = new StringBuilder();
                        funArgsNames = new String[args.length - 1];
                        System.arraycopy(args, 1, funArgsNames, 0, funArgsNames.length);
                        break;
                    case "END":
                        if (!inFun) {
                            addError(ErrorType.END_OUT_OF_FUN, i);
                            continue;
                        }
                        inFun = false;
                        funMap.put(funName, new Function(funArgsNames, funBody.toString()));
                        break;
                    case "CALL":
                        Function callFun = funMap.get(args[0]);
                        if (callFun == null) {
                            addError(ErrorType.UNKNOWN_FUN, i);
                            continue;
                        }
                        if ((args.length - 1) != callFun.getArgsCount()) {
                            addError(ErrorType.ARG_NUM, i);
                            continue;
                        }

                        HashMap<String, Integer> callVals = new HashMap<>();
                        for (int j = 0; j < callFun.getArgsCount(); j++) {
                            callVals.put(callFun.getArgName(j), parse(values, null, args[j + 1]));
                        }

                        executeCommands(callFun.getBody(), false, values, funMap, callVals);
                        break;
                }
            } catch (NumberFormatException ignored) {
                addError(ErrorType.NOT_A_NUM, i);
            }
        }

        repaint();
    }
}
