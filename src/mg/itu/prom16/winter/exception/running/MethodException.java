// Source code is decompiled from a .class file using FernFlower decompiler.
package mg.itu.prom16.winter.exception.running;

import mg.itu.prom16.winter.exception.WinterException;

public class MethodException extends WinterException{
   public MethodException(String methodUsed, String methodShouldUsed, String url) {
      super("La méthode utilisée est :\"" + methodUsed + "\" alors que la méthode qu'on doit utilisé pour l'url \"" + methodShouldUsed + "\" est :\"" + url + "\"");
   }
}
