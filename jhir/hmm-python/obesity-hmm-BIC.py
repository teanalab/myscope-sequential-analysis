#**************************
# Author: Md Mehedi Hsan  *
# Wayne State University  *
# email: mehedi@wayne.edu *
#**************************

import numpy as np
import utility
import argparse
import math

# Parse command line arguments
parser = argparse.ArgumentParser(description='Train HMM Model.')
parser.add_argument('--succ_hidden_states', default=17, type=int, help='number of hidden states for successful sequences')
parser.add_argument('--unsucc_hidden_states', default=11, type=int, help='number of hidden states for unsuccessful sequences')
parser.add_argument('--codebook', default='codebook.txt', help='File location containing codebook.')

# Read parameters
args = parser.parse_args()
succ_hidden_states = args.succ_hidden_states
unsucc_hidden_states = args.unsucc_hidden_states
codebook_filename = args.codebook

# initialize some variables
codebook = utility.loadCodeBook(codebook_filename)

# open file to write results
f_succ = open("results_succ_bic.txt", "a")
f_unsucc = open("results_unsucc_bic.txt", "a")

# get train and test data
training_filename = "Data/successful_unsuccessful.txt"

# determine codebook size and number of hidden states
all_codebook, success_codebook, unsuccess_codebook = utility.loadCodeBookFromTrainingFile(training_filename)

for h_state in range(1, 26, 1):

    succ_hidden_states = h_state
    unsucc_hidden_states = h_state

    # fit successful model
    n_observations = len(success_codebook)
    sequences, seq_labels, seq_lengths = utility.loadData(training_filename, success_codebook, 1)
    success_model = utility.getHMMModel(succ_hidden_states, n_observations, sequences, seq_lengths)

    logLike = success_model.score(sequences, seq_lengths)
    k = (len(success_model.startprob_)*(len(success_model.startprob_)-1) + (len(success_model.startprob_)-1)
         + ((len(success_model.startprob_))*(n_observations-1)))

    # succ_bic = -2 * logLike + k * math.log(n_observations)
    succ_bic = -2 * logLike + k * math.log(len(seq_lengths))
    succ_aic = -2 * logLike + k * 2
    # succ_bic = logLike

    # fit unsuccessful model
    n_observations = len(unsuccess_codebook)
    sequences, seq_labels, seq_lengths = utility.loadData(training_filename, unsuccess_codebook, 0)
    unsuccess_model = utility.getHMMModel(unsucc_hidden_states, n_observations, sequences, seq_lengths)

    logLike = unsuccess_model.score(sequences, seq_lengths)
    k = (len(unsuccess_model.startprob_) * (len(unsuccess_model.startprob_) - 1) + (len(unsuccess_model.startprob_) - 1)
         + ((len(unsuccess_model.startprob_)) * (n_observations - 1)))

    # unsucc_bic = -2 * logLike + k * math.log(n_observations)
    unsucc_bic = -2 * logLike + k * math.log(len(seq_lengths))
    unsucc_aic = -2 * logLike + k * 2
    # unsucc_bic = logLike

    # write results into file
    f_succ.write(str(succ_hidden_states) + "," + str(succ_bic) + "," + str(succ_aic))
    f_succ.write("\n")
    f_succ.flush()
    f_unsucc.write(str(unsucc_hidden_states) + "," + str(unsucc_bic) + "," + str(unsucc_aic))
    f_unsucc.write("\n")
    f_unsucc.flush()

    print("Computed for " + str(h_state) + " hidden states." + " S_BIC: " + str(succ_bic) 
    + ", UNS_BIC: " + str(unsucc_bic) + " S_AIC: " + str(succ_aic) + ", UNS_AIC: " + str(unsucc_aic))
    
f_succ.close()
f_unsucc.close()
