import numpy as np
from hmmlearn import hmm

states = ["Rainy", "Sunny"]
n_states = len(states)

observations = ["walk", "shop", "clean"]
n_observations = len(observations)

start_probability = np.ones(n_states)
start_probability = start_probability/n_states

#transition_probability = np.np.zeros(n_states*n_states).reshape(n_states, n_states)
#transition_probability = np.identity(n_states)
transition_probability = np.ones(n_states*n_states).reshape(n_states, n_states)
transition_probability = transition_probability/n_states

#emission_probability = np.zeros(n_states*n_observations).reshape(n_states, n_observations)
emission_probability = np.ones(n_states*n_observations).reshape(n_states, n_observations)
emission_probability = emission_probability/n_observations

print "\nBefore model fitting..."
print start_probability
print transition_probability
print emission_probability

# create model and set initial values
model = hmm.MultinomialHMM(n_components=n_states)
model.startprob_=start_probability
model.transmat_=transition_probability
model.emissionprob_=emission_probability

# prepare data
sequences = np.array([[[0], [2], [1], [1], [2], [0]], [[0], [1], [1], [0], [2]], [[2], [1], [1]], [[1], [1], [1], [0]]])
seq_lengths = np.array([6, 5, 3, 4])
data = sequences[0]
for i in range(1, len(sequences)):
  data = np.concatenate([data, sequences[i]])
sequences = data

# fit model
model = model.fit(sequences, seq_lengths)

print "\nAfter model fitting..."
print model.startprob_
print model.transmat_
print model.emissionprob_

# get log likelihood for the given sequence(s)
test_seq = np.array([[0], [2], [1], [1], [2], [0]])
logL = model.score(test_seq)
print "\nLog likelihood of the sequence: ", logL, "\n"

# decode sequences
logprob, state_sequences = model.decode(test_seq, algorithm="viterbi")
print "\nLog probability of the produced state sequence: ", logprob, "\n"
print("Observed sequences:", ", ".join(map(lambda x: observations[int(x)], test_seq)))
print("State sequences:", ", ".join(map(lambda x: states[int(x)], state_sequences)))