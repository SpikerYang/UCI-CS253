from __future__ import print_function
#
# import keras
import re
import numpy as np
from sklearn.model_selection import train_test_split
from keras.models import Sequential
from keras.layers import Dense
from keras.optimizers import Adam

train_x = []
train_y = []

matrix_x = []
matrix_y = []

X_SAMPLE_SIZE = 0
Y_SAMPLE_SIZE = 0


def encode_x(line):
    x = np.zeros(X_SAMPLE_SIZE, int)
    i = 0
    for l in line:
        x[i] = ord(l) - ord('a') + 1
        i = i + 1
    return x


def encode_y(l):
    y = np.zeros(Y_SAMPLE_SIZE, int)
    i = 0
    for c in l:
        for st in c:
            y[i] = ord(st) - ord('a') + 1
            i = i + 1
        i = i + 1
    return y


with open('../pride-and-prejudice.txt') as f:
    for s in f.readlines():
        s = s.strip()
        if 10 < len(s) < 110:
            train_y.append(re.findall('[a-z]{2,}', s.lower()))

for t in train_y:
    num = len(s)
    s = ''.join(t)
    if len(s) + num - 1 > Y_SAMPLE_SIZE:
        Y_SAMPLE_SIZE = len(s) + num - 1

    if len(s) > X_SAMPLE_SIZE:
        X_SAMPLE_SIZE = len(s)
    train_x.append(s)


for t in train_x:
    matrix_x.append(encode_x(t))

for t in train_y:
    matrix_y.append(encode_y(t))


X_train, X_test, y_train, y_test = train_test_split(np.array(matrix_x), np.array(matrix_y), test_size=0.33, random_state=30)

model = Sequential()
model.add(Dense(Y_SAMPLE_SIZE, input_dim=X_SAMPLE_SIZE))
model.add(Dense(Y_SAMPLE_SIZE, activation='relu'))
model.add(Dense(Y_SAMPLE_SIZE, activation='relu'))
model.add(Dense(Y_SAMPLE_SIZE, activation='relu'))
model.add(Dense(Y_SAMPLE_SIZE, activation='relu'))
model.add(Dense(Y_SAMPLE_SIZE, activation='relu'))
model.add(Dense(Y_SAMPLE_SIZE, activation='relu'))
model.add(Dense(Y_SAMPLE_SIZE, activation='relu'))
model.add(Dense(Y_SAMPLE_SIZE, activation='relu'))
model.add(Dense(Y_SAMPLE_SIZE, activation='softmax'))

model.compile(loss='binary_crossentropy', optimizer='adam', metrics=['accuracy'])

b_size = 10
max_epochs = 1000
print("Starting training ")
h = model.fit(X_train, y_train, batch_size=b_size, epochs=max_epochs, shuffle=True, verbose=1)
print("Training finished \n")

res = model.evaluate(X_test, y_test, verbose=0)
print("Evaluation on test data: loss = %0.6f accuracy = %0.2f%% \n" \
      % (res[0], res[1] * 100))


