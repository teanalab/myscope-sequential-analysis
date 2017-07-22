# Various function as helper
import numpy
import os
import re
import random
from keras.utils import np_utils
from keras.preprocessing.sequence import pad_sequences

###################################################################
# split sequences based on commitment language

###################################################################
# call smote to balance sample

###################################################################
# read codebook from file
def loadCodeBook(codebook_filename):
    codebook = []
    with open(codebook_filename, "r") as filestream:
        for line in filestream:
            codebook.append(line[:3])
    return codebook

###################################################################
# read all sequences and return X, y
def readSequenceFromFile(sequence_file, codebook, seq_len = 5, is_train = True):
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

    # one hot encode the output variable
    y = np_utils.to_categorical(dataY)

    return X, y, max_len

###################################################################
# evaluate model with F1, Precision and Recall

###################################################################
# split data set by using stratified method

###################################################################
# cross validation results in terms of F1, Precision and Recall

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
# pre-process data from given text files
def createSequence(rawFileLocation, codeMappingFile):
    count_pos = 0
    count_neg = 0
    #codebook_dict = getDictionaryForCodeBook(codeMappingFile)

    codebook_dict = {}
    for filename in os.listdir(rawFileLocation):
        with open(rawFileLocation+filename, "r") as filestream:
            seq = []
            for line in filestream:
                currentline = re.sub(r"\s+", "", line).split(",")
                if len(currentline) > 1:
                    code = currentline[1]
                    if (code[1:-1] == 'CHT+') or (code[1:-1] == 'CML+') or (code[1:-2] == 'CHT+') or (code[1:-2] == 'CML+'):
                        seq.append('500')
                        if len(seq) > 2:
                            count_pos += 1
                        seq = []
                    elif (code[1:-1] == 'CHT-') or (code[1:-1] == 'CML-') or (code[1:-2] == 'CHT+') or (code[1:-2] == 'CML+'):
                        seq.append('400')
                        if len(seq) > 2:
                            count_neg += 1
                        seq = []
                    else:
                        #seq.append(codebook_dict[code[1:-1]])
                        seq.append(code[1:-1])
                        codebook_dict[code[1:-1]] = 1
    print "total positive sequences: ", count_pos, "total negative sequences: ", count_neg
    # f = open("/home/mehedi/teana/data-source/seq-analysis/codemap.txt", "w")
    # #write all unique code to file
    # for key in codebook_dict.keys():
    #     f.write(key + "," + " \n")
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