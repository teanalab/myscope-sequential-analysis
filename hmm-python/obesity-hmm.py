import numpy as np
import utility
import argparse

#############################################################################################
# Parse command line arguments
parser = argparse.ArgumentParser(description='Train HMM Model.')
parser.add_argument('-training_data', default='/home/mehedi/teana/data-source/seq-analysis/hmm/unbalanced/cht-cml/train_shuffled.txt'
                    , help='File location containing training sequence.')
parser.add_argument('-testing_data', default='/home/mehedi/teana/data-source/seq-analysis/hmm/unbalanced/cht-cml/test.txt',
                    help='File location containing testing sequence.')
parser.add_argument('-codebook', default='/home/mehedi/teana/data-source/seq-analysis/hmm/codebook.txt',
                    help='File location containing codebook.')
parser.add_argument('-output_directory', default='/home/mehedi/teana/data-source/seq-analysis/hmm/',
                    help='Directory to save results.')

args = parser.parse_args()

############################################################################################
# Load up training data
training_filename = args.training_data
testing_filename = args.testing_data
codebook_filename = args.codebook
output_directory = args.output_directory

# determine codebook size and number of hidden states
n_states = 5
codebook = utility.loadCodeBook(codebook_filename)
n_observations = len(codebook)

# fit successful model
sequences, seq_labels, seq_lengths = utility.loadData(training_filename, codebook)
success_model = utility.getHMMModel(n_states, n_observations, sequences, seq_lengths)

print "\nAfter model fitting..."
print success_model.startprob_
print success_model.transmat_
print success_model.emissionprob_

# fit unsuccessful model
sequences, seq_labels, seq_lengths = utility.loadData(training_filename, codebook)
unsuccess_model = utility.getHMMModel(n_states, n_observations, sequences, seq_lengths)

print "\nAfter model fitting..."
print unsuccess_model.startprob_
print unsuccess_model.transmat_
print unsuccess_model.emissionprob_

# get log likelihood for the given test sequence(s)
seq_start_index = 0
lbl_index = 0
sequences, seq_labels, seq_lengths = utility.loadData(testing_filename, codebook)
pred_labels = []
for seq_len in seq_lengths:
    seq = sequences[seq_start_index:(seq_start_index+seq_len)]
    seq_start_index += seq_len
    lbl_index += 1
    success_logL = success_model.score(seq)
    unsuccess_logL = unsuccess_model.score(seq)
    if success_logL > unsuccess_logL:
        pred_labels.append("500")
    else:
        pred_labels.append("400")

# print results
pred_labels = np.array(pred_labels)
accuracy, precision, recall, f_measure = utility.getPerformance(seq_labels, pred_labels)

print "\nOverall results: "
print "Accuracy:", accuracy, "Precision:", precision, "Recall:", recall, "F1:", f_measure
