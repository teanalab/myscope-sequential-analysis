# Sequence Classification of MYSCOPE code

Predicting Success of Clinical Interviews via Deep Learning and Probabilistic Modeling of Patient-Provider Communication Sequences

## Methods

* Markov Chain
* Hidden Markov Model
* Recurrent Neural Network (RNN)
    * Long Short Term Memory (LSTM)
    * Gated Recurrent Unit (GRU)

## Results

* When Undersampling is used for data balancing

Method | Accuracy | Precision | Recall | F1-Score
------------------ | ------------- | ------------ | ------------- | ------------
Markov Chain 1st Order | 0.7013 | 0.7037 | 0.7013 | 0.7003
Markov Chain 2nd Order | 0.5905 | 0.5917 | 0.5905 | 0.5890
HMM | 0.5439 | 0.5778 | 0.5439 | 0.4865
LSTM | 0.8770 | 0.8812 | 0.8770 | 0.8766
LSTM-TR | 0.8824 | 0.8890 | 0.8824 | 0.8817
GRU | 0.8844 | 0.8897 | 0.8844 | 0.8840
GRU-TR | **0.8858** | **0.8917** | **0.8858** | **0.8853**


* When Oversampling is used for data balancing

Method | Accuracy | Precision | Recall | F1-Score
------------------ | ------------- | ------------ | ------------- | ------------
Markov Chain 1st Order | 0.7930 | 0.8127 | 0.7930 | 0.7896
Markov Chain 2nd Order | 0.7325 | 0.7461 | 0.7325 | 0.7288
HMM | 0.7763 | 0.8062 | 0.7763 | 0.7706
LSTM | 0.8703 | 0.8782 | 0.8703 | 0.8696
LSTM-TR | 0.8713 | 0.8789 | 0.8713 | 0.8706
GRU | 0.8711 | 0.8785 | 0.8711 | 0.8705
GRU-TR | **0.8722** | **0.8790** | **0.8722** | **0.8716**

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for testing purposes. See 'Running the tests' notes on how to run the project on your local system.

### Prerequisites

What things you need to install the software and how to install them

```
Give examples
```

## Built With

* [Dropwizard](http://www.dropwizard.io/1.0.2/docs/) - The web framework used
* [Maven](https://maven.apache.org/) - Dependency Management
* [ROME](https://rometools.github.io/rome/) - Used to generate RSS Feeds

## Running the tests

Explain how to run the automated tests for this system

### Break down into end to end tests

Explain what these tests test and why

```
Give an example
```

## Authors

* **Mehedi Hasan** - *Wayne State University* - [link](https://www.researchgate.net/profile/Mehedi_Hasan33)
* **Alexander Kotov** - *Wayne State University* - [link](http://www.cs.wayne.edu/kotov/)


## Acknowledgments

* We would like to thank the student assistants in the department of Family Medicine and Public Health Sciences at Wayne State University School of Medicine for their help with transcribing the recordings of motivational interviews.



