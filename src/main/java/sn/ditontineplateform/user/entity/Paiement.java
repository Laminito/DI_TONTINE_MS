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
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import sn.ditontineplateform.user.enumeration.MethodePaiement;
import sn.ditontineplateform.user.enumeration.StatutPaiement;
import sn.ditontineplateform.user.enumeration.TypePaiement;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entité représentant un paiement effectué par un participant à une tontine. Contient les informations sur le montant,
 * la méthode de paiement et le statut.
 *
 * @author DiTontine Team
 * @version 1.0
 * @since 2025-01-01
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Entity
@Table(name = "paiements")
public class Paiement extends BaseEntity {

    /**
     * Participation associée au paiement.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participation_id", nullable = false)
    private Participation participation;

    /**
     * Tontine associée au paiement.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tontine_id", nullable = false)
    private Tontine tontine;

    /**
     * Montant du paiement.
     */
    @NotNull(message = "Le montant du paiement est obligatoire")
    @DecimalMin(value = "0.01", message = "Le montant doit être supérieur à 0")
    @Column(name = "montant", precision = 15, scale = 2, nullable = false)
    private BigDecimal montant;

    /**
     * Méthode de paiement utilisée.
     */
    @NotNull(message = "La méthode de paiement est obligatoire")
    @Enumerated(EnumType.STRING)
    @Column(name = "methode_paiement", nullable = false)
    private MethodePaiement methodePaiement;

    /**
     * Type de paiement.
     */
    @NotNull(message = "Le type de paiement est obligatoire")
    @Enumerated(EnumType.STRING)
    @Column(name = "type_paiement", nullable = false)
    private TypePaiement typePaiement;

