package org.javasim.examples;

import org.javasim.JavaSim;
import org.javasim.Rng;

public class SANMax {

  public static void main(String[] args) {
    double n, c, tp, y, theta;
    double[] x = new double[5];

    Rng generator = new Rng();
    String simulationName = "Stochastic Activity Network";
    JavaSim javaSim = new JavaSim(simulationName);
    javaSim.report("Pr{Y > tp}", 0, 0);
    javaSim.report("True Theta", 0, 1);

    n = 1000.0;
    c = 0.0;
    tp = 5.0;

    for(int rep = 0; rep < n; rep++) {
      for(int i = 0; i < 5; i++) {
        x[i] = generator.expon(1.0, 6);
      }
      y = Math.max(Math.max(x[0] + x[3], x[0] + x[2] + x[4]), x[1] + x[4]);

      if (y > tp) {
        c++;
      }
    }

    javaSim.report(c / n, 1, 0);

    theta = 1.0 - ((tp * tp / 2.0 - 3.0 * tp - 3.0) * Math.exp(-2.0 * tp) + (-tp * tp / 2.0 - 3.0 * tp + 3.0) * Math.exp(-tp) + 1.0 - Math.exp(-3.0 * tp));

    javaSim.report(theta, 1, 1);

  }
}
