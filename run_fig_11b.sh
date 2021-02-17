# command											gen time (sec)
# RW 1,000
java -Djava.ext.dirs=. TPAGenerator -model RW 1000 power-law 1 power-law 0.8 poisson 5 30 31	# 1.685929817
# RW 5,000
java -Djava.ext.dirs=. TPAGenerator -model RW 5000 power-law 1 power-law 0.8 poisson 5 30 44	# 56.326724217
# RW 10,000
java -Djava.ext.dirs=. TPAGenerator -model RW 10000 power-law 1 power-law 0.8 poisson 5 30 53	# 182.834920913

# SA 
java -Djava.ext.dirs=. TPAGenerator -model SA 1000 power-law 1 power-law 0.8 poisson 5 30 150		# 0.023189241
java -Djava.ext.dirs=. TPAGenerator -model SA 5000 power-law 1 power-law 0.8 poisson 5 30 200		# 0.049674661
java -Djava.ext.dirs=. TPAGenerator -model SA 10000 power-law 1 power-law 0.8 poisson 5 30 280		# 0.059310762
java -Djava.ext.dirs=. TPAGenerator -model SA 50000 power-law 1 power-law 0.8 poisson 5 30 400		# 0.181805616
java -Djava.ext.dirs=. TPAGenerator -model SA 100000 power-law 1 power-law 0.8 poisson 5 30 500		# 0.322056854
java -Djava.ext.dirs=. TPAGenerator -model SA 500000 power-law 1 power-law 0.8 poisson 5 30 670		# 1.858955992
java -Djava.ext.dirs=. TPAGenerator -model SA 1000000 power-law 1 power-law 0.8 poisson 5 30 800	# 3.389019196
java -Djava.ext.dirs=. TPAGenerator -model SA 5000000 power-law 1 power-law 0.8 poisson 5 30 1000	# 29.400322204
java -Djava.ext.dirs=. TPAGenerator -model SA 10000000 power-law 1 power-law 0.8 poisson 5 30 1300	# 59.635529214	

# Hybrid
java -Djava.ext.dirs=. TPAGenerator -model Mixed 1000 power-law 1 power-law 0.8 poisson 5 30 150 100000		# 0.024650496
java -Djava.ext.dirs=. TPAGenerator -model Mixed 5000 power-law 1 power-law 0.8 poisson 5 30 200 100000		# 0.048468204
java -Djava.ext.dirs=. TPAGenerator -model Mixed 10000 power-law 1 power-law 0.8 poisson 5 30 280 100000	# 0.067952886
java -Djava.ext.dirs=. TPAGenerator -model Mixed 50000 power-law 1 power-law 0.8 poisson 5 30 400 100000	# 0.18801412
java -Djava.ext.dirs=. TPAGenerator -model Mixed 100000 power-law 1 power-law 0.8 poisson 5 30 500 100000	# 0.352276734
java -Djava.ext.dirs=. TPAGenerator -model Mixed 500000 power-law 1 power-law 0.8 poisson 5 30 670 100000	# 1.744999149
java -Djava.ext.dirs=. TPAGenerator -model Mixed 1000000 power-law 1 power-law 0.8 poisson 5 30 800 100000	# 3.007089629
java -Djava.ext.dirs=. TPAGenerator -model Mixed 5000000 power-law 1 power-law 0.8 poisson 5 30 2000 100000	# 10.582567855
java -Djava.ext.dirs=. TPAGenerator -model Mixed 10000000 power-law 1 power-law 0.8 poisson 5 30 3000 100000	# 21.896990997
java -Djava.ext.dirs=. TPAGenerator -model Mixed 50000000 power-law 1 power-law 0.8 poisson 5 30 12000 100000	# 107.979303208
java -Djava.ext.dirs=. TPAGenerator -model Mixed 100000000 power-law 1 power-law 0.8 poisson 5 30 23000 100000	# 201.025225746























