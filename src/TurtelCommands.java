import exeptions.InvalidLengthException;
import exeptions.NoCompOperatorException;
import exeptions.NotAColorException;

import java.util.ArrayList;
import java.util.HashMap;

public class TurtelCommands {
    private static final String MAIN_FUN_NAME = "MAIN";
    private final ArrayList<String> errors;
    private final Turtel turtel;

    public TurtelCommands(Turtel turtel) {
        errors = new ArrayList<>();
        this.turtel = turtel;
    }

    public void draw() {
        turtel.repaint();
    }

    public void reset() {
        turtel.reset();
        errors.clear();
    }

    public ArrayList<String> getErrors() {
        return errors;
    }

    private enum ErrorType {
        ARG_NUM, NOT_A_NUM, FUN_IN_FUN, END_OUT_OF_FUN, UNKNOWN_FUN, COMP_FALSELY_FORMAT, UNKNOWN_COMP, MAIN_FUN, NO_COLOR, INVALID_LENGTH,
    }

    private void addError(ErrorType e, int line, String funName) {
        String msg = switch (e) {
            case ARG_NUM -> "wrong number of args";
            case NOT_A_NUM -> "Not a number";
            case FUN_IN_FUN -> "FUN in Fun";
            case END_OUT_OF_FUN -> "END before FUN";
            case UNKNOWN_FUN -> "Unknown FUN";
            case COMP_FALSELY_FORMAT -> "Comparison is falsely formated";
            case UNKNOWN_COMP -> "Unknown comparison";
            case MAIN_FUN -> "FUN with name " + MAIN_FUN_NAME + " is not allowed";
            case NO_COLOR -> "This is not a color at line";
            case INVALID_LENGTH -> "LENGTH IN MOVE MUST BE > 0";
        };

        errors.add(msg + " at line " + line + " in " + funName);
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
                case "*" -> parseNum(realFloat(parse(values, funVals, input[0])) * realFloat(parse(values, funVals, input[2])));
                case "/" -> parseNum(realFloat(parse(values, funVals, input[0])) / realFloat(parse(values, funVals, input[2])));
                case "%" -> parseNum(realFloat(parse(values, funVals, input[0])) % realFloat(parse(values, funVals, input[2])));
                default -> throw new NumberFormatException();
            };

        } else if (value.contains(" ")) throw new NumberFormatException();

        Integer funStored = funVals == null
                ? null
                : funVals.get(value);

        if (funStored == null) {
            Integer stored = values.get(value);
            return stored == null
                    ? parseNumber(value)
                    : stored;
        } else {
            return funStored;
        }
    }

    private int parseNumber(String numberStr) throws NumberFormatException {
        String cleared = numberStr.replace("_", "");
        String[] numParts = cleared.split("\\.", 2);

        if (numParts[0].length() > 7) throw new NumberFormatException();

        int leftPart = Integer.parseInt(numParts[0]);
        int rightPart = 0;
        if (numParts.length == 2) {
            if (numParts[1].length() > 3) throw new NumberFormatException();

            numParts[1] = String.format("%-3s", numParts[1]);
            numParts[1] = numParts[1].replace(" ", "0");
            rightPart = Integer.parseInt(numParts[1]);
        }

        return leftPart*1_000 + rightPart;
    }

    private int parseNum(float num) {
        return (int) num * 1000;
    }

    private float realFloat(int num) {
        return num / 1000F;
    }

    private int round(int num) {
        return Math.round(num/1000F);
    }

    public void executeCommands(String commandList) {
        HashMap<String, Function> funMap = new HashMap<>();
        HashMap<String, Integer> funValues = new HashMap<>();

        executeCommands(commandList, true, MAIN_FUN_NAME, funMap, funValues);
    }

    private void executeCommands(
            String commandList,
            boolean reset,
            String currentFun,
            HashMap<String, Function> funMap,
            HashMap<String, Integer> funValues
    ) {
        String[] commands = commandList.split("\n");

        long startTime = 0;
        if (reset) {
            startTime = System.nanoTime();
            reset();
        }

        HashMap<String, Integer> values = new HashMap<>();

        boolean inFun = false;
        String funName = null;
        StringBuilder funBody = null;
        String[] funArgsNames = null;

        for (int i = 0; i < commands.length; i++) {
            if (commands[i].isBlank() || commands[i].startsWith("#")) continue;

            commands[i] = commands[i].trim();
            String[] command = commands[i].split(" ", 2);

            if (inFun && !command[0].equals("END") && !command[0].equals("FUN")) {
                assert funBody != null;
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
                            return;
                        }
                        int length = parse(values, funValues, args[0]);
                        turtel.move(length);
                        break;
                    case "ROTATE":
                        if (args.length != 2) {
                            addError(ErrorType.ARG_NUM, i, currentFun);
                            return;
                        }
                        int angel = parse(values, funValues, args[1]);
                        turtel.rotate(args[0], round(angel));
                        break;
                    case "COLOR":
                        if (args.length != 3) {
                            addError(ErrorType.ARG_NUM, i, currentFun);
                            return;
                        }
                        int r = round(parse(values, funValues, args[0]));
                        int g = round(parse(values, funValues, args[1]));
                        int b = round(parse(values, funValues, args[2]));

                        turtel.setColor(r, g, b);
                        break;
                    case "PEN_UP":
                        turtel.setDrawing(false);
                        break;
                    case "PEN_DOWN":
                        turtel.setDrawing(true);
                        break;
                    case "PEN_SWITCH":
                        turtel.switchDrawing();
                        break;

                    case "VAL":
                        if (args.length != 2) {
                            addError(ErrorType.ARG_NUM, i, currentFun);
                            return;
                        }
                        values.put(args[0], parse(values, funValues, args[1]));
                        break;

                    case "FUN":
                        if (args.length == 0) {
                            addError(ErrorType.ARG_NUM, i, funName);
                            return;
                        }
                        if (args[0].equals(MAIN_FUN_NAME)) {
                            addError(ErrorType.MAIN_FUN, i, funName);
                            return;
                        }
                        if (inFun) {
                            addError(ErrorType.FUN_IN_FUN, i, currentFun);
                            return;
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
                            return;
                        }
                        inFun = false;
                        assert funBody != null;
                        funMap.put(funName, new Function(funArgsNames, funBody.toString()));
                        break;
                    case "CALL_IF":
                        if (args.length < 2) {
                            addError(ErrorType.ARG_NUM, i, currentFun);
                            return;
                        }
                        String[] split = args[0].split(" ");
                        if (split.length != 3) {
                            addError(ErrorType.COMP_FALSELY_FORMAT, i, currentFun);
                            return;
                        }
                        int left = parse(values, funValues, split[0]);
                        int right = parse(values, funValues, split[2]);
                        boolean result = parseIf(left, right, split[1]);

                        if (!result) continue;
                        arg_offset = 1;
                    case "CALL":
                        Function callFun = funMap.get(args[arg_offset]);
                        if (callFun == null) {
                            addError(ErrorType.UNKNOWN_FUN, i, currentFun);
                            return;
                        }
                        if ((args.length - 1 - arg_offset) != callFun.getArgsCount()) {
                            addError(ErrorType.ARG_NUM, i, currentFun);
                            return;
                        }

                        HashMap<String, Integer> callVals = new HashMap<>();
                        for (int j = 0; j < callFun.getArgsCount(); j++) {
                            callVals.put(callFun.getArgName(j), parse(values, funValues, args[arg_offset + j + 1]));
                        }

                        executeCommands(callFun.getBody(), false, args[arg_offset], funMap, callVals);
                        break;
                }
            } catch (NumberFormatException ignored) {
                addError(ErrorType.NOT_A_NUM, i, funName);
                return;
            } catch (NoCompOperatorException ignored) {
                addError(ErrorType.UNKNOWN_COMP, i, funName);
                return;
            } catch (NotAColorException ignored) {
                addError(ErrorType.NO_COLOR, i, funName);
                return;
            } catch (InvalidLengthException ignored) {
                addError(ErrorType.INVALID_LENGTH, i, funName);
                return;
            }
        }

        if (reset) {
            long endTime = System.nanoTime();
            System.out.printf("Pars time: %,fs %n", (double) (endTime - startTime) / 1_000_000_000.0);
            turtel.start();
        }
    }
}
