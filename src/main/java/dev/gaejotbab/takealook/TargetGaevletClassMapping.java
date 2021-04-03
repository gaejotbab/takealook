package dev.gaejotbab.takealook;

import dev.gaejotbab.gaevlet.Gaevlet;

public class TargetGaevletClassMapping {
    public enum MatchingRule {
        PREFIX,
        EXACT,
        ;
    }

    private final String target;
    private final MatchingRule matchingRule;
    private final Class<? extends Gaevlet> gaevletClass;

    public TargetGaevletClassMapping(String target, MatchingRule matchingRule, Class<? extends Gaevlet> gaevletClass) {
        this.target = target;
        this.matchingRule = matchingRule;
        this.gaevletClass = gaevletClass;
    }

    public static TargetGaevletClassMapping of(String target, MatchingRule matchingRule, Class<? extends Gaevlet> gaevletClass) {
        return new TargetGaevletClassMapping(target, matchingRule, gaevletClass);
    }

    public String target() {
        return target;
    }

    public MatchingRule matchingRule() {
        return matchingRule;
    }

    public Class<? extends Gaevlet> gaevletClass() {
        return gaevletClass;
    }
}
