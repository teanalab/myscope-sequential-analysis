#####################################################################
# Stateless
#####################################################################
# LSTM with Variable Length Input Sequences to One Character Output
import numpy
from keras.models import Sequential
from keras.layers import Dense
from keras.layers import LSTM
from keras.utils import np_utils
from keras.preprocessing.sequence import pad_sequences

# fix random seed for reproducibility
numpy.random.seed(7)

# define the raw dataset
alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"

# create mapping of characters to integers (0-25) and the reverse
char_to_int = dict((c, i) for i, c in enumerate(alphabet))
int_to_char = dict((i, c) for i, c in enumerate(alphabet))

# prepare the dataset of input to output pairs encoded as integers
num_inputs = 1000
max_len = 5
dataX = []
dataY = []
for i in range(num_inputs):
    start = numpy.random.randint(len(alphabet) - 2)
    end = numpy.random.randint(start, min(start + max_len, len(alphabet) - 1))
    sequence_in = alphabet[start:end + 1]
    sequence_out = numpy.random.randint(2)

    if i % 2 == 0 or i % 5 == 0:
        if sequence_in[-1] >= 'M':
            sequence_out = 1

        if sequence_in[-1] < 'M':
            sequence_out = 0

    dataX.append([char_to_int[char] for char in sequence_in])
    dataY.append(sequence_out)
    #print sequence_in, '->', sequence_out

# convert list of lists to array and pad sequences if needed
X = pad_sequences(dataX, maxlen=max_len, dtype='float32')

# reshape X to be [samples, time steps, features]
X = numpy.reshape(X, (X.shape[0], max_len, 1))

# normalize
X = X / float(len(alphabet))

# one hot encode the output variable
y = np_utils.to_categorical(dataY)

# create and fit the model
batch_size = 64
model = Sequential()
model.add(LSTM(32, input_shape=(X.shape[1], 1)))
model.add(Dense(y.shape[1], activation='softmax'))
model.compile(loss='categorical_crossentropy', optimizer='adam', metrics=['accuracy'])
print("\nModel fitting...")
model.fit(X, y, epochs=500, batch_size=batch_size, verbose=0, shuffle=False)

# summarize performance of the model
scores = model.evaluate(X, y, verbose=0)
print("\nModel Accuracy: %.2f%%" % (scores[1] * 100))

# demonstrate some model predictions
for i in range(50):
    pattern_index = numpy.random.randint(len(dataX))
    pattern = dataX[pattern_index]
    x = pad_sequences([pattern], maxlen=max_len, dtype='float32')
    x = numpy.reshape(x, (1, max_len, 1))
    x = x / float(len(alphabet))
    prediction = model.predict(x, verbose=0)
    index = numpy.argmax(prediction)
    result = index
    seq_in = [int_to_char[value] for value in pattern]
    print seq_in, "->", result


#####################################################################
# Stateful
#####################################################################

# LSTM with Variable Length Input Sequences to One Character Output
import numpy
from keras.models import Sequential
from keras.layers import Dense
from keras.layers import LSTM
from keras.utils import np_utils
from keras.preprocessing.sequence import pad_sequences

# fix random seed for reproducibility
numpy.random.seed(7)

# define the raw dataset
alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"

# create mapping of characters to integers (0-25) and the reverse
char_to_int = dict((c, i) for i, c in enumerate(alphabet))
int_to_char = dict((i, c) for i, c in enumerate(alphabet))

# prepare the dataset of input to output pairs encoded as integers
num_inputs = 1000
max_len = 5
dataX = []
dataY = []
for i in range(num_inputs):
    start = numpy.random.randint(len(alphabet) - 2)
    end = numpy.random.randint(start, min(start + max_len, len(alphabet) - 1))
    sequence_in = alphabet[start:end + 1]
    sequence_out = numpy.random.randint(2)

    if i % 2 == 0 or i % 5 == 0:
        if sequence_in[-1] >= 'M':
            sequence_out = 1

        if sequence_in[-1] < 'M':
            sequence_out = 0

    dataX.append([char_to_int[char] for char in sequence_in])
    dataY.append(sequence_out)

# convert list of lists to array and pad sequences if needed
X = pad_sequences(dataX, maxlen=max_len, dtype='float32')

# reshape X to be [samples, time steps, features]
X = numpy.reshape(X, (X.shape[0], max_len, 1))

# normalize
X = X / float(len(alphabet))

# one hot encode the output variable
y = np_utils.to_categorical(dataY)

# create and fit the model
batch_size = 64
model = Sequential()
model.add(LSTM(16, batch_input_shape=(batch_size, X.shape[1], X.shape[2]), stateful=True))
model.add(Dense(y.shape[1], activation='softmax'))
model.compile(loss='categorical_crossentropy', optimizer='adam', metrics=['accuracy'])
print("\nModel fitting...")
for i in range(300):
    model.fit(X, y, epochs=5, batch_size=batch_size, verbose=0, shuffle=False)
    model.reset_states()

# summarize performance of the model
scores = model.evaluate(X, y, verbose=0)
model.reset_states()
print("\nModel Accuracy: %.2f%%" % (scores[1] * 100))

# demonstrate some model predictions
for i in range(50):
    pattern_index = numpy.random.randint(len(dataX))
    pattern = dataX[pattern_index]
    x = pad_sequences([pattern], maxlen=max_len, dtype='float32')
    x = numpy.reshape(x, (1, max_len, 1))
    x = x / float(len(alphabet))
    prediction = model.predict(x, verbose=0)
    index = numpy.argmax(prediction)
    result = index
    seq_in = [int_to_char[value] for value in pattern]
    print seq_in, "->", result

model.reset_states()