import Football.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.io.File;
import java.time.LocalDate;
import java.util.List;
import java.util.Random;

public class MainFGui {

    // ----------- Modern Professional Colors -----------
    private static final Color COLOR_PRIMARY = new Color(0x1B263B);    // Navy Blue
    private static final Color COLOR_ACCENT = new Color(0x45B69C);     // Professional Teal
    private static final Color COLOR_BG = new Color(0xF6F9FB);         // Soft Background
    private static final Color COLOR_PANEL = new Color(0xE6ECEF);      // Panel contrast
    private static final Color COLOR_BORDER = new Color(0xCBD3DB);     // Border/lines
    private static final Color COLOR_TEXT = new Color(34, 39, 46);     // Elegant dark text

    private static final Font FONT_MAIN = new Font("SansSerif", Font.PLAIN, 15);
    private static final Font FONT_HEADER = new Font("SansSerif", Font.BOLD, 24);
    private static final Font FONT_TABLE_HDR = new Font("SansSerif", Font.BOLD, 16);

    // ----------- Main Components -----------
    private Tournament tournament;
    private final Random rand = new Random();

    private final JFrame frame = new JFrame("⚽ Football Tournament Manager");
    private final JTabbedPane tabbedPane = new JTabbedPane();
    private final JTextArea summaryArea = new JTextArea(10, 40);
    private final JTextArea scheduleArea = new JTextArea(20, 60);
    private final DefaultTableModel pointsTableModel = new DefaultTableModel();
    private final DefaultTableModel topScorersModel = new DefaultTableModel();

