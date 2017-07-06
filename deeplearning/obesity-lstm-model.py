# LSTM with Variable Length Input Sequences to One Character Output
import utility
import argparse
from keras.models import Sequential
from keras.layers import Dense
from keras.layers import LSTM

##############################
# Parse command line arguments

parser = argparse.ArgumentParser(description='Train LSTM Sequential Model.')

parser.add_argument('-training_data', default='/home/mehedi/teana/data-source/seq-analysis/deepLearn/balanced/cht-cml/train.txt'
                    , help='File location containing training sequence.')
parser.add_argument('-testing_data', default='/home/mehedi/teana/data-source/seq-analysis/deepLearn/balanced/cht-cml/test.txt',
                    help='File location containing testing sequence.')
parser.add_argument('-codebook', default='/home/mehedi/teana/data-source/seq-analysis/deepLearn/codebook.txt',
                    help='File location containing codebook.')
parser.add_argument('-output_directory', default='/home/mehedi/teana/data-source/seq-analysis/deepLearn/',
                    help='Directory to save results.')

args = parser.parse_args()

##############################
# Load up training data

training_filename = args.training_data
testing_filename = args.testing_data
codebook_filename = args.codebook
output_directory = args.output_directory

codebook = utility.loadCodeBook(codebook_filename)
X, y, seq_len = utility.readSequenceFromFile(training_filename, codebook)
test_X, test_y, max_len = utility.readSequenceFromFile(testing_filename, codebook, seq_len, False)

# create and fit the model
batch_size = 1
model = Sequential()
model.add(LSTM(32, input_shape=(X.shape[1], 1)))
model.add(Dense(y.shape[1], activation='softmax'))
model.compile(loss='categorical_crossentropy', optimizer='adam', metrics=['accuracy'])
print("\nModel fitting...")
model.fit(X, y, epochs=60, batch_size=batch_size, verbose=2, shuffle=True, validation_split=0.1)

# summarize performance of the model
print("\nEvaluating model...")
scores = model.evaluate(test_X, test_y, verbose=2)
print("\nModel Accuracy: %.2f%%" % (scores[1] * 100))

# display test results one by one
utility.showResultsForTestData(codebook, testing_filename, seq_len)