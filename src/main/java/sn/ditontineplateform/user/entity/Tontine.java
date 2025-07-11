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
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import sn.ditontineplateform.user.enumeration.StatutTontine;
import sn.ditontineplateform.user.enumeration.TypeEvenement;
import sn.ditontineplateform.user.enumeration.TypeTirage;
import sn.ditontineplateform.user.enumeration.TypeTontine;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Entité représentant une tontine dans la plateforme DiTontine. Une tontine est un système d'épargne collectif avec des
 * règles spécifiques.
 *
 * @author DiTontine Team
 * @version 1.0
 * @since 2025-01-01
 */
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"participations", "jackpots", "paiements", "notifications"})
@ToString(callSuper = true, exclude = {"participations", "jackpots", "paiements", "notifications"})
@Entity
@Table(name = "tontines")
public class Tontine extends BaseEntity {

    /**
     * Nom de la tontine.
     */
    @NotBlank(message = "Le nom de la tontine ne peut pas être vide")
    @Size(min = 3, max = 100, message = "Le nom doit contenir entre 3 et 100 caractères")
    @Column(name = "nom", nullable = false, length = 100)
    private String nom;

    /**
     * Description de la tontine.
     */
    @Size(max = 500, message = "La description ne peut pas dépasser 500 caractères")
    @Column(name = "description", length = 500)
    private String description;

    /**
     * Type de la tontine.
     */
    @NotNull(message = "Le type de tontine est obligatoire")
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private TypeTontine type;

    /**
     * Type de tirage pour la tontine.
     */
    @NotNull(message = "Le type de tirage est obligatoire")
    @Enumerated(EnumType.STRING)
    @Column(name = "type_tirage", nullable = false)
    private TypeTirage typeTirage;

