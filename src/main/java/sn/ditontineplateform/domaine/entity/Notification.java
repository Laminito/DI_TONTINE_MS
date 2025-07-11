package sn.ditontineplateform.domaine.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import sn.ditontineplateform.domaine.enumeration.Priorite;
import sn.ditontineplateform.domaine.enumeration.TypeNotification;

import java.time.LocalDateTime;

/**
 * Entité représentant une notification dans le système DiTontine. Les notifications sont utilisées pour informer les
 * utilisateurs des événements importants tels que les rappels de paiement, les jackpots à venir, ou les nouvelles
 * participations.
 *
 * @author DiTontine Team
 * @version 1.0
 * @since 2025-01-01
 */
@Data
@Entity
@Table(name = "notifications")
public class Notification extends BaseEntity {

    /**
     * Utilisateur destinataire de la notification. Cette relation est obligatoire. Chargée en mode LAZY pour optimiser
     * les performances.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private User utilisateur;

    /**
     * Tontine associée à la notification (optionnelle). Null si la notification n'est pas liée à une tontine
     * spécifique.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tontine_id")
    private Tontine tontine;

    /**
     * Type de la notification définissant sa catégorie et son comportement.
     *
     * @see TypeNotification
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeNotification type;

    /**
     * Priorité de la notification déterminant son importance et son affichage.
     *
     * @see Priorite
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Priorite priorite;

    /**
     * Titre court de la notification (max 200 caractères). Doit être concis mais descriptif.
     */
    @Column(nullable = false, length = 200)
    private String titre;

    /**
     * Contenu détaillé de la notification (max 1000 caractères). Peut contenir des instructions ou des informations
     * complémentaires.
     */
    @Column(nullable = false, length = 1000)
    private String message;

    /**
     * Indicateur de lecture. True si la notification a été lue par l'utilisateur. Initialisé à false par défaut.
     */
    @Column(nullable = false)
    private Boolean estLue = false;

    /**
     * Date et heure de lecture de la notification par l'utilisateur. Null si la notification n'a pas encore été lue.
     */
    @Column
    private LocalDateTime dateLecture;

    /**
     * Date et heure de création de la notification dans le système. Initialisée automatiquement à la date courante.
     */
    @Column(nullable = false)
    private LocalDateTime dateCreation = LocalDateTime.now();

    /**
     * Date d'échéance associée à la notification (optionnelle). Utilisée pour les rappels ou les notifications
     * temporelles.
     */
    @Column
    private LocalDateTime dateEcheance;

    /**
     * Marque la notification comme lue et enregistre la date de lecture. Met à jour les champs estLue et dateLecture
     * avec les valeurs actuelles.
     */
    public void marquerCommeLue () {
        this.estLue = true;
        this.dateLecture = LocalDateTime.now();
    }
}