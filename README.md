# Sequence Classification of Motivational Interviews

Main objective of this project is the classification of Patient-Provider Communication Sequences via Deep Learning and Probabilistic Models. There are three main subprojects under the main repository:

* deeplearning
* hmm-python
* mchain-python

## Methods

* Markov Chain
    * 1st order
    * 2nd order
* Hidden Markov Model
* Recurrent Neural Network (RNN)
    * Long Short Term Memory (LSTM)
       * Vanilla LSTM
       * LSTM with minimized loss at each step
    * Gated Recurrent Unit (GRU)
       * Vanilla GRU
       * GRU with minimized loss at each step

## Results

* When Undersampling is used for data balancing (\# of hidden units = 15, embedding dimension = 44, batch size = 8, learning rate = 0.00005, minimum change = 0.0010)

Method | Accuracy | Precision | Recall | F1-Score
------------------ | ------------- | ------------ | ------------- | ------------
Markov Chain 1st Order | 0.7044 | 0.7060 | 0.7044 | 0.7038
Markov Chain 2nd Order | 0.6385 | 0.6395 | 0.6385 | 0.6379
HMM | 0.6143 | 0.6244 | 0.6143 | 0.6067
LSTM | 0.8626 | 0.8672 | 0.8626 | 0.8622
LSTM-TR | **0.8681** | **0.8733** | **0.8681** | **0.8677**
GRU | 0.8648 | 0.8674 | 0.8648 | 0.8646
GRU-TR | 0.8676 | 0.8705 | 0.8676 | 0.8673


* When Oversampling is used for data balancing (\# of hidden units = 15, embedding dimension = 44, batch size = 55, learning rate = 0.00010, minimum change = 0.0005)

Method | Accuracy | Precision | Recall | F1-Score
------------------ | ------------- | ------------ | ------------- | ------------
Markov Chain 1st Order | 0.7799 | 0.7932 | 0.7799 | 0.7775
Markov Chain 2nd Order | 0.7029 | 0.7111 | 0.7029 | 0.7000
HMM | 0.7567 | 0.7775 | 0.7567 | 0.7520
LSTM | 0.8372 | 0.8411 | 0.8372 | 0.8368
LSTM-TR | **0.8385** | **0.8424** | **0.8385** | **0.8381**
GRU | 0.8342 | 0.8379 | 0.8342 | 0.8337
GRU-TR | 0.8377 | 0.8412 | 0.8377 | 0.8373

## Getting Started

Download or clone the project from our Github repository. To run our models, several python packages are required. See the _prerequisites_ section for details. See _Running the tests_ notes on how to run the project on your local system.

### Prerequisites

The following python packages are required to run our program:
* [keras](https://keras.io/) - The Python Deep Learning library. Keras uses the following dependencies:
    * [numpy](https://pypi.python.org/pypi/numpy)
    * [scipy](https://pypi.python.org/pypi/scipy)
    * [yaml](https://anaconda.org/conda-forge/yaml)
* [TensorFlow](https://maven.apache.org/) - Used TensorFlow in backend:
* [hmmlearn](https://hmmlearn.readthedocs.io/en/latest/) - Simple algorithms and models to learn HMMs (Hidden Markov Models) in Python.
* [imblearn](http://contrib.scikit-learn.org/imbalanced-learn/stable/api.html#module-imblearn.over_sampling) - API for over and under sampling.
* [scikit-learn](https://pypi.python.org/pypi/scikit-learn) - A set of python modules for machine learning and data mining.


### Running the tests

For running the test program, we need to run one python file containing _obesity_ as a prefix. You will find that python file in each folder of the corresponding subproject directory. There are several command line parameters are required to execute the program. However, I set the default value for each of the parameters. 

For example, to run HMM with oversampling and 10 folds cross-validation, you have to go to the hmm-python directory and then run the following command: 
```
python obesity-hmm.py --sampling 'over' --folds 10   
```
Similarly, for deep learning method
```
python obesity-gru-lstm.py --sampling 'over' --folds 10 --min_change 0.0015
```

**N.B:** data folder is only given in split-data folder. You need to create your own data sets (over and under sampling) for the execution of each subproject.  

## Authors

* **[Mehedi Hasan](https://www.researchgate.net/profile/Mehedi_Hasan33)** - *Department of Computer Science at Wayne State University*
* **[Alexander Kotov](http://www.cs.wayne.edu/kotov/)** - *Department of Computer Science at Wayne State University*

## Citation

Please cite the following paper if you use our code or ideas in your work:

Mehedi Hasan, Alexander Kotov, April Idalski Carcone, Ming Dong, Sylvie Naar, "Predicting the Outcome of Patient-Provider Communication Sequences using Recurrent Neural Networks and Probabilistic Models", In Proceedings of the 2018 Informatics Summit of the American Medical Informatics Association (AMIA Summit 2018).

## Acknowledgments

* We would like to thank the student assistants in the department of Family Medicine and Public Health Sciences at Wayne State University School of Medicine for their help with transcribing the recordings of motivational interviews.

