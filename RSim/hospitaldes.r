library(simmer)
library(dplyr)
library(magrittr)
G <- list(RANDOM_SEED = 42,
          INTERVAL_patientS = 10.0, # Generate new patients roughly every x seconds
          maxTime = 480.0,          # minutes
          timeReceptionist = 0.8,   # mean, minutes
          phases = 3,
          ARRint = 1.0,             # mean, minutes
          theseed = 99999,
          reps = 10)

visitor <- trajectory("Arriving visitor") %>%
  seize("receptionist", amount=1) %>%
  timeout(function () rgamma(n=1, 
                             scale=G[['timeReceptionist']]/G[['phases']], 
                             shape = G[['phases']])
  ) %>%
  release("receptionist", amount=1)
  
envs <- lapply(1:reps, function(i) {
                set.seed(G[['RANDOM_SEED']]+20+i)
                simmer("HospitalDES") %>%
                add_resource("receptionist", 1) %>%
                add_generator("visitor", visitor, 
                              function() rexp(n=1,rate = (1/G[['ARRint']]))) %>%
                run(until=G[['maxTime']])
})

envs[[1]] %>% get_n_generated("visitor")

envs[[1]] %>% get_queue_size("receptionist")
head(
  envs %>% get_mon_resources()
)

receptionmonitor <- lapply(1:reps, function(i){
  result <- envs[[i]] %>% 
  get_mon_resources()
  
})

paste("replication", "utilization", "system")
for (i in 1:reps) {cat(i, mean(receptionmonitor[[i]]$server), 
                     mean(receptionmonitor[[i]]$system),
                     "\n")}
patientmonitor <- lapply(1:reps, function(i){
        result <- envs[[i]] %>% 
          get_mon_arrivals %>%
          mutate(flow_time = end_time - start_time)
})
paste("replication, patients, flowtime")
for (i in 1:reps) {cat(i, length(patientmonitor[[i]]$name), mean(patientmonitor[[i]]$flow_time), "\n")}

for (i in 1:reps) {
  print(paste("Average wait for ", sum(patientmonitor[[i]]$finished), " completions was ",
      mean(patientmonitor[[i]]$flow_time), "minutes."))
}
meanflowtimes <- sapply(1:reps, function(i){
  mean(patientmonitor[[i]]$flow_time)
})

hist(patientmonitor[[1]]$flow_time)