package parfumerie.modeles;

import java.math.BigDecimal;

public class LigneFacture {

    private int idLigne;
    private int idFacture;
    private int idProduit;
    private int quantite;
    private BigDecimal prixUnitaire;
    // sous_total est calculé automatiquement en BD (GENERATED ALWAYS)
    private BigDecimal sousTotal;

    public LigneFacture() {}

    public LigneFacture(int idLigne, int idFacture, int idProduit,
                        int quantite, BigDecimal prixUnitaire) {
        this.idLigne = idLigne;
        this.idFacture = idFacture;
        this.idProduit = idProduit;
        this.quantite = quantite;
        this.prixUnitaire = prixUnitaire;
        this.sousTotal = prixUnitaire.multiply(BigDecimal.valueOf(quantite));
    }

    // Getters & Setters
    public int getIdLigne() { return idLigne; }
    public void setIdLigne(int idLigne) { this.idLigne = idLigne; }

    public int getIdFacture() { return idFacture; }
    public void setIdFacture(int idFacture) { this.idFacture = idFacture; }

    public int getIdProduit() { return idProduit; }
    public void setIdProduit(int idProduit) { this.idProduit = idProduit; }

    public int getQuantite() { return quantite; }
    public void setQuantite(int quantite) { this.quantite = quantite; }

    public BigDecimal getPrixUnitaire() { return prixUnitaire; }
    public void setPrixUnitaire(BigDecimal prixUnitaire) { this.prixUnitaire = prixUnitaire; }

    public BigDecimal getSousTotal() { return sousTotal; }
    public void setSousTotal(BigDecimal sousTotal) { this.sousTotal = sousTotal; }

    @Override
    public String toString() {
        return "Ligne[facture=" + idFacture + ", produit=" + idProduit
                + ", qte=" + quantite + ", sous-total=" + sousTotal + "]";
    }
}