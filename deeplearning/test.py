from collections import Counter
from sklearn.datasets import make_classification
from imblearn.over_sampling import SMOTE
import numpy

#X, y = make_classification(n_classes=2, class_sep=2,
#weights=[0.1, 0.9], n_informative=3, n_redundant=1, flip_y=0,
#n_features=20, n_clusters_per_class=1, n_samples=1000, random_state=10)

X = [[1, 0, 1, 0, 1, 1, 1],
     [1, 0, 1, 0, 1, 0, 1],
     [0, 1, 0, 0, 1, 0, 0],
     [0, 1, 1, 0, 1, 1, 1],
     [0, 1, 1, 0, 1, 0, 1],
     [0, 1, 0, 0, 1, 0, 1],
     [0, 1, 1, 0, 1, 1, 0],
     [0, 1, 1, 0, 1, 0, 1],
     [0, 1, 0, 0, 1, 0, 0],
     [0, 1, 0, 0, 1, 0, 1],
     [0, 1, 1, 0, 1, 1, 1],
     [0, 1, 1, 0, 1, 0, 1],
     [0, 1, 0, 0, 1, 0, 0],
     [1, 0, 1, 0, 1, 1, 1],
     [1, 0, 1, 0, 1, 0, 1],
     [1, 0, 0, 0, 1, 0, 1],
     [1, 0, 1, 0, 1, 1, 0],
     [1, 0, 1, 0, 1, 0, 1],
     [1, 0, 0, 0, 1, 0, 0],
     [1, 0, 0, 0, 1, 0, 1]]

X = numpy.array(X, dtype=int).reshape(20, 7)
print X

y = numpy.array([1, 1, 0, 0, 0, 0, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1])
print y

print('Original dataset shape {}'.format(Counter(y)))
sm = SMOTE(random_state=42)
X_res, y_res = sm.fit_sample(X, y)
print('Resampled dataset shape {}'.format(Counter(y_res)))

print X_res
