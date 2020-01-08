import math
import random
import matplotlib.pyplot as plt
class SAN:
    N = 1000
    tp = 5.0

def constructionsan(seed):
    random.seed(seed)
    X = [random.expovariate(1./1.0) for i in range(5)]
    Y = max(X[0]+X[3], X[0]+X[2] + X[4], X[1] + X[4])
    return Y

initialseed = 2124
Y = [constructionsan(initialseed + i) for i in range(SAN.N)]
Ytp = [1.0 if Y[i] > SAN.tp else 0 for i in range(SAN.N)]
thetahat = sum(Ytp) / SAN.N
sethetahat = math.sqrt((thetahat * (1 - thetahat)) / SAN.N)
Theta = 1 - ((math.pow(SAN.tp, 2) / 2 - 3.0 * SAN.tp - 3) *
             math.exp(-2 * SAN.tp) +
             (-1.0/2 * math.pow(SAN.tp, 2) - 3 * SAN.tp + 3) *
             math.exp(-SAN.tp) + 1.0 - math.exp(-3 * SAN.tp))
print("Compare (thetahat = %6.5f) plus/minus 1.96 * (sehat = %6.5f)" %
      (thetahat, sethetahat))
print("to theta = %6.5f" % (Theta))
lowerlimit = (thetahat - 1.96 * sethetahat)
upperlimit = (thetahat + 1.96 * sethetahat)
print("(%4.3f, %4.3f) %4.3f" % (lowerlimit, upperlimit, Theta))
plt.hist(Y, bins=60, range=(0, 12), histtype='step', cumulative=True)
