import utility
import argparse

# Parse command line arguments
parser = argparse.ArgumentParser(description='Create train and test data.')
parser.add_argument('--codebook', default='codebook.txt', help='File location containing codebook.')

# Read parameters
args = parser.parse_args()
codebook_filename = args.codebook

# create K-folds
def createFolds(codebook, kFolds=10):
    foldData, max_len = utility.createStartifiedFolds(codebook, kFolds)

    for i in range(0, kFolds):
        # get train and test data
        utility.createUnderAndOverSample(foldData[i][0], "folds/fold" + str(i + 1) + "/test.txt", max_len,
                                        codebook)
        utility.createUnderAndOverSample(foldData[i][1], "folds/fold" + str(i + 1) + "/train.txt", max_len,
                                        codebook)
        utility.splitTrainAndValidationSet("under/folds/fold" + str(i + 1) + "/train.txt")
        utility.splitTrainAndValidationSet("over/folds/fold" + str(i + 1) + "/train.txt")


# create folds with over and under sampling
codebook = utility.loadCodeBook(codebook_filename)
createFolds(codebook, kFolds=10)
