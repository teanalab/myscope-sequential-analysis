import numpy as np
import re
import operator
from hmmlearn import hmm


###################################################################
# read codebook from file
def loadCodeBook(codebook_filename):
    codebook = []
    with open(codebook_filename, "r") as filestream:
        for line in filestream:
            codebook.append(line[:3])
    return codebook

###########################################################################################
def loadCodeBookFromTrainingFile(fileLocation):
    success_code_map = {}
    unsuccess_code_map = {}
    with open(fileLocation, "r") as filestream:
        for line in filestream:
            l = re.sub(r"\s+", "", line).split(",")
            if l[len(l) - 1] == "500":
                for i in range(0, len(l) - 1):
                    success_code_map[l[i]] = l[i]
            elif l[len(l) - 1] == "400":
                for i in range(0, len(l) - 1):
                    unsuccess_code_map[l[i]] = l[i]

    success_sorted_map = sorted(success_code_map.items(), key=operator.itemgetter(0))
    unsuccess_sorted_map = sorted(unsuccess_code_map.items(), key=operator.itemgetter(0))

    success_map = []
    unsuccess_map = []
    for key, value in success_sorted_map:
        success_map.append(key)
    for key, value in unsuccess_sorted_map:
        unsuccess_map.append(key)

    return success_map, unsuccess_map

###########################################################################################
def loadData(fileLocation, codebook, flag):
    code_to_int = dict((c, i) for i, c in enumerate(codebook))
    seq = []
    seq_label = []
    lengths = []
    map = {}
    with open(fileLocation, "r") as filestream:
        for line in filestream:
            l = re.sub(r"\s+", "", line).split(",")
            if (flag == 1 and l[len(l) - 1] == "500") or (flag == 0 and l[len(l) - 1] == "400"):
                for i in range(0, len(l) - 1):
                    seq.append([code_to_int[l[i]]])
                    map[l[i]] = l[i]
                lengths.append(len(l) - 1)
                seq_label.append(l[len(l) - 1])
            elif flag == 2:
                var_len = 0
                for i in range(0, len(l) - 1):
                    if l[i] in code_to_int.keys():
                        seq.append([code_to_int[l[i]]])
                        map[l[i]] = l[i]
                        var_len += 1
                lengths.append(var_len)
                seq_label.append(l[len(l) - 1])

    # sorted_map = sorted(map.items(), key=operator.itemgetter(0))
    # for key, value in sorted_map:
    #    print key
    return np.array(seq), np.array(seq_label), np.array(lengths)


############################################################################################
def getHMMModel(n_states, n_observations, sequences, seq_lengths):
    start_probability = np.ones(n_states)
    start_probability = start_probability / n_states

    transition_probability = np.ones(n_states * n_states).reshape(n_states, n_states)
    transition_probability = transition_probability / n_states

    emission_probability = np.ones(n_states * n_observations).reshape(n_states, n_observations)
    emission_probability = emission_probability / n_observations

    # create model and set initial values
    model = hmm.MultinomialHMM(n_components=n_states)
    model.startprob_ = start_probability
    model.transmat_ = transition_probability
    model.emissionprob_ = emission_probability

    # fit model
    model = model.fit(sequences, seq_lengths)
    return model


#############################################################################################
def getPerformance(actual, predicted):
    tp = 0
    fp = 0
    tn = 0
    fn = 0

    for i in range(0, len(actual)):
        if actual[i] == predicted[i] and actual[i] == "500":
            tp += 1
        elif actual[i] != predicted[i] and actual[i] == "500":
            fn += 1
        elif actual[i] == predicted[i] and actual[i] == "400":
            tn += 1
        elif actual[i] != predicted[i] and actual[i] == "400":
            fp += 1

    #print tp, fp, tn, fn
    precision = float(tp) / (tp + fp)
    recall = float(tp) / (tp + fn)
    f_measure = float(2 * precision * recall) / (precision + recall)
    accuracy = float(tp + tn) / (tp + fp + tn + fn)

    return accuracy, precision, recall, f_measure
