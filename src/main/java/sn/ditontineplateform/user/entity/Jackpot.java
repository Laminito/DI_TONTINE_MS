package sn.ditontineplateform.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entité représentant un jackpot dans une tontine. Un jackpot est la somme qu'un participant reçoit lors de son tour.
 *
 * @author DiTontine Team
 * @version 1.0
 * @since 2025-01-01
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Entity
@Table(name = "jackpots")
public class Jackpot extends BaseEntity {

    /**
     * Statut du jackpot.
     */
    public enum StatutJackpot {
        PROGRAMME,
        ACTIVE,
        DISTRIBUE,
        REPORTE,
        ANNULE
    }

    /**
     * Tontine à laquelle appartient le jackpot.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tontine_id", nullable = false)
    private Tontine tontine;

    /**
     * Bénéficiaire du jackpot.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "beneficiaire_id", nullable = false)
    private Participation beneficiaire;

    /**
     * Numéro du cycle dans la tontine.
     */
    @NotNull(message = "Le numéro de cycle est obligatoire")
    @Min(value = 1, message = "Le numéro de cycle doit être positif")
    @Column(name = "numero_cycle", nullable = false)
    private Integer numeroCycle;

    /**
     * Montant du jackpot.
     */
    @NotNull(message = "Le montant du jackpot est obligatoire")
    @DecimalMin(value = "0.0", message = "Le montant du jackpot ne peut pas être négatif")
    @Column(name = "montant", precision = 15, scale = 2, nullable = false)
    private BigDecimal montant;

    /**
     * Date prévue de distribution.
     */
    @NotNull(message = "La date de distribution est obligatoire")
    @Column(name = "date_distribution", nullable = false)
    private LocalDate dateDistribution;

    /**
     * Date effective de distribution.
     */
    @Column(name = "date_distribution_effective")
    private LocalDateTime dateDistributionEffective;

    /**
     * Statut du jackpot.
     */
    @NotNull(message = "Le statut du jackpot est obligatoire")
    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false)
    private StatutJackpot statut = StatutJackpot.PROGRAMME;

    /**
     * Montant des frais de gestion prélevés.
     */
    @DecimalMin(value = "0.0", message = "Les frais de gestion ne peuvent pas être négatifs")
    @Column(name = "frais_gestion", precision = 15, scale = 2)
    private BigDecimal fraisGestion = BigDecimal.ZERO;

    /**
     * Montant des pénalités déduites.
     */
    @DecimalMin(value = "0.0", message = "Les pénalités ne peuvent pas être négatives")
    @Column(name = "penalites_deduites", precision = 15, scale = 2)
    private BigDecimal penalitesDeduites = BigDecimal.ZERO;

    /**
     * Montant net reçu par le bénéficiaire.
     */
    @DecimalMin(value = "0.0", message = "Le montant net ne peut pas être négatif")
    @Column(name = "montant_net", precision = 15, scale = 2)
    private BigDecimal montantNet;

    /**
     * Méthode de distribution (VIREMENT, MOBILE_MONEY, ESPECES).
     */
    @Column(name = "methode_distribution", length = 50)
    private String methodeDistribution;

    /**
     * Référence de la transaction de distribution.
     */
    @Size(max = 100, message = "La référence de transaction ne peut pas dépasser 100 caractères")
    @Column(name = "reference_transaction", length = 100)
    private String referenceTransaction;

    /**
     * Commentaire sur la distribution.
     */
    @Size(max = 500, message = "Le commentaire ne peut pas dépasser 500 caractères")
    @Column(name = "commentaire", length = 500)
    private String commentaire;

    /**
     * Indicateur si le jackpot est prioritaire.
     */
    @Column(name = "is_prioritaire", nullable = false)
    private Boolean isPrioritaire = false;

    /**
     * Nombre de jours avant distribution pour notification.
     */
    @Min(value = 1, message = "Le nombre de jours de notification doit être positif")
    @Column(name = "jours_notification", nullable = false)
    private Integer joursNotification = 7;

    /**
     * Vérifie si le jackpot est programmé.
     *
     * @return true si le jackpot est programmé
     */
    public boolean isProgramme () {
        return StatutJackpot.PROGRAMME.equals(this.statut);
    }

    /**
     * Vérifie si le jackpot est actif.
     *
     * @return true si le jackpot est actif
     */
    public boolean isActif () {
        return StatutJackpot.ACTIVE.equals(this.statut);
    }

    /**
     * Vérifie si le jackpot est distribué.
     *
     * @return true si le jackpot est distribué
     */
    public boolean isDistribue () {
        return StatutJackpot.DISTRIBUE.equals(this.statut);
    }

    /**
     * Vérifie si le jackpot doit être notifié.
     *
     * @return true si le jackpot doit être notifié
     */
    public boolean doitEtreNotifie () {
        return isActif() && LocalDate.now().isAfter(dateDistribution.minusDays(joursNotification));
    }

    /**
     * Vérifie si le jackpot est en retard.
     *
     * @return true si le jackpot est en retard
     */
    public boolean isEnRetard () {
        return (isProgramme() || isActif()) && LocalDate.now().isAfter(dateDistribution);
    }

    /**
     * Calcule le montant net après déduction des frais et pénalités.
     *
     * @return le montant net
     */
    public BigDecimal calculerMontantNet () {
        BigDecimal montantNet = montant.subtract(fraisGestion).subtract(penalitesDeduites);
        return montantNet.max(BigDecimal.ZERO);
    }

    /**
     * Marque le jackpot comme distribué.
     *
     * @param methode   la méthode de distribution
     * @param reference la référence de la transaction
     */
    public void marquerCommeDistribue (String methode, String reference) {
        this.statut = StatutJackpot.DISTRIBUE;
        this.dateDistributionEffective = LocalDateTime.now();
        this.methodeDistribution = methode;
        this.referenceTransaction = reference;
        this.montantNet = calculerMontantNet();

        // Marquer la participation comme ayant reçu un jackpot
        if (beneficiaire != null) {
            beneficiaire.setARecuJackpot(true);
        }
    }

    /**
     * Active le jackpot.
     */
    public void activer () {
        if (isProgramme()) {
            this.statut = StatutJackpot.ACTIVE;
        }
    }

    /**
     * Reporte le jackpot à une nouvelle date.
     *
     * @param nouvelleDate la nouvelle date de distribution
     * @param raison       la raison du report
     */
    public void reporter (LocalDate nouvelleDate, String raison) {
        this.statut = StatutJackpot.REPORTE;
        this.dateDistribution = nouvelleDate;
        this.commentaire = raison;
    }

    /**
     * Annule le jackpot.
     *
     * @param raison la raison de l'annulation
     */
    public void annuler (String raison) {
        this.statut = StatutJackpot.ANNULE;
        this.commentaire = raison;
    }

    /**
     * Calcule le pourcentage de prélèvement (frais + pénalités).
     *
     * @return le pourcentage de prélèvement
     */
    public BigDecimal getPourcentagePrelevement () {
        if (montant.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal totalPrelevement = fraisGestion.add(penalitesDeduites);
        return totalPrelevement.multiply(BigDecimal.valueOf(100)).divide(montant, 2, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * Validation avant persistance.
     */
    @PrePersist
    @PreUpdate
    private void validateJackpot () {
        if (montantNet == null) {
            this.montantNet = calculerMontantNet();
        }

        if (fraisGestion.add(penalitesDeduites).compareTo(montant) > 0) {
            throw new IllegalArgumentException("Les frais et pénalités ne peuvent pas dépasser le montant du jackpot");
        }
    }
}