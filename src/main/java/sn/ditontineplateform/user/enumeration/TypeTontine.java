package sn.ditontineplateform.user.enumeration;

/**
 * Type de tontine (CLASSIQUE, EVENEMENTIELLE).
 */
public enum TypeTontine {
    /**
     * Tontine classique avec cotisations fixes et jackpots rotatifs
     */
    CLASSIQUE,

    /**
     * Tontine pour événements spécifiques avec cotisations variables
     */
    EVENEMENTIELLE,

    /**
     * Tontine pour jackpot groupé
     */
    GROUPEE
}