#**************************
# Author: Md Mehedi Hsan  *
# Wayne State University  *
# email: mehedi@wayne.edu *
#**************************

import numpy as np
import utility
import argparse

# Parse command line arguments
parser = argparse.ArgumentParser(description='Train HMM Model.')
parser.add_argument('--succ_hidden_states', default=5, type=int, help='number of hidden states for successful sequences')
parser.add_argument('--unsucc_hidden_states', default=2, type=int, help='number of hidden states for unsuccessful sequences')
parser.add_argument('--codebook', default='codebook.txt', help='File location containing codebook.')

# Read parameters
args = parser.parse_args()
succ_hidden_states = args.succ_hidden_states
unsucc_hidden_states = args.unsucc_hidden_states
codebook_filename = args.codebook

# initialize some variables
macro_results = []
micro_results = []
codebook = utility.loadCodeBook(codebook_filename)

success_start_fp = open("success_start.csv", "w")
success_trans_fp = open("success_transition.csv", "w")
success_emission_fp = open("success_emission.csv", "w")

unsuccess_start_fp = open("unsuccess_start.csv", "w")
unsuccess_trans_fp = open("unsuccess_transition.csv", "w")
unsuccess_emission_fp = open("unsuccess_emission.csv", "w")

# get train and test data
training_filename = "Data/successful_unsuccessful.txt"

# determine codebook size and number of hidden states
all_codebook, success_codebook, unsuccess_codebook = utility.loadCodeBookFromTrainingFile(training_filename)

# fit successful model
n_observations = len(success_codebook)
sequences, seq_labels, seq_lengths = utility.loadData(training_filename, success_codebook, 1)
success_model = utility.getHMMModel(succ_hidden_states, n_observations, sequences, seq_lengths)

for i in np.arange(0, len(success_model.startprob_)):
    success_start_fp.write("state" + str(i + 1) + ",")
    success_trans_fp.write("," + "state" + str(i + 1))

success_start_fp.write("\n")
success_trans_fp.write("\n")

for state_prob in success_model.startprob_:
    success_start_fp.write(str(float("{0:.4f}".format(state_prob))) + ",")

counter = 1
for line_state_prob in success_model.transmat_:
    success_trans_fp.write("state" + str(counter))
    counter += 1
    for state_prob in line_state_prob:
        success_trans_fp.write("," + str(float("{0:.4f}".format(state_prob))))
    success_trans_fp.write("\n")

for symbol in success_codebook:
    success_emission_fp.write("," + symbol)

success_emission_fp.write("\n")
counter = 1
for line_emission_prob in success_model.emissionprob_:
    success_emission_fp.write("state" + str(counter))
    counter += 1
    for symbol_prob in line_emission_prob:
        success_emission_fp.write("," + str(float("{0:.4f}".format(symbol_prob))))
    success_emission_fp.write("\n")

# fit unsuccessful model
n_observations = len(unsuccess_codebook)
sequences, seq_labels, seq_lengths = utility.loadData(training_filename, unsuccess_codebook, 0)
unsuccess_model = utility.getHMMModel(unsucc_hidden_states, n_observations, sequences, seq_lengths)

for i in np.arange(0, len(unsuccess_model.startprob_)):
    unsuccess_start_fp.write("state" + str(i + 1) + ",")
    unsuccess_trans_fp.write("," + "state" + str(i + 1))

unsuccess_start_fp.write("\n")
unsuccess_trans_fp.write("\n")

for state_prob in unsuccess_model.startprob_:
    unsuccess_start_fp.write(str(float("{0:.4f}".format(state_prob))) + ",")

counter = 1
for line_state_prob in unsuccess_model.transmat_:
    unsuccess_trans_fp.write("state" + str(counter))
    counter += 1
    for state_prob in line_state_prob:
        unsuccess_trans_fp.write("," + str(float("{0:.4f}".format(state_prob))))
    unsuccess_trans_fp.write("\n")

for symbol in unsuccess_codebook:
    unsuccess_emission_fp.write("," + symbol)

unsuccess_emission_fp.write("\n")
counter = 1
for line_emission_prob in unsuccess_model.emissionprob_:
    unsuccess_emission_fp.write("state" + str(counter))
    counter += 1
    for symbol_prob in line_emission_prob:
        unsuccess_emission_fp.write("," + str(float("{0:.4f}".format(symbol_prob))))
    unsuccess_emission_fp.write("\n")
    

# Close file pointers
success_start_fp.close()
success_trans_fp.close()
success_emission_fp.close()

unsuccess_start_fp.close()
unsuccess_trans_fp.close()
unsuccess_emission_fp.close()
