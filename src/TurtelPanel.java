import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

public class TurtelPanel extends JPanel {
    private static final String MAIN_FUN_NAME = "MAIN";

    private final ArrayList<Line> lines;
    private final ArrayList<String> errors;
    private final Point turtelPos;
    private double angel;
    private long totalLineLength;
    private long toPrintLine;
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
        totalLineLength = 0L;
    }

    private void paintLoop() {
        Thread t = new Thread(() -> {
            toPrintLine = 0L;

            double drawInterval = 1_000_000_000.0/120.0;
            double delta = 0.0;
            long lastTime = System.nanoTime();
            long currentTime;

            while (toPrintLine < totalLineLength) {
                currentTime = System.nanoTime();
                delta += (currentTime - lastTime) / drawInterval;
                lastTime = currentTime;

                if (delta >= 1) {
                    repaint();
                    toPrintLine = Math.min(toPrintLine + 10L, totalLineLength);
                    delta--;
                }
            }
        });

        t.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // draw Lines
        if (!lines.isEmpty()) {
            System.out.println("HELLO: " + toPrintLine);
            long printed = 0L;
            int i = 0;
            while (printed < toPrintLine) {
                Line line = lines.get(i);
                if ((line.length() + printed) <= toPrintLine) {
                    line.draw(g);
                    printed += line.length();
                    i++;
                } else {
                    long diff = toPrintLine - printed;
                    float c = ((float) diff) / ((float) line.length());

                    int newX = Math.round(line.x0 * c);
                    int newY = Math.round(line.y0 * c);

                    g.drawLine(line.x0, line.y0, newX, newY);
                    break;
                }
            }
            System.out.println("BYE: " + toPrintLine);
        }

        // draw "turtel"
        Graphics2D g2 = (Graphics2D) g;
        g2.translate(turtelPos.x, turtelPos.y);
        g2.rotate(angel);
        g2.drawChars(new char[]{'>'}, 0, 1, 0, 0);
        g2.rotate(-angel);
        g2.translate(-turtelPos.x, -turtelPos.y);

        // print errors
        g.setColor(Color.RED);
        for (int j = 0; j < errors.size(); j++) {
            g.drawString(errors.get(j), 0, (j + 1) * 20);
        }
//        g.setColor(Color.BLACK);
    }

    public void move(int length) {
        int newX = (int) Math.round(turtelPos.x + length * Math.cos(angel));
        int newY = (int) Math.round(turtelPos.y + length * Math.sin(angel));

        Line line = new Line(turtelPos.x, turtelPos.y, newX, newY);
        lines.add(line);
        totalLineLength += line.length();

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
        ARG_NUM, NOT_A_NUM, FUN_IN_FUN, END_OUT_OF_FUN, UNKNOWN_FUN, COMP_FALSELY_FORMAT, UNKNOWN_COMP, MAIN_FUN,
    }

    private void addError(ErrorType e, int line, String funName) {
        switch (e) {
            case ARG_NUM:
                errors.add("wrong number of args at line " + line + " of " + funName);
                break;
            case NOT_A_NUM:
                errors.add("Not a number at line " + line + " of " + funName);
                break;
            case FUN_IN_FUN:
                errors.add("FUN in Fun at line " + line + " of " + funName);
                break;
            case END_OUT_OF_FUN:
                errors.add("END before FUN at line " + line + " of " + funName);
                break;
            case UNKNOWN_FUN:
                errors.add("Unknown FUN at line " + line + " of " + funName);
                break;
            case COMP_FALSELY_FORMAT:
                errors.add("Comparison is falsely formated at line " + line + " of " + funName);
                break;
            case UNKNOWN_COMP:
                errors.add("Unknown comparison at line " + line + " of " + funName);
                break;
            case MAIN_FUN:
                errors.add("FUN with name " + MAIN_FUN_NAME + " is not allowed at line " + line);
        }
    }

    private static class NoCompOperatorException extends RuntimeException {
    }

    private boolean parseIf(int a, int b, String op) throws NoCompOperatorException {
        return switch (op) {
            case "==" -> a == b;
            case "!=" -> a != b;
            case "<" -> a < b;
            case ">" -> a > b;
            case "<=" -> a <= b;
            case ">=" -> a >= b;
            default -> throw new NoCompOperatorException();
        };
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

        executeCommands(commandList, true, MAIN_FUN_NAME, values, funMap, funValues);
    }

    public void executeCommands(
            String commandList,
            boolean reset,
            String currentFun,
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

            if (inFun && !command[0].equals("END") && !command[0].equals("FUN")) {
                funBody.append(commands[i]).append("\n");
                continue;
            }

            String[] args = (command.length > 1)
                    ? command[1].split("\\|")
                    : new String[0];

            for (int j = 0; j < args.length; j++) {
                args[j] = args[j].trim();
            }

            int arg_offset = 0;
            try {
                switch (command[0]) {
                    case "MOVE":
                        if (args.length != 1) {
                            addError(ErrorType.ARG_NUM, i, currentFun);
                            continue;
                        }
                        int length = parse(values, funValues, args[0]);
                        move(length);
                        break;
                    case "ROTATE":
                        if (args.length != 2) {
                            addError(ErrorType.ARG_NUM, i, currentFun);
                            continue;
                        }
                        int angel = parse(values, funValues, args[1]);
                        rotate(args[0], angel);
                        break;

                    case "VAL":
                        if (args.length != 2) {
                            addError(ErrorType.ARG_NUM, i, currentFun);
                            continue;
                        }
                        values.put(args[0], parse(values, funValues, args[1]));
                        break;

                    case "FUN":
                        if (args.length == 0) {
                            addError(ErrorType.ARG_NUM, i, funName);
                            continue;
                        }
                        if (args[0].equals(MAIN_FUN_NAME)) {
                            addError(ErrorType.MAIN_FUN, i, funName);
                            continue;
                        }
                        if (inFun) {
                            addError(ErrorType.FUN_IN_FUN, i, currentFun);
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
                            addError(ErrorType.END_OUT_OF_FUN, i, currentFun);
                            continue;
                        }
                        inFun = false;
                        funMap.put(funName, new Function(funArgsNames, funBody.toString()));
                        break;
                    case "CALL-IF":
                        if (args.length < 2) {
                            addError(ErrorType.ARG_NUM, i, currentFun);
                            continue;
                        }
                        String[] split = args[0].split(" ");
                        if (split.length != 3) {
                            addError(ErrorType.COMP_FALSELY_FORMAT, i, currentFun);
                            continue;
                        }
                        int a = parse(values, funValues, split[0]);
                        int b = parse(values, funValues, split[2]);
                        boolean result = parseIf(a, b, split[1]);

                        if (!result) continue;
                        arg_offset = 1;
                    case "CALL":
                        Function callFun = funMap.get(args[arg_offset]);
                        if (callFun == null) {
                            addError(ErrorType.UNKNOWN_FUN, i, currentFun);
                            continue;
                        }
                        if ((args.length - 1 - arg_offset) != callFun.getArgsCount()) {
                            addError(ErrorType.ARG_NUM, i, currentFun);
                            continue;
                        }

                        HashMap<String, Integer> callVals = new HashMap<>();
                        for (int j = 0; j < callFun.getArgsCount(); j++) {
                            callVals.put(callFun.getArgName(j), parse(values, funValues, args[arg_offset + j + 1]));
                        }

                        executeCommands(callFun.getBody(), false, args[arg_offset], values, funMap, callVals);
                        break;
                }
            } catch (NumberFormatException ignored) {
                addError(ErrorType.NOT_A_NUM, i, funName);
            } catch (NoCompOperatorException ignored) {
                addError(ErrorType.UNKNOWN_COMP, i, funName);
            }
        }

        if (reset) paintLoop();
    }
}
