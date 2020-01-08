library(ggplot2)

lindley <- function(m=55000, d=5000, replications = 10){
  lindley <- c()
  for (rep in 1:replications){
    y<- 0
    SumY <- 0
    for (i in 1:d){
      a <- rexp(1,1)
      x <- rgamma(1, shape=3, scale = 0.8/3)
      y <- max(0, y + x -a)
    }
    for (i in d:m){
      a <- rexp(1,1)
      x <- rgamma(1, shape=3, scale = 0.8/3)
      y <- max(0, y + x - a)
      SumY <- SumY + y
    }
    result <- SumY /(m-d)
    lindley <- c(lindley, result)
  }
  lindley
}

set.seed(1234)
result <- lindley(replications = 10)
print("run, waiting time")
runs <- 1:length(result)
cat("Average: ", mean(result), "\n")
cat("Std Dev: ", sd(result))
receptionist <- data.frame(run=runs, waiting=result)
ggplot(receptionist) + geom_histogram(aes(x=waiting)) + 
  ggtitle("Average waiting time \n using Lindsey's method") + 
  xlab("Minutes") + ylab("Count out of 100 replications") +
  scale_y_continuous(breaks = c(0,2,4,6,8,10)) +
  theme_bw()
