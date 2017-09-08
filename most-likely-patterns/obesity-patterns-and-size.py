# **************************
# Author: Md Mehedi Hsan  *
# Wayne State University  *
# email: mehedi@wayne.edu *
# **************************

import utility
from collections import Counter
import operator
import math

successful_fp = open("successful_patterns.txt", "w")
unsuccessful_fp = open("unsuccessful_patterns.txt", "w")

map_successful_patterns = dict()
map_unsuccessful_patterns = dict()

successful_filename = "data/successful.txt"
unsuccessful_filename = "data/unsuccessful.txt"

total_utterances = 0
successful_patterns = 0
unsuccessful_patterns = 0

with open(successful_filename, "r") as file_stream:
    for line in file_stream:
        codes = line.replace("\n", "").strip().split(",")
        total_utterances += len(codes)
        successful_patterns += 1

avg_length_successful = float(total_utterances)/successful_patterns

unsuccessful_seq_length = 0
with open(unsuccessful_filename, "r") as file_stream:
    for line in file_stream:
        codes = line.replace("\n", "").strip().split(",")
        unsuccessful_seq_length += len(codes)
        unsuccessful_patterns += 1

avg_length_unsuccessful = float(unsuccessful_seq_length)/unsuccessful_patterns
total_utterances += unsuccessful_seq_length

print("Successful patterns: " + str(successful_patterns), ("Avg. Length: " + str(avg_length_successful)))
print("Unsuccessful patterns: " + str(unsuccessful_patterns), ("Avg. Length: " + str(avg_length_unsuccessful)))
print("Total utterances: " + str(total_utterances))

# set dictionary for successful sequences
successful_dict = utility.loadTransitionDictionary(successful_filename, 1, "500")

# set dictionary for unsuccessful sequences
unsuccessful_dict = utility.loadTransitionDictionary(unsuccessful_filename, 1, "400")

with open(successful_filename, "r") as file_stream:
    for line in file_stream:
        words = line.replace("\n", "").split(",")

        successful_prob = math.log(0.5)

        # get probability of generating sequence from successful transcript
        words[len(words) - 1] = "500"
        for i in xrange(0, len(words) - 1 - 1):
            current_tuple = tuple([words[j] for j in xrange(i, i + 1)])
            if current_tuple in successful_dict.keys():
                next_states = successful_dict[current_tuple]
                next_states_counter = Counter(next_states)
                transition_prob = -5.0
                if words[i + 1] in next_states_counter:
                    transition_prob = math.log(float(next_states_counter[words[i + 1]]) / len(next_states))
                successful_prob += transition_prob
            else:
                successful_prob -= 5.0
        # store it to map
        map_successful_patterns[line] = successful_prob

with open(unsuccessful_filename, "r") as file_stream:
    for line in file_stream:
        words = line.replace("\n", "").split(",")
        unsuccessful_prob = math.log(0.5)

        # get probability of generating sequence from successful transcript
        words[len(words) - 1] = "400"
        for i in xrange(0, len(words) - 1 - 1):
            current_tuple = tuple([words[j] for j in xrange(i, i + 1)])
            if current_tuple in unsuccessful_dict.keys():
                next_states = unsuccessful_dict[current_tuple]
                next_states_counter = Counter(next_states)
                transition_prob = -5.0
                if words[i + 1] in next_states_counter:
                    transition_prob = math.log(float(next_states_counter[words[i + 1]]) / len(next_states))
                unsuccessful_prob += transition_prob
            else:
                unsuccessful_prob -= 5.0
        # store it to map
        map_unsuccessful_patterns[line] = unsuccessful_prob

successful_sorted_map = sorted(map_successful_patterns.items(), key=operator.itemgetter(1))
for key, val in successful_sorted_map:
    successful_fp.write(str(val) + ":" + key + "\n")

unsuccessful_sorted_map = sorted(map_unsuccessful_patterns.items(), key=operator.itemgetter(1))
for key, val in unsuccessful_sorted_map:
    unsuccessful_fp.write(str(val) + ":" + key + "\n")

successful_fp.close()
unsuccessful_fp.close()