    public static void main(String[] args) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) { }
        SwingUtilities.invokeLater(() -> new MainFGui().initUI());
    }

    private void initUI() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1024, 720);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(COLOR_BG);

        JLabel titleLabel = new JLabel("⚽ FOOTBALL TOURNAMENT ORGANIZER", SwingConstants.CENTER);
        titleLabel.setFont(FONT_HEADER);
        titleLabel.setForeground(COLOR_ACCENT);
        titleLabel.setOpaque(true);
        titleLabel.setBackground(COLOR_PRIMARY);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(24, 0, 24, 0));
        frame.add(titleLabel, BorderLayout.NORTH);

        setupTabs();
        frame.add(tabbedPane, BorderLayout.CENTER);

        frame.add(createButtonPanel(), BorderLayout.SOUTH);
        frame.setVisible(true);
    }

    private JPanel createButtonPanel() {
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 22, 15));
        btnPanel.setBackground(COLOR_BG);

        String[] labels = {"Create Tournament", "Load Tournament", "Save Tournament", "Play Next Match", "Refresh", "Exit"};
        JButton[] buttons = new JButton[labels.length];

        for (int i = 0; i < labels.length; i++) {
            buttons[i] = new JButton(labels[i]);
            styleButton(buttons[i]);
            btnPanel.add(buttons[i]);
        }

        buttons[0].addActionListener(e -> createNewTournament());
        buttons[1].addActionListener(e -> loadTournament());
        buttons[2].addActionListener(e -> { if (ensureTournament()) saveTournament(); });
        buttons[3].addActionListener(e -> { if (ensureTournament()) playNextMatchWithGUI(); });
        buttons[4].addActionListener(e -> refreshAllTabs());
        buttons[5].addActionListener(e -> System.exit(0));

        return btnPanel;
    }

    private void styleButton(JButton btn) {
        btn.setPreferredSize(new Dimension(180, 40));
        btn.setFont(new Font("SansSerif", Font.BOLD, 14));
        btn.setForeground(Color.WHITE);
        btn.setBackground(COLOR_ACCENT);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER, 1, true),
                BorderFactory.createEmptyBorder(8, 20, 8, 20)
        ));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) { btn.setBackground(COLOR_PRIMARY); }
            public void mouseExited(java.awt.event.MouseEvent evt) { btn.setBackground(COLOR_ACCENT); }
        });
    }

    private void setupTabs() {
        tabbedPane.setFont(FONT_TABLE_HDR);
        tabbedPane.setBackground(COLOR_PANEL);
        tabbedPane.setBorder(BorderFactory.createMatteBorder(0, 1, 2, 1, COLOR_BORDER));

        summaryArea.setEditable(false);
        summaryArea.setFont(FONT_MAIN);
        summaryArea.setBackground(COLOR_PANEL);
        summaryArea.setForeground(COLOR_PRIMARY);
        summaryArea.setBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(COLOR_BORDER, 1),
                        "Overview", 0, 0,
                        new Font("SansSerif", Font.ITALIC, 13), COLOR_ACCENT)
        );
        tabbedPane.addTab("Summary", new JScrollPane(summaryArea));

        scheduleArea.setEditable(false);
        scheduleArea.setFont(FONT_MAIN);
        scheduleArea.setBackground(COLOR_PANEL);
        scheduleArea.setForeground(COLOR_PRIMARY);
        scheduleArea.setBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(COLOR_BORDER, 1),
                        "Upcoming Schedule", 0, 0,
                        new Font("SansSerif", Font.ITALIC, 13), COLOR_ACCENT)
        );
        tabbedPane.addTab("Schedule", new JScrollPane(scheduleArea));

        JTable pointsTable = new JTable(pointsTableModel);
        stylizeTable(pointsTable);
        tabbedPane.addTab("Points Table", new JScrollPane(pointsTable));

        JTable scorersTable = new JTable(topScorersModel);
        stylizeTable(scorersTable);
        tabbedPane.addTab("Top Scorers & Awards", new JScrollPane(scorersTable));
    }

    private void stylizeTable(JTable table) {
        table.setRowHeight(28);
        table.setFont(FONT_MAIN);
        table.setForeground(COLOR_TEXT);
        table.setBackground(Color.WHITE);
        table.setGridColor(COLOR_BORDER);
        table.setSelectionBackground(COLOR_ACCENT.darker());
        table.setSelectionForeground(Color.WHITE);

        JTableHeader header = table.getTableHeader();
        header.setFont(FONT_TABLE_HDR);
        header.setBackground(COLOR_PRIMARY);
        header.setForeground(Color.WHITE);
    }

    private void refreshAllTabs() {
        if (!ensureTournament()) return;
        summaryArea.setText(getTournamentSummary());
        scheduleArea.setText(buildScheduleText());
        tournament.updatePointsTable(pointsTableModel);
        tournament.updateTopScorersAndAwards(topScorersModel);
    }

    private boolean ensureTournament() {
        if (tournament == null) {
            JOptionPane.showMessageDialog(frame, "Please create or load a tournament first.", "No Tournament", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    private String prompt(String msg, String def) {
        String res = JOptionPane.showInputDialog(frame, msg, def);
        return (res == null) ? "" : res.trim();
    }

    private void showMessage(String msg) {
        JOptionPane.showMessageDialog(frame, msg, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(frame, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    // ========== MATCH PLAY (GUI) ==========

    private void playNextMatchWithGUI() {
        List<Match> scheduled = tournament.getScheduledMatches();
        if (scheduled.isEmpty()) {
            showMessage("No more matches!\nTournament is complete!");
            return;
        }
        Match match = scheduled.get(0);
        Team t1 = match.getCurrentTeam1();
        Team t2 = match.getCurrentTeam2();
        if (t1 == null || t2 == null) {
            showMessage("Match not ready yet (waiting for previous results).");
            return;
        }

        String info = "<html><h2>" + match.getMatchName() + "</h2>"
                + "<b>" + t1.getName() + " vs " + t2.getName() + "</b><br>"
                + "Venue: " + (match.getVenue() != null ? match.getVenue().getName() : "TBD") + "</html>";

        String[] options = {"Auto Simulate", "Manual Scoring (GUI)", "Cancel"};
        int choice = JOptionPane.showOptionDialog(frame, info, "Play Match",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

        if (choice == 0) guiAutoSimulate(match);
        else if (choice == 1) guiManualScoring(match);
        else return;

        scheduled.remove(0);
        tournament.getAllMatches().add(match);
        refreshAllTabs();
        showMessage("Match completed!\n" + t1.getName() + " " + match.getScoreTeam1()
                + " - " + match.getScoreTeam2() + " " + t2.getName());
    }

    private void guiAutoSimulate(Match match) {
        JDialog dialog = new JDialog(frame, "Auto Simulating: " + match.getCurrentTeam1().getName() + " vs " + match.getCurrentTeam2().getName(), true);
        dialog.setSize(680, 480);
        dialog.setLocationRelativeTo(frame);
        dialog.getContentPane().setBackground(COLOR_BG);

        JTextArea logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(FONT_MAIN);
        JScrollPane scroll = new JScrollPane(logArea);
        dialog.add(scroll);

        SwingWorker<Void, String> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                for (int min = 1; min <= 45; min += rand.nextInt(10) + 2) {
                    if (min > 45) min = 45;
                    publish("\n--- " + min + "' ---\n");
                    int events = rand.nextInt(4);
                    for (int i = 0; i < events; i++) {
                        String eventTxt = simulateRandomEventGUI(match, min);
                        if (!eventTxt.isEmpty()) publish(eventTxt + "\n");
                    }
                    publish(match.toString() + "\n");
                    Thread.sleep(850); // subtle for user, a bit faster
                }
                match.finishMatch();
                publish("\n=== FULL TIME! ===\n");
                publish(match.toString() + "\n");
                if (match.getManOfTheMatch() != null)
                    publish("Man of the Match: " + match.getManOfTheMatch().getName() + "\n");
                return null;
            }

            @Override
            protected void process(List<String> chunks) {
                for (String text : chunks) {
                    logArea.append(text);
                }
                logArea.setCaretPosition(logArea.getDocument().getLength());
            }

            @Override
            protected void done() { dialog.dispose(); }
        };
        worker.execute();
        dialog.setVisible(true);
    }

    private String simulateRandomEventGUI(Match match, int minute) {
        Team team1 = match.getCurrentTeam1();
        Team team2 = match.getCurrentTeam2();
        Team attacking = rand.nextBoolean() ? team1 : team2;
        Team defending = (attacking == team1) ? team2 : team1;

        Player scorer = getRandomForwardOrMid(attacking);
        Player assist = rand.nextBoolean() ? getRandomMidOrDef(attacking) : null;
        Player gk = getRandomGoalkeeper(defending);

        if (scorer == null) return "";
        int chance = rand.nextInt(100);

        if (chance < 50) { // Goal or saved
            if (rand.nextInt(100) < 38) {
                String eventText = minute + "' ⚽ GOAL! " + scorer.getName();
                if (assist != null) eventText += " (Assist: " + assist.getName() + ")";
                match.addEvent(new MatchEvent(minute, MatchEvent.EventType.GOAL, scorer, assist, eventText));
                return eventText;
            } else {
                String eventText = minute + "' Great save by " + (gk != null ? gk.getName() : "keeper") + "!";
                if (gk != null) match.addEvent(new MatchEvent(minute, MatchEvent.EventType.SAVE, gk, eventText));
                return eventText;
            }
        } else if (chance < 80) {
            Player carded = rand.nextBoolean() ? scorer : getRandomPlayer(defending);
            boolean isRed = rand.nextInt(100) < 13;
            String eventText = minute + "' "
                    + (isRed ? "🔴 RED CARD! " : "🟨 YELLOW CARD! ") + carded.getName();
            match.addEvent(new MatchEvent(minute, isRed ? MatchEvent.EventType.RED_CARD : MatchEvent.EventType.YELLOW_CARD,
                    carded, eventText));
            return eventText;
        }
        return "";
    }

    private Player getRandomForwardOrMid(Team team) {
        List<Player> candidates = team.getPlayers().stream()
                .filter(p -> p.getPosition() == Position.FORWARD || p.getPosition() == Position.MIDFIELDER)
                .toList();
        return candidates.isEmpty() ? (team.getPlayers().isEmpty() ? null : team.getPlayers().get(0)) :
                candidates.get(rand.nextInt(candidates.size()));
    }
    private Player getRandomMidOrDef(Team team) {
        List<Player> candidates = team.getPlayers().stream()
                .filter(p -> p.getPosition() == Position.MIDFIELDER || p.getPosition() == Position.DEFENDER)
                .toList();
        return candidates.isEmpty() ? null : candidates.get(rand.nextInt(candidates.size()));
    }
    private Player getRandomGoalkeeper(Team team) {
        return team.getPlayers().stream()
                .filter(p -> p.getPosition() == Position.GOALKEEPER)
                .findFirst().orElse(null);
    }
    private Player getRandomPlayer(Team team) {
        if (team.getPlayers().isEmpty()) return null;
        return team.getPlayers().get(rand.nextInt(team.getPlayers().size()));
    }

    private void guiManualScoring(Match match) {
        JDialog dialog = new JDialog(frame, "Manual Scoring: " + match.getCurrentTeam1().getName() + " vs " + match.getCurrentTeam2().getName(), true);
        dialog.setSize(700, 540);
        dialog.setLocationRelativeTo(frame);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(COLOR_BG);

        JLabel scoreLabel = new JLabel("", SwingConstants.CENTER);
        scoreLabel.setFont(new Font("SansSerif", Font.BOLD, 32));
        dialog.add(scoreLabel, BorderLayout.NORTH);

        JTextArea eventsArea = new JTextArea();
        eventsArea.setEditable(false);
        eventsArea.setFont(FONT_MAIN);
        JScrollPane eventsScroll = new JScrollPane(eventsArea);
        dialog.add(eventsScroll, BorderLayout.CENTER);

        JPanel controls = new JPanel(new GridLayout(1, 4, 22, 16));
        controls.setBorder(BorderFactory.createEmptyBorder(18, 24, 18, 24));
        controls.setBackground(COLOR_BG);

        JButton goal1 = new JButton("Goal " + match.getCurrentTeam1().getName());
        JButton goal2 = new JButton("Goal " + match.getCurrentTeam2().getName());
        JButton undo = new JButton("Undo");
        JButton finish = new JButton("Finish Match");
        styleButton(goal1); styleButton(goal2); styleButton(undo); styleButton(finish);

        goal1.addActionListener(e -> addGoalGUI(match, match.getCurrentTeam1(), eventsArea, scoreLabel));
        goal2.addActionListener(e -> addGoalGUI(match, match.getCurrentTeam2(), eventsArea, scoreLabel));
        undo.addActionListener(e -> {
            match.undoLastEvent();
            updateScoreAndEvents(match, scoreLabel, eventsArea);
        });
        finish.addActionListener(e -> {
            match.finishMatch();
            dialog.dispose();
        });

        controls.add(goal1); controls.add(goal2); controls.add(undo); controls.add(finish);
        dialog.add(controls, BorderLayout.SOUTH);

        updateScoreAndEvents(match, scoreLabel, eventsArea);
        dialog.setVisible(true);
    }

    private void addGoalGUI(Match match, Team scoringTeam, JTextArea eventsArea, JLabel scoreLabel) {
        List<Player> players = scoringTeam.getPlayers();
        if (players.isEmpty()) return;
        String[] names = players.stream().map(Player::getName).toArray(String[]::new);

        String scorerName = (String) JOptionPane.showInputDialog(frame, "Select goal scorer for " + scoringTeam.getName(),
                "Goal Scorer", JOptionPane.QUESTION_MESSAGE, null, names, names[0]);
        if (scorerName == null) return;
        Player scorer = players.stream().filter(p -> p.getName().equals(scorerName)).findFirst().orElse(null);

        String assistName = (String) JOptionPane.showInputDialog(frame, "Assist player (optional, cancel for none)",
                "Assist", JOptionPane.QUESTION_MESSAGE, null, names, null);
        Player assist = (assistName == null) ? null : players.stream().filter(p -> p.getName().equals(assistName)).findFirst().orElse(null);

        String desc = scorer.getName() + " scores!" + (assist != null ? " (Assist: " + assist.getName() + ")" : "");
        match.addEvent(new MatchEvent(0, MatchEvent.EventType.GOAL, scorer, assist, desc));
        updateScoreAndEvents(match, scoreLabel, eventsArea);
    }

    private void updateScoreAndEvents(Match match, JLabel scoreLabel, JTextArea eventsArea) {
        scoreLabel.setText("<html><h1>" + match.getCurrentTeam1().getName() + " " + match.getScoreTeam1() +
                " - " + match.getScoreTeam2() + " " + match.getCurrentTeam2().getName() + "</h1></html>");
        eventsArea.setText("");
        for (MatchEvent e : match.getAllEvents()) eventsArea.append(e.getDescription() + "\n");
        eventsArea.setCaretPosition(eventsArea.getDocument().getLength());
    }

    // ========== TOURNAMENT CREATION and LOAD/SAVE ==========

    private void createNewTournament() {
        String name = prompt("Tournament name:", "My Tournament");
        if (name.isEmpty()) { showError("Name cannot be empty!"); return; }

        String[] types = {"Round Robin", "Double Round Robin", "Knockout"};
        int tSel = JOptionPane.showOptionDialog(frame, "Select Tournament Type", "Type",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, types, types[0]);
        TournamentType type = switch (tSel) {
            case 1 -> TournamentType.DOUBLE_ROUND_ROBIN;
            case 2 -> TournamentType.KNOCKOUT;
            default -> TournamentType.ROUND_ROBIN;
        };

        tournament = new Tournament(name, Sport.FOOTBALL, type);
        addVenues();
        createTeams();
        ScheduleGenerator.generateSchedule(tournament);

        showMessage("Tournament '" + name + "' created and schedule generated!");
        refreshAllTabs();
    }

    private void addVenues() {
        while (true) {
            String vName = prompt("Venue name (blank to finish)", "");
            if (vName.isEmpty()) {
                if (tournament.getVenues().isEmpty()) {
                    tournament.addVenue(new Venue("Default Stadium", "City", 50000));
                    showMessage("Default venue added.");
                }
                break;
            }
            String city = prompt("City for " + vName, "City");
            int cap = 50000;
            try { cap = Integer.parseInt(prompt("Capacity", "50000")); } catch (Exception ignored) { }
            tournament.addVenue(new Venue(vName, city, cap));
        }
    }

    private void createTeams() {
        while (true) {
            String tName = prompt("Team name (blank to finish)", "");
            if (tName.isEmpty()) break;
            String coach = prompt("Coach Name", "Coach");
            Team team = new Team(tName, coach);

            int num = 11;
            try { num = Math.max(1, Math.min(22, Integer.parseInt(prompt("Number of Players (1-22)", "11")))); }
            catch (Exception ignored) {}

            for (int i = 0; i < num; i++) {
                String pName = prompt("Player " + (i + 1) + " name", "Player " + (i + 1));
                int age = 25;
                try { age = Integer.parseInt(prompt("Age (15-45)", "25")); } catch (Exception ignored) {}
                int posChoice = JOptionPane.showOptionDialog(frame, "Position for " + pName, "Select Position",
                        JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                        null, new String[]{"Goalkeeper", "Defender", "Midfielder", "Forward"}, "Midfielder");
                Position pos = switch (posChoice) {
                    case 0 -> Position.GOALKEEPER;
                    case 1 -> Position.DEFENDER;
                    case 2 -> Position.MIDFIELDER;
                    case 3 -> Position.FORWARD;
                    default -> Position.MIDFIELDER;
                };
                team.addPlayer(new Player(pName, age, pos));
            }
            if (!team.getPlayers().isEmpty()) {
                team.setCaptain(team.getPlayers().get(0));
                team.setFormation(Formation.F_4_3_3);
            }
            tournament.addTeam(team);
        }
        if (tournament.getTeams().size() < 2) {
            showError("At least 2 teams required.");
            createTeams();
        }
    }

    private void loadTournament() {
        JFileChooser chooser = new JFileChooser(".");
        chooser.setFileFilter(new FileNameExtensionFilter("Tournament files (*.dat)", "dat"));
        if (chooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            Tournament loaded = Tournament.loadTournament(file.getAbsolutePath());
            if (loaded != null) {
                tournament = loaded;
                showMessage("Tournament loaded: " + tournament.getName());
                refreshAllTabs();
            } else {
                showError("Failed to load tournament.");
            }
        }
    }

    private void saveTournament() {
        String baseName = tournament.getName().replaceAll("[^a-zA-Z0-9]", "_");
        String suggest = "Tournament_" + baseName + "_" + LocalDate.now() + ".dat";
        JFileChooser chooser = new JFileChooser(".");
        chooser.setSelectedFile(new File(suggest));
        chooser.setFileFilter(new FileNameExtensionFilter("Tournament files (*.dat)", "dat"));
        if (chooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            if (!file.getName().endsWith(".dat")) file = new File(file.getAbsolutePath() + ".dat");
            tournament.saveTournament(file.getAbsolutePath());
            showMessage("Tournament saved successfully!");
        }
    }

    private String getTournamentSummary() {
        if (tournament == null) return "No tournament loaded.";
        return String.format(
                "TOURNAMENT: %s\n" +
                        "Type: %s\n" +
                        "Teams: %d\n" +
                        "Venues: %d\n" +
                        "Matches Played: %d\n" +
                        "Matches Remaining: %d",
                tournament.getName(),
                tournament.getType(),
                tournament.getTeams().size(),
                tournament.getVenues().size(),
                tournament.getAllMatches().size(),
                tournament.getScheduledMatches().size()
        );
    }

    private String buildScheduleText() {
        StringBuilder sb = new StringBuilder("=== REMAINING SCHEDULE ===\n\n");
        if (tournament.getScheduledMatches().isEmpty()) sb.append("🏆 TOURNAMENT COMPLETE! 🏆\n");
        for (Match m : tournament.getScheduledMatches()) {
            String name = m.getMatchName().isEmpty() ? "" : m.getMatchName() + ":\n";
            String t1 = m.getCurrentTeam1() != null ? m.getCurrentTeam1().getName() : "TBD";
            String t2 = m.getCurrentTeam2() != null ? m.getCurrentTeam2().getName() : "TBD";
            String venue = m.getVenue() != null ? m.getVenue().getName() : "TBD";
            sb.append(name).append(t1).append("  vs  ").append(t2).append("  @  ").append(venue).append("\n\n");
        }
        return sb.toString();
    }
}