import utility


# rawFilePath = "/home/mehedi/teana/data-source/seq-analysis/rawFile/"
# codeMapping = "/home/mehedi/teana/data-source/seq-analysis/codemap-newfile.txt"
# utility.createSequence(rawFilePath, codeMapping)

#inputFile = "/home/mehedi/teana/data-source/seq-analysis/deepLearn/balanced/cht-cml/train.txt"
#outputFile = "/home/mehedi/teana/data-source/seq-analysis/deepLearn/balanced/cht-cml/train_shuffled.txt"
#utility.writeShuffledData(inputFile, outputFile)

inputFile = "/home/mehedi/teana/data-source/seq-analysis/hmm/obesity-newfile/balanced/allsequence.txt"
outputFile = "/home/mehedi/teana/data-source/seq-analysis/hmm/obesity-newfile/balanced/allsequence_bal.txt"
utility.writeShuffledData(inputFile, outputFile)
#utility.writeBalancedData(inputFile, outputFile, 742)

