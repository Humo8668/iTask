package uz.app.Anno.Util;

import java.lang.instrument.Instrumentation;

public class AnnoAgent {
    private static Instrumentation instrumentation;

    public static void premain(String agentArgs, Instrumentation instrumentation) {
        AnnoAgent.instrumentation = instrumentation;
    }
}
