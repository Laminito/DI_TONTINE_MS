package sn.ditontineplateform.user.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Entité représentant le coffre personnel d'un utilisateur. Le coffre permet l'épargne individuelle avec des objectifs
 * et l'auto-épargne.
 *
 * @author DiTontine Team
 * @version 1.0
 * @since 2025-01-01
 */
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"transactions", "objectifs"})
@ToString(callSuper = true, exclude = {"transactions", "objectifs"})
@Entity
@Table(name = "coffres")
public class Coffre extends BaseEntity {

    /**
     * Propriétaire du coffre.
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proprietaire_id", nullable = false, unique = true)
    private User proprietaire;

    /**
     * Solde actuel du coffre.
     */
    @NotNull(message = "Le solde est obligatoire")
    @DecimalMin(value = "0.0", message = "Le solde ne peut pas être négatif")
    @Column(name = "solde", precision = 15, scale = 2, nullable = false)
    private BigDecimal solde = BigDecimal.ZERO;

    /**
     * Objectif d'épargne principal.
     */
    @DecimalMin(value = "0.0", message = "L'objectif d'épargne ne peut pas être négatif")
    @Column(name = "objectif_epargne", precision = 15, scale = 2)
    private BigDecimal objectifEpargne;

    /**
     * Nom de l'objectif d'épargne.
     */
    @Size(max = 100, message = "Le nom de l'objectif ne peut pas dépasser 100 caractères")
    @Column(name = "nom_objectif", length = 100)
    private String nomObjectif;

    /**
     * Date limite pour atteindre l'objectif.
     */
    @Column(name = "date_limite_objectif")
    private LocalDate dateLimiteObjectif;

    /**
     * Montant d'auto-épargne mensuelle.
     */
    @DecimalMin(value = "0.0", message = "Le montant d'auto-épargne ne peut pas être négatif")
    @Column(name = "montant_auto_epargne", precision = 15, scale = 2)
    private BigDecimal montantAutoEpargne;


    /**
     * Liste des transactions associées à ce coffre. Cette relation est bidirectionnelle et gérée par l'entité
     * TransactionCoffre. Les transactions sont chargées en mode LAZY pour optimiser les performances et sont cascadees
     * pour une gestion automatique des opérations de persistance.
     */
    @OneToMany(mappedBy = "coffre", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<TransactionCoffre> transactions = new HashSet<>();

    /**
     * Validation métier pour vérifier que la date limite de l'objectif est dans le futur. Cette méthode est appelée
     * automatiquement par le framework de validation.
     *
     * @return true si la date limite est valide (dans le futur ou non définie), false sinon
     * @throws jakarta.validation.ValidationException si la validation échoue
     */
    @AssertTrue(message = "La date limite doit être dans le futur")
    public boolean isDateLimiteValide () {
        return dateLimiteObjectif == null || dateLimiteObjectif.isAfter(LocalDate.now());
    }

    /**
     * Jour du mois choisi pour le prélèvement automatique de l'auto-épargne. Doit être compris entre 1 (premier jour du
     * mois) et 28 (dernier jour valide pour tous les mois). Null si l'auto-épargne n'est pas activée.
     */
    @Min(1)
    @Max(28)
    @Column(name = "jour_prelevement")
    private Integer jourPrelevement;
}