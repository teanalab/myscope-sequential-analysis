import utility
import numpy
import argparse
from keras.preprocessing.sequence import pad_sequences
from imblearn.over_sampling import SMOTE
from imblearn.under_sampling import ClusterCentroids
from collections import Counter

#############################################################################################
# parse command line arguments
parser = argparse.ArgumentParser(description='Train LSTM Sequential Model.')
parser.add_argument('-data',
                    default='/home/mehedi/teana/data-source/seq-analysis/deepLearn/obesity-newfile/unbalanced/cht-cml/allsequence.txt'
                    , help='File location containing training sequence.')
parser.add_argument('-codebook', default='/home/mehedi/teana/data-source/seq-analysis/deepLearn/obesity-newfile/codebook-newfile.txt',
                    help='File location containing codebook.')

args = parser.parse_args()
#############################################################################################
# Load up training data
data_filename = args.data
codebook_filename = args.codebook


#############################################################################################
# normalize data
def normalizeData(dataX, dataY, codebook, max_len):
    # convert list of lists to array and pad sequences if needed
    X = pad_sequences(dataX, maxlen=max_len, dtype='float32')

    # normalize
    X = X / (float(len(codebook)) - 2)

    # get codebook size
    codebookSize = len(codebook) - 2

    # get label as 0 and 1
    dataY[:] = [x - codebookSize for x in dataY]

    # one hot encode the output variable
    y = numpy.array(dataY)

    return X, y, max_len


###################################################################
codebook = utility.loadCodeBook(codebook_filename)
X, y, seq_len = utility.readSequenceFromFile(data_filename, codebook)
X_norm, y_norm, max_len = normalizeData(X, y, codebook, seq_len)

# perform over or under sampling
sm = SMOTE(random_state=42, kind='borderline2')
#sm = ClusterCentroids(random_state=42)
X_res, y_res = sm.fit_sample(X_norm, y_norm)

# save data to a csv file
out = open('/home/mehedi/teana/data-source/seq-analysis/over-sampling-bdl2.csv', 'w')
idx = 0
for row in X_res:
    for column in row:
        out.write('%f,' % column)
    out.write('%d' % y_res[idx])
    idx += 1
    out.write('\n')
out.close()

# display new sample size
print Counter(y_res)

