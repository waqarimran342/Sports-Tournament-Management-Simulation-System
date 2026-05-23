package Animation;

public class ConsoleFX {

    // ⏳ Loading dots animation
    public static void loadingDots(String msg, int dots, int delay) {
        System.out.print(msg);
        for (int i = 0; i < dots; i++) {
            sleep(delay);
            System.out.print(".");
        }
        System.out.println();
    }

    // 📊 Progress bar animation
    public static void progressBar(String msg, int length, int delay) {
        System.out.print(msg + " [");
        for (int i = 0; i < length; i++) {
            sleep(delay);
            System.out.print("█");
        }
        System.out.println("]");
    }

    // ⏸ Smooth pause
    public static void pause(int millis) {
        sleep(millis);
    }

    private static void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ignored) {}
    }
}
