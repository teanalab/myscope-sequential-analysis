#**************************
# Author: Md Mehedi Hsan  *
# Wayne State University  *
# email: mehedi@wayne.edu *
#**************************

import numpy as np
import utility
import argparse
from collections import Counter
import operator

# Parse command line arguments
parser = argparse.ArgumentParser(description='Train Markov Model.')
parser.add_argument('--order', default=1, type=int, help='order of the markov model.')
parser.add_argument('--codebook', default='codebook.txt', help='File location containing codebook.')
parser.add_argument('--trans', default=1, type=int, help='Indicate whether it will be frequency or probabilty')


# Read parameters
args = parser.parse_args()
codebook_filename = args.codebook
n_order = args.order
trans = args.trans


# get results fro k folds
macro_results = []
micro_results = []
codebook = utility.loadCodeBook(codebook_filename)

success_fp = open("success_transition_order"+str(n_order)+".csv", "w")
unsuccess_fp = open("unsuccess_transition_order"+str(n_order)+".csv", "w")

# get train and test data
training_filename_suc = "Data/successful.txt"
training_filename_unsuc = "Data/unsuccessful.txt"

# set dictionary for successful sequences
successful_dict = utility.loadTransitionDictionary(training_filename_suc, n_order, "500")

# set dictionary for unsuccessful sequences
unsuccessful_dict = utility.loadTransitionDictionary(training_filename_unsuc, n_order, "400")

successful_sorted_map = sorted(successful_dict.items(), key=operator.itemgetter(0))

for state in codebook:
    success_fp.write("," + state)
    unsuccess_fp.write("," + state)
success_fp.write("\n")
unsuccess_fp.write("\n")

for key, val in successful_sorted_map:
    next_states_counter = Counter(val)
    one_line = []
    clean_key = str(key).replace("(", "").replace(")", "").replace(",", "")
    one_line.append(clean_key)
    for state in codebook:
        trans_prob = 0.0
        if state in val:
            if trans > 1:
	        trans_prob = float(next_states_counter[state]) / len(val)
	        trans_prob = float("{0:.4f}".format(trans_prob))
	        one_line.append(str(trans_prob))
	    else:
            	one_line.append(str(next_states_counter[state]))
        else:
	    if trans > 1:
            	one_line.append(str(trans_prob))
	    else:
            	one_line.append(str(0))

    success_fp.write(",".join(one_line) + "\n")

unsuccessful_sorted_map = sorted(unsuccessful_dict.items(), key=operator.itemgetter(0))

for key, val in unsuccessful_sorted_map:
    next_states_counter = Counter(val)
    one_line = []
    clean_key = str(key).replace("(", "").replace(")", "").replace(",", "")
    one_line.append(clean_key)
    for state in codebook:
        trans_prob = 0.0
        if state in val:
	    if trans > 1:
	        trans_prob = float(next_states_counter[state]) / len(val)
	        trans_prob = float("{0:.4f}".format(trans_prob))
	        one_line.append(str(trans_prob))
	    else:
            	one_line.append(str(next_states_counter[state]))
        else:
	    if trans > 1:
            	one_line.append(str(trans_prob))
	    else:
            	one_line.append(str(0))

    unsuccess_fp.write(",".join(one_line) + "\n")
    

success_fp.close()
unsuccess_fp.close()
