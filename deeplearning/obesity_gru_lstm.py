# ***************************************************************
# Author: Md Mehedi Hasan
# Wayne State University
# email: mehedi@wayne.edu
# ***************************************************************

# Import necessary packages
import utility
import numpy as np
import math
import argparse
import random
from keras.preprocessing.sequence import pad_sequences
from keras.utils import np_utils
import tensorflow as tf
from sklearn.metrics import accuracy_score

# Parse command line arguments
parser = argparse.ArgumentParser(description='Train RNN Model.')
parser.add_argument('--codebook', default='codebook.txt',
                    help='File location containing codebook or vocabulary.')
parser.add_argument('--hidden_units', default=20, type=int, help='no. of hidden units in hidden layer.')
parser.add_argument('--batch_size', default=4, type=int, help='batch size of the training data.')
parser.add_argument('--embed_dimension', default=45, type=int, help='embedding dimension parameter.')
parser.add_argument('--keep_rate', default=1.0, type=float, help='keep rate or (1 - dropout) rate parameter.')
parser.add_argument('--alpha', default=0.50, type=float, help='alpha for loss optimization.')
parser.add_argument('--beta', default=0.03, type=float, help='beta parameter.')
parser.add_argument('--sampling', default='under', type=str, help='sampling parameter.')
parser.add_argument('--n_epoch', default=100, type=int, help='no. of epoch parameter.')
parser.add_argument('--min_change', default=0.0015, type=float, help='minimum change parameter.')
args = parser.parse_args()

# Set arguments value
codebook_filename = args.codebook
hidden_units = args.hidden_units
batch_size = args.batch_size
embed_dimension = args.embed_dimension
keep_rate = args.keep_rate
alpha = args.alpha
beta = args.beta
sampling = args.sampling
training_epochs = args.n_epoch
min_change = args.min_change

# Set initial value of some parameters
n_steps = 130
n_classes = 2
learning_rate = 0.00005
wait_epoch = 5
graph_level_seed = 42
operation_level_seed = 72

# Set randomness deterministic
np.random.seed(operation_level_seed)
random.seed(operation_level_seed)

# Read codebook or vocabulary
codebook = utility.loadCodeBook(codebook_filename)


def RNN(x, weights, biases, step_size):
    embed_x = tf.contrib.layers.embed_sequence(x, vocab_size=len(codebook), embed_dim=embed_dimension)
    cell = tf.contrib.rnn.LSTMCell(hidden_units)
    # cell = tf.contrib.rnn.GRUCell(hidden_units)
    # cell = tf.contrib.rnn.DropoutWrapper(cell, output_keep_prob=keep_rate, seed=operation_level_seed)
    output, state = tf.nn.dynamic_rnn(cell, embed_x, dtype=tf.float32)
    output_flattened = tf.reshape(output, [-1, hidden_units])
    output_logits = tf.add(tf.matmul(output_flattened, weights), biases)
    output_all = tf.nn.sigmoid(output_logits)
    output_reshaped = tf.reshape(output_all, [-1, step_size, n_classes])
    output_last = tf.gather(tf.transpose(output_reshaped, [1, 0, 2]), step_size - 1)
    return output_last, output_all


def runModel(fold, train_x, train_y, validation_x, validation_y, test_x, test_y, total_batches, step_size):
    # Reset graph for each run
    tf.reset_default_graph()
    tf.set_random_seed(graph_level_seed)

    # tf Graph input
    x = tf.placeholder("int32", [None, step_size])
    y = tf.placeholder("float", [None, n_classes])
    y_steps = tf.placeholder("float", [None, n_classes])

    # Weight and biases
    weights = tf.Variable(
        tf.truncated_normal([hidden_units, n_classes], stddev=math.sqrt(2.0 / hidden_units), seed=operation_level_seed))
    biases = tf.Variable(tf.zeros([n_classes]))

    # Outputs of each step and final step
    y_last, y_all = RNN(x, weights, biases, step_size)

    # Set loss function and optimizer
    all_steps_cost = -tf.reduce_mean((y_steps * tf.log(y_all)) + (1 - y_steps) * tf.log(1 - y_all))
    last_step_cost = -tf.reduce_mean((y * tf.log(y_last)) + ((1 - y) * tf.log(1 - y_last)))
    loss_function = (alpha * all_steps_cost) + ((1 - alpha) * last_step_cost)
    # regularizer = tf.nn.l2_loss(weights) + tf.nn.l2_loss(biases)
    # loss_function = tf.reduce_mean(total_loss + beta * regularizer)
    optimizer = tf.train.AdamOptimizer(learning_rate=learning_rate).minimize(loss_function)

    best_valid_acc = 0
    best_valid_loss = 10000
    best_test_acc = 0
    best_test_loss = 10000
    best_train_acc = 0
    best_train_loss = 10000
    counter = 0
    pred_labels = []

    with tf.Session() as session:
        tf.global_variables_initializer().run()
        f = open("results.txt", "a")
        for epoch in range(training_epochs):
            train_acc = 0
            train_loss = 0
            for b in range(total_batches):
                offset = (b * batch_size) % (train_y.shape[0] - batch_size)
                batch_x = train_x[offset:(offset + batch_size), :]
                batch_y = train_y[offset:(offset + batch_size), :]
                batch_y_steps = np.tile(batch_y, (int(train_x.shape[1]), 1))
                _, batch_labels, c = session.run([optimizer, y_last, loss_function],
                                                 feed_dict={x: batch_x, y: batch_y, y_steps: batch_y_steps})
                batch_acc = accuracy_score(np.argmax(batch_labels, 1), np.argmax(batch_y, 1))
                train_loss += c
                train_acc += batch_acc

            train_loss = train_loss / total_batches
            train_acc = train_acc / total_batches
            validation_y_steps = np.tile(validation_y, (int(validation_x.shape[1]), 1))
            pred_valid_labels, valid_loss = session.run([y_last, loss_function],
                                                        feed_dict={x: validation_x, y: validation_y,
                                                                   y_steps: validation_y_steps})
            test_y_steps = np.tile(test_y, (int(test_x.shape[1]), 1))
            curr_pred_labels, test_loss = session.run([y_last, loss_function],
                                                      feed_dict={x: test_x, y: test_y, y_steps: test_y_steps})
            valid_acc = accuracy_score(np.argmax(pred_valid_labels, 1), np.argmax(validation_y, 1))
            test_acc = accuracy_score(np.argmax(curr_pred_labels, 1), np.argmax(test_y, 1))

            if (best_valid_loss - valid_loss) >= min_change:
                best_valid_loss = valid_loss
                best_valid_acc = valid_acc
                best_test_loss = test_loss
                best_test_acc = test_acc
                best_train_acc = train_acc
                best_train_loss = train_loss
                pred_labels = curr_pred_labels
                counter = 0
                print(
                    "Epoch: {:.1f}, val_acc: {:.4f}, val_loss: {:.4f}, train_acc: {:.4f}, train_loss: {:.4f}".format(
                        (epoch + 1), best_valid_acc, best_valid_loss, best_train_acc, best_train_loss))
            elif wait_epoch == counter and training_epochs - 1 > epoch:
                f.write(str(fold + 1) + "," + str(best_valid_acc) + "," + str(best_valid_loss) + "," + str(
                    best_test_acc) + "," + str(best_test_loss) + "," + str(best_train_acc) + "," + str(
                    best_train_loss) + "\n")
                break
            else:
                counter += 1

            if training_epochs - 1 == epoch:
                f.write(str(fold + 1) + "," + str(best_valid_acc) + "," + str(best_valid_loss) + "," + str(
                    best_test_acc) + "," + str(best_test_loss) + "," + str(best_train_acc) + "," + str(
                    best_train_loss) + "\n")
                pred_labels = curr_pred_labels
        f.close()
    return pred_labels


