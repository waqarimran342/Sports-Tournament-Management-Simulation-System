import Animation.ConsoleFX;

import Football.MainF;

import java.util.Scanner;

public class Main {

    private static final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        showWelcome();

        while (true) {
            printSportMenu();
            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1" -> {
                    transition("Loading Football Module...");
                  MainF.Footballstart() ;
                }

                case "2" -> exitApp();
                default -> error("Invalid selection! Choose 1–2.");
            }
        }
    }

    // ===================== UI METHODS =====================

    private static void showWelcome() {
        System.out.println("""
                ╔══════════════════════════════════════════════════╗
                ║        🏆 SPORTS TOURNAMENT MANAGEMENT SYSTEM      ║
                ║               Console Edition                    ║
                ╚══════════════════════════════════════════════════╝
                """);
    }

    private static void printSportMenu() {
        System.out.println("""
                ┌──────────────────────────────────────────────┐
                │                SELECT SPORT                  │
                ├──────────────────────────────────────────────┤
                │  [1] ⚽ Football Tournament                  │
                │                  │
                │  [2] ❌ Exit System                          │
                └──────────────────────────────────────────────┘
                """);
        System.out.print("➤ Enter choice: ");
    }

    private static void transition(String msg) {
        System.out.println();
        ConsoleFX.loadingDots(msg, 3, 400);
        ConsoleFX.progressBar("Starting module", 20, 50);
        System.out.println();
    }


    private static void exitApp() {
        ConsoleFX.loadingDots("Saving session", 3, 400);
        ConsoleFX.progressBar("Shutting down system", 15, 60);

        System.out.println("""
            ╔══════════════════════════════════════════════╗
            ║      👋 Thank you for using the system!       ║
            ║         See you next time ⚡                  ║
            ╚══════════════════════════════════════════════╝
            """);
        System.exit(0);
    }


    private static void error(String msg) {
        System.out.println("✖ ERROR: " + msg);
    }
}
