package kz.ncanode.cmd;

import kz.ncanode.NCANodeApplication;
import kz.ncanode.ioc.ServiceProvider;

import java.util.HashMap;

/**
 * Обработчик аргументов командной строки
 */
public class CmdServiceProvider implements ServiceProvider {

    private final HashMap<String, String> arguments;

    enum ParseState {
        EXPECT_ARGUMENT_TOKEN,
        EXPECT_ARGUMENT_NAME,
        EXPECT_ARGUMENT_VALUE
    }

    public CmdServiceProvider() {
        arguments = parse(NCANodeApplication.arguments);
    }

    public String get(String argumentName) {
        return arguments.get(argumentName);
    }

    public static HashMap<String, String> parse(String[] argumentsList) {

        HashMap<String, String> result = new HashMap<>();

        for (String arg : argumentsList) {
            String[] parsed = parseArg(arg);

            if (parsed[0].length() == 0) continue;

            result.put(parsed[0], parsed[1]);
        }

        return result;
    }

    private static String[] parseArg(String argument) {
        ParseState state = ParseState.EXPECT_ARGUMENT_TOKEN;

        StringBuilder argName  = new StringBuilder();
        StringBuilder argValue = new StringBuilder();

        for (int i=0; i<argument.length(); ++i) {
            char c = argument.charAt(i);

            switch (state) {
                case EXPECT_ARGUMENT_TOKEN:
                    if (c == '-') {
                        state = ParseState.EXPECT_ARGUMENT_NAME;
                        continue;
                    }
                    break;
                case EXPECT_ARGUMENT_NAME:

                    if (c == ':') {
                        state = ParseState.EXPECT_ARGUMENT_VALUE;
                        continue;
                    }

                    argName.append(c);
                    break;
                case EXPECT_ARGUMENT_VALUE:
                    argValue.append(c);
                    break;
            }
        }

        argName = new StringBuilder(argName.toString().trim());
        argValue = new StringBuilder(argValue.toString().trim());

        String[] result = new String[2];
        result[0] = argName.toString();
        result[1] = argValue.toString();

        return result;
    }
}
