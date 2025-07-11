package sn.ditontineplateform.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PreRemove;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Classe de base abstraite pour toutes les entités du système DiTontine. Fournit les propriétés communes comme l'ID,
 * les timestamps et la suppression logique.
 *
 * @author DiTontine Team
 * @version 1.0
 * @since 2025-01-01
 */
@Data
@MappedSuperclass
@Where(clause = "is_deleted = false")
public abstract class BaseEntity {

    /**
     * Identifiant unique de l'entité généré automatiquement.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * Indicateur de suppression logique. Par défaut false, devient true lors de la suppression.
     */
    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;

    /**
     * Date et heure de création de l'entité. Automatiquement renseignée lors de la persistance.
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Date et heure de dernière modification de l'entité. Automatiquement mise à jour lors des modifications.
     */
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Méthode appelée avant la suppression de l'entité. Effectue une suppression logique en définissant isDeleted à
     * true.
     */
    @PreRemove
    public void preRemove () {
        this.isDeleted = true;
    }
}