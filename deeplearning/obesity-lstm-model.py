# LSTM with Variable Length Input Sequences to Output as successful or unsuccessful
import utility
import numpy
import argparse
from sklearn.model_selection import train_test_split
from keras.models import Sequential
from keras.layers import LSTM, Dense, Dropout
from keras.layers.convolutional import Conv1D
from keras.layers.convolutional import MaxPooling1D
from keras.regularizers import l1_l2
from keras.callbacks import EarlyStopping, ModelCheckpoint
from keras.models import load_model

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

##############################
# Load up training data
training_filename = args.training_data
testing_filename = args.testing_data
codebook_filename = args.codebook
model_path = args.model_path
output_directory = args.output_directory

codebook = utility.loadCodeBook(codebook_filename)
X, y, seq_len = utility.readSequenceFromFile(training_filename, codebook)

# test_X, test_y, max_len = utility.readSequenceFromFile(testing_filename, codebook, seq_len, False)
# display test results one by one
# utility.showResultsForTestData(trained_model, codebook, testing_filename, seq_len)

##############################################################################################
# get results for K folds
def getKFoldsResults(kFolds=10):
    results = []
    for i in range(0, kFolds):
        X_tr, X_ts, y_tr, y_ts = train_test_split(X, y, test_size=1.0 / kFolds)
        X_train, y_train, maxlen = utility.normalizeData(X_tr, y_tr, codebook, seq_len)
        X_test, y_test, maxlen = utility.normalizeData(X_ts, y_ts, codebook, seq_len)

        # create and fit the model
        batch_size = 1
        model = Sequential()
        model.add(
            Conv1D(filters=32, kernel_size=3, padding='same', activation='relu', input_shape=(X_train.shape[1], 1)))
        model.add(MaxPooling1D(pool_size=2))
        model.add(LSTM(64, recurrent_regularizer=l1_l2(l1=0.0, l2=0.015)))
        model.add(Dropout(0.25))
        model.add(Dense(2, activation='softmax'))
        model.compile(loss='categorical_crossentropy', optimizer='adam', metrics=['accuracy'])

        # save best model into file
        n_epoch = 20
        print "\nModel fitting...for fold ", i
        callbacks = [
            EarlyStopping(monitor='val_acc', min_delta=0.01, verbose=1, patience=n_epoch),
            ModelCheckpoint(model_path, monitor='val_acc', save_best_only=True, verbose=0),
        ]
        model.fit(X_train, y_train, epochs=n_epoch, batch_size=batch_size, verbose=2, shuffle=True,
                  validation_split=0.1, callbacks=callbacks)

        # summarize performance of the model
        print "\nEvaluating model for fold ", i
        trained_model = load_model(model_path)
        pred_labels = trained_model.predict(X_test)
        accuracy, precision, recall, f_measure = utility.getPerformance(numpy.argmax(y_test, axis=1),
                                                                        numpy.argmax(pred_labels, axis=1))
        fold_result = [i, accuracy, precision, recall, f_measure]
        results.append(fold_result)
        print results

        # scores = trained_model.evaluate(X_test, y_test, verbose=0)
        # print "Actual:  ", numpy.argmax(y_test, axis=1)
        # print "Predict: ", numpy.argmax(pred_labels, axis=1)
        # print("\nModel Accuracy: %.2f%%\n" % (scores[1] * 100))
        # fold_result = [i, (scores[1] * 100)]

    return results

###########################################################################################
# print kFolds result
results = getKFoldsResults(kFolds=10)
print "Average accuracy: ", (numpy.mean(results, axis=0))