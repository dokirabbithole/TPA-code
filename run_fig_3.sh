# RW 10,000 ; 100,000
java -Djava.ext.dirs=. TPAGenerator -model RW 10000 power-law 1 power-law 0.8 poisson 5 30 53
# BA 
java -Djava.ext.dirs=. TPAGenerator -model BA 10000 10
# DPA
java -Djava.ext.dirs=. TPAGenerator -model DPA 10000 10 power-law 0.8 poisson 5 30
# DPAVary
java -Djava.ext.dirs=. TPAGenerator -model DPAVary 10000 power-law 1.1 50 power-law 0.8 poisson 5 30

javac -Xlint:unchecked -encoding gbk observation/DegreeDistribution.java
java observation.DegreeDistribution