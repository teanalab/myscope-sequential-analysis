import numpy as np
import re
import operator
from hmmlearn import hmm

###################################################################
# read codebook from file
def loadCodeBook(codebook_filename):
    codebook = []
    with open(codebook_filename, "r") as filestream:
        for line in filestream:
            codebook.append(line[:3])
    return codebook

###########################################################################################
def loadData(fileLocation, codebook):
    code_to_int = dict((c, i) for i, c in enumerate(codebook))
    seq = []
    lengths = []
    map = {}
    with open(fileLocation, "r") as filestream:
        for line in filestream:
            l = re.sub(r"\s+", "", line).split(",")
            for i in range(0, len(l) - 1):
                seq.append([code_to_int[l[i]]])
                map[l[i]] = l[i]
            lengths.append(len(l) - 1)
    #sorted_map = sorted(map.items(), key=operator.itemgetter(0))
    #for key, value in sorted_map:
    #    print key
    return np.array(seq), np.array(lengths)


############################################################################################
def getHMMModel(n_states, n_observations, sequences, seq_lengths):
    start_probability = np.ones(n_states)
    start_probability = start_probability / n_states

    transition_probability = np.ones(n_states * n_states).reshape(n_states, n_states)
    transition_probability = transition_probability / n_states

    emission_probability = np.ones(n_states * n_observations).reshape(n_states, n_observations)
    emission_probability = emission_probability / n_observations

    # create model and set initial values
    model = hmm.MultinomialHMM(n_components=n_states)
    model.startprob_ = start_probability
    model.transmat_ = transition_probability
    model.emissionprob_ = emission_probability

    # fit model
    model = model.fit(sequences, seq_lengths)
    return model

#############################################################################################
