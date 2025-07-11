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
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "transactions_coffre")
public class TransactionCoffre extends BaseEntity {

    public enum TypeTransaction {
        DEPOT, RETRAIT, AUTO_EPARGNE, OBJECTIF_ATTEINT
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coffre_id", nullable = false)
    private Coffre coffre;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private User utilisateur;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeTransaction type;

    @Column(precision = 15, scale = 2, nullable = false)
    private BigDecimal montant;

    @Column(precision = 15, scale = 2, nullable = false)
    private BigDecimal soldeAvant;

    @Column(precision = 15, scale = 2, nullable = false)
    private BigDecimal soldeApres;

    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    private LocalDateTime dateTransaction = LocalDateTime.now();

    // Méthode pour initialiser une transaction
    public void initialiserTransaction(Coffre coffre, User user, TypeTransaction type, BigDecimal montant, String description) {
        this.coffre = coffre;
        this.utilisateur = user;
        this.type = type;
        this.montant = montant;
        this.soldeAvant = coffre.getSolde();
        this.soldeApres = coffre.getSolde().add(montant);
        this.description = description;
    }
}