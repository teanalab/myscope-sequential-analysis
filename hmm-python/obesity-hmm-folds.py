import numpy as np
import utility
import argparse

#############################################################################################
# Parse command line arguments
parser = argparse.ArgumentParser(description='Train HMM Model.')
# parser.add_argument('-data',
#                     default='/home/mehedi/teana/data-source/seq-analysis/hmm/unbalanced/cht-cml/allsequence.txt',
#                     help='File location containing training sequence.')
# parser.add_argument('-output_directory', default='/home/mehedi/teana/data-source/seq-analysis/hmm/',
#                     help='Directory to save results.')

parser.add_argument('-data',
                    default='/home/mehedi/teana/data-source/seq-analysis/hmm/balanced/cht-cml/over-sampling-reg-old.txt',
                    help='File location containing training sequence.')
parser.add_argument('-output_directory', default='/home/mehedi/teana/data-source/seq-analysis/hmm/',
                    help='Directory to save results.')

args = parser.parse_args()

############################################################################################
# Load up training data
data_filename = args.data
output_directory = args.output_directory
training_filename = output_directory + "train.txt"
testing_filename = output_directory + "test.txt"

# read entire data to list
data = utility.readAllData(data_filename)

# get results fro k folds
kFolds = 10
macro_results = []
micro_results = []
for k in np.arange(0, kFolds):
    # create training and testing file
    utility.createTrainAndTestFile(data, kFolds, training_filename, testing_filename)

    # determine codebook size and number of hidden states
    n_states = 5
    success_codebook, unsuccess_codebook = utility.loadCodeBookFromTrainingFile(training_filename)

    # fit successful model
    n_observations = len(success_codebook)
    sequences, seq_labels, seq_lengths = utility.loadData(training_filename, success_codebook, 1)
    success_model = utility.getHMMModel(n_states, n_observations, sequences, seq_lengths)

    # fit unsuccessful model
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
