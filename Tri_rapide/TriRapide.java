// -*- coding: utf-8 -*-

import java.util.Random ;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.*;
public class TriRapide {
    static final int taille = 1_000_000 ;                   // Longueur du tableau à trier
    static final int [] tableau = new int[taille] ;         // Le tableau d'entiers à trier 
    static final int borne = 10 * taille ;                  // Valeur maximale dans le tableau
    static double P = 1.0;

    static CompletionService<Void> completionService;
    static final AtomicInteger tasks = new AtomicInteger();

    private static void echangerElements(int[] t, int m, int n) {
        int temp = t[m] ;
        t[m] = t[n] ;
        t[n] = temp ;
    }

    private static int partitionner(int[] t, int début, int fin) {
        int v = t[fin] ;                               // Choix (arbitraire) du pivot : t[fin]
        int place = début ;                            // Place du pivot, à droite des éléments déplacés
        for (int i = début ; i<fin ; i++) {            // Parcours du *reste* du tableau
            if (t[i] < v) {                            // Cette valeur t[i] doit être à droite du pivot
                echangerElements(t, i, place) ;        // On le place à sa place
                place++ ;                              // On met à jour la place du pivot
            }
        }
        echangerElements(t, place, fin) ;              // Placement définitif du pivot
        return place ;
    }
    
    private static void seqTrierRapidement(int[] t, int début, int fin) {
        if (début < fin) {                             // S'il y a un seul élément, il n'y a rien à faire!
            int p = partitionner(t, début, fin) ;
            seqTrierRapidement(t, début, p-1) ;
            seqTrierRapidement(t, p+1, fin) ;
        }
    }
    private static void parallelTrierRapidement(int[] t, int début, int fin) {
        ExecutorService executorService = Executors.newFixedThreadPool(4);
        completionService = new ExecutorCompletionService<>(executorService);
        tasks.set(0);
        parallelTrierRapidement(t, début, fin);

        while (tasks.getAndAdd(-1) > 0) {
            try {
                completionService.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        executorService.shutdown();
        completionService = null;
    }
    private static void trierRapidement(int[] t, int début, int fin) {
        if (début < fin) {                             // S'il y a un seul élément, il n'y a rien à faire!
            int p = partitionner(t, début, fin) ;      
            int l = p - 1 - début;
            //if (l > 1000 && l > taille / 100) {
            if (l > (int)(P * taille)) {
                tasks.addAndGet(2);
                completionService.submit(() -> {
                    parallelTrierRapidement(t, début, p - 1);
                }, null);
                completionService.submit(() -> {
                    parallelTrierRapidement(t, p + 1, fin);
                }, null);
            } else {
                seqTrierRapidement(t, début, p - 1);
                seqTrierRapidement(t, p + 1, fin);
            }
        }
    }

    private static void afficher(int[] t, int début, int fin) {
        for (int i = début ; i <= début+3 ; i++) {
            System.out.print(" " + t[i]) ;
        }
        System.out.print("...") ;
        for (int i = fin-3 ; i <= fin ; i++) {
            System.out.print(" " + t[i]) ;
        }
        System.out.println() ;
    }

    public static void main(String[] args) {
        Random aléa = new Random() ;
        for (int i=0 ; i<taille ; i++) {                          // Remplissage aléatoire du tableau
            tableau[i] = aléa.nextInt(2*borne) - borne ;            
        }
        System.out.print("Tableau initial : ") ;
        afficher(tableau, 0, taille -1) ;                         // Affiche le tableau à trier

        System.out.println("Démarrage du tri rapide.") ;
        long débutDuTri = System.nanoTime();

        trierRapidement(tableau, 0, taille-1) ;                   // Tri du tableau

        long finDuTri = System.nanoTime();
        long duréeDuTri = (finDuTri - débutDuTri) / 1_000_000 ;
        System.out.print("Tableau trié : ") ; 
        afficher(tableau, 0, taille -1) ;                         // Affiche le tableau obtenu
        System.out.println("obtenu en " + duréeDuTri + " millisecondes.") ;
    }
}


/*
  $ make
  javac *.java
  $ java TriRapide
  Tableau initial :  4967518 -8221265 -951337 4043143... -4807623 -1976577 -2776352 -6800164
  Démarrage du tri rapide.
  Tableau trié :  -9999981 -9999967 -9999957 -9999910... 9999903 9999914 9999947 9999964
  obtenu en 85 millisecondes.
*/
