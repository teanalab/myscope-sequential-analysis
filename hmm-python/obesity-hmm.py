import numpy as np
import utility
import argparse

#############################################################################################
# Parse command line arguments
parser = argparse.ArgumentParser(description='Train HMM Model.')
parser.add_argument('-training_data', default='/home/mehedi/teana/data-source/seq-analysis/hmm/balanced/cht-cml/train_shuffled.txt'
                    , help='File location containing training sequence.')
parser.add_argument('-testing_data', default='/home/mehedi/teana/data-source/seq-analysis/hmm/balanced/cht-cml/test.txt',
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

kFolds = 5
results = []
for k in np.arange(0, kFolds):

    # determine codebook size and number of hidden states
    n_states = 5
    success_codebook, unsuccess_codebook = utility.loadCodeBookFromTrainingFile(training_filename)

    print "\n Success: ", success_codebook,
    print "\n Unsuccess: ", unsuccess_codebook

    # fit successful model
    n_observations = len(success_codebook)
    sequences, seq_labels, seq_lengths = utility.loadData(training_filename, success_codebook, 1)
    success_model = utility.getHMMModel(n_states, n_observations, sequences, seq_lengths)

    print "\nAfter model fitting..."
    print success_model.startprob_
    print success_model.transmat_
    print success_model.emissionprob_

    # fit unsuccessful model
    n_observations = len(unsuccess_codebook)
    sequences, seq_labels, seq_lengths = utility.loadData(training_filename, unsuccess_codebook, 0)
    unsuccess_model = utility.getHMMModel(n_states, n_observations, sequences, seq_lengths)

    print "\nAfter model fitting..."
    print unsuccess_model.startprob_
    print unsuccess_model.transmat_
    print unsuccess_model.emissionprob_

    # get log likelihood for the given test sequence(s)
    sequences_for_success_model, seq_labels, success_seq_lengths = utility.loadData(testing_filename, success_codebook, 2)
    sequences_for_unsuccess_model, seq_labels, unsuccess_seq_lengths = utility.loadData(testing_filename, unsuccess_codebook, 2)

    success_seq_start_index = 0
    unsuccess_seq_start_index = 0
    pred_labels = []

    for i in range(0, len(success_seq_lengths)):
        success_seq = sequences_for_success_model[success_seq_start_index:(success_seq_start_index+success_seq_lengths[i])]
        unsuccess_seq = sequences_for_unsuccess_model[unsuccess_seq_start_index:(unsuccess_seq_start_index + unsuccess_seq_lengths[i])]
        success_seq_start_index += success_seq_lengths[i]
        unsuccess_seq_start_index += unsuccess_seq_lengths[i]
        success_logL = success_model.score(success_seq)
        unsuccess_logL = unsuccess_model.score(unsuccess_seq)
        if success_logL > unsuccess_logL:
            pred_labels.append("500")
        else:
            pred_labels.append("400")

    # print results
    pred_labels = np.array(pred_labels)
    accuracy, precision, recall, f_measure = utility.getPerformance(seq_labels, pred_labels)

    print "\nOverall results: "
    print "Accuracy:", accuracy, "Precision:", precision, "Recall:", recall, "F1:", f_measure

    fold_result = [k, accuracy, precision, recall, f_measure]
    results.append(fold_result)

############################################################################################
print "Average results: ", (np.mean(results, axis=0))