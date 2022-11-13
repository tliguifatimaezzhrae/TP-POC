// -*- coding: utf-8 -*-

import java.util.ArrayList;
import java.math.BigInteger;

public class PremiersNombresPremiers {
  static final ArrayList<Long> premiersNombresPremiers = new ArrayList<Long>() ;    
  static final long borne = 10_000_000 ;    

  public static void main(String[] args) {
    final long début = System.nanoTime() ;

    for (long i = 1; i <= borne; i++) {
      if (BigInteger.valueOf(i).isProbablePrime(50)) premiersNombresPremiers.add(i) ;
    }

    final long fin = System.nanoTime() ;
    final long durée = (fin - début) / 1_000_000 ;

    System.out.print("Nombre de nombres premiers inférieurs à " + borne + " : ") ;
    System.out.println(premiersNombresPremiers.size()) ;
    System.out.format("Durée du calcul: %.3f s.%n", (double) durée/1000) ;
  }
}

/*
  $ java PremiersNombresPremiers
  Nombre de nombres premiers inférieurs à 10000000 : 664579
  Durée du calcul: 23,336 s.
  $
*/
