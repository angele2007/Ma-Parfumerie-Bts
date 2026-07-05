package parfumerie.modele;

import java.math.BigDecimal;

public class Produit {

    public enum Categorie { Homme, Femme, Mixte, Enfant }

    private int idProduit;
    private String nom;
    private String marque;
    private Categorie categorie;
    private Integer contenanceMl;
    private BigDecimal prixAchat;
    private BigDecimal prixVente;
    private int quantiteStock;
    private int seuilAlerte;

    public Produit() {}

    public Produit(int idProduit, String nom, String marque, Categorie categorie,
                   Integer contenanceMl, BigDecimal prixAchat, BigDecimal prixVente,
                   int quantiteStock, int seuilAlerte) {
        this.idProduit = idProduit;
        this.nom = nom;
        this.marque = marque;
        this.categorie = categorie;
        this.contenanceMl = contenanceMl;
        this.prixAchat = prixAchat;
        this.prixVente = prixVente;
        this.quantiteStock = quantiteStock;
        this.seuilAlerte = seuilAlerte;
    }

    // Getters & Setters
    public int getIdProduit() { return idProduit; }
    public void setIdProduit(int idProduit) { this.idProduit = idProduit; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getMarque() { return marque; }
    public void setMarque(String marque) { this.marque = marque; }

    public Categorie getCategorie() { return categorie; }
    public void setCategorie(Categorie categorie) { this.categorie = categorie; }

    public Integer getContenanceMl() { return contenanceMl; }
    public void setContenanceMl(Integer contenanceMl) { this.contenanceMl = contenanceMl; }

    public BigDecimal getPrixAchat() { return prixAchat; }
    public void setPrixAchat(BigDecimal prixAchat) { this.prixAchat = prixAchat; }

    public BigDecimal getPrixVente() { return prixVente; }
    public void setPrixVente(BigDecimal prixVente) { this.prixVente = prixVente; }

    public int getQuantiteStock() { return quantiteStock; }
    public void setQuantiteStock(int quantiteStock) { this.quantiteStock = quantiteStock; }

    public int getSeuilAlerte() { return seuilAlerte; }
    public void setSeuilAlerte(int seuilAlerte) { this.seuilAlerte = seuilAlerte; }

    @Override
    public String toString() {
        return nom + " - " + marque + " (" + categorie + ", " + contenanceMl + "ml)";
    }
}