# Read all sequences from file and return X, y
def readSequenceFromFile(sequence_file, seq_len=5, is_train=True):
    max_len = seq_len
    dataX = []
    dataY = []

    # Create mapping of behavior code
    code_to_int = dict((c, i) for i, c in enumerate(codebook))

    # Prepare the dataset of input to output pairs encoded as integers
    with open(sequence_file, "r") as filestream:
        for line in filestream:
            currentline = line.split(",")
            dataX.append([int(code_to_int[item]) for item in currentline[0:-1]])
            if str(currentline[-1].strip()) == "500":
                dataY.append(1)
            else:
                dataY.append(0)
            if len(currentline) > max_len and is_train:
                max_len = len(currentline)
    return dataX, dataY, max_len


def normalizeData(dataX, dataY, max_len):
    x = pad_sequences(dataX, maxlen=max_len, dtype='int32')
    y = np_utils.to_categorical(dataY)
    return x, y, max_len


# Get results for K-folds
def getKFoldsResults(kFolds=10):
    macro_results = []
    micro_results = []

    for i in range(0, kFolds):
        # read data from file
        tr_x, tr_y, seq_len = readSequenceFromFile(sampling + "/folds/fold" + str(i + 1) + "/train.txt")
        x_ts, y_ts, seq_len = readSequenceFromFile(sampling + "/folds/fold" + str(i + 1) + "/test.txt", seq_len, False)
        test_x, test_y, maxlen = normalizeData(x_ts, y_ts, seq_len)

        train_x, train_y, seq_1 = readSequenceFromFile(sampling + "/folds/fold" + str(i + 1) + "/train_split.txt")
        train_x, train_y, seq_1 = normalizeData(train_x, train_y, seq_len)
        validation_x, validation_y, seq_1 = readSequenceFromFile(
            sampling + "/folds/fold" + str(i + 1) + "/valid_split.txt")
        validation_x, validation_y, seq_1 = normalizeData(validation_x, validation_y, seq_len)

        total_batches = (train_x.shape[0] / batch_size)
        n_steps = maxlen

        # predict labels with trained model 
        pred_labels = runModel(i, train_x, train_y, validation_x, validation_y, test_x, test_y, total_batches, n_steps)

        # summarize performance of the model with macro results
        accuracy, precision, recall, f_measure = utility.getMacroAveragePerformance(np.argmax(test_y, axis=1),
                                                                                    np.argmax(pred_labels, axis=1))
        fold_result = [i, accuracy, precision, recall, f_measure]
        macro_results.insert(0, fold_result)

        # summarize performance of the model with micro results
        accuracy, precision, recall, f_measure = utility.getMicroAveragePerformance(np.argmax(test_y, axis=1),
                                                                                    np.argmax(pred_labels, axis=1))
        fold_result = [i, accuracy, precision, recall, f_measure]
        micro_results.insert(0, fold_result)
        print "\nResults for fold ", (i + 1), fold_result, "\n"

    return macro_results, micro_results


# Run the model and print kFolds result
macro_results, micro_results = getKFoldsResults(kFolds=10)
print "Macro results: ", (np.mean(macro_results, axis=0))
print "Micro results: ", (np.mean(micro_results, axis=0))
