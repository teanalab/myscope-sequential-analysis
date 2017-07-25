import pandas
from keras.wrappers.scikit_learn import KerasClassifier
from keras.utils import np_utils
from sklearn.model_selection import cross_val_score
from sklearn.model_selection import KFold
from sklearn.preprocessing import LabelEncoder
import utility
import numpy
import argparse
from sklearn.model_selection import train_test_split
from keras.models import Sequential
from keras.layers import LSTM, Dense, Dropout
from keras.layers.wrappers import Bidirectional
from keras.layers.convolutional import Conv1D
from keras.layers.convolutional import MaxPooling1D
from keras.regularizers import l1_l2
from keras.callbacks import EarlyStopping, ModelCheckpoint
from keras.models import load_model
from keras.preprocessing.sequence import pad_sequences

#############################################################################################
# Parse command line arguments
parser = argparse.ArgumentParser(description='Train LSTM Sequential Model.')
parser.add_argument('-training_data',
                    default='/home/mehedi/teana/data-source/seq-analysis/deepLearn/balanced/cht-cml/train_shuffled.txt'
                    , help='File location containing training sequence.')
parser.add_argument('-testing_data',
                    default='/home/mehedi/teana/data-source/seq-analysis/deepLearn/balanced/cht-cml/test.txt',
                    help='File location containing testing sequence.')
parser.add_argument('-codebook', default='/home/mehedi/teana/data-source/seq-analysis/deepLearn/codebook.txt',
                    help='File location containing codebook.')
parser.add_argument('-model_path', default='/home/mehedi/teana/data-source/seq-analysis/deepLearn/model.h5',
                    help='Directory to save model.')
parser.add_argument('-output_directory', default='/home/mehedi/teana/data-source/seq-analysis/deepLearn/',
                    help='Directory to save results.')

args = parser.parse_args()

#############################################################################################
# Load up training data
training_filename = args.training_data
testing_filename = args.testing_data
codebook_filename = args.codebook
model_path = args.model_path
output_directory = args.output_directory

codebook = utility.loadCodeBook(codebook_filename)
X, y, seq_len = utility.readSequenceFromFile(training_filename, codebook)
X = pad_sequences(X, maxlen=seq_len, dtype='float32')
y[:] = [x - 41 for x in y]
y = np_utils.to_categorical(y)

print y

print X[0]

# fix random seed for reproducibility
seed = 7
numpy.random.seed(seed)

# load dataset
# dataframe = pandas.read_csv("/home/mehedi/teana/data-source/seq-analysis/deepLearn/iris.csv", header=None)
# dataset = dataframe.values
# X = dataset[:, 0:4].astype(float)
# print X[0]
# Y = dataset[:, 4]
#
# #
# encoder = LabelEncoder()
# encoder.fit(Y)
# encoded_Y = encoder.transform(Y)
# # convert integers to dummy variables (i.e. one hot encoded)
# dummy_y = np_utils.to_categorical(encoded_Y)
#
# print dummy_y

# define baseline model
def baseline_model():
    # create model
    model = Sequential()
    model.add(Dense(8, input_dim=X.shape[1], activation='relu'))
    model.add(Dense(2, activation='softmax'))
    # Compile model
    model.compile(loss='categorical_crossentropy', optimizer='adam', metrics=['accuracy'])
    return model


estimator = KerasClassifier(build_fn=baseline_model, epochs=50, batch_size=5, verbose=2)
kfold = KFold(n_splits=10, shuffle=True, random_state=seed)
results = cross_val_score(estimator, X, y, cv=kfold)
print("Baseline: %.2f%% (%.2f%%)" % (results.mean() * 100, results.std() * 100))
