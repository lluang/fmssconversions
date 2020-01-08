NSPP_Fax <- function(arrivalrate, MaxRate, NPeriods, periodlength, stream){
  # Non-stationary Poisson Process
  # 
  # This function generates interarrival times from a NSPP with        
  # piecewise constant arrival rate over a fixed time of NPeriod time units
  # 
  # arrivalrate - Array of arrival rates over a common length period
  # MaxRate - The maximum value of ARate
  # NPeriods - Number of time periods in ARate
  # periodlength - time units between (possible) changes in arrival rate
  # stream <- random number seed
  set.seed(stream)
  thinningrate <- function(hourlyrate){
    thinrate <- 1 - hourlyrate/MaxRate
  }
  pthinning <- sapply(arrivalrate, FUN=thinningrate)
  t = 0.0
  arrivaltimes = c()
  totaltime = NPeriods * periodlength
  while (t < totaltime){
    deltat = rexp(MaxRate)
    t = t + deltat
    if (t < totaltime){
      pthin = pthinning[int(floor(t/periodlength))]
      uthin = random.random()
      if (uthin > pthin)
        arrivaltimes.append(float(t))
    }
  }
   arrivaltimes
}