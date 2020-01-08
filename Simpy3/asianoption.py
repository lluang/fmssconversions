# Simulating the Asian Option
# ===========================
#
# Here we consider estimating the value of an Asian option
#
# $$
# \nu = \E\left[ \mathrm{e}^{-rT} (\bar{X}(T) - K)^+ \right]
# $$
#
# as described in Sect.~\ref{sec:fe}, where the maturity is $T = 1$
# year, the risk-free interest rate is $r = 0.05$ and the strike price
# is $K = \$55$. The underlying asset has an initial value of $X(0) =
# \$50$ and the volitility is $\sigma^2 = (0.3)^2$.  Recall that the
# key quantity is
# $$
# \bar{X}(T) = \frac{1}{T}\int_0^T X(t)\, \D t
# $$
# the time average of a continuous-time, continuous-state geometric
# Brownian motion
# process which we cannot truely simulate on a digital computer.
# \index{geometric Brownian motion}
# Thus, we approximate it by dividing the
# interval $[0, T]$ into $m$ steps of size $\Delta t =
# T/m$ and using the discrete approximation
# $$
# \widehat{\bar{X}(T)} = \frac{1}{m}
# \sum_{i=1}^m X(i \Delta t) .
# $$
# This makes simulation possible, since
#
# $$
# X(t_{i+1}) = X(t_i) \exp\left\{\left( r - \frac{1}{2}\sigma^2
# \right)(t_{i+1}
# - t_i) + \sigma\sqrt{t_{i+1} - t_i} \, Z_{i+1} \right\}
# $$
#
# for any increasing sequence of times $\{t_0, t_1, \ldots, t_m\}$,
# where $Z_1, Z_2, \ldots, Z_m$ are i.i.d.\ $\mathrm{N}(0,1)$.
#
# Figure~\ref{fig:asian.sim} is VBASim code that uses $m=32$ steps in
# the approximation, and makes $10{,}000$ replications to estimate
# $\nu$. Discrete-event structure would slow execution without any
# obvious benefit, so a simple loop is used to advance time. The value
# of the option from each replication is written to Excel for
# post-simulation analysis.
#
# The estimated value of $\nu$ is \$2.20 with a relative error of just over 2\%
# (recall that the relative error is the standard error divided by the
# mean). As the histogram in Fig.~\ref{fig:AsianHistogram} shows, the
# option is frequently worthless (approximately 68\% of the time), but
# the average payoff, conditional on the payoff being positive, is
# approximately \$6.95.

import math
import random
import scipy as sp
import matplotlib.pyplot as plt


def Asianoption(interestrate, sigma, steps, initialValue,
                strikePrice, maturity, seed):
    '''  Asian Options simulation
    '''
    sumx = 0.0
    generator = random.Random()
    generator.seed(seed)
    interval = maturity/steps
    x = initialValue
    sigma2 = (sigma*sigma)/2.0
    for j in range(steps):
        z = generator.normalvariate(0, 1)
        x = x * math.exp((interestRate - sigma2) * interval +
                         sigma * math.sqrt(interval) * z)
        sumx = sumx + x
    value = math.exp(-interestRate * maturity) * \
        max(sumx / float(steps) - strikePrice, 0.0)
    return value


replications = 10000
initialSeed = 1234
maturity = 1.0
steps = 32
sigma = 0.3
interestRate = 0.05
initialValue = 50.0
strikePrice = 55.0
interval = maturity / float(steps)
values = [Asianoption(interestRate, sigma, steps, initialValue,
          strikePrice, maturity, i + initialSeed)
          for i in range(replications)]

print(sp.mean(values))
print(sp.std(values)/math.sqrt(replications))  # standard error
print(sp.std(values)/math.sqrt(replications)/sp.mean(values))  # relative error

plt.hist(values, bins=30, range=(0, 30))

positivepayoff = [value for value in values if value > 0]

sp.mean(positivepayoff)

len(positivepayoff)
