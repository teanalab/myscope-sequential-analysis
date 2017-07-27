import utility
import numpy
import argparse

#############################################################################################
# parse command line arguments
parser = argparse.ArgumentParser(description='Train LSTM Sequential Model.')
parser.add_argument('-inputdata',
                    default='/home/mehedi/Desktop/Link to teana/data-source/seq-analysis/over-sampling-reg-old-test.csv'
                    , help='File location containing input data sequence.')
parser.add_argument('-outputdata',
                    default='/home/mehedi/Desktop/Link to teana/data-source/seq-analysis/over-sampling-reg-old-test-post.txt'
                    , help='File location containing out data sequence.')
parser.add_argument('-codebook', default='/home/mehedi/Desktop/Link to teana/data-source/seq-analysis/deepLearn/codebook.txt',
                    help='File location containing codebook.')

args = parser.parse_args()

#############################################################################################
inputdata_filename = args.inputdata
outputdata_filename = args.outputdata
codebook_filename = args.codebook
codebook = utility.loadCodeBook(codebook_filename)

# load data and denormalize
X = numpy.genfromtxt(inputdata_filename, delimiter=",")
X = utility.denormalizeData(X, codebook)

# write data into file
utility.writeSequenceFromPaddedSequence(X, codebook, outputdata_filename)

#############################################################################################
