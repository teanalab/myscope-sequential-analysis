import numpy as np
import utility
import argparse
import random

#############################################################################################
# Parse command line arguments
parser = argparse.ArgumentParser(description='Train HMM Model.')
parser.add_argument('--hidden_states', default=5, type=int, help='number of hidden states')
parser.add_argument('--codebook', default='codebook.txt', help='File location containing codebook.')
parser.add_argument('--sampling', default='over', type=str, help='dropout rate parameter.')

############################################################################################
# Read parameters
args = parser.parse_args()
codebook_filename = args.codebook
sampling = args.sampling
hidden_states = args.hidden_states

# initialize some variables
kFolds = 10
training_filename = "train.txt"
testing_filename = "test.txt"
macro_results = []
micro_results = []
codebook = utility.loadCodeBook(codebook_filename)

for k in np.arange(0, kFolds):
    # get train and test data
    training_filename = sampling + "/folds/fold" + str(k + 1) + "/train.txt"
    testing_filename = sampling + "/folds/fold" + str(k + 1) + "/test.txt"

    # determine codebook size and number of hidden states
    all_codebook, success_codebook, unsuccess_codebook = utility.loadCodeBookFromTrainingFile(training_filename)

    # fit successful model
    n_states = 5
    n_observations = len(success_codebook)
    sequences, seq_labels, seq_lengths = utility.loadData(training_filename, success_codebook, 1)
    success_model = utility.getHMMModel(n_states, n_observations, sequences, seq_lengths)

    # fit unsuccessful model
    n_states = 22
    n_observations = len(unsuccess_codebook)
    sequences, seq_labels, seq_lengths = utility.loadData(training_filename, unsuccess_codebook, 0)
    unsuccess_model = utility.getHMMModel(n_states, n_observations, sequences, seq_lengths)

    # get log likelihood for the given test sequence(s)
    sequences_for_success_model, seq_labels, success_seq_lengths = utility.loadData(testing_filename, success_codebook,
                                                                                    2)
    sequences_for_unsuccess_model, seq_labels, unsuccess_seq_lengths = utility.loadData(testing_filename,
                                                                                        unsuccess_codebook, 2)

    success_seq_start_index = 0
    unsuccess_seq_start_index = 0
    pred_labels = []

    for i in range(0, len(success_seq_lengths)):
        if success_seq_lengths[i] == 0 or unsuccess_seq_lengths[i] == 0:
            # use random guess
            guess = random.randint(0, 1)
            if guess > 0:
                pred_labels.append("500")
            else:
                pred_labels.append("400")
            continue

        success_seq = sequences_for_success_model[
                      success_seq_start_index:(success_seq_start_index + success_seq_lengths[i])]
        unsuccess_seq = sequences_for_unsuccess_model[
                        unsuccess_seq_start_index:(unsuccess_seq_start_index + unsuccess_seq_lengths[i])]
        success_seq_start_index += success_seq_lengths[i]
        unsuccess_seq_start_index += unsuccess_seq_lengths[i]
        success_logL = success_model.score(success_seq)
        unsuccess_logL = unsuccess_model.score(unsuccess_seq)
        if (success_logL - unsuccess_logL) > 0.0:
            pred_labels.append("500")
        else:
            pred_labels.append("400")

    # store results in macro average
    pred_labels = np.array(pred_labels)
    accuracy, precision, recall, f_measure = utility.getMacroAveragePerformance(seq_labels, pred_labels)
    print "\nResults for fold", (
        k + 1), ": Accuracy:", accuracy, "Precision:", precision, "Recall:", recall, "F1:", f_measure
    fold_result = [k, accuracy, precision, recall, f_measure]
    macro_results.append(fold_result)

    # store results in micro average
    accuracy, precision, recall, f_measure = utility.getMicroAveragePerformance(seq_labels, pred_labels)
    print "\nResults for fold", (
        k + 1), ": Accuracy:", accuracy, "Precision:", precision, "Recall:", recall, "F1:", f_measure
    fold_result = [k, accuracy, precision, recall, f_measure]
    micro_results.append(fold_result)

############################################################################################
print "\nMacro average results: ", (np.mean(macro_results, axis=0))
print "\nMicro average results: ", (np.mean(micro_results, axis=0))

f = open("results.txt", "a")
f.write(str(n_states) + ",")
for x in np.mean(macro_results, axis=0):
    f.write(str(x) + ",")
for x in np.mean(micro_results, axis=0):
    f.write(str(x) + ",")
f.write("\n")
f.close()