    /**
     * Statut actuel de la tontine.
     */
    @NotNull(message = "Le statut de la tontine est obligatoire")
    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false)
    private StatutTontine statut = StatutTontine.EN_ATTENTE;

    /**
     * Montant de cotisation pour les tontines classiques.
     */
    @DecimalMin(value = "1000.0", message = "Le montant de cotisation doit être d'au moins 1000 FCFA")
    @Column(name = "montant_cotisation", precision = 15, scale = 2)
    private BigDecimal montantCotisation;

    /**
     * Montant minimum pour les tontines événementielles.
     */
    @DecimalMin(value = "0.0", message = "Le montant minimum ne peut pas être négatif")
    @Column(name = "montant_minimum", precision = 15, scale = 2)
    private BigDecimal montantMinimum;

    /**
     * Montant maximum pour les tontines événementielles.
     */
    @DecimalMin(value = "0.0", message = "Le montant maximum ne peut pas être négatif")
    @Column(name = "montant_maximum", precision = 15, scale = 2)
    private BigDecimal montantMaximum;

    /**
     * Nombre maximum de participants.
     */
    @NotNull(message = "Le nombre maximum de participants est obligatoire")
    @Min(value = 2, message = "Une tontine doit avoir au moins 2 participants")
    @Max(value = 100, message = "Une tontine ne peut pas avoir plus de 100 participants")
    @Column(name = "nombre_max_participants", nullable = false)
    private Integer nombreMaxParticipants;

    /**
     * Nombre minimum de participants requis pour démarrer.
     */
    @NotNull(message = "Le nombre minimum de participants est obligatoire")
    @Min(value = 2, message = "Une tontine doit avoir au moins 2 participants")
    @Column(name = "nombre_min_participants", nullable = false)
    private Integer nombreMinParticipants;

    /**
     * Fréquence des cotisations en jours.
     */
    @Min(value = 1, message = "La fréquence doit être d'au moins 1 jour")
    @Column(name = "frequence_cotisation_jours")
    private Integer frequenceCotisationJours;

    /**
     * Date de début de la tontine.
     */
    @Future(message = "La date de début doit être dans le futur")
    @Column(name = "date_debut")
    private LocalDate dateDebut;

    /**
     * Date de fin de la tontine.
     */
    @Column(name = "date_fin")
    private LocalDate dateFin;

    /**
     * Date de l'événement pour les tontines événementielles.
     */
    @Column(name = "date_evenement")
    private LocalDate dateEvenement;

    /**
     * Type d'événement pour les tontines événementielles.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "type_evenement")
    private TypeEvenement typeEvenement;

    /**
     * Nom personnalisé de l'événement.
     */
    @Size(max = 100, message = "Le nom de l'événement ne peut pas dépasser 100 caractères")
    @Column(name = "nom_evenement_personnalise", length = 100)
    private String nomEvenementPersonnalise;

    /**
     * Règles spécifiques de la tontine.
     */
    @Size(max = 1000, message = "Les règles ne peuvent pas dépasser 1000 caractères")
    @Column(name = "regles", length = 1000)
    private String regles;

    /**
     * Ordre de sélection pour les tirages sélectifs.
     */
    @Column(name = "ordre_selection", columnDefinition = "TEXT")
    private String ordreSelection;

    /**
     * Critères de sélection pour les tirages sélectifs.
     */
    @Column(name = "criteres_selection", columnDefinition = "TEXT")
    private String criteresSelection;

    /**
     * Indicateur si la tontine est publique.
     */
    @Column(name = "is_public", nullable = false)
    private Boolean isPublic = true;

    /**
     * Code d'invitation pour les tontines privées.
     */
    @Size(max = 20, message = "Le code d'invitation ne peut pas dépasser 20 caractères")
    @Column(name = "code_invitation", length = 20)
    private String codeInvitation;

    /**
     * Pénalité en cas de retard de paiement.
     */
    @DecimalMin(value = "0.0", message = "La pénalité ne peut pas être négative")
    @Column(name = "penalite_retard", precision = 15, scale = 2)
    private BigDecimal penaliteRetard;

    /**
     * Délai de grâce en jours avant application de la pénalité.
     */
    @Min(value = 0, message = "Le délai de grâce ne peut pas être négatif")
    @Column(name = "delai_grace_jours")
    private Integer delaiGraceJours = 3;

    /**
     * Administrateur de la tontine.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "administrateur_id", nullable = false)
    private User administrateur;

    /**
     * Participations à la tontine.
     */
    @OneToMany(mappedBy = "tontine", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Participation> participations = new HashSet<>();

    /**
     * Jackpots de la tontine.
     */
    @OneToMany(mappedBy = "tontine", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Jackpot> jackpots = new HashSet<>();

    /**
     * Paiements liés à la tontine.
     */
    @OneToMany(mappedBy = "tontine", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Paiement> paiements = new HashSet<>();

    /**
     * Notifications liées à la tontine.
     */
    @OneToMany(mappedBy = "tontine", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Notification> notifications = new HashSet<>();

    /**
     * Vérifie si la tontine est de type événementielle.
     *
     * @return true si la tontine est événementielle
     */
    public boolean isEvenementielle () {
        return TypeTontine.EVENEMENTIELLE.equals(this.type);
    }

    /**
     * Vérifie si la tontine est active.
     *
     * @return true si la tontine est active
     */
    public boolean isActive () {
        return StatutTontine.ACTIVE.equals(this.statut);
    }

    /**
     * Vérifie si la tontine peut accepter de nouveaux participants.
     *
     * @return true si la tontine peut accepter de nouveaux participants
     */
    public boolean peutAccepterNouveauParticipant () {
        return isActive() && participations.size() < nombreMaxParticipants;
    }

    /**
     * Retourne le nombre actuel de participants.
     *
     * @return le nombre de participants
     */
    public int getNombreParticipants () {
        return participations.size();
    }

    /**
     * Vérifie si la tontine a atteint le nombre minimum de participants.
     *
     * @return true si le nombre minimum est atteint
     */
    public boolean aNombreMinimumParticipants () {
        return getNombreParticipants() >= nombreMinParticipants;
    }

    /**
     * Calcule le montant total collecté par cycle.
     *
     * @return le montant total par cycle
     */
    public BigDecimal getMontantTotalParCycle () {
        if (isEvenementielle()) {
            return participations.stream()
                    .map(p -> p.getMontantCotisation())
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        } else {
            return montantCotisation.multiply(BigDecimal.valueOf(getNombreParticipants()));
        }
    }

    /**
     * Validation personnalisée pour les tontines événementielles.
     */
    @PrePersist
    @PreUpdate
    private void validateTontine () {
        if (isEvenementielle()) {
            if (dateEvenement == null) {
                throw new IllegalArgumentException("La date d'événement est obligatoire pour les tontines événementielles");
            }
            if (montantMinimum != null && montantMaximum != null && montantMinimum.compareTo(montantMaximum) > 0) {
                throw new IllegalArgumentException("Le montant minimum ne peut pas être supérieur au montant maximum");
            }
        } else {
            if (montantCotisation == null) {
                throw new IllegalArgumentException("Le montant de cotisation est obligatoire pour les tontines classiques");
            }
            if (frequenceCotisationJours == null) {
                throw new IllegalArgumentException("La fréquence de cotisation est obligatoire pour les tontines classiques");
            }
        }
    }
}