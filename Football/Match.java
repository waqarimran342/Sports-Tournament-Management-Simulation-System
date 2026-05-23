package Football;

import java.io.Serializable;
import java.util.*;

public class Match implements Serializable {

    // Teams
    private Team team1;
    private Team team2;

    // Placeholder teams (used in knockout rounds)
    private Team placeholderTeam1;
    private Team placeholderTeam2;

    // Football.Match info
    private Venue venue;
    private Date date;
    private int scoreTeam1 = 0;
    private int scoreTeam2 = 0;
    private boolean isCompleted = false;
    private String matchName = "";

    // Awards
    private Player manOfTheMatch;

    // Knockout tree link
    private Match winnerGoesTo;

    // DSA: Stack (undo) + List (full replay)
    private final Stack<MatchEvent> eventHistory = new Stack<>();
    private final List<MatchEvent> allEvents = new ArrayList<>();

    // Constructor
    public Match(Team team1, Team team2, Venue venue, Date date) {
        this.team1 = team1;
        this.team2 = team2;
        this.placeholderTeam1 = team1;
        this.placeholderTeam2 = team2;
        this.venue = venue;
        this.date = date;
    }

    // ================= EVENTS =================

    public void addEvent(MatchEvent event) {
        allEvents.add(event);
        eventHistory.push(event);

        if (event.getType() == MatchEvent.EventType.GOAL) {
            Player scorer = event.getPlayer();
            if (getCurrentTeam1().getPlayers().contains(scorer)) {
                scoreTeam1++;
            } else {
                scoreTeam2++;
            }
            scorer.addGoal();
            if (event.getAssistPlayer() != null)
                event.getAssistPlayer().addAssist();
        }

        if (event.getType() == MatchEvent.EventType.YELLOW_CARD)
            event.getPlayer().addYellowCard();

        if (event.getType() == MatchEvent.EventType.RED_CARD)
            event.getPlayer().addRedCard();

        if (event.getType() == MatchEvent.EventType.SAVE &&
                event.getPlayer().getPosition().getShortForm().equals("GK")) {
            event.getPlayer().addSave();
        }
    }

    // Undo last event (STACK)
    public void undoLastEvent() {
        if (eventHistory.isEmpty()) {
            System.out.println("Nothing to undo!");
            return;
        }

        MatchEvent last = eventHistory.pop();
        allEvents.remove(allEvents.size() - 1);

        if (last.getType() == MatchEvent.EventType.GOAL) {
            Player scorer = last.getPlayer();
            if (getCurrentTeam1().getPlayers().contains(scorer)) {
                scoreTeam1 = Math.max(0, scoreTeam1 - 1);
            } else {
                scoreTeam2 = Math.max(0, scoreTeam2 - 1);
            }
        }

        System.out.println("UNDO → " + last.getDescription());
    }

    // ================= FINISH MATCH =================

    public void finishMatch() {
        isCompleted = true;
        updateTeamStats();
        selectManOfTheMatch();
        propagateWinnerIfKnockout();
    }

    private void updateTeamStats() {
        Team t1 = getCurrentTeam1();
        Team t2 = getCurrentTeam2();

        t1.addGoalsScored(scoreTeam1);
        t1.addGoalsConceded(scoreTeam2);
        t2.addGoalsScored(scoreTeam2);
        t2.addGoalsConceded(scoreTeam1);

        if (scoreTeam1 > scoreTeam2) {
            t1.addWin(); t2.addLoss();
        } else if (scoreTeam2 > scoreTeam1) {
            t2.addWin(); t1.addLoss();
        } else {
            t1.addDraw(); t2.addDraw();
        }
    }

    private void selectManOfTheMatch() {
        PriorityQueue<Player> pq =
                new PriorityQueue<>(Collections.reverseOrder());

        getCurrentTeam1().getPlayers().forEach(pq::offer);
        getCurrentTeam2().getPlayers().forEach(pq::offer);

        manOfTheMatch = pq.isEmpty() ? null : pq.poll();
    }

    public Player getManOfTheMatch() {
        return manOfTheMatch;
    }


// ================= KNOCKOUT =================

    private void propagateWinnerIfKnockout() {
        if (winnerGoesTo == null) return;

        Team winner =
                scoreTeam1 > scoreTeam2 ? getCurrentTeam1() :
                        scoreTeam2 > scoreTeam1 ? getCurrentTeam2() : null;

        if (winner == null) {
            System.out.println("Draw! Penalty shootout needed.");
            return;
        }

        if (winnerGoesTo.placeholderTeam1 == null) {
            winnerGoesTo.setPlaceholderTeam1(winner);
        } else {
            winnerGoesTo.setPlaceholderTeam2(winner);
        }

        System.out.println(
                "Winner: " + winner.getName() +
                        " → advances to " + winnerGoesTo.getMatchName()
        );
    }

    // ================= HELPERS =================

    public Team getCurrentTeam1() {
        return placeholderTeam1 != null ? placeholderTeam1 : team1;
    }

    public Team getCurrentTeam2() {
        return placeholderTeam2 != null ? placeholderTeam2 : team2;
    }

    public void setPlaceholderTeam1(Team t) {
        placeholderTeam1 = t;
        team1 = t;
    }

    public void setPlaceholderTeam2(Team t) {
        placeholderTeam2 = t;
        team2 = t;
    }

    public void setWinnerGoesTo(Match m) {
        winnerGoesTo = m;
    }

    public Match getWinnerGoesTo() {
        return winnerGoesTo;
    }

    public void setMatchName(String name) {
        matchName = name;
    }

    public String getMatchName() {
        return matchName.isEmpty() ? toString() : matchName;
    }

    public List<MatchEvent> getAllEvents() {
        return Collections.unmodifiableList(allEvents);
    }

    // ================= DISPLAY =================

    @Override
    public String toString() {
        String t1 = getCurrentTeam1() != null ? getCurrentTeam1().getName() : "TBD";
        String t2 = getCurrentTeam2() != null ? getCurrentTeam2().getName() : "TBD";
        return String.format(
                "%s %d - %d %s | %s | %s",
                t1, scoreTeam1, scoreTeam2, t2,
                venue != null ? venue.getName() : "?",
                isCompleted ? "FT" : "Scheduled"
        );
    }

    public Venue getVenue() {
        return venue;
    }

    public void setVenue(Venue venue) {
        this.venue = venue;
    }
    public int getScoreTeam1() {
        return scoreTeam1;
    }

    public int getScoreTeam2() {
        return scoreTeam2;
    }
}
