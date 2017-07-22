import numpy as np
import utility
import argparse

#############################################################################################
# Parse command line arguments
parser = argparse.ArgumentParser(description='Train HMM Model.')
parser.add_argument('-training_data', default='/home/mehedi/teana/data-source/seq-analysis/deepLearn/unbalanced/cht-cml/train_shuffled.txt'
                    , help='File location containing training sequence.')
parser.add_argument('-testing_data', default='/home/mehedi/teana/data-source/seq-analysis/deepLearn/unbalanced/cht-cml/test.txt',
                    help='File location containing testing sequence.')
parser.add_argument('-codebook', default='/home/mehedi/teana/data-source/seq-analysis/deepLearn/codebook.txt',
                    help='File location containing codebook.')
parser.add_argument('-model_path', default='/home/mehedi/teana/data-source/seq-analysis/deepLearn/model.h5',
                    help='Directory to save model.')
parser.add_argument('-output_directory', default='/home/mehedi/teana/data-source/seq-analysis/deepLearn/',
                    help='Directory to save results.')

args = parser.parse_args()

############################################################################################
# Load up training data
training_filename = args.training_data
testing_filename = args.testing_data
codebook_filename = args.codebook
model_path = args.model_path
output_directory = args.output_directory

# prepare data
codebook = utility.loadCodeBook(codebook_filename)
sequences, seq_lengths = utility.loadData(training_filename, codebook)

# fit model
n_states = 5
n_observations = len(codebook)
model = utility.getHMMModel(n_states, n_observations, sequences, seq_lengths)

print "\nAfter model fitting..."
print model.startprob_
print model.transmat_
print model.emissionprob_

# get log likelihood for the given sequence(s)
test_seq = np.array([[0], [2], [1], [1], [2], [0]])
logL = model.score(test_seq)
print "\nLog likelihood of the sequence: ", logL, "\n"
