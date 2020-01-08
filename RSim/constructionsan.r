library(foreach)
library(ggplot2)

SANmax <- function(N=1000){
  tp = 5.0
  
  constructionsan <-  function(seed){
    set.seed(seed)
    X = rexp(5, 1.0)
    Y = max(X[1]+X[4], X[1]+X[3] + X[5], X[2] + X[5])
  }
  
  initialseed = 2124
  i = initialseed:(initialseed + N)
   
  Y = sapply(i, constructionsan)
  isan = 1:N
  fy <- function(i) ifelse(Y[i]>tp,1,0)
  Ytp <- sapply(isan, fy)
  thetahat = sum(Ytp)/N
  sethetahat = sqrt((thetahat*(1-thetahat))/N)
  Theta = 1 - ( ( tp^2 / 2 - 3.0 * tp - 3) * exp(-2 * tp) + 
                  (-1.0/2 *tp^2 - 3 * tp + 3) * exp(-tp) + 
                  1.0 - exp(-3 * tp))
  sanmax <- Theta
  setheta <- sqrt((Theta * (1-Theta))/N)
  result <- c(sanmax, setheta, Y, Ytp)                
}

reps = 1000
result <- SANmax(reps)

sanmax <- result[1]
setheta <- result[2]
resultY <- result[3:(reps+2)]
resultYtp <- result[reps+3:(2*reps + 3)]
paste(sanmax, setheta)
sanresults <- data.frame(constructiontime = resultY)
ggplot(sanresults) + geom_density(aes(constructiontime)) +
  ggtitle("Outcome of time to completion for Construction SAN") +
  xlab("Time to completion") +
  theme_bw()
