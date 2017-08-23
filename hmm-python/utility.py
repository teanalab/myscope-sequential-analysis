import numpy as np
import random
import re
import operator
from hmmlearn import hmm


###################################################################
# read codebook from file
def loadCodeBook(codebook_filename):
    codebook = []
    with open(codebook_filename, "r") as filestream:
        for line in filestream:
            codebook.append(line.replace("\n", ""))
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
    model = hmm.MultinomialHMM(n_components=n_states, random_state=42)
    model.startprob_ = start_probability
    model.transmat_ = transition_probability
    model.emissionprob_ = emission_probability

    # fit model
    model = model.fit(sequences, seq_lengths)
    return model


#############################################################################################
def getMacroAveragePerformance(actual, predicted):
    precision = 0.0
    recall = 0.0
    f_measure = 0.0
    accuracy = 0.0

    labels = ["500", "400", "400", "500"]

    for k in [0, 2]:

        tp = 0
        fp = 0
        tn = 0
        fn = 0

        for i in range(0, len(actual)):
            if actual[i] == predicted[i] and actual[i] == labels[k]:
                tp += 1
            elif actual[i] != predicted[i] and actual[i] == labels[k]:
                fn += 1
            elif actual[i] == predicted[i] and actual[i] == labels[k + 1]:
                tn += 1
            elif actual[i] != predicted[i] and actual[i] == labels[k + 1]:
                fp += 1

        local_precision = (float(tp) / (tp + fp))
        local_recall = (float(tp) / (tp + fn))
        local_f_measure = (float(2 * local_precision * local_recall) / (local_precision + local_recall))
        accuracy = (float(tp + tn) / (tp + fp + tn + fn))

        # for checking calculation
        # print tp, fp, tn, fn
        # print local_accuracy, local_precision, local_recall, local_f_measure

        precision += local_precision
        recall += local_recall
        f_measure += local_f_measure

    return accuracy, precision / 2, recall / 2, f_measure / 2


#############################################################################################
def getMicroAveragePerformance(actual, predicted):
    precision = 0.0
    recall = 0.0
    f_measure = 0.0
    accuracy = 0.0

    total_sample = len(actual)

    labels = ["500", "400", "400", "500"]

    for k in [0, 2]:

        tp = 0
        fp = 0
        tn = 0
        fn = 0

        for i in range(0, len(actual)):
            if actual[i] == predicted[i] and actual[i] == labels[k]:
                tp += 1
            elif actual[i] != predicted[i] and actual[i] == labels[k]:
                fn += 1
            elif actual[i] == predicted[i] and actual[i] == labels[k + 1]:
                tn += 1
            elif actual[i] != predicted[i] and actual[i] == labels[k + 1]:
                fp += 1

        local_precision = (float(tp) / (tp + fp))
        local_recall = (float(tp) / (tp + fn))
        local_f_measure = (float(2 * local_precision * local_recall) / (local_precision + local_recall))
        accuracy = (float(tp + tn) / (tp + fp + tn + fn))

        # for checking calculation
        # print tp, fp, tn, fn
        # print local_accuracy, local_precision, local_recall, local_f_measure

        precision += (local_precision * (float(tp + fn) / total_sample))
        recall += (local_recall * (float(tp + fn) / total_sample))
        f_measure += (local_f_measure * (float(tp + fn) / total_sample))

    return accuracy, precision, recall, f_measure


#############################################################################################
def createTrainAndTestFile(data, kFolds, training_filename, testing_filename):
    foldSize = len(data) / kFolds
    random.shuffle(data)

    test = data[:foldSize]
    train = data[foldSize:]
    with open(training_filename, "w") as output:
        for x in train:
            output.write(x)
    with open(testing_filename, "w") as output:
        for x in test:
            output.write(x)


#############################################################################################
def readAllData(data_filename):
    data = []
    with open(data_filename, "r") as filestream:
        for line in filestream:
            data.append(line)
    return data


#############################################################################################
def AUC(y_true, y_pred):
    not_y_pred = np.logical_not(y_pred)
    y_int1 = y_true * y_pred
    y_int0 = np.logical_not(y_true) * not_y_pred
    TP = np.sum(y_pred * y_int1)
    FP = np.sum(y_pred) - TP
    TN = np.sum(not_y_pred * y_int0)
    FN = np.sum(not_y_pred) - TN
    TPR = np.float(TP) / (TP + FN)
    FPR = np.float(FP) / (FP + TN)
    return ((1 + TPR - FPR) / 2)
