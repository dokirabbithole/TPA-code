# command											gen time (sec)
# RW 1,000
java -Djava.ext.dirs=. TPAGenerator -model RW 1000 power-law 1 power-law 0.8 poisson 5 30 37	# 1.048104751
# RW 5,000
java -Djava.ext.dirs=. TPAGenerator -model RW 5000 power-law 1 power-law 0.8 poisson 5 30 56	# 27.428313554
# RW 10,000
java -Djava.ext.dirs=. TPAGenerator -model RW 10000 power-law 1 power-law 0.8 poisson 5 30 68	# 97.087967942

# SA 
java -Djava.ext.dirs=. TPAGenerator -model SA 1000 power-law 1 power-law 0.8 poisson 5 30 10000		# 0.029725969
java -Djava.ext.dirs=. TPAGenerator -model SA 5000 power-law 1 power-law 0.8 poisson 5 30 10000		# 0.059357441
java -Djava.ext.dirs=. TPAGenerator -model SA 10000 power-law 1 power-law 0.8 poisson 5 30 10000	# 0.074289608
java -Djava.ext.dirs=. TPAGenerator -model SA 50000 power-law 1 power-law 0.8 poisson 5 30 10000	# 0.171666254
java -Djava.ext.dirs=. TPAGenerator -model SA 100000 power-law 1 power-law 0.8 poisson 5 30 10000	# 0.263984947
java -Djava.ext.dirs=. TPAGenerator -model SA 500000 power-law 1 power-law 0.8 poisson 5 30 10000	# 0.985441112
java -Djava.ext.dirs=. TPAGenerator -model SA 1000000 power-law 1 power-law 0.8 poisson 5 30 10000	# 2.39359242
java -Djava.ext.dirs=. TPAGenerator -model SA 5000000 power-law 1 power-law 0.8 poisson 5 30 10000	# 10.770635143
java -Djava.ext.dirs=. TPAGenerator -model SA 10000000 power-law 1 power-law 0.8 poisson 5 30 10000	# 37.201864107
java -Djava.ext.dirs=. TPAGenerator -model SA 50000000 power-law 1 power-law 0.8 poisson 5 30 10000	# 285.397896472

# Hybrid
java -Djava.ext.dirs=. TPAGenerator -model Mixed 1000 power-law 1 power-law 0.8 poisson 5 30 10000 100000	# 0.035174367
java -Djava.ext.dirs=. TPAGenerator -model Mixed 5000 power-law 1 power-law 0.8 poisson 5 30 10000 100000	# 0.063525619
java -Djava.ext.dirs=. TPAGenerator -model Mixed 10000 power-law 1 power-law 0.8 poisson 5 30 10000 100000	# 0.083358644
java -Djava.ext.dirs=. TPAGenerator -model Mixed 50000 power-law 1 power-law 0.8 poisson 5 30 10000 100000	# 0.191110542
java -Djava.ext.dirs=. TPAGenerator -model Mixed 100000 power-law 1 power-law 0.8 poisson 5 30 10000 100000	# 0.31267765
java -Djava.ext.dirs=. TPAGenerator -model Mixed 500000 power-law 1 power-law 0.8 poisson 5 30 10000 100000	# 1.339946444
java -Djava.ext.dirs=. TPAGenerator -model Mixed 1000000 power-law 1 power-law 0.8 poisson 5 30 10000 100000	# 2.185901115
java -Djava.ext.dirs=. TPAGenerator -model Mixed 5000000 power-law 1 power-law 0.8 poisson 5 30 10000 100000	# 7.454454936
java -Djava.ext.dirs=. TPAGenerator -model Mixed 10000000 power-law 1 power-law 0.8 poisson 5 30 10000 100000	# 14.122385316
java -Djava.ext.dirs=. TPAGenerator -model Mixed 50000000 power-law 1 power-law 0.8 poisson 5 30 100000 100000	# 70.754305607
java -Djava.ext.dirs=. TPAGenerator -model Mixed 100000000 power-law 1 power-law 0.8 poisson 5 30 100000 100000	# 127.190342333





