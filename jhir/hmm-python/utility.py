import numpy as np
import random
import re
import operator
from hmmlearn import hmm
from imblearn.over_sampling import SMOTE
from collections import Counter
from imblearn.under_sampling import ClusterCentroids

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
    all_code_map = {}
    success_code_map = {}
    unsuccess_code_map = {}
    with open(fileLocation, "r") as filestream:
        for line in filestream:
            l = re.sub(r"\s+", "", line).split(",")
            if l[len(l) - 1] == "500":
                for i in range(0, len(l) - 1):
                    success_code_map[l[i]] = l[i]
		    all_code_map[l[i]] = l[i]
            elif l[len(l) - 1] == "400":
                for i in range(0, len(l) - 1):
                    unsuccess_code_map[l[i]] = l[i]
                    all_code_map[l[i]] = l[i]

    success_sorted_map = sorted(success_code_map.items(), key=operator.itemgetter(0))
    unsuccess_sorted_map = sorted(unsuccess_code_map.items(), key=operator.itemgetter(0))
    all_sorted_map = sorted(all_code_map.items(), key=operator.itemgetter(0))

    success_map = []
    unsuccess_map = []
    all_map = []
    
    for key, value in all_sorted_map:
        all_map.append(key)
    for key, value in success_sorted_map:
        success_map.append(key)
    for key, value in unsuccess_sorted_map:
        unsuccess_map.append(key)

    return all_map, success_map, unsuccess_map


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
                for i in range(0, len(l)-1):
                    seq.append([code_to_int[l[i]]])
                    map[l[i]] = l[i]
                lengths.append(len(l) - 1)
                seq_label.append(l[len(l) - 1])
            elif flag == 2:
                var_len = 0
                for i in range(0, len(l)-1):
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

#######################################################################
# create startified folds for cross validation
def createStartifiedFolds(codebook, kFolds=10):
    folds = []
    success = []
    unsuccess = []
    max_len = 0
    len_tmp = 0
    code_to_int = dict((c, i + 1) for i, c in enumerate(codebook))

    with open("data/successful.txt", "r") as filestream:
        for line in filestream:
            len_tmp = len(line.split(","))
            if len_tmp > max_len:
                max_len = len_tmp
            currentline = line.replace("\n", "").split(",")
            seq = []
            for s in currentline:
                if s in code_to_int.keys():
                    seq.append(int(code_to_int[s]))
                else:
                    seq.append(500)
            success.append(seq)

    random.shuffle(success)
    with open("data/unsuccessful.txt", "r") as fstream:
        for line in fstream:
            len_tmp = len(line.split(","))
            if len_tmp > max_len:
                max_len = len_tmp
            currentline = line.replace("\n", "").split(",")
            seq = []
            for s in currentline:
                if s in code_to_int.keys():
                    seq.append(int(code_to_int[s]))
                else:
                    seq.append(400)
            unsuccess.append(seq)

    random.shuffle(unsuccess)
    for i in range(0, kFolds):
        foldSize_succ = int(float(len(success)) / kFolds)
        foldSize_unsucc = int(float(len(unsuccess)) / kFolds)
        idx_succ = range(i * foldSize_succ, i * foldSize_succ + foldSize_succ)
        random.shuffle(idx_succ)
        idx_unsucc = range(i * foldSize_unsucc, i * foldSize_unsucc + foldSize_unsucc)
        random.shuffle(idx_unsucc)
        test = [success[index] for index in idx_succ] + [unsuccess[index] for index in idx_unsucc]
        train = [success[index] for index in range(0, len(success)) if index not in idx_succ] + \
                [unsuccess[index] for index in range(0, len(unsuccess)) if index not in idx_unsucc]
        random.shuffle(test)
        random.shuffle(train)
        folds.append([test, train])
    return folds, max_len

###################################################################
def writeSampledSequences(X, y, codebook, outputdata_filename):
    int_to_code = dict((i+1, c) for i, c in enumerate(codebook))
    f = open(outputdata_filename, "w")
    for i in range(0, len(X)):
        seq = []
        for s in X[i]:
            val = int(round(s))
            if val > 0:
                if val in int_to_code.keys():
                    seq.append(str(int_to_code[val]))
                else:
                    print "Error code: ", val
        seq.append(str(y[i]))
        f.write(",".join(seq) + "\n")
    f.close()

#######################################################################
# create startified folds for cross validation
def createUnderOrOverSample(method, given_data, outputdata_filename, max_len, codebook):
    dataX = []
    dataY = []
    for xx in given_data:
        dataX.append(xx[0:-1])
        dataY.append(xx[-1])

    X = pad_sequences(dataX, maxlen=max_len, dtype='float32')
    X_norm = X / (float(len(codebook)))
    y_norm = np.array(dataY)

    # perform over or under sampling
    X_d = []
    y_res = []
    if method == "over":
        sm = SMOTE(kind='borderline2')
        X_res, y_res = sm.fit_sample(X_norm, y_norm)
    else:
        sm = ClusterCentroids()
        X_res, y_res = sm.fit_sample(X_norm, y_norm)

    X_d = X_res * (float(len(codebook)))
    writeSampledSequences(X_d, y_res, codebook, outputdata_filename)

