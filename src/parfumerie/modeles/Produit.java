/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package parfumerie.modeles;

/**
 *
 * @author Angele
 */


import java.math.BigDecimal;

public class Produit {

    // ── Enum Categorie (correspond exactement aux valeurs MySQL) ─────────────
    public enum Categorie {
        Homme, Femme, Mixte, Enfant
    }

    private int        idProduit;
    private String     nom;
    private String     marque;
    private Categorie  categorie;
    private Integer    contenanceMl;   // Integer nullable (pas int)
    private BigDecimal prixAchat;
    private BigDecimal prixVente;
    private int        quantiteStock;
    private int        seuilAlerte;

    public Produit() {}

    // ── Getters / Setters ────────────────────────────────────────────────────

    public int        getIdProduit()               { return idProduit; }
    public void       setIdProduit(int v)           { this.idProduit = v; }

    public String     getNom()                     { return nom; }
    public void       setNom(String v)             { this.nom = v; }

    public String     getMarque()                  { return marque; }
    public void       setMarque(String v)          { this.marque = v; }

    public Categorie  getCategorie()               { return categorie; }
    public void       setCategorie(Categorie v)    { this.categorie = v; }

    public Integer    getContenanceMl()             { return contenanceMl; }
    public void       setContenanceMl(Integer v)    { this.contenanceMl = v; }

    public BigDecimal getPrixAchat()               { return prixAchat; }
    public void       setPrixAchat(BigDecimal v)   { this.prixAchat = v; }

    public BigDecimal getPrixVente()               { return prixVente; }
    public void       setPrixVente(BigDecimal v)   { this.prixVente = v; }

    public int        getQuantiteStock()           { return quantiteStock; }
    public void       setQuantiteStock(int v)      { this.quantiteStock = v; }

    public int        getSeuilAlerte()             { return seuilAlerte; }
    public void       setSeuilAlerte(int v)        { this.seuilAlerte = v; }

    @Override
    public String toString() { return nom + " – " + marque; }
}