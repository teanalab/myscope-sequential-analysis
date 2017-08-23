# Various function as helper
import numpy
import os
import re
import random
import operator
from keras.utils import np_utils
from keras.preprocessing.sequence import pad_sequences
from imblearn.over_sampling import SMOTE
from collections import Counter
from imblearn.under_sampling import ClusterCentroids

random.seed(42)

###################################################################
# read codebook from file
def loadCodeBook(codebook_filename):
    codebook = []
    with open(codebook_filename, "r") as filestream:
        for line in filestream:
            codebook.append(line.replace("\n", ""))
    return codebook


###################################################################
# read all sequences and return X, y
def readSequenceFromFile(sequence_file, codebook, seq_len=5, is_train=True):
    max_len = seq_len
    dataX = []
    dataY = []

    # create mapping of characters to integers (0-25) and the reverse
    code_to_int = dict((c, i) for i, c in enumerate(codebook))

    # prepare the dataset of input to output pairs encoded as integers
    with open(sequence_file, "r") as filestream:
        for line in filestream:
            currentline = line.split(",")
            dataX.append([code_to_int[item] for item in currentline[0:-1]])
            dataY.append(code_to_int[str(currentline[-1].strip())])
            if len(currentline) > max_len and is_train:
                max_len = len(currentline)

    return dataX, dataY, max_len


###################################################################
# normalize and hot encode
def normalizeData(dataX, dataY, codebook, max_len):
    # convert list of lists to array and pad sequences if needed
    X = pad_sequences(dataX, maxlen=max_len, dtype='float32')

    # reshape X to be [samples, time steps, features]
    X = numpy.reshape(X, (X.shape[0], max_len, 1))

    # normalize
    X = X / float(len(codebook))

    # get codebook size
    codebookSize = len(codebook)

    # get label as 0 and 1
    dataY[:] = [x - codebookSize + 2 for x in dataY]

    # one hot encode the output variable
    y = np_utils.to_categorical(dataY)

    return X, y, max_len


###################################################################
# evaluate model with F1, Precision and Recall
def getMacroAveragePerformance(actual, predicted):
    precision = 0.0
    recall = 0.0
    f_measure = 0.0
    accuracy = 0.0

    labels = [1, 0, 0, 1]

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

        local_precision = 0.0
        local_recall = 0.0
        local_f_measure = 0.0

        if (tp + fp) > 0:
            local_precision = (float(tp) / (tp + fp))
        if (tp + fn) > 0:
            local_recall = (float(tp) / (tp + fn))
        if (local_precision + local_recall) > 0:
            local_f_measure = (float(2 * local_precision * local_recall) / (local_precision + local_recall))
        local_accuracy = (float(tp + tn) / (tp + fp + tn + fn))

        # for checking calculation
        # print tp, fp, tn, fn
        # print local_accuracy, local_precision, local_recall, local_f_measure

        precision += local_precision
        recall += local_recall
        f_measure += local_f_measure
        accuracy += local_accuracy

    return accuracy / 2, precision / 2, recall / 2, f_measure / 2


###################################################################
# evaluate model with F1, Precision and Recall
def getMicroAveragePerformance(actual, predicted):
    precision = 0.0
    recall = 0.0
    f_measure = 0.0
    accuracy = 0.0

    total_sample = len(actual)

    labels = [1, 0, 0, 1]

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

        # for checking calculation
        # print tp, fp, tn, fn
        # print local_accuracy, local_precision, local_recall, local_f_measure

        local_precision = 0.0
        local_recall = 0.0
        local_f_measure = 0.0

        if (tp + fp) > 0:
            local_precision = (float(tp) / (tp + fp))
        if (tp + fn) > 0:
            local_recall = (float(tp) / (tp + fn))
        if (local_precision + local_recall) > 0:
            local_f_measure = (float(2 * local_precision * local_recall) / (local_precision + local_recall))

        precision += (local_precision * (float(tp + fn) / total_sample))
        recall += (local_recall * (float(tp + fn) / total_sample))
        f_measure += (local_f_measure * (float(tp + fn) / total_sample))

        accuracy = (float(tp + tn) / (tp + fp + tn + fn))

    return accuracy, precision, recall, f_measure


###################################################################
def denormalizeData(X, codebook):
    X_d = X * (float(len(codebook)) - 2)
    y = X_d[:, -1]
    y[y > 0] = len(codebook) - 1
    y[y == 0] = len(codebook) - 2
    X_d[:, -1] = y
    return X_d


###################################################################
def writeSequenceFromPaddedSequence(X, codebook, outputdata_filename):
    int_to_code = dict((i, c) for i, c in enumerate(codebook))
    f = open(outputdata_filename, "w")
    for x in X:
        seq = []
        for s in x:
            val = int(round(s))
            if val > 0:
                if val in int_to_code.keys():
                    seq.append(str(int_to_code[val]))
                else:
                    print "Error code: ", val

        f.write(",".join(seq) + "\n")
    f.close()