    /**
     * Statut du paiement.
     */
    @NotNull(message = "Le statut du paiement est obligatoire")
    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false)
    private StatutPaiement statut = StatutPaiement.EN_ATTENTE;

    /**
     * Date d'échéance du paiement.
     */
    @Column(name = "date_echeance")
    private LocalDate dateEcheance;

    /**
     * Date effective du paiement.
     */
    @Column(name = "date_paiement")
    private LocalDateTime datePaiement;

    /**
     * Référence de la transaction.
     */
    @Size(max = 100, message = "La référence de transaction ne peut pas dépasser 100 caractères")
    @Column(name = "reference_transaction", length = 100)
    private String referenceTransaction;

    /**
     * Numéro du cycle concerné.
     */
    @Min(value = 1, message = "Le numéro de cycle doit être positif")
    @Column(name = "numero_cycle")
    private Integer numeroCycle;

    /**
     * Montant de la pénalité associée.
     */
    @DecimalMin(value = "0.0", message = "Le montant de la pénalité ne peut pas être négatif")
    @Column(name = "montant_penalite", precision = 15, scale = 2)
    private BigDecimal montantPenalite = BigDecimal.ZERO;

    /**
     * Nombre de jours de retard.
     */
    @Min(value = 0, message = "Le nombre de jours de retard ne peut pas être négatif")
    @Column(name = "jours_retard")
    private Integer joursRetard = 0;

    /**
     * Commentaire sur le paiement.
     */
    @Size(max = 500, message = "Le commentaire ne peut pas dépasser 500 caractères")
    @Column(name = "commentaire", length = 500)
    private String commentaire;

    /**
     * Numéro de téléphone pour Mobile Money.
     */
    @Pattern(regexp = "^(\\+221|00221)?[0-9]{9}$", message = "Format de téléphone invalide")
    @Column(name = "numero_telephone", length = 20)
    private String numeroTelephone;

    /**
     * Nom du titulaire du compte.
     */
    @Size(max = 100, message = "Le nom du titulaire ne peut pas dépasser 100 caractères")
    @Column(name = "nom_titulaire", length = 100)
    private String nomTitulaire;

    /**
     * Frais de transaction.
     */
    @DecimalMin(value = "0.0", message = "Les frais de transaction ne peuvent pas être négatifs")
    @Column(name = "frais_transaction", precision = 15, scale = 2)
    private BigDecimal fraisTransaction = BigDecimal.ZERO;

    /**
     * Indicateur si le paiement est automatique.
     */
    @Column(name = "is_automatique", nullable = false)
    private Boolean isAutomatique = false;

    /**
     * Tentative de paiement (pour les paiements automatiques).
     */
    @Min(value = 1, message = "La tentative doit être positive")
    @Column(name = "tentative")
    private Integer tentative = 1;

    /**
     * Vérifie si le paiement est confirmé.
     *
     * @return true si le paiement est confirmé
     */
    public boolean isConfirme () {
        return StatutPaiement.CONFIRME.equals(this.statut);
    }

    /**
     * Vérifie si le paiement est en attente.
     *
     * @return true si le paiement est en attente
     */
    public boolean isEnAttente () {
        return StatutPaiement.EN_ATTENTE.equals(this.statut);
    }

    /**
     * Vérifie si le paiement a échoué.
     *
     * @return true si le paiement a échoué
     */
    public boolean isEchoue () {
        return StatutPaiement.ECHOUE.equals(this.statut);
    }

    /**
     * Vérifie si le paiement est en retard.
     *
     * @return true si le paiement est en retard
     */
    public boolean isEnRetard () {
        return dateEcheance != null && LocalDate.now().isAfter(dateEcheance) && !isConfirme();
    }

    /**
     * Calcule le nombre de jours de retard.
     *
     * @return le nombre de jours de retard
     */
    public int calculerJoursRetard () {
        if (dateEcheance == null || !isEnRetard()) {
            return 0;
        }

        LocalDate dateReference = isConfirme() ? datePaiement.toLocalDate() : LocalDate.now();
        return (int) java.time.temporal.ChronoUnit.DAYS.between(dateEcheance, dateReference);
    }

    /**
     * Calcule le montant de la pénalité de retard.
     *
     * @return le montant de la pénalité
     */
    public BigDecimal calculerPenaliteRetard () {
        if (!isEnRetard() || tontine.getPenaliteRetard() == null) {
            return BigDecimal.ZERO;
        }

        int joursRetardCalcule = calculerJoursRetard();
        int delaiGrace = tontine.getDelaiGraceJours() != null ? tontine.getDelaiGraceJours() : 0;

        if (joursRetardCalcule <= delaiGrace) {
            return BigDecimal.ZERO;
        }

        return tontine.getPenaliteRetard().multiply(BigDecimal.valueOf(joursRetardCalcule - delaiGrace));
    }

    /**
     * Confirme le paiement.
     *
     * @param referenceTransaction la référence de la transaction
     */
    public void confirmer (String referenceTransaction) {
        this.statut = StatutPaiement.CONFIRME;
        this.datePaiement = LocalDateTime.now();
        this.referenceTransaction = referenceTransaction;
        this.joursRetard = calculerJoursRetard();
        this.montantPenalite = calculerPenaliteRetard();

        // Mettre à jour les statistiques de la participation
        if (participation != null) {
            participation.setNombrePaiementsEffectues(participation.getNombrePaiementsEffectues() + 1);
            participation.setMontantTotalPaye(participation.getMontantTotalPaye().add(montant));
            participation.setMontantPenalites(participation.getMontantPenalites().add(montantPenalite));

            if (joursRetard > 0) {
                participation.setNombrePaiementsRetard(participation.getNombrePaiementsRetard() + 1);
            }

            participation.mettreAJourScorePerformance();
        }
    }

    /**
     * Marque le paiement comme échoué.
     *
     * @param raison la raison de l'échec
     */
    public void marquerEchoue (String raison) {
        this.statut = StatutPaiement.ECHOUE;
        this.commentaire = raison;
    }

    /**
     * Annule le paiement.
     *
     * @param raison la raison de l'annulation
     */
    public void annuler (String raison) {
        this.statut = StatutPaiement.ANNULE;
        this.commentaire = raison;
    }

    /**
     * Calcule le montant total incluant les frais et pénalités.
     *
     * @return le montant total
     */
    public BigDecimal getMontantTotal () {
        return montant.add(fraisTransaction).add(montantPenalite);
    }

    /**
     * Vérifie si le paiement nécessite une validation manuelle.
     *
     * @return true si une validation manuelle est nécessaire
     */
    public boolean necessiteValidationManuelle () {
        return montant.compareTo(BigDecimal.valueOf(100000)) > 0 ||
                MethodePaiement.ESPECES.equals(methodePaiement) ||
                tentative > 3;
    }

    /**
     * Validation avant persistance.
     */
    @PrePersist
    @PreUpdate
    private void validatePaiement () {
        if (joursRetard == null) {
            this.joursRetard = calculerJoursRetard();
        }

        if (montantPenalite == null) {
            this.montantPenalite = calculerPenaliteRetard();
        }

        if (TypePaiement.COTISATION.equals(typePaiement) && numeroCycle == null) {
            throw new IllegalArgumentException("Le numéro de cycle est obligatoire pour les cotisations");
        }
    }
}