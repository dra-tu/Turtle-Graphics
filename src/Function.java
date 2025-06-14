public class Function {
    private final String[] args;
    private final String body;

    public Function(String[] args, String body) {
        this.args = args;
        this.body = body;
    }

    public String getBody() {
        return body;
    }

    public int getArgsCount() {
        return args.length;
    }

    public String getArgName(int index) {
        if (args.length <= index) return null;

        return args[index];
    }
}
