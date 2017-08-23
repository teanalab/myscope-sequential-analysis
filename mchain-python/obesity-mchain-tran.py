import numpy as np
import utility
import argparse
import random
import math
from collections import Counter

#############################################################################################
# Parse command line arguments
parser = argparse.ArgumentParser(description='Train Markov Model.')
parser.add_argument('--codebook', default='/home/mehedi/teana/data-source/seq-analysis/deepLearn/codebook_improve_new.txt',
                    help='File location containing codebook.')
parser.add_argument('--sampling', default='under', type=str, help='sampling parameter.')
parser.add_argument('--order', default=1, type=int, help='order of the markov model.')

args = parser.parse_args()

############################################################################################
# Load up training data
codebook_filename = args.codebook
sampling = args.sampling
n_order = args.order
training_filename = "train.txt"
testing_filename = "test.txt"

# get results fro k folds
kFolds = 10
macro_results = []
micro_results = []
codebook = utility.loadCodeBook(codebook_filename)
foldData, max_len = utility.createStartifiedFolds(codebook, kFolds)


for k in np.arange(0, kFolds):
    # get train and test data
    utility.createUnderOrOverSample(sampling, foldData[k][0], testing_filename, max_len, codebook)
    utility.createUnderOrOverSample(sampling, foldData[k][1], training_filename, max_len, codebook)

    # set dictionary for successful sequences
    successful_dict = utility.loadTransitionDictionary(training_filename, n_order, "500")

    # set dictionary for unsuccessful sequences
    unsuccessful_dict = utility.loadTransitionDictionary(training_filename, n_order, "400")

    # set likelihood of the given test sequence(s) and then classify
    prediction_labels = []
    seq_labels = []

    with open(testing_filename, "r") as file_stream:
        for line in file_stream:
            words = line.replace("\n", "").split(",")
            actual_label = words[len(words)-1]
            seq_labels.append(actual_label)
            if len(words) <= n_order:
                print "Short sequence: use random guess"
                guess = random.randint(0, 1)
                if guess > 0:
                    prediction_labels.append("500")
                else:
                    prediction_labels.append("400")
                continue

            successful_prob = math.log(0.5)
            unsuccessful_prob = math.log(0.5)

            # get probability of generating sequence from successful transcript
            words[len(words)-1] = "500"
            for i in xrange(0, len(words) - n_order - 1):
                current_tuple = tuple([words[j] for j in xrange(i, i + n_order)])
                if current_tuple in successful_dict.keys():
                    next_states = successful_dict[current_tuple]
                    next_states_counter = Counter(next_states)
                    transition_prob = -5.0
                    if words[i + n_order] in next_states_counter:
                        transition_prob = math.log(float(next_states_counter[words[i + n_order]]) / len(next_states))
                    successful_prob += transition_prob
                else:
                    successful_prob -= 5.0

            # get probability of generating sequence from unsuccessful transcript
            words[len(words) - 1] = "400"
            for i in xrange(0, len(words) - n_order - 1):
                current_tuple = tuple([words[j] for j in xrange(i, i + n_order)])
                if current_tuple in unsuccessful_dict.keys():
                    next_states = unsuccessful_dict[current_tuple]
                    next_states_counter = Counter(next_states)
                    transition_prob = -5.0
                    if words[i + n_order] in next_states_counter:
                        transition_prob = math.log(float(next_states_counter[words[i + n_order]]) / len(next_states))
                    unsuccessful_prob += transition_prob
                else:
                    unsuccessful_prob -= 5.0

            # classify sequence
            if (successful_prob - unsuccessful_prob) > 0.0:
                prediction_labels.append("500")
            else:
                prediction_labels.append("400")

    # store results in macro average
        prediction_labels = np.array(prediction_labels)
    accuracy, precision, recall, f_measure = utility.getMacroAveragePerformance(seq_labels, prediction_labels)
    print "\nResults for fold", (
        k + 1), ": Accuracy:", accuracy, "Precision:", precision, "Recall:", recall, "F1:", f_measure
    fold_result = [k, accuracy, precision, recall, f_measure]
    macro_results.append(fold_result)

    # store results in micro average
    accuracy, precision, recall, f_measure = utility.getMicroAveragePerformance(seq_labels, prediction_labels)
    print "\nResults for fold", (
        k + 1), ": Accuracy:", accuracy, "Precision:", precision, "Recall:", recall, "F1:", f_measure
    fold_result = [k, accuracy, precision, recall, f_measure]
    micro_results.append(fold_result)

############################################################################################
print "\nMacro average results: ", (np.mean(macro_results, axis=0))
print "\nMicro average results: ", (np.mean(micro_results, axis=0))

f = open("results.txt", "a")
f.write(str(n_order) + ",")
for x in np.mean(macro_results, axis=0):
    f.write(str(x) + ",")
for x in np.mean(micro_results, axis=0):
    f.write(str(x) + ",")
f.write("\n")
f.close()
