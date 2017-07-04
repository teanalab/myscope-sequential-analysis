# Various function as helper
import numpy
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