###################################################################
# display test results one by one
def showResultsForTestData(model, codebook, training_filename, seq_len):
    int_to_code = dict((i, c) for i, c in enumerate(codebook))
    code_to_int = dict((c, i) for i, c in enumerate(codebook))
    miss_classify_400 = 0
    correct_classify_400 = 0
    miss_classify_500 = 0
    correct_classify_500 = 0
    with open(training_filename, "r") as filestream:
        for line in filestream:
            currentline = line.split(",")
            dataX = [code_to_int[item] for item in currentline[0:-1]]
            actual = str(currentline[-1].strip())
            x = pad_sequences([dataX], maxlen=seq_len, dtype='float32')
            x = numpy.reshape(x, (1, seq_len, 1))
            x = x / float(len(codebook))
            prediction = model.predict(x, verbose=0)
            index = int(numpy.argmax(prediction))
            result = int_to_code[index]
            seq_in = [int_to_code[value] for value in dataX]

            if (actual != result) and (actual == '500'):
                miss_classify_500 += 1
                print seq_in, "->", result, " : ", actual
            if (actual == result) and (actual == '500'):
                correct_classify_500 += 1

            if (actual != result) and (actual == '400'):
                miss_classify_400 += 1
                print seq_in, "->", result, " : ", actual
            if (actual == result) and (actual == '400'):
                correct_classify_400 += 1

    print "correct500: ", correct_classify_500, " incorrect500: ", miss_classify_500, "correct400", \
        correct_classify_400, " incorrect400: ", miss_classify_400


######################################################################
def getCodeMapping(dataFileLocation):
    codebook_dict = {}
    with open(dataFileLocation, "r") as filestream:
        for line in filestream:
            line = line.replace("\n", "").strip()
            currentline = line.split(",")
            for x in currentline:
                if x in codebook_dict.keys():
                    codebook_dict[x] += 1
                else:
                    codebook_dict[x] = 1

    sorted_map = sorted(codebook_dict.items(), key=operator.itemgetter(1))
    print len(sorted_map)
    # f = open("/home/mehedi/teana/data-source/seq-analysis/codemap.txt", "w")
    # write all unique code to file
    # for key, val in sorted_map:
    #     f.write(key + "," + key + "\n")
    # f.close()


#######################################################################
# create dictionary from file
def getDictionaryForCodeBook(codeMappingFile):
    codebook_dict = {}
    with open(codeMappingFile, "r") as filestream:
        for line in filestream:
            l = re.sub(r"\s+", "", line).split(",")
            codebook_dict[l[0]] = l[1]
    return codebook_dict


#######################################################################
# shuffle data and rewrite to file
def writeShuffledData(inputFile, outputFile):
    data = []
    f = open(outputFile, "w")
    with open(inputFile, "r") as filestream:
        for line in filestream:
            data.append(line)

    random.shuffle(data)
    for line in data:
        f.write(line)
    f.close()


#######################################################################
# shuffle data and rewrite to file for making balanced
def writeBalancedData(inputFile, outputFile, sampleSize):
    data = []
    f = open(outputFile, "w")
    with open(inputFile, "r") as filestream:
        for line in filestream:
            data.append(line)

    random.shuffle(data)
    count = 0
    for line in data:
        f.write(line)
        count += 1
        if count >= sampleSize:
            break
    f.close()


#######################################################################
# create startified folds for cross validation
def createStartifiedFolds(codebook, kFolds=10):
    folds = []
    success = []
    unsuccess = []
    max_len = 0
    len_tmp = 0
    code_to_int = dict((c, i) for i, c in enumerate(codebook))

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
        test = [unsuccess[index] for index in idx_unsucc] + [success[index] for index in idx_succ]
        train = [unsuccess[index] for index in range(0, len(unsuccess)) if index not in idx_unsucc] + \
                [success[index] for index in range(0, len(success)) if index not in idx_succ]
        random.shuffle(test)
        random.shuffle(train)
        random.shuffle(test)
        random.shuffle(train)
        random.shuffle(test)
        random.shuffle(train)
        folds.append([test, train])
    return folds, max_len


###################################################################
def writeSampledSequences(X, y, codebook, outputdata_filename):
    data_sequence = []
    int_to_code = dict((i, c) for i, c in enumerate(codebook))
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
        data_sequence.append(",".join(seq))

    random.shuffle(data_sequence)
    f = open(outputdata_filename, "w")
    for sample in data_sequence:
        f.write(sample + "\n")
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
    y_norm = numpy.array(dataY)

    # perform over or under sampling
    X_d = []
    y_res = []
    if method == "oversampling":
        sm = SMOTE(kind='borderline2')
        X_res, y_res = sm.fit_sample(X_norm, y_norm)
    else:
        sm = ClusterCentroids()
        X_res, y_res = sm.fit_sample(X_norm, y_norm)

    X_d = X_res * (float(len(codebook)))
    writeSampledSequences(X_d, y_res, codebook, outputdata_filename)
