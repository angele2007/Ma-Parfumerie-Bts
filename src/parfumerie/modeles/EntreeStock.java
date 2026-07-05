package parfumerie.modeles;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class EntreeStock {

    private int idEntree;
    private int idProduit;
    private int idFournisseur;
    private int quantite;
    private LocalDateTime dateEntree;
    private BigDecimal prixUnitaire;

    public EntreeStock() {}

    public EntreeStock(int idEntree, int idProduit, int idFournisseur,
                       int quantite, LocalDateTime dateEntree, BigDecimal prixUnitaire) {
        this.idEntree = idEntree;
        this.idProduit = idProduit;
        this.idFournisseur = idFournisseur;
        this.quantite = quantite;
        this.dateEntree = dateEntree;
        this.prixUnitaire = prixUnitaire;
    }

    // Getters & Setters
    public int getIdEntree() { return idEntree; }
    public void setIdEntree(int idEntree) { this.idEntree = idEntree; }

    public int getIdProduit() { return idProduit; }
    public void setIdProduit(int idProduit) { this.idProduit = idProduit; }

    public int getIdFournisseur() { return idFournisseur; }
    public void setIdFournisseur(int idFournisseur) { this.idFournisseur = idFournisseur; }

    public int getQuantite() { return quantite; }
    public void setQuantite(int quantite) { this.quantite = quantite; }

    public LocalDateTime getDateEntree() { return dateEntree; }
    public void setDateEntree(LocalDateTime dateEntree) { this.dateEntree = dateEntree; }

    public BigDecimal getPrixUnitaire() { return prixUnitaire; }
    public void setPrixUnitaire(BigDecimal prixUnitaire) { this.prixUnitaire = prixUnitaire; }

    @Override
    public String toString() {
        return "Entrée[produit=" + idProduit + ", qte=" + quantite
                + ", fournisseur=" + idFournisseur + ", date=" + dateEntree + "]";
    }
}