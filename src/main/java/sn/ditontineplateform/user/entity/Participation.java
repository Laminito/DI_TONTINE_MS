package sn.ditontineplateform.user.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Entité représentant la participation d'un utilisateur à une tontine. Contient les informations sur la participation,
 * le statut et les performances.
 *
 * @author DiTontine Team
 * @version 1.0
 * @since 2025-01-01
 */
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"paiements", "jackpotsRecus"})
@ToString(callSuper = true, exclude = {"paiements", "jackpotsRecus"})
@Entity
@Table(
        name = "participations", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"participant_id", "tontine_id"})
}
)
public class Participation extends BaseEntity {

    /**
     * Statut de la participation.
     */
    public enum StatutParticipation {
        EN_ATTENTE,
        ACTIVE,
        SUSPENDUE,
        TERMINEE,
        EXCLUE
    }

    /**
     * Participant à la tontine.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participant_id", nullable = false)
    private User participant;

    /**
     * Tontine à laquelle l'utilisateur participe.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tontine_id", nullable = false)
    private Tontine tontine;

    /**
     * Statut de la participation.
     */
    @NotNull(message = "Le statut de participation est obligatoire")
    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false)
    private StatutParticipation statut = StatutParticipation.EN_ATTENTE;

    /**
     * Date de demande de participation.
     */
    @Column(name = "date_demande", nullable = false)
    private LocalDateTime dateDemande = LocalDateTime.now();

    /**
     * Date d'acceptation de la participation.
     */
    @Column(name = "date_acceptation")
    private LocalDateTime dateAcceptation;

    /**
     * Montant de cotisation pour cette participation (pour les tontines événementielles).
     */
    @DecimalMin(value = "0.0", message = "Le montant de cotisation ne peut pas être négatif")
    @Column(name = "montant_cotisation", precision = 15, scale = 2)
    private BigDecimal montantCotisation;

    /**
     * Position dans l'ordre de tirage (pour les tontines sélectives).
     */
    @Min(value = 1, message = "La position doit être positive")
    @Column(name = "position_tirage")
    private Integer positionTirage;

    /**
     * Nombre de paiements effectués.
     */
    @Min(value = 0, message = "Le nombre de paiements ne peut pas être négatif")
    @Column(name = "nombre_paiements_effectues", nullable = false)
    private Integer nombrePaiementsEffectues = 0;

    /**
     * Nombre de paiements en retard.
     */
    @Min(value = 0, message = "Le nombre de paiements en retard ne peut pas être négatif")
    @Column(name = "nombre_paiements_retard", nullable = false)
    private Integer nombrePaiementsRetard = 0;

    /**
     * Montant total payé par le participant.
     */
    @DecimalMin(value = "0.0", message = "Le montant total payé ne peut pas être négatif")
    @Column(name = "montant_total_paye", precision = 15, scale = 2, nullable = false)
    private BigDecimal montantTotalPaye = BigDecimal.ZERO;

    /**
     * Montant total des pénalités.
     */
    @DecimalMin(value = "0.0", message = "Le montant des pénalités ne peut pas être négatif")
    @Column(name = "montant_penalites", precision = 15, scale = 2, nullable = false)
    private BigDecimal montantPenalites = BigDecimal.ZERO;

    /**
     * Indicateur si le participant a déjà reçu un jackpot.
     */
    @Column(name = "a_recu_jackpot", nullable = false)
    private Boolean aRecuJackpot = false;

    /**
     * Score de performance du participant dans cette tontine.
     */
    @DecimalMin(value = "0.0", message = "Le score de performance ne peut pas être négatif")
    @DecimalMax(value = "100.0", message = "Le score de performance ne peut pas dépasser 100")
    @Column(name = "score_performance", precision = 5, scale = 2)
    private BigDecimal scorePerformance = BigDecimal.valueOf(100.0);

    /**
     * Commentaire de l'administrateur sur la participation.
     */
    @Size(max = 500, message = "Le commentaire ne peut pas dépasser 500 caractères")
    @Column(name = "commentaire_admin", length = 500)
    private String commentaireAdmin;

    /**
     * Indicateur si la participation est favorite.
     */
    @Column(name = "is_favorite", nullable = false)
    private Boolean isFavorite = false;

    /**
     * Paiements effectués par le participant.
     */
    @OneToMany(mappedBy = "participation", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Paiement> paiements = new HashSet<>();

    /**
     * Jackpots reçus par le participant.
     */
    @OneToMany(mappedBy = "beneficiaire", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Jackpot> jackpotsRecus = new HashSet<>();

    /**
     * Vérifie si la participation est active.
     *
     * @return true si la participation est active
     */
    public boolean isActive () {
        return StatutParticipation.ACTIVE.equals(this.statut);
    }

    /**
     * Vérifie si la participation est en attente.
     *
     * @return true si la participation est en attente
     */
    public boolean isEnAttente () {
        return StatutParticipation.EN_ATTENTE.equals(this.statut);
    }

    /**
     * Calcule le pourcentage de ponctualité du participant.
     *
     * @return le pourcentage de ponctualité
     */
    public BigDecimal getPourcentagePonctualite () {
        if (nombrePaiementsEffectues == 0) {
            return BigDecimal.valueOf(100.0);
        }

        int paiementsATemps = nombrePaiementsEffectues - nombrePaiementsRetard;
        return BigDecimal.valueOf(paiementsATemps * 100.0 / nombrePaiementsEffectues);
    }

    /**
     * Vérifie si le participant peut recevoir un jackpot.
     *
     * @return true si le participant peut recevoir un jackpot
     */
    public boolean peutRecevoirJackpot () {
        return isActive() && !aRecuJackpot && scorePerformance.compareTo(BigDecimal.valueOf(50.0)) >= 0;
    }

    /**
     * Met à jour le score de performance basé sur la ponctualité.
     */
    public void mettreAJourScorePerformance () {
        BigDecimal ponctualite = getPourcentagePonctualite();

        // Score basé sur la ponctualité et les pénalités
        BigDecimal scoreBase = ponctualite;
        if (montantPenalites.compareTo(BigDecimal.ZERO) > 0) {
            scoreBase = scoreBase.subtract(BigDecimal.valueOf(10.0));
        }

        this.scorePerformance = scoreBase.max(BigDecimal.ZERO).min(BigDecimal.valueOf(100.0));
    }

    /**
     * Calcule le montant total dû par le participant.
     *
     * @return le montant total dû
     */
    public BigDecimal getMontantTotalDu () {
        if (tontine.isEvenementielle()) {
            return montantCotisation != null ? montantCotisation : BigDecimal.ZERO;
        } else {
            // Pour les tontines classiques, calculer selon le nombre de cycles
            return tontine.getMontantCotisation().multiply(BigDecimal.valueOf(tontine.getNombreParticipants()));
        }
    }

    /**
     * Calcule le montant restant à payer.
     *
     * @return le montant restant à payer
     */
    public BigDecimal getMontantRestantAPayer () {
        return getMontantTotalDu().subtract(montantTotalPaye);
    }

    /**
     * Vérifie si la participation est à jour dans ses paiements.
     *
     * @return true si la participation est à jour
     */
    public boolean isAJourPaiements () {
        return getMontantRestantAPayer().compareTo(BigDecimal.ZERO) <= 0;
    }

}