import utility


# rawFilePath = "/home/mehedi/teana/data-source/seq-analysis/rawFile/"
# codeMapping = "/home/mehedi/teana/data-source/seq-analysis/codemap.txt"
# utility.createSequence(rawFilePath, codeMapping)

inputFile = "/home/mehedi/teana/data-source/seq-analysis/deepLearn/balanced/cht-cml/train.txt"
outputFile = "/home/mehedi/teana/data-source/seq-analysis/deepLearn/balanced/cht-cml/train_shuffled.txt"

utility.writeShuffledData(inputFile, outputFile)