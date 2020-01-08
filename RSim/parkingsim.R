library(simmer)
library(simmer.plot)
library(ggplot2)
library(dplyr)
set.seed(1234)
parkingtime = 2

env <- simmer("ParkingSimulation")

arrival <- trajectory("Car path") %>%
  seize("parking", 1) %>%
  timeout(function() rexp(1, 1/parkingtime)) %>%
  release("parking", 1)

env %>%
  add_resource("parking", capacity = 100, mon=TRUE) %>%
  add_generator("arrival", arrival, function() rexp(1, 25))

env %>% run(until= (24))

head(
  env %>% get_mon_resources()
)

plot(env, "resources", metric="usage", names="parking")


envs <- lapply(1:100, function(i) {
  simmer("ReplicatedParkingSim") %>%
    add_resource("parking", 100) %>%
    add_generator("arrival", arrival, function() rexp(1, 25)) %>% 
    run(until= (24))
})

envs[[1]] %>% get_n_generated("arrival")
envs[[1]] %>% get_capacity("parking")
envs[[1]] %>% get_queue_size("parking")
head(
  envs %>% get_mon_resources()
)
head(
  envs %>% get_mon_arrivals()
)

plot(envs, "resources", metric="utilization", names="parking")

plot_evolution_arrival_times(envs, type="flow_time")
