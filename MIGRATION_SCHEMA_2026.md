# Adaptation de l'application au nouveau schéma de base de données

## Date : 20 janvier 2026

### Résumé des changements

L'application a été adaptée pour utiliser la nouvelle conception de la base de données (schéma 19-01-2026.sql). Les modifications principales incluent :

## 1. **Restructuration des modèles JPA**

### Suppression des anciennes tables
- `VolClasse` : Table intermédiaire obsolète pour stocker les places par classe
  - Cette table stockait les places restantes statiquement
  - Remplacée par un calcul dynamique basé sur les réservations

### Remplacement des modèles
- **`PrixClasseAge`** → **`PrixClasse`**
  - Ancienne structure : Storait les prix par classe ET catégorie d'âge
  - Nouvelle structure : Stocke uniquement les prix de base par classe
  - Les remises par catégorie d'âge sont maintenant gérées via `RemiseClasseCategorie`

### Adaptation de `RemiseClasseCategorie`
- Ancien modèle utilisant `@EmbeddedId` → Nouveau modèle utilisant `@IdClass`
- Stocke maintenant le pourcentage de réduction (0-100)
- Méthode utilitaire `appliquerRemise()` pour calculer le prix final

### Modification de `VolDetail`
- Suppression de la relation `OneToMany` vers `VolClasse`
- Ajout de la relation `OneToMany` vers `PrixClasse`
- **Calcul dynamique des places restantes** :
  ```
  Places restantes = Capacité de l'avion - Nombre de réservations (confirmées + en attente)
  ```
- Méthode `getPlacesRestantesByClasse()` : Calcule les places disponibles dynamiquement
- Méthode `getPrixFinal()` : Applique la remise au prix de base
- Méthode `getChiffreAffairesReel()` : Utilise les prix avec remises appliquées

## 2. **Nouveaux Repositories**

### `PrixClasseRepository`
- Remplace partiellement `PrixClasseAgeRepository`
- Méthodes pour récupérer les prix de base par classe

### Mise à jour de `RemiseClasseCategorieRepository`
- Méthodes pour trouver les remises par classe et catégorie

## 3. **Nouveaux Services**

### `PrixClasseService`
- Gère les prix de base par classe
- Méthodes pour récupérer les prix par volDetail et classe

### `RemiseService`
- Gère les remises entre classe et catégorie d'âge
- Méthode `calculerPrixAvecRemise()` pour appliquer les réductions

### Adaptation de `VolDetailService`
- Suppression de la dépendance à `VolClasseService`
- Ajout de la dépendance à `PrixClasseService` et `RemiseService`
- Méthode `createWithPrix()` : Crée un vol_detail avec les prix de base uniquement
- Méthode `getPlacesRestantes()` : Retourne les places calculées dynamiquement
- Méthode `calculerPrixAvecRemise()` : Applique les remises

### Adaptation de `ReservationService`
- Suppression de la dépendance à `VolClasseService` et `PrixClasseAgeService`
- Ajout de la dépendance à `VolDetailService`
- **Plus de décrément/incrément de places** : Les places sont calculées dynamiquement
- Validation des places disponibles avant création de réservation
- Méthode `getPrixFinal()` : Calcule le prix avec remise appliquée
- Méthode `getPlacesRestantes()` : Retourne les places disponibles

## 4. **Adaptation des Contrôleurs**

### `VolController`
- Suppression de `VolClasseService` et `PrixClasseAgeService`
- Remplacement par `PrixClasseService`
- Formulaire d'ajout de vol_detail : Demande maintenant le "prix_base" au lieu de "places" et "prix_adulte"
- Les places sont automatiquement déterminées par la capacité de l'avion

### `ReservationController`
- Suppression de la dépendance à `RemiseService` (non utilisé directement)
- Validation des places disponibles via `volDetail.getPlacesRestantesByClasse()`
- Calcul du prix final avec remise appliquée
- Affichage du prix avec remise dans les détails de réservation

## 5. **Logique Métier - Calcul des places restantes**

### Avant
```sql
Places restantes = stockées dans vol_classe.places_restantes
Mise à jour lors de chaque réservation
```

### Après
```
Places restantes = Capacité (avion_classe.capacite) - Nombre de réservations (CONFIRMEE + EN_ATTENTE)
Calculé dynamiquement à chaque requête
```

**Avantages** :
- Pas de synchronisation nécessaire
- Cohérence garantie avec les réservations
- Annulation automatique : Les places sont libérées via le changement du statut à "ANNULÉ"

## 6. **Logique Métier - Calcul des prix**

### Avant
```
prix = prix_classe_age.prix
```

### Après
```
prix_final = prix_classe.prix_base * (1 - remise_classe_categorie.pourcentage / 100)
```

**Flux** :
1. Stocker le prix de base par classe dans `prix_classe`
2. Stocker le pourcentage de remise par (classe, catégorie) dans `remise_classe_categorie`
3. Appliquer la remise lors de la lecture

## 7. **Fichiers Modifiés**

### Modèles JPA
- `VolDetail.java` : Refactorisé avec nouveaux calculs dynamiques
- `PrixClasse.java` : Nouveau modèle pour prix_classe
- `RemiseClasseCategorie.java` : Adapté à la nouvelle structure

### Repositories
- `PrixClasseRepository.java` : Nouveau repository
- `RemiseClasseCategorieRepository.java` : Existant, utilisé comme avant

### Services
- `VolDetailService.java` : Refactorisé
- `ReservationService.java` : Refactorisé (plus de gestion des places)
- `PrixClasseService.java` : Nouveau service
- `RemiseService.java` : Nouveau service
- `PaiementService.java` : Adapté pour utiliser `getPrixFinal()`

### Contrôleurs
- `VolController.java` : Refactorisé
- `ReservationController.java` : Refactorisé
- `ChiffreAffaireController.java` : Adapté

## 8. **Notes importantes**

1. **Pas de table `vol_classe` en base** : Elle était utilisée pour stocker les places restantes. Ces données sont maintenant calculées dynamiquement.

2. **Annulation de réservation** : Lorsqu'une réservation est annulée (statut = "ANNULÉ"), les places sont automatiquement libérées car elles sont recalculées.

3. **Chiffre d'affaires réel** : Calculé avec les remises appliquées, pas juste le prix de base.

4. **Compatibilité** : Le code est rétrocompatible avec les anciennes données (si présentes) via les anciennesmethods de création.

## 9. **Test recommandé**

Pour vérifier le bon fonctionnement :
1. Créer un vol_detail avec un avion (capacité connue)
2. Créer des réservations
3. Vérifier que `getPlacesRestantes()` retourne `capacité - nombre_reservations`
4. Annuler une réservation et vérifier que les places sont libérées
5. Vérifier que le prix final inclut la remise appliquée

---

**Migration complète et compilation sans erreurs critiques.**
