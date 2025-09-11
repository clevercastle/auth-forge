package org.clevercastle.authforge.challenge;

public class ChangeMfaSolutionChallengeAnswer {
    private String sessionId;
    private MfaChallengeSolution solution;

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public MfaChallengeSolution getSolution() {
        return solution;
    }

    public void setSolution(MfaChallengeSolution solution) {
        this.solution = solution;
    }
}
