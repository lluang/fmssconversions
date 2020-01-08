package org.javasim.examples;

import org.javasim.JavaSim;
import org.javasim.Rng;

public class MG1Lindley {

  public static void main(String[] args) {
    int d;
    long m;
    double y, x, a;
    double sumY;

    Rng generator = new Rng();
    m = 55000;
    d = 5000;

    String simulationName = "M/G/1 Lindley";
    JavaSim javaSim = new JavaSim(simulationName);
    javaSim.report("Average Wait", 0, 0);

    for(int rep = 0; rep < 10; rep++) {
      y = 0.0;
      sumY = 0.0;

      for(int i = 0; i < d; i++) {
        a = generator.expon(1.0, 0);
        x = generator.erlang(3, 0.8, 1);
        y = Math.max(0, y + x - a);
      }

      for(int i = d; i < m; i++) {
        a = generator.expon(1.0, 0);
        x = generator.erlang(3, 0.8, 1);
        y = Math.max(0, y + x - a);
        sumY += y;
      }
      javaSim.report(sumY / ((double) m - d), rep + 1, 0);
    }

  }
}